# HeyCyan Glasses SDK

Comprehensive SDKs for controlling HeyCyan smart glasses via Bluetooth Low Energy (BLE).

## Platform Support

- **iOS**: Full SDK available with demo application (see `ios/` directory)
- **Android**: Full SDK available with demo application (see `android/` directory)

## Overview

This repository provides SDKs for developers to integrate HeyCyan smart glasses functionality into their applications. The glasses support photo capture, video recording, audio recording, and AI-powered image generation.

## Features

### Device Management
- **Bluetooth LE Scanning**: Discover nearby HeyCyan glasses
- **Connection Management**: Connect/disconnect and manage device state
- **Device Information**: Retrieve hardware/firmware versions and MAC address
- **Safety Locator**: Activate audio/visual alerts to find misplaced glasses

### Media Controls
- **Photo Capture**: Remote shutter control for taking photos
- **Video Recording**: Start/stop video recording with status tracking
- **Audio Recording**: Start/stop audio recording with status tracking
- **AI Image Generation**: Trigger AI-powered image creation and receive generated images

### Device Monitoring
- **Battery Status**: Real-time battery level and charging state
- **Media Counts**: Track number of photos, videos, and audio files on device
- **Time Synchronization**: Set device time to match iOS device


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
- `QCOperatorDeviceModeFindDevice` - Trigger audible chime and LED flash to locate glasses

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

## Proprietary Protocol Information

This SDK encapsulates the proprietary BLE communication protocol for HeyCyan glasses. Without this SDK, developers would need to reverse-engineer the following:

### BLE Service & Characteristic UUIDs (Found in Binary)
- **Primary Service UUID**: `7905FFF0-B5CE-4E99-A40F-4B1E122D00D0`
- **Secondary Service UUID**: `6e40fff0-b5a3-f393-e0a9-e50e24dcca9e`
- **QCSDKSERVERUUID1**: Internal service identifier
- **QCSDKSERVERUUID2**: Internal service identifier
- **Command Characteristic**: Write characteristic for device commands
- **Notification Characteristic**: For receiving device responses and status updates
- **Data Transfer Characteristic**: For large data transfers (AI images)

### Command Protocol Structure
Each command follows a specific byte format:
- **Header**: Command identifier bytes
- **Payload**: Command-specific data
- **Checksum**: Validation bytes
- **Acknowledgment**: Required response format

### Key Command Sequences (Examples)
- **Take Photo**: `QCOperatorDeviceModePhoto` command with specific byte encoding
- **Battery Status**: Request/response with battery level (0-100) and charging flag
- **AI Image Transfer**: `QCOperatorDeviceModeAIPhoto` triggers multi-packet protocol
- **Version Info**: Returns hardware version, firmware version, WiFi hardware/firmware versions
- **Media Counts**: Returns photo count, video count, audio count as integers
- **Video Control**: `QCOperatorDeviceModeVideo` / `QCOperatorDeviceModeVideoStop`
- **Audio Control**: `QCOperatorDeviceModeAudio` / `QCOperatorDeviceModeAudioStop`

### Authentication & Handshake
- Initial pairing sequence
- Session establishment protocol
- Keep-alive requirements
- Disconnection handling

### Data Encoding Formats
- **Battery Level**: NSInteger (0-100) with BOOL charging flag
- **Media Counts**: NSInteger values for photo, video, audio counts
- **Timestamp Format**: Uses iOS device time via `setupDeviceDateTime`
- **Image Data**: NSData chunks received via `didReceiveAIChatImageData` delegate
- **MAC Address**: String format returned by `getDeviceMacAddress`
- **Version Strings**: Multiple version fields (hardware, firmware, WiFi versions)

### State Management
- **Connection States**: `QCStateUnbind`, `QCStateConnecting`, `QCStateConnected`, `QCStateDisconnecting`, `QCStateDisconnected`
- **Bluetooth States**: Via `QCBluetoothState` enum
- **Recording States**: Tracked via `recordingVideo` and `recordingAudio` flags
- **Mode Restrictions**: Cannot record video and audio simultaneously
- **Delegate Callbacks**: `QCSDKManagerDelegate` for battery, media updates, AI image data
- **Error Handling**: Fail blocks return current device mode on mode switch failures

Without this SDK, implementing device communication would require:
1. BLE packet sniffing during device operations
2. Reverse-engineering command structures through trial and error
3. Implementing proper error handling for undocumented states
4. Managing complex multi-packet data transfers
5. Handling device-specific quirks and timing requirements

## Troubleshooting

- **Cannot find devices**: Ensure Bluetooth is enabled and glasses are in pairing mode
- **Connection fails**: Check if glasses are already connected to another device
- **Commands fail**: Ensure device is connected and not in use by another operation

## License

This SDK is proprietary software. Contact HeyCyan for licensing information.

## Branches

- **`main`** - Current development branch with improvements and modifications
- **`manufacturer-original`** - Preserved original SDK from manufacturer (unmodified baseline)

## Additional Documentation

For more detailed technical information, see our GitHub issues:

- **[Issue #1: Convert Objective-C SDK to Swift Library](https://github.com/ebowwa/HeyCyanGlassesSDK/issues/1)** - Comprehensive guide for creating a modern Swift wrapper with async/await, Combine, and SwiftUI support
- **[Issue #2: Complete Device I/O Documentation](https://github.com/ebowwa/HeyCyanGlassesSDK/issues/2)** - Exhaustive documentation of every input/output operation with exact code examples and expected responses

## Support

For technical support or questions about the SDK, please contact the HeyCyan development team.