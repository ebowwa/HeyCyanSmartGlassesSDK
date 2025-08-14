#!/bin/bash
# Capture logs from official HeyCyan app

echo "Starting logcat capture for HeyCyan app..."
echo "1. Open the official HeyCyan app"
echo "2. Connect to glasses"
echo "3. Open media browser/WiFi features"
echo "4. Press Ctrl+C when done"
echo ""

# Clear old logs
adb logcat -c

# Capture logs (adjust package name if different)
adb logcat -v time | grep -E "heycyan|glasses|wifi|WiFi|http|HTTP|192.168" | tee heycyan_logs.txt

echo "Logs saved to heycyan_logs.txt"
echo ""
echo "Analyzing for interesting patterns..."

grep -E "http://|https://|192\.168\.|URL|endpoint|api|API" heycyan_logs.txt | sort -u