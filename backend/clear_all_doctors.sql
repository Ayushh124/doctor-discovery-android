-- clear_all_doctors.sql
-- ⚠️  WARNING: This will DELETE ALL doctors from the database!
-- Only run this if you want to start fresh with manual registration

USE doctor_discovery;

-- Show current count before deletion
SELECT COUNT(*) as 'Doctors before deletion' FROM doctors;

-- Delete all doctors
DELETE FROM doctors;

-- Reset AUTO_INCREMENT to start from 1
ALTER TABLE doctors AUTO_INCREMENT = 1;

-- Verify deletion
SELECT COUNT(*) as 'Doctors after deletion' FROM doctors;

-- Show table is now empty
SELECT * FROM doctors;

-- Success message
SELECT '✅ All doctors deleted! Database is now empty.' as 'Status';
