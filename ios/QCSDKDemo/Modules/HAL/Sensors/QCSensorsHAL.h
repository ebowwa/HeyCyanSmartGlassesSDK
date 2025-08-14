//
//  QCSensorsHAL.h
//  Hardware Abstraction Layer - Sensors
//
//  Sensor interfaces for Cyan Glasses (IMU, Touch, Proximity, etc.)
//

#import <Foundation/Foundation.h>
#import <CoreMotion/CoreMotion.h>

NS_ASSUME_NONNULL_BEGIN

// MARK: - IMU (Inertial Measurement Unit)

@protocol QCIMUDelegate <NSObject>
@optional
- (void)imuDidUpdateAcceleration:(CMAcceleration)acceleration;
- (void)imuDidUpdateRotationRate:(CMRotationRate)rotationRate;
- (void)imuDidUpdateHeading:(double)heading;
- (void)imuDidDetectMotion:(NSString *)motionType;
- (void)imuDidDetectGesture:(NSString *)gesture;
@end

@interface QCIMUHAL : NSObject

@property (nonatomic, weak) id<QCIMUDelegate> delegate;
@property (nonatomic, readonly) BOOL isActive;
@property (nonatomic, assign) NSTimeInterval updateInterval;

// IMU Control
- (void)startMotionUpdates;
- (void)stopMotionUpdates;

// Motion Detection
- (void)enableHeadTracking;
- (void)disableHeadTracking;
- (void)enableGestureRecognition;
- (void)disableGestureRecognition;

// Get Current Values
- (CMAcceleration)currentAcceleration;
- (CMRotationRate)currentRotationRate;
- (double)currentHeading;

// Gesture Detection
- (void)detectNod;  // Head nod yes
- (void)detectShake;  // Head shake no
- (void)detectTilt;  // Head tilt

@end

// MARK: - Touch Sensor

typedef NS_ENUM(NSInteger, QCTouchGesture) {
    QCTouchGestureTap = 0,
    QCTouchGestureDoubleTap,
    QCTouchGestureSwipeForward,
    QCTouchGestureSwipeBackward,
    QCTouchGestureLongPress
};

@protocol QCTouchSensorDelegate <NSObject>
@optional
- (void)touchSensorDidDetectGesture:(QCTouchGesture)gesture;
- (void)touchSensorDidBeginTouch:(CGPoint)location;
- (void)touchSensorDidMoveTouch:(CGPoint)location;
- (void)touchSensorDidEndTouch:(CGPoint)location;
@end

@interface QCTouchSensorHAL : NSObject

@property (nonatomic, weak) id<QCTouchSensorDelegate> delegate;
@property (nonatomic, readonly) BOOL isTouching;
@property (nonatomic, assign) BOOL hapticFeedbackEnabled;

// Touch Control
- (void)enableTouchDetection;
- (void)disableTouchDetection;

// Gesture Configuration
- (void)configureTapSensitivity:(CGFloat)sensitivity;
- (void)configureSwipeSensitivity:(CGFloat)sensitivity;

// Haptic Feedback
- (void)provideHapticFeedback;
- (void)provideHapticFeedbackWithIntensity:(CGFloat)intensity;

@end

// MARK: - Proximity Sensor

@protocol QCProximitySensorDelegate <NSObject>
@optional
- (void)proximitySensorDidDetectNearObject;
- (void)proximitySensorDidDetectFarObject;
- (void)proximitySensorDidUpdateDistance:(CGFloat)distanceInCM;
@end

@interface QCProximitySensorHAL : NSObject

@property (nonatomic, weak) id<QCProximitySensorDelegate> delegate;
@property (nonatomic, readonly) BOOL objectIsNear;
@property (nonatomic, readonly) CGFloat currentDistance;

// Proximity Control
- (void)startProximityDetection;
- (void)stopProximityDetection;
- (void)setProximityThreshold:(CGFloat)distanceInCM;

@end

// MARK: - Ambient Light Sensor

@protocol QCAmbientLightSensorDelegate <NSObject>
@optional
- (void)ambientLightSensorDidUpdateBrightness:(CGFloat)lux;
- (void)ambientLightSensorDidDetectDarkEnvironment;
- (void)ambientLightSensorDidDetectBrightEnvironment;
@end

@interface QCAmbientLightSensorHAL : NSObject

@property (nonatomic, weak) id<QCAmbientLightSensorDelegate> delegate;
@property (nonatomic, readonly) CGFloat currentLux;
@property (nonatomic, readonly) BOOL isDarkEnvironment;

// Light Sensor Control
- (void)startLightDetection;
- (void)stopLightDetection;
- (void)setDarkThreshold:(CGFloat)lux;
- (void)setBrightThreshold:(CGFloat)lux;

@end

// MARK: - Combined Sensors Manager

@interface QCSensorsHAL : NSObject

@property (nonatomic, strong, readonly) QCIMUHAL *imu;
@property (nonatomic, strong, readonly) QCTouchSensorHAL *touchSensor;
@property (nonatomic, strong, readonly) QCProximitySensorHAL *proximitySensor;
@property (nonatomic, strong, readonly) QCAmbientLightSensorHAL *ambientLightSensor;

+ (instancetype)sharedInstance;

// Sensor Management
- (void)initializeAllSensors;
- (void)shutdownAllSensors;

// Calibration
- (void)calibrateIMU;
- (void)calibrateTouchSensor;

// Power Management
- (void)enterLowPowerMode;
- (void)exitLowPowerMode;

@end

NS_ASSUME_NONNULL_END