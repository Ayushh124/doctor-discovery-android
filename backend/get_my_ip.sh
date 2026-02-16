#!/bin/bash

echo "🔍 Finding your Mac's IP address..."
echo ""

# Try multiple methods to get IP
IP=$(ipconfig getifaddr en0 2>/dev/null)
if [ -z "$IP" ]; then
    IP=$(ipconfig getifaddr en1 2>/dev/null)
fi
if [ -z "$IP" ]; then
    IP=$(ifconfig | grep "inet " | grep -v 127.0.0.1 | awk '{print $2}' | head -1)
fi

if [ -z "$IP" ]; then
    echo "❌ Could not auto-detect IP"
    echo ""
    echo "Manual method:"
    echo "1. System Preferences > Network"
    echo "2. Select Wi-Fi (or Ethernet)"
    echo "3. Look for 'IP Address'"
else
    echo "✅ Your Mac's IP: $IP"
    echo ""
    echo "📱 Update your Android app:"
    echo ""
    echo "File 1: RetrofitInstance.kt"
    echo "   Change BASE_URL to: \"http://$IP:3000/api/\""
    echo ""
    echo "File 2: ImageUrlHelper.kt"
    echo "   Change BASE_URL to: \"http://$IP:3000\""
    echo ""
    echo "Then rebuild the app in Android Studio."
fi

echo ""
echo "⚠️  Make sure:"
echo "  - Your phone and Mac are on the SAME Wi-Fi network"
echo "  - Backend server is running (npm run dev)"
echo ""
