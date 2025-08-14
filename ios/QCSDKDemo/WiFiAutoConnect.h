//
//  WiFiAutoConnect.h
//  QCSDKDemo
//
//  Automatic WiFi connection helper
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface WiFiAutoConnect : NSObject

+ (void)connectToWiFiWithSSID:(NSString *)ssid 
                      password:(NSString *)password 
                    completion:(void (^)(BOOL success, NSError * _Nullable error))completion;

+ (void)disconnectFromWiFiWithSSID:(NSString *)ssid;

@end

NS_ASSUME_NONNULL_END