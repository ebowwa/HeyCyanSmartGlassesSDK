//
//  NetworkExplorer.m
//  QCSDKDemo
//
//  Experimental network discovery to find hidden glasses protocols
//

#import "NetworkExplorer.h"
#import <sys/socket.h>
#import <netinet/in.h>
#import <arpa/inet.h>
#import <unistd.h>

@interface NetworkExplorer () <NSStreamDelegate>

@property (nonatomic, strong) NSMutableArray *activeTasks;
@property (nonatomic, strong) NSOperationQueue *scanQueue;
@property (nonatomic, assign) int udpSocket;
@property (nonatomic, strong) dispatch_source_t udpSource;

@end

@implementation NetworkExplorer

- (instancetype)init {
    self = [super init];
    if (self) {
        _activeTasks = [NSMutableArray array];
        _scanQueue = [[NSOperationQueue alloc] init];
        _scanQueue.maxConcurrentOperationCount = 10;
        _udpSocket = -1;
    }
    return self;
}

- (void)dealloc {
    [self stopExploration];
}

- (void)startExplorationWithIP:(NSString *)ipAddress {
    self.targetIP = ipAddress;
    
    NSLog(@"🔍 Starting comprehensive network exploration for IP: %@", ipAddress);
    
    // Run all exploration methods in parallel
    [self scanCommonMediaPorts];
    [self probeHTTPEndpoints];
    [self startUDPListener];
    [self probeRawProtocols];
}

- (void)scanCommonMediaPorts {
    NSLog(@"🔍 Scanning common media ports...");
    
    // Common ports for media services
    NSArray *portsToScan = @[
        @80,    // HTTP
        @443,   // HTTPS
        @554,   // RTSP
        @1935,  // RTMP
        @8080,  // HTTP Alt
        @8081,  // HTTP Alt
        @8082,  // HTTP Alt
        @8088,  // HTTP Alt
        @8090,  // HTTP Alt
        @8888,  // HTTP Alt
        @9000,  // Custom
        @5000,  // UPnP
        @1900,  // SSDP
        @7000,  // AFS
        @8008,  // HTTP Alt
        @8554,  // RTSP Alt
        @5353,  // mDNS
        @6666,  // Custom
        @9999,  // Custom
        @10000, // Webmin
        @32400, // Plex
        @8096,  // Emby
        @8200,  // DLNA
        @2869,  // UPnP
        @49152, // UPnP
        @49153, // UPnP
        @15000, // Custom media
        @20000, // Custom media
        @30000, // Custom media
        @40000, // Custom media
        @50000, // Custom media
        @60000  // Custom media
    ];
    
    for (NSNumber *portNum in portsToScan) {
        [self.scanQueue addOperationWithBlock:^{
            [self checkPort:portNum.intValue];
        }];
    }
}

- (void)checkPort:(int)port {
    int sock = socket(AF_INET, SOCK_STREAM, 0);
    if (sock < 0) return;
    
    // Set timeout
    struct timeval timeout;
    timeout.tv_sec = 1;
    timeout.tv_usec = 0;
    setsockopt(sock, SOL_SOCKET, SO_RCVTIMEO, &timeout, sizeof(timeout));
    setsockopt(sock, SOL_SOCKET, SO_SNDTIMEO, &timeout, sizeof(timeout));
    
    struct sockaddr_in addr;
    memset(&addr, 0, sizeof(addr));
    addr.sin_family = AF_INET;
    addr.sin_port = htons(port);
    inet_pton(AF_INET, [self.targetIP UTF8String], &addr.sin_addr);
    
    if (connect(sock, (struct sockaddr *)&addr, sizeof(addr)) == 0) {
        NSLog(@"✅ Port %d is OPEN!", port);
        
        dispatch_async(dispatch_get_main_queue(), ^{
            if ([self.delegate respondsToSelector:@selector(networkExplorerDidFindService:atPort:)]) {
                [self.delegate networkExplorerDidFindService:[self guessServiceForPort:port] atPort:port];
            }
        });
        
        // Try to grab banner
        [self grabBanner:sock port:port];
    }
    
    close(sock);
}

- (NSString *)guessServiceForPort:(int)port {
    NSDictionary *knownPorts = @{
        @80: @"HTTP",
        @443: @"HTTPS",
        @554: @"RTSP",
        @1935: @"RTMP",
        @8080: @"HTTP-Alt",
        @5353: @"mDNS",
        @1900: @"SSDP/UPnP",
        @8554: @"RTSP-Alt",
        @5000: @"UPnP",
        @8200: @"DLNA"
    };
    
    return knownPorts[@(port)] ?: @"Unknown";
}

- (void)grabBanner:(int)sock port:(int)port {
    // Send HTTP request to see what we get back
    NSString *request = @"GET / HTTP/1.1\r\nHost: glasses\r\nConnection: close\r\n\r\n";
    send(sock, [request UTF8String], [request length], 0);
    
    char buffer[1024];
    memset(buffer, 0, sizeof(buffer));
    ssize_t bytesRead = recv(sock, buffer, sizeof(buffer) - 1, 0);
    
    if (bytesRead > 0) {
        NSString *response = [NSString stringWithUTF8String:buffer];
        NSLog(@"📥 Banner from port %d:\n%@", port, response);
        
        dispatch_async(dispatch_get_main_queue(), ^{
            if ([self.delegate respondsToSelector:@selector(networkExplorerDidFindEndpoint:withResponse:)]) {
                [self.delegate networkExplorerDidFindEndpoint:[NSString stringWithFormat:@"Port %d", port] 
                                                 withResponse:response ?: @"Binary data"];
            }
        });
    }
}

- (void)probeHTTPEndpoints {
    NSLog(@"🔍 Probing HTTP endpoints...");
    
    // Common endpoints to try
    NSArray *endpoints = @[
        @"/",
        @"/index.html",
        @"/api",
        @"/api/v1",
        @"/api/media",
        @"/api/files",
        @"/api/photos",
        @"/api/videos",
        @"/api/list",
        @"/api/device",
        @"/api/status",
        @"/media",
        @"/files",
        @"/photos",
        @"/videos",
        @"/gallery",
        @"/list",
        @"/download",
        @"/upload",
        @"/stream",
        @"/content",
        @"/data",
        @"/storage",
        @"/dcim",
        @"/DCIM",
        @"/camera",
        @"/recordings",
        @"/captures",
        @"/device",
        @"/status",
        @"/info",
        @"/version",
        @"/config",
        @"/settings",
        @"/system",
        @"/admin",
        @"/webapi",
        @"/rest",
        @"/v1",
        @"/v2",
        @"/api/v2",
        @"/cgi-bin",
        @"/cgi",
        @"/json",
        @"/xml",
        @"/rpc",
        @"/ws",
        @"/websocket",
        @"/sse",
        @"/mjpeg",
        @"/hls",
        @"/dash",
        @"/rtsp",
        @"/live",
        @"/preview",
        @"/.well-known",
        @"/robots.txt",
        @"/favicon.ico"
    ];
    
    // Try different ports
    NSArray *ports = @[@80, @8080, @8081, @8082, @8088, @8090, @8888, @9000];
    
    for (NSNumber *port in ports) {
        for (NSString *endpoint in endpoints) {
            [self.activeTasks addObject:[self probeEndpoint:endpoint onPort:port.intValue]];
        }
    }
}

- (NSURLSessionTask *)probeEndpoint:(NSString *)endpoint onPort:(int)port {
    NSString *urlString = [NSString stringWithFormat:@"http://%@:%d%@", self.targetIP, port, endpoint];
    NSURL *url = [NSURL URLWithString:urlString];
    
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:url];
    request.timeoutInterval = 2.0;
    request.HTTPMethod = @"GET";
    [request setValue:@"QCSDKDemo/1.0" forHTTPHeaderField:@"User-Agent"];
    
    NSURLSessionTask *task = [[NSURLSession sharedSession] dataTaskWithRequest:request 
        completionHandler:^(NSData *data, NSURLResponse *response, NSError *error) {
            
            NSHTTPURLResponse *httpResponse = (NSHTTPURLResponse *)response;
            
            if (!error && httpResponse.statusCode < 500) {
                NSLog(@"✅ Found endpoint: %@ (Status: %ld)", urlString, (long)httpResponse.statusCode);
                
                NSString *responseStr = [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
                if (responseStr) {
                    NSLog(@"📥 Response: %@", [responseStr substringToIndex:MIN(200, responseStr.length)]);
                }
                
                dispatch_async(dispatch_get_main_queue(), ^{
                    if ([self.delegate respondsToSelector:@selector(networkExplorerDidFindEndpoint:withResponse:)]) {
                        [self.delegate networkExplorerDidFindEndpoint:urlString 
                                                         withResponse:responseStr ?: @"Binary response"];
                    }
                });
            }
        }];
    
    [task resume];
    return task;
}

- (void)startUDPListener {
    NSLog(@"🔍 Starting UDP listener...");
    
    // Create UDP socket
    self.udpSocket = socket(AF_INET, SOCK_DGRAM, 0);
    if (self.udpSocket < 0) {
        NSLog(@"Failed to create UDP socket");
        return;
    }
    
    // Enable broadcast
    int broadcastEnable = 1;
    setsockopt(self.udpSocket, SOL_SOCKET, SO_BROADCAST, &broadcastEnable, sizeof(broadcastEnable));
    
    // Bind to any address
    struct sockaddr_in addr;
    memset(&addr, 0, sizeof(addr));
    addr.sin_family = AF_INET;
    addr.sin_port = 0; // Any port
    addr.sin_addr.s_addr = INADDR_ANY;
    
    if (bind(self.udpSocket, (struct sockaddr *)&addr, sizeof(addr)) < 0) {
        NSLog(@"Failed to bind UDP socket");
        close(self.udpSocket);
        self.udpSocket = -1;
        return;
    }
    
    // Set up dispatch source for async reading
    self.udpSource = dispatch_source_create(DISPATCH_SOURCE_TYPE_READ, self.udpSocket, 0, dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0));
    
    dispatch_source_set_event_handler(self.udpSource, ^{
        char buffer[4096];
        struct sockaddr_in sender;
        socklen_t senderLen = sizeof(sender);
        
        ssize_t bytesRead = recvfrom(self.udpSocket, buffer, sizeof(buffer), 0, (struct sockaddr *)&sender, &senderLen);
        
        if (bytesRead > 0) {
            NSData *data = [NSData dataWithBytes:buffer length:bytesRead];
            NSString *senderAddress = [NSString stringWithUTF8String:inet_ntoa(sender.sin_addr)];
            
            NSLog(@"📥 UDP broadcast from %@:%d (%ld bytes)", senderAddress, ntohs(sender.sin_port), (long)bytesRead);
            
            dispatch_async(dispatch_get_main_queue(), ^{
                if ([self.delegate respondsToSelector:@selector(networkExplorerDidReceiveBroadcast:fromAddress:)]) {
                    [self.delegate networkExplorerDidReceiveBroadcast:data fromAddress:senderAddress];
                }
            });
        }
    });
    
    dispatch_source_set_cancel_handler(self.udpSource, ^{
        close(self.udpSocket);
        self.udpSocket = -1;
    });
    
    dispatch_resume(self.udpSource);
    
    // Also send some discovery broadcasts
    [self sendDiscoveryBroadcasts];
}

- (void)sendDiscoveryBroadcasts {
    if (self.udpSocket < 0) return;
    
    // SSDP M-SEARCH
    NSString *ssdpSearch = @"M-SEARCH * HTTP/1.1\r\n"
                          @"HOST: 239.255.255.250:1900\r\n"
                          @"MAN: \"ssdp:discover\"\r\n"
                          @"MX: 3\r\n"
                          @"ST: ssdp:all\r\n\r\n";
    
    struct sockaddr_in ssdpAddr;
    memset(&ssdpAddr, 0, sizeof(ssdpAddr));
    ssdpAddr.sin_family = AF_INET;
    ssdpAddr.sin_port = htons(1900);
    inet_pton(AF_INET, "239.255.255.250", &ssdpAddr.sin_addr);
    
    sendto(self.udpSocket, [ssdpSearch UTF8String], [ssdpSearch length], 0, 
           (struct sockaddr *)&ssdpAddr, sizeof(ssdpAddr));
    
    NSLog(@"📤 Sent SSDP discovery broadcast");
}

- (void)probeRawProtocols {
    NSLog(@"🔍 Probing raw protocols...");
    
    // Try connecting with various protocols
    [self tryFTPConnection];
    [self tryTelnetConnection];
    [self trySSHConnection];
    [self tryCustomProtocol];
}

- (void)tryFTPConnection {
    [self.scanQueue addOperationWithBlock:^{
        int sock = socket(AF_INET, SOCK_STREAM, 0);
        if (sock < 0) return;
        
        struct timeval timeout = {.tv_sec = 2, .tv_usec = 0};
        setsockopt(sock, SOL_SOCKET, SO_RCVTIMEO, &timeout, sizeof(timeout));
        
        struct sockaddr_in addr;
        memset(&addr, 0, sizeof(addr));
        addr.sin_family = AF_INET;
        addr.sin_port = htons(21); // FTP
        inet_pton(AF_INET, [self.targetIP UTF8String], &addr.sin_addr);
        
        if (connect(sock, (struct sockaddr *)&addr, sizeof(addr)) == 0) {
            NSLog(@"✅ FTP port is open!");
            
            char buffer[256];
            if (recv(sock, buffer, sizeof(buffer) - 1, 0) > 0) {
                NSLog(@"FTP Banner: %s", buffer);
            }
        }
        
        close(sock);
    }];
}

- (void)tryTelnetConnection {
    [self.scanQueue addOperationWithBlock:^{
        int sock = socket(AF_INET, SOCK_STREAM, 0);
        if (sock < 0) return;
        
        struct timeval timeout = {.tv_sec = 2, .tv_usec = 0};
        setsockopt(sock, SOL_SOCKET, SO_RCVTIMEO, &timeout, sizeof(timeout));
        
        struct sockaddr_in addr;
        memset(&addr, 0, sizeof(addr));
        addr.sin_family = AF_INET;
        addr.sin_port = htons(23); // Telnet
        inet_pton(AF_INET, [self.targetIP UTF8String], &addr.sin_addr);
        
        if (connect(sock, (struct sockaddr *)&addr, sizeof(addr)) == 0) {
            NSLog(@"✅ Telnet port is open!");
        }
        
        close(sock);
    }];
}

- (void)trySSHConnection {
    [self.scanQueue addOperationWithBlock:^{
        int sock = socket(AF_INET, SOCK_STREAM, 0);
        if (sock < 0) return;
        
        struct timeval timeout = {.tv_sec = 2, .tv_usec = 0};
        setsockopt(sock, SOL_SOCKET, SO_RCVTIMEO, &timeout, sizeof(timeout));
        
        struct sockaddr_in addr;
        memset(&addr, 0, sizeof(addr));
        addr.sin_family = AF_INET;
        addr.sin_port = htons(22); // SSH
        inet_pton(AF_INET, [self.targetIP UTF8String], &addr.sin_addr);
        
        if (connect(sock, (struct sockaddr *)&addr, sizeof(addr)) == 0) {
            NSLog(@"✅ SSH port is open!");
            
            char buffer[256];
            if (recv(sock, buffer, sizeof(buffer) - 1, 0) > 0) {
                NSLog(@"SSH Banner: %s", buffer);
            }
        }
        
        close(sock);
    }];
}

- (void)tryCustomProtocol {
    // Try sending custom glasses protocol messages
    NSArray *customPorts = @[@6666, @7777, @8888, @9999];
    
    for (NSNumber *port in customPorts) {
        [self.scanQueue addOperationWithBlock:^{
            int sock = socket(AF_INET, SOCK_STREAM, 0);
            if (sock < 0) return;
            
            struct timeval timeout = {.tv_sec = 2, .tv_usec = 0};
            setsockopt(sock, SOL_SOCKET, SO_RCVTIMEO, &timeout, sizeof(timeout));
            
            struct sockaddr_in addr;
            memset(&addr, 0, sizeof(addr));
            addr.sin_family = AF_INET;
            addr.sin_port = htons(port.intValue);
            inet_pton(AF_INET, [self.targetIP UTF8String], &addr.sin_addr);
            
            if (connect(sock, (struct sockaddr *)&addr, sizeof(addr)) == 0) {
                NSLog(@"✅ Custom port %@ is open!", port);
                
                // Try various handshakes
                const char *handshakes[] = {
                    "HELLO\r\n",
                    "QC\r\n",
                    "GLASSES\r\n",
                    "MEDIA\r\n",
                    "LIST\r\n",
                    "{\"cmd\":\"list\"}\r\n",
                    "\x00\x00\x00\x01", // Binary protocol attempt
                    NULL
                };
                
                for (int i = 0; handshakes[i] != NULL; i++) {
                    send(sock, handshakes[i], strlen(handshakes[i]), 0);
                    
                    char buffer[256];
                    memset(buffer, 0, sizeof(buffer));
                    if (recv(sock, buffer, sizeof(buffer) - 1, MSG_DONTWAIT) > 0) {
                        NSLog(@"Response to '%s': %s", handshakes[i], buffer);
                    }
                }
            }
            
            close(sock);
        }];
    }
}

- (void)stopExploration {
    NSLog(@"🛑 Stopping network exploration");
    
    // Cancel all active tasks
    for (NSURLSessionTask *task in self.activeTasks) {
        [task cancel];
    }
    [self.activeTasks removeAllObjects];
    
    // Cancel all operations
    [self.scanQueue cancelAllOperations];
    
    // Close UDP socket
    if (self.udpSource) {
        dispatch_source_cancel(self.udpSource);
        self.udpSource = nil;
    }
}

@end