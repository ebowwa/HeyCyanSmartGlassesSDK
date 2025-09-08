//
//  QCCentralManager.h
//  QCBandSDKDemo
//
//  Created by steve on 2023/2/28.
//

#import <Foundation/Foundation.h>
#import <CoreBluetooth/CoreBluetooth.h>

NS_ASSUME_NONNULL_BEGIN

typedef NS_ENUM(NSInteger, QCDeviceType) {
    QCDeviceTypeUnkown = 0,
    QCDeviceTypeWatch,
    QCDeviceTypeRing,
    QCDeviceTypeGlasses,
};

typedef NS_ENUM(NSInteger, QCState) {
    QCStateUnkown = 0,
    QCStateUnbind,
    QCStateConnecting,
    QCStateConnected,
    QCStateDisconnecting,
    QCStateDisconnected,
};

typedef NS_ENUM(NSInteger, QCBluetoothState) {
    QCBluetoothStateUnkown = 0,
    QCBluetoothStateResetting,
    QCBluetoothStateUnsupported,
    QCBluetoothStateUnauthorized,
    QCBluetoothStatePoweredOff,
    QCBluetoothStatePoweredOn,
};

@interface QCBlePeripheral : NSObject

@property (nonatomic, strong) CBPeripheral *peripheral;
@property (nonatomic, copy) NSString *mac;
@property (nonatomic, strong) NSDictionary<NSString *,id> *advertisementData;
@property (nonatomic, strong) NSNumber *RSSI;
@property (nonatomic, assign) BOOL isPaired;
@end

@protocol QCCentralManagerDelegate <NSObject>

@optional

- (void)didState:(QCState)state;
- (void)didBluetoothState:(QCBluetoothState)state;
- (void)didScanPeripherals:(NSArray <QCBlePeripheral*>*)peripheralArr;
- (void)scanPeripheralFinish;
- (void)didConnected:(CBPeripheral *)peripheral;
- (void)didDisconnecte:(CBPeripheral *)peripheral;
- (void)didFailConnected:(CBPeripheral *)peripheral error:(nullable NSError*)error;

@end

@interface QCCentralManager : NSObject

@property (nonatomic, weak) id<QCCentralManagerDelegate> delegate;

@property (strong, nonatomic,readonly) CBCentralManager *centerManager;

@property (strong, nonatomic,readonly) CBPeripheral *connectedPeripheral;

@property (nonatomic,assign,readonly)QCState deviceState;

@property (nonatomic,assign,readonly)QCBluetoothState bleState;

+ (instancetype)shared;


/// Scan Device(Timout: 30s)
- (void)scan;

/// Scan Device with timeout
///
/// - Parameter timeout: If less than or equal to 0, use the default value of 30s
- (void)scanWithTimeout:(NSInteger)timeout;

/// stop scan device
- (void)stopScan;

/// connect smartwatch (Timeout:6s,DeviceType:Ring)
///
/// - Parameter peripheral: device
- (void)connect:(CBPeripheral *)peripheral;

/// connect device (Timeout:6s)
///
/// - Parameter peripheral: device
- (void)connect:(CBPeripheral *)peripheral deviceType:(QCDeviceType)deviceType;

/// connect device with timeout
/// - Parameters:
///   - peripheral: device
///   - timeout: If less than or equal to 0, use the default value of 6s
- (void)connect:(CBPeripheral *)peripheral timeout:(NSInteger)timeout;

/// connect device with timeout
/// - Parameters:
///   - peripheral: device
///   - timeout: If less than or equal to 0, use the default value of 6s
///   - deviceType: defalult is QCDeviceTypeRing
- (void)connect:(CBPeripheral *)peripheral timeout:(NSInteger)timeout deviceType:(QCDeviceType)deviceType;

/// remove bind device
- (void)remove;
@end

NS_ASSUME_NONNULL_END