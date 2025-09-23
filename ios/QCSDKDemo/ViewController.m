//
//  ViewController.m
//  QCSDKDemo
//
//  Created by steve on 2025/7/22.
//

#import "ViewController.h"
#import <QCSDK/QCVersionHelper.h>
#import <QCSDK/QCSDKManager.h>
#import <QCSDK/QCSDKCmdCreator.h>

#import "QCScanViewController.h"
#import "QCCentralManager.h"

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

    /// Download media files to the phone
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

@property(nonatomic,assign)BOOL isDownloadingMedia;
@property(nonatomic,assign)BOOL didEnableWifiForDownload;
@property(nonatomic,strong)NSURLSession *mediaSession;
@property(nonatomic,strong)NSURLSessionDataTask *configTask;
@property(nonatomic,strong)NSURLSessionDownloadTask *currentDownloadTask;
@property(nonatomic,strong)NSArray<NSString *> *mediaFiles;
@property(nonatomic,assign)NSUInteger currentDownloadIndex;
@property(nonatomic,copy)NSString *downloadPrimaryStatus;
@property(nonatomic,copy)NSString *downloadSecondaryStatus;
@property(nonatomic,strong)UIImage *latestDownloadedImage;
@property(nonatomic,copy)NSString *latestDownloadedFileName;
@property(nonatomic,copy)NSString *deviceWifiSSID;
@property(nonatomic,copy)NSString *deviceWifiPassword;
@property(nonatomic,copy)NSString *deviceIPAddress;
@property(nonatomic,strong)NSURL *mediaDirectoryURL;
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

    self.downloadPrimaryStatus = @"Idle";
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
        case QGDeviceActionTypeDownloadMedia: {
            cell.textLabel.text = @"Download Media Files";
            NSMutableArray<NSString *> *statusComponents = [NSMutableArray array];
            if (self.downloadPrimaryStatus.length > 0) {
                [statusComponents addObject:self.downloadPrimaryStatus];
            }
            if (self.downloadSecondaryStatus.length > 0) {
                [statusComponents addObject:self.downloadSecondaryStatus];
            }
            BOOL containsLastFileEntry = NO;
            for (NSString *component in statusComponents) {
                if ([component containsString:@"Last file:"]) {
                    containsLastFileEntry = YES;
                    break;
                }
            }
            if (!self.latestDownloadedImage && self.latestDownloadedFileName.length > 0 && !containsLastFileEntry) {
                [statusComponents addObject:[NSString stringWithFormat:@"Last file: %@", self.latestDownloadedFileName]];
            }
            cell.detailTextLabel.text = statusComponents.count > 0 ? [statusComponents componentsJoinedByString:@"\n"] : @"Idle";
            cell.imageView.image = self.latestDownloadedImage;
            break;
        }
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
            [self startMediaDownloadWorkflow];
            break;
        case QGDeviceActionTypeReserved:
        default:
            break;
    }

}

#pragma mark - Media Download Workflow

- (void)startMediaDownloadWorkflow {
    if (self.isDownloadingMedia) {
        [self presentDownloadAlertWithTitle:@"Download In Progress"
                                     message:@"A media download session is already running. Please wait for it to finish."];
        return;
    }

    self.isDownloadingMedia = YES;
    [self updateDownloadUIWithPrimary:@"Requesting Wi-Fi credentials..."
                              secondary:nil
                         latestFileName:nil
                              latestImage:nil];

    __weak typeof(self) weakSelf = self;
    [QCSDKCmdCreator openWifiWithMode:QCOperatorDeviceModeTransfer success:^(NSString * _Nonnull ssid, NSString * _Nonnull password) {
        __strong typeof(weakSelf) strongSelf = weakSelf;
        if (!strongSelf) { return; }

        strongSelf.didEnableWifiForDownload = YES;
        strongSelf.deviceWifiSSID = ssid;
        strongSelf.deviceWifiPassword = password;

        [strongSelf updateDownloadUIWithPrimary:@"Wi-Fi enabled"
                                      secondary:[NSString stringWithFormat:@"SSID: %@", ssid]
                                 latestFileName:nil
                                      latestImage:nil];

        [strongSelf requestDeviceIPAddress];
    } fail:^(NSInteger mode) {
        __strong typeof(weakSelf) strongSelf = weakSelf;
        if (!strongSelf) { return; }

        NSString *message = [NSString stringWithFormat:@"Unable to enable Wi-Fi (code %ld).", (long)mode];
        [strongSelf finalizeDownloadWithSuccess:NO
                                        message:message
                                        primary:@"Wi-Fi enable failed"
                                      secondary:message];
    }];
}

- (void)requestDeviceIPAddress {
    __weak typeof(self) weakSelf = self;
    [self updateDownloadUIWithPrimary:@"Requesting device IP..."
                              secondary:nil
                         latestFileName:nil
                              latestImage:nil];

    [QCSDKCmdCreator getDeviceWifiIPSuccess:^(NSString * _Nullable ip) {
        __strong typeof(weakSelf) strongSelf = weakSelf;
        if (!strongSelf) { return; }

        if (ip.length == 0) {
            NSString *message = @"Received an empty IP address from the device.";
            [strongSelf finalizeDownloadWithSuccess:NO
                                            message:message
                                            primary:@"Invalid device IP"
                                          secondary:message];
            return;
        }

        strongSelf.deviceIPAddress = ip;
        [strongSelf updateDownloadUIWithPrimary:@"Device IP received"
                                      secondary:[NSString stringWithFormat:@"IP: %@", ip]
                                 latestFileName:nil
                                      latestImage:nil];

        [strongSelf fetchMediaConfiguration];
    } failed:^{
        __strong typeof(weakSelf) strongSelf = weakSelf;
        if (!strongSelf) { return; }

        NSString *message = @"Failed to obtain the device IP address.";
        [strongSelf finalizeDownloadWithSuccess:NO
                                        message:message
                                        primary:@"Device IP request failed"
                                      secondary:message];
    }];
}

- (void)fetchMediaConfiguration {
    NSString *ip = self.deviceIPAddress;
    if (ip.length == 0) {
        NSString *message = @"Missing device IP address.";
        [self finalizeDownloadWithSuccess:NO
                                  message:message
                                  primary:@"Configuration download failed"
                                secondary:message];
        return;
    }

    [self updateDownloadUIWithPrimary:@"Downloading media manifest..."
                              secondary:nil
                         latestFileName:nil
                              latestImage:nil];

    NSURL *url = [NSURL URLWithString:[NSString stringWithFormat:@"http://%@/files/media.config", ip]];
    if (!url) {
        NSString *message = @"Unable to build the media manifest URL.";
        [self finalizeDownloadWithSuccess:NO
                                  message:message
                                  primary:@"Configuration URL error"
                                secondary:message];
        return;
    }

    NSURLSessionConfiguration *configuration = [NSURLSessionConfiguration defaultSessionConfiguration];
    configuration.allowsCellularAccess = NO;
    self.mediaSession = [NSURLSession sessionWithConfiguration:configuration];

    __weak typeof(self) weakSelf = self;
    self.configTask = [self.mediaSession dataTaskWithURL:url
                                       completionHandler:^(NSData * _Nullable data, NSURLResponse * _Nullable response, NSError * _Nullable error) {
        __strong typeof(weakSelf) strongSelf = weakSelf;
        if (!strongSelf) { return; }

        strongSelf.configTask = nil;

        if (error) {
            NSString *message = [NSString stringWithFormat:@"Manifest request error: %@", error.localizedDescription ?: @"unknown error"];
            [strongSelf finalizeDownloadWithSuccess:NO
                                            message:message
                                            primary:@"Manifest download failed"
                                          secondary:message];
            return;
        }

        NSHTTPURLResponse *httpResponse = (NSHTTPURLResponse *)response;
        if (![httpResponse isKindOfClass:[NSHTTPURLResponse class]] || httpResponse.statusCode < 200 || httpResponse.statusCode >= 300) {
            NSString *message = [NSString stringWithFormat:@"Manifest HTTP error: %ld", (long)httpResponse.statusCode];
            [strongSelf finalizeDownloadWithSuccess:NO
                                            message:message
                                            primary:@"Manifest HTTP error"
                                          secondary:message];
            return;
        }

        NSError *parseError = nil;
        NSArray<NSString *> *files = [strongSelf mediaEntriesFromConfigData:data error:&parseError];
        if (parseError) {
            NSString *message = [NSString stringWithFormat:@"Failed to parse manifest: %@", parseError.localizedDescription ?: @"unknown error"];
            [strongSelf finalizeDownloadWithSuccess:NO
                                            message:message
                                            primary:@"Manifest parse error"
                                          secondary:message];
            return;
        }

        strongSelf.mediaFiles = files;
        strongSelf.currentDownloadIndex = 0;

        if (files.count == 0) {
            NSString *message = @"The manifest did not contain any media files.";
            [strongSelf finalizeDownloadWithSuccess:YES
                                            message:message
                                            primary:@"No media files available"
                                          secondary:message];
            return;
        }

        [strongSelf downloadNextMediaFile];
    }];

    [self.configTask resume];
}

- (NSArray<NSString *> *)mediaEntriesFromConfigData:(NSData *)data error:(NSError **)error {
    if (data.length == 0) {
        if (error) {
            *error = [NSError errorWithDomain:@"com.heycyan.sdk" code:-1 userInfo:@{NSLocalizedDescriptionKey: @"Manifest response was empty."}];
        }
        return @[];
    }

    NSError *jsonError = nil;
    id json = [NSJSONSerialization JSONObjectWithData:data options:0 error:&jsonError];
    NSMutableArray<NSString *> *results = [NSMutableArray array];

    if ([json isKindOfClass:[NSArray class]]) {
        for (id item in (NSArray *)json) {
            if ([item isKindOfClass:[NSString class]] && ((NSString *)item).length > 0) {
                [results addObject:(NSString *)item];
            }
        }
    } else if ([json isKindOfClass:[NSDictionary class]]) {
        id filesNode = ((NSDictionary *)json)[@"files"] ?: ((NSDictionary *)json)[@"media"];
        if ([filesNode isKindOfClass:[NSArray class]]) {
            for (id item in (NSArray *)filesNode) {
                if ([item isKindOfClass:[NSString class]] && ((NSString *)item).length > 0) {
                    [results addObject:(NSString *)item];
                }
            }
        }
    }

    if (results.count > 0) {
        return results;
    }

    if (jsonError) {
        // Fall back to newline separated plain text.
        NSString *manifestString = [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
        if (manifestString.length == 0) {
            if (error) {
                *error = jsonError;
            }
            return @[];
        }

        NSArray<NSString *> *components = [manifestString componentsSeparatedByCharactersInSet:[NSCharacterSet newlineCharacterSet]];
        for (NSString *component in components) {
            NSString *trimmed = [component stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceAndNewlineCharacterSet]];
            if (trimmed.length > 0) {
                [results addObject:trimmed];
            }
        }
    }

    if (error && results.count == 0) {
        *error = jsonError ?: [NSError errorWithDomain:@"com.heycyan.sdk"
                                                 code:-2
                                             userInfo:@{NSLocalizedDescriptionKey: @"Unable to extract media file list."}];
    }

    return results;
}

- (void)downloadNextMediaFile {
    if (self.currentDownloadIndex >= self.mediaFiles.count) {
        NSUInteger totalCount = self.mediaFiles.count;
        NSString *primary = totalCount > 0 ? [NSString stringWithFormat:@"Completed %lu file%@",
                                              (unsigned long)totalCount,
                                              totalCount == 1 ? @"" : @"s"] : @"No media files available";
        NSString *secondary = self.latestDownloadedFileName.length > 0 ? [NSString stringWithFormat:@"Last file: %@", self.latestDownloadedFileName] : nil;
        NSString *message = totalCount > 0 ? @"All media files have been downloaded." : @"The manifest did not contain any media files.";
        [self finalizeDownloadWithSuccess:YES
                                    message:message
                                    primary:primary
                                  secondary:secondary];
        return;
    }

    NSString *relativePath = self.mediaFiles[self.currentDownloadIndex];
    NSString *encodedPath = [relativePath stringByAddingPercentEncodingWithAllowedCharacters:[NSCharacterSet URLPathAllowedCharacterSet]];
    if (encodedPath.length == 0) {
        NSString *message = [NSString stringWithFormat:@"Unable to encode download path for %@.", relativePath];
        [self finalizeDownloadWithSuccess:NO
                                  message:message
                                  primary:@"Encoding error"
                                secondary:message];
        return;
    }
    NSURL *url = [NSURL URLWithString:[NSString stringWithFormat:@"http://%@/%@", self.deviceIPAddress, encodedPath]];

    if (!url) {
        NSString *message = [NSString stringWithFormat:@"Unable to build download URL for %@.", relativePath];
        [self finalizeDownloadWithSuccess:NO
                                  message:message
                                  primary:@"Download URL error"
                                secondary:message];
        return;
    }

    NSUInteger currentDisplayIndex = self.currentDownloadIndex + 1;
    NSUInteger totalCount = self.mediaFiles.count;
    [self updateDownloadUIWithPrimary:[NSString stringWithFormat:@"Downloading %lu of %lu...",
                                        (unsigned long)currentDisplayIndex,
                                        (unsigned long)totalCount]
                              secondary:[NSString stringWithFormat:@"Current file: %@", relativePath]
                         latestFileName:nil
                              latestImage:nil];

    __weak typeof(self) weakSelf = self;
    self.currentDownloadTask = [self.mediaSession downloadTaskWithURL:url
                                                     completionHandler:^(NSURL * _Nullable location, NSURLResponse * _Nullable response, NSError * _Nullable error) {
        __strong typeof(weakSelf) strongSelf = weakSelf;
        if (!strongSelf) { return; }

        strongSelf.currentDownloadTask = nil;

        if (error) {
            NSString *message = [NSString stringWithFormat:@"Failed to download %@: %@", relativePath, error.localizedDescription ?: @"unknown error"];
            [strongSelf finalizeDownloadWithSuccess:NO
                                            message:message
                                            primary:@"Download failed"
                                          secondary:message];
            return;
        }

        NSHTTPURLResponse *httpResponse = (NSHTTPURLResponse *)response;
        if (![httpResponse isKindOfClass:[NSHTTPURLResponse class]] || httpResponse.statusCode < 200 || httpResponse.statusCode >= 300) {
            NSString *message = [NSString stringWithFormat:@"HTTP %ld while downloading %@", (long)httpResponse.statusCode, relativePath];
            [strongSelf finalizeDownloadWithSuccess:NO
                                            message:message
                                            primary:@"HTTP error"
                                          secondary:message];
            return;
        }

        NSURL *baseDirectory = [strongSelf mediaDirectoryURL];
        if (!baseDirectory) {
            NSString *message = @"Unable to locate the GlassesMedia directory.";
            [strongSelf finalizeDownloadWithSuccess:NO
                                            message:message
                                            primary:@"Storage unavailable"
                                          secondary:message];
            return;
        }

        NSURL *destinationURL = [baseDirectory URLByAppendingPathComponent:relativePath];
        NSURL *destinationDirectory = [destinationURL URLByDeletingLastPathComponent];
        NSError *fileError = nil;
        if (![[NSFileManager defaultManager] createDirectoryAtURL:destinationDirectory
                                      withIntermediateDirectories:YES
                                                       attributes:nil
                                                            error:&fileError]) {
            NSString *message = [NSString stringWithFormat:@"Failed to prepare directory for %@: %@", relativePath, fileError.localizedDescription ?: @"unknown error"];
            [strongSelf finalizeDownloadWithSuccess:NO
                                            message:message
                                            primary:@"File system error"
                                          secondary:message];
            return;
        }

        [[NSFileManager defaultManager] removeItemAtURL:destinationURL error:nil];
        if (![[NSFileManager defaultManager] moveItemAtURL:location toURL:destinationURL error:&fileError]) {
            NSString *message = [NSString stringWithFormat:@"Failed to save %@: %@", relativePath, fileError.localizedDescription ?: @"unknown error"];
            [strongSelf finalizeDownloadWithSuccess:NO
                                            message:message
                                            primary:@"File save error"
                                          secondary:message];
            return;
        }

        long long expectedBytes = httpResponse.expectedContentLength;
        if (expectedBytes > 0) {
            NSDictionary<NSFileAttributeKey, id> *attributes = [[NSFileManager defaultManager] attributesOfItemAtPath:destinationURL.path error:&fileError];
            if (fileError) {
                NSString *message = [NSString stringWithFormat:@"Unable to validate %@: %@", relativePath, fileError.localizedDescription ?: @"unknown error"];
                [strongSelf finalizeDownloadWithSuccess:NO
                                                message:message
                                                primary:@"Validation error"
                                              secondary:message];
                return;
            }

            NSNumber *fileSize = attributes[NSFileSize];
            if (fileSize && fileSize.longLongValue != expectedBytes) {
                NSString *message = [NSString stringWithFormat:@"Partial download detected for %@ (expected %lld bytes, got %lld bytes).",
                                      relativePath,
                                      expectedBytes,
                                      fileSize.longLongValue];
                [strongSelf finalizeDownloadWithSuccess:NO
                                                message:message
                                                primary:@"Partial download"
                                              secondary:message];
                return;
            }
        }

        UIImage *thumbnail = nil;
        NSString *lowercaseExtension = destinationURL.pathExtension.lowercaseString;
        NSSet<NSString *> *imageExtensions = [NSSet setWithArray:@[@"jpg", @"jpeg", @"png", @"bmp", @"gif", @"heic", @"heif"]];
        if ([imageExtensions containsObject:lowercaseExtension]) {
            thumbnail = [UIImage imageWithContentsOfFile:destinationURL.path];
        }

        strongSelf.latestDownloadedFileName = destinationURL.lastPathComponent;
        strongSelf.latestDownloadedImage = thumbnail;

        NSUInteger finishedCount = strongSelf.currentDownloadIndex + 1;
        NSUInteger totalCount = strongSelf.mediaFiles.count;
        [strongSelf updateDownloadUIWithPrimary:[NSString stringWithFormat:@"Downloaded %lu of %lu",
                                                (unsigned long)finishedCount,
                                                (unsigned long)totalCount]
                                      secondary:[NSString stringWithFormat:@"Last file: %@", strongSelf.latestDownloadedFileName ?: relativePath]
                                 latestFileName:strongSelf.latestDownloadedFileName
                                      latestImage:thumbnail];

        strongSelf.currentDownloadIndex += 1;

        dispatch_async(dispatch_get_main_queue(), ^{
            [strongSelf downloadNextMediaFile];
        });
    }];

    [self.currentDownloadTask resume];
}

- (void)finalizeDownloadWithSuccess:(BOOL)success message:(NSString *)message primary:(NSString *)primary secondary:(NSString *)secondary {
    [self stopDeviceWifiIfNeeded];
    [self cleanupDownloadResources];

    [self updateDownloadUIWithPrimary:primary
                              secondary:secondary
                         latestFileName:self.latestDownloadedFileName
                              latestImage:self.latestDownloadedImage];

    if (message.length > 0) {
        NSString *title = success ? @"Download Complete" : @"Download Failed";
        [self presentDownloadAlertWithTitle:title message:message];
    }
}

- (void)cleanupDownloadResources {
    if (self.configTask) {
        [self.configTask cancel];
        self.configTask = nil;
    }

    if (self.currentDownloadTask) {
        [self.currentDownloadTask cancel];
        self.currentDownloadTask = nil;
    }

    if (self.mediaSession) {
        [self.mediaSession invalidateAndCancel];
        self.mediaSession = nil;
    }

    self.mediaFiles = nil;
    self.currentDownloadIndex = 0;
    self.deviceIPAddress = nil;
    self.deviceWifiSSID = nil;
    self.deviceWifiPassword = nil;
    self.isDownloadingMedia = NO;
    self.didEnableWifiForDownload = NO;
}

- (void)stopDeviceWifiIfNeeded {
    if (!self.didEnableWifiForDownload) {
        return;
    }

    self.didEnableWifiForDownload = NO;
    [QCSDKCmdCreator setDeviceMode:QCOperatorDeviceModeTransferStop success:^{} fail:^(NSInteger mode) {
        NSLog(@"Failed to stop transfer mode: %ld", (long)mode);
    }];
}

- (void)updateDownloadUIWithPrimary:(NSString *)primary
                            secondary:(NSString * _Nullable)secondary
                       latestFileName:(NSString * _Nullable)fileName
                            latestImage:(UIImage * _Nullable)image {
    dispatch_async(dispatch_get_main_queue(), ^{
        if (primary) {
            self.downloadPrimaryStatus = primary;
        }
        self.downloadSecondaryStatus = secondary;
        if (fileName) {
            self.latestDownloadedFileName = fileName;
        }
        if (image || (!image && fileName)) {
            self.latestDownloadedImage = image;
        }

        [self.tableView reloadData];
    });
}

- (void)presentDownloadAlertWithTitle:(NSString *)title message:(NSString *)message {
    dispatch_async(dispatch_get_main_queue(), ^{
        UIAlertController *alert = [UIAlertController alertControllerWithTitle:title
                                                                       message:message
                                                                preferredStyle:UIAlertControllerStyleAlert];
        [alert addAction:[UIAlertAction actionWithTitle:@"OK" style:UIAlertActionStyleDefault handler:nil]];
        [self presentViewController:alert animated:YES completion:nil];
    });
}

- (NSURL *)mediaDirectoryURL {
    if (_mediaDirectoryURL) {
        return _mediaDirectoryURL;
    }

    NSError *error = nil;
    NSURL *documentsDirectory = [[NSFileManager defaultManager] URLForDirectory:NSDocumentDirectory
                                                                       inDomain:NSUserDomainMask
                                                              appropriateForURL:nil
                                                                         create:YES
                                                                          error:&error];
    if (error) {
        NSLog(@"Failed to resolve documents directory: %@", error.localizedDescription);
        return nil;
    }

    NSURL *mediaDirectory = [documentsDirectory URLByAppendingPathComponent:@"GlassesMedia" isDirectory:YES];
    if (![[NSFileManager defaultManager] fileExistsAtPath:mediaDirectory.path]) {
        [[NSFileManager defaultManager] createDirectoryAtURL:mediaDirectory
                                 withIntermediateDirectories:YES
                                                  attributes:nil
                                                       error:&error];
        if (error) {
            NSLog(@"Failed to create GlassesMedia directory: %@", error.localizedDescription);
        }
    }

    _mediaDirectoryURL = mediaDirectory;
    return _mediaDirectoryURL;
}

@end
