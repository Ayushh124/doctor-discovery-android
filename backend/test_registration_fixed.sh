#!/bin/bash

echo "🧪 Testing Fixed Registration Flow"
echo "=================================="
echo ""

# Test Step 1 with new fields (gender, age instead of specialization)
echo "📋 Step 1: Testing personal information submission..."
STEP1_RESPONSE=$(curl -s -X POST http://localhost:3000/api/register/step1 \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "name=Dr. Test Doctor" \
  -d "gender=Male" \
  -d "age=35" \
  -d "email=test.doctor@example.com" \
  -d "phone=9876543210" \
  -d "location=Mumbai")

echo "$STEP1_RESPONSE" | python3 -m json.tool
echo ""

# Extract tempId from response
TEMP_ID=$(echo "$STEP1_RESPONSE" | python3 -c "import sys, json; print(json.load(sys.stdin).get('tempId', ''))")

if [ -z "$TEMP_ID" ]; then
    echo "❌ Step 1 failed - no tempId received"
    exit 1
fi

echo "✅ Step 1 successful! TempId: $TEMP_ID"
echo ""

# Test Step 2 with professional details
echo "📋 Step 2: Testing professional details submission..."
STEP2_RESPONSE=$(curl -s -X POST http://localhost:3000/api/register/step2 \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "tempId=$TEMP_ID" \
  -d "specialization=Cardiologist" \
  -d "institute=AIIMS Delhi" \
  -d "degree=MBBS, MD" \
  -d "experience_years=10" \
  -d "consultation_fee=1200" \
  -d "bio=Expert in heart diseases")

echo "$STEP2_RESPONSE" | python3 -m json.tool
echo ""

# Check if doctor was created
if echo "$STEP2_RESPONSE" | grep -q '"success":true'; then
    echo "✅ Registration completed successfully!"
    DOCTOR_ID=$(echo "$STEP2_RESPONSE" | python3 -c "import sys, json; print(json.load(sys.stdin)['data']['id'])")
    echo "   New Doctor ID: $DOCTOR_ID"
else
    echo "❌ Step 2 failed"
    exit 1
fi

echo ""
echo "=================================="
echo "✅ All tests passed!"
echo "=================================="
