// middleware/upload.js - Multer Configuration for Image Uploads
// This handles file uploads for doctor profile pictures

const multer = require('multer');
const path = require('path');
const fs = require('fs');

/**
 * Configure storage for uploaded files
 * We'll save files with unique names to prevent conflicts
 */
const storage = multer.diskStorage({
    /**
     * Destination: Where to save uploaded files
     * Creates the directory if it doesn't exist
     */
    destination: function (req, file, cb) {
        const uploadPath = path.join(__dirname, '../uploads/doctors');
        
        // Create directory if it doesn't exist
        if (!fs.existsSync(uploadPath)) {
            fs.mkdirSync(uploadPath, { recursive: true });
        }
        
        cb(null, uploadPath);
    },
    
    /**
     * Filename: How to name the uploaded file
     * Format: timestamp-randomnumber.extension
     * Example: 1676543210123-987654321.jpg
     */
    filename: function (req, file, cb) {
        // Get file extension (.jpg, .png, etc.)
        const ext = path.extname(file.originalname);
        
        // Generate unique filename using timestamp and random number
        const uniqueName = `${Date.now()}-${Math.round(Math.random() * 1E9)}${ext}`;
        
        cb(null, uniqueName);
    }
});

/**
 * File filter: Only allow image files
 * Accepts: jpg, jpeg, png, gif, webp
 * Rejects: Everything else
 */
const fileFilter = (req, file, cb) => {
    // Allowed file types
    const allowedTypes = /jpeg|jpg|png|gif|webp/;
    
    // Check extension
    const extname = allowedTypes.test(path.extname(file.originalname).toLowerCase());
    
    // Check mime type (file content type)
    const mimetype = allowedTypes.test(file.mimetype);
    
    if (extname && mimetype) {
        // File is valid
        cb(null, true);
    } else {
        // File is invalid
        cb(new Error('Only image files are allowed (jpg, jpeg, png, gif, webp)'));
    }
};

/**
 * Create multer upload middleware
 * Configuration:
 * - storage: Custom storage configuration
 * - limits: Max file size 5MB
 * - fileFilter: Only allow images
 */
const upload = multer({
    storage: storage,
    limits: {
        fileSize: 5 * 1024 * 1024 // 5MB in bytes
    },
    fileFilter: fileFilter
});

/**
 * Export different upload configurations
 * - single: Upload single file
 * - multiple: Upload multiple files
 */
module.exports = {
    // Upload single image with field name 'image'
    uploadSingle: upload.single('image'),
    
    // Upload multiple images (max 5) with field name 'images'
    uploadMultiple: upload.array('images', 5),
    
    // Raw multer instance for custom configurations
    upload: upload
};
