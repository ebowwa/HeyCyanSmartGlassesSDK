# SDK Source Files

This directory contains the visible source files and headers for the SDK components.

## Structure

### QCSDK/
Contains the public headers from the QCSDK.framework:
- `QCSDKManager.h` - Main SDK manager for device communication
- `QCSDKCmdCreator.h` - Command creation utilities
- `QCVersionHelper.h` - Version management
- `QCVolumeInfoModel.h` - Volume information model
- `QCDFU_Utils.h` - Device Firmware Update utilities
- `OdmBleConstants.h` - Bluetooth constants

## Notes
- The QCSDK.framework is a pre-compiled binary framework
- These headers define the public API for interacting with Cyan Glasses devices
- Implementation files (.m) are compiled into the framework binary

## Usage
Import these headers in your project:
```objc
#import <QCSDK/QCSDKManager.h>
#import <QCSDK/QCSDKCmdCreator.h>
```

## Framework Location
The compiled framework is at: `/ios/QCSDK.framework/`