# HeyCyan Glasses SDK - Android

Android SDK for controlling HeyCyan smart glasses via Bluetooth Low Energy (BLE).

## Files

- `glasses_sdk_20250723_v01.aar` - Android SDK library (AAR format)
- `GlassesSDKSample/` - Sample Android application demonstrating SDK usage
- `Android_SDK_Development_Guide_CN.pdf` - SDK documentation (Chinese)

## Quick Start

1. Add the AAR file to your Android project's `libs` directory
2. Add the dependency in your app's `build.gradle`:
   ```gradle
   implementation files('libs/glasses_sdk_20250723_v01.aar')
   ```
3. See the `GlassesSDKSample` project for implementation examples

## Requirements

- Android 5.0+ (API level 21)
- Bluetooth Low Energy support
- Android Studio

## Sample Application

The `GlassesSDKSample` directory contains a complete Android application demonstrating:
- Device scanning and connection
- Photo/video/audio capture controls
- Battery status monitoring
- AI image generation
- Device information retrieval

## Support

For technical support or questions about the Android SDK, please see our GitHub issues or contact the HeyCyan development team.