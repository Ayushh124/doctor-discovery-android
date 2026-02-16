#!/bin/bash

echo "⚠️  DELETE ALL DOCTORS FROM DATABASE"
echo "====================================="
echo ""
echo "This will permanently delete all existing doctors."
echo "You will start with an empty database."
echo ""

# Count current doctors
CURRENT_COUNT=$(mysql -u doctor_app -pdoctor123 -D doctor_discovery -se "SELECT COUNT(*) FROM doctors" 2>/dev/null)

if [ -z "$CURRENT_COUNT" ]; then
    echo "❌ Could not connect to database"
    exit 1
fi

echo "📊 Current doctors in database: $CURRENT_COUNT"
echo ""

# Ask for confirmation
read -p "❓ Do you want to DELETE all $CURRENT_COUNT doctors? (yes/no): " CONFIRM

if [ "$CONFIRM" != "yes" ]; then
    echo "❌ Cancelled. No doctors were deleted."
    exit 0
fi

echo ""
echo "🗑️  Deleting all doctors..."

# Run the delete script
mysql -u doctor_app -pdoctor123 -D doctor_discovery << EOF
-- Delete all doctors
DELETE FROM doctors;

-- Reset AUTO_INCREMENT to start from 1
ALTER TABLE doctors AUTO_INCREMENT = 1;

-- Verify deletion
SELECT COUNT(*) as 'Remaining doctors' FROM doctors;
EOF

echo ""
echo "✅ All doctors deleted!"
echo ""
echo "📋 Database is now empty."
echo "   Next doctor registered will have ID = 1"
echo ""
echo "🎉 You can now add doctors manually through the app!"
echo ""
echo "====================================="
