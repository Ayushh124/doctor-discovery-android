// routes/registration.js - Doctor Registration Routes
// Handles 2-step registration process with image upload

const express = require('express');
const router = express.Router();
const db = require('../database');
const { uploadSingle } = require('../middleware/upload');
const { validateStep1, validateStep2 } = require('../middleware/validation');

/**
 * In-memory temporary storage for multi-step registration
 * In production, you might want to use Redis or database
 * 
 * Structure: {
 *   "temporary_id": {
 *     step1Data: {...},
 *     step2Data: {...},
 *     timestamp: 1234567890
 *   }
 * }
 */
const tempRegistrations = new Map();

/**
 * Clean up old temporary registrations (older than 1 hour)
 * Runs every 10 minutes
 */
setInterval(() => {
    const oneHourAgo = Date.now() - (60 * 60 * 1000);
    
    for (const [key, value] of tempRegistrations.entries()) {
        if (value.timestamp < oneHourAgo) {
            tempRegistrations.delete(key);
            console.log(`Cleaned up expired registration: ${key}`);
        }
    }
}, 10 * 60 * 1000);

/**
 * POST /api/register/step1
 * First step: Save basic doctor information
 * 
 * Required fields:
 * - name
 * - email
 * - phone
 * - specialization
 * - location
 */
router.post('/step1', validateStep1, async (req, res) => {
    try {
        const { name, email, phone, gender, age, location } = req.body;
        
        // Check if email already exists
        const [existingDoctors] = await db.query(
            'SELECT id FROM doctors WHERE email = ?',
            [email]
        );
        
        if (existingDoctors.length > 0) {
            return res.status(409).json({
                success: false,
                message: 'Email already registered',
                field: 'email'
            });
        }
        
        // Generate temporary ID for this registration session
        const tempId = `temp_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`;
        
        // Save step 1 data temporarily
        tempRegistrations.set(tempId, {
            step1Data: {
                name: name.trim(),
                email: email.trim().toLowerCase(),
                phone: phone.trim(),
                gender: gender.trim(),
                age: parseInt(age),
                location: location.trim()
            },
            timestamp: Date.now()
        });
        
        res.json({
            success: true,
            message: 'Step 1 completed successfully',
            tempId: tempId,
            data: {
                name,
                email,
                phone,
                gender,
                age,
                location
            }
        });
        
    } catch (error) {
        console.error('Error in registration step 1:', error);
        res.status(500).json({
            success: false,
            message: 'Registration failed',
            error: error.message
        });
    }
});

/**
 * POST /api/register/step2
 * Second step: Complete registration with additional info
 * 
 * Required fields:
 * - tempId (from step 1)
 * - experience_years
 * - consultation_fee
 * - bio
 * - rating (optional, defaults to 0.0)
 * - image_url (from previous image upload)
 */
router.post('/step2', validateStep2, async (req, res) => {
    try {
        const {
            tempId,
            specialization,
            institute,
            degree,
            experience_years,
            consultation_fee,
            bio = null,
            rating = 0.0,
            image_url = null
        } = req.body;
        
        // Validate tempId
        if (!tempId || !tempRegistrations.has(tempId)) {
            return res.status(400).json({
                success: false,
                message: 'Invalid or expired registration session. Please start over.'
            });
        }
        
        // Get step 1 data
        const registrationData = tempRegistrations.get(tempId);
        const step1Data = registrationData.step1Data;
        
        // Check if email still available (in case someone registered with same email)
        const [existingDoctors] = await db.query(
            'SELECT id FROM doctors WHERE email = ?',
            [step1Data.email]
        );
        
        if (existingDoctors.length > 0) {
            tempRegistrations.delete(tempId);
            return res.status(409).json({
                success: false,
                message: 'Email already registered. Please use a different email.'
            });
        }
        
        // Insert complete doctor record into database
        const [result] = await db.query(
            `INSERT INTO doctors 
            (name, email, phone, gender, age, specialization, institute, degree,
             location, experience_years, consultation_fee, bio, rating, image_url, search_count) 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 0)`,
            [
                step1Data.name,
                step1Data.email,
                step1Data.phone,
                step1Data.gender,
                step1Data.age,
                specialization.trim(),
                institute.trim(),
                degree.trim(),
                step1Data.location,
                parseInt(experience_years),
                parseInt(consultation_fee),
                bio ? bio.trim() : null,
                parseFloat(rating),
                image_url
            ]
        );
        
        // Get the newly created doctor
        const [newDoctor] = await db.query(
            'SELECT * FROM doctors WHERE id = ?',
            [result.insertId]
        );
        
        // Clean up temporary registration data
        tempRegistrations.delete(tempId);
        
        res.status(201).json({
            success: true,
            message: 'Registration completed successfully',
            data: newDoctor[0]
        });
        
    } catch (error) {
        console.error('Error in registration step 2:', error);
        res.status(500).json({
            success: false,
            message: 'Registration failed',
            error: error.message
        });
    }
});

/**
 * POST /api/register/upload-image
 * Upload doctor profile image
 * 
 * This can be called between step 1 and step 2
 * Returns the URL of the uploaded image
 * 
 * Form data:
 * - image: File (jpg, png, gif, webp, max 5MB)
 */
router.post('/upload-image', uploadSingle, (req, res) => {
    try {
        // Check if file was uploaded
        if (!req.file) {
            return res.status(400).json({
                success: false,
                message: 'No image file provided'
            });
        }
        
        // Generate URL for the uploaded image
        // This URL will be accessible from Android app using 10.0.2.2
        const imageUrl = `${req.protocol}://${req.get('host')}/uploads/doctors/${req.file.filename}`;
        
        // Return just the image path (relative path for database storage)
        const imagePath = `uploads/doctors/${req.file.filename}`;
        
        res.json({
            success: true,
            message: 'Image uploaded successfully',
            imagePath: imagePath
        });
        
    } catch (error) {
        console.error('Error uploading image:', error);
        res.status(500).json({
            success: false,
            message: 'Image upload failed',
            error: error.message
        });
    }
});

/**
 * GET /api/register/temp/:tempId
 * Retrieve temporary registration data
 * Useful for Android to restore state if app was closed
 */
router.get('/temp/:tempId', (req, res) => {
    const { tempId } = req.params;
    
    if (!tempRegistrations.has(tempId)) {
        return res.status(404).json({
            success: false,
            message: 'Registration session not found or expired'
        });
    }
    
    const data = tempRegistrations.get(tempId);
    
    res.json({
        success: true,
        data: data.step1Data,
        expiresIn: Math.floor((data.timestamp + 3600000 - Date.now()) / 1000) // seconds until expiry
    });
});

/**
 * DELETE /api/register/temp/:tempId
 * Cancel registration and clean up temporary data
 */
router.delete('/temp/:tempId', (req, res) => {
    const { tempId } = req.params;
    
    if (!tempRegistrations.has(tempId)) {
        return res.status(404).json({
            success: false,
            message: 'Registration session not found'
        });
    }
    
    tempRegistrations.delete(tempId);
    
    res.json({
        success: true,
        message: 'Registration cancelled successfully'
    });
});

/**
 * GET /api/register/stats
 * Get registration statistics (for testing/debugging)
 */
router.get('/stats', (req, res) => {
    res.json({
        success: true,
        activeRegistrations: tempRegistrations.size,
        registrations: Array.from(tempRegistrations.keys()).map(key => ({
            tempId: key,
            timestamp: tempRegistrations.get(key).timestamp,
            age: Date.now() - tempRegistrations.get(key).timestamp
        }))
    });
});

module.exports = router;
