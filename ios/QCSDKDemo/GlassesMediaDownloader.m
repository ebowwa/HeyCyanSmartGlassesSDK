#import "GlassesMediaDownloader.h"

#import <QCSDK/QCSDKCmdCreator.h>
#import <NetworkExtension/NetworkExtension.h>
#import <SystemConfiguration/CaptiveNetwork.h>
#include <ifaddrs.h>
#include <arpa/inet.h>

static NSString * const GlassesMediaDownloaderErrorDomain = @"GlassesMediaDownloaderErrorDomain";

typedef NS_ENUM(NSInteger, GlassesMediaDownloaderErrorCode) {
    GlassesMediaDownloaderErrorCodeWifiCredentials = 1,
    GlassesMediaDownloaderErrorCodeWifiIP,
    GlassesMediaDownloaderErrorCodeHotspotUnavailable,
    GlassesMediaDownloaderErrorCodeManifest,
    GlassesMediaDownloaderErrorCodeDownload,
    GlassesMediaDownloaderErrorCodeFilesystem
};

@interface GlassesMediaDownloader ()
@property (nonatomic, copy) GlassesMediaDownloaderStatusHandler statusHandler;
@property (nonatomic, copy) GlassesMediaDownloaderCompletionHandler completionHandler;
@property (nonatomic, copy) NSString *ssid;
@property (nonatomic, copy) NSString *password;
@property (nonatomic, copy) NSString *deviceIP;
@property (nonatomic, strong) NSURLSession *session;
@property (nonatomic, assign) BOOL didFinish;
@property (nonatomic, assign) BOOL isRequestingWifiCredentials;
@end

@implementation GlassesMediaDownloader

- (instancetype)initWithStatusHandler:(GlassesMediaDownloaderStatusHandler)statusHandler {
    self = [super init];
    if (self) {
        _statusHandler = [statusHandler copy];

        // Create custom session configuration that forces WiFi
        NSURLSessionConfiguration *config = [NSURLSessionConfiguration defaultSessionConfiguration];
        config.requestCachePolicy = NSURLRequestReloadIgnoringLocalCacheData;
        config.timeoutIntervalForRequest = 10.0;
        config.timeoutIntervalForResource = 30.0;
        config.waitsForConnectivity = YES;
        config.allowsCellularAccess = NO; // Force WiFi only
        config.discretionary = NO;

        _session = [NSURLSession sessionWithConfiguration:config];
        _isRequestingWifiCredentials = NO;
    }
    return self;
}

- (void)startDownloadWithCompletion:(GlassesMediaDownloaderCompletionHandler)completion {
    self.completionHandler = [completion copy];
    self.didFinish = NO;
    [self updateStatus:@"Requesting Wi-Fi credentials..." preview:nil];
    [self requestWifiCredentials];
}

#pragma mark - Flow

- (void)requestWifiCredentials {
    // Prevent duplicate requests
    if (self.isRequestingWifiCredentials) {
        NSLog(@"🔥 WiFi credentials already being requested, skipping duplicate");
        return;
    }

    self.isRequestingWifiCredentials = YES;
    __weak typeof(self) weakSelf = self;
    NSLog(@"🔥 requestWifiCredentials called - trying to enable WiFi transfer mode");

    // First try to enable WiFi transfer mode on the device
    [QCSDKCmdCreator openWifiWithMode:QCOperatorDeviceModeTransfer success:^(NSString *ssid, NSString *password) {
        __strong typeof(weakSelf) strongSelf = weakSelf;
        if (!strongSelf) { return; }
        strongSelf.ssid = ssid ?: @"";
        strongSelf.password = password ?: @"";
        strongSelf.isRequestingWifiCredentials = NO;
        NSLog(@"🔥 Got WiFi credentials from device - SSID: %@", ssid);
        [strongSelf updateStatus:[NSString stringWithFormat:@"Got hotspot: %@", ssid ?: @"<unknown>"] preview:nil];

        // First try to get device IP - if that works, we don't need to join hotspot
        [strongSelf tryGetDeviceIPFirst];
    } fail:^(NSInteger code) {
        __strong typeof(weakSelf) strongSelf = weakSelf;
        if (!strongSelf) { return; }
        strongSelf.isRequestingWifiCredentials = NO;
        NSLog(@"🔥 Failed to get WiFi credentials from device, code: %ld", (long)code);

        // Try to prompt for manual hotspot connection
        [strongSelf promptForManualHotspot];
    }];
}

- (void)tryGetDeviceIPFirst {
    __weak typeof(self) weakSelf = self;
    NSLog(@"🔥 Got credentials, checking current connection");
    [self updateStatus:[NSString stringWithFormat:@"WiFi: %@ (tap to join)", self.ssid] preview:nil];

    // Try to connect immediately if possible, otherwise prompt user
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(2.0 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        __strong typeof(self) strongSelf = weakSelf;
        if (!strongSelf) { return; }
        [strongSelf tryQuickConnection];
    });
}

- (void)tryQuickConnection {
    NSLog(@"🔥 Attempting quick connection test");
    [self updateStatus:@"Testing connection..." preview:nil];

    // Try to get device IP immediately - this will work if already on same network
    [QCSDKCmdCreator getDeviceWifiIPSuccess:^(NSString * _Nullable ipAddress) {
        if (ipAddress.length > 0) {
            NSLog(@"🔥 Quick connection successful - IP: %@", ipAddress);
            [self updateStatus:[NSString stringWithFormat:@"Found device at %@", ipAddress] preview:nil];
            [self testConnectionToIP:ipAddress];
        } else {
            NSLog(@"🔥 Quick connection failed - trying hotspot configuration");
            [self tryHotspotConfiguration];
        }
    } failed:^{
        NSLog(@"🔥 Quick connection failed - trying hotspot configuration");
        [self tryHotspotConfiguration];
    }];
}

- (void)tryHotspotConfiguration {
    NSLog(@"🔥 Trying hotspot configuration - PERSISTENT approach");
    [self updateStatus:@"Configuring WiFi automatically..." preview:nil];

    if (@available(iOS 11.0, *)) {
        [self joinHotspotWithAggressiveConfig];
    } else {
        [self showManualConnectionInstructions];
    }
}

- (void)joinHotspotWithAggressiveConfig {
    if (@available(iOS 11.0, *)) {
        NSLog(@"🔥 joinHotspotWithAggressiveConfig called with SSID: %@", self.ssid);

        // Create aggressive configuration - multiple attempts with different settings
        [self attemptHotspotConfigurationWithJoinOnce:NO attempt:1];
    }
}

- (void)attemptHotspotConfigurationWithJoinOnce:(BOOL)joinOnce attempt:(NSInteger)attempt {
    if (@available(iOS 11.0, *)) {
        NSLog(@"🔥 Attempt %ld: joinOnce=%d for SSID: %@", (long)attempt, joinOnce, self.ssid);

        NEHotspotConfiguration *configuration = nil;

        if (self.password.length > 0) {
            configuration = [[NEHotspotConfiguration alloc] initWithSSID:self.ssid
                                                               passphrase:self.password
                                                                   isWEP:NO];
        } else {
            configuration = [[NEHotspotConfiguration alloc] initWithSSID:self.ssid];
        }

        // Aggressive configuration options
        configuration.joinOnce = joinOnce;
        if (@available(iOS 13.0, *)) {
            configuration.lifeTimeInDays = @1;
        }

        __weak typeof(self) weakSelf = self;

        [[NEHotspotConfigurationManager sharedManager] applyConfiguration:configuration
                                                     completionHandler:^(NSError * _Nullable error) {
            __strong typeof(weakSelf) strongSelf = weakSelf;
            if (!strongSelf) { return; }

            dispatch_async(dispatch_get_main_queue(), ^{
                if (!error) {
                    NSLog(@"🔥 SUCCESS: Applied hotspot configuration (attempt %ld)", (long)attempt);
                    [strongSelf updateStatus:@"WiFi configured! Waiting for connection..." preview:nil];

                    // Wait for connection - LONGER time like working apps
                    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(12.0 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
                        [strongSelf testConnectionAfterAggressiveJoin];
                    });
                } else if (error.code == NEHotspotConfigurationErrorAlreadyAssociated) {
                    NSLog(@"🔥 Already associated - testing connection");
                    [strongSelf updateStatus:@"Already connected! Testing..." preview:nil];
                    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(5.0 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
                        [strongSelf testConnectionAfterAggressiveJoin];
                    });
                } else if (attempt < 3) {
                    NSLog(@"🔥 Attempt %ld failed, retrying with different settings: %@", (long)attempt, error.localizedDescription);
                    [strongSelf attemptHotspotConfigurationWithJoinOnce:!joinOnce attempt:attempt + 1];
                } else {
                    NSLog(@"🔥 All hotspot attempts failed: %@", error.localizedDescription);
                    [strongSelf updateStatus:@"Auto WiFi failed. Manual connection needed." preview:nil];

                    // Wait before showing manual instructions
                    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(3.0 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
                        [strongSelf showManualConnectionInstructions];
                    });
                }
            });
        }];
    }
}

- (void)testConnectionAfterAggressiveJoin {
    NSLog(@"🔥 Testing connection after AGGRESSIVE hotspot join");
    [self updateStatus:@"Testing WiFi connection..." preview:nil];

    // Aggressive connection testing like working apps
    [self testConnectionWithIncreasingTimeouts];
}

- (void)testConnectionWithIncreasingTimeouts {
    NSLog(@"🔥 Starting aggressive connection testing");

    // Try multiple approaches with increasing timeouts
    [self tryGetDeviceIPWithTimeout:5.0 attempt:1];
}

- (void)tryGetDeviceIPWithTimeout:(NSTimeInterval)timeout attempt:(NSInteger)attempt {
    NSLog(@"🔥 Getting device IP with timeout %.1f, attempt %ld", timeout, (long)attempt);

    [QCSDKCmdCreator getDeviceWifiIPSuccess:^(NSString * _Nullable ipAddress) {
        if (ipAddress.length > 0) {
            NSLog(@"🔥 Got device IP: %@", ipAddress);
            [self updateStatus:[NSString stringWithFormat:@"Found device! Testing connection to %@", ipAddress] preview:nil];
            [self testConnectionToIPWithTimeout:ipAddress timeout:timeout];
        } else if (attempt < 3) {
            NSLog(@"🔥 No IP on attempt %ld, retrying with longer timeout", (long)attempt);
            dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(3.0 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
                [self tryGetDeviceIPWithTimeout:timeout + 3.0 attempt:attempt + 1];
            });
        } else {
            NSLog(@"🔥 No device IP after multiple attempts");
            [self testConnectionWithCommonIPs];
        }
    } failed:^{
        if (attempt < 3) {
            NSLog(@"🔥 QCSDK failed on attempt %ld, retrying", (long)attempt);
            dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(3.0 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
                [self tryGetDeviceIPWithTimeout:timeout + 3.0 attempt:attempt + 1];
            });
        } else {
            NSLog(@"🔥 QCSDK failed after multiple attempts");
            [self testConnectionWithCommonIPs];
        }
    }];
}

- (void)testConnectionToIPWithTimeout:(NSString *)ip timeout:(NSTimeInterval)timeout {
    NSLog(@"🔥 Testing connection to %@ with timeout %.1f", ip, timeout);

    [self updateStatus:[NSString stringWithFormat:@"Testing connection to %@...", ip] preview:nil];

    // Use shorter timeout for initial test, but with retry logic
    NSTimeInterval adjustedTimeout = (timeout > 5.0) ? 3.0 : timeout;
    NSURL *testURL = [NSURL URLWithString:[NSString stringWithFormat:@"http://%@/files/media.config", ip]];
    NSURLRequest *request = [NSURLRequest requestWithURL:testURL
                                            cachePolicy:NSURLRequestReloadIgnoringLocalCacheData
                                        timeoutInterval:adjustedTimeout];

    NSURLSessionDataTask *task = [self.session dataTaskWithRequest:request
                                                                 completionHandler:^(NSData * _Nullable data, NSURLResponse * _Nullable response, NSError * _Nullable error) {
        dispatch_async(dispatch_get_main_queue(), ^{
            if (!error && data && ((NSHTTPURLResponse *)response).statusCode == 200) {
                NSLog(@"🔥 SUCCESS: Connected to device at %@", ip);
                [self updateStatus:[NSString stringWithFormat:@"✅ CONNECTED to %@", ip] preview:nil];
                // Set deviceIP BEFORE proceeding to download manifest
                self->_deviceIP = ip;
                // Verify WiFi connection is established before downloading
                [self verifyWiFiConnectionAndProceed];
            } else {
                NSLog(@"🔥 Failed to connect to %@: %@", ip, error.localizedDescription);
                // If this was the original IP from QCSDK, try it once more with longer timeout
                if ([ip isEqualToString:@"3.192.168.31"] && timeout <= 5.0) {
                    NSLog(@"🔥 Retrying original IP with longer timeout...");
                    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(1.0 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
                        [self testConnectionToIPWithTimeout:ip timeout:8.0];
                    });
                } else {
                    [self testConnectionWithCommonIPs];
                }
            }
        });
    }];
    [task resume];
}


- (void)testConnectivityWithRetries:(NSInteger)maxRetries attempt:(NSInteger)attempt {
    if (self->_deviceIP.length == 0) {
        NSLog(@"🔥 ERROR: No device IP set for connectivity test");
        [self testConnectionWithCommonIPs];
        return;
    }

    NSURL *testURL = [NSURL URLWithString:[NSString stringWithFormat:@"http://%@/files/media.config", self->_deviceIP]];
    NSURLRequest *request = [NSURLRequest requestWithURL:testURL
                                            cachePolicy:NSURLRequestReloadIgnoringLocalCacheData
                                        timeoutInterval:2.0]; // Reduced from 3.0

    [[self.session dataTaskWithRequest:request
                                     completionHandler:^(NSData *data, NSURLResponse *response, NSError *error) {
        dispatch_async(dispatch_get_main_queue(), ^{
            if (!error && data && ((NSHTTPURLResponse *)response).statusCode == 200) {
                NSLog(@"🔥 WiFi connection verified successfully (attempt %ld)", (long)attempt);
                [self updateStatus:[NSString stringWithFormat:@"✅ WiFi stable at %@", self->_deviceIP] preview:nil];
                // Now proceed with endpoint discovery
                [self discoverAllEndpointsForIP:self->_deviceIP];
            } else if (attempt < maxRetries) {
                NSLog(@"🔥 WiFi test failed on attempt %ld, retrying...", (long)attempt);
                [self updateStatus:[NSString stringWithFormat:@"Testing connection... (attempt %ld)", (long)attempt] preview:nil];
                dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(1.0 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{ // Reduced from 2.0
                    [self testConnectivityWithRetries:maxRetries attempt:attempt + 1];
                });
            } else {
                NSLog(@"🔥 WiFi connection test failed after %ld attempts", (long)attempt);
                [self updateStatus:@"WiFi connection unstable" preview:nil];
                [self testConnectionWithCommonIPs];
            }
        });
    }] resume];
}

- (void)testConnectionAfterEnhancedHotspotJoin {
    NSLog(@"🔥 Testing connection after enhanced hotspot join");
    [self updateStatus:@"Testing enhanced connection..." preview:nil];

    // Check network connectivity with multiple approaches
    [self checkNetworkConnectivityWithCompletion:^(BOOL isConnected) {
        if (isConnected) {
            NSLog(@"🔥 Network connectivity confirmed, trying device connection");
            [self tryFinalConnectionTest];
        } else {
            NSLog(@"🔥 No network connectivity, falling back to manual instructions");
            [self showManualConnectionInstructions];
        }
    }];
}

- (void)checkNetworkConnectivityWithCompletion:(void (^)(BOOL))completion {
    // Test internet connectivity by trying to reach apple.com
    NSURL *testURL = [NSURL URLWithString:@"http://captive.apple.com"];
    NSURLRequest *request = [NSURLRequest requestWithURL:testURL
                                            cachePolicy:NSURLRequestReloadIgnoringLocalCacheData
                                        timeoutInterval:3.0];

    [[self.session dataTaskWithRequest:request
                                     completionHandler:^(NSData *data, NSURLResponse *response, NSError *error) {
        BOOL canReachInternet = !error && ((NSHTTPURLResponse *)response).statusCode == 200;
        dispatch_async(dispatch_get_main_queue(), ^{
            completion(canReachInternet);
        });
    }] resume];
}

- (void)tryFinalConnectionTest {
    NSLog(@"🔥 Final connection test attempt");
    [self updateStatus:@"Final connection test..." preview:nil];

    [QCSDKCmdCreator getDeviceWifiIPSuccess:^(NSString * _Nullable ipAddress) {
        if (ipAddress.length > 0) {
            [self testConnectionToIP:ipAddress];
        } else {
            [self showManualConnectionInstructions];
        }
    } failed:^{
        [self showManualConnectionInstructions];
    }];
}

- (void)showManualConnectionInstructions {
    NSLog(@"🔥 Showing enhanced manual connection instructions");
    NSString *instructions = [NSString stringWithFormat:@"📶 MANUALLY join WiFi:\n%@\n\n👆 Then wait for connection...", self.ssid];
    [self updateStatus:instructions preview:nil];

    // Try connection after giving user more time to manually connect
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(20.0 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        [self tryConnectionAfterManualPrompt];
    });
}

- (void)tryConnectionAfterManualPrompt {
    NSLog(@"🔥 Testing connection after manual prompt");
    [self updateStatus:@"Testing manual connection..." preview:nil];

    // First check if we can get device IP through QCSDK
    [QCSDKCmdCreator getDeviceWifiIPSuccess:^(NSString * _Nullable ipAddress) {
        if (ipAddress.length > 0) {
            NSLog(@"🔥 Got device IP after manual prompt: %@", ipAddress);
            [self updateStatus:[NSString stringWithFormat:@"Found device at %@", ipAddress] preview:nil];
            [self testConnectionToIP:ipAddress];
        } else {
            NSLog(@"🔥 No IP from QCSDK, trying comprehensive IP scan");
            [self performComprehensiveIPScan];
        }
    } failed:^{
        NSLog(@"🔥 QCSDK failed, trying comprehensive IP scan");
        [self performComprehensiveIPScan];
    }];
}

- (void)performComprehensiveIPScan {
    [self updateStatus:@"Scanning for glasses device..." preview:nil];

    // Expanded list of common glasses IPs
    NSArray *comprehensiveIPs = @[
        @"192.168.43.1", @"192.168.4.1", @"192.168.1.1", @"192.168.0.1",
        @"192.168.31.1", @"192.168.100.1", @"192.168.123.1", @"192.168.137.1",
        @"3.192.168.31", @"10.0.0.1", @"172.20.10.1"
    ];

    __block NSString *workingIP = nil;
    dispatch_semaphore_t semaphore = dispatch_semaphore_create(0);

    NSLog(@"🔥 Starting comprehensive IP scan with %lu IPs", (unsigned long)comprehensiveIPs.count);

    for (NSString *ip in comprehensiveIPs) {
        NSURL *testURL = [NSURL URLWithString:[NSString stringWithFormat:@"http://%@/files/media.config", ip]];
        NSURLRequest *request = [NSURLRequest requestWithURL:testURL
                                                cachePolicy:NSURLRequestReloadIgnoringLocalCacheData
                                            timeoutInterval:2.0];

        [[self.session dataTaskWithRequest:request
                                         completionHandler:^(NSData *data, NSURLResponse *response, NSError *error) {
            if (!error && data && ((NSHTTPURLResponse *)response).statusCode == 200) {
                workingIP = ip;
                NSLog(@"🔥 Found working IP: %@", ip);
            }
            dispatch_semaphore_signal(semaphore);
        }] resume];

        dispatch_semaphore_wait(semaphore, dispatch_time(DISPATCH_TIME_NOW, (int64_t)(2.5 * NSEC_PER_SEC)));
        if (workingIP) break;
    }

    dispatch_async(dispatch_get_main_queue(), ^{
        if (workingIP) {
            self->_deviceIP = workingIP;
            [self updateStatus:[NSString stringWithFormat:@"✅ Connected to glasses at %@", workingIP] preview:nil];
            // Verify WiFi connection before proceeding to manifest download
            [self verifyWiFiConnectionAndProceed];
        } else {
            [self showFinalErrorMessage];
        }
    });
}

- (void)showFinalErrorMessage {
    NSString *errorMessage = [NSString stringWithFormat:@"❌ Connection failed!\n\n1. 📶 Join WiFi: %@\n2. 🔋 Ensure glasses are ON\n3. 🔄 Try restarting app\n\nNote: Auto WiFi doesn't work reliably on iOS 18+", self.ssid];
    [self updateStatus:errorMessage preview:nil];

    NSError *error = [NSError errorWithDomain:GlassesMediaDownloaderErrorDomain
                                         code:GlassesMediaDownloaderErrorCodeWifiIP
                                     userInfo:@{NSLocalizedDescriptionKey : @"Could not establish connection to glasses device. Please check WiFi connection and try again."}];
    [self finishWithError:error];
}

- (void)promptForManualHotspot {
    [self updateStatus:@"Please connect to glasses WiFi hotspot (AM01W_XXXX) manually" preview:nil];

    // Try common glasses SSIDs after user has time to connect manually
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(10.0 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        __weak typeof(self) weakSelf = self;
        [self tryCommonGlassesIPs];
    });
}

- (void)tryCommonGlassesIPs {
    [self updateStatus:@"Looking for glasses on network..." preview:nil];

    // Try common glasses IPs to find the device
    NSArray *commonIPs = @[@"192.168.1.1", @"192.168.0.1", @"192.168.43.1", @"192.168.4.1", @"192.168.31.1"];

    __block NSString *workingIP = nil;
    dispatch_semaphore_t semaphore = dispatch_semaphore_create(0);

    for (NSString *ip in commonIPs) {
        NSURL *testURL = [NSURL URLWithString:[NSString stringWithFormat:@"http://%@/files/media.config", ip]];
        NSURLRequest *request = [NSURLRequest requestWithURL:testURL cachePolicy:NSURLRequestReloadIgnoringLocalCacheData timeoutInterval:2.0];

        [[self.session dataTaskWithRequest:request completionHandler:^(NSData *data, NSURLResponse *response, NSError *error) {
            if (!error && data && ((NSHTTPURLResponse *)response).statusCode == 200) {
                workingIP = ip;
            }
            dispatch_semaphore_signal(semaphore);
        }] resume];

        dispatch_semaphore_wait(semaphore, dispatch_time(DISPATCH_TIME_NOW, (int64_t)(3.0 * NSEC_PER_SEC)));
        if (workingIP) break;
    }

    dispatch_async(dispatch_get_main_queue(), ^{
        if (workingIP) {
            self->_deviceIP = workingIP;
            [self updateStatus:[NSString stringWithFormat:@"Found glasses at %@", workingIP] preview:nil];
            // Verify WiFi connection before proceeding to manifest download
            [self verifyWiFiConnectionAndProceed];
        } else {
            NSError *error = [NSError errorWithDomain:GlassesMediaDownloaderErrorDomain code:GlassesMediaDownloaderErrorCodeWifiIP userInfo:@{NSLocalizedDescriptionKey : @"Could not find glasses. Please ensure you're connected to the glasses WiFi hotspot."}];
            [self finishWithError:error];
        }
    });
}

- (void)requestDeviceIP {
    __weak typeof(self) weakSelf = self;
    NSLog(@"🔥 requestDeviceIP called after hotspot join");
    [self updateStatus:@"Retrieving device IP address..." preview:nil];
    [QCSDKCmdCreator getDeviceWifiIPSuccess:^(NSString * _Nullable ipAddress) {
        __strong typeof(weakSelf) strongSelf = weakSelf;
        if (!strongSelf) { return; }
        NSLog(@"🔥 Got IP address: %@", ipAddress ?: @"nil");
        if (ipAddress.length == 0) {
            NSLog(@"🔥 IP address is empty, testing connection manually");
            [strongSelf testConnectionWithCommonIPs];
            return;
        }
        NSLog(@"🔥 Setting device IP to: %@", ipAddress);
        strongSelf->_deviceIP = ipAddress;
        [strongSelf updateStatus:[NSString stringWithFormat:@"Found device at %@", ipAddress] preview:nil];

        // Actually test if we can reach this IP
        [strongSelf testConnectionToIP:ipAddress];
    } failed:^{
        __strong typeof(weakSelf) strongSelf = weakSelf;
        if (!strongSelf) { return; }
        NSLog(@"🔥 Failed to get device IP, testing common IPs");
        [strongSelf testConnectionWithCommonIPs];
    }];
}

- (void)testConnectionToIP:(NSString *)ip {
    [self updateStatus:[NSString stringWithFormat:@"Testing connection to %@...", ip] preview:nil];

    NSURL *testURL = [NSURL URLWithString:[NSString stringWithFormat:@"http://%@/files/media.config", ip]];
    NSURLRequest *request = [NSURLRequest requestWithURL:testURL cachePolicy:NSURLRequestReloadIgnoringLocalCacheData timeoutInterval:5.0];

    NSURLSessionDataTask *task = [self.session dataTaskWithRequest:request completionHandler:^(NSData * _Nullable data, NSURLResponse * _Nullable response, NSError * _Nullable error) {
        dispatch_async(dispatch_get_main_queue(), ^{
            if (!error && data && ((NSHTTPURLResponse *)response).statusCode == 200) {
                NSLog(@"🔥 Successfully connected to %@", ip);
                [self updateStatus:[NSString stringWithFormat:@"Connected to %@", ip] preview:nil];
                // Set deviceIP and verify connection before proceeding
                self->_deviceIP = ip;
                [self verifyWiFiConnectionAndProceed];
            } else {
                NSLog(@"🔥 Failed to connect to %@: %@", ip, error.localizedDescription);
                [self testConnectionWithCommonIPs];
            }
        });
    }];
    [task resume];
}

- (void)testConnectionWithCommonIPs {
    [self updateStatus:@"🔍 Scanning for glasses endpoints..." preview:nil];

    // Prioritized IP list - try most likely ones first
    NSArray *priorityIPs = @[@"192.168.31.1", @"3.192.168.31", @"192.168.43.1", @"192.168.4.1"];
    NSArray *fallbackIPs = @[@"192.168.1.1", @"192.168.0.1"];

    __block NSString *workingIP = nil;
    dispatch_semaphore_t semaphore = dispatch_semaphore_create(0);

    NSLog(@"🔥 Starting fast IP scan with priority IPs first");

    // Test priority IPs first with shorter timeout
    for (NSString *ip in priorityIPs) {
        NSURL *testURL = [NSURL URLWithString:[NSString stringWithFormat:@"http://%@/files/media.config", ip]];
        NSURLRequest *request = [NSURLRequest requestWithURL:testURL cachePolicy:NSURLRequestReloadIgnoringLocalCacheData timeoutInterval:1.5];

        [[self.session dataTaskWithRequest:request completionHandler:^(NSData *data, NSURLResponse *response, NSError *error) {
            if (!error && data && ((NSHTTPURLResponse *)response).statusCode == 200) {
                workingIP = ip;
                NSLog(@"🔥 Found working priority IP: %@", ip);
            }
            dispatch_semaphore_signal(semaphore);
        }] resume];

        dispatch_semaphore_wait(semaphore, dispatch_time(DISPATCH_TIME_NOW, (int64_t)(2.0 * NSEC_PER_SEC)));
        if (workingIP) break;
    }

    // If no priority IPs worked, try fallbacks
    if (!workingIP) {
        NSLog(@"🔥 No priority IPs worked, trying fallbacks");
        for (NSString *ip in fallbackIPs) {
            NSURL *testURL = [NSURL URLWithString:[NSString stringWithFormat:@"http://%@/files/media.config", ip]];
            NSURLRequest *request = [NSURLRequest requestWithURL:testURL cachePolicy:NSURLRequestReloadIgnoringLocalCacheData timeoutInterval:1.0];

            [[self.session dataTaskWithRequest:request completionHandler:^(NSData *data, NSURLResponse *response, NSError *error) {
                if (!error && data && ((NSHTTPURLResponse *)response).statusCode == 200) {
                    workingIP = ip;
                    NSLog(@"🔥 Found working fallback IP: %@", ip);
                }
                dispatch_semaphore_signal(semaphore);
            }] resume];

            dispatch_semaphore_wait(semaphore, dispatch_time(DISPATCH_TIME_NOW, (int64_t)(1.5 * NSEC_PER_SEC)));
            if (workingIP) break;
        }
    }

    dispatch_async(dispatch_get_main_queue(), ^{
        if (workingIP) {
            self->_deviceIP = workingIP;
            [self updateStatus:[NSString stringWithFormat:@"✅ Found glasses at %@", workingIP] preview:nil];
            NSLog(@"🔥 Successfully connected to glasses at IP: %@", workingIP);
            // Verify WiFi connection before proceeding to endpoint discovery
            [self verifyWiFiConnectionAndProceed];
        } else {
            NSError *error = [NSError errorWithDomain:GlassesMediaDownloaderErrorDomain code:GlassesMediaDownloaderErrorCodeWifiIP userInfo:@{NSLocalizedDescriptionKey : @"Could not reach glasses. Please manually connect to the glasses WiFi hotspot."}];
            [self finishWithError:error];
        }
    });
}

- (void)discoverAllEndpointsForIP:(NSString *)ip {
    NSLog(@"🔥 Starting fast endpoint discovery for IP: %@", ip);
    [self updateStatus:[NSString stringWithFormat:@"🔍 Discovering endpoints on %@...", ip] preview:nil];

    // Prioritized list - test most likely endpoints first
    NSArray *priorityEndpoints = @[
        @"/files/media.config",
        @"/media.config",
        @"/files/manifest",
        @"/manifest"
    ];

    // Secondary endpoints to test if priority ones fail
    NSArray *secondaryEndpoints = @[
        @"/api/media",
        @"/api/files",
        @"/config",
        @"/files",
        @"/"
    ];

    NSMutableArray *foundEndpoints = [NSMutableArray array];
    dispatch_semaphore_t semaphore = dispatch_semaphore_create(0);

    NSLog(@"🔥 Testing priority endpoints first on IP %@", ip);

    // Test priority endpoints first with shorter timeout
    for (NSString *endpoint in priorityEndpoints) {
        NSString *urlString = [NSString stringWithFormat:@"http://%@%@", ip, endpoint];
        NSURL *testURL = [NSURL URLWithString:urlString];
        NSURLRequest *request = [NSURLRequest requestWithURL:testURL
                                                cachePolicy:NSURLRequestReloadIgnoringLocalCacheData
                                            timeoutInterval:1.5]; // Reduced from 3.0

        [[self.session dataTaskWithRequest:request
                                         completionHandler:^(NSData *data, NSURLResponse *response, NSError *error) {
            if (!error) {
                NSHTTPURLResponse *httpResponse = (NSHTTPURLResponse *)response;
                NSLog(@"🔥 PRIORITY ENDPOINT: %@ - Status: %ld", urlString, (long)httpResponse.statusCode);

                if (httpResponse.statusCode == 200 && data.length > 0) {
                    [foundEndpoints addObject:@{
                        @"url": urlString,
                        @"endpoint": endpoint,
                        @"status": @(httpResponse.statusCode),
                        @"contentLength": @(data.length),
                        @"contentType": httpResponse.MIMEType ?: @"unknown"
                    }];
                    NSLog(@"🔥 ✅ VALID PRIORITY ENDPOINT: %@", urlString);
                }
            }
            dispatch_semaphore_signal(semaphore);
        }] resume];

        dispatch_semaphore_wait(semaphore, dispatch_time(DISPATCH_TIME_NOW, (int64_t)(2.0 * NSEC_PER_SEC))); // Reduced from 3.5

        // If we found the main endpoint, break early
        if (foundEndpoints.count > 0) {
            NSLog(@"🔥 Found priority endpoint, skipping secondary scan");
            break;
        }
    }

    // If no priority endpoints found, test secondary ones
    if (foundEndpoints.count == 0) {
        NSLog(@"🔥 No priority endpoints found, testing secondary ones");

        for (NSString *endpoint in secondaryEndpoints) {
            NSString *urlString = [NSString stringWithFormat:@"http://%@%@", ip, endpoint];
            NSURL *testURL = [NSURL URLWithString:urlString];
            NSURLRequest *request = [NSURLRequest requestWithURL:testURL
                                                    cachePolicy:NSURLRequestReloadIgnoringLocalCacheData
                                                timeoutInterval:1.0];

            [[self.session dataTaskWithRequest:request
                                             completionHandler:^(NSData *data, NSURLResponse *response, NSError *error) {
                if (!error) {
                    NSHTTPURLResponse *httpResponse = (NSHTTPURLResponse *)response;
                    if (httpResponse.statusCode == 200 && data.length > 0) {
                        [foundEndpoints addObject:@{
                            @"url": urlString,
                            @"endpoint": endpoint,
                            @"status": @(httpResponse.statusCode),
                            @"contentLength": @(data.length),
                            @"contentType": httpResponse.MIMEType ?: @"unknown"
                        }];
                        NSLog(@"🔥 ✅ VALID SECONDARY ENDPOINT: %@", urlString);
                    }
                }
                dispatch_semaphore_signal(semaphore);
            }] resume];

            dispatch_semaphore_wait(semaphore, dispatch_time(DISPATCH_TIME_NOW, (int64_t)(1.5 * NSEC_PER_SEC)));

            // If we found an endpoint, break early
            if (foundEndpoints.count > 0) break;
        }
    }

    dispatch_async(dispatch_get_main_queue(), ^{
        NSLog(@"🔥 Fast endpoint discovery complete. Found %lu valid endpoints.", (unsigned long)foundEndpoints.count);

        // Log all found endpoints
        for (NSDictionary *endpointInfo in foundEndpoints) {
            NSLog(@"🔥 📄 Found: %@ (%@ bytes, %@)",
                  endpointInfo[@"url"],
                  endpointInfo[@"contentLength"],
                  endpointInfo[@"contentType"]);
        }

        if (foundEndpoints.count > 0) {
            [self updateStatus:[NSString stringWithFormat:@"🔍 Found %lu endpoints!", (unsigned long)foundEndpoints.count] preview:nil];
            [self processDiscoveredEndpoints:foundEndpoints];
        } else {
            [self updateStatus:@"No media endpoints found" preview:nil];
            NSError *error = [NSError errorWithDomain:GlassesMediaDownloaderErrorDomain code:GlassesMediaDownloaderErrorCodeManifest userInfo:@{NSLocalizedDescriptionKey : @"No media endpoints found on glasses device."}];
            [self finishWithError:error];
        }
    });
}

- (void)processDiscoveredEndpoints:(NSArray *)foundEndpoints {
    NSLog(@"🔥 Processing %lu discovered endpoints", (unsigned long)foundEndpoints.count);

    // Priority order for endpoints
    NSArray *priorityEndpoints = @[
        @"/files/media.config",
        @"/media.config",
        @"/files/manifest",
        @"/manifest",
        @"/api/media"
    ];

    // Try priority endpoints first
    for (NSString *priorityEndpoint in priorityEndpoints) {
        for (NSDictionary *endpointInfo in foundEndpoints) {
            if ([endpointInfo[@"endpoint"] isEqualToString:priorityEndpoint]) {
                NSLog(@"🔥 🎯 Using priority endpoint: %@", endpointInfo[@"url"]);
                [self useDiscoveredEndpoint:endpointInfo];
                return;
            }
        }
    }

    // If no priority endpoints, use the first one found
    if (foundEndpoints.count > 0) {
        NSDictionary *firstEndpoint = foundEndpoints[0];
        NSLog(@"🔥 📋 Using first available endpoint: %@", firstEndpoint[@"url"]);
        [self useDiscoveredEndpoint:firstEndpoint];
    }
}

- (void)useDiscoveredEndpoint:(NSDictionary *)endpointInfo {
    NSString *urlString = endpointInfo[@"url"];
    NSNumber *contentLength = endpointInfo[@"contentLength"];
    NSString *contentType = endpointInfo[@"contentType"];

    NSLog(@"🔥 Using endpoint: %@ (Size: %@ bytes, Type: %@)", urlString, contentLength, contentType);

    // Check if this is a manifest/config file
    NSString *endpoint = endpointInfo[@"endpoint"];
    if ([endpoint containsString:@"media.config"] || [endpoint containsString:@"manifest"] || [endpoint containsString:@"config"]) {
        NSLog(@"🔥 Treating as manifest endpoint: %@", urlString);
        self.deviceIP = [self extractIPFromURL:urlString];
        [self updateStatus:[NSString stringWithFormat:@"📄 Loading manifest from %@", endpoint] preview:nil];
        [self downloadManifestFromCustomURL:urlString];
    } else {
        NSLog(@"🔥 Treating as direct media endpoint: %@", urlString);
        [self updateStatus:[NSString stringWithFormat:@"🎥 Direct media access: %@", endpoint] preview:nil];
        [self downloadDirectMediaFromURL:urlString];
    }
}

- (NSString *)extractIPFromURL:(NSString *)urlString {
    NSURL *url = [NSURL URLWithString:urlString];
    return url.host;
}

- (void)downloadManifestFromCustomURL:(NSString *)manifestURLString {
    NSLog(@"🔥 Downloading manifest from custom URL: %@", manifestURLString);

    NSURL *manifestURL = [NSURL URLWithString:manifestURLString];
    if (!manifestURL) {
        NSError *error = [NSError errorWithDomain:GlassesMediaDownloaderErrorDomain code:GlassesMediaDownloaderErrorCodeManifest userInfo:@{NSLocalizedDescriptionKey : @"Invalid manifest URL."}];
        [self finishWithError:error];
        return;
    }

    [self updateStatus:@"Fetching custom media manifest..." preview:nil];
    __weak typeof(self) weakSelf = self;
    NSURLSessionDataTask *task = [self.session dataTaskWithURL:manifestURL completionHandler:^(NSData * _Nullable data, NSURLResponse * _Nullable response, NSError * _Nullable error) {
        __strong typeof(weakSelf) strongSelf = weakSelf;
        if (!strongSelf) { return; }

        if (error) {
            NSString *description = [NSString stringWithFormat:@"Failed to download manifest: %@", error.localizedDescription ?: @"unknown error"];
            NSError *manifestError = [NSError errorWithDomain:GlassesMediaDownloaderErrorDomain code:GlassesMediaDownloaderErrorCodeManifest userInfo:@{NSLocalizedDescriptionKey : description}];
            [strongSelf finishWithError:manifestError];
            return;
        }

        NSHTTPURLResponse *httpResponse = (NSHTTPURLResponse *)response;
        if (httpResponse.statusCode < 200 || httpResponse.statusCode >= 300) {
            NSString *description = [NSString stringWithFormat:@"Manifest request returned HTTP %ld.", (long)httpResponse.statusCode];
            NSError *manifestError = [NSError errorWithDomain:GlassesMediaDownloaderErrorDomain code:GlassesMediaDownloaderErrorCodeManifest userInfo:@{NSLocalizedDescriptionKey : description}];
            [strongSelf finishWithError:manifestError];
            return;
        }

        NSLog(@"🔥 ✅ Manifest downloaded successfully! Size: %llu bytes", httpResponse.expectedContentLength);

        NSString *configString = [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
        if (!configString) {
            configString = [[NSString alloc] initWithData:data encoding:NSISOLatin1StringEncoding];
        }

        NSLog(@"🔥 📄 Manifest content: %@", configString.length > 200 ? [configString substringToIndex:200] : configString);

        if (configString.length == 0) {
            NSError *manifestError = [NSError errorWithDomain:GlassesMediaDownloaderErrorDomain code:GlassesMediaDownloaderErrorCodeManifest userInfo:@{NSLocalizedDescriptionKey : @"Media manifest is empty."}];
            [strongSelf finishWithError:manifestError];
            return;
        }

        NSArray<NSURL *> *fileURLs = [strongSelf fileURLsFromManifestString:configString baseURL:manifestURL];
        NSLog(@"🔥 📁 Found %lu media files in manifest", (unsigned long)fileURLs.count);

        [strongSelf prepareDownloadDirectoryWithManifest:fileURLs baseURL:manifestURL];
    }];
    [task resume];
}

- (void)downloadDirectMediaFromURL:(NSString *)mediaURLString {
    NSLog(@"🔥 Downloading direct media from: %@", mediaURLString);

    NSURL *mediaURL = [NSURL URLWithString:mediaURLString];
    if (!mediaURL) {
        NSError *error = [NSError errorWithDomain:GlassesMediaDownloaderErrorDomain code:GlassesMediaDownloaderErrorCodeDownload userInfo:@{NSLocalizedDescriptionKey : @"Invalid media URL."}];
        [self finishWithError:error];
        return;
    }

    NSString *filename = mediaURL.lastPathComponent.length > 0 ? mediaURL.lastPathComponent : @"media_file";
    [self updateStatus:[NSString stringWithFormat:@"Downloading: %@", filename] preview:nil];

    // Create a single-file manifest for direct download
    NSArray<NSURL *> *singleFileArray = @[mediaURL];
    NSURL *baseURL = [mediaURL URLByDeletingLastPathComponent];

    [self prepareDownloadDirectoryWithManifest:singleFileArray baseURL:baseURL];
}


- (void)joinHotspotWithModernConfig {
    NSLog(@"🔥 joinHotspotWithModernConfig called with SSID: %@, password: %@", self.ssid, self.password);
    [self updateStatus:@"Configuring WiFi connection..." preview:nil];

    if (@available(iOS 11.0, *)) {
        NEHotspotConfiguration *configuration = nil;

        // Modern iOS configuration
        if (self.password.length > 0) {
            configuration = [[NEHotspotConfiguration alloc] initWithSSID:self.ssid passphrase:self.password isWEP:NO];
        } else {
            configuration = [[NEHotspotConfiguration alloc] initWithSSID:self.ssid];
        }

        // Modern configuration options
        configuration.joinOnce = YES;  // Join once for this session

        NSLog(@"🔥 Applying modern NEHotspotConfiguration for SSID: %@", self.ssid);
        __weak typeof(self) weakSelf = self;

        [[NEHotspotConfigurationManager sharedManager] applyConfiguration:configuration completionHandler:^(NSError * _Nullable error) {
            __strong typeof(weakSelf) strongSelf = weakSelf;
            if (!strongSelf) { return; }

            if (!error) {
                NSLog(@"🔥 Successfully applied hotspot configuration!");
                [strongSelf updateStatus:@"WiFi configured. Testing connection..." preview:nil];

                // Wait a moment for connection to establish
                dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(3.0 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
                    [strongSelf testConnectionAfterHotspotJoin];
                });
            } else if (error.code == NEHotspotConfigurationErrorAlreadyAssociated) {
                NSLog(@"🔥 Already associated with hotspot");
                [strongSelf updateStatus:@"Already connected. Testing connection..." preview:nil];
                dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(2.0 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
                    [strongSelf testConnectionAfterHotspotJoin];
                });
            } else {
                NSLog(@"🔥 Failed to configure hotspot: %@", error.localizedDescription);
                [strongSelf updateStatus:[NSString stringWithFormat:@"WiFi config failed: %@", error.localizedDescription] preview:nil];

                // Fallback to manual connection prompt
                dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(2.0 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
                    [strongSelf promptForManualConnection];
                });
            }
        }];
    } else {
        [self promptForManualConnection];
    }
}

- (void)testConnectionAfterHotspotJoin {
    NSLog(@"🔥 Testing connection after hotspot join");
    [self updateStatus:@"Testing network connection..." preview:nil];

    // Check if we're actually connected to the target WiFi
    if ([self isCurrentlyOnTargetWiFi]) {
        NSLog(@"🔥 Confirmed on target WiFi, proceeding with connection test");
        [self testConnectionWithQCSDKAndCommonIPs];
    } else {
        NSLog(@"🔥 Not on target WiFi, prompting for manual connection");
        [self promptForManualConnection];
    }
}

- (BOOL)isCurrentlyOnTargetWiFi {
    // Test connectivity to target network instead of checking SSID directly
    return [self testConnectivityToTargetNetwork];
}

- (BOOL)testConnectivityToTargetNetwork {
    // Test if we can reach the glasses device by trying common IPs
    NSArray *targetIPs = @[@"192.168.43.1", @"192.168.4.1", @"192.168.1.1", @"192.168.0.1"];

    __block BOOL canReachTarget = NO;
    dispatch_semaphore_t semaphore = dispatch_semaphore_create(0);

    for (NSString *ip in targetIPs) {
        NSURL *testURL = [NSURL URLWithString:[NSString stringWithFormat:@"http://%@/files/media.config", ip]];
        NSURLRequest *request = [NSURLRequest requestWithURL:testURL
                                                cachePolicy:NSURLRequestReloadIgnoringLocalCacheData
                                            timeoutInterval:1.0];

        [[self.session dataTaskWithRequest:request
                                         completionHandler:^(NSData *data, NSURLResponse *response, NSError *error) {
            if (!error && ((NSHTTPURLResponse *)response).statusCode == 200) {
                canReachTarget = YES;
            }
            dispatch_semaphore_signal(semaphore);
        }] resume];

        dispatch_semaphore_wait(semaphore, dispatch_time(DISPATCH_TIME_NOW, (int64_t)(1.5 * NSEC_PER_SEC)));
        if (canReachTarget) break;
    }

    return canReachTarget;
}

- (void)testConnectionWithQCSDKAndCommonIPs {
    // First try the QCSDK IP retrieval
    [QCSDKCmdCreator getDeviceWifiIPSuccess:^(NSString * _Nullable ipAddress) {
        if (ipAddress.length > 0) {
            NSLog(@"🔥 Got device IP: %@", ipAddress);
            [self testConnectionToIP:ipAddress];
        } else {
            NSLog(@"🔥 No IP from QCSDK, trying common IPs");
            [self testConnectionWithCommonIPs];
        }
    } failed:^{
        NSLog(@"🔥 QCSDK IP failed, trying common IPs");
        [self testConnectionWithCommonIPs];
    }];
}

- (void)promptForManualConnection {
    [self updateStatus:[NSString stringWithFormat:@"Please manually join WiFi: %@", self.ssid] preview:nil];

    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(15.0 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        [self testConnectionWithCommonIPs];
    });
}

- (void)joinHotspot {
    // Legacy method - redirect to modern config
    [self joinHotspotWithModernConfig];
}

- (void)downloadManifest {
    NSString *manifestString = [NSString stringWithFormat:@"http://%@/files/media.config", self->_deviceIP];
    NSURL *manifestURL = [NSURL URLWithString:manifestString];
    if (!manifestURL) {
        NSError *error = [NSError errorWithDomain:GlassesMediaDownloaderErrorDomain code:GlassesMediaDownloaderErrorCodeManifest userInfo:@{NSLocalizedDescriptionKey : @"Invalid manifest URL."}];
        [self finishWithError:error];
        return;
    }

    [self updateStatus:@"Fetching media manifest..." preview:nil];
    __weak typeof(self) weakSelf = self;
    NSURLSessionDataTask *task = [self.session dataTaskWithURL:manifestURL completionHandler:^(NSData * _Nullable data, NSURLResponse * _Nullable response, NSError * _Nullable error) {
        __strong typeof(weakSelf) strongSelf = weakSelf;
        if (!strongSelf) { return; }
        if (error) {
            NSString *description = [NSString stringWithFormat:@"Failed to download manifest: %@", error.localizedDescription ?: @"unknown error"];
            NSError *manifestError = [NSError errorWithDomain:GlassesMediaDownloaderErrorDomain code:GlassesMediaDownloaderErrorCodeManifest userInfo:@{NSLocalizedDescriptionKey : description}];
            [strongSelf finishWithError:manifestError];
            return;
        }

        NSHTTPURLResponse *httpResponse = (NSHTTPURLResponse *)response;
        if (httpResponse.statusCode < 200 || httpResponse.statusCode >= 300) {
            NSString *description = [NSString stringWithFormat:@"Manifest request returned HTTP %ld.", (long)httpResponse.statusCode];
            NSError *manifestError = [NSError errorWithDomain:GlassesMediaDownloaderErrorDomain code:GlassesMediaDownloaderErrorCodeManifest userInfo:@{NSLocalizedDescriptionKey : description}];
            [strongSelf finishWithError:manifestError];
            return;
        }

        NSString *configString = [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
        if (!configString) {
            configString = [[NSString alloc] initWithData:data encoding:NSISOLatin1StringEncoding];
        }
        if (configString.length == 0) {
            NSError *manifestError = [NSError errorWithDomain:GlassesMediaDownloaderErrorDomain code:GlassesMediaDownloaderErrorCodeManifest userInfo:@{NSLocalizedDescriptionKey : @"Media manifest is empty."}];
            [strongSelf finishWithError:manifestError];
            return;
        }

        NSArray<NSURL *> *fileURLs = [strongSelf fileURLsFromManifestString:configString baseURL:manifestURL];
        [strongSelf prepareDownloadDirectoryWithManifest:fileURLs baseURL:manifestURL];
    }];
    [task resume];
}

- (NSArray<NSURL *> *)fileURLsFromManifestString:(NSString *)manifest baseURL:(NSURL *)manifestURL {
    NSArray<NSString *> *lines = [manifest componentsSeparatedByCharactersInSet:NSCharacterSet.newlineCharacterSet];
    NSMutableArray<NSURL *> *fileURLs = [NSMutableArray array];
    NSURL *baseURL = [manifestURL URLByDeletingLastPathComponent];
    for (NSString *line in lines) {
        NSString *trimmed = [line stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceAndNewlineCharacterSet]];
        if (trimmed.length == 0) { continue; }
        if ([trimmed hasPrefix:@"#"]) { continue; }
        NSURL *resolvedURL = nil;
        if ([trimmed hasPrefix:@"http://"] || [trimmed hasPrefix:@"https://"]) {
            resolvedURL = [NSURL URLWithString:trimmed];
        } else {
            resolvedURL = [NSURL URLWithString:trimmed relativeToURL:baseURL];
        }
        if (resolvedURL) {
            [fileURLs addObject:resolvedURL.absoluteURL];
        }
    }
    return fileURLs.copy;
}

- (void)prepareDownloadDirectoryWithManifest:(NSArray<NSURL *> *)fileURLs baseURL:(NSURL *)manifestURL {
    (void)manifestURL;
    NSString *documentsDirectory = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES).firstObject;
    if (documentsDirectory.length == 0) {
        NSError *error = [NSError errorWithDomain:GlassesMediaDownloaderErrorDomain code:GlassesMediaDownloaderErrorCodeFilesystem userInfo:@{NSLocalizedDescriptionKey : @"Unable to locate the Documents directory."}];
        [self finishWithError:error];
        return;
    }

    NSString *mediaDirectoryPath = [documentsDirectory stringByAppendingPathComponent:@"GlassesMedia"];
    NSURL *mediaDirectoryURL = [NSURL fileURLWithPath:mediaDirectoryPath isDirectory:YES];

    NSError *directoryError = nil;
    if (![[NSFileManager defaultManager] createDirectoryAtURL:mediaDirectoryURL withIntermediateDirectories:YES attributes:nil error:&directoryError]) {
        NSError *error = [NSError errorWithDomain:GlassesMediaDownloaderErrorDomain code:GlassesMediaDownloaderErrorCodeFilesystem userInfo:@{NSLocalizedDescriptionKey : directoryError.localizedDescription ?: @"Unable to prepare download directory."}];
        [self finishWithError:error];
        return;
    }

    if (fileURLs.count == 0) {
        [self updateStatus:@"No media listed in manifest." preview:nil];
        [self finishWithError:nil];
        return;
    }

    [self downloadFiles:fileURLs toDirectory:mediaDirectoryURL atIndex:0 latestPreview:nil];
}

- (void)downloadFiles:(NSArray<NSURL *> *)fileURLs toDirectory:(NSURL *)directoryURL atIndex:(NSUInteger)index latestPreview:(UIImage * _Nullable)latestPreview {
    if (index >= fileURLs.count) {
        [self updateStatus:@"All media downloaded." preview:latestPreview];
        [self finishWithError:nil];
        return;
    }

    NSURL *fileURL = fileURLs[index];
    NSString *filename = fileURL.lastPathComponent.length > 0 ? fileURL.lastPathComponent : [NSString stringWithFormat:@"media_%lu", (unsigned long)index];
    NSURL *destinationURL = [directoryURL URLByAppendingPathComponent:filename];

    [self updateStatus:[NSString stringWithFormat:@"Downloading %lu/%lu: %@", (unsigned long)(index + 1), (unsigned long)fileURLs.count, filename] preview:latestPreview];

    __weak typeof(self) weakSelf = self;
    NSURLSessionDataTask *task = [self.session dataTaskWithURL:fileURL completionHandler:^(NSData * _Nullable data, NSURLResponse * _Nullable response, NSError * _Nullable error) {
        __strong typeof(weakSelf) strongSelf = weakSelf;
        if (!strongSelf) { return; }
        if (error) {
            NSString *description = [NSString stringWithFormat:@"Failed to download %@: %@", filename, error.localizedDescription ?: @"unknown error"];
            NSError *downloadError = [NSError errorWithDomain:GlassesMediaDownloaderErrorDomain code:GlassesMediaDownloaderErrorCodeDownload userInfo:@{NSLocalizedDescriptionKey : description}];
            [strongSelf finishWithError:downloadError];
            return;
        }

        NSHTTPURLResponse *httpResponse = (NSHTTPURLResponse *)response;
        if (httpResponse.statusCode < 200 || httpResponse.statusCode >= 300) {
            NSString *description = [NSString stringWithFormat:@"Failed to download %@: HTTP %ld", filename, (long)httpResponse.statusCode];
            NSError *downloadError = [NSError errorWithDomain:GlassesMediaDownloaderErrorDomain code:GlassesMediaDownloaderErrorCodeDownload userInfo:@{NSLocalizedDescriptionKey : description}];
            [strongSelf finishWithError:downloadError];
            return;
        }

        if (data.length == 0) {
            NSString *description = [NSString stringWithFormat:@"%@ is empty.", filename];
            NSError *downloadError = [NSError errorWithDomain:GlassesMediaDownloaderErrorDomain code:GlassesMediaDownloaderErrorCodeDownload userInfo:@{NSLocalizedDescriptionKey : description}];
            [strongSelf finishWithError:downloadError];
            return;
        }

        [[NSFileManager defaultManager] removeItemAtURL:destinationURL error:nil];
        NSError *writeError = nil;
        if (![data writeToURL:destinationURL options:NSDataWritingAtomic error:&writeError]) {
            NSString *description = [NSString stringWithFormat:@"Failed to save %@: %@", filename, writeError.localizedDescription ?: @"unknown error"];
            NSError *filesystemError = [NSError errorWithDomain:GlassesMediaDownloaderErrorDomain code:GlassesMediaDownloaderErrorCodeFilesystem userInfo:@{NSLocalizedDescriptionKey : description}];
            [strongSelf finishWithError:filesystemError];
            return;
        }

        UIImage *previewImage = [strongSelf previewImageForData:data];
        UIImage *nextPreview = previewImage ?: latestPreview;
        NSString *status = [NSString stringWithFormat:@"Saved %@", filename];
        [strongSelf updateStatus:status preview:nextPreview];
        [strongSelf downloadFiles:fileURLs toDirectory:directoryURL atIndex:index + 1 latestPreview:nextPreview];
    }];
    [task resume];
}

#pragma mark - Helpers

- (UIImage * _Nullable)previewImageForData:(NSData *)data {
    UIImage *image = [UIImage imageWithData:data scale:UIScreen.mainScreen.scale];
    return image;
}

- (void)updateStatus:(NSString *)status preview:(UIImage * _Nullable)preview {
    if (!self.statusHandler) { return; }
    dispatch_async(dispatch_get_main_queue(), ^{
        self.statusHandler(status, preview);
    });
}

- (void)attemptDelayedModeSwitchWithDelay:(NSTimeInterval)delay attempt:(NSInteger)attempt {
    NSLog(@"🔥 Attempting delayed mode switch to capture mode (attempt %zd, delay %.1fs)", attempt, delay);

    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(delay * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        [self trySwitchToCaptureModeWithAttempt:attempt];
    });
}

- (void)trySwitchToCaptureModeWithAttempt:(NSInteger)attempt {
    NSLog(@"🔥 Trying to switch to capture mode (attempt %zd)", attempt);

    [QCSDKCmdCreator setDeviceMode:QCOperatorDeviceModePhoto success:^{
        NSLog(@"🔥 ✅ Successfully returned to capture mode after download (attempt %zd)", attempt);
        [self updateStatus:@"Successfully returned to capture mode" preview:nil];
    } fail:^(NSInteger mode) {
        NSLog(@"🔥 ⚠️ Failed to return to capture mode, current mode: %zd (attempt %zd)", mode, attempt);

        // Try video mode as intermediate step with exponential backoff
        if (attempt < 5) { // Limit attempts to prevent infinite retry
            NSTimeInterval nextDelay = 2.0 * pow(2.0, attempt - 1); // 2s, 4s, 8s, 16s

            [self updateStatus:[NSString stringWithFormat:@"Device busy, retrying in %.1fs...", nextDelay] preview:nil];

            // First try video mode, then photo mode
            [QCSDKCmdCreator setDeviceMode:QCOperatorDeviceModeVideo success:^{
                NSLog(@"🔥 Switched to video mode, now trying capture mode (attempt %zd)", attempt);
                [QCSDKCmdCreator setDeviceMode:QCOperatorDeviceModePhoto success:^{
                    NSLog(@"🔥 ✅ Successfully returned to capture mode via video mode (attempt %zd)", attempt);
                    [self updateStatus:@"Successfully returned to capture mode" preview:nil];
                } fail:^(NSInteger photoMode) {
                    NSLog(@"🔥 ❌ Still failed to return to capture mode, current mode: %zd (attempt %zd)", photoMode, attempt);
                    // Schedule next attempt with exponential backoff
                    [self attemptDelayedModeSwitchWithDelay:nextDelay attempt:attempt + 1];
                }];
            } fail:^(NSInteger videoMode) {
                NSLog(@"🔥 ❌ Failed to switch to video mode, current mode: %zd (attempt %zd)", videoMode, attempt);
                // Schedule next attempt with exponential backoff
                [self attemptDelayedModeSwitchWithDelay:nextDelay attempt:attempt + 1];
            }];
        } else {
            NSLog(@"🔥 ❌ Max attempts reached, device remains in transfer mode");
            [self updateStatus:@"Device remains in transfer mode. Please restart if needed." preview:nil];
        }
    }];
}

- (void)finishWithError:(NSError * _Nullable)error {
    if (self.didFinish) { return; }
    self.didFinish = YES;

    // Properly close WiFi connection and return device to capture mode when download completes
    if (!error) {
        NSLog(@"🔥 Download completed successfully, closing WiFi connection and returning to capture mode");
        [self updateStatus:@"Download complete! Returning to capture mode..." preview:nil];

        // Device needs time to transition between WiFi (transfer) and Bluetooth (capture) modes
        // Implement delayed mode switching with exponential backoff
        [self attemptDelayedModeSwitchWithDelay:2.0 attempt:1];
    }

    GlassesMediaDownloaderCompletionHandler completion = self.completionHandler;
    if (completion) {
        dispatch_async(dispatch_get_main_queue(), ^{
            completion(error);
        });
    }
    self.completionHandler = nil;
}

- (BOOL)isUsingWiFiConnection {
    // Check if we have a valid WiFi connection to the device
    struct ifaddrs *interfaces;
    if (!getifaddrs(&interfaces)) {
        struct ifaddrs *interface;
        for (interface = interfaces; interface; interface = interface->ifa_next) {
            if (interface->ifa_addr->sa_family == AF_INET) {
                NSString *interfaceName = [NSString stringWithUTF8String:interface->ifa_name];
                // Check for common WiFi interface names
                if ([interfaceName hasPrefix:@"en"] || [interfaceName hasPrefix:@"bridge"]) {
                    // Get the IP address
                    struct sockaddr_in *addr = (struct sockaddr_in *)interface->ifa_addr;
                    char addr_buf[INET_ADDRSTRLEN];
                    inet_ntop(AF_INET, &(addr->sin_addr), addr_buf, INET_ADDRSTRLEN);
                    NSString *ip = [NSString stringWithUTF8String:addr_buf];

                    NSLog(@"🔥 Network interface: %@ - IP: %@", interfaceName, ip);

                    // Check if this IP is in the same subnet as our device
                    if ([self isIP:ip inSameSubnetAs:self.deviceIP]) {
                        freeifaddrs(interfaces);
                        return YES;
                    }
                }
            }
        }
        freeifaddrs(interfaces);
    }
    return NO;
}

- (BOOL)isIP:(NSString *)ip1 inSameSubnetAs:(NSString *)ip2 {
    // Simple subnet check for common private ranges
    NSArray *ip1Components = [ip1 componentsSeparatedByString:@"."];
    NSArray *ip2Components = [ip2 componentsSeparatedByString:@"."];

    if (ip1Components.count >= 3 && ip2Components.count >= 3) {
        // Check first three octets for /24 subnet
        return [ip1Components[0] isEqualToString:ip2Components[0]] &&
               [ip1Components[1] isEqualToString:ip2Components[1]] &&
               [ip1Components[2] isEqualToString:ip2Components[2]];
    }
    return NO;
}

- (void)verifyWiFiConnectionAndProceed {
    NSLog(@"🔥 Verifying WiFi connection is stable before proceeding");
    [self updateStatus:@"Verifying WiFi connection..." preview:nil];

    // Check if we're actually using WiFi
    if ([self isUsingWiFiConnection]) {
        NSLog(@"🔥 WiFi interface detected, proceeding with connectivity test");
        [self testConnectivityWithRetries:3 attempt:1];
    } else {
        NSLog(@"🔥 WARNING: No WiFi interface detected, forcing connection anyway");
        [self testConnectivityWithRetries:3 attempt:1];
    }
}

@end
