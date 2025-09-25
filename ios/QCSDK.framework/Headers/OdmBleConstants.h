//
//  OdmBleConstants.h
//  OdmLightBle
//
//  Created by 李锋 on 15/8/11.
//  Copyright (c) 2015年 X. All rights reserved.
//

#import <Foundation/Foundation.h>

#define Ble_Extern extern
/*!
 *  @discussion 计步器通知,用户开启计步器之后,如果需要得到变化,需监听此通知
 */
Ble_Extern NSString *const OdmNotifyPedometer;
/*!
 *  @discussion 在CentralManager中registeClassForPeripheral传入，表示使用哪种类型解析
 */
Ble_Extern NSString *const OdmWphPeripheral;
/*!
 *  @discussion 特征值检索完毕通知,蓝牙的一切操作都在这之后执行,否则会失败
 */
Ble_Extern NSString *const OdmNotifyRetrieveFinish;
/*!
 *  @discussion device to phone 通知
 */
Ble_Extern NSString *const OdmNotifyD2P;
Ble_Extern NSString *const OdmNotifyD2PDataKey;
Ble_Extern NSString *const OdmNotifyD2PCharacterKey;
Ble_Extern NSString *const OdmNotifyD2PPeripheralKey;
/*!
 *  @discussion 蓝牙中央状态,使用KVO观察值变化
 */
Ble_Extern NSString *const OdmCentralState;
/*!
 *  @discussion 蓝牙连接状态,用KVO观察值变化
 */
Ble_Extern NSString *const OdmBleConnectState;
/*!
 * @discussion 周边蓝牙RSSI,使用notification
 */
Ble_Extern NSString *const OdmBleRSSI;
/*!
 *  @discussion 蓝牙连接状态
 */
typedef NS_ENUM(NSInteger, BLECONNECTSTATE) {
    BLECONNECTSTATEOFF, //未连接
    BLECONNECTSTATEON,  //已连接
    BLECONNECTSTATEFAIL //连接失败
};
