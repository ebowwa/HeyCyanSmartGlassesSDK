# Issue: Expand Remote App Support for Glasses Mobile Connection

## Summary
The SDK already exposes a wide range of device operations that the mobile companion app can drive remotely, but the current remote app experience only surfaces basic capture controls. We should expand the remote implementation to cover the full set of capabilities so operators can manage the glasses without reaching for the hardware.

## Requested Enhancements
### 1. Operator mode coverage
Update the remote controller UI and command handling so users can trigger every operator mode defined in `QCOperatorDeviceMode`. Besides photo/video control, the SDK supports OTA, AI capture, speech, audio, factory reset, device finding, P2P restart, voice playback, and translation controls that are currently missing from the remote app.
- `QCOperatorDeviceModeOTA` — enter firmware update flow.
- `QCOperatorDeviceModeAIPhoto` — start AI-enhanced photo capture.
- `QCOperatorDeviceModeSpeechRecognition` / `SpeechRecognitionStop` — toggle live speech services.
- `QCOperatorDeviceModeAudio` / `AudioStop` — manage audio recording sessions.
- `QCOperatorDeviceModeFactoryReset` — initiate remote factory reset with safeguards.
- `QCOperatorDeviceModeFindDevice` — trigger audible/visual locator to find misplaced glasses.
- `QCOperatorDeviceModeRestart` / `NoPowerP2P` — restart pipelines including low-power P2P reset.
- `QCOperatorDeviceModeSpeakStart` / `SpeakStop` — start and stop voice playback prompts.
- `QCOperatorDeviceModeTranslateStart` / `TranslateStop` — toggle translation mode.

Reference: [`QCOperatorDeviceMode` enum in QCDFU_Utils.h][operator-mode].

### 2. OTA and configuration utilities
Expose the OTA/config APIs already present in `QCSDKCmdCreator` so the remote app can manage firmware updates and device configuration without a separate workflow.
- Send OTA download links and switch to single-band DFU when needed.
- Sync device time from the phone.
- Fetch thumbnails and other media status for remote review.
- Maintain the voice heartbeat to prevent speech features from timing out.
- Read and toggle voice wake-up and wearing detection.
- Fetch device configuration snapshots.
- Select AI speaking models for voice responses.
- Control Bluetooth power state and read the MAC address when diagnosing issues.

Reference: [OTA/config/misc commands in QCSDKCmdCreator.h][ota-config].

### 3. Remote audio management
Add UI/logic so the remote app can adjust the glasses’ volume per the `QCVolumeInfoModel`. The SDK lets the phone set explicit modes for Music, Call, and System mixes, giving operators precise control depending on the scenario.

Reference: [`QCVolumeMode` enum in QCVolumeInfoModel.h][volume-mode].

## Acceptance Criteria
- Remote app exposes UI affordances (buttons, toggles, flows) for each SDK operator mode and safeguards destructive actions (factory reset, restarts).
- OTA firmware management, configuration toggles, and diagnostic reads can be initiated and monitored from the remote app.
- Volume adjustments let users pick the target mix (Music/Call/System) and push levels reliably.
- QA coverage ensures each command path is exercised against the glasses hardware or simulator.

[operator-mode]: ../QCSDK.framework/Headers/QCDFU_Utils.h
[ota-config]: ../QCSDK.framework/Headers/QCSDKCmdCreator.h
[volume-mode]: ../QCSDK.framework/Headers/QCVolumeInfoModel.h
