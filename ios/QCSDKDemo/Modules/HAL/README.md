# Hardware Abstraction Layer (HAL)

This directory contains the Hardware Abstraction Layer interfaces for all Cyan Glasses peripherals and hardware components.

## Structure

### 📷 Camera (`/Camera`)
- **QCCameraHAL.h** - Camera control interface
  - Photo capture
  - Video recording
  - AI photo processing
  - Streaming capabilities
  - Resolution and mode settings

### 🔊 Audio (`/Audio`)
- **QCAudioHAL.h** - Complete audio system interface
  - **Microphone** - Recording, voice recognition, noise cancellation
  - **Speaker** - Playback, text-to-speech, spatial audio
  - Audio routing and session management

### 🖥️ Display (`/Display`)
- **QCDisplayHAL.h** - HUD/Display control
  - AR overlay support
  - Notification display
  - HUD elements (time, battery, navigation)
  - Brightness and color control
  - Power management

### 📡 Sensors (`/Sensors`)
- **QCSensorsHAL.h** - All sensor interfaces
  - **IMU** - Accelerometer, gyroscope, head tracking
  - **Touch Sensor** - Gesture recognition
  - **Proximity Sensor** - Object detection
  - **Ambient Light Sensor** - Auto-brightness

### 🔋 Battery (`/Battery`)
- **QCBatteryHAL.h** - Power management
  - Battery level monitoring
  - Charging status and control
  - Power modes (Normal, Low Power, Performance)
  - Battery health diagnostics
  - Temperature monitoring

### 💾 Storage (`/Storage`)
- **QCStorageHAL.h** - File system management
  - File operations (CRUD)
  - Media management
  - Storage optimization
  - Cloud sync capabilities
  - Storage monitoring and alerts

## Usage Example

```objc
// Camera usage
QCCameraHAL *camera = [[QCCameraHAL alloc] init];
camera.delegate = self;
[camera initializeCamera];
[camera capturePhoto];

// Audio usage
QCAudioHAL *audio = [QCAudioHAL sharedInstance];
[audio.microphone startRecording];
[audio.speaker setVolume:0.8];

// Sensor usage
QCSensorsHAL *sensors = [QCSensorsHAL sharedInstance];
[sensors.imu startMotionUpdates];
[sensors.touchSensor enableTouchDetection];
```

## Integration Notes

1. These are header files defining the interfaces
2. Implementation files (.m) should be created based on actual hardware capabilities
3. Each HAL component can be used independently or through combined managers
4. All delegates are optional for flexible integration
5. Thread-safety should be implemented in the .m files

## Architecture Benefits

- **Abstraction** - Hardware details hidden from application layer
- **Modularity** - Each component can be developed/tested independently  
- **Testability** - Easy to mock for unit testing
- **Portability** - Hardware changes only affect HAL implementation
- **Maintainability** - Clear separation of concerns

## Power Management

Most HAL components include power management features:
- Low power modes
- Auto-shutdown capabilities
- Battery optimization
- Resource monitoring

## Future Enhancements

Potential additions:
- GPS/Location HAL
- Bluetooth HAL (separate from BLE)
- WiFi Direct HAL
- NFC HAL
- Biometric sensors HAL