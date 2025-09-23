//
//  ViewController.m
//  QCSDKDemo
//
//  Created by steve on 2025/7/22.
//

#import "ViewController.h"
#import <NetworkExtension/NetworkExtension.h>
#import <QCSDK/QCVersionHelper.h>
#import <QCSDK/QCSDKManager.h>
#import <QCSDK/QCSDKCmdCreator.h>

#import "QCScanViewController.h"
#import "QCCentralManager.h"

static NSString *const QGDownloadErrorDomain = @"com.heycyan.qcsdkdemo.download";

typedef NS_ENUM(NSInteger, QGDeviceActionType) {
    /// Get hardware version, firmware version, and WiFi firmware versions
    QGDeviceActionTypeGetVersion = 0,

    /// Set the current device time
    QGDeviceActionTypeSetTime,

    /// Get battery level and charging status
    QGDeviceActionTypeGetBattery,

    /// Get the number of photos, videos, and audio files on the device
    QGDeviceActionTypeGetMediaInfo,

    /// Trigger the device to take a photo
    QGDeviceActionTypeTakePhoto,

    /// Start or stop video recording
    QGDeviceActionTypeToggleVideoRecording,

    /// Start or stop audio recording
    QGDeviceActionTypeToggleAudioRecording,
    
    /// Take AI Image
    QGDeviceActionTypeToggleTakeAIImage,

    /// Download media over Wi-Fi
    QGDeviceActionTypeDownloadMedia,

    /// Reserved for future use
    QGDeviceActionTypeReserved,
};



@interface ViewController ()<UITableViewDelegate, UITableViewDataSource,QCCentralManagerDelegate,QCSDKManagerDelegate>

@property(nonatomic,strong)UIBarButtonItem *rightItem;
@property(nonatomic,strong)UITableView *tableView;

@property(nonatomic,copy)NSString *hardVersion;
@property(nonatomic,copy)NSString *firmVersion;
@property(nonatomic,copy)NSString *hardWiFiVersion;
@property(nonatomic,copy)NSString *firmWiFiVersion;

@property(nonatomic,copy)NSString *mac;

@property(nonatomic,assign)NSInteger battary;
@property(nonatomic,assign)BOOL charging;

@property(nonatomic,assign)NSInteger photoCount;
@property(nonatomic,assign)NSInteger videoCount;
@property(nonatomic,assign)NSInteger audioCount;

@property(nonatomic,assign)BOOL recordingVideo;
@property(nonatomic,assign)BOOL recordingAudio;

@property(nonatomic,strong)NSData *aiImageData;
@property(nonatomic,assign)BOOL downloadingMedia;
@property(nonatomic,copy)NSString *downloadStatusMessage;
@property(nonatomic,copy)NSString *currentDownloadSSID;
@end

@implementation ViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
    self.title = @"Feature(Tap to get data)";
    
    self.rightItem = [[UIBarButtonItem alloc] initWithTitle:@"Search"
                                                      style:(UIBarButtonItemStylePlain)
                                                     target:self
                                                     action:@selector(rightAction)];
    self.navigationItem.rightBarButtonItem = self.rightItem;
    
    self.tableView = [[UITableView alloc] initWithFrame:self.view.bounds style:(UITableViewStylePlain)];
    self.tableView.backgroundColor = [UIColor clearColor];
    self.tableView.delegate = self;
    self.tableView.dataSource = self;
    self.tableView.estimatedRowHeight = 60;
    self.tableView.rowHeight = UITableViewAutomaticDimension;
    self.tableView.hidden = YES;
    [self.view addSubview:self.tableView];
    
    [QCSDKManager shareInstance].delegate = self;
}

#pragma mark - Device Data Report
- (void)didUpdateBatteryLevel:(NSInteger)battery charging:(BOOL)charging {
    self.battary = battery;
    self.charging = charging;
    [self.tableView reloadData];
}

- (void)didUpdateMediaWithPhotoCount:(NSInteger)photo videoCount:(NSInteger)video audioCount:(NSInteger)audio type:(NSInteger)type {
    
    self.photoCount = photo;
    self.videoCount = video;
    self.audioCount = audio;
    [self.tableView reloadData];
}

- (void)didReceiveAIChatImageData:(NSData *)imageData {
    NSLog(@"didReceiveAIChatImageData");
    self.aiImageData = imageData;
    [self.tableView reloadData];
}

#pragma mark - Feature Fuctions
- (void)getHardVersionAndFirmVersion {
    [QCSDKCmdCreator getDeviceVersionInfoSuccess:^(NSString * _Nonnull hdVersion, NSString * _Nonnull firmVersion, NSString * _Nonnull hdWifiVersion, NSString * _Nonnull firmWifiVersion) {
        
        self.hardVersion = hdVersion;
        self.firmVersion = firmVersion;
        self.hardWiFiVersion = hdWifiVersion;
        self.firmWiFiVersion = firmWifiVersion;
        [self.tableView reloadData];
        NSLog(@"hard Version:%@",hdVersion);
        NSLog(@"firm Version:%@",firmVersion);
        NSLog(@"hard Wifi Version:%@",hdWifiVersion);
        NSLog(@"firm Wifi Version:%@",firmWifiVersion);
    } fail:^{
        NSLog(@"get version fail");
    }];
}

- (void)getMacAddress {
    //[QCSDKCmdCreator get
    [QCSDKCmdCreator getDeviceMacAddressSuccess:^(NSString * _Nullable macAddress) {
        self.mac = macAddress;
        [self.tableView reloadData];
    } fail:^{
        NSLog(@"get mac address fail");
    }];
}

- (void)setTime {
    [QCSDKCmdCreator setupDeviceDateTime:^(BOOL isSuccess, NSError * _Nullable err) {
        if (err) {
            NSLog(@"get err fail");
        }
    }];
}

- (void)getBattary {
    [QCSDKCmdCreator getDeviceBattery:^(NSInteger battary, BOOL charging) {
        
        self.battary = battary;
        self.charging = charging;
        [self.tableView reloadData];
    } fail:^{
        
    }];
}

- (void)getMediaInfo {
    [QCSDKCmdCreator getDeviceMedia:^(NSInteger photo, NSInteger video, NSInteger audio, NSInteger type) {
        
        self.photoCount = photo;
        self.videoCount = video;
        self.audioCount = audio;
        [self.tableView reloadData];
    } fail:^{
        
    }];
}

- (void)takePhoto {
    //
    [QCSDKCmdCreator setDeviceMode:(QCOperatorDeviceModePhoto) success:^{
        
    } fail:^(NSInteger mode) {
        NSLog(@"set fail,current device model:%zd",mode);
    }];
}

- (void)recordVideo {
    
    if (self.recordingVideo) {
        
        [QCSDKCmdCreator setDeviceMode:(QCOperatorDeviceModeVideoStop) success:^{
            self.recordingVideo = NO;
            [self.tableView reloadData];
        } fail:^(NSInteger mode) {
            NSLog(@"set fail,current device model:%zd",mode);
        }];
    }
    else {
        [QCSDKCmdCreator setDeviceMode:(QCOperatorDeviceModeVideo) success:^{
            self.recordingVideo = YES;
            [self.tableView reloadData];
        } fail:^(NSInteger mode) {
            NSLog(@"set fail,current device model:%zd",mode);

        }];
    }
}

- (void)recordAudio {
    if (self.recordingVideo) {
        [QCSDKCmdCreator setDeviceMode:(QCOperatorDeviceModeAudioStop) success:^{
            self.recordingAudio = NO;
            [self.tableView reloadData];
        } fail:^(NSInteger mode) {
            NSLog(@"set fail,current device model:%zd",mode);
        }];
    } else {
        [QCSDKCmdCreator setDeviceMode:(QCOperatorDeviceModeAudio) success:^{
            self.recordingAudio = YES;
            [self.tableView reloadData];
        } fail:^(NSInteger mode) {
            NSLog(@"set fail,current device model:%zd",mode);
        }];
    }
}

- (void)takeAIImage {
    //- (void)didReceiveAIChatImageData:(NSData *)imageData
    [QCSDKCmdCreator setDeviceMode:(QCOperatorDeviceModeAIPhoto) success:^{
        
    } fail:^(NSInteger mode) {
        NSLog(@"set fail,current device model:%zd",mode);
    }];
}

#pragma mark - Actions
- (void)viewDidAppear:(BOOL)animated {
    [super viewDidAppear:animated];
    
    [QCCentralManager shared].delegate = self;
    [self didState:[QCCentralManager shared].deviceState];
}

- (void)rightAction {
    
    if([self.rightItem.title isEqualToString:@"Unbind"]) {
        [[QCCentralManager shared] remove];
    }
    else if ([self.rightItem.title isEqualToString:@"Search"])  {
        QCScanViewController *viewCtrl = [[QCScanViewController alloc] init];
        [self.navigationController pushViewController:viewCtrl animated:true];
    }
}

#pragma mark - QCCentralManagerDelegate
- (void)didState:(QCState)state {
    self.title = @"Feature";
    switch(state) {
        case QCStateUnbind:
            self.rightItem.title = @"Search";
            self.tableView.hidden = YES;
            break;
        case QCStateConnecting:
            self.title = [QCCentralManager shared].connectedPeripheral.name;
            self.rightItem.title = @"Connecting";
            self.rightItem.enabled = NO;
            self.tableView.hidden = YES;
            break;
        case QCStateConnected:
            self.title = [NSString stringWithFormat:@"%@(Tap to get data)",[QCCentralManager shared].connectedPeripheral.name];
            self.rightItem.title = @"Unbind";
            self.rightItem.enabled = YES;
            self.tableView.hidden = NO;
            break;
        case QCStateUnkown:
            break;
        case QCStateDisconnecting:
        case QCStateDisconnected:
            self.rightItem.title = @"Search";
            self.rightItem.enabled = YES;
            self.tableView.hidden = YES;
            break;
    }
}

- (void)didBluetoothState:(QCBluetoothState)state {
    
}

- (void)didConnected:(CBPeripheral *)peripheral     //用户可以返回设备类型
{
    NSLog(@"didConnected");
    self.rightItem.enabled = YES;
    self.title = peripheral.name;
}

- (void)didDisconnecte:(CBPeripheral *)peripheral {
    NSLog(@"didDisconnecte");
    self.title = @"Feature";
    
    self.rightItem.title = @"Search";
    self.rightItem.enabled = YES;
    self.tableView.hidden = YES;
}

- (void)didFailConnected:(CBPeripheral *)peripheral {
    
    NSLog(@"didFailConnected");
    self.rightItem.enabled = YES;
}


#pragma mark - UITableViewDataSource
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return QGDeviceActionTypeReserved;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {

    static NSString *cellIdentifier = @"Cell";

    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:cellIdentifier];

    if (!cell) {
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:cellIdentifier];
    }

    cell.detailTextLabel.numberOfLines = 0;
    cell.detailTextLabel.lineBreakMode = NSLineBreakByWordWrapping;
    cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
    cell.imageView.image = nil;
    
    switch ((QGDeviceActionType)indexPath.row) {
        case QGDeviceActionTypeGetVersion:
            cell.textLabel.text = @"Get hard Version & firm Version";
            cell.detailTextLabel.text = [NSString stringWithFormat:@"hardVersion:%@,\nfirmVersion:%@,\nhardWifiVersion:%@,\nfirmWifiVersion:%@", self.hardVersion, self.firmVersion, self.hardWiFiVersion, self.firmWiFiVersion];
            break;
        case QGDeviceActionTypeSetTime:
            cell.textLabel.text = @"Set Time";
            cell.detailTextLabel.text = @"";
            break;
        case QGDeviceActionTypeGetBattery:
            cell.textLabel.text = @"Get Battary";
            cell.detailTextLabel.text = [NSString stringWithFormat:@"battary:%zd,charing:%zd", self.battary, (NSInteger)self.charging];
            break;
        case QGDeviceActionTypeGetMediaInfo:
            cell.textLabel.text = @"Get media info";
            cell.detailTextLabel.text = [NSString stringWithFormat:@"photo:%zd,video:%zd,audio:%zd", self.photoCount, self.videoCount, self.audioCount];
            break;
        case QGDeviceActionTypeTakePhoto:
            cell.textLabel.text = @"Take Photo";
            break;
        case QGDeviceActionTypeToggleVideoRecording:
            cell.textLabel.text = self.recordingVideo ? @"Stop Recording Video" : @"Start Recording Video";
            break;
        case QGDeviceActionTypeToggleAudioRecording:
            cell.textLabel.text = self.recordingAudio ? @"Stop Record audio" : @"Start Record audio";
            break;
        case QGDeviceActionTypeToggleTakeAIImage:
            cell.textLabel.text = @"Take AI Image";
            if (self.aiImageData) {
                cell.imageView.image = [UIImage imageWithData:self.aiImageData];
            }
            break;
        case QGDeviceActionTypeDownloadMedia:
            cell.textLabel.text = @"Download Media Over Wi-Fi";
            cell.detailTextLabel.text = self.downloadStatusMessage ?: @"Tap to download the latest media files over the device Wi-Fi.";
            break;
        case QGDeviceActionTypeReserved:
            break;
        default:
            break;
    }

    return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    [tableView deselectRowAtIndexPath:indexPath animated:NO];
    QGDeviceActionType actionType = (QGDeviceActionType)indexPath.row;

    switch (actionType) {
        case QGDeviceActionTypeGetVersion:
            [self getHardVersionAndFirmVersion];
            break;
        case QGDeviceActionTypeSetTime:
            [self setTime];
            break;
        case QGDeviceActionTypeGetBattery:
            [self getBattary];
            break;
        case QGDeviceActionTypeGetMediaInfo:
            [self getMediaInfo];
            break;
        case QGDeviceActionTypeTakePhoto:
            [self takePhoto];
            break;
        case QGDeviceActionTypeToggleVideoRecording:
            [self recordVideo];
            break;
        case QGDeviceActionTypeToggleAudioRecording:
            [self recordAudio];
            break;
        case QGDeviceActionTypeToggleTakeAIImage:
            [self takeAIImage];
            break;
        case QGDeviceActionTypeDownloadMedia:
            [self downloadMediaOverWiFi];
            break;
        case QGDeviceActionTypeReserved:
        default:
            break;
    }

}

#pragma mark - Wi-Fi Media Download

- (void)downloadMediaOverWiFi {
    if (self.downloadingMedia) {
        [self updateDownloadStatus:@"Media transfer already in progress..."];
        return;
    }

    self.downloadingMedia = YES;
    [self updateDownloadStatus:@"Preparing Wi-Fi transfer..."];

    __weak typeof(self) weakSelf = self;
    dispatch_async(dispatch_get_global_queue(QOS_CLASS_USER_INITIATED, 0), ^{
        __strong typeof(weakSelf) strongSelf = weakSelf;
        if (!strongSelf) {
            return;
        }
        [strongSelf handleMediaDownloadWorkflow];
    });
}

- (void)handleMediaDownloadWorkflow {
    static NSTimeInterval const QGDeviceWiFiCommandTimeout = 15.0;
    static NSTimeInterval const QGDeviceHTTPRequestTimeout = 45.0;

    [self updateDownloadStatus:@"Requesting device Wi-Fi credentials..."];

    __block NSString *ssid = nil;
    __block NSString *password = nil;
    __block NSInteger wifiErrorCode = 0;

    dispatch_semaphore_t wifiSem = dispatch_semaphore_create(0);
    dispatch_async(dispatch_get_main_queue(), ^{
        [QCSDKCmdCreator openWifiWithMode:QCOperatorDeviceModeTransfer success:^(NSString * _Nonnull wifiSSID, NSString * _Nonnull wifiPassword) {
            ssid = wifiSSID;
            password = wifiPassword;
            dispatch_semaphore_signal(wifiSem);
        } fail:^(NSInteger errorCode) {
            wifiErrorCode = errorCode;
            dispatch_semaphore_signal(wifiSem);
        }];
    });

    if (dispatch_semaphore_wait(wifiSem, dispatch_time(DISPATCH_TIME_NOW, (int64_t)(QGDeviceWiFiCommandTimeout * NSEC_PER_SEC))) != 0 || ssid.length == 0) {
        NSString *message = ssid.length == 0 ? [NSString stringWithFormat:@"Failed to open device Wi-Fi (code %ld).", (long)wifiErrorCode] : @"Timed out while requesting Wi-Fi credentials.";
        [self completeMediaDownloadWithMessage:message ssid:nil success:NO];
        return;
    }

    self.currentDownloadSSID = ssid;
    [self updateDownloadStatus:[NSString stringWithFormat:@"Connecting to %@...", ssid]];

    NEHotspotConfiguration *configuration = nil;
    if (password.length > 0) {
        configuration = [[NEHotspotConfiguration alloc] initWithSSID:ssid passphrase:password isWEP:NO];
    } else {
        configuration = [[NEHotspotConfiguration alloc] initWithSSID:ssid];
    }
    configuration.joinOnce = YES;

    __block NSError *hotspotError = nil;
    dispatch_semaphore_t hotspotSem = dispatch_semaphore_create(0);
    dispatch_async(dispatch_get_main_queue(), ^{
        [[NEHotspotConfigurationManager sharedManager] applyConfiguration:configuration completionHandler:^(NSError * _Nullable error) {
            hotspotError = error;
            dispatch_semaphore_signal(hotspotSem);
        }];
    });

    if (dispatch_semaphore_wait(hotspotSem, dispatch_time(DISPATCH_TIME_NOW, (int64_t)(QGDeviceWiFiCommandTimeout * NSEC_PER_SEC))) != 0) {
        [self completeMediaDownloadWithMessage:@"Timed out while joining device Wi-Fi network." ssid:ssid success:NO];
        return;
    }

    if (hotspotError && !([hotspotError.domain isEqualToString:NEHotspotConfigurationErrorDomain] && hotspotError.code == NEHotspotConfigurationErrorAlreadyAssociated)) {
        NSString *message = [NSString stringWithFormat:@"Unable to join device Wi-Fi: %@", hotspotError.localizedDescription];
        [self completeMediaDownloadWithMessage:message ssid:ssid success:NO];
        return;
    }

    [self updateDownloadStatus:@"Fetching device IP address..."];

    __block NSString *deviceIP = nil;
    dispatch_semaphore_t ipSem = dispatch_semaphore_create(0);
    dispatch_async(dispatch_get_main_queue(), ^{
        [QCSDKCmdCreator getDeviceWifiIPSuccess:^(NSString * _Nullable ipAddress) {
            deviceIP = ipAddress;
            dispatch_semaphore_signal(ipSem);
        } failed:^{
            dispatch_semaphore_signal(ipSem);
        }];
    });

    if (dispatch_semaphore_wait(ipSem, dispatch_time(DISPATCH_TIME_NOW, (int64_t)(QGDeviceWiFiCommandTimeout * NSEC_PER_SEC))) != 0 || deviceIP.length == 0) {
        [self completeMediaDownloadWithMessage:@"Failed to obtain device IP address." ssid:ssid success:NO];
        return;
    }

    NSString *configURLString = [NSString stringWithFormat:@"http://%@/files/media.config", deviceIP];
    NSURL *configURL = [NSURL URLWithString:configURLString];
    if (!configURL) {
        [self completeMediaDownloadWithMessage:@"Invalid media configuration URL." ssid:ssid success:NO];
        return;
    }

    [self updateDownloadStatus:@"Downloading media manifest..."];

    NSError *configError = nil;
    NSData *configData = [self fetchDataSynchronouslyWithURL:configURL timeout:QGDeviceHTTPRequestTimeout error:&configError];
    if (!configData) {
        NSString *message = configError ? configError.localizedDescription : @"Unable to download media manifest.";
        [self completeMediaDownloadWithMessage:message ssid:ssid success:NO];
        return;
    }

    NSError *parseError = nil;
    NSArray<NSString *> *mediaFiles = [self mediaFileNamesFromConfigData:configData error:&parseError];
    NSMutableArray<NSString *> *normalizedFileNames = [NSMutableArray array];
    for (NSString *fileName in mediaFiles) {
        NSString *trimmed = [fileName stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceAndNewlineCharacterSet]];
        if (trimmed.length > 0) {
            [normalizedFileNames addObject:trimmed];
        }
    }

    if (normalizedFileNames.count == 0) {
        if (parseError) {
            [self completeMediaDownloadWithMessage:parseError.localizedDescription ssid:ssid success:NO];
        } else {
            [self completeMediaDownloadWithMessage:@"No media files available for download." ssid:ssid success:YES];
        }
        return;
    }

    NSString *documentsPath = [NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES) firstObject];
    NSString *mediaDirectory = [documentsPath stringByAppendingPathComponent:@"GlassesMedia"];
    NSError *directoryError = nil;
    if (![[NSFileManager defaultManager] createDirectoryAtPath:mediaDirectory withIntermediateDirectories:YES attributes:nil error:&directoryError]) {
        NSString *message = [NSString stringWithFormat:@"Unable to create media directory: %@", directoryError.localizedDescription];
        [self completeMediaDownloadWithMessage:message ssid:ssid success:NO];
        return;
    }

    NSURLSessionConfiguration *sessionConfiguration = [NSURLSessionConfiguration defaultSessionConfiguration];
    sessionConfiguration.timeoutIntervalForRequest = QGDeviceHTTPRequestTimeout;
    sessionConfiguration.timeoutIntervalForResource = QGDeviceHTTPRequestTimeout * 2;
    NSURLSession *session = [NSURLSession sessionWithConfiguration:sessionConfiguration];

    NSError *downloadError = nil;
    NSUInteger completedCount = 0;
    for (NSString *trimmedName in normalizedFileNames) {
        completedCount++;
        NSString *status = [NSString stringWithFormat:@"Downloading %lu/%lu: %@", (unsigned long)completedCount, (unsigned long)normalizedFileNames.count, trimmedName];
        [self updateDownloadStatus:status];

        NSString *encodedFileName = [trimmedName stringByAddingPercentEncodingWithAllowedCharacters:[NSCharacterSet URLPathAllowedCharacterSet]];
        NSString *fileURLString = [NSString stringWithFormat:@"http://%@/files/%@", deviceIP, encodedFileName];
        NSURL *fileURL = [NSURL URLWithString:fileURLString];
        if (!fileURL) {
            downloadError = [NSError errorWithDomain:QGDownloadErrorDomain code:-2 userInfo:@{NSLocalizedDescriptionKey: [NSString stringWithFormat:@"Invalid file URL for %@", trimmedName]}];
            break;
        }

        NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:fileURL];
        request.timeoutInterval = QGDeviceHTTPRequestTimeout;
        NSData *fileData = [self fetchDataSynchronouslyWithRequest:request session:session timeout:QGDeviceHTTPRequestTimeout error:&downloadError];
        if (!fileData) {
            break;
        }

        NSString *destinationPath = [mediaDirectory stringByAppendingPathComponent:trimmedName.lastPathComponent];
        NSURL *destinationURL = [NSURL fileURLWithPath:destinationPath];
        [[NSFileManager defaultManager] removeItemAtURL:destinationURL error:nil];
        if (![fileData writeToURL:destinationURL options:NSDataWritingAtomic error:&downloadError]) {
            break;
        }

        NSString *savedStatus = [NSString stringWithFormat:@"Saved %@ (%lu/%lu)", destinationURL.lastPathComponent, (unsigned long)completedCount, (unsigned long)normalizedFileNames.count];
        [self updateDownloadStatus:savedStatus];
    }

    [session finishTasksAndInvalidate];

    if (downloadError) {
        NSString *message = [NSString stringWithFormat:@"Download failed: %@", downloadError.localizedDescription ?: @"Unknown error"];
        [self completeMediaDownloadWithMessage:message ssid:ssid success:NO];
        return;
    }

    [self completeMediaDownloadWithMessage:@"Media transfer completed successfully." ssid:ssid success:YES];
}

- (NSData *)fetchDataSynchronouslyWithURL:(NSURL *)url timeout:(NSTimeInterval)timeout error:(NSError **)error {
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:url];
    request.timeoutInterval = timeout;
    return [self fetchDataSynchronouslyWithRequest:request session:[NSURLSession sharedSession] timeout:timeout error:error];
}

- (NSData *)fetchDataSynchronouslyWithRequest:(NSURLRequest *)request session:(NSURLSession *)session timeout:(NSTimeInterval)timeout error:(NSError **)error {
    dispatch_semaphore_t sem = dispatch_semaphore_create(0);
    __block NSData *resultData = nil;
    __block NSURLResponse *response = nil;
    __block NSError *responseError = nil;

    NSURLSessionDataTask *task = [session dataTaskWithRequest:request completionHandler:^(NSData * _Nullable data, NSURLResponse * _Nullable resp, NSError * _Nullable err) {
        resultData = data;
        response = resp;
        responseError = err;
        dispatch_semaphore_signal(sem);
    }];

    [task resume];

    if (dispatch_semaphore_wait(sem, dispatch_time(DISPATCH_TIME_NOW, (int64_t)(timeout * NSEC_PER_SEC))) != 0) {
        [task cancel];
        if (error) {
            *error = [NSError errorWithDomain:QGDownloadErrorDomain code:-1001 userInfo:@{NSLocalizedDescriptionKey: @"Request timed out."}];
        }
        return nil;
    }

    if (responseError) {
        if (error) {
            *error = responseError;
        }
        return nil;
    }

    if ([response isKindOfClass:[NSHTTPURLResponse class]]) {
        NSInteger statusCode = ((NSHTTPURLResponse *)response).statusCode;
        if (statusCode < 200 || statusCode >= 300) {
            if (error) {
                NSString *message = [NSString stringWithFormat:@"HTTP %ld", (long)statusCode];
                *error = [NSError errorWithDomain:QGDownloadErrorDomain code:statusCode userInfo:@{NSLocalizedDescriptionKey: message}];
            }
            return nil;
        }
    }

    if (!resultData) {
        if (error) {
            *error = [NSError errorWithDomain:QGDownloadErrorDomain code:-1002 userInfo:@{NSLocalizedDescriptionKey: @"No data received."}];
        }
        return nil;
    }

    return resultData;
}

- (NSArray<NSString *> *)mediaFileNamesFromConfigData:(NSData *)configData error:(NSError **)error {
    if (!configData) {
        if (error) {
            *error = [NSError errorWithDomain:QGDownloadErrorDomain code:-2000 userInfo:@{NSLocalizedDescriptionKey: @"Missing media configuration data."}];
        }
        return @[];
    }

    NSMutableArray<NSString *> *filenames = [NSMutableArray array];
    NSError *jsonError = nil;
    id jsonObject = [NSJSONSerialization JSONObjectWithData:configData options:0 error:&jsonError];
    if (!jsonError && jsonObject) {
        if ([jsonObject isKindOfClass:[NSArray class]]) {
            for (id entry in (NSArray *)jsonObject) {
                if ([entry isKindOfClass:[NSString class]]) {
                    [filenames addObject:(NSString *)entry];
                }
            }
        } else if ([jsonObject isKindOfClass:[NSDictionary class]]) {
            id filesValue = jsonObject[@"files"] ?: jsonObject[@"media"] ?: jsonObject[@"items"];
            if ([filesValue isKindOfClass:[NSArray class]]) {
                for (id entry in (NSArray *)filesValue) {
                    if ([entry isKindOfClass:[NSString class]]) {
                        [filenames addObject:(NSString *)entry];
                    }
                }
            }
        }
    }

    if (filenames.count == 0) {
        NSString *configString = [[NSString alloc] initWithData:configData encoding:NSUTF8StringEncoding];
        if (configString.length > 0) {
            [configString enumerateLinesUsingBlock:^(NSString * _Nonnull line, BOOL * _Nonnull stop) {
                NSString *trimmed = [line stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceAndNewlineCharacterSet]];
                if (trimmed.length > 0 && ![trimmed hasPrefix:@"#"]) {
                    [filenames addObject:trimmed];
                }
            }];
        }
    }

    if (filenames.count == 0 && error) {
        *error = [NSError errorWithDomain:QGDownloadErrorDomain code:-2001 userInfo:@{NSLocalizedDescriptionKey: @"Unable to parse media configuration."}];
    }

    return filenames;
}

- (void)completeMediaDownloadWithMessage:(NSString *)message ssid:(NSString *)ssid success:(BOOL)success {
    [self updateDownloadStatus:message];
    dispatch_async(dispatch_get_main_queue(), ^{
        self.downloadingMedia = NO;
        NSString *targetSSID = ssid.length > 0 ? ssid : self.currentDownloadSSID;
        if (targetSSID.length > 0) {
            [[NEHotspotConfigurationManager sharedManager] removeConfigurationForSSID:targetSSID];
        }
        self.currentDownloadSSID = nil;
    });
}

- (void)updateDownloadStatus:(NSString *)status {
    dispatch_async(dispatch_get_main_queue(), ^{
        self.downloadStatusMessage = status;
        NSIndexPath *indexPath = [NSIndexPath indexPathForRow:QGDeviceActionTypeDownloadMedia inSection:0];
        if ([self.tableView numberOfSections] > 0 && indexPath.row < [self.tableView numberOfRowsInSection:0]) {
            [self.tableView reloadRowsAtIndexPaths:@[indexPath] withRowAnimation:UITableViewRowAnimationNone];
        } else {
            [self.tableView reloadData];
        }
    });
}

@end
