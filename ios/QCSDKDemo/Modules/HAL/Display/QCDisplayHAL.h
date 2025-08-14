//
//  QCDisplayHAL.h
//  Hardware Abstraction Layer - Display
//
//  Display interface for Cyan Glasses HUD/Screen
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

typedef NS_ENUM(NSInteger, QCDisplayMode) {
    QCDisplayModeNormal = 0,
    QCDisplayModeAR,          // Augmented Reality overlay
    QCDisplayModeHUD,         // Heads-up display
    QCDisplayModeNotification,
    QCDisplayModeLowPower
};

typedef NS_ENUM(NSInteger, QCDisplayBrightness) {
    QCDisplayBrightnessAuto = -1,
    QCDisplayBrightnessMin = 0,
    QCDisplayBrightnessLow = 25,
    QCDisplayBrightnessMedium = 50,
    QCDisplayBrightnessHigh = 75,
    QCDisplayBrightnessMax = 100
};

@protocol QCDisplayHALDelegate <NSObject>
@optional
- (void)displayDidChangeMode:(QCDisplayMode)mode;
- (void)displayDidUpdateBrightness:(NSInteger)brightness;
- (void)displayDidShowNotification:(NSString *)notificationId;
- (void)displayDidHideNotification:(NSString *)notificationId;
@end

@interface QCDisplayHAL : NSObject

@property (nonatomic, weak) id<QCDisplayHALDelegate> delegate;
@property (nonatomic, readonly) QCDisplayMode currentMode;
@property (nonatomic, assign) NSInteger brightness; // 0-100 or -1 for auto
@property (nonatomic, readonly) CGSize displayResolution;
@property (nonatomic, readonly) BOOL isDisplayOn;

// Display Control
- (void)initializeDisplay;
- (void)shutdownDisplay;
- (void)turnOnDisplay;
- (void)turnOffDisplay;

// Display Modes
- (void)setDisplayMode:(QCDisplayMode)mode;
- (void)enableAlwaysOnDisplay:(BOOL)enable;

// Content Display
- (void)displayText:(NSString *)text;
- (void)displayText:(NSString *)text withFont:(UIFont *)font color:(UIColor *)color;
- (void)displayImage:(UIImage *)image;
- (void)displayView:(UIView *)view;
- (void)clearDisplay;

// Notifications
- (void)showNotification:(NSString *)title message:(NSString *)message;
- (void)showNotification:(NSString *)title message:(NSString *)message icon:(UIImage * _Nullable)icon;
- (void)hideNotification:(NSString *)notificationId;
- (void)hideAllNotifications;

// HUD Elements
- (void)showTime;
- (void)showBatteryLevel;
- (void)showConnectionStatus;
- (void)showNavigationArrow:(CGFloat)angle distance:(CGFloat)distance;
- (void)showMetric:(NSString *)label value:(NSString *)value unit:(NSString *)unit;

// AR Overlay
- (void)enableARMode;
- (void)disableARMode;
- (void)addARAnnotation:(NSString *)text atPosition:(CGPoint)position;
- (void)removeARAnnotation:(NSString *)annotationId;

// Settings
- (void)setBrightness:(NSInteger)brightness animated:(BOOL)animated;
- (void)setAutoBrightness:(BOOL)enabled;
- (void)setColorTemperature:(CGFloat)temperature; // 0.0 (warm) to 1.0 (cool)
- (void)setNightMode:(BOOL)enabled;

@end

NS_ASSUME_NONNULL_END