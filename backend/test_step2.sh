#!/bin/bash
# Step 2 API Testing Script
# Run this to test all the new search and filter endpoints

echo "═══════════════════════════════════════════════════════"
echo "🧪 Testing Step 2: Search & Filter API"
echo "═══════════════════════════════════════════════════════"
echo ""

BASE_URL="http://localhost:3000/api/doctors"

echo "1️⃣  Testing: Get All Cities"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
curl -s "${BASE_URL}/cities" | python3 -m json.tool
echo ""
echo ""

echo "2️⃣  Testing: Get All Specializations"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
curl -s "${BASE_URL}/specializations" | python3 -m json.tool
echo ""
echo ""

echo "3️⃣  Testing: Search by Name (partial match 'raj')"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
curl -s "${BASE_URL}/search?name=raj" | python3 -m json.tool
echo ""
echo ""

echo "4️⃣  Testing: Filter by Specialization (Cardiologist)"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
curl -s "${BASE_URL}/search?specialization=Cardiologist" | python3 -m json.tool
echo ""
echo ""

echo "5️⃣  Testing: Filter by City (Mumbai)"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
curl -s "${BASE_URL}/search?location=Mumbai" | python3 -m json.tool
echo ""
echo ""

echo "6️⃣  Testing: Sort by Rating (descending)"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
curl -s "${BASE_URL}/search?sortBy=rating&order=desc&limit=3" | python3 -m json.tool
echo ""
echo ""

echo "7️⃣  Testing: Filter by Max Fee (₹700 or less)"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
curl -s "${BASE_URL}/search?maxFee=700&sortBy=consultation_fee&order=asc" | python3 -m json.tool
echo ""
echo ""

echo "8️⃣  Testing: Pagination (Page 1, Limit 3)"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
curl -s "${BASE_URL}/search?page=1&limit=3" | python3 -m json.tool
echo ""
echo ""

echo "9️⃣  Testing: Get Doctor by ID (with popularity increment)"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
curl -s "${BASE_URL}/1" | python3 -m json.tool
echo ""
echo ""

echo "🔟 Testing: Complex Query (Pediatrician in Hyderabad, min rating 4.5)"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
curl -s "${BASE_URL}/search?specialization=Pediatrician&location=Hyderabad&minRating=4.5" | python3 -m json.tool
echo ""
echo ""

echo "═══════════════════════════════════════════════════════"
echo "✅ All tests complete!"
echo "═══════════════════════════════════════════════════════"
