#!/bin/bash

echo "🧪 Testing: New Doctor Appears at Bottom"
echo "=========================================="
echo ""

# Generate unique email to avoid duplicate error
TIMESTAMP=$(date +%s)
EMAIL="test.doctor.${TIMESTAMP}@example.com"

echo "📋 Step 1: Register new doctor..."
STEP1=$(curl -s -X POST http://localhost:3000/api/register/step1 \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "name=Dr. New Bottom Test" \
  -d "gender=Female" \
  -d "age=40" \
  -d "email=$EMAIL" \
  -d "phone=9999999999" \
  -d "location=Delhi")

TEMP_ID=$(echo "$STEP1" | python3 -c "import sys, json; print(json.load(sys.stdin).get('tempId', ''))")

if [ -z "$TEMP_ID" ]; then
    echo "❌ Step 1 failed"
    echo "$STEP1" | python3 -m json.tool
    exit 1
fi

echo "✅ Step 1 successful! TempId: $TEMP_ID"
echo ""

echo "📋 Step 2: Complete registration..."
STEP2=$(curl -s -X POST http://localhost:3000/api/register/step2 \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "tempId=$TEMP_ID" \
  -d "specialization=Dermatologist" \
  -d "institute=Test Institute" \
  -d "degree=MBBS" \
  -d "experience_years=5" \
  -d "consultation_fee=900" \
  -d "bio=This doctor should appear at the bottom of the list")

NEW_DOCTOR_ID=$(echo "$STEP2" | python3 -c "import sys, json; print(json.load(sys.stdin)['data']['id'])" 2>/dev/null)

if [ -z "$NEW_DOCTOR_ID" ]; then
    echo "❌ Step 2 failed"
    echo "$STEP2" | python3 -m json.tool
    exit 1
fi

echo "✅ Registration successful! New Doctor ID: $NEW_DOCTOR_ID"
echo ""

sleep 1

echo "📋 Checking doctor list order..."
curl -s "http://localhost:3000/api/doctors/search?limit=100" | python3 -c "
import sys, json
new_id = $NEW_DOCTOR_ID

data = json.load(sys.stdin)
doctors = data['data']
print(f'\n✅ Total doctors: {len(doctors)}')
print(f'\n📋 First 3 doctors (oldest):')
for i, d in enumerate(doctors[:3]):
    print(f'   {i+1}. ID:{d[\"id\"]} - {d[\"name\"]}')

print(f'\n📋 Last 3 doctors (newest, should include ID:{new_id}):')
for i, d in enumerate(doctors[-3:]):
    marker = ' ← NEW DOCTOR!' if d['id'] == new_id else ''
    print(f'   {i+1}. ID:{d[\"id\"]} - {d[\"name\"]}{marker}')

# Verify new doctor is at the bottom
last_doctor = doctors[-1]
if last_doctor['id'] == new_id:
    print(f'\n✅ SUCCESS! New doctor (ID:{new_id}) appears at BOTTOM of list!')
else:
    print(f'\n❌ FAILED! New doctor (ID:{new_id}) is NOT at bottom. Last doctor is ID:{last_doctor[\"id\"]}')
"

echo ""
echo "=========================================="
echo "✅ Test complete!"
echo "=========================================="
