#!/bin/bash

echo "🔧 Doctor Discovery - Network Fix Script"
echo "========================================"
echo ""

# Try to get Mac IP address
echo "📡 Finding your Mac's IP address..."
IP=$(ipconfig getifaddr en0 2>/dev/null || ipconfig getifaddr en1 2>/dev/null || ifconfig | grep "inet " | grep -v 127.0.0.1 | awk '{print $2}' | head -1)

if [ -z "$IP" ]; then
    echo "❌ Could not auto-detect IP address."
    echo ""
    echo "Please find your Mac's IP manually:"
    echo "1. Open System Preferences > Network"
    echo "2. Select your active connection (Wi-Fi or Ethernet)"
    echo "3. Look for 'IP Address: xxx.xxx.xxx.xxx'"
    echo ""
    read -p "Enter your Mac's IP address: " IP
fi

echo ""
echo "✅ Using IP: $IP"
echo ""

# Update RetrofitInstance.kt
echo "📝 Updating RetrofitInstance.kt..."
RETROFIT_FILE="app/src/main/java/com/ayush/doctordiscovery/data/remote/RetrofitInstance.kt"

if [ -f "$RETROFIT_FILE" ]; then
    # Backup original
    cp "$RETROFIT_FILE" "${RETROFIT_FILE}.backup"
    
    # Replace BASE_URL
    sed -i '' "s|http://localhost:3000/api/|http://$IP:3000/api/|g" "$RETROFIT_FILE"
    sed -i '' "s|http://127.0.0.1:3000/api/|http://$IP:3000/api/|g" "$RETROFIT_FILE"
    sed -i '' "s|http://10.0.2.2:3000/api/|http://$IP:3000/api/|g" "$RETROFIT_FILE"
    
    echo "✅ Updated RetrofitInstance.kt"
else
    echo "❌ RetrofitInstance.kt not found!"
fi

# Update ImageUrlHelper.kt
echo "📝 Updating ImageUrlHelper.kt..."
IMAGE_FILE="app/src/main/java/com/ayush/doctordiscovery/util/ImageUrlHelper.kt"

if [ -f "$IMAGE_FILE" ]; then
    # Backup original
    cp "$IMAGE_FILE" "${IMAGE_FILE}.backup"
    
    # Replace BASE_URL
    sed -i '' "s|http://localhost:3000|http://$IP:3000|g" "$IMAGE_FILE"
    sed -i '' "s|http://127.0.0.1:3000|http://$IP:3000|g" "$IMAGE_FILE"
    sed -i '' "s|http://10.0.2.2:3000|http://$IP:3000|g" "$IMAGE_FILE"
    
    echo "✅ Updated ImageUrlHelper.kt"
else
    echo "❌ ImageUrlHelper.kt not found!"
fi

echo ""
echo "========================================"
echo "✅ Network configuration updated!"
echo ""
echo "Next steps:"
echo "1. Make sure your phone and Mac are on the SAME Wi-Fi network"
echo "2. In Android Studio, click Build > Rebuild Project"
echo "3. Run the app on your phone"
echo ""
echo "Backend URL: http://$IP:3000"
echo "========================================"
