-- Add missing fields to doctors table
-- Gender, Age, Institute, Degree

USE doctor_discovery;

-- Add new columns
ALTER TABLE doctors 
ADD COLUMN gender ENUM('Male', 'Female', 'Other') DEFAULT 'Male' AFTER email,
ADD COLUMN age INT DEFAULT 35 AFTER gender,
ADD COLUMN institute VARCHAR(255) DEFAULT 'Medical College' AFTER bio,
ADD COLUMN degree VARCHAR(255) DEFAULT 'MBBS' AFTER institute;

-- Update existing doctors with realistic data
UPDATE doctors SET 
    gender = 'Male',
    age = 42,
    institute = 'All India Institute of Medical Sciences (AIIMS), Delhi',
    degree = 'MBBS, MD (Cardiology)'
WHERE id = 1; -- Dr. Rajesh Kumar

UPDATE doctors SET 
    gender = 'Female',
    age = 38,
    institute = 'Armed Forces Medical College, Pune',
    degree = 'MBBS, MD (Dermatology)'
WHERE id = 2; -- Dr. Priya Sharma

UPDATE doctors SET 
    gender = 'Male',
    age = 35,
    institute = 'Osmania Medical College, Hyderabad',
    degree = 'MBBS, MD (Pediatrics)'
WHERE id = 3; -- Dr. Anil Reddy

UPDATE doctors SET 
    gender = 'Female',
    age = 40,
    institute = 'Madras Medical College, Chennai',
    degree = 'MBBS, MS (Obstetrics & Gynecology)'
WHERE id = 4; -- Dr. Kavita Iyer

UPDATE doctors SET 
    gender = 'Male',
    age = 50,
    institute = 'B.J. Medical College, Pune',
    degree = 'MBBS, MS (Orthopedics), DNB'
WHERE id = 5; -- Dr. Suresh Mehta

UPDATE doctors SET 
    gender = 'Female',
    age = 33,
    institute = 'R.G. Kar Medical College, Kolkata',
    degree = 'MBBS, MD (General Medicine)'
WHERE id = 6; -- Dr. Anjali Banerjee

UPDATE doctors SET 
    gender = 'Male',
    age = 45,
    institute = 'B.J. Medical College, Ahmedabad',
    degree = 'MBBS, DM (Neurology)'
WHERE id = 7; -- Dr. Vikram Desai

UPDATE doctors SET 
    gender = 'Female',
    age = 37,
    institute = 'National Institute of Mental Health, Bangalore',
    degree = 'MBBS, MD (Psychiatry)'
WHERE id = 8; -- Dr. Sneha Gupta

UPDATE doctors SET 
    gender = 'Male',
    age = 43,
    institute = 'SMS Medical College, Jaipur',
    degree = 'MBBS, MS (ENT)'
WHERE id = 9; -- Dr. Manoj Jain

UPDATE doctors SET 
    gender = 'Female',
    age = 44,
    institute = 'Post Graduate Institute, Chandigarh',
    degree = 'MBBS, MS (Ophthalmology)'
WHERE id = 10; -- Dr. Neha Kapoor

UPDATE doctors SET 
    gender = 'Male',
    age = 39,
    institute = 'King Edward Memorial Hospital, Mumbai',
    degree = 'MBBS, MD (Cardiology)'
WHERE id = 11; -- Dr. Test Kumar

-- Verify the changes
SELECT id, name, gender, age, institute, degree, search_count FROM doctors;
