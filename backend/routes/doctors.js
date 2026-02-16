// routes/doctors.js - All doctor-related routes and search logic
// This file contains the advanced search, filter, and pagination functionality

const express = require('express');
const router = express.Router();
const db = require('../database');

/**
 * GET /api/doctors/top
 * Returns top doctors by search_count with full information
 * Used for displaying "Most Searched" section on home screen
 * Query param: limit (default 4, max 10)
 */
router.get('/top', async (req, res) => {
    try {
        const limit = Math.min(parseInt(req.query.limit) || 4, 10);
        
        const [topDoctors] = await db.query(
            'SELECT * FROM doctors ORDER BY search_count DESC LIMIT ?',
            [limit]
        );
        
        res.json({
            success: true,
            count: topDoctors.length,
            data: topDoctors
        });
    } catch (error) {
        console.error('Error fetching top doctors:', error);
        res.status(500).json({
            success: false,
            message: 'Failed to fetch top doctors',
            error: error.message
        });
    }
});

/**
 * GET /api/doctors/cities
 * Returns the list of all available cities
 * Used for city dropdown in Android app
 */
router.get('/cities', async (req, res) => {
    try {
        // Get distinct cities from database, sorted alphabetically
        const [cities] = await db.query(
            'SELECT DISTINCT location as city FROM doctors ORDER BY location ASC'
        );
        
        res.json({
            success: true,
            count: cities.length,
            data: cities.map(row => row.city) // Convert to simple array of strings
        });
    } catch (error) {
        console.error('Error fetching cities:', error);
        res.status(500).json({
            success: false,
            message: 'Failed to fetch cities',
            error: error.message
        });
    }
});

/**
 * GET /api/doctors/specializations
 * Returns the list of all available specializations
 * Used for specialization dropdown in Android app
 */
router.get('/specializations', async (req, res) => {
    try {
        // Get distinct specializations from database, sorted alphabetically
        const [specializations] = await db.query(
            'SELECT DISTINCT specialization FROM doctors ORDER BY specialization ASC'
        );
        
        res.json({
            success: true,
            count: specializations.length,
            data: specializations.map(row => row.specialization) // Convert to simple array
        });
    } catch (error) {
        console.error('Error fetching specializations:', error);
        res.status(500).json({
            success: false,
            message: 'Failed to fetch specializations',
            error: error.message
        });
    }
});

/**
 * GET /api/doctors/search
 * Advanced search and filter endpoint with pagination
 * 
 * Query Parameters:
 * - name: Partial match on doctor name (case-insensitive)
 * - specialization: Exact match on specialization
 * - location: Exact match on location/city
 * - minRating: Minimum rating filter (e.g., 4.5)
 * - maxFee: Maximum consultation fee
 * - minExperience: Minimum years of experience
 * - sortBy: Field to sort by (rating, experience_years, consultation_fee, search_count, name)
 * - order: Sort order (asc or desc)
 * - page: Page number (default: 1)
 * - limit: Results per page (default: 10)
 * 
 * Example: /api/doctors/search?name=raj&specialization=Cardiologist&sortBy=rating&order=desc&page=1&limit=10
 */
router.get('/search', async (req, res) => {
    try {
        // Extract query parameters with defaults
        const {
            name = '',
            specialization = '',
            location = '',
            minRating = 0,
            maxFee = 999999,
            minExperience = 0,
            sortBy = 'id',
            order = 'asc',
            page = 1,
            limit = 10
        } = req.query;

        // Convert pagination params to numbers
        const pageNum = parseInt(page);
        const limitNum = parseInt(limit);
        const offset = (pageNum - 1) * limitNum;

        // Validate sort column to prevent SQL injection
        const allowedSortFields = ['id', 'name', 'rating', 'experience_years', 'consultation_fee', 'search_count', 'created_at'];
        const sortField = allowedSortFields.includes(sortBy) ? sortBy : 'id';
        
        // Validate sort order (default to ASC so new doctors appear at bottom)
        const sortOrder = order.toLowerCase() === 'desc' ? 'DESC' : 'ASC';

        /**
         * Build dynamic WHERE clause
         * We'll use parameterized queries to prevent SQL injection
         */
        let whereConditions = [];
        let queryParams = [];

        // Name search - partial match using LIKE
        // % means "any characters before/after"
        if (name) {
            whereConditions.push('name LIKE ?');
            queryParams.push(`%${name}%`); // Will match "raj" in "Rajesh"
        }

        // Specialization filter - exact match
        if (specialization) {
            whereConditions.push('specialization = ?');
            queryParams.push(specialization);
        }

        // Location filter - exact match
        if (location) {
            whereConditions.push('location = ?');
            queryParams.push(location);
        }

        // Rating filter - minimum rating
        if (minRating > 0) {
            whereConditions.push('rating >= ?');
            queryParams.push(parseFloat(minRating));
        }

        // Fee filter - maximum fee
        if (maxFee < 999999) {
            whereConditions.push('consultation_fee <= ?');
            queryParams.push(parseInt(maxFee));
        }

        // Experience filter - minimum experience
        if (minExperience > 0) {
            whereConditions.push('experience_years >= ?');
            queryParams.push(parseInt(minExperience));
        }

        // Construct WHERE clause
        // If no conditions, WHERE clause is empty (returns all doctors)
        const whereClause = whereConditions.length > 0 
            ? 'WHERE ' + whereConditions.join(' AND ')
            : '';

        /**
         * Query 1: Get total count for pagination
         * This tells us how many pages we have
         */
        const countQuery = `SELECT COUNT(*) as total FROM doctors ${whereClause}`;
        const [countResult] = await db.query(countQuery, queryParams);
        const totalDoctors = countResult[0].total;
        const totalPages = Math.ceil(totalDoctors / limitNum);

        /**
         * Query 2: Get paginated results
         * LIMIT controls how many results per page
         * OFFSET skips the previous pages' results
         */
        const searchQuery = `
            SELECT * FROM doctors 
            ${whereClause}
            ORDER BY ${sortField} ${sortOrder}
            LIMIT ? OFFSET ?
        `;
        
        // Add pagination params to the query params
        const [doctors] = await db.query(searchQuery, [...queryParams, limitNum, offset]);

        // Return results with pagination metadata
        res.json({
            success: true,
            count: doctors.length,
            pagination: {
                currentPage: pageNum,
                totalPages: totalPages,
                totalResults: totalDoctors,
                resultsPerPage: limitNum,
                hasNextPage: pageNum < totalPages,
                hasPreviousPage: pageNum > 1
            },
            filters: {
                name: name || null,
                specialization: specialization || null,
                location: location || null,
                minRating: minRating > 0 ? parseFloat(minRating) : null,
                maxFee: maxFee < 999999 ? parseInt(maxFee) : null,
                minExperience: minExperience > 0 ? parseInt(minExperience) : null
            },
            sorting: {
                sortBy: sortField,
                order: sortOrder
            },
            data: doctors
        });

    } catch (error) {
        console.error('Error in search:', error);
        res.status(500).json({
            success: false,
            message: 'Search failed',
            error: error.message
        });
    }
});

/**
 * GET /api/doctors/:id
 * Get single doctor by ID
 * This endpoint also increments the search_count for popularity tracking
 */
router.get('/:id', async (req, res) => {
    try {
        const { id } = req.params;

        // Validate ID is a number
        if (isNaN(id)) {
            return res.status(400).json({
                success: false,
                message: 'Invalid doctor ID'
            });
        }

        // Get doctor details
        const [doctors] = await db.query(
            'SELECT * FROM doctors WHERE id = ?',
            [id]
        );

        if (doctors.length === 0) {
            return res.status(404).json({
                success: false,
                message: 'Doctor not found'
            });
        }

        // Increment search count (for popularity tracking)
        // This will be visible in Step 5 when we show popular doctors
        await db.query(
            'UPDATE doctors SET search_count = search_count + 1 WHERE id = ?',
            [id]
        );

        // Update the search_count in the returned data
        const doctor = doctors[0];
        doctor.search_count = doctor.search_count + 1;

        res.json({
            success: true,
            data: doctor
        });

    } catch (error) {
        console.error('Error fetching doctor:', error);
        res.status(500).json({
            success: false,
            message: 'Failed to fetch doctor',
            error: error.message
        });
    }
});

/**
 * GET /api/doctors
 * Get all doctors (basic endpoint, no filters)
 * Useful for testing and simple listing
 */
router.get('/', async (req, res) => {
    try {
        const [doctors] = await db.query(
            'SELECT * FROM doctors ORDER BY created_at DESC'
        );
        
        res.json({
            success: true,
            count: doctors.length,
            data: doctors
        });
    } catch (error) {
        console.error('Error fetching doctors:', error);
        res.status(500).json({
            success: false,
            message: 'Failed to fetch doctors',
            error: error.message
        });
    }
});

module.exports = router;
