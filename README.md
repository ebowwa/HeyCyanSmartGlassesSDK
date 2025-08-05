# HeyCyan Glasses SDK for iOS

A comprehensive iOS SDK and demo application for controlling HeyCyan smart glasses via Bluetooth Low Energy (BLE).

## Overview

This SDK provides a complete interface for iOS developers to integrate HeyCyan smart glasses functionality into their applications. The glasses support photo capture, video recording, audio recording, and AI-powered image generation.

## Features

### Device Management
- **Bluetooth LE Scanning**: Discover nearby HeyCyan glasses
- **Connection Management**: Connect/disconnect and manage device state
- **Device Information**: Retrieve hardware/firmware versions and MAC address

### Media Controls
- **Photo Capture**: Remote shutter control for taking photos
- **Video Recording**: Start/stop video recording with status tracking
- **Audio Recording**: Start/stop audio recording with status tracking
- **AI Image Generation**: Trigger AI-powered image creation and receive generated images

### Device Monitoring
- **Battery Status**: Real-time battery level and charging state
- **Media Counts**: Track number of photos, videos, and audio files on device
- **Time Synchronization**: Set device time to match iOS device

## Project Structure

```
HeyCyanGlassesSDK-IOS/
├── QCSDK.framework/          # Core SDK framework
│   ├── Headers/              # Public SDK headers
│   │   ├── QCSDK.h          # Main SDK header
│   │   ├── QCSDKManager.h   # Device management
│   │   ├── QCSDKCmdCreator.h # Command creation utilities
│   │   └── ...
│   └── Info.plist
├── QCSDKDemo/               # Demo application
│   ├── AppDelegate.*        # App lifecycle
│   ├── ViewController.*     # Main features demo
│   ├── QCScanViewController.* # Device scanning
│   └── QCCentralManager.*   # BLE connection management
└── QCSDKDemo.xcodeproj      # Xcode project file
```

## Requirements

- iOS 11.0+
- Xcode 12.0+
- Swift 5.0+ or Objective-C
- Physical iOS device (Bluetooth not supported in simulator)

## Installation

1. Clone or download this repository
2. Open `QCSDKDemo.xcodeproj` in Xcode
3. Build and run on a physical iOS device

## Usage

### Basic Implementation

1. **Import the SDK**
```objc
#import <QCSDK/QCSDK.h>
```

2. **Initialize SDK Manager**
```objc
[QCSDKManager shareInstance].delegate = self;
```

3. **Scan for Devices**
```objc
[[QCCentralManager shared] scan];
```

4. **Connect to Device**
```objc
[[QCCentralManager shared] connect:peripheral];
```

5. **Control Device**
```objc
// Take a photo
[QCSDKCmdCreator setDeviceMode:QCOperatorDeviceModePhoto 
                       success:^{ NSLog(@"Photo taken"); } 
                          fail:^(NSInteger mode) { NSLog(@"Failed"); }];

// Get battery status
[QCSDKCmdCreator getDeviceBattery:^(NSInteger battery, BOOL charging) {
    NSLog(@"Battery: %ld%%, Charging: %@", battery, charging ? @"YES" : @"NO");
} fail:^{ NSLog(@"Failed to get battery"); }];
```

## API Reference

### QCSDKManager
- Singleton instance for SDK management
- Handles device data updates via delegate callbacks

### QCSDKCmdCreator
Key methods:
- `getDeviceVersionInfo` - Get hardware/firmware versions
- `getDeviceMacAddress` - Get device MAC address
- `setupDeviceDateTime` - Sync device time
- `getDeviceBattery` - Get battery level and charging status
- `getDeviceMedia` - Get media file counts
- `setDeviceMode` - Control device operations (photo/video/audio)

### Device Modes
- `QCOperatorDeviceModePhoto` - Take photo
- `QCOperatorDeviceModeVideo` - Start video recording
- `QCOperatorDeviceModeVideoStop` - Stop video recording
- `QCOperatorDeviceModeAudio` - Start audio recording
- `QCOperatorDeviceModeAudioStop` - Stop audio recording
- `QCOperatorDeviceModeAIPhoto` - Generate AI image

## Demo App

The included demo application demonstrates all SDK features:

1. **Search Screen**: Scan and list available devices
2. **Feature Screen**: Control connected device with options for:
   - Version information retrieval
   - Time synchronization
   - Battery status monitoring
   - Media count tracking
   - Photo/video/audio capture
   - AI image generation

## Permissions

Add to your app's `Info.plist`:
```xml
<key>NSBluetoothAlwaysUsageDescription</key>
<string>This app needs Bluetooth to connect to HeyCyan glasses</string>
<key>NSBluetoothPeripheralUsageDescription</key>
<string>This app needs Bluetooth to communicate with HeyCyan glasses</string>
```

## Troubleshooting

- **Cannot find devices**: Ensure Bluetooth is enabled and glasses are in pairing mode
- **Connection fails**: Check if glasses are already connected to another device
- **Commands fail**: Ensure device is connected and not in use by another operation

## License

This SDK is proprietary software. Contact HeyCyan for licensing information.

## Support

For technical support or questions about the SDK, please contact the HeyCyan development team.