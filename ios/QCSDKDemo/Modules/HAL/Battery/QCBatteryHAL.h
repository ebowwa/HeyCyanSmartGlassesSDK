//
//  QCBatteryHAL.h
//  Hardware Abstraction Layer - Battery
//
//  Battery management interface for Cyan Glasses
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

typedef NS_ENUM(NSInteger, QCBatteryState) {
    QCBatteryStateUnknown = 0,
    QCBatteryStateDischarging,
    QCBatteryStateCharging,
    QCBatteryStateFull
};

typedef NS_ENUM(NSInteger, QCPowerMode) {
    QCPowerModeNormal = 0,
    QCPowerModeLowPower,
    QCPowerModeUltraLowPower,
    QCPowerModePerformance
};

typedef NS_ENUM(NSInteger, QCChargingSpeed) {
    QCChargingSpeedSlow = 0,
    QCChargingSpeedNormal,
    QCChargingSpeedFast
};

@protocol QCBatteryHALDelegate <NSObject>
@optional
- (void)batteryDidUpdateLevel:(NSInteger)level;
- (void)batteryDidChangeState:(QCBatteryState)state;
- (void)batteryDidEnterLowPowerMode;
- (void)batteryDidExitLowPowerMode;
- (void)batteryTemperatureWarning:(CGFloat)temperature;
- (void)batteryChargingSpeedChanged:(QCChargingSpeed)speed;
@end

@interface QCBatteryHAL : NSObject

@property (nonatomic, weak) id<QCBatteryHALDelegate> delegate;
@property (nonatomic, readonly) NSInteger batteryLevel; // 0-100
@property (nonatomic, readonly) QCBatteryState batteryState;
@property (nonatomic, readonly) BOOL isCharging;
@property (nonatomic, readonly) QCPowerMode currentPowerMode;
@property (nonatomic, readonly) NSTimeInterval estimatedTimeRemaining;
@property (nonatomic, readonly) CGFloat batteryTemperature; // Celsius
@property (nonatomic, readonly) NSInteger batteryHealth; // 0-100
@property (nonatomic, readonly) NSInteger cycleCount;

// Battery Monitoring
- (void)startBatteryMonitoring;
- (void)stopBatteryMonitoring;
- (void)updateBatteryStatus;

// Power Management
- (void)setPowerMode:(QCPowerMode)mode;
- (void)enableAutoPowerManagement:(BOOL)enable;
- (void)setLowPowerThreshold:(NSInteger)percentage;

// Charging Control
- (BOOL)isChargingPortConnected;
- (QCChargingSpeed)currentChargingSpeed;
- (NSTimeInterval)estimatedTimeToFullCharge;
- (void)optimizeCharging:(BOOL)enable;

// Battery Health
- (void)runBatteryDiagnostics;
- (NSDictionary *)getBatteryHealthReport;
- (void)calibrateBattery;

// Power Consumption
- (NSDictionary *)getPowerConsumptionByComponent;
- (CGFloat)getCurrentPowerDraw; // mA
- (CGFloat)getAveragePowerDraw; // mA

// Alerts
- (void)setLowBatteryAlert:(NSInteger)percentage;
- (void)setCriticalBatteryAlert:(NSInteger)percentage;
- (void)setTemperatureWarningThreshold:(CGFloat)celsius;

@end

NS_ASSUME_NONNULL_END