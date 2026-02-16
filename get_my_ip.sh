#!/bin/bash

# Script to get your Mac's current IP address
# Run this whenever you change locations (home ↔ office)

echo "═══════════════════════════════════════"
echo "🌐 Finding Your Mac's IP Address..."
echo "═══════════════════════════════════════"
echo ""

# Get WiFi IP (en0)
wifi_ip=$(ipconfig getifaddr en0 2>/dev/null)

# Get Ethernet IP (en1)
ethernet_ip=$(ipconfig getifaddr en1 2>/dev/null)

# Display results
if [ ! -z "$wifi_ip" ]; then
    echo "✅ WiFi IP (en0): $wifi_ip"
    current_ip="$wifi_ip"
elif [ ! -z "$ethernet_ip" ]; then
    echo "✅ Ethernet IP (en1): $ethernet_ip"
    current_ip="$ethernet_ip"
else
    echo "❌ No active network connection found"
    exit 1
fi

echo ""
echo "═══════════════════════════════════════"
echo "📝 Update These Files:"
echo "═══════════════════════════════════════"
echo ""
echo "1. RetrofitInstance.kt"
echo "   Line 21: private const val BASE_URL = \"http://$current_ip:3000/api/\""
echo ""
echo "2. ImageUrlHelper.kt"
echo "   Line 17: private const val BASE_URL = \"http://$current_ip:3000\""
echo ""
echo "═══════════════════════════════════════"
echo "🔧 Quick Commands:"
echo "═══════════════════════════════════════"
echo ""
echo "Test backend:"
echo "  curl http://$current_ip:3000/health"
echo ""
echo "Test from browser:"
echo "  http://$current_ip:3000/api/doctors"
echo ""
echo "═══════════════════════════════════════"
echo "💡 Current Location Guide:"
echo "═══════════════════════════════════════"
if [[ $current_ip == 192.168.1.* ]]; then
    echo "📍 You are at: HOME"
    echo "   Home IP: $current_ip"
elif [[ $current_ip == 10.5.50.* ]]; then
    echo "📍 You are at: OFFICE"
    echo "   Office IP: $current_ip"
else
    echo "📍 Unknown location"
    echo "   Current IP: $current_ip"
fi
echo ""
