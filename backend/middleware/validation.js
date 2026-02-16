// middleware/validation.js - Request Validation Middleware
// Validates incoming data before processing

/**
 * Validate email format
 */
const isValidEmail = (email) => {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
};

/**
 * Validate phone number (Indian format)
 * Accepts: +91-9876543210, +919876543210, 9876543210
 */
const isValidPhone = (phone) => {
    const phoneRegex = /^(\+91[\-\s]?)?[6-9]\d{9}$/;
    return phoneRegex.test(phone);
};

/**
 * Validate doctor registration - Step 1
 * Basic personal information validation
 */
const validateStep1 = (req, res, next) => {
    const { name, email, phone, gender, age, location } = req.body;
    
    console.log('📋 Step 1 Validation - Received data:', { name, email, phone, gender, age, location });
    
    const errors = [];
    
    // Name validation
    if (!name || name.trim().length < 3) {
        errors.push('Name must be at least 3 characters long');
    }
    
    // Email validation
    if (!email || !isValidEmail(email)) {
        errors.push('Valid email is required');
    }
    
    // Phone validation
    if (!phone || !isValidPhone(phone)) {
        errors.push(`Valid Indian phone number is required. Received: ${phone}`);
    }
    
    // Gender validation
    if (!gender || !['Male', 'Female', 'Other'].includes(gender)) {
        errors.push(`Valid gender is required. Received: ${gender}`);
    }
    
    // Age validation
    const ageNum = parseInt(age);
    if (!age || isNaN(ageNum) || ageNum < 25 || ageNum > 80) {
        errors.push(`Age must be between 25 and 80. Received: ${age}`);
    }
    
    // Location validation
    if (!location || location.trim().length < 2) {
        errors.push('Location is required');
    }
    
    // If there are errors, return them
    if (errors.length > 0) {
        console.log('❌ Step 1 Validation failed:', errors);
        return res.status(400).json({
            success: false,
            message: 'Validation failed',
            errors: errors
        });
    }
    
    console.log('✅ Step 1 Validation passed');
    
    // Validation passed, continue to next middleware
    next();
};

/**
 * Validate doctor registration - Step 2
 * Professional details validation
 */
const validateStep2 = (req, res, next) => {
    const { specialization, institute, degree, experience_years, consultation_fee, bio } = req.body;
    
    const errors = [];
    
    // Specialization validation
    if (!specialization || specialization.trim().length < 3) {
        errors.push('Specialization is required');
    }
    
    // Institute validation
    if (!institute || institute.trim().length < 3) {
        errors.push('Institute name is required');
    }
    
    // Degree validation
    if (!degree || degree.trim().length < 2) {
        errors.push('Degree is required');
    }
    
    // Experience validation
    if (!experience_years || isNaN(experience_years) || experience_years < 0 || experience_years > 70) {
        errors.push('Experience years must be between 0 and 70');
    }
    
    // Consultation fee validation
    if (!consultation_fee || isNaN(consultation_fee) || consultation_fee < 0) {
        errors.push('Valid consultation fee is required');
    }
    
    // Bio validation (optional - no minimum length)
    // Bio is optional, so we don't validate it
    
    // If there are errors, return them
    if (errors.length > 0) {
        return res.status(400).json({
            success: false,
            message: 'Validation failed',
            errors: errors
        });
    }
    
    // Validation passed, continue to next middleware
    next();
};

/**
 * Validate search/filter parameters
 */
const validateSearch = (req, res, next) => {
    const { minRating, maxFee, minExperience, page, limit } = req.query;
    
    const errors = [];
    
    // Rating validation
    if (minRating && (isNaN(minRating) || minRating < 0 || minRating > 5)) {
        errors.push('minRating must be between 0 and 5');
    }
    
    // Fee validation
    if (maxFee && (isNaN(maxFee) || maxFee < 0)) {
        errors.push('maxFee must be a positive number');
    }
    
    // Experience validation
    if (minExperience && (isNaN(minExperience) || minExperience < 0)) {
        errors.push('minExperience must be a positive number');
    }
    
    // Page validation
    if (page && (isNaN(page) || page < 1)) {
        errors.push('page must be a positive number');
    }
    
    // Limit validation
    if (limit && (isNaN(limit) || limit < 1 || limit > 100)) {
        errors.push('limit must be between 1 and 100');
    }
    
    // If there are errors, return them
    if (errors.length > 0) {
        return res.status(400).json({
            success: false,
            message: 'Validation failed',
            errors: errors
        });
    }
    
    // Validation passed, continue to next middleware
    next();
};

module.exports = {
    validateStep1,
    validateStep2,
    validateSearch,
    isValidEmail,
    isValidPhone
};
