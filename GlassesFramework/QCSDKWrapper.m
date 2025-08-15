//
//  QCSDKWrapper.m
//  GlassesFramework
//
//  Created on 2025/8/15.
//

#import "QCSDKWrapper.h"
#import <QCSDK/QCSDKManager.h>

@implementation QCSDKWrapper

+ (QCSDKManager *)sharedManager {
    return [QCSDKManager shareInstance];
}

+ (QCCentralManager *)sharedCentralManager {
    return [QCCentralManager shared];
}

@end