const express = require('express');
const path = require('path');
const fs = require('fs');
const levenshtein = require('fast-levenshtein');
const multer = require('multer');

const app = express();
const port = 3000;

console.log("__dirname: " + __dirname); // Add this line

// Serve static files (HTML, CSS, JS)
//app.use(express.static('public')); /// F*ck this doesnt work is node is executed from another 
app.use(express.static(path.join(__dirname, 'public')));


app.use(express.json()); 

// Configure multer for metadata image uploads
const storage = multer.diskStorage({
  destination: function (req, file, cb) {
    // Create a temporary destination, we'll move the file in the endpoint
    cb(null, 'temp_uploads');
  },
  filename: function (req, file, cb) {
    // Create a temporary filename
    const timestamp = Date.now();
    const ext = path.extname(file.originalname) || '.png';
    cb(null, `temp_${timestamp}${ext}`);
  }
});

// Ensure temp directory exists
const tempDir = path.join(__dirname, 'temp_uploads');
if (!fs.existsSync(tempDir)) {
  fs.mkdirSync(tempDir, { recursive: true });
}

const upload = multer({ storage: storage });

// Metadata utility functions
function cleanFileName(name) {
  return name
    .replace(/-pass/gi, '')
    .replace(/-wtc/gi, '')
    .replace(/-r[0-3]/gi, '')
    .replace(/ffmpeg/gi, '')          // Remove ffmpeg
    .replace(/[!@#$%^&*()]+/g, '')
    .trim();
}

function findBestMatch(targetName, existingNames) {
  const cleanTarget = cleanFileName(targetName);
  const matches = existingNames.map(name => {
    const cleanExisting = cleanFileName(path.parse(name).name);
    const distance = levenshtein.get(cleanTarget, cleanExisting);
    const maxDistance = Math.min(5, Math.ceil(cleanTarget.length * 0.3));
    return { name, distance, isMatch: distance <= maxDistance };
  }).filter(match => match.isMatch)
    .sort((a, b) => a.distance - b.distance);
  
  return matches.length > 0 ? matches[0].name : null;
}

function getMetadataPath(folderPath) {
  return path.join(folderPath, 'x_current_metadata');
}

function ensureMetadataDir(metadataPath) {
  if (!fs.existsSync(metadataPath)) {
    fs.mkdirSync(metadataPath, { recursive: true });
  }
}

// Serve index.html at the root URL
app.get('/', (req, res) => {
    res.sendFile(path.join(__dirname, 'public', 'index.html'));
});

// Metadata API endpoints

// GET metadata for a specific item
app.get('/metadata', (req, res) => {
    const { folderPath, itemName } = req.query;
    
    if (!folderPath || !itemName) {
        return res.status(400).json({ error: 'folderPath and itemName are required' });
    }

    const metadataDir = getMetadataPath(folderPath);
    
    if (!fs.existsSync(metadataDir)) {
        return res.json({ metadata: null });
    }

    try {
        const metadataFiles = fs.readdirSync(metadataDir).filter(f => f.endsWith('.json'));
        const matchedFile = findBestMatch(itemName, metadataFiles);
        
        if (!matchedFile) {
            return res.json({ metadata: null });
        }

        const metadataPath = path.join(metadataDir, matchedFile);
        const metadata = JSON.parse(fs.readFileSync(metadataPath, 'utf8'));
        
        // Check if associated image exists
        if (metadata.hasImage && metadata.imageExtension) {
            const imageName = matchedFile.replace('.json', metadata.imageExtension);
            const imagePath = path.join(metadataDir, imageName);
            metadata.imageExists = fs.existsSync(imagePath);
            metadata.imagePath = metadata.imageExists ? `/metadata-image?folderPath=${encodeURIComponent(folderPath)}&imageName=${encodeURIComponent(imageName)}` : null;
        }

        res.json({ metadata });
    } catch (error) {
        console.error('Error reading metadata:', error);
        res.status(500).json({ error: 'Error reading metadata' });
    }
});

// POST save/update metadata
app.post('/metadata', (req, res) => {
    const { folderPath, itemName, text, hasImage, imageExtension } = req.body;
    
    if (!folderPath || !itemName) {
        return res.status(400).json({ error: 'folderPath and itemName are required' });
    }

    const metadataDir = getMetadataPath(folderPath);
    ensureMetadataDir(metadataDir);

    try {
        const metadataFileName = itemName + '.json';
        const metadataPath = path.join(metadataDir, metadataFileName);
        
        const metadata = {
            originalName: itemName,
            canonicalName: cleanFileName(path.parse(itemName).name),
            text: text || '',
            hasImage: hasImage || false,
            imageExtension: imageExtension || null,
            created: fs.existsSync(metadataPath) ? JSON.parse(fs.readFileSync(metadataPath, 'utf8')).created : new Date().toISOString(),
            modified: new Date().toISOString()
        };

        fs.writeFileSync(metadataPath, JSON.stringify(metadata, null, 2));
        res.json({ success: true, message: 'Metadata saved successfully' });
    } catch (error) {
        console.error('Error saving metadata:', error);
        res.status(500).json({ error: 'Error saving metadata' });
    }
});

// DELETE metadata
app.delete('/metadata', (req, res) => {
    const { folderPath, itemName } = req.query;
    
    if (!folderPath || !itemName) {
        return res.status(400).json({ error: 'folderPath and itemName are required' });
    }

    const metadataDir = getMetadataPath(folderPath);
    
    if (!fs.existsSync(metadataDir)) {
        return res.json({ success: true, message: 'No metadata to delete' });
    }

    try {
        const metadataFiles = fs.readdirSync(metadataDir).filter(f => f.endsWith('.json'));
        const matchedFile = findBestMatch(itemName, metadataFiles);
        
        if (matchedFile) {
            const metadataPath = path.join(metadataDir, matchedFile);
            const metadata = JSON.parse(fs.readFileSync(metadataPath, 'utf8'));
            
            // Delete associated image if exists
            if (metadata.hasImage && metadata.imageExtension) {
                const imageName = matchedFile.replace('.json', metadata.imageExtension);
                const imagePath = path.join(metadataDir, imageName);
                if (fs.existsSync(imagePath)) {
                    fs.unlinkSync(imagePath);
                }
            }
            
            // Delete metadata file
            fs.unlinkSync(metadataPath);
            
            // Check if metadata directory is empty and remove it
            const remainingFiles = fs.readdirSync(metadataDir);
            if (remainingFiles.length === 0) {
                fs.rmdirSync(metadataDir);
            }
        }

        res.json({ success: true, message: 'Metadata deleted successfully' });
    } catch (error) {
        console.error('Error deleting metadata:', error);
        res.status(500).json({ error: 'Error deleting metadata' });
    }
});

// POST upload metadata image
app.post('/metadata-image', upload.single('image'), (req, res) => {
    if (!req.file) {
        return res.status(400).json({ error: 'No image file provided' });
    }

    const { folderPath, itemName, imageExtension } = req.body;
    
    if (!folderPath || !itemName) {
        return res.status(400).json({ error: 'folderPath and itemName are required' });
    }
    
    try {
        // Create metadata directory
        const metadataDir = getMetadataPath(folderPath);
        ensureMetadataDir(metadataDir);
        
        // Move file from temp location to metadata directory
        const tempFilePath = req.file.path;
        const finalImageName = itemName + (imageExtension || '.png');
        const finalImagePath = path.join(metadataDir, finalImageName);
        
        // Copy the file to final location
        fs.copyFileSync(tempFilePath, finalImagePath);
        
        // Delete temp file
        fs.unlinkSync(tempFilePath);
        
        // Update metadata to reflect image existence
        const metadataPath = path.join(metadataDir, itemName + '.json');
        if (fs.existsSync(metadataPath)) {
            const metadata = JSON.parse(fs.readFileSync(metadataPath, 'utf8'));
            metadata.hasImage = true;
            metadata.imageExtension = imageExtension || '.png';
            metadata.modified = new Date().toISOString();
            fs.writeFileSync(metadataPath, JSON.stringify(metadata, null, 2));
        }
        
        res.json({ success: true, message: 'Image uploaded successfully' });
    } catch (error) {
        console.error('Error uploading metadata image:', error);
        
        // Clean up temp file if it exists
        if (req.file && req.file.path && fs.existsSync(req.file.path)) {
            try {
                fs.unlinkSync(req.file.path);
            } catch (cleanupError) {
                console.error('Error cleaning up temp file:', cleanupError);
            }
        }
        
        res.status(500).json({ error: 'Error uploading image: ' + error.message });
    }
});

// GET serve metadata images
app.get('/metadata-image', (req, res) => {
    const { folderPath, imageName } = req.query;
    
    if (!folderPath || !imageName) {
        return res.status(400).json({ error: 'folderPath and imageName are required' });
    }

    const metadataDir = getMetadataPath(folderPath);
    const imagePath = path.join(metadataDir, imageName);
    
    if (!fs.existsSync(imagePath)) {
        return res.status(404).json({ error: 'Image not found' });
    }

    res.sendFile(path.resolve(imagePath));
});

// API endpoint to list folder contents
app.get('/list', (req, res) => {
    const inputFolder = req.query.folder || __dirname; // Default to current directory
    fs.readdir(inputFolder, { withFileTypes: true }, (err, files) => {
        if (err) {
            return res.status(500).json({ error: 'Unable to read directory' });
        }
        const fileList = files.map(file => ({
            name: file.name,
            isDirectory: file.isDirectory(),
            path: path.join(inputFolder, file.name)
        }));

        // Sort fileList in natural, alphabetical order
        fileList.sort((a, b) => a.name.localeCompare(b.name, undefined, { numeric: true }));

        res.json(fileList);
    });
});



// Serve video files
app.get('/video', (req, res) => {
    const filePath = req.query.path;
	console.log(filePath);
    if (!filePath) {
        return res.status(400).send('File path is required');
    }
    const absolutePath = path.resolve(filePath);
    res.sendFile(absolutePath);
});

// Serve image files
app.get('/image', (req, res) => {
    const filePath = req.query.path;
    console.log('Image file request:', filePath);
    if (!filePath) {
        return res.status(400).send('File path is required');
    }
    const absolutePath = path.resolve(filePath);
    res.sendFile(absolutePath);
});

// Serve PDF files
app.get('/pdf', (req, res) => {
    const filePath = req.query.path;
    console.log('PDF file request:', filePath);
    if (!filePath) {
        return res.status(400).send('File path is required');
    }
    const absolutePath = path.resolve(filePath);
    res.setHeader('Content-Type', 'application/pdf');
    res.sendFile(absolutePath);
});

// Serve EPUB files
app.get('/epub', (req, res) => {
    const filePath = req.query.path;
    console.log('EPUB file request:', filePath);
    if (!filePath) {
        return res.status(400).send('File path is required');
    }
    const absolutePath = path.resolve(filePath);
    res.setHeader('Content-Type', 'application/epub+zip');
    res.setHeader('Access-Control-Allow-Origin', '*');
    res.setHeader('Access-Control-Allow-Methods', 'GET');
    res.setHeader('Access-Control-Allow-Headers', 'Range');
    res.sendFile(absolutePath);
});

// Serve text files content
app.get('/text', (req, res) => {
    const filePath = req.query.path;
    console.log('Text file request:', filePath);
    if (!filePath) {
        return res.status(400).json({ error: 'File path is required' });
    }
    const absolutePath = path.resolve(filePath);
    
    // Check if file exists
    fs.access(absolutePath, fs.constants.F_OK, (err) => {
        if (err) {
            console.error('File not found:', absolutePath);
            return res.status(404).json({ error: 'File not found' });
        }
        
        // Read the file
        fs.readFile(absolutePath, 'utf8', (readErr, data) => {
            if (readErr) {
                console.error('Error reading file:', readErr);
                return res.status(500).json({ error: 'Unable to read file' });
            }
            res.json({ content: data, filename: path.basename(filePath) });
        });
    });
});

app.get('/stats', (req,res)=> {
	    const inputFolder = req.query.folder || __dirname; // Default to current directory

});

app.post('/markPass', (req, res) => {
  console.log("Received request body:", req.body);
  const { filename } = req.body;

  if (!filename) {
    return res.status(400).send('Filename is required in the request body');
  }

  const absolutePath = path.resolve(filename);

  fs.access(absolutePath, fs.constants.F_OK, (err) => {
    if (err) {
      return res.status(404).send('File not found');
    }

    const { name, ext } = path.parse(absolutePath);
    const lowerName = name.toLowerCase();

    if (lowerName.includes('-pass')) {
      return res.status(200).json({
        message: 'Filename already contains "-PASS"',
        newFilename: name + ext,
      });
    }

    let newName;

    if (lowerName.endsWith('-ffmpeg-ffmpeg')) {
      newName = name.replace(/-ffmpeg-ffmpeg$/i, '-PASS-ffmpeg-ffmpeg');
    } else if (lowerName.endsWith('-ffmpeg')) {
      newName = name.replace(/-ffmpeg$/i, '-PASS-ffmpeg');
    } else {
      newName = `${name}-PASS`;
    }

    const newPath = path.join(path.dirname(absolutePath), newName + ext);

    fs.rename(absolutePath, newPath, (renameErr) => {
      if (renameErr) {
        console.error('Error renaming file:', renameErr);
        return res.status(500).send('Error renaming file');
      }
      res.status(200).json({
        message: 'File renamed successfully',
        newFilename: newName + ext,
      });
    });
  });
});

app.post('/markWtc', (req, res) => {
  console.log("Received request body:", req.body);
  const { filename } = req.body;

  if (!filename) {
    return res.status(400).send('Filename is required in the request body');
  }

  const absolutePath = path.resolve(filename);

  fs.access(absolutePath, fs.constants.F_OK, (err) => {
    if (err) {
      return res.status(404).send('File not found');
    }

    const { name, ext } = path.parse(absolutePath);
    const lowerName = name.toLowerCase();

    if (lowerName.includes('-wtc')) {
      return res.status(200).json({
        message: 'Filename already contains "-wtc"',
        newFilename: name + ext,
      });
    }

    let newName;

    if (lowerName.endsWith('-ffmpeg-ffmpeg')) {
      newName = name.replace(/-ffmpeg-ffmpeg$/i, '-wtc-ffmpeg-ffmpeg');
    } else if (lowerName.endsWith('-ffmpeg')) {
      newName = name.replace(/-ffmpeg$/i, '-wtc-ffmpeg');
    } else {
      newName = `${name}-wtc`;
    }

    const newPath = path.join(path.dirname(absolutePath), newName + ext);

    fs.rename(absolutePath, newPath, (renameErr) => {
      if (renameErr) {
        console.error('Error renaming file:', renameErr);
        return res.status(500).send('Error renaming file');
      }
      res.status(200).json({
        message: 'File renamed successfully',
        newFilename: newName + ext,
      });
    });
  });
});

app.post('/setRating', (req, res) => {
  console.log("Received request body:", req.body);
  const { filename, rating } = req.body;

  if (!filename || rating === undefined) {
    return res.status(400).send('Filename and rating are required in the request body');
  }

  if (rating < 0 || rating > 3 || !Number.isInteger(rating)) {
    return res.status(400).send('Rating must be an integer between 0 and 3');
  }

  const absolutePath = path.resolve(filename);

  fs.access(absolutePath, fs.constants.F_OK, (err) => {
    if (err) {
      return res.status(404).send('File not found');
    }

    // Use the full basename (filename without directory path) instead of path.parse
    // to avoid issues with periods being treated as extensions
    const fullBasename = path.basename(absolutePath);
    const lastDotIndex = fullBasename.lastIndexOf('.');
    
    let nameWithoutExt = fullBasename;
    let ext = '';
    
    // Only treat as extension if it's at the end and looks like a real file extension
    if (lastDotIndex > 0 && lastDotIndex < fullBasename.length - 1) {
      const possibleExt = fullBasename.substring(lastDotIndex);
      if (possibleExt.match(/^\.(mp4|avi|mkv|txt|pdf|jpg|png|js|html|css|json|zip|exe|doc|docx|ppt|pptx|xlsx|gif|bmp|webp|ts|webm)$/i)) {
        nameWithoutExt = fullBasename.substring(0, lastDotIndex);
        ext = possibleExt;
      }
    }
    
    // Remove existing rating if any
    let cleanName = nameWithoutExt.replace(/-r[0-3]/gi, '');
    const lowerCleanName = cleanName.toLowerCase();
    
    let newName;
    const ratingTag = `-r${rating}`;

    if (lowerCleanName.endsWith('-ffmpeg-ffmpeg')) {
      newName = cleanName.replace(/-ffmpeg-ffmpeg$/i, `${ratingTag}-ffmpeg-ffmpeg`);
    } else if (lowerCleanName.endsWith('-ffmpeg')) {
      newName = cleanName.replace(/-ffmpeg$/i, `${ratingTag}-ffmpeg`);
    } else {
      newName = `${cleanName}${ratingTag}`;
    }

    const newPath = path.join(path.dirname(absolutePath), newName + ext);

    fs.rename(absolutePath, newPath, (renameErr) => {
      if (renameErr) {
        console.error('Error renaming file:', renameErr);
        return res.status(500).send('Error renaming file');
      }
      res.status(200).json({
        message: 'File rating updated successfully',
        newFilename: newName + ext,
        rating: rating,
      });
    });
  });
});

// Start the server
app.listen(port, () => {
    console.log(`Server running at http://localhost:${port}`);
});