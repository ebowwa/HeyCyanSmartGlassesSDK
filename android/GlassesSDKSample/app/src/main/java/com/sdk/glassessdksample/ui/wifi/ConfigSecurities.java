package com.glasssutdio.wear.wifi;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiNetworkSpecifier;
import com.glasssutdio.wear.wifi.utils.SSIDUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/* loaded from: classes.dex */
final class ConfigSecurities {
    static final String SECURITY_EAP = "EAP";
    static final String SECURITY_NONE = "OPEN";
    static final String SECURITY_PSK = "PSK";
    static final String SECURITY_WEP = "WEP";

    ConfigSecurities() {
    }

    static void setupSecurity(WifiConfiguration config, String security, final String password) {
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        WifiUtils.wifiLog("Setting up security " + security);
        security.hashCode();
        switch (security) {
            case "EAP":
                config.allowedProtocols.set(1);
                config.allowedProtocols.set(0);
                config.allowedGroupCiphers.set(0);
                config.allowedGroupCiphers.set(1);
                config.allowedGroupCiphers.set(2);
                config.allowedGroupCiphers.set(3);
                config.allowedPairwiseCiphers.set(1);
                config.allowedPairwiseCiphers.set(2);
                config.allowedKeyManagement.set(2);
                config.allowedKeyManagement.set(3);
                config.preSharedKey = SSIDUtils.convertToQuotedString(password);
                break;
            case "PSK":
                config.allowedProtocols.set(1);
                config.allowedProtocols.set(0);
                config.allowedKeyManagement.set(1);
                config.allowedPairwiseCiphers.set(2);
                config.allowedPairwiseCiphers.set(1);
                config.allowedGroupCiphers.set(0);
                config.allowedGroupCiphers.set(1);
                config.allowedGroupCiphers.set(3);
                config.allowedGroupCiphers.set(2);
                if (password.matches("[0-9A-Fa-f]{64}")) {
                    config.preSharedKey = password;
                    break;
                } else {
                    config.preSharedKey = SSIDUtils.convertToQuotedString(password);
                    break;
                }
            case "WEP":
                config.allowedKeyManagement.set(0);
                config.allowedProtocols.set(1);
                config.allowedProtocols.set(0);
                config.allowedAuthAlgorithms.set(0);
                config.allowedAuthAlgorithms.set(1);
                config.allowedPairwiseCiphers.set(2);
                config.allowedPairwiseCiphers.set(1);
                config.allowedGroupCiphers.set(0);
                config.allowedGroupCiphers.set(1);
                if (ConnectorUtils.isHexWepKey(password)) {
                    config.wepKeys[0] = password;
                    break;
                } else {
                    config.wepKeys[0] = SSIDUtils.convertToQuotedString(password);
                    break;
                }
            case "OPEN":
                config.allowedKeyManagement.set(0);
                config.allowedProtocols.set(1);
                config.allowedProtocols.set(0);
                config.allowedPairwiseCiphers.set(2);
                config.allowedPairwiseCiphers.set(1);
                config.allowedGroupCiphers.set(0);
                config.allowedGroupCiphers.set(1);
                config.allowedGroupCiphers.set(3);
                config.allowedGroupCiphers.set(2);
                break;
            default:
                WifiUtils.wifiLog("Invalid security type: " + security);
                break;
        }
    }

    static void setupSecurityHidden(WifiConfiguration config, String security, final String password) {
        config.hiddenSSID = true;
        setupSecurity(config, security, password);
    }

    static void setupWifiNetworkSpecifierSecurities(WifiNetworkSpecifier.Builder wifiNetworkSpecifierBuilder, String security, final String password) {
        WifiUtils.wifiLog("Setting up WifiNetworkSpecifier.Builder " + security);
        security.hashCode();
        switch (security) {
            case "EAP":
            case "PSK":
                wifiNetworkSpecifierBuilder.setWpa2Passphrase(password);
                break;
            case "WEP":
            case "OPEN":
                break;
            default:
                WifiUtils.wifiLog("Invalid security type: " + security);
                break;
        }
    }

    static WifiConfiguration getWifiConfiguration(final WifiManager wifiMgr, final WifiConfiguration configToFind) {
        String str = configToFind.SSID;
        if (str != null && !str.isEmpty()) {
            String str2 = configToFind.BSSID != null ? configToFind.BSSID : "";
            String security = getSecurity(configToFind);
            List<WifiConfiguration> configuredNetworks = wifiMgr.getConfiguredNetworks();
            if (configuredNetworks == null) {
                WifiUtils.wifiLog("NULL configs");
                return null;
            }
            for (WifiConfiguration wifiConfiguration : configuredNetworks) {
                if (str2.equals(wifiConfiguration.BSSID) || str.equals(wifiConfiguration.SSID)) {
                    if (Objects.equals(security, getSecurity(wifiConfiguration))) {
                        return wifiConfiguration;
                    }
                }
            }
            WifiUtils.wifiLog("Couldn't find " + str);
        }
        return null;
    }

    static WifiConfiguration getWifiConfiguration(final WifiManager wifiManager, final String ssid) {
        String str = "\"" + ssid + '\"';
        for (WifiConfiguration wifiConfiguration : wifiManager.getConfiguredNetworks()) {
            if (wifiConfiguration.SSID != null && wifiConfiguration.SSID.equals(str)) {
                return wifiConfiguration;
            }
        }
        return null;
    }

    static WifiConfiguration getWifiConfiguration(final WifiManager wifiManager, final ScanResult scanResult) {
        if (scanResult.BSSID != null && scanResult.SSID != null && !scanResult.SSID.isEmpty() && !scanResult.BSSID.isEmpty()) {
            String strConvertToQuotedString = SSIDUtils.convertToQuotedString(scanResult.SSID);
            String str = scanResult.BSSID;
            String security = getSecurity(scanResult);
            List<WifiConfiguration> configuredNetworks = wifiManager.getConfiguredNetworks();
            if (configuredNetworks == null) {
                return null;
            }
            for (WifiConfiguration wifiConfiguration : configuredNetworks) {
                if (str.equals(wifiConfiguration.BSSID) || strConvertToQuotedString.equals(wifiConfiguration.SSID)) {
                    if (Objects.equals(security, getSecurity(wifiConfiguration))) {
                        return wifiConfiguration;
                    }
                }
            }
        }
        return null;
    }

    static String getSecurity(WifiConfiguration config) {
        ArrayList arrayList = new ArrayList();
        boolean z = config.allowedKeyManagement.get(0);
        String str = SECURITY_NONE;
        if (z) {
            if (config.wepKeys[0] != null) {
                str = SECURITY_WEP;
            }
            arrayList.add(str);
        }
        if (config.allowedKeyManagement.get(2) || config.allowedKeyManagement.get(3)) {
            str = SECURITY_EAP;
            arrayList.add(SECURITY_EAP);
        }
        if (config.allowedKeyManagement.get(1)) {
            str = SECURITY_PSK;
            arrayList.add(SECURITY_PSK);
        }
        WifiUtils.wifiLog("Got Security Via WifiConfiguration " + arrayList);
        return str;
    }

    static String getSecurity(ScanResult result) {
        String str = result.capabilities;
        String str2 = SECURITY_WEP;
        if (!str.contains(SECURITY_WEP)) {
            str2 = SECURITY_NONE;
        }
        if (result.capabilities.contains(SECURITY_PSK)) {
            str2 = SECURITY_PSK;
        }
        if (result.capabilities.contains(SECURITY_EAP)) {
            str2 = SECURITY_EAP;
        }
        WifiUtils.wifiLog("ScanResult capabilities " + result.capabilities);
        WifiUtils.wifiLog("Got security via ScanResult ".concat(str2));
        return str2;
    }

    static String getSecurity(String result) {
        String str = SECURITY_WEP;
        if (!result.contains(SECURITY_WEP)) {
            str = SECURITY_NONE;
        }
        if (result.contains(SECURITY_PSK)) {
            str = SECURITY_PSK;
        }
        return result.contains(SECURITY_EAP) ? SECURITY_EAP : str;
    }

    public static String getSecurityPrettyPlusWps(ScanResult scanResult) {
        if (scanResult == null) {
            return "";
        }
        String security = getSecurity(scanResult);
        return scanResult.capabilities.contains("WPS") ? security + ", WPS" : security;
    }
}
