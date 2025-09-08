# GlassesFramework Tests

This test suite is organized into two categories based on hardware requirements:

## 📁 UnitTests/
**No hardware required** - Can run on simulator or any device

### Tests:
- **DeviceInfoTests.swift** - Tests the DeviceInfo struct properties and initialization
- **DeviceActionTypeTests.swift** - Tests the DeviceActionType enum values and titles
- **GlassesFrameworkTests.swift** - Tests SDK initialization and singleton patterns

### How to run:
```bash
# Run all unit tests
xcodebuild test -scheme HeyCyanSwift -only-testing:GlassesFrameworkTests/UnitTests

# Or run on simulator
xcodebuild test -scheme HeyCyanSwift -destination 'platform=iOS Simulator,name=iPhone 15'
```

---

## 📁 HardwareIntegrationTests/
**Requires physical HeyCyan glasses + iPhone with Bluetooth**

### Prerequisites:
- ✅ Physical HeyCyan glasses (M01 model)
- ✅ iPhone with Bluetooth enabled
- ✅ Glasses powered on and in range
- ✅ Glasses in pairing mode (if first time)

### Tests:

#### 1. **AIImageCaptureTest.swift**
- Captures AI-processed images from glasses
- Verifies image data receipt and validity
- Keeps preview visible for 5 seconds
- Tests image storage in gallery

#### 2. **ConnectionBondingTests.swift**
- Full connection lifecycle (discover → connect → verify → disconnect)
- Bonding/pairing persistence testing
- Multiple connection cycles (stability testing)
- Connection state transitions

#### 3. **RealDeviceIntegrationTests.swift**
- Comprehensive device operations (battery, version, media info)
- Device commands (take photo, set time)
- Media operations (photo, video, audio recording)

#### 4. **DiscoveredDeviceTests.swift**
- Bluetooth scanning for real devices
- Device discovery and listing
- Connection to discovered devices

#### 5. **BluetoothManagerTests.swift**
- BluetoothManager operations with real device
- State management and delegate callbacks
- Battery and media info updates

### How to run:
```bash
# Run all hardware tests (requires connected iPhone + glasses)
xcodebuild test -scheme HeyCyanSwift -destination 'id=<YOUR_DEVICE_ID>' \
  -only-testing:GlassesFrameworkTests/HardwareIntegrationTests

# Run specific test
xcodebuild test -scheme HeyCyanSwift -destination 'id=<YOUR_DEVICE_ID>' \
  -only-testing:GlassesFrameworkTests/HardwareIntegrationTests/AIImageCaptureTest
```

### Expected Device:
- Model: M01_9FD8 (or similar M01_xxxx)
- Firmware: AM01_V3.2 (or later)

---

## Running All Tests

```bash
# Run everything (requires hardware)
xcodebuild test -scheme HeyCyanSwift -destination 'id=<YOUR_DEVICE_ID>'

# Run only unit tests (no hardware needed)
xcodebuild test -scheme HeyCyanSwift \
  -only-testing:GlassesFrameworkTests/DeviceInfoTests \
  -only-testing:GlassesFrameworkTests/DeviceActionTypeTests \
  -only-testing:GlassesFrameworkTests/GlassesFrameworkTests
```

## Test Results Summary

| Test Category | Hardware Required | Number of Tests | Typical Duration |
|--------------|------------------|-----------------|------------------|
| Unit Tests | No | 18 tests | < 1 second |
| Hardware Integration | Yes (Glasses + iPhone) | 15+ tests | 15-60 seconds |

## Notes

- Hardware tests will fail if glasses are not connected or powered on
- Some tests may take 10-20 seconds due to Bluetooth operations
- AI image capture typically takes 5-10 seconds per image
- Connection tests include deliberate delays for stability