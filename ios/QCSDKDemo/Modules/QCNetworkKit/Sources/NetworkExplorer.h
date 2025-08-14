//
//  NetworkExplorer.h
//  QCSDKDemo
//
//  Experimental network discovery to find hidden glasses protocols
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@protocol NetworkExplorerDelegate <NSObject>
@optional
- (void)networkExplorerDidFindService:(NSString *)service atPort:(NSInteger)port;
- (void)networkExplorerDidFindEndpoint:(NSString *)endpoint withResponse:(NSString *)response;
- (void)networkExplorerDidReceiveBroadcast:(NSData *)data fromAddress:(NSString *)address;
@end

@interface NetworkExplorer : NSObject

@property (nonatomic, weak) id<NetworkExplorerDelegate> delegate;
@property (nonatomic, copy) NSString *targetIP;

// Start comprehensive network exploration
- (void)startExplorationWithIP:(NSString *)ipAddress;

// Port scanning for common services
- (void)scanCommonMediaPorts;

// Try various HTTP endpoints
- (void)probeHTTPEndpoints;

// Listen for UDP broadcasts
- (void)startUDPListener;

// Try raw socket connections
- (void)probeRawProtocols;

// Stop all exploration
- (void)stopExploration;

@end

NS_ASSUME_NONNULL_END