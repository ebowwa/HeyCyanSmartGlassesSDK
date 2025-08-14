//
//  WiFiAutoConnect.m
//  QCSDKDemo
//
//  Automatic WiFi connection helper
//

#import "WiFiAutoConnect.h"
#import <NetworkExtension/NetworkExtension.h>

@implementation WiFiAutoConnect

+ (void)connectToWiFiWithSSID:(NSString *)ssid 
                      password:(NSString *)password 
                    completion:(void (^)(BOOL success, NSError * _Nullable error))completion {
    
    if (@available(iOS 11.0, *)) {
        NEHotspotConfiguration *configuration = [[NEHotspotConfiguration alloc] initWithSSID:ssid 
                                                                                  passphrase:password 
                                                                                    isWEP:NO];
        
        // Set to join once (not persistent)
        configuration.joinOnce = YES;
        
        [[NEHotspotConfigurationManager sharedManager] applyConfiguration:configuration 
                                                         completionHandler:^(NSError * _Nullable error) {
            if (error) {
                NSLog(@"WiFi connection error: %@", error.localizedDescription);
                if (completion) {
                    completion(NO, error);
                }
            } else {
                NSLog(@"Successfully requested WiFi connection to %@", ssid);
                if (completion) {
                    completion(YES, nil);
                }
            }
        }];
    } else {
        // iOS 10 or earlier - can't auto-connect
        NSError *error = [NSError errorWithDomain:@"WiFiAutoConnect" 
                                              code:-1 
                                          userInfo:@{NSLocalizedDescriptionKey: @"Automatic WiFi connection requires iOS 11 or later"}];
        if (completion) {
            completion(NO, error);
        }
    }
}

+ (void)disconnectFromWiFiWithSSID:(NSString *)ssid {
    if (@available(iOS 11.0, *)) {
        [[NEHotspotConfigurationManager sharedManager] removeConfigurationForSSID:ssid];
        NSLog(@"Removed WiFi configuration for %@", ssid);
    }
}

@end