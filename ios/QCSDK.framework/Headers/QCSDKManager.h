//
//  QCSDKManager.h
//  QCBandSDK
//
//  Created by steve on 2021/7/7.
//

#import <Foundation/Foundation.h>
#import <CoreBluetooth/CoreBluetooth.h>
#import <UIKit/UIKit.h>
#import <QCSDK/QCDFU_Utils.h>

NS_ASSUME_NONNULL_BEGIN

/*!
 *  @discussion Service IDs supported by the device
 */

extern NSString *const QCSDKSERVERUUID1;
extern NSString *const QCSDKSERVERUUID2;

@protocol QCSDKManagerDelegate <NSObject>

@optional
/// Called when the device battery status is updated.
/// @param battery Battery level percentage (0–100).
/// @param charging YES if the device is currently charging, NO otherwise.
- (void)didUpdateBatteryLevel:(NSInteger)battery charging:(BOOL)charging;

/// Called when media information is updated.
/// @param photo Number of photo files.
/// @param video Number of video files.
/// @param audio Number of audio files.
/// @param type  Media update type identifier (custom defined).
- (void)didUpdateMediaWithPhotoCount:(NSInteger)photo
                          videoCount:(NSInteger)video
                          audioCount:(NSInteger)audio
                                type:(NSInteger)type;

/// Called when WiFi firmware upgrade progress is updated.
- (void)didUpdateWiFiUpgradeProgressWithDownload:(NSInteger)download
                                        upgrade1:(NSInteger)upgrade1
                                        upgrade2:(NSInteger)upgrade2;

/// Called when WiFi firmware upgrade result is reported.
- (void)didReceiveWiFiUpgradeResult:(BOOL)success;

/// Called when raw image data is received from AI chat response.
///
/// @param imageData The raw binary data (e.g. PNG, JPEG) of the image.
- (void)didReceiveAIChatImageData:(NSData *)imageData;


@end

@interface QCSDKManager : NSObject

@property(nonatomic,assign)BOOL debug;

@property (nonatomic, weak) id<QCSDKManagerDelegate> delegate;
// 单例类实例
+ (instancetype)shareInstance;

#pragma mark - 外围设备相关

/// Add peripherals
///
/// @param peripheral     :peripheral equipment
/// @param finished         :add completion callback
- (void)addPeripheral:(CBPeripheral *)peripheral finished:(void (^)(BOOL))finished;

/// remove peripherals
///
/// @param peripheral peripheral equipment
- (void)removePeripheral:(CBPeripheral *)peripheral;

/// remove all peripherals
- (void)removeAllPeripheral;

#pragma mark - AI Functions


#pragma mark - Wifi Funcutions

@end

NS_ASSUME_NONNULL_END
