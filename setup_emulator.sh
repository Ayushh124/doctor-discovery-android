#!/bin/bash

# Setup Script for Android Emulator Port Forwarding
# This allows the emulator to access your Mac's localhost

echo "═══════════════════════════════════════"
echo "🔧 Setting Up Emulator Port Forwarding"
echo "═══════════════════════════════════════"
echo ""

# Check if adb is available
if ! command -v adb &> /dev/null; then
    echo "❌ Error: adb not found!"
    echo ""
    echo "adb is part of Android SDK Platform Tools."
    echo "It should be at: ~/Library/Android/sdk/platform-tools/adb"
    echo ""
    echo "Add to PATH:"
    echo "  export PATH=\$PATH:~/Library/Android/sdk/platform-tools"
    echo ""
    exit 1
fi

echo "✅ adb found!"
echo ""

# Check if emulator is running
echo "Checking for running emulator..."
devices=$(adb devices | grep -v "List" | grep "device" | wc -l)

if [ $devices -eq 0 ]; then
    echo "❌ No emulator running!"
    echo ""
    echo "Please start the emulator first:"
    echo "  1. Open Android Studio"
    echo "  2. Click 'Run' (▶️)"
    echo "  3. Wait for emulator to fully start"
    echo "  4. Run this script again"
    echo ""
    exit 1
fi

echo "✅ Emulator is running!"
echo ""

# Set up port forwarding
echo "Setting up port forwarding..."
echo "  Emulator port 3000 → Mac localhost:3000"
echo ""

adb reverse tcp:3000 tcp:3000

if [ $? -eq 0 ]; then
    echo "✅ Port forwarding successful!"
    echo ""
    echo "═══════════════════════════════════════"
    echo "🎉 Setup Complete!"
    echo "═══════════════════════════════════════"
    echo ""
    echo "You can now run your app!"
    echo ""
    echo "What this does:"
    echo "  - App requests: http://localhost:3000"
    echo "  - adb forwards to: Your Mac's localhost:3000"
    echo "  - Backend responds!"
    echo ""
    echo "This works at HOME and OFFICE! 🏠🏢"
    echo ""
else
    echo "❌ Port forwarding failed!"
    echo ""
    echo "Troubleshooting:"
    echo "  1. Make sure emulator is fully started"
    echo "  2. Try: adb kill-server && adb start-server"
    echo "  3. Run this script again"
    echo ""
    exit 1
fi
