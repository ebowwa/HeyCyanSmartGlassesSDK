//
//  WiFiMediaBrowser.m
//  QCSDKDemo
//
//  WiFi-based media browser for accessing glasses media files
//

#import "WiFiMediaBrowser.h"
#import <WebKit/WebKit.h>
#import <SystemConfiguration/CaptiveNetwork.h>
#import "NetworkExplorer.h"

@interface WiFiMediaBrowser () <WKNavigationDelegate, NSURLSessionDelegate, UITableViewDelegate, UITableViewDataSource, NetworkExplorerDelegate>

@property (nonatomic, strong) WKWebView *webView;
@property (nonatomic, strong) UITableView *fileListTable;
@property (nonatomic, strong) NSMutableArray *mediaFiles;
@property (nonatomic, strong) UISegmentedControl *viewModeControl;
@property (nonatomic, strong) NSURLSession *downloadSession;
@property (nonatomic, strong) NetworkExplorer *networkExplorer;
@property (nonatomic, strong) NSMutableArray *discoveredEndpoints;

@end

@implementation WiFiMediaBrowser

- (void)viewDidLoad {
    [super viewDidLoad];
    
    self.title = @"Media Browser";
    self.view.backgroundColor = [UIColor systemBackgroundColor];
    
    self.mediaFiles = [NSMutableArray array];
    self.discoveredEndpoints = [NSMutableArray array];
    
    // Initialize network explorer
    self.networkExplorer = [[NetworkExplorer alloc] init];
    self.networkExplorer.delegate = self;
    
    // Create navigation items
    UIBarButtonItem *refreshButton = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemRefresh 
                                                                                    target:self 
                                                                                    action:@selector(refreshMediaList)];
    self.navigationItem.rightBarButtonItem = refreshButton;
    
    // Create view mode control
    self.viewModeControl = [[UISegmentedControl alloc] initWithItems:@[@"List", @"Web View"]];
    self.viewModeControl.selectedSegmentIndex = 0;
    [self.viewModeControl addTarget:self action:@selector(viewModeChanged:) forControlEvents:UIControlEventValueChanged];
    self.navigationItem.titleView = self.viewModeControl;
    
    // Setup table view for file list
    self.fileListTable = [[UITableView alloc] initWithFrame:self.view.bounds style:UITableViewStylePlain];
    self.fileListTable.delegate = self;
    self.fileListTable.dataSource = self;
    [self.view addSubview:self.fileListTable];
    
    // Setup web view (hidden initially)
    self.webView = [[WKWebView alloc] initWithFrame:self.view.bounds];
    self.webView.navigationDelegate = self;
    self.webView.hidden = YES;
    [self.view addSubview:self.webView];
    
    // Setup download session
    NSURLSessionConfiguration *config = [NSURLSessionConfiguration defaultSessionConfiguration];
    self.downloadSession = [NSURLSession sessionWithConfiguration:config delegate:self delegateQueue:nil];
    
    // Just connect directly - no annoying dialogs
    if (self.deviceIPAddress) {
        [self connectToDevice];
    } else {
        NSLog(@"No device IP address available");
    }
}

- (void)viewWillLayoutSubviews {
    [super viewWillLayoutSubviews];
    self.fileListTable.frame = self.view.bounds;
    self.webView.frame = self.view.bounds;
}

- (void)showConnectionInfo {
    // IP address will be fixed in connectToDevice if needed
    
    NSString *message = [NSString stringWithFormat:@"📶 WiFi Network: %@\n🔑 Password: %@\n📍 Device IP: %@\n\n⚠️ IMPORTANT:\n1. Go to iPhone Settings > Wi-Fi\n2. Connect to network: %@\n3. Enter password: %@\n4. Return here and tap OK\n\nNote: You will temporarily lose internet access while connected to the glasses.",
                         self.wifiSSID ?: @"Not available",
                         self.wifiPassword ?: @"Not available", 
                         self.deviceIPAddress ?: @"Not available",
                         self.wifiSSID ?: @"",
                         self.wifiPassword ?: @""];
    
    UIAlertController *alert = [UIAlertController alertControllerWithTitle:@"Connect to Glasses WiFi"
                                                                   message:message
                                                            preferredStyle:UIAlertControllerStyleAlert];
    
    [alert addAction:[UIAlertAction actionWithTitle:@"Open WiFi Settings" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
        // Open WiFi settings
        NSURL *url = [NSURL URLWithString:UIApplicationOpenSettingsURLString];
        if ([[UIApplication sharedApplication] canOpenURL:url]) {
            [[UIApplication sharedApplication] openURL:url options:@{} completionHandler:nil];
        }
        
        // Wait a bit then try to connect
        dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(2.0 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
            [self connectToDevice];
        });
    }]];
    
    [alert addAction:[UIAlertAction actionWithTitle:@"I'm Connected" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
        [self connectToDevice];
    }]];
    
    // Only present if we have a valid view controller hierarchy
    if (self.navigationController || self.presentingViewController) {
        [self presentViewController:alert animated:YES completion:nil];
    } else {
        // Delay presentation until view appears
        dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0.5 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
            if (self.navigationController || self.presentingViewController) {
                [self presentViewController:alert animated:YES completion:nil];
            }
        });
    }
}

- (void)viewModeChanged:(UISegmentedControl *)sender {
    if (sender.selectedSegmentIndex == 0) {
        // List mode
        self.fileListTable.hidden = NO;
        self.webView.hidden = YES;
    } else {
        // Web view mode
        self.fileListTable.hidden = YES;
        self.webView.hidden = NO;
        [self loadWebInterface];
    }
}

- (void)connectToDevice {
    if (!self.deviceIPAddress) {
        [self showError:@"No device IP address available"];
        return;
    }
    
    // Fix the IP address format - "3.192.168.31" should be "192.168.3.31"
    NSString *fixedIP = self.deviceIPAddress;
    if ([fixedIP hasPrefix:@"3."]) {
        NSArray *parts = [fixedIP componentsSeparatedByString:@"."];
        if (parts.count == 4) {
            // Rearrange: 3.192.168.31 -> 192.168.3.31
            fixedIP = [NSString stringWithFormat:@"%@.%@.%@.%@", parts[1], parts[2], parts[0], parts[3]];
            self.deviceIPAddress = fixedIP;
            NSLog(@"Fixed IP address format: %@ -> %@", parts, fixedIP);
        }
    }
    
    NSLog(@"Attempting to connect to device at IP: %@", self.deviceIPAddress);
    
    // Start comprehensive network exploration
    NSLog(@"🚀 Starting network exploration to discover glasses protocols...");
    [self.networkExplorer startExplorationWithIP:self.deviceIPAddress];
    
    // Check current WiFi connection
    [self checkWiFiConnection];
    
    // Try to load the device's web interface
    [self loadWebInterface];
    
    // Also try to fetch media list via HTTP
    [self fetchMediaList];
}

- (void)checkWiFiConnection {
    // Get current SSID to verify we're connected to the right network
    NSArray *interfaces = CFBridgingRelease(CNCopySupportedInterfaces());
    NSLog(@"Network interfaces: %@", interfaces);
    
    for (NSString *interface in interfaces) {
        NSDictionary *info = CFBridgingRelease(CNCopyCurrentNetworkInfo((__bridge CFStringRef)interface));
        if (info) {
            NSString *ssid = info[@"SSID"];
            NSLog(@"Currently connected to WiFi: %@", ssid);
            
            if (![ssid isEqualToString:self.wifiSSID]) {
                dispatch_async(dispatch_get_main_queue(), ^{
                    [self showError:[NSString stringWithFormat:@"Not connected to glasses WiFi.\nExpected: %@\nCurrent: %@", self.wifiSSID, ssid]];
                });
            }
        }
    }
}

- (void)loadWebInterface {
    if (!self.deviceIPAddress) return;
    
    // Try common URLs for media servers
    NSArray *possibleURLs = @[
        [NSString stringWithFormat:@"http://%@", self.deviceIPAddress],
        [NSString stringWithFormat:@"http://%@:8080", self.deviceIPAddress],
        [NSString stringWithFormat:@"http://%@:80", self.deviceIPAddress],
        [NSString stringWithFormat:@"http://%@/index.html", self.deviceIPAddress],
        [NSString stringWithFormat:@"http://%@/media", self.deviceIPAddress]
    ];
    
    // Try the first URL
    NSURL *url = [NSURL URLWithString:possibleURLs[0]];
    NSURLRequest *request = [NSURLRequest requestWithURL:url];
    [self.webView loadRequest:request];
}

- (void)fetchMediaList {
    if (!self.deviceIPAddress) return;
    
    // Wait a bit for WiFi to stabilize, then try direct connection
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(2.0 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        // Try simple direct connection first
        [self tryDirectConnection];
    });
}

- (void)tryDirectConnection {
    NSLog(@"Trying direct socket connection to %@", self.deviceIPAddress);
    
    // Create a simple URL session without proxy
    NSURLSessionConfiguration *config = [NSURLSessionConfiguration ephemeralSessionConfiguration];
    config.connectionProxyDictionary = @{}; // No proxy
    config.allowsCellularAccess = NO; // WiFi only
    config.timeoutIntervalForRequest = 10.0;
    config.timeoutIntervalForResource = 30.0;
    
    NSURLSession *session = [NSURLSession sessionWithConfiguration:config];
    
    // Try simplest endpoint first
    NSString *urlString = [NSString stringWithFormat:@"http://%@/", self.deviceIPAddress];
    NSURL *url = [NSURL URLWithString:urlString];
    
    NSURLSessionDataTask *task = [session dataTaskWithURL:url completionHandler:^(NSData * _Nullable data, NSURLResponse * _Nullable response, NSError * _Nullable error) {
        if (error) {
            NSLog(@"Direct connection error: %@", error);
            // Try fallback endpoints
            dispatch_async(dispatch_get_main_queue(), ^{
                [self tryFallbackEndpoints];
            });
        } else {
            NSHTTPURLResponse *httpResponse = (NSHTTPURLResponse *)response;
            NSLog(@"Direct connection success! Status: %ld", (long)httpResponse.statusCode);
            
            if (data.length > 0) {
                NSString *responseString = [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
                NSLog(@"Response: %@", responseString);
                
                // Parse response or try media endpoints
                dispatch_async(dispatch_get_main_queue(), ^{
                    [self parseMediaList:data fromURL:urlString];
                });
            }
        }
    }];
    
    [task resume];
}

- (void)tryFallbackEndpoints {
    NSArray *endpoints = @[
        [NSString stringWithFormat:@"http://%@/media/list", self.deviceIPAddress],
        [NSString stringWithFormat:@"http://%@/files", self.deviceIPAddress],
        [NSString stringWithFormat:@"http://%@/api/media", self.deviceIPAddress],
        [NSString stringWithFormat:@"http://%@/DCIM", self.deviceIPAddress]
    ];
    
    [self tryEndpoint:endpoints index:0];
}

- (void)tryEndpoint:(NSArray *)endpoints index:(NSInteger)index {
    if (index >= endpoints.count) {
        NSLog(@"Could not find media endpoint");
        return;
    }
    
    NSString *urlString = endpoints[index];
    NSURL *url = [NSURL URLWithString:urlString];
    
    NSURLSessionDataTask *task = [self.downloadSession dataTaskWithURL:url completionHandler:^(NSData * _Nullable data, NSURLResponse * _Nullable response, NSError * _Nullable error) {
        if (error) {
            NSLog(@"Error fetching from %@: %@", urlString, error);
            // Try next endpoint
            dispatch_async(dispatch_get_main_queue(), ^{
                [self tryEndpoint:endpoints index:index + 1];
            });
            return;
        }
        
        NSHTTPURLResponse *httpResponse = (NSHTTPURLResponse *)response;
        if (httpResponse.statusCode == 200) {
            NSLog(@"Found media endpoint at: %@", urlString);
            [self parseMediaList:data fromURL:urlString];
        } else {
            // Try next endpoint
            dispatch_async(dispatch_get_main_queue(), ^{
                [self tryEndpoint:endpoints index:index + 1];
            });
        }
    }];
    
    [task resume];
}

- (void)parseMediaList:(NSData *)data fromURL:(NSString *)baseURL {
    // Try to parse as JSON first
    NSError *jsonError;
    id json = [NSJSONSerialization JSONObjectWithData:data options:0 error:&jsonError];
    
    if (!jsonError && json) {
        // Parse JSON response
        if ([json isKindOfClass:[NSArray class]]) {
            [self.mediaFiles removeAllObjects];
            for (NSDictionary *file in json) {
                [self.mediaFiles addObject:file];
            }
        } else if ([json isKindOfClass:[NSDictionary class]]) {
            NSDictionary *dict = json;
            if (dict[@"files"]) {
                [self.mediaFiles removeAllObjects];
                [self.mediaFiles addObjectsFromArray:dict[@"files"]];
            }
        }
    } else {
        // Try to parse as HTML directory listing
        NSString *html = [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
        [self parseHTMLDirectoryListing:html fromURL:baseURL];
    }
    
    dispatch_async(dispatch_get_main_queue(), ^{
        [self.fileListTable reloadData];
    });
}

- (void)parseHTMLDirectoryListing:(NSString *)html fromURL:(NSString *)baseURL {
    // Simple HTML parsing for directory listings
    NSRegularExpression *regex = [NSRegularExpression regularExpressionWithPattern:@"href=\"([^\"]+\\.(jpg|jpeg|png|mp4|mov|mp3|wav)[^\"]*)\"" 
                                                                            options:NSRegularExpressionCaseInsensitive 
                                                                              error:nil];
    
    NSArray *matches = [regex matchesInString:html options:0 range:NSMakeRange(0, html.length)];
    
    [self.mediaFiles removeAllObjects];
    for (NSTextCheckingResult *match in matches) {
        NSString *filename = [html substringWithRange:[match rangeAtIndex:1]];
        NSDictionary *fileInfo = @{
            @"name": filename,
            @"url": [NSString stringWithFormat:@"%@/%@", baseURL, filename]
        };
        [self.mediaFiles addObject:fileInfo];
    }
}

- (void)refreshMediaList {
    [self fetchMediaList];
}

- (void)downloadFile:(NSDictionary *)fileInfo {
    NSString *urlString = fileInfo[@"url"];
    if (!urlString) {
        urlString = [NSString stringWithFormat:@"http://%@/%@", self.deviceIPAddress, fileInfo[@"name"]];
    }
    
    NSURL *url = [NSURL URLWithString:urlString];
    NSURLSessionDownloadTask *downloadTask = [self.downloadSession downloadTaskWithURL:url completionHandler:^(NSURL * _Nullable location, NSURLResponse * _Nullable response, NSError * _Nullable error) {
        if (error) {
            dispatch_async(dispatch_get_main_queue(), ^{
                [self showError:[NSString stringWithFormat:@"Download failed: %@", error.localizedDescription]];
            });
            return;
        }
        
        // Save to Photos
        NSString *filename = fileInfo[@"name"];
        [self saveFileToPhotos:location filename:filename];
    }];
    
    [downloadTask resume];
    
    UIAlertController *alert = [UIAlertController alertControllerWithTitle:@"Downloading"
                                                                   message:[NSString stringWithFormat:@"Downloading %@...", fileInfo[@"name"]]
                                                            preferredStyle:UIAlertControllerStyleAlert];
    [self presentViewController:alert animated:YES completion:^{
        dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(1.5 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
            [alert dismissViewControllerAnimated:YES completion:nil];
        });
    }];
}

- (void)saveFileToPhotos:(NSURL *)fileURL filename:(NSString *)filename {
    NSString *extension = [filename pathExtension].lowercaseString;
    
    if ([extension isEqualToString:@"jpg"] || [extension isEqualToString:@"jpeg"] || 
        [extension isEqualToString:@"png"] || [extension isEqualToString:@"gif"]) {
        // Save image
        UIImage *image = [UIImage imageWithContentsOfFile:fileURL.path];
        if (image) {
            UIImageWriteToSavedPhotosAlbum(image, self, @selector(image:didFinishSavingWithError:contextInfo:), nil);
        }
    } else if ([extension isEqualToString:@"mp4"] || [extension isEqualToString:@"mov"]) {
        // Save video
        if (UIVideoAtPathIsCompatibleWithSavedPhotosAlbum(fileURL.path)) {
            UISaveVideoAtPathToSavedPhotosAlbum(fileURL.path, self, @selector(video:didFinishSavingWithError:contextInfo:), nil);
        }
    }
}

- (void)image:(UIImage *)image didFinishSavingWithError:(NSError *)error contextInfo:(void *)contextInfo {
    dispatch_async(dispatch_get_main_queue(), ^{
        if (error) {
            [self showError:@"Failed to save image to Photos"];
        } else {
            [self showSuccess:@"Image saved to Photos"];
        }
    });
}

- (void)video:(NSString *)videoPath didFinishSavingWithError:(NSError *)error contextInfo:(void *)contextInfo {
    dispatch_async(dispatch_get_main_queue(), ^{
        if (error) {
            [self showError:@"Failed to save video to Photos"];
        } else {
            [self showSuccess:@"Video saved to Photos"];
        }
    });
}

- (void)showError:(NSString *)message {
    UIAlertController *alert = [UIAlertController alertControllerWithTitle:@"Error"
                                                                   message:message
                                                            preferredStyle:UIAlertControllerStyleAlert];
    [alert addAction:[UIAlertAction actionWithTitle:@"OK" style:UIAlertActionStyleDefault handler:nil]];
    [self presentViewController:alert animated:YES completion:nil];
}

- (void)showSuccess:(NSString *)message {
    UIAlertController *alert = [UIAlertController alertControllerWithTitle:@"Success"
                                                                   message:message
                                                            preferredStyle:UIAlertControllerStyleAlert];
    [alert addAction:[UIAlertAction actionWithTitle:@"OK" style:UIAlertActionStyleDefault handler:nil]];
    [self presentViewController:alert animated:YES completion:nil];
}

#pragma mark - UITableView DataSource & Delegate

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return self.mediaFiles.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    static NSString *cellID = @"MediaCell";
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:cellID];
    if (!cell) {
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:cellID];
    }
    
    NSDictionary *fileInfo = self.mediaFiles[indexPath.row];
    cell.textLabel.text = fileInfo[@"name"];
    cell.detailTextLabel.text = fileInfo[@"size"] ? [NSString stringWithFormat:@"Size: %@", fileInfo[@"size"]] : @"Tap to download";
    cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
    
    return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
    
    NSDictionary *fileInfo = self.mediaFiles[indexPath.row];
    
    UIAlertController *alert = [UIAlertController alertControllerWithTitle:fileInfo[@"name"]
                                                                   message:@"What would you like to do?"
                                                            preferredStyle:UIAlertControllerStyleActionSheet];
    
    [alert addAction:[UIAlertAction actionWithTitle:@"Download" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
        [self downloadFile:fileInfo];
    }]];
    
    [alert addAction:[UIAlertAction actionWithTitle:@"Cancel" style:UIAlertActionStyleCancel handler:nil]];
    
    [self presentViewController:alert animated:YES completion:nil];
}

#pragma mark - WKNavigationDelegate

- (void)webView:(WKWebView *)webView didFinishNavigation:(WKNavigation *)navigation {
    NSLog(@"Web page loaded successfully");
}

- (void)webView:(WKWebView *)webView didFailProvisionalNavigation:(WKNavigation *)navigation withError:(NSError *)error {
    NSLog(@"Failed to load web page: %@", error);
    
    // Try next URL
    static NSInteger urlIndex = 0;
    urlIndex++;
    
    NSArray *possibleURLs = @[
        [NSString stringWithFormat:@"http://%@", self.deviceIPAddress],
        [NSString stringWithFormat:@"http://%@:8080", self.deviceIPAddress],
        [NSString stringWithFormat:@"http://%@:80", self.deviceIPAddress],
        [NSString stringWithFormat:@"http://%@/index.html", self.deviceIPAddress],
        [NSString stringWithFormat:@"http://%@/media", self.deviceIPAddress]
    ];
    
    if (urlIndex < possibleURLs.count) {
        NSURL *url = [NSURL URLWithString:possibleURLs[urlIndex]];
        NSURLRequest *request = [NSURLRequest requestWithURL:url];
        [self.webView loadRequest:request];
    }
}

#pragma mark - NetworkExplorerDelegate

- (void)networkExplorerDidFindService:(NSString *)service atPort:(NSInteger)port {
    NSLog(@"🔍 DISCOVERED SERVICE: %@ on port %ld", service, (long)port);
    
    // Add to discovered endpoints
    NSString *endpoint = [NSString stringWithFormat:@"http://%@:%ld", self.deviceIPAddress, (long)port];
    [self.discoveredEndpoints addObject:@{
        @"service": service,
        @"port": @(port),
        @"url": endpoint
    }];
    
    // If we find an HTTP service, try to browse it
    if (port == 80 || port == 8080 || port == 8081 || port == 8082) {
        dispatch_async(dispatch_get_main_queue(), ^{
            [self tryEndpoint:@[endpoint] index:0];
        });
    }
}

- (void)networkExplorerDidFindEndpoint:(NSString *)endpoint withResponse:(NSString *)response {
    NSLog(@"🔍 DISCOVERED ENDPOINT: %@", endpoint);
    NSLog(@"📥 Response preview: %@", [response substringToIndex:MIN(200, response.length)]);
    
    // Check if response contains media-related content
    if ([response containsString:@"photo"] || [response containsString:@"video"] ||
        [response containsString:@"media"] || [response containsString:@"file"] ||
        [response containsString:@"DCIM"] || [response containsString:@"jpg"] ||
        [response containsString:@"mp4"] || [response containsString:@"json"]) {
        
        NSLog(@"✅ POTENTIAL MEDIA ENDPOINT FOUND: %@", endpoint);
        
        dispatch_async(dispatch_get_main_queue(), ^{
            // Try to fetch media from this endpoint
            [self tryEndpoint:@[endpoint] index:0];
        });
    }
}

- (void)networkExplorerDidReceiveBroadcast:(NSData *)data fromAddress:(NSString *)address {
    NSString *broadcast = [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
    if (broadcast) {
        NSLog(@"📡 BROADCAST from %@: %@", address, broadcast);
        
        // Look for service announcements
        if ([broadcast containsString:@"SERVER"] || [broadcast containsString:@"LOCATION"]) {
            NSLog(@"✅ DISCOVERED SERVICE ANNOUNCEMENT");
        }
    }
}

- (void)dealloc {
    [self.networkExplorer stopExploration];
}

@end