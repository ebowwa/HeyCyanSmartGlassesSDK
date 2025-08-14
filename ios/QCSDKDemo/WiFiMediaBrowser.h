//
//  WiFiMediaBrowser.h
//  QCSDKDemo
//
//  WiFi-based media browser for accessing glasses media files
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface WiFiMediaBrowser : UIViewController

@property (nonatomic, strong) NSString *deviceIPAddress;
@property (nonatomic, strong) NSString *wifiSSID;
@property (nonatomic, strong) NSString *wifiPassword;

@end

NS_ASSUME_NONNULL_END