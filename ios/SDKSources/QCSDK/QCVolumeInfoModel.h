//
//  QCVolumeInfoModel.h
//  QCSDK
//
//  Created by steve on 2025/7/22.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

/// Enum representing different volume modes.
typedef NS_ENUM(NSInteger, QCVolumeMode) {
    QCVolumeModeMusic   = 0x01,  ///< Music volume mode
    QCVolumeModeCall    = 0x02,  ///< Call volume mode
    QCVolumeModeSystem  = 0x03   ///< System volume mode
};

/// Model representing volume information for music, call, and system modes.
@interface QCVolumeInfoModel : NSObject

/// Minimum volume level for music mode.
@property (nonatomic, assign) NSInteger musicMin;

/// Maximum volume level for music mode.
@property (nonatomic, assign) NSInteger musicMax;

/// Current volume level for music mode.
@property (nonatomic, assign) NSInteger musicCurrent;

/// Minimum volume level for call mode.
@property (nonatomic, assign) NSInteger callMin;

/// Maximum volume level for call mode.
@property (nonatomic, assign) NSInteger callMax;

/// Current volume level for call mode.
@property (nonatomic, assign) NSInteger callCurrent;

/// Minimum volume level for system mode.
@property (nonatomic, assign) NSInteger systemMin;

/// Maximum volume level for system mode.
@property (nonatomic, assign) NSInteger systemMax;

/// Current volume level for system mode.
@property (nonatomic, assign) NSInteger systemCurrent;

/// The current volume mode (music, call, or system).
@property (nonatomic, assign) QCVolumeMode mode;

@end


NS_ASSUME_NONNULL_END
