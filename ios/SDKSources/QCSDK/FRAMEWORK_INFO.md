# QCSDK Framework Information

## Framework Structure

### Binary (`QCSDK`)
- **Type**: Static library archive (ar archive)
- **Architecture**: arm64 (iOS devices)
- **Location**: `/QCSDK.framework/QCSDK`
- **Description**: Compiled binary containing the implementation of all SDK classes

### Info.plist
- **Bundle ID**: com.qcwx.dev.QCSDK
- **Version**: 1.0.0
- **Minimum iOS**: 9.0
- **Supported Devices**: iPhone and iPad (arm64 only)
- **Build SDK**: iOS 18.2
- **Xcode Version**: 16.2 (16C5032a)

### Code Signature (`_CodeSignature/`)
Contains digital signatures for framework validation:
- `CodeDirectory` - Directory of signed code
- `CodeRequirements` - Requirements for code execution
- `CodeResources` - Resource signatures
- `CodeSignature` - Main signature file

### Headers
Public API headers exposed by the framework:
- `QCSDK.h` - Main umbrella header
- `QCSDKManager.h` - Device management
- `QCSDKCmdCreator.h` - Command creation
- `QCVersionHelper.h` - Version utilities
- `QCVolumeInfoModel.h` - Volume model
- `QCDFU_Utils.h` - Firmware updates
- `OdmBleConstants.h` - BLE constants

## Important Notes

1. This is a **pre-compiled binary framework** - source code (.m files) is not available
2. The framework is built for **arm64 only** (physical iOS devices)
3. To use in simulator, you would need an x86_64/arm64 (M1) slice
4. The framework is **code-signed** for distribution

## Usage in Xcode

1. Add framework to project
2. Embed & Sign in target settings
3. Import headers: `#import <QCSDK/QCSDK.h>`