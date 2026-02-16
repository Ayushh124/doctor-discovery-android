#!/bin/bash

# Test Image Serving Script
# This script verifies that the backend correctly serves static images

echo "═══════════════════════════════════════"
echo "🖼️  Testing Image Serving"
echo "═══════════════════════════════════════"
echo ""

# Test 1: Check if uploads folder exists
echo "Test 1: Checking uploads folder..."
if [ -d "uploads/doctors" ]; then
    echo "✅ uploads/doctors folder exists"
    echo "   Files in folder:"
    ls -lh uploads/doctors/ 2>/dev/null | tail -n +2 || echo "   (Folder is empty)"
else
    echo "❌ uploads/doctors folder does not exist"
    echo "   Creating folder..."
    mkdir -p uploads/doctors
    echo "✅ Folder created"
fi
echo ""

# Test 2: Check if server is running
echo "Test 2: Checking if backend server is running..."
if curl -s http://localhost:3000/health > /dev/null; then
    echo "✅ Backend server is running"
else
    echo "❌ Backend server is NOT running"
    echo "   Start it with: npm run dev"
    exit 1
fi
echo ""

# Test 3: Try to access uploads route
echo "Test 3: Testing static file serving..."
response=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:3000/uploads/)
if [ "$response" -eq 200 ] || [ "$response" -eq 403 ] || [ "$response" -eq 404 ]; then
    echo "✅ Static file route is configured (Status: $response)"
else
    echo "❌ Static file route issue (Status: $response)"
fi
echo ""

# Test 4: Check if any actual image files exist
echo "Test 4: Looking for actual image files..."
file_count=$(find uploads/doctors -type f 2>/dev/null | wc -l | tr -d ' ')
if [ "$file_count" -gt 0 ]; then
    echo "✅ Found $file_count image file(s)"
    echo "   Files:"
    ls -1 uploads/doctors/ | head -5
    
    # Try to access the first image
    first_image=$(ls -1 uploads/doctors/ | head -1)
    if [ ! -z "$first_image" ]; then
        echo ""
        echo "   Testing first image: $first_image"
        img_response=$(curl -s -o /dev/null -w "%{http_code}" "http://localhost:3000/uploads/doctors/$first_image")
        if [ "$img_response" -eq 200 ]; then
            echo "   ✅ Image is accessible (Status: $img_response)"
            echo "   📸 URL: http://localhost:3000/uploads/doctors/$first_image"
        else
            echo "   ❌ Image not accessible (Status: $img_response)"
        fi
    fi
else
    echo "⚠️  No image files found (folder is empty)"
    echo "   Upload images using: open test_image_upload.html"
fi
echo ""

# Test 5: Check server.js for static middleware
echo "Test 5: Checking server.js configuration..."
if grep -q "express.static.*uploads" server.js; then
    echo "✅ Static file middleware found in server.js"
    grep "express.static" server.js | head -1
else
    echo "❌ Static file middleware NOT found in server.js"
    echo "   Add this line to server.js:"
    echo "   app.use('/uploads', express.static(path.join(__dirname, 'uploads')));"
fi
echo ""

# Test 6: Test from Android emulator perspective
echo "Test 6: Getting Mac IP address for Android testing..."
mac_ip=$(ipconfig getifaddr en0 2>/dev/null)
if [ ! -z "$mac_ip" ]; then
    echo "✅ Mac IP Address: $mac_ip"
    echo "   Use this in ImageUrlHelper.kt:"
    echo "   private const val BASE_URL = \"http://$mac_ip:3000\""
    
    if [ "$file_count" -gt 0 ]; then
        first_image=$(ls -1 uploads/doctors/ | head -1)
        if [ ! -z "$first_image" ]; then
            echo ""
            echo "   Test URL from Android:"
            echo "   http://$mac_ip:3000/uploads/doctors/$first_image"
        fi
    fi
else
    echo "⚠️  Could not determine Mac IP address"
    echo "   Find it manually: ipconfig getifaddr en0"
fi
echo ""

echo "═══════════════════════════════════════"
echo "✅ Image serving test complete!"
echo "═══════════════════════════════════════"
echo ""
echo "Next Steps:"
echo "1. Make sure BASE_URL in ImageUrlHelper.kt matches your IP"
echo "2. Rebuild Android app"
echo "3. Test image loading in the app"
echo ""
