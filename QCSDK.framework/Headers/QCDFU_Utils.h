//
//  QCDFU_Utils.h
//  OudmonBandV2
//
//  Created by ZongBill on 16/9/26.
//  Copyright © 2016年 ODM. All rights reserved.
//

#import <Foundation/Foundation.h>

/**
 *  @discussion 本接口适用于所有芯片在正常模式下的DFU升级功能, 如有其它nRF系列芯片需要使用, 请查阅芯片SDK文档, 确认是否适用, 在此不做保障
 */
@interface QCDFU_Utils : NSObject

extern NSString *const ODM_DFU_UUID_Service;              //服务UUID
extern NSString *const ODM_DFU_UUID_WriteCharacteristic;  //写入特征ID
extern NSString *const ODM_DFU_UUID_NotifyCharacteristic; //通知特征ID

extern int const ODM_DEFAULT_DFU_PACKET_SIZE;
extern int ODM_DFU_PACKET_SIZE;

typedef enum {
    ODM_DFU_FileExtensionHex,
    ODM_DFU_FileExtensionBin,
    ODM_DFU_FileExtensionZip
} ODM_DFU_FileExtension;

typedef enum {
    ODM_DFU_Operation_StartDfuRequest = 0x01,                    //启动固件升级
    ODM_DFU_Operation_InitializeDfuParametersRequest = 0x02,     //发送固件信息
    ODM_DFU_Operation_ReceiveFirmwareImageRequest = 0x03,        //接收固件
    ODM_DFU_Operation_ValidateFirmwareRequest = 0x04,            //校验固件
    ODM_DFU_Operation_ActivateAndResetRequest = 0x05,            //激活固件并重启
    ODM_DFU_Operation_CheckStatus = 0x06,                        //检查固件升级状态
    ODM_DFU_Operation_SetupDeviceStatus = 0x40,              //初始化设备信息
    ODM_DFU_Operation_SetDeviceMode = 0x41,                 //设置设备信息
    ODM_DFU_Operation_GetDeviceBattery = 0x42,                 //设置设备电量信息
    ODM_DFU_Operation_GetDeviceVersion = 0x43,                 //设置设备版本信息
    ODM_DFU_Operation_VoiceWakeup = 0x44,                       //AI语音唤醒
    ODM_DFU_Operation_VoiceHeartbeat = 0x45,                    //发送AI语音心跳包
    ODM_DFU_Operation_WearingDetection = 0x46,                  //佩戴校准
    ODM_DFU_Operation_DeviceConfig = 0x47,                      //固件配置
    ODM_DFU_Operation_AISpeak = 0x48,                           //AI语音播报
    ODM_DFU_Operation_Volume = 0x51,                           //音量
    ODM_DFU_Operation_BTStatus = 0x52,                           //设置BT状态
    ODM_DFU_Operation_OTAFileDownloadLink = 0xFC,              //发送OTA文件下载链接
    ODM_DFU_Operation_Thumbnail = 0xFD,                         //AI拍照的图片
    ODM_DFU_Operation_DataUpdate = 0x73,                        //数据上报
} ODM_DFU_Operation;

/// Device operating modes used for various device functionalities.
typedef NS_ENUM(NSInteger, QCOperatorDeviceMode) {
    QCOperatorDeviceModeUnkown = 0x00,             ///< Unknown mode
    QCOperatorDeviceModePhoto = 0x01,               ///< Photo mode (taking pictures)
    QCOperatorDeviceModeVideo,                       ///< Video recording mode
    QCOperatorDeviceModeVideoStop,                   ///< Stop video recording
    QCOperatorDeviceModeTransfer,                    ///< Data transfer mode
    QCOperatorDeviceModeOTA,                         ///< OTA (firmware update) mode
    QCOperatorDeviceModeAIPhoto,                     ///< AI-powered photo mode
    QCOperatorDeviceModeSpeechRecognition,          ///< Speech recognition mode
    QCOperatorDeviceModeAudio,                       ///< Audio recording mode
    QCOperatorDeviceModeTransferStop,                ///< Stop data transfer (media transfer stopped, Bluetooth off)
    QCOperatorDeviceModeFactoryReset,                ///< Factory reset mode
    QCOperatorDeviceModeSpeechRecognitionStop,       ///< Stop speech recognition
    QCOperatorDeviceModeAudioStop,                    ///< Stop audio recording
    QCOperatorDeviceModeFindDevice,                   ///< Find device mode
    QCOperatorDeviceModeRestart,                      ///< Restart device
    QCOperatorDeviceModeNoPowerP2P,                   ///< Restart P2P without power off
    QCOperatorDeviceModeSpeakStart,                   ///< Voice playback start
    QCOperatorDeviceModeSpeakStop,                    ///< Voice playback stop
    QCOperatorDeviceModeTranslateStart,               ///< Translation start
    QCOperatorDeviceModeTranslateStop,                ///< Translation stop
};

/// AI speaking modes indicating the speaking state of the device.
typedef NS_ENUM(NSInteger, QGAISpeakMode) {
    QGAISpeakModeStart = 0x01,        ///< Start speaking
    QGAISpeakModeHold,                ///< Pause speaking (hold)
    QGAISpeakModeStop,                ///< Stop speaking
    QGAISpeakModeThinkingStart,      ///< Start thinking (processing)
    QGAISpeakModeThinkingHold,       ///< Hold thinking (processing)
    QGAISpeakModeThinkingStop,       ///< Stop thinking (processing)
    QGAISpeakModeNoNet = 0xf1,       ///< No network available
};

typedef enum {
    ODM_DFU_Operation_FileInit_Add = 0x01,
    ODM_DFU_Operation_FileInit_Delete = 0x02,
    ODM_DFU_Operation_FileInit_Music = 0x03,
    ODM_DFU_Operation_FileInit_ebook = 0x04
} ODM_DFU_Operation_FileInit_Code;

typedef enum {
    QC_Operation_File_Music = 0x01, //音乐
    QC_Operation_File_Ebook = 0x02, //电子书
    QC_Operation_File_Record = 0x03  //录音
} QC_Operation_File_Code; //文件操作类型

typedef enum {
    ODM_DFU_OperationStatus_SuccessfulResponse = 0x00,
    ODM_DFU_OperationStatus_WrongDataLengthResponse = 0X01,
    ODM_DFU_OperationStatus_InvalidDataResponse = 0x02,
    ODM_DFU_OperationStatus_WrongCommandStageResponse = 0x03,
    ODM_DFU_OperationStatus_InvalidCommandParameterResponse = 0x04,
    ODM_DFU_OperationStatus_DeviceInternalErrorResponse = 0x05,
    ODM_DFU_OperationStatus_NotEnoughPowerResponse = 0x06,
    ODM_DFU_OperationStatus_DialFileOverwhelmingResponse = 0x07
} ODM_DFU_OperationStatus;

typedef NS_ENUM(NSUInteger, ODM_DFU_Device_Process_Status) {
    ODM_DFU_Device_Process_Status_Free = 0x00,
    ODM_DFU_Device_Process_Status_ReadyToUpdate = 0x01,
    ODM_DFU_Device_Process_Status_ParameterInited = 0x02,
    ODM_DFU_Device_Process_Status_FirmwareReceiving = 0x03,
    ODM_DFU_Device_Process_Status_FirmwareValidated = 0x04,
    ODM_DFU_Device_Process_Status_NotKnown = 0x05
};

typedef enum {
    ODM_DFU_FirmwareType_Application = 0x01, //应用程序
    ODM_DFU_FirmwareType_Bootloader = 0x02,  //启动驱动
    ODM_DFU_FirmwareType_Softdevice = 0x03,  //硬件驱动
} ODM_DFU_FirmwareType;

typedef enum {
    ODM_DFU_BandType_TwoBand = 0x00, //"双页"升级模式
    ODM_DFU_BandType_OneBand = 0x01, //"单页"升级模式
} ODM_DFU_BandType;

typedef enum {
    ODM_RES_ResourceType_Default = 0x00, //默认, 即无资源
    ODM_RES_ResourceType_Image = 0x01,   //图片
    ODM_RES_ResourceType_Text = 0x02,    //文字
} ODM_RES_ResourceType;

typedef enum {
    ODM_RES_UIType_StandBy = 0x01,  //待机资源
    ODM_RES_UIType_Boot = 0x02,     //开机资源
    ODM_RES_UIType_ShutDown = 0x03, //关机资源
    ODM_RES_UIType_All = 0xFF       //全部资源
} ODM_RES_UIType;

typedef enum {
    QCBandRealTimeHeartRateCmdTypeStart = 0x01,//Start real-time heart rate measurement
    QCBandRealTimeHeartRateCmdTypeEnd,//End real-time heart rate measurement
    QCBandRealTimeHeartRateCmdTypeHold,//Continuous heart rate test (for continuous measurement to keep alive)
} QCBandRealTimeHeartRateCmdType;


//错误相关
extern NSString *const kOdmDFUErrorDomain;
extern NSString *const kOdmDFUErrorMessageKey;
extern NSString *const kOdmDFUErrorStatusCodeKey;

typedef NS_ENUM(NSUInteger, ODM_DFU_Error_Code) {
    ODM_DFU_Error_Code_ChannelBusy = 1001,
    ODM_DFU_Error_Code_NotifyTimeOut,
    ODM_DFU_Error_Code_InvalidParameter,
    ODM_DFU_Error_Code_ResponseTypeNotCorrect
};

typedef NS_ENUM(NSUInteger, QC_File_Error_Code) {
    QC_File_Error_Code_Success = 0,
    QC_File_Error_Code_Size,
    QC_File_Error_Code_Data,
    QC_File_Error_Code_State,
    QC_File_Error_Code_Format,
    QC_File_Error_Code_Flash_Operate,
    QC_File_Error_Code_Lower_Power,
    QC_File_Error_Code_Memory_Full,
};

typedef NS_ENUM(NSInteger, QCDeviceDataUpdateReport) {
    QCDeviceDataUpdateHeartRate = 0x01,
    QCDeviceDataUpdateBloodPressure,
    QCDeviceDataUpdateBloodOxygen,
    QCDeviceDataUpdateStep,//旧版，已没有使用，请使用QCDeviceDataUpdateStepInfo
    QCDeviceDataUpdateTemperature,
    QCDeviceDataUpdateSleep,
    QCDeviceDataUpdateSportRecord,
    QCDeviceDataUpdateAlarm,
    QCDeviceDataUpdateDoNotDisturb,
    QCDeviceDataUpdateAudioRecord,
    QCDeviceDataUpdateHourly,
    QCDeviceDataUpdatePower,
    QCDeviceDataUpdateLowBloodSugar,
    QCDeviceDataUpdateDialIndex,
    QCDeviceDataUpdateLowPower,
    QCDeviceDataUpdateGoal,
    QCDeviceDataUpdateRaiseToWake,
    QCDeviceDataUpdateStepInfo,
    QCDeviceDataUpdatePrayer = 0x25,
    QCDeviceDataUpdateTouchControl = 0x28,
    QCDeviceDataUpdateGame = 0x29,
    QCDeviceDataUpdateTouchSleep = 0x2a,
    QCDeviceDataUpdateHRV = 0x2b,
    QCDeviceDataUpdateStress = 0x2c
};

typedef enum {
    QCSportStateStart = 0x01, //开始
    QCSportStatePause = 0x02,  //暂停
    QCSportStateContinue = 0x03,  //继续
    QCSportStateStop = 0x04,  //结束
    QCSportStateRunning = 0x05,  //运动中
    QCSportStateGetTime = 0x06,  //获取运动开始时间
} QCSportState;

typedef NS_ENUM(NSInteger, QCTouchGestureControlType) {
    QCTouchGestureControlTypeOff = 0x00, //关闭
    QCTouchGestureControlTypeMusic,//音乐
    QCTouchGestureControlTypeVideo,//短视频(Tiktok等等)
    QCTouchGestureControlTypeMSLPraise,//赞念(穆斯林)
    QCTouchGestureControlTypeEBook,//电子书
    QCTouchGestureControlTypeTakePhoto,//拍照
    QCTouchGestureControlTypePhoneCall,//接听电话
    QCTouchGestureControlTypeGame,//游戏
    QCTouchGestureControlTypeHRMeasure,//心率测量
};

+ (NSArray *)getFirmwareTypes;
+ (NSString *)stringFileExtension:(ODM_DFU_FileExtension)fileExtension;

+ (NSData *)packageData:(NSData *)data type:(UInt8)type;
+ (UInt16)packageDataLength:(NSData *)data;
+ (NSData *)unpackData:(NSData *)data;

+ (NSString *)errorWithRetType:(ODM_DFU_OperationStatus)typeCode;
+ (NSString *)getLocalizedTimeOutMessage;

@end
