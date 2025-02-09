const express = require('express');
const path = require('path');
const fs = require('fs');

const app = express();
const port = 3000;

// Serve static files (HTML, CSS, JS)
app.use(express.static('public'));

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

app.post('/markPass', (req, res) => {
    console.log("su0:" + req.body );
	const { filename } = req.body; // Get filename from request body

    if (!filename) {
        return res.status(400).send('Filename is required in the request body');
    }
	
    const absolutePath = path.resolve(filename); // Resolve to absolute path

    fs.access(absolutePath, fs.constants.F_OK, (err) => { // Check if file exists
        if (err) {
            return res.status(404).send('File not found');
        }

        const { name, ext } = path.parse(absolutePath); // Extract name and extension

        if (!name.toLowerCase().includes('-pass')) { // Case-insensitive check
            const newName = `${name}-PASS${ext}`; // Create new filename
            const newPath = path.join(path.dirname(absolutePath), newName); // Create full new path

            fs.rename(absolutePath, newPath, (err) => { // Rename the file
                if (err) {
                    console.error("Error renaming file:", err);
                    return res.status(500).send('Error renaming file');
                }
                res.status(200).json({ message: 'File renamed successfully', newFilename: newName}); // Send success response
            });
        } else {
          res.status(200).json({ message: 'Filename already contains "-PASS"', newFilename: name + ext });
        }
    });
});

// Start the server
app.listen(port, () => {
    console.log(`Server running at http://localhost:${port}`);
});