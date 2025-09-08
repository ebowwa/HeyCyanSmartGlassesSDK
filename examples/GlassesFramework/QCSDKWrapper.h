//
//  QCSDKWrapper.h
//  GlassesFramework
//
//  Created on 2025/8/15.
//

#import <Foundation/Foundation.h>
#import <CoreBluetooth/CoreBluetooth.h>
#import <GlassesFramework/QCCentralManager.h>

NS_ASSUME_NONNULL_BEGIN

@class QCSDKManager;

// Re-export the QC types for Swift visibility
@interface QCSDKWrapper : NSObject

+ (QCSDKManager *)sharedManager;
+ (QCCentralManager *)sharedCentralManager;

@end

NS_ASSUME_NONNULL_END