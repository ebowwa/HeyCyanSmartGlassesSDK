# Audio HAL Recording Behavior

## What Actually Happens When You Record

### 📱 **Phone Recording (Local)**
- **Location**: iPhone's microphone → iPhone storage
- **File Path**: `/Documents/recording_[UUID].m4a`
- **This is what you can PLAYBACK** ✅

### 🕶️ **Glasses Recording (Remote)**
- **Location**: Glasses microphone → Glasses storage
- **Command**: Sent via `QCSDKCmdCreator setDeviceMode:QCOperatorDeviceModeAudio`
- **This is NOT accessible for immediate playback** ❌

## Current Implementation Issues

### ✅ **What Works:**
1. iPhone records locally using `AVAudioRecorder`
2. File is saved to Documents directory
3. File URL is stored for playback

### ❌ **What Might Not Work:**

1. **Audio Permissions**: App needs microphone permission
2. **Audio Session**: May conflict between recording and playback
3. **File Format**: M4A might not play on all devices
4. **Glasses Connection**: Glasses commands might fail if not connected

## Debug Steps

### 1. Check Console Output
Look for these logs when testing:
```
🎤 HAL: Starting LOCAL phone recording...
🎤 HAL: Recording to: [file path]
🎤 HAL: Local recording started: YES/NO
🕶️ HAL: SUCCESS/FAILED - Glasses audio recording
🎤 HAL: Recording saved! File size: [bytes]
🔊 HAL: File exists, size: [bytes]
🔊 HAL: Audio player created successfully, duration: [seconds]
🔊 HAL: Audio playback started: YES/NO
```

### 2. Permission Check
Add to Info.plist:
```xml
<key>NSMicrophoneUsageDescription</key>
<string>This app needs microphone access to record audio</string>
```

### 3. Audio Session Issues
The HAL tries to switch between:
- **Recording Session**: `AVAudioSessionCategoryRecord`
- **Playback Session**: `AVAudioSessionCategoryPlayback`

This switching might cause issues.

## Recommended Usage

For now, this HAL does:
- ✅ **Records audio locally on iPhone**
- ✅ **Sends commands to glasses to record**
- ✅ **Can playback LOCAL iPhone recordings**
- ❌ **Cannot playback glasses recordings directly**

To access glasses recordings, you'd need to:
1. Use WiFi transfer mode
2. Download files from glasses via HTTP
3. Then play downloaded files