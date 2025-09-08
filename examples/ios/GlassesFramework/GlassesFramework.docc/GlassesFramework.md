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

### Speech Recognition

The framework includes a `SpeechRecognitionManager` class that enables speech-to-text transcription using the glasses' microphone:

- **Real-time Transcription**: Converts speech from the glasses' mic to text in real-time
- **Multi-language Support**: Supports multiple languages including English, Spanish, French, and more
- **Continuous Recognition**: Can run continuously with automatic restart after silence
- **Glasses Integration**: Automatically detects and uses the glasses' Bluetooth HFP microphone when connected (verified to use "M01_9FD8 (BluetoothHFP)")
- **Automatic Input Selection**: If glasses are connected but not the active input, the manager will automatically switch to the glasses' microphone

**Usage Example**:
```swift
let speechManager = SpeechRecognitionManager()

// Request authorization
speechManager.requestAuthorization { authorized in
    if authorized {
        // Set up handlers
        speechManager.onTranscriptionUpdate = { partial in
            print("Partial: \(partial)")
        }
        
        speechManager.onFinalTranscription = { final in
            print("Final: \(final)")
        }
        
        // Start recognition
        try? speechManager.startRecognition()
    }
}
```

**Verified Behavior**: 
- Testing confirms the speech recognition correctly uses the glasses' microphone (not the phone's built-in mic)
- The glasses appear as "M01_9FD8 (BluetoothHFP)" in the audio input route
- Speech captured through the glasses is successfully transcribed to text

**Important**: Requires adding `NSSpeechRecognitionUsageDescription` to your app's Info.plist

### Built-in Voice Detection ("Hey Cyan")

The HeyCyan glasses include built-in voice detection capabilities that operate independently from iOS:

- **Voice Wakeup**: The glasses have a built-in "Hey Cyan" wake phrase detection system
- **Always Listening**: When enabled, the glasses continuously listen for the wake phrase without phone interaction
- **Wearing Detection**: The glasses can detect when they're being worn to optimize voice detection
- **Full Control**: Both features can be enabled/disabled programmatically through the SDK

**Controlling Voice Wakeup**:
```swift
// Check current status
bluetoothManager.getVoiceWakeupStatus { enabled in
    print("Voice wakeup is \(enabled ? "enabled" : "disabled")")
}

// Enable or disable voice wakeup
bluetoothManager.setVoiceWakeup(enabled: true) { success in
    if success {
        print("Voice wakeup enabled - glasses will listen for 'Hey Cyan'")
    }
}

bluetoothManager.setVoiceWakeup(enabled: false) { success in
    if success {
        print("Voice wakeup disabled - glasses will not respond to 'Hey Cyan'")
    }
}
```

**Controlling Wearing Detection**:
```swift
// Check current status
bluetoothManager.getWearingDetectionStatus { enabled in
    print("Wearing detection is \(enabled ? "enabled" : "disabled")")
}

// Note: setWearingDetection() would need to be added to BluetoothManager
// The QCSDK supports it via QCSDKCmdCreator.setWearingDetection()
```

**AI Speak Modes**:
```swift
// Control AI speaking functionality
bluetoothManager.setAISpeakMode(.start) { success in
    // Start AI speaking
}

bluetoothManager.setAISpeakMode(.stop) { success in
    // Stop AI speaking
}
```

**Verified Status** (Testing Results):
- ✅ Voice Wakeup: **ENABLED** and **CONTROLLABLE** - Can be toggled on/off
- ✅ Wearing Detection: **ENABLED** - Glasses can detect wearing status
- ✅ Both features operate independently from iOS and run directly on the glasses
- ✅ The SDK provides full control over these features

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