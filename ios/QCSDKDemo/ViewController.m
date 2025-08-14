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
#import "WiFiMediaBrowser.h"
#import "WiFiAutoConnect.h"
#import "Modules/HAL/Audio/QCAudioHAL.h"

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
    
    /// Open WiFi Hotspot and Start Transfer Mode
    QGDeviceActionTypeOpenWiFi,
    
    /// Get WiFi IP Address
    QGDeviceActionTypeGetWiFiIP,
    
    /// Delete All Media Files
    QGDeviceActionTypeDeleteAllMedia,
    
    /// Browse Media Files
    QGDeviceActionTypeBrowseMedia,
    
    /// Play Last Recorded Audio
    QGDeviceActionTypePlayRecordedAudio,

    /// Reserved for future use
    QGDeviceActionTypeReserved,
};



@interface ViewController ()<UITableViewDelegate, UITableViewDataSource,QCCentralManagerDelegate,QCSDKManagerDelegate,QCMicrophoneHALDelegate,QCSpeakerHALDelegate>

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
@property(nonatomic,strong)NSURL *lastRecordedAudioURL;
@property(nonatomic,assign)BOOL playingAudio;

@property(nonatomic,strong)NSData *aiImageData;

@property(nonatomic,copy)NSString *wifiSSID;
@property(nonatomic,copy)NSString *wifiPassword;
@property(nonatomic,copy)NSString *wifiIPAddress;
@property(nonatomic,assign)BOOL isTransferMode;
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
    // Using the new Audio HAL
    QCAudioHAL *audioHAL = [QCAudioHAL sharedInstance];
    QCMicrophoneHAL *microphone = audioHAL.microphone;
    microphone.delegate = self;
    
    if (self.recordingAudio) {
        // Stop recording using HAL
        [microphone stopRecording];
        self.recordingAudio = NO;
        [self.tableView reloadData];
        
        // Also send command to glasses to stop
        [QCSDKCmdCreator setDeviceMode:(QCOperatorDeviceModeAudioStop) success:^{
            NSLog(@"Audio recording stopped on glasses");
        } fail:^(NSInteger mode) {
            NSLog(@"Failed to stop audio on glasses, current device model:%zd",mode);
        }];
    } else {
        // Initialize and start recording using HAL
        [microphone initializeMicrophone];
        [microphone startRecording];
        self.recordingAudio = YES;
        [self.tableView reloadData];
        
        // Also send command to glasses to start
        [QCSDKCmdCreator setDeviceMode:(QCOperatorDeviceModeAudio) success:^{
            NSLog(@"Audio recording started on glasses");
        } fail:^(NSInteger mode) {
            NSLog(@"Failed to start audio on glasses, current device model:%zd",mode);
        }];
    }
}

- (void)playRecordedAudio {
    if (!self.lastRecordedAudioURL) {
        UIAlertController *alert = [UIAlertController alertControllerWithTitle:@"No Recording"
                                                                     message:@"Please record audio first"
                                                              preferredStyle:UIAlertControllerStyleAlert];
        [alert addAction:[UIAlertAction actionWithTitle:@"OK" style:UIAlertActionStyleDefault handler:nil]];
        [self presentViewController:alert animated:YES completion:nil];
        return;
    }
    
    QCAudioHAL *audioHAL = [QCAudioHAL sharedInstance];
    QCSpeakerHAL *speaker = audioHAL.speaker;
    speaker.delegate = self;
    
    if (self.playingAudio) {
        // Stop playback
        [speaker stopPlayback];
        self.playingAudio = NO;
        [self.tableView reloadData];
    } else {
        // Start playback
        [speaker initializeSpeaker];
        [speaker playAudioFromURL:self.lastRecordedAudioURL];
        self.playingAudio = YES;
        [self.tableView reloadData];
    }
}

- (void)takeAIImage {
    //- (void)didReceiveAIChatImageData:(NSData *)imageData
    [QCSDKCmdCreator setDeviceMode:(QCOperatorDeviceModeAIPhoto) success:^{
        
    } fail:^(NSInteger mode) {
        NSLog(@"set fail,current device model:%zd",mode);
    }];
}

- (void)openWifiTransferMode {
    // Alias for openWiFiHotspot for clarity
    [self openWiFiHotspot];
}

- (void)openWiFiHotspot {
    NSLog(@"Opening WiFi Hotspot...");
    [QCSDKCmdCreator openWifiWithMode:QCOperatorDeviceModeTransfer success:^(NSString *ssid, NSString *password) {
        NSLog(@"WiFi Hotspot Opened Successfully!");
        NSLog(@"SSID: %@", ssid);
        NSLog(@"Password: %@", password);
        
        self.wifiSSID = ssid;
        self.wifiPassword = password;
        
        // Automatically get IP address after WiFi opens
        dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(1.0 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
            [self getWiFiIPAddress];
        });
        
        dispatch_async(dispatch_get_main_queue(), ^{
            [self.tableView reloadData];
            
            // Just auto-connect immediately like the official app
            [WiFiAutoConnect connectToWiFiWithSSID:ssid password:password completion:^(BOOL success, NSError * _Nullable error) {
                if (success || error.code == 13) { // Code 13 = already connected
                    NSLog(@"WiFi connection successful or already connected");
                    
                    // Wait for connection to establish then open browser
                    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(3.0 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
                        WiFiMediaBrowser *browser = [[WiFiMediaBrowser alloc] init];
                        browser.deviceIPAddress = self.wifiIPAddress;
                        browser.wifiSSID = self.wifiSSID;
                        browser.wifiPassword = self.wifiPassword;
                        [self.navigationController pushViewController:browser animated:YES];
                    });
                } else {
                    NSLog(@"WiFi connection failed: %@", error.localizedDescription);
                    
                    // Simple fallback alert
                    UIAlertController *alert = [UIAlertController alertControllerWithTitle:@"Connection Required"
                                                                                 message:@"Please connect to the glasses WiFi network"
                                                                          preferredStyle:UIAlertControllerStyleAlert];
                    [alert addAction:[UIAlertAction actionWithTitle:@"OK" style:UIAlertActionStyleDefault handler:nil]];
                    [self presentViewController:alert animated:YES completion:nil];
                }
            }];
        });
    } fail:^(NSInteger errorCode) {
        NSLog(@"Failed to open WiFi Hotspot. Error code: %zd", errorCode);
        
        dispatch_async(dispatch_get_main_queue(), ^{
            UIAlertController *alert = [UIAlertController alertControllerWithTitle:@"WiFi Error"
                                                                         message:[NSString stringWithFormat:@"Failed to open WiFi hotspot (Error: %zd)", errorCode]
                                                                  preferredStyle:UIAlertControllerStyleAlert];
            [alert addAction:[UIAlertAction actionWithTitle:@"OK" style:UIAlertActionStyleDefault handler:nil]];
            [self presentViewController:alert animated:YES completion:nil];
        });
    }];
}

- (void)getWiFiIPAddress {
    NSLog(@"Getting WiFi IP Address...");
    [QCSDKCmdCreator getDeviceWifiIPSuccess:^(NSString * _Nullable ipAddress) {
        if (ipAddress) {
            NSLog(@"WiFi IP Address: %@", ipAddress);
            self.wifiIPAddress = ipAddress;
            
            dispatch_async(dispatch_get_main_queue(), ^{
                [self.tableView reloadData];
                
                UIAlertController *alert = [UIAlertController alertControllerWithTitle:@"WiFi IP Address"
                                                                             message:ipAddress
                                                                      preferredStyle:UIAlertControllerStyleAlert];
                [alert addAction:[UIAlertAction actionWithTitle:@"OK" style:UIAlertActionStyleDefault handler:nil]];
                [self presentViewController:alert animated:YES completion:nil];
            });
        } else {
            NSLog(@"No WiFi IP Address available");
        }
    } failed:^{
        NSLog(@"Failed to get WiFi IP Address");
    }];
}

- (void)deleteAllMediaFiles {
    UIAlertController *confirmAlert = [UIAlertController alertControllerWithTitle:@"Delete All Media?"
                                                                         message:@"This will permanently delete all photos, videos, and audio files from the device."
                                                                  preferredStyle:UIAlertControllerStyleAlert];
    
    [confirmAlert addAction:[UIAlertAction actionWithTitle:@"Cancel" style:UIAlertActionStyleCancel handler:nil]];
    [confirmAlert addAction:[UIAlertAction actionWithTitle:@"Delete All" style:UIAlertActionStyleDestructive handler:^(UIAlertAction * _Nonnull action) {
        NSLog(@"Deleting all media files...");
        [QCSDKCmdCreator deleleteAllMediasSuccess:^{
            NSLog(@"All media files deleted successfully");
            
            self.photoCount = 0;
            self.videoCount = 0;
            self.audioCount = 0;
            
            dispatch_async(dispatch_get_main_queue(), ^{
                [self.tableView reloadData];
                
                UIAlertController *alert = [UIAlertController alertControllerWithTitle:@"Success"
                                                                             message:@"All media files have been deleted"
                                                                      preferredStyle:UIAlertControllerStyleAlert];
                [alert addAction:[UIAlertAction actionWithTitle:@"OK" style:UIAlertActionStyleDefault handler:nil]];
                [self presentViewController:alert animated:YES completion:nil];
            });
        } fail:^{
            NSLog(@"Failed to delete media files");
            
            dispatch_async(dispatch_get_main_queue(), ^{
                UIAlertController *alert = [UIAlertController alertControllerWithTitle:@"Error"
                                                                             message:@"Failed to delete media files"
                                                                      preferredStyle:UIAlertControllerStyleAlert];
                [alert addAction:[UIAlertAction actionWithTitle:@"OK" style:UIAlertActionStyleDefault handler:nil]];
                [self presentViewController:alert animated:YES completion:nil];
            });
        }];
    }]];
    
    [self presentViewController:confirmAlert animated:YES completion:nil];
}

/* Removed - now using simplified WiFi flow
- (void)startMediaTransferMode {
    if (self.isTransferMode) {
        NSLog(@"Stopping Media Transfer Mode...");
        // Stop transfer mode
        [QCSDKCmdCreator setDeviceMode:QCOperatorDeviceModeTransferStop success:^{
            NSLog(@"Media Transfer Mode stopped successfully");
            self.isTransferMode = NO;
            self.wifiSSID = nil;
            self.wifiPassword = nil;
            self.wifiIPAddress = nil;
            
            dispatch_async(dispatch_get_main_queue(), ^{
                [self.tableView reloadData];
                
                UIAlertController *alert = [UIAlertController alertControllerWithTitle:@"Transfer Mode Stopped"
                                                                             message:@"Media transfer mode has been disabled"
                                                                      preferredStyle:UIAlertControllerStyleAlert];
                [alert addAction:[UIAlertAction actionWithTitle:@"OK" style:UIAlertActionStyleDefault handler:nil]];
                [self presentViewController:alert animated:YES completion:nil];
            });
        } fail:^(NSInteger errorCode) {
            NSLog(@"Failed to stop transfer mode. Error: %zd", errorCode);
            dispatch_async(dispatch_get_main_queue(), ^{
                UIAlertController *alert = [UIAlertController alertControllerWithTitle:@"Error"
                                                                             message:[NSString stringWithFormat:@"Failed to stop transfer mode (Error: %zd)", errorCode]
                                                                      preferredStyle:UIAlertControllerStyleAlert];
                [alert addAction:[UIAlertAction actionWithTitle:@"OK" style:UIAlertActionStyleDefault handler:nil]];
                [self presentViewController:alert animated:YES completion:nil];
            });
        }];
    } else {
        NSLog(@"Starting Media Transfer Mode...");
        // First ensure we have WiFi info
        if (!self.wifiSSID || !self.wifiIPAddress) {
            UIAlertController *alert = [UIAlertController alertControllerWithTitle:@"Setup Required"
                                                                         message:@"Please open WiFi Hotspot first, then start transfer mode"
                                                                  preferredStyle:UIAlertControllerStyleAlert];
            [alert addAction:[UIAlertAction actionWithTitle:@"Open WiFi" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
                [self openWiFiHotspot];
            }]];
            [alert addAction:[UIAlertAction actionWithTitle:@"Cancel" style:UIAlertActionStyleCancel handler:nil]];
            [self presentViewController:alert animated:YES completion:nil];
            return;
        }
        
        // Start transfer mode
        [QCSDKCmdCreator setDeviceMode:QCOperatorDeviceModeTransfer success:^{
            NSLog(@"Media Transfer Mode started successfully");
            self.isTransferMode = YES;
            
            dispatch_async(dispatch_get_main_queue(), ^{
                [self.tableView reloadData];
                
                UIAlertController *alert = [UIAlertController alertControllerWithTitle:@"Transfer Mode Active"
                                                                             message:@"Device is now in media transfer mode. You can access media files via WiFi."
                                                                      preferredStyle:UIAlertControllerStyleAlert];
                [alert addAction:[UIAlertAction actionWithTitle:@"Open Media Browser" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
                    WiFiMediaBrowser *browser = [[WiFiMediaBrowser alloc] init];
                    browser.deviceIPAddress = self.wifiIPAddress;
                    browser.wifiSSID = self.wifiSSID;
                    browser.wifiPassword = self.wifiPassword;
                    [self.navigationController pushViewController:browser animated:YES];
                }]];
                [alert addAction:[UIAlertAction actionWithTitle:@"Later" style:UIAlertActionStyleCancel handler:nil]];
                [self presentViewController:alert animated:YES completion:nil];
            });
        } fail:^(NSInteger errorCode) {
            NSLog(@"Failed to start transfer mode. Error: %zd", errorCode);
            
            dispatch_async(dispatch_get_main_queue(), ^{
                UIAlertController *alert = [UIAlertController alertControllerWithTitle:@"Error"
                                                                             message:[NSString stringWithFormat:@"Failed to start transfer mode (Error: %zd)", errorCode]
                                                                      preferredStyle:UIAlertControllerStyleAlert];
                [alert addAction:[UIAlertAction actionWithTitle:@"OK" style:UIAlertActionStyleDefault handler:nil]];
                [self presentViewController:alert animated:YES completion:nil];
            });
        }];
    }
}
*/

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
    // Show play audio option only if we have a recording
    if (self.lastRecordedAudioURL) {
        return QGDeviceActionTypeReserved;
    } else {
        return QGDeviceActionTypePlayRecordedAudio; // Don't show play option if no recording
    }
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
        case QGDeviceActionTypeOpenWiFi:
            cell.textLabel.text = self.wifiSSID ? @"WiFi Hotspot Active" : @"Open WiFi Hotspot";
            if (self.wifiSSID) {
                cell.detailTextLabel.text = [NSString stringWithFormat:@"SSID: %@\nPassword: %@\nIP: %@", 
                                            self.wifiSSID, self.wifiPassword, 
                                            self.wifiIPAddress ?: @"Getting IP..."];
            } else {
                cell.detailTextLabel.text = @"Enable WiFi for media transfer";
            }
            break;
        case QGDeviceActionTypeGetWiFiIP:
            cell.textLabel.text = @"Refresh WiFi IP Address";
            if (self.wifiIPAddress) {
                cell.detailTextLabel.text = self.wifiIPAddress;
            } else {
                cell.detailTextLabel.text = @"Tap to get IP address";
            }
            break;
        case QGDeviceActionTypeDeleteAllMedia:
            cell.textLabel.text = @"Delete All Media Files";
            cell.detailTextLabel.text = @"⚠️ This will permanently delete all media";
            break;
        case QGDeviceActionTypeBrowseMedia:
            cell.textLabel.text = @"Browse Media Files";
            if (self.wifiSSID && self.wifiIPAddress) {
                cell.detailTextLabel.text = @"Access media via WiFi";
            } else {
                cell.detailTextLabel.text = @"Open WiFi hotspot first";
            }
            break;
        case QGDeviceActionTypePlayRecordedAudio:
            if (self.lastRecordedAudioURL) {
                cell.textLabel.text = self.playingAudio ? @"Stop Audio Playback" : @"Play Last Recording";
                cell.detailTextLabel.text = self.lastRecordedAudioURL.lastPathComponent;
            } else {
                cell.textLabel.text = @"No Recording Available";
                cell.detailTextLabel.text = @"Record audio first";
            }
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
        case QGDeviceActionTypeOpenWiFi:
            if (self.wifiSSID) {
                // WiFi already open, ask what to do
                UIAlertController *alert = [UIAlertController alertControllerWithTitle:@"WiFi Already Active"
                                                                             message:[NSString stringWithFormat:@"SSID: %@\nPassword: %@", self.wifiSSID, self.wifiPassword]
                                                                      preferredStyle:UIAlertControllerStyleActionSheet];
                [alert addAction:[UIAlertAction actionWithTitle:@"Close WiFi" style:UIAlertActionStyleDestructive handler:^(UIAlertAction * _Nonnull action) {
                    // Note: SDK may not have a close WiFi method, just clear our state
                    self.wifiSSID = nil;
                    self.wifiPassword = nil;
                    self.wifiIPAddress = nil;
                    self.isTransferMode = NO;
                    [self.tableView reloadData];
                }]];
                [alert addAction:[UIAlertAction actionWithTitle:@"Refresh" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
                    [self openWiFiHotspot];
                }]];
                [alert addAction:[UIAlertAction actionWithTitle:@"Cancel" style:UIAlertActionStyleCancel handler:nil]];
                [self presentViewController:alert animated:YES completion:nil];
            } else {
                [self openWiFiHotspot];
            }
            break;
        case QGDeviceActionTypeGetWiFiIP:
            [self getWiFiIPAddress];
            break;
        case QGDeviceActionTypeDeleteAllMedia:
            [self deleteAllMediaFiles];
            break;
        case QGDeviceActionTypeBrowseMedia:
            if (self.wifiSSID && self.wifiIPAddress) {
                WiFiMediaBrowser *browser = [[WiFiMediaBrowser alloc] init];
                browser.deviceIPAddress = self.wifiIPAddress;
                browser.wifiSSID = self.wifiSSID;
                browser.wifiPassword = self.wifiPassword;
                [self.navigationController pushViewController:browser animated:YES];
            } else {
                UIAlertController *alert = [UIAlertController alertControllerWithTitle:@"Setup Required"
                                                                             message:@"Please open WiFi Hotspot first"
                                                                      preferredStyle:UIAlertControllerStyleAlert];
                [alert addAction:[UIAlertAction actionWithTitle:@"Open WiFi" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
                    [self openWiFiHotspot];
                }]];
                [alert addAction:[UIAlertAction actionWithTitle:@"Cancel" style:UIAlertActionStyleCancel handler:nil]];
                [self presentViewController:alert animated:YES completion:nil];
            }
            break;
        case QGDeviceActionTypePlayRecordedAudio:
            [self playRecordedAudio];
            break;
        case QGDeviceActionTypeReserved:
        default:
            break;
    }

}

#pragma mark - QCMicrophoneHALDelegate

- (void)microphoneDidStartRecording {
    NSLog(@"HAL: Microphone started recording");
}

- (void)microphoneDidStopRecording:(NSURL *)audioURL {
    NSLog(@"HAL: Glasses recording stopped. Now checking for WiFi to download...");
    
    // Since we're recording on glasses only, audioURL will be nil
    // We need to get the glasses IP and download the file
    
    // First, check if WiFi is available
    [QCSDKCmdCreator getDeviceWifiIPSuccess:^(NSString * _Nullable ipAddress) {
        if (ipAddress && ipAddress.length > 0) {
            NSLog(@"HAL: Got glasses IP: %@, starting download...", ipAddress);
            
            // Download the recording from glasses
            QCMicrophoneHAL *microphone = [QCAudioHAL sharedInstance].microphone;
            [microphone downloadLastRecordingFromGlasses:ipAddress completion:^(NSURL * _Nullable localURL, NSError * _Nullable error) {
                if (error) {
                    NSLog(@"HAL: Download failed: %@", error.localizedDescription);
                    dispatch_async(dispatch_get_main_queue(), ^{
                        UIAlertController *alert = [UIAlertController alertControllerWithTitle:@"Download Failed"
                                                                                     message:[NSString stringWithFormat:@"Could not download audio from glasses: %@", error.localizedDescription]
                                                                              preferredStyle:UIAlertControllerStyleAlert];
                        [alert addAction:[UIAlertAction actionWithTitle:@"OK" style:UIAlertActionStyleDefault handler:nil]];
                        [self presentViewController:alert animated:YES completion:nil];
                    });
                } else if (localURL) {
                    NSLog(@"HAL: Download successful! File at: %@", localURL.path);
                    
                    // Save the URL for playback
                    self.lastRecordedAudioURL = localURL;
                    
                    // Show success alert
                    dispatch_async(dispatch_get_main_queue(), ^{
                        [self.tableView reloadData]; // Update table to show play option
                        
                        UIAlertController *alert = [UIAlertController alertControllerWithTitle:@"Audio Downloaded"
                                                                                     message:@"Glasses audio downloaded successfully!\nYou can now play it back!"
                                                                              preferredStyle:UIAlertControllerStyleAlert];
                        [alert addAction:[UIAlertAction actionWithTitle:@"Play Now" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
                            [self playRecordedAudio];
                        }]];
                        [alert addAction:[UIAlertAction actionWithTitle:@"OK" style:UIAlertActionStyleCancel handler:nil]];
                        [self presentViewController:alert animated:YES completion:nil];
                    });
                }
            }];
        } else {
            NSLog(@"HAL: No WiFi IP available. Need to enable WiFi transfer mode first.");
            
            dispatch_async(dispatch_get_main_queue(), ^{
                UIAlertController *alert = [UIAlertController alertControllerWithTitle:@"WiFi Required"
                                                                             message:@"Recording saved on glasses.\nTo download and play, please:\n1. Enable WiFi Transfer mode\n2. Connect to glasses WiFi\n3. Try downloading again"
                                                                      preferredStyle:UIAlertControllerStyleAlert];
                [alert addAction:[UIAlertAction actionWithTitle:@"Enable WiFi Transfer" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
                    // Enable WiFi transfer mode
                    [self openWifiTransferMode];
                }]];
                [alert addAction:[UIAlertAction actionWithTitle:@"Later" style:UIAlertActionStyleCancel handler:nil]];
                [self presentViewController:alert animated:YES completion:nil];
            });
        }
    } failed:^{
        NSLog(@"HAL: Failed to get WiFi IP. Recording is saved on glasses but cannot download.");
        
        dispatch_async(dispatch_get_main_queue(), ^{
            UIAlertController *alert = [UIAlertController alertControllerWithTitle:@"Recording Saved on Glasses"
                                                                         message:@"Audio recorded on glasses.\nEnable WiFi Transfer mode to download and play."
                                                                  preferredStyle:UIAlertControllerStyleAlert];
            [alert addAction:[UIAlertAction actionWithTitle:@"OK" style:UIAlertActionStyleDefault handler:nil]];
            [self presentViewController:alert animated:YES completion:nil];
        });
    }];
}

- (void)microphoneDidUpdateLevel:(Float32)level {
    // Could update UI with audio level meter
    NSLog(@"HAL: Audio level: %.2f", level);
}

- (void)microphoneDidEncounterError:(NSError *)error {
    NSLog(@"HAL: Microphone error: %@", error.localizedDescription);
    
    dispatch_async(dispatch_get_main_queue(), ^{
        UIAlertController *alert = [UIAlertController alertControllerWithTitle:@"Recording Error"
                                                                     message:error.localizedDescription
                                                              preferredStyle:UIAlertControllerStyleAlert];
        [alert addAction:[UIAlertAction actionWithTitle:@"OK" style:UIAlertActionStyleDefault handler:nil]];
        [self presentViewController:alert animated:YES completion:nil];
    });
}

#pragma mark - QCSpeakerHALDelegate

- (void)speakerDidStartPlayback {
    NSLog(@"HAL: Speaker started playback");
    self.playingAudio = YES;
    dispatch_async(dispatch_get_main_queue(), ^{
        [self.tableView reloadData];
    });
}

- (void)speakerDidFinishPlayback {
    NSLog(@"HAL: Speaker finished playback");
    self.playingAudio = NO;
    dispatch_async(dispatch_get_main_queue(), ^{
        [self.tableView reloadData];
        
        UIAlertController *alert = [UIAlertController alertControllerWithTitle:@"Playback Complete"
                                                                     message:@"Audio playback finished"
                                                              preferredStyle:UIAlertControllerStyleAlert];
        [alert addAction:[UIAlertAction actionWithTitle:@"OK" style:UIAlertActionStyleDefault handler:nil]];
        [self presentViewController:alert animated:YES completion:nil];
    });
}

- (void)speakerDidPausePlayback {
    NSLog(@"HAL: Speaker paused playback");
}

- (void)speakerDidEncounterError:(NSError *)error {
    NSLog(@"HAL: Speaker error: %@", error.localizedDescription);
    self.playingAudio = NO;
    
    dispatch_async(dispatch_get_main_queue(), ^{
        [self.tableView reloadData];
        
        UIAlertController *alert = [UIAlertController alertControllerWithTitle:@"Playback Error"
                                                                     message:error.localizedDescription
                                                              preferredStyle:UIAlertControllerStyleAlert];
        [alert addAction:[UIAlertAction actionWithTitle:@"OK" style:UIAlertActionStyleDefault handler:nil]];
        [self presentViewController:alert animated:YES completion:nil];
    });
}

@end
