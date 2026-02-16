#!/bin/bash
# Step 3 API Testing Script
# Tests the 2-step registration flow

echo "═══════════════════════════════════════════════════════"
echo "🧪 Testing Step 3: Multi-step Registration"
echo "═══════════════════════════════════════════════════════"
echo ""

BASE_URL="http://localhost:3000/api/register"

echo "1️⃣  Testing: Registration Step 1 (Basic Info)"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

RESPONSE=$(curl -s -X POST "${BASE_URL}/step1" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Dr. Test Kumar",
    "email": "test.kumar@healthcare.com",
    "phone": "+91-9876543220",
    "specialization": "Cardiologist",
    "location": "Mumbai"
  }')

echo "$RESPONSE" | python3 -m json.tool
echo ""

# Extract tempId from response
TEMP_ID=$(echo "$RESPONSE" | python3 -c "import sys, json; print(json.load(sys.stdin).get('tempId', ''))")

if [ -z "$TEMP_ID" ]; then
    echo "❌ Step 1 failed - no tempId received"
    exit 1
fi

echo "✅ Received tempId: $TEMP_ID"
echo ""
echo ""

echo "2️⃣  Testing: Retrieve Temporary Registration Data"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
curl -s "${BASE_URL}/temp/${TEMP_ID}" | python3 -m json.tool
echo ""
echo ""

echo "3️⃣  Testing: Registration Step 2 (Complete Registration)"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
curl -s -X POST "${BASE_URL}/step2" \
  -H "Content-Type: application/json" \
  -d "{
    \"tempId\": \"${TEMP_ID}\",
    \"experience_years\": 12,
    \"consultation_fee\": 900,
    \"bio\": \"Experienced cardiologist with over 12 years of practice in Mumbai. Specialized in preventive cardiology.\",
    \"rating\": 4.5,
    \"image_url\": \"https://via.placeholder.com/400x400?text=Dr.+Test+Kumar\"
  }" | python3 -m json.tool
echo ""
echo ""

echo "4️⃣  Testing: Verify New Doctor in Database"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
curl -s "http://localhost:3000/api/doctors/search?email=test.kumar@healthcare.com" | python3 -m json.tool
echo ""
echo ""

echo "5️⃣  Testing: Duplicate Email Prevention"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
curl -s -X POST "${BASE_URL}/step1" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Another Doctor",
    "email": "test.kumar@healthcare.com",
    "phone": "+91-9876543221",
    "specialization": "Dermatologist",
    "location": "Delhi"
  }' | python3 -m json.tool
echo ""
echo ""

echo "6️⃣  Testing: Invalid Email Validation"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
curl -s -X POST "${BASE_URL}/step1" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Dr. Invalid",
    "email": "not-an-email",
    "phone": "+91-9876543222",
    "specialization": "Cardiologist",
    "location": "Mumbai"
  }' | python3 -m json.tool
echo ""
echo ""

echo "7️⃣  Testing: Invalid Phone Validation"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
curl -s -X POST "${BASE_URL}/step1" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Dr. Invalid Phone",
    "email": "valid@test.com",
    "phone": "123",
    "specialization": "Cardiologist",
    "location": "Mumbai"
  }' | python3 -m json.tool
echo ""
echo ""

echo "8️⃣  Testing: Invalid TempId in Step 2"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
curl -s -X POST "${BASE_URL}/step2" \
  -H "Content-Type: application/json" \
  -d '{
    "tempId": "invalid_temp_id",
    "experience_years": 12,
    "consultation_fee": 900,
    "bio": "Test bio with more than twenty characters as required by validation.",
    "rating": 4.5
  }' | python3 -m json.tool
echo ""
echo ""

echo "9️⃣  Testing: Registration Stats"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
curl -s "${BASE_URL}/stats" | python3 -m json.tool
echo ""
echo ""

echo "═══════════════════════════════════════════════════════"
echo "✅ All Step 3 tests complete!"
echo "═══════════════════════════════════════════════════════"
echo ""
echo "📝 Note: Image upload test requires a real image file."
echo "   Use the test_image_upload.html file in a browser to test image uploads."
