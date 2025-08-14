# QCBluetoothKit

## Overview
Bluetooth Low Energy connectivity module for QC SDK smart glasses.

## Contents
- `QCCentralManager` - Core Bluetooth manager for device discovery and connection
- `QCBlePeripheral` - Bluetooth peripheral device model
- Bluetooth state management and delegation protocols

## Public API

### Classes
- `QCCentralManager` - Main interface for Bluetooth operations
- `QCBlePeripheral` - Device representation

### Protocols
- `QCCentralManagerDelegate` - Bluetooth event callbacks

### Enums
- `QCDeviceType` - Device type identification
- `QCState` - Connection state
- `QCBluetoothState` - Bluetooth adapter state

## Integration
1. Add as Framework target in Xcode
2. Link against CoreBluetooth.framework
3. Import via `#import <QCBluetoothKit/QCBluetoothKit.h>` or module import

## Dependencies
- Foundation.framework
- CoreBluetooth.framework