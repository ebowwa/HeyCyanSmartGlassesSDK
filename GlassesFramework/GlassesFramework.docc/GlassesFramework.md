# ``GlassesFramework``

A comprehensive framework for connecting to and controlling HeyCyan smart glasses via Bluetooth.

## Overview

GlassesFramework provides a complete Swift and Objective-C interface for interacting with HeyCyan smart glasses. The framework handles all aspects of Bluetooth communication, device management, and glasses-specific functionality including photo capture, video recording, audio recording, and AI-powered image capture.

### Key Features

- **Bluetooth Device Management**: Scan, connect, and manage HeyCyan glasses devices
- **Real-time Device Information**: Battery status, firmware versions, and device state monitoring  
- **Media Controls**: Take photos, record videos, capture audio, and trigger AI image processing
- **Cross-Platform Support**: Works on iOS 13.0+ with full support for modern Swift features

## Getting Started

### Basic Usage

```swift
import GlassesFramework

// Access the shared Bluetooth manager
let bluetoothManager = GlassesSDK.bluetoothManager

// Start scanning for devices
bluetoothManager.startScanning()

// Connect to a discovered device
bluetoothManager.connect(to: device)

// Take a photo
bluetoothManager.takePhoto()
```

### Observing Connection State

The framework uses Combine for reactive state management:

```swift
import SwiftUI
import GlassesFramework

struct ContentView: View {
    @StateObject private var bluetoothManager = GlassesSDK.bluetoothManager
    
    var body: some View {
        if bluetoothManager.isConnected {
            Text("Connected to: \(bluetoothManager.connectedDeviceName)")
        }
    }
}
```

## Architecture

The framework is built with a modular architecture:

- **BluetoothManager**: Main Swift interface for all device operations
- **QCCentralManager**: Low-level Objective-C Bluetooth management
- **QCSDKWrapper**: Bridge between Swift and the QCSDK framework
- **GlassesSDK**: Main entry point and version management

## Topics

### Essential Components

- ``GlassesSDK``
- ``BluetoothManager``
- ``DeviceInfo``
- ``DiscoveredDevice``
- ``DeviceActionType``

### Device Discovery

- ``BluetoothManager/startScanning()``
- ``BluetoothManager/stopScanning()``
- ``BluetoothManager/discoveredDevices``
- ``BluetoothManager/isScanning``

### Connection Management

- ``BluetoothManager/connect(to:)``
- ``BluetoothManager/disconnect()``
- ``BluetoothManager/isConnected``
- ``BluetoothManager/connectionState``
- ``BluetoothManager/connectedDeviceName``

### Device Operations

- ``BluetoothManager/takePhoto()``
- ``BluetoothManager/toggleVideoRecording()``
- ``BluetoothManager/toggleAudioRecording()``
- ``BluetoothManager/takeAIImage()``

### Audio and Volume Control

The HeyCyan glasses function as standard Bluetooth headphones for audio playback and volume control:

- **Volume Control**: The glasses use iOS system volume controls. When connected, they appear as a Bluetooth HFP (Hands-Free Profile) audio device
- **Adjusting Volume**: Use the iPhone's physical volume buttons or Control Center to adjust the glasses' volume
- **Audio Routing**: The glasses automatically become the active audio output when connected, shown as "M01_9FD8 (BluetoothHFP)" in audio settings
- **Stereo Audio**: The glasses support full stereo audio playback with proper left/right channel separation. Use AVAudioPlayer for best Bluetooth compatibility rather than AVAudioEngine

**Note**: The QCSDK volume control commands (`getVolumeSettings`/`setVolumeSettings`) are not supported on current firmware versions. Volume control is handled entirely through iOS system audio.

### Microphone and Audio Recording

The HeyCyan glasses include microphone functionality with two distinct modes:

- **iOS Audio Input**: The glasses' microphone appears as a standard Bluetooth HFP audio input device when connected. You can record audio through the glasses using AVAudioRecorder or any iOS audio recording API. The glasses will be listed as "M01_9FD8 (BluetoothHFP)" in the available audio inputs
- **Internal Recording Mode**: The glasses can record audio directly to their internal storage using `toggleAudioRecording()`. This is a one-way recording that saves audio files on the glasses themselves, which can later be retrieved. This mode does not stream audio back to the phone
- **Recording Quality**: The microphone supports high-quality audio recording at 44.1kHz sample rate with AAC compression
- **Input Monitoring**: You can monitor microphone input levels in real-time using AVAudioRecorder's metering capabilities

**Usage Example**: 
- For phone-based recording: Use AVAudioRecorder with the glasses selected as the audio input
- For glasses storage recording: Call `bluetoothManager.toggleAudioRecording()` to start/stop internal recording

### Device Information

- ``BluetoothManager/getVersionInfo()``
- ``BluetoothManager/getBatteryStatus()``
- ``BluetoothManager/getMediaInfo()``
- ``BluetoothManager/setDeviceTime()``
- ``BluetoothManager/getMacAddress()``
- ``BluetoothManager/deviceInfo``

### Notifications

The framework posts notifications for important events:

- `Notification.Name.aiImageReceived`: Posted when an AI image is received from the glasses

## Requirements

- iOS 13.0+
- Swift 5.0+
- Xcode 14.0+
- QCSDK.framework (included)

## Integration

### Swift Package Manager

Add the framework to your Xcode project:

1. Select your project in Xcode
2. Go to the target's General settings
3. Under "Frameworks, Libraries, and Embedded Content", add GlassesFramework

### Manual Integration

1. Add `GlassesFramework.xcframework` to your project
2. Ensure "Embed & Sign" is selected
3. Import the framework: `import GlassesFramework`

## Bluetooth Permissions

Add the following keys to your app's Info.plist:

```xml
<key>NSBluetoothAlwaysUsageDescription</key>
<string>This app needs Bluetooth to connect to your HeyCyan glasses</string>
<key>NSBluetoothPeripheralUsageDescription</key>
<string>This app needs Bluetooth to communicate with your smart glasses</string>
```

## Error Handling

The framework provides detailed error information through delegate callbacks:

```swift
extension YourClass: QCCentralManagerDelegate {
    func didFailConnected(_ peripheral: CBPeripheral, error: Error?) {
        print("Connection failed: \(error?.localizedDescription ?? "Unknown error")")
    }
}
```

## Best Practices

1. **Always check Bluetooth state** before scanning or connecting
2. **Handle disconnections gracefully** - devices may disconnect unexpectedly
3. **Stop scanning** when not needed to preserve battery life
4. **Monitor device battery** and alert users when glasses battery is low
5. **Use Combine** for reactive UI updates based on device state

## Troubleshooting

### Device Not Found

- Ensure glasses are powered on and in pairing mode
- Check that Bluetooth is enabled on the iOS device
- Verify app has Bluetooth permissions granted

### Connection Failures

- The framework defaults to QCDeviceTypeRing for compatibility
- Some operations may require the glasses to be idle (not recording)
- Check console logs for detailed error messages

### Data Reception Issues

- AI images are received asynchronously via notifications
- Large data transfers may take several seconds
- Monitor the `deviceInfo.aiImageData` property for updates

## Version History

- **1.0.0**: Initial release with full glasses control functionality

## Support

For issues or questions about the GlassesFramework, please refer to the HeyCyan developer documentation or contact support.