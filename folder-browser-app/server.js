const express = require('express');
const path = require('path');
const fs = require('fs');

const app = express();
const port = 3000;

console.log("__dirname: " + __dirname); // Add this line

// Serve static files (HTML, CSS, JS)
//app.use(express.static('public')); /// F*ck this doesnt work is node is executed from another 
app.use(express.static(path.join(__dirname, 'public')));


app.use(express.json()); 

// Serve index.html at the root URL
app.get('/', (req, res) => {
    res.sendFile(path.join(__dirname, 'public', 'index.html'));
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

    const { name, ext } = path.parse(absolutePath);
    
    // Remove existing rating if any
    let cleanName = name.replace(/-r[0-3]/gi, '');
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