# Wi-Fi Hotspot Configuration on iOS

This guide summarizes Apple's supported approach for prompting users to join a known Wi-Fi network from within an iOS application. It also clarifies unsupported behaviors so you can plan onboarding flows without relying on private APIs.

## Supported and Unsupported Actions

- ✅ **Prompt to join a known SSID** (passphrase or EAP) using [`NEHotspotConfigurationManager.apply(_:)`](https://developer.apple.com/documentation/networkextension/wi-fi-configuration?utm_source=chatgpt.com). iOS presents a system sheet and, when credentials are valid and the network is in range, connects automatically.
- ✅ **One-shot vs persistent connections**: set [`joinOnce`](https://developer.apple.com/documentation/networkextension/nehotspotconfiguration/joinonce?utm_source=chatgpt.com) to `true` to keep the association while your app is in the foreground. Leaving it `false` persists the configuration in system settings.
- ❌ **Enabling/disabling Personal Hotspot** programmatically is not possible. There is no approved public API for creating or toggling the iPhone hotspot from your app.
- ⚠️ **`NEHotspotHelper`** requires a rarely granted entitlement that is typically reserved for carriers or large hotspot providers. Do not plan on App Store approval when using it.
- ⚠️ **Reading the current SSID** via `CNCopyCurrentNetworkInfo` is limited and requires the *Access Wi-Fi Information* entitlement. Treat it as unreliable for real-time UX decisions.

## Minimum Capabilities and Setup

1. Add the **Hotspot Configuration** capability (`com.apple.developer.networking.HotspotConfiguration`) to your app target to unlock `NEHotspotConfigurationManager` APIs.
2. Provide the SSID and passphrase (or full EAP configuration). The API cannot perform discovery or scanning, so your app must know the network details in advance.

> Local Network Privacy or Bluetooth permissions are not required for basic Wi-Fi onboarding with `NEHotspotConfiguration`.

## Recommended User Flow

1. Begin the "data import" or device setup step in your app.
2. Display messaging such as "To continue, join **<Device Hotspot SSID>**?" with a **Continue** button.
3. Call `applyConfiguration(_:)` when the user taps **Continue**. iOS presents the system "Join Wi-Fi Network" sheet.
4. If the SSID is available and credentials succeed, iOS joins the network and the app can proceed with the transfer.
5. When using `joinOnce = true`, the association ends after your app leaves the foreground. You can also call `removeConfiguration(forSSID:)` to proactively disconnect (behavior varies slightly across iOS releases).

## Swift 6 Reference Implementation

```swift
import NetworkExtension

enum WiFiJoinError: Error {
    case notInRange, userCancelled, systemError(Error?)
}

func joinWifi(ssid: String, passphrase: String?, joinOnce: Bool = true) async throws {
    let config: NEHotspotConfiguration
    if let pass = passphrase, !pass.isEmpty {
        config = NEHotspotConfiguration(ssid: ssid, passphrase: pass, isWEP: false)
    } else {
        config = NEHotspotConfiguration(ssid: ssid)
    }
    config.joinOnce = joinOnce
    // Optional: set to true if the network does not broadcast its SSID
    // config.hidden = true

    try await withCheckedThrowingContinuation { continuation in
        NEHotspotConfigurationManager.shared.apply(config) { error in
            if let err = error as? NEHotspotConfigurationError {
                switch err.code {
                case .userDenied:
                    continuation.resume(throwing: WiFiJoinError.userCancelled)
                case .invalid, .invalidSSID, .invalidWPAPassphrase:
                    continuation.resume(throwing: WiFiJoinError.systemError(err))
                case .internal, .systemConfiguration, .unknown:
                    continuation.resume(throwing: WiFiJoinError.systemError(err))
                @unknown default:
                    continuation.resume(throwing: WiFiJoinError.systemError(err))
                }
            } else {
                continuation.resume()
            }
        }
    }
}

func leaveWifi(ssid: String) {
    NEHotspotConfigurationManager.shared.removeConfiguration(forSSID: ssid)
}
```

The `NEHotspotConfigurationError` enum covers the possible error codes. Expect generic "Unable to join" alerts when the access point is out of range, the signal is weak, or a captive portal blocks authentication. Provide retry guidance in your UI.

## Operational Considerations

- The target network must be in range at the moment you call `applyConfiguration(_:)`; there is no API to scan for available SSIDs.
- iOS typically shows the join sheet only the first time for a given SSID/app combination. Subsequent calls while already associated may complete silently. To encourage a prompt each session, use `joinOnce = true` or remove the saved configuration between transfers.
- Personal Hotspot remains entirely under user control. If your flow relies on the phone's hotspot, guide the user through manual enablement.
- Avoid gating logic on the current SSID. Instead, rely on transport-level handshakes or other signals you control.
- Joining Enterprise/EAP networks is supported with additional configuration, but `joinOnce` is not available for EAP setups (`joinOnceNotSupported`).

## Acceptance Criteria

- The iOS target includes the Hotspot Configuration capability.
- Tapping **Join network** during onboarding triggers `NEHotspotConfigurationManager.apply(_:)` with the known SSID credentials.
- iOS presents the system join sheet, and the app proceeds when the association succeeds.
- The configuration is temporary (via `joinOnce = true` or `removeConfiguration(forSSID:)`) so the device returns to the previous network when onboarding is complete.
- The UI offers clear recovery steps for out-of-range networks, invalid credentials, or user cancellation.
- The app does not attempt to toggle Personal Hotspot or depend on `NEHotspotHelper`.
