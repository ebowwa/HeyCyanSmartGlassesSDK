//
//  QCSDKCmdCreator.h
//  OdmLightBle
//
//  Created by ZongBill on 16/8/4.
//  Copyright (c) 2016年 X. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <QCSDK/OdmBleConstants.h>
#import <QCSDK/QCDFU_Utils.h>

NS_ASSUME_NONNULL_BEGIN

/**
 * This command set is suitable for Bluetooth protocol 1.6.x.
 * For versions earlier than 1.5.x, please use the V1 command set.
 */
@class QCVolumeInfoModel;

@interface QCSDKCmdCreator : NSObject

/// Set the device's current working mode.
/// @param mode Operation mode to be set.
/// @param suc Callback when the operation succeeds.
/// @param fail Callback when the operation fails with an error code.
+ (void)setDeviceMode:(QCOperatorDeviceMode)mode success:(void (^)(void))suc fail:(void (^)(NSInteger))fail;

/// Open Wi-Fi with the specified device mode.
/// @param mode Mode to be used when opening Wi-Fi.
/// @param suc Callback with SSID and password when successful.
/// @param fail Callback with error code when failed.
+ (void)openWifiWithMode:(QCOperatorDeviceMode)mode success:(void (^)(NSString *,NSString *))suc fail:(void (^)(NSInteger))fail;

/// Configure video parameters.
/// @param angle Video angle setting.
/// @param duration Recording duration.
/// @param suc Success callback.
/// @param fail Failure callback.
+ (void)setVideoInfo:(NSInteger)angle duration:(NSInteger)duration success:(void (^)(void))suc fail:(void (^)(void))fail;

/// Retrieve video parameters.
/// @param suc Callback with angle and duration.
/// @param fail Failure callback.
+ (void)getVideoInfoSuccess:(void (^)(NSInteger,NSInteger))suc fail:(void (^)(void))fail;

/// Get the IP address of the device's Wi-Fi connection.
/// @param suc Callback with IP string (nullable).
/// @param fail Failure callback.
+ (void)getDeviceWifiIPSuccess:(nullable void (^)(NSString *_Nullable))suc failed:(nullable void (^)(void))fail;

/// Get media statistics from the device.
/// @param suc Callback with photoCount, videoCount, audioCount, and totalSize.
/// @param fail Failure callback.
+ (void)getDeviceMedia:(void (^)(NSInteger,NSInteger,NSInteger,NSInteger))suc fail:(void (^)(void))fail;

/// Delete all media files on the device.
/// @param suc Success callback.
/// @param fail Failure callback.
+ (void)deleleteAllMediasSuccess:(void (^)(void))suc fail:(void (^)(void))fail;

/// Delete a specific media file by name.
/// @param name Media file name.
/// @param suc Success callback.
/// @param fail Failure callback.
+ (void)deleleteMedia:(NSString*)name success:(void (^)(void))suc fail:(void (^)(void))fail;

/// Configure audio parameters.
/// @param angle Audio recording direction.
/// @param duration Recording duration.
/// @param suc Success callback.
/// @param fail Failure callback.
+ (void)setAudioInfo:(NSInteger)angle duration:(NSInteger)duration success:(void (^)(void))suc fail:(void (^)(void))fail;

/// Retrieve audio configuration info.
/// @param suc Callback with angle and duration.
/// @param fail Failure callback.
+ (void)getAudioInfoSuccess:(void (^)(NSInteger,NSInteger))suc fail:(void (^)(void))fail;

/// Get battery level and charging status of the device.
/// @param suc Callback with battery percentage and charging status.
/// @param fail Failure callback.
+ (void)getDeviceBattery:(void (^)(NSInteger,BOOL))suc fail:(void (^)(void))fail;

/// Get version info of the device, including hardware/firmware.
/// @param suc Callback with hardware version, firmware version, etc.
/// @param fail Failure callback.
+ (void)getDeviceVersionInfoSuccess:(void (^)(NSString*,NSString*,NSString*,NSString*))suc fail:(void (^)(void))fail;

/// Check if the peripheral (Bluetooth device) is currently free (not busy).
/// This should be checked before performing critical operations.
+ (BOOL)isPeripheralFreeNow;

#pragma mark - DFU (Firmware Upgrade)

/// Switch device to DFU (Device Firmware Update) mode.
/// @param finished Callback with optional error.
+ (void)switchToDFU:(nullable void (^)(NSError *_Nullable error))finished;

/// Initialize DFU parameters before sending firmware file.
/// @param type Firmware file type.
/// @param binFileSize Size of the .bin file in bytes.
/// @param checkSum Checksum of the file.
/// @param crc16 CRC16 of the file.
/// @param finished Completion callback with optional error.
+ (void)initDFUFirmwareType:(ODM_DFU_FirmwareType)type binFileSize:(UInt32)binFileSize checkSum:(UInt16)checkSum crc16:(UInt16)crc16 finished:(nullable void (^)(NSError *_Nullable error))finished;

/// Send one packet of the firmware file.
/// @param packetData Data of one firmware packet.
/// @param sn Serial number of the current packet.
/// @param finished Callback with serial number and optional error.
+ (void)sendFilePacketData:(NSData *)packetData serialNumber:(NSUInteger)sn finished:(nullable void (^)(NSUInteger serialNumber, NSError *_Nullable error))finished;

/// Verify the sent firmware file.
/// @param data Additional data if needed.
/// @param finished Completion callback.
+ (void)checkMyFirmwareWithData:(nullable NSData *)data finished:(nullable void (^)(NSError *_Nullable error))finished;

/// Finalize DFU process after all packets are sent.
/// @param finished Completion callback.
+ (void)finishDFU:(nullable void (^)(NSError *_Nullable error))finished;

/// Check the upgrade status of the device.
/// @param data Optional payload.
/// @param finished Callback with current status and optional error.
+ (void)checkCurrentStatusWithData:(nullable NSData *)data finished:(nullable void (^)(ODM_DFU_Device_Process_Status status, NSError *_Nullable error))finished;

/// Get DFU band type information (single/dual band).
/// @param getData Callback with band type and in-DFU flag.
/// @param fail Failure callback.
+ (void)getDFUBandTypeInfoSuccess:(nullable void (^)(ODM_DFU_BandType bandType, BOOL inDFU))getData fail:(nullable void (^)(void))fail;

/// Switch to single-band DFU mode.
/// @param finished Completion callback with optional error.
+ (void)switchToOneBandDFU:(nullable void (^)(NSError *_Nullable error))finished;

#pragma mark - OTA / Config / Misc

/// Send a link to download an OTA firmware file.
/// @param downloadURL URL to download firmware.
/// @param finished Callback with success and optional error.
+ (void)sendOTAFileLink:(NSString *)downloadURL finished:(void (^)(BOOL, NSError *_Nullable))finished;

/// Sync device time with the phone.
/// @param finished Callback with success and optional error.
+ (void)setupDeviceDateTime:(void (^)(BOOL, NSError *_Nullable))finished;

/// Get thumbnail image from specified media pocket.
/// @param pocket Media index.
/// @param suc Success callback with image data and width/height.
/// @param fail Failure callback.
+ (void)getThumbnail:(NSInteger)pocket success:(void (^)(NSData *,NSInteger,NSInteger))suc fail:(void (^)(void))fail;

/// Send heartbeat signal to keep voice feature alive.
/// @param finished Completion callback.
+ (void)sendVoiceHeartbeatWithFinished:(void (^)(BOOL, NSError *_Nullable))finished;

/// Get voice wakeup status.
/// @param finished Callback with wakeup status and result info.
+ (void)getVoiceWakeupWithFinished:(void (^)(BOOL, NSError *_Nullable,id _Nullable))finished;

/// Enable or disable voice wakeup feature.
/// @param isOn Whether to enable voice wakeup.
/// @param finished Callback with success and result.
+ (void)setVoiceWakeup:(BOOL)isOn finished:(void (^)(BOOL, NSError *_Nullable,id _Nullable result))finished;

/// Get wearing detection status.
/// @param finished Callback with status and result.
+ (void)getWearingDetectionWithFinished:(void (^)(BOOL, NSError *_Nullable,id _Nullable))finished;

/// Enable or disable wearing detection.
/// @param isOn Whether to enable detection.
/// @param finished Callback with status and result.
+ (void)setWearingDetection:(BOOL)isOn finished:(void (^)(BOOL, NSError *_Nullable,id _Nullable result))finished;

/// Get device configuration.
/// @param finished Callback with result.
+ (void)getDeviceConfigWithFinished:(void (^)(BOOL, NSError *_Nullable,id _Nullable))finished;

/// Set AI speaking mode.
/// @param model AI speaking mode.
/// @param finished Callback with result.
+ (void)setAISpeekModel:(QGAISpeakMode)model finished:(void (^)(BOOL, NSError *_Nullable))finished;

/// Get volume settings.
/// @param finished Callback with volume info.
+ (void)getVolumeWithFinished:(void (^)(BOOL, NSError *_Nullable,id _Nullable))finished;

/// Set volume settings.
/// @param infoModel Volume configuration model.
/// @param finished Callback with result info.
+ (void)setVolume:(QCVolumeInfoModel*)infoModel finished:(void (^)(BOOL, NSError *_Nullable,id _Nullable result))finished;

/// Set Bluetooth on/off status.
/// @param isOpen Whether to enable Bluetooth.
/// @param finished Callback with result.
+ (void)setBTStatus:(BOOL)isOpen finished:(void (^)(BOOL, NSError *_Nullable))finished;

/// Get Bluetooth status.
/// @param finished Callback with status.
+ (void)getBTStatusWithFinished:(void (^)(BOOL, NSError *_Nullable))finished;

/// Retrieves the MAC address of the connected device.
/// @param success Callback block with the MAC address as a string if retrieval succeeds.
/// @param fail Callback block invoked if the retrieval fails.
+ (void)getDeviceMacAddressSuccess:(nullable void (^)(NSString *_Nullable macAddress))success
                              fail:(void (^)(void))fail;

@end


NS_ASSUME_NONNULL_END
