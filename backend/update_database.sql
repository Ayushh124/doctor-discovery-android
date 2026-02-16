-- Quick update script to refresh database with Indian data
USE doctor_discovery;

-- Delete old data
DELETE FROM doctors;

-- Reset auto-increment
ALTER TABLE doctors AUTO_INCREMENT = 1;

-- Insert 10 Indian doctors
INSERT INTO doctors (name, specialization, experience_years, location, rating, consultation_fee, phone, email, bio, image_url, search_count) VALUES
(
    'Dr. Rajesh Kumar',
    'Cardiologist',
    15,
    'Mumbai',
    4.8,
    800,
    '+91-9876543210',
    'rajesh.kumar@healthcare.com',
    'Board-certified cardiologist with expertise in preventive cardiology and heart disease management. Over 15 years of experience in treating cardiac patients.',
    'https://via.placeholder.com/400x400?text=Dr.+Rajesh+Kumar',
    234
),
(
    'Dr. Priya Sharma',
    'Dermatologist',
    10,
    'Delhi',
    4.6,
    700,
    '+91-9876543211',
    'priya.sharma@healthcare.com',
    'Specialized in cosmetic and medical dermatology. Expert in skin conditions, acne treatment, and anti-aging procedures.',
    'https://via.placeholder.com/400x400?text=Dr.+Priya+Sharma',
    189
),
(
    'Dr. Anil Reddy',
    'Pediatrician',
    8,
    'Hyderabad',
    4.9,
    600,
    '+91-9876543212',
    'anil.reddy@healthcare.com',
    'Compassionate pediatrician dedicated to children\'s health and development. Specializes in child immunization and growth monitoring.',
    'https://via.placeholder.com/400x400?text=Dr.+Anil+Reddy',
    312
),
(
    'Dr. Kavita Iyer',
    'Gynecologist',
    12,
    'Chennai',
    4.7,
    900,
    '+91-9876543213',
    'kavita.iyer@healthcare.com',
    'Experienced gynecologist specializing in women\'s health, prenatal care, and minimally invasive surgeries.',
    'https://via.placeholder.com/400x400?text=Dr.+Kavita+Iyer',
    267
),
(
    'Dr. Suresh Mehta',
    'Orthopedic',
    18,
    'Pune',
    4.8,
    1000,
    '+91-9876543214',
    'suresh.mehta@healthcare.com',
    'Renowned orthopedic surgeon specializing in joint replacement, sports injuries, and spine surgery.',
    'https://via.placeholder.com/400x400?text=Dr.+Suresh+Mehta',
    421
),
(
    'Dr. Anjali Banerjee',
    'General Physician',
    7,
    'Kolkata',
    4.5,
    500,
    '+91-9876543215',
    'anjali.banerjee@healthcare.com',
    'Family medicine specialist focused on holistic healthcare, preventive medicine, and lifestyle counseling.',
    'https://via.placeholder.com/400x400?text=Dr.+Anjali+Banerjee',
    156
),
(
    'Dr. Vikram Desai',
    'Neurologist',
    14,
    'Ahmedabad',
    4.7,
    1200,
    '+91-9876543216',
    'vikram.desai@healthcare.com',
    'Expert neurologist specializing in stroke management, epilepsy, and neurodegenerative disorders.',
    'https://via.placeholder.com/400x400?text=Dr.+Vikram+Desai',
    298
),
(
    'Dr. Sneha Gupta',
    'Psychiatrist',
    9,
    'Bangalore',
    4.6,
    1100,
    '+91-9876543217',
    'sneha.gupta@healthcare.com',
    'Compassionate psychiatrist specializing in depression, anxiety, stress management, and cognitive behavioral therapy.',
    'https://via.placeholder.com/400x400?text=Dr.+Sneha+Gupta',
    203
),
(
    'Dr. Manoj Jain',
    'ENT Specialist',
    11,
    'Jaipur',
    4.8,
    750,
    '+91-9876543218',
    'manoj.jain@healthcare.com',
    'ENT specialist with expertise in sinus problems, hearing disorders, and throat surgeries.',
    'https://via.placeholder.com/400x400?text=Dr.+Manoj+Jain',
    187
),
(
    'Dr. Neha Kapoor',
    'Ophthalmologist',
    13,
    'Chandigarh',
    4.9,
    850,
    '+91-9876543219',
    'neha.kapoor@healthcare.com',
    'Experienced eye specialist focusing on cataract surgery, LASIK, and treatment of retinal disorders.',
    'https://via.placeholder.com/400x400?text=Dr.+Neha+Kapoor',
    345
);

-- Verify the update
SELECT COUNT(*) as total_doctors FROM doctors;
SELECT DISTINCT specialization FROM doctors ORDER BY specialization;
SELECT DISTINCT location FROM doctors ORDER BY location;
