// server.js - Main Express Server Entry Point
// This is the heart of our backend that handles all API requests

const express = require('express');
const cors = require('cors');
const path = require('path');
require('dotenv').config();

const db = require('./database');

// Import routes
const doctorRoutes = require('./routes/doctors');
const registrationRoutes = require('./routes/registration');

// Initialize Express app
const app = express();
const PORT = process.env.PORT || 3000;

/**
 * MIDDLEWARE SETUP
 * Middleware functions process requests before they reach our routes
 */

// CORS - Allow requests from Android app
// This is crucial for local development with Android emulator
app.use(cors({
    origin: '*', // In production, replace with specific origins
    methods: ['GET', 'POST', 'PUT', 'DELETE'],
    allowedHeaders: ['Content-Type', 'Authorization']
}));

// Parse JSON request bodies
// This allows us to access req.body in our routes
app.use(express.json());

// Parse URL-encoded data (for form submissions)
app.use(express.urlencoded({ extended: true }));

// Serve static files (for uploaded images)
// Images will be accessible via: http://10.0.2.2:3000/uploads/doctors/filename.jpg
app.use('/uploads', express.static(path.join(__dirname, 'uploads')));

/**
 * ROUTES - Step 2: Search & Filter API
 */

// Health check endpoint
// Test this with: curl http://localhost:3000/health
app.get('/health', (req, res) => {
    res.json({
        status: 'ok',
        message: 'Doctor Discovery API is running',
        timestamp: new Date().toISOString()
    });
});

// Test database connection endpoint
app.get('/api/test-db', async (req, res) => {
    try {
        const [rows] = await db.query('SELECT COUNT(*) as count FROM doctors');
        res.json({
            success: true,
            message: 'Database connection successful',
            doctorCount: rows[0].count
        });
    } catch (error) {
        res.status(500).json({
            success: false,
            message: 'Database connection failed',
            error: error.message
        });
    }
});

// Mount doctor routes
// All routes in doctorRoutes.js will be prefixed with /api/doctors
app.use('/api/doctors', doctorRoutes);

// Mount registration routes
// All routes in registrationRoutes.js will be prefixed with /api/register
app.use('/api/register', registrationRoutes);

/**
 * 404 HANDLER - Catch all undefined routes
 */
app.use('*', (req, res) => {
    res.status(404).json({
        success: false,
        message: 'API endpoint not found'
    });
});

/**
 * ERROR HANDLER - Global error handling middleware
 */
app.use((err, req, res, next) => {
    console.error('Unhandled error:', err);
    res.status(500).json({
        success: false,
        message: 'Internal server error',
        error: process.env.NODE_ENV === 'development' ? err.message : undefined
    });
});

/**
 * START SERVER
 */
app.listen(PORT, () => {
    console.log('═══════════════════════════════════════');
    console.log('🏥 Doctor Discovery API Server');
    console.log('═══════════════════════════════════════');
    console.log(`📡 Server running on port ${PORT}`);
    console.log(`🌐 Local: http://localhost:${PORT}`);
    console.log(`📱 Android Emulator: http://10.0.2.2:${PORT}`);
    console.log(`🛠️  Environment: ${process.env.NODE_ENV}`);
    console.log('═══════════════════════════════════════');
});
