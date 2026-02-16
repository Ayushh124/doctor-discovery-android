-- backup_doctors.sql
-- Creates a backup of all current doctors before deletion
-- You can restore this later if needed

USE doctor_discovery;

-- Export current doctors to a backup file (run this first!)
-- This creates a SELECT output that you can save

SELECT 
    CONCAT(
        'INSERT INTO doctors ',
        '(id, name, email, phone, gender, age, specialization, institute, degree, ',
        'location, experience_years, consultation_fee, bio, rating, image_url, search_count) ',
        'VALUES (',
        id, ', ',
        QUOTE(name), ', ',
        QUOTE(email), ', ',
        QUOTE(phone), ', ',
        QUOTE(IFNULL(gender, '')), ', ',
        IFNULL(age, 'NULL'), ', ',
        QUOTE(specialization), ', ',
        QUOTE(IFNULL(institute, '')), ', ',
        QUOTE(IFNULL(degree, '')), ', ',
        QUOTE(location), ', ',
        experience_years, ', ',
        consultation_fee, ', ',
        IFNULL(QUOTE(bio), 'NULL'), ', ',
        rating, ', ',
        IFNULL(QUOTE(image_url), 'NULL'), ', ',
        search_count,
        ');'
    ) as 'Backup SQL'
FROM doctors
ORDER BY id;

-- To save this backup:
-- mysql -u doctor_app -pdoctor123 < backup_doctors.sql > doctors_backup_$(date +%Y%m%d).sql
