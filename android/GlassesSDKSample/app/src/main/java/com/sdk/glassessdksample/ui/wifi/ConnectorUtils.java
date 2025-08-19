package com.glasssutdio.wear.wifi;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.MacAddress;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiNetworkSpecifier;
import android.net.wifi.WpsInfo;
import android.os.PatternMatcher;
import android.provider.Settings;
import androidx.arch.core.util.Function;
import com.glasssutdio.wear.wifi.ConnectorUtils;
import com.glasssutdio.wear.wifi.utils.Elvis;
import com.glasssutdio.wear.wifi.utils.SSIDUtils;
import com.glasssutdio.wear.wifi.utils.VersionUtils;
import com.glasssutdio.wear.wifi.wifiConnect.ConnectionErrorCode;
import com.glasssutdio.wear.wifi.wifiConnect.DisconnectCallbackHolder;
import com.glasssutdio.wear.wifi.wifiConnect.WifiConnectionCallback;
import com.glasssutdio.wear.wifi.wifiWps.ConnectionWpsListener;
import com.thanosfisherman.wifiutils.utils.VersionUtil;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/* loaded from: classes.dex */
public final class ConnectorUtils {
    private static final int MAX_PRIORITY = 99999;

    public static int getPowerPercentage(int power) {
        if (power <= -93) {
            return 0;
        }
        if (-25 > power || power > 0) {
            return power + 125;
        }
        return 100;
    }

    public static boolean isAlreadyConnected(WifiManager wifiManager, String bssid) {
        if (bssid == null || wifiManager == null || wifiManager.getConnectionInfo() == null || wifiManager.getConnectionInfo().getBSSID() == null || wifiManager.getConnectionInfo().getIpAddress() == 0 || !Objects.equals(bssid, wifiManager.getConnectionInfo().getBSSID())) {
            return false;
        }
        WifiUtils.wifiLog("Already connected to: " + wifiManager.getConnectionInfo().getSSID() + "  BSSID: " + wifiManager.getConnectionInfo().getBSSID());
        return true;
    }

    public static boolean isAlreadyConnected2(WifiManager wifiManager, String ssid) {
        if (ssid == null || wifiManager == null || wifiManager.getConnectionInfo() == null || wifiManager.getConnectionInfo().getSSID() == null || wifiManager.getConnectionInfo().getIpAddress() == 0 || !Objects.equals(ssid, wifiManager.getConnectionInfo().getSSID())) {
            return false;
        }
        WifiUtils.wifiLog("Already connected to: " + wifiManager.getConnectionInfo().getSSID() + "  BSSID: " + wifiManager.getConnectionInfo().getBSSID());
        return true;
    }

    private static boolean isConnectedToNetworkLollipop(ConnectivityManager connectivityManager) {
        if (connectivityManager == null) {
            return false;
        }
        boolean zIsConnected = false;
        for (Network network : connectivityManager.getAllNetworks()) {
            NetworkInfo networkInfo = connectivityManager.getNetworkInfo(network);
            if (networkInfo != null && 1 == networkInfo.getType()) {
                zIsConnected |= networkInfo.isConnected();
            }
        }
        return zIsConnected;
    }

    public static boolean isAlreadyConnected(ConnectivityManager connectivityManager) {
        if (VersionUtils.isLollipopOrLater()) {
            return isConnectedToNetworkLollipop(connectivityManager);
        }
        return Elvis.m176of(connectivityManager).next(new Function() { // from class: com.glasssutdio.wear.wifi.ConnectorUtils$$ExternalSyntheticLambda0
            @Override // androidx.arch.core.util.Function
            public final Object apply(Object obj) {
                return ((ConnectivityManager) obj).getNetworkInfo(1);
            }
        }).next(new Function() { // from class: com.glasssutdio.wear.wifi.ConnectorUtils$$ExternalSyntheticLambda1
            @Override // androidx.arch.core.util.Function
            public final Object apply(Object obj) {
                return ((NetworkInfo) obj).getState();
            }
        }).next(new Function() { // from class: com.glasssutdio.wear.wifi.ConnectorUtils$$ExternalSyntheticLambda2
            @Override // androidx.arch.core.util.Function
            public final Object apply(Object obj) {
                return Boolean.valueOf(((NetworkInfo.State) obj) == NetworkInfo.State.CONNECTED);
            }
        }).getBoolean();
    }

    public static boolean isAlreadyConnected(WifiManager wifiManager, ConnectivityManager connectivityManager, String ssid) {
        boolean zIsAlreadyConnected = isAlreadyConnected(connectivityManager);
        if (!zIsAlreadyConnected || ssid == null || wifiManager == null) {
            return zIsAlreadyConnected;
        }
        if (VersionUtils.isJellyBeanOrLater()) {
            ssid = SSIDUtils.convertToQuotedString(ssid);
        }
        String ssid2 = wifiManager.getConnectionInfo().getSSID();
        return ssid2 != null && ssid2.equals(ssid);
    }

    private static boolean checkForExcessOpenNetworkAndSave(final ContentResolver resolver, final WifiManager wifiMgr) {
        int i;
        List<WifiConfiguration> configuredNetworks = wifiMgr.getConfiguredNetworks();
        sortByPriority(configuredNetworks);
        if (VersionUtils.isJellyBeanOrLater()) {
            i = Settings.Secure.getInt(resolver, "wifi_num_open_networks_kept", 10);
        } else {
            i = Settings.Secure.getInt(resolver, "wifi_num_open_networks_kept", 10);
        }
        boolean z = false;
        int i2 = 0;
        for (int size = configuredNetworks.size() - 1; size >= 0; size--) {
            WifiConfiguration wifiConfiguration = configuredNetworks.get(size);
            if (Objects.equals("OPEN", ConfigSecurities.getSecurity(wifiConfiguration)) && (i2 = i2 + 1) >= i) {
                wifiMgr.removeNetwork(wifiConfiguration.networkId);
                z = true;
            }
        }
        return !z || wifiMgr.saveConfiguration();
    }

    private static int getMaxPriority(final WifiManager wifiManager) {
        int i = 0;
        if (wifiManager == null) {
            return 0;
        }
        for (WifiConfiguration wifiConfiguration : wifiManager.getConfiguredNetworks()) {
            if (wifiConfiguration.priority > i) {
                i = wifiConfiguration.priority;
            }
        }
        return i;
    }

    private static int shiftPriorityAndSave(final WifiManager wifiMgr) {
        if (wifiMgr == null) {
            return 0;
        }
        List<WifiConfiguration> configuredNetworks = wifiMgr.getConfiguredNetworks();
        sortByPriority(configuredNetworks);
        int size = configuredNetworks.size();
        for (int i = 0; i < size; i++) {
            WifiConfiguration wifiConfiguration = configuredNetworks.get(i);
            wifiConfiguration.priority = i;
            wifiMgr.updateNetwork(wifiConfiguration);
        }
        wifiMgr.saveConfiguration();
        return size;
    }

    private static String trimQuotes(String str) {
        return (str == null || str.isEmpty()) ? str : str.replaceAll("^\"*", "").replaceAll("\"*$", "");
    }

    public static boolean isHexWepKey(String wepKey) {
        int length = wepKey == null ? 0 : wepKey.length();
        return (length == 10 || length == 26 || length == 58) && wepKey.matches("[0-9A-Fa-f]*");
    }

    static /* synthetic */ int lambda$sortByPriority$2(WifiConfiguration wifiConfiguration, WifiConfiguration wifiConfiguration2) {
        return wifiConfiguration.priority - wifiConfiguration2.priority;
    }

    private static void sortByPriority(final List<WifiConfiguration> configurations) {
        Collections.sort(configurations, new Comparator() { // from class: com.glasssutdio.wear.wifi.ConnectorUtils$$ExternalSyntheticLambda3
            @Override // java.util.Comparator
            public final int compare(Object obj, Object obj2) {
                return ConnectorUtils.lambda$sortByPriority$2((WifiConfiguration) obj, (WifiConfiguration) obj2);
            }
        });
    }

    public static int frequencyToChannel(int freq) {
        if (2412 <= freq && freq <= 2484) {
            return ((freq - 2412) / 5) + 1;
        }
        if (5170 > freq || freq > 5825) {
            return -1;
        }
        return ((freq - 5170) / 5) + 34;
    }

    static void registerReceiver(final Context context, final BroadcastReceiver receiver, final IntentFilter filter) {
        if (receiver != null) {
            try {
                context.registerReceiver(receiver, filter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    static void unregisterReceiver(final Context context, final BroadcastReceiver receiver) {
        if (receiver != null) {
            try {
                context.unregisterReceiver(receiver);
            } catch (IllegalArgumentException unused) {
            }
        }
    }

    static boolean connectToWifi(final Context context, final WifiManager wifiManager, final ConnectivityManager connectivityManager, WeakHandler handler, final ScanResult scanResult, final String password, WifiConnectionCallback wifiConnectionCallback, boolean patternMatch, String ssid) {
        if (wifiManager == null || connectivityManager == null) {
            return false;
        }
        if (VersionUtils.isAndroidQOrLater()) {
            return connectAndroidQ(wifiManager, connectivityManager, handler, wifiConnectionCallback, scanResult, password, patternMatch, ssid);
        }
        return connectPreAndroidQ(context, wifiManager, scanResult, password);
    }

    static boolean connectToWifiHidden(final Context context, final WifiManager wifiManager, final ConnectivityManager connectivityManager, WeakHandler handler, final String ssid, final String type, final String password, WifiConnectionCallback wifiConnectionCallback) {
        if (wifiManager == null || connectivityManager == null || type == null) {
            return false;
        }
        if (VersionUtils.isAndroidQOrLater()) {
            return connectAndroidQHidden(wifiManager, connectivityManager, handler, wifiConnectionCallback, ssid, type, password);
        }
        return connectPreAndroidQHidden(context, wifiManager, ssid, type, password);
    }

    private static boolean connectPreAndroidQ(final Context context, final WifiManager wifiManager, final ScanResult scanResult, final String password) {
        if (wifiManager == null) {
            return false;
        }
        WifiConfiguration wifiConfiguration = ConfigSecurities.getWifiConfiguration(wifiManager, scanResult);
        if (wifiConfiguration != null && password.isEmpty()) {
            WifiUtils.wifiLog("PASSWORD WAS EMPTY. TRYING TO CONNECT TO EXISTING NETWORK CONFIGURATION");
            return connectToConfiguredNetwork(wifiManager, wifiConfiguration, true);
        }
        if (!cleanPreviousConfiguration(wifiManager, wifiConfiguration)) {
            WifiUtils.wifiLog("COULDN'T REMOVE PREVIOUS CONFIG, CONNECTING TO EXISTING ONE");
            return connectToConfiguredNetwork(wifiManager, wifiConfiguration, true);
        }
        String security = ConfigSecurities.getSecurity(scanResult);
        if (Objects.equals("OPEN", security)) {
            checkForExcessOpenNetworkAndSave(context.getContentResolver(), wifiManager);
        }
        WifiConfiguration wifiConfiguration2 = new WifiConfiguration();
        wifiConfiguration2.SSID = SSIDUtils.convertToQuotedString(scanResult.SSID);
        wifiConfiguration2.BSSID = scanResult.BSSID;
        ConfigSecurities.setupSecurity(wifiConfiguration2, security, password);
        int iAddNetwork = wifiManager.addNetwork(wifiConfiguration2);
        WifiUtils.wifiLog("Network ID: " + iAddNetwork);
        if (iAddNetwork == -1) {
            return false;
        }
        if (!wifiManager.saveConfiguration()) {
            WifiUtils.wifiLog("Couldn't save wifi config");
            return false;
        }
        WifiConfiguration wifiConfiguration3 = ConfigSecurities.getWifiConfiguration(wifiManager, wifiConfiguration2);
        if (wifiConfiguration3 == null) {
            WifiUtils.wifiLog("Error getting wifi config after save. (config == null)");
            return false;
        }
        return connectToConfiguredNetwork(wifiManager, wifiConfiguration3, true);
    }

    private static boolean connectPreAndroidQHidden(final Context context, final WifiManager wifiManager, final String ssid, final String type, final String password) {
        if (wifiManager == null) {
            return false;
        }
        String security = ConfigSecurities.getSecurity(type);
        if (Objects.equals("OPEN", security)) {
            checkForExcessOpenNetworkAndSave(context.getContentResolver(), wifiManager);
        }
        WifiConfiguration wifiConfiguration = new WifiConfiguration();
        wifiConfiguration.SSID = SSIDUtils.convertToQuotedString(ssid);
        ConfigSecurities.setupSecurityHidden(wifiConfiguration, security, password);
        int iAddNetwork = wifiManager.addNetwork(wifiConfiguration);
        WifiUtils.wifiLog("Hidden-Network ID: " + iAddNetwork);
        if (iAddNetwork == -1) {
            return false;
        }
        if (!wifiManager.saveConfiguration()) {
            WifiUtils.wifiLog("Couldn't save wifi config");
            return false;
        }
        WifiConfiguration wifiConfiguration2 = ConfigSecurities.getWifiConfiguration(wifiManager, wifiConfiguration);
        if (wifiConfiguration2 == null) {
            WifiUtils.wifiLog("Error getting wifi config after save. (config == null)");
            return false;
        }
        return connectToConfiguredNetwork(wifiManager, wifiConfiguration2, true);
    }

    private static boolean connectToConfiguredNetwork(WifiManager wifiManager, WifiConfiguration config, boolean reassociate) {
        WifiConfiguration wifiConfiguration;
        if (config == null || wifiManager == null) {
            return false;
        }
        if (VersionUtils.isMarshmallowOrLater()) {
            if (!disableAllButOne(wifiManager, config)) {
                return false;
            }
            if (reassociate) {
                if (!wifiManager.reassociate()) {
                    return false;
                }
            } else if (!wifiManager.reconnect()) {
                return false;
            }
            return true;
        }
        int maxPriority = getMaxPriority(wifiManager) + 1;
        if (maxPriority > MAX_PRIORITY) {
            maxPriority = shiftPriorityAndSave(wifiManager);
            config = ConfigSecurities.getWifiConfiguration(wifiManager, config);
            if (config == null) {
                return false;
            }
        }
        config.priority = maxPriority;
        int iUpdateNetwork = wifiManager.updateNetwork(config);
        if (iUpdateNetwork == -1 || !wifiManager.enableNetwork(iUpdateNetwork, false) || !wifiManager.saveConfiguration() || (wifiConfiguration = ConfigSecurities.getWifiConfiguration(wifiManager, config)) == null || !disableAllButOne(wifiManager, wifiConfiguration)) {
            return false;
        }
        if (reassociate) {
            if (!wifiManager.reassociate()) {
                return false;
            }
        } else if (!wifiManager.reconnect()) {
            return false;
        }
        return true;
    }

    private static boolean connectAndroidQ(WifiManager wifiManager, ConnectivityManager connectivityManager, WeakHandler handler, WifiConnectionCallback wifiConnectionCallback, ScanResult scanResult, String password, boolean patternMatch, String ssid) {
        if (connectivityManager == null) {
            return false;
        }
        WifiNetworkSpecifier.Builder builder = new WifiNetworkSpecifier.Builder();
        if (patternMatch) {
            if (ssid == null) {
                ssid = scanResult.SSID;
            }
            builder.setSsidPattern(new PatternMatcher(ssid, 1));
        } else {
            builder.setSsid(scanResult.SSID).setBssid(MacAddress.fromString(scanResult.BSSID));
        }
        ConfigSecurities.setupWifiNetworkSpecifierSecurities(builder, ConfigSecurities.getSecurity(scanResult), password);
        NetworkRequest networkRequestBuild = new NetworkRequest.Builder().addTransportType(1).setNetworkSpecifier(builder.build()).addCapability(13).build();
        DisconnectCallbackHolder.getInstance().disconnect();
        DisconnectCallbackHolder.getInstance().addNetworkCallback(new C11381(connectivityManager, handler, wifiManager, scanResult, wifiConnectionCallback), connectivityManager);
        WifiUtils.wifiLog("connecting with Android 10");
        DisconnectCallbackHolder.getInstance().requestNetwork(networkRequestBuild);
        return true;
    }

    /* renamed from: com.glasssutdio.wear.wifi.ConnectorUtils$1 */
    class C11381 extends ConnectivityManager.NetworkCallback {
        final /* synthetic */ ConnectivityManager val$connectivityManager;
        final /* synthetic */ WeakHandler val$handler;
        final /* synthetic */ ScanResult val$scanResult;
        final /* synthetic */ WifiConnectionCallback val$wifiConnectionCallback;
        final /* synthetic */ WifiManager val$wifiManager;

        C11381(final ConnectivityManager val$connectivityManager, final WeakHandler val$handler, final WifiManager val$wifiManager, final ScanResult val$scanResult, final WifiConnectionCallback val$wifiConnectionCallback) {
            this.val$connectivityManager = val$connectivityManager;
            this.val$handler = val$handler;
            this.val$wifiManager = val$wifiManager;
            this.val$scanResult = val$scanResult;
            this.val$wifiConnectionCallback = val$wifiConnectionCallback;
        }

        @Override // android.net.ConnectivityManager.NetworkCallback
        public void onAvailable(Network network) {
            super.onAvailable(network);
            WifiUtils.wifiLog("AndroidQ+ connected to wifi ");
            DisconnectCallbackHolder.getInstance().bindProcessToNetwork(network);
            this.val$connectivityManager.setNetworkPreference(1);
            WeakHandler weakHandler = this.val$handler;
            final WifiManager wifiManager = this.val$wifiManager;
            final ScanResult scanResult = this.val$scanResult;
            final WifiConnectionCallback wifiConnectionCallback = this.val$wifiConnectionCallback;
            weakHandler.postDelayed(new Runnable() { // from class: com.glasssutdio.wear.wifi.ConnectorUtils$1$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    ConnectorUtils.C11381.lambda$onAvailable$1(wifiManager, scanResult, wifiConnectionCallback);
                }
            }, 500L);
        }

        static /* synthetic */ void lambda$onAvailable$1(WifiManager wifiManager, ScanResult scanResult, WifiConnectionCallback wifiConnectionCallback) {
            if (ConnectorUtils.isAlreadyConnected(wifiManager, (String) Elvis.m176of(scanResult).next(new Function() { // from class: com.glasssutdio.wear.wifi.ConnectorUtils$1$$ExternalSyntheticLambda0
                @Override // androidx.arch.core.util.Function
                public final Object apply(Object obj) {
                    return ((ScanResult) obj).BSSID;
                }
            }).get())) {
                wifiConnectionCallback.successfulConnect();
            } else {
                wifiConnectionCallback.errorConnect(ConnectionErrorCode.ANDROID_10_IMMEDIATELY_DROPPED_CONNECTION);
            }
        }

        @Override // android.net.ConnectivityManager.NetworkCallback
        public void onUnavailable() {
            super.onUnavailable();
            WifiUtils.wifiLog("AndroidQ+ could not connect to wifi");
            this.val$wifiConnectionCallback.errorConnect(ConnectionErrorCode.USER_CANCELLED);
        }

        @Override // android.net.ConnectivityManager.NetworkCallback
        public void onLost(Network network) {
            super.onLost(network);
            WifiUtils.wifiLog("onLost");
            DisconnectCallbackHolder.getInstance().unbindProcessFromNetwork();
            DisconnectCallbackHolder.getInstance().disconnect();
        }
    }

    private static boolean connectAndroidQHidden(WifiManager wifiManager, ConnectivityManager connectivityManager, WeakHandler handler, WifiConnectionCallback wifiConnectionCallback, String ssid, String type, String password) {
        if (connectivityManager == null) {
            return false;
        }
        WifiNetworkSpecifier.Builder ssid2 = new WifiNetworkSpecifier.Builder().setIsHiddenSsid(true).setSsid(ssid);
        ConfigSecurities.setupWifiNetworkSpecifierSecurities(ssid2, ConfigSecurities.getSecurity(type), password);
        NetworkRequest networkRequestBuild = new NetworkRequest.Builder().addTransportType(1).addCapability(12).addCapability(13).setNetworkSpecifier(ssid2.build()).build();
        DisconnectCallbackHolder.getInstance().disconnect();
        DisconnectCallbackHolder.getInstance().addNetworkCallback(new C11392(connectivityManager, handler, wifiManager, ssid, wifiConnectionCallback), connectivityManager);
        WifiUtils.wifiLog("connecting with Android 10");
        DisconnectCallbackHolder.getInstance().requestNetwork(networkRequestBuild);
        return true;
    }

    /* renamed from: com.glasssutdio.wear.wifi.ConnectorUtils$2 */
    class C11392 extends ConnectivityManager.NetworkCallback {
        final /* synthetic */ ConnectivityManager val$connectivityManager;
        final /* synthetic */ WeakHandler val$handler;
        final /* synthetic */ String val$ssid;
        final /* synthetic */ WifiConnectionCallback val$wifiConnectionCallback;
        final /* synthetic */ WifiManager val$wifiManager;

        C11392(final ConnectivityManager val$connectivityManager, final WeakHandler val$handler, final WifiManager val$wifiManager, final String val$ssid, final WifiConnectionCallback val$wifiConnectionCallback) {
            this.val$connectivityManager = val$connectivityManager;
            this.val$handler = val$handler;
            this.val$wifiManager = val$wifiManager;
            this.val$ssid = val$ssid;
            this.val$wifiConnectionCallback = val$wifiConnectionCallback;
        }

        @Override // android.net.ConnectivityManager.NetworkCallback
        public void onAvailable(Network network) {
            super.onAvailable(network);
            WifiUtils.wifiLog("AndroidQ+ connected to wifi ");
            DisconnectCallbackHolder.getInstance().bindProcessToNetwork(network);
            this.val$connectivityManager.setNetworkPreference(1);
            WeakHandler weakHandler = this.val$handler;
            final WifiManager wifiManager = this.val$wifiManager;
            final String str = this.val$ssid;
            final WifiConnectionCallback wifiConnectionCallback = this.val$wifiConnectionCallback;
            weakHandler.postDelayed(new Runnable() { // from class: com.glasssutdio.wear.wifi.ConnectorUtils$2$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    ConnectorUtils.C11392.lambda$onAvailable$0(wifiManager, str, wifiConnectionCallback);
                }
            }, 500L);
        }

        static /* synthetic */ void lambda$onAvailable$0(WifiManager wifiManager, String str, WifiConnectionCallback wifiConnectionCallback) {
            if (ConnectorUtils.isAlreadyConnected(wifiManager, str)) {
                wifiConnectionCallback.successfulConnect();
            } else {
                wifiConnectionCallback.errorConnect(ConnectionErrorCode.ANDROID_10_IMMEDIATELY_DROPPED_CONNECTION);
            }
        }

        @Override // android.net.ConnectivityManager.NetworkCallback
        public void onUnavailable() {
            super.onUnavailable();
            WifiUtils.wifiLog("AndroidQ+ could not connect to wifi");
            this.val$wifiConnectionCallback.errorConnect(ConnectionErrorCode.USER_CANCELLED);
        }

        @Override // android.net.ConnectivityManager.NetworkCallback
        public void onLost(Network network) {
            super.onLost(network);
            WifiUtils.wifiLog("onLost");
            DisconnectCallbackHolder.getInstance().unbindProcessFromNetwork();
            DisconnectCallbackHolder.getInstance().disconnect();
        }

        @Override // android.net.ConnectivityManager.NetworkCallback
        public void onLinkPropertiesChanged(Network network, LinkProperties linkProperties) {
            super.onLinkPropertiesChanged(network, linkProperties);
            WifiUtils.wifiLog("onLost");
        }
    }

    private static boolean disableAllButOne(final WifiManager wifiManager, final WifiConfiguration config) {
        boolean zEnableNetwork = false;
        if (wifiManager == null) {
            return false;
        }
        List<WifiConfiguration> configuredNetworks = wifiManager.getConfiguredNetworks();
        if (configuredNetworks != null && config != null && !configuredNetworks.isEmpty()) {
            for (WifiConfiguration wifiConfiguration : configuredNetworks) {
                if (wifiConfiguration != null) {
                    if (wifiConfiguration.networkId == config.networkId) {
                        zEnableNetwork = wifiManager.enableNetwork(wifiConfiguration.networkId, true);
                    } else {
                        wifiManager.disableNetwork(wifiConfiguration.networkId);
                    }
                }
            }
            WifiUtils.wifiLog("disableAllButOne " + zEnableNetwork);
        }
        return zEnableNetwork;
    }

    private static boolean disableAllButOne(final WifiManager wifiManager, final ScanResult scanResult) {
        boolean zEnableNetwork = false;
        if (wifiManager == null) {
            return false;
        }
        List<WifiConfiguration> configuredNetworks = wifiManager.getConfiguredNetworks();
        if (configuredNetworks != null && scanResult != null && !configuredNetworks.isEmpty()) {
            for (WifiConfiguration wifiConfiguration : configuredNetworks) {
                if (wifiConfiguration != null) {
                    if (Objects.equals(scanResult.BSSID, wifiConfiguration.BSSID) && Objects.equals(scanResult.SSID, trimQuotes(wifiConfiguration.SSID))) {
                        zEnableNetwork = wifiManager.enableNetwork(wifiConfiguration.networkId, true);
                    } else {
                        wifiManager.disableNetwork(wifiConfiguration.networkId);
                    }
                }
            }
        }
        return zEnableNetwork;
    }

    public static boolean reEnableNetworkIfPossible(final WifiManager wifiManager, final ScanResult scanResult) {
        boolean zEnableNetwork = false;
        if (wifiManager == null) {
            return false;
        }
        List<WifiConfiguration> configuredNetworks = wifiManager.getConfiguredNetworks();
        if (configuredNetworks != null && scanResult != null && !configuredNetworks.isEmpty()) {
            Iterator<WifiConfiguration> it = configuredNetworks.iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                WifiConfiguration next = it.next();
                if (Objects.equals(scanResult.BSSID, next.BSSID) && Objects.equals(scanResult.SSID, trimQuotes(next.SSID))) {
                    zEnableNetwork = wifiManager.enableNetwork(next.networkId, true);
                    break;
                }
            }
            WifiUtils.wifiLog("reEnableNetworkIfPossible " + zEnableNetwork);
        }
        return zEnableNetwork;
    }

    static void connectWps(final WifiManager wifiManager, final WeakHandler handler, final ScanResult scanResult, String pin, long timeOutMillis, final ConnectionWpsListener connectionWpsListener) {
        if (wifiManager == null) {
            connectionWpsListener.isSuccessful(false);
            return;
        }
        WpsInfo wpsInfo = new WpsInfo();
        final Runnable runnable = new Runnable() { // from class: com.glasssutdio.wear.wifi.ConnectorUtils.3
            @Override // java.lang.Runnable
            public void run() {
                wifiManager.cancelWps(null);
                WifiUtils.wifiLog("Connection with WPS has timed out");
                ConnectorUtils.cleanPreviousConfiguration(wifiManager, scanResult);
                connectionWpsListener.isSuccessful(false);
                handler.removeCallbacks(this);
            }
        };
        WifiManager.WpsCallback wpsCallback = new WifiManager.WpsCallback() { // from class: com.glasssutdio.wear.wifi.ConnectorUtils.4
            @Override // android.net.wifi.WifiManager.WpsCallback
            public void onStarted(String pin2) {
            }

            @Override // android.net.wifi.WifiManager.WpsCallback
            public void onSucceeded() {
                handler.removeCallbacks(runnable);
                WifiUtils.wifiLog("CONNECTED With WPS successfully");
                connectionWpsListener.isSuccessful(true);
            }

            @Override // android.net.wifi.WifiManager.WpsCallback
            public void onFailed(int reason) {
                String strValueOf;
                handler.removeCallbacks(runnable);
                if (reason == 3) {
                    strValueOf = "WPS_OVERLAP_ERROR";
                } else if (reason == 4) {
                    strValueOf = "WPS_WEP_PROHIBITED";
                } else if (reason == 5) {
                    strValueOf = "WPS_TKIP_ONLY_PROHIBITED";
                } else if (reason == 6) {
                    strValueOf = "WPS_AUTH_FAILURE";
                } else if (reason == 7) {
                    strValueOf = "WPS_TIMED_OUT";
                } else {
                    strValueOf = String.valueOf(reason);
                }
                WifiUtils.wifiLog("FAILED to connect with WPS. Reason: " + strValueOf);
                ConnectorUtils.cleanPreviousConfiguration(wifiManager, scanResult);
                ConnectorUtils.reenableAllHotspots(wifiManager);
                connectionWpsListener.isSuccessful(false);
            }
        };
        WifiUtils.wifiLog("Connecting with WPS...");
        wpsInfo.setup = 2;
        wpsInfo.BSSID = scanResult.BSSID;
        wpsInfo.pin = pin;
        wifiManager.cancelWps(null);
        if (!cleanPreviousConfiguration(wifiManager, scanResult)) {
            disableAllButOne(wifiManager, scanResult);
        }
        handler.postDelayed(runnable, timeOutMillis);
        wifiManager.startWps(wpsInfo, wpsCallback);
    }

    static boolean disconnectFromWifi(final WifiManager wifiManager) {
        return wifiManager.disconnect();
    }

    static boolean removeWifi(final WifiManager wifiManager, final String ssid) {
        return cleanPreviousConfiguration(wifiManager, ConfigSecurities.getWifiConfiguration(wifiManager, ssid));
    }

    static boolean cleanPreviousConfiguration(final WifiManager wifiManager, final ScanResult scanResult) {
        if (wifiManager == null) {
            return false;
        }
        WifiConfiguration wifiConfiguration = ConfigSecurities.getWifiConfiguration(wifiManager, scanResult);
        WifiUtils.wifiLog("Attempting to remove previous network config...");
        if (wifiConfiguration == null) {
            return true;
        }
        if (!wifiManager.removeNetwork(wifiConfiguration.networkId)) {
            return false;
        }
        wifiManager.saveConfiguration();
        return true;
    }

    static boolean cleanPreviousConfiguration(final WifiManager wifiManager, final WifiConfiguration config) {
        if (wifiManager == null) {
            return false;
        }
        WifiUtils.wifiLog("Attempting to remove previous network config...");
        if (config == null) {
            return true;
        }
        if (!wifiManager.removeNetwork(config.networkId)) {
            return false;
        }
        wifiManager.saveConfiguration();
        return true;
    }

    static void reenableAllHotspots(WifiManager wifi) {
        List<WifiConfiguration> configuredNetworks;
        if (wifi == null || (configuredNetworks = wifi.getConfiguredNetworks()) == null || configuredNetworks.isEmpty()) {
            return;
        }
        Iterator<WifiConfiguration> it = configuredNetworks.iterator();
        while (it.hasNext()) {
            wifi.enableNetwork(it.next().networkId, false);
        }
    }

    static ScanResult matchScanResultSsid(String ssid, Iterable<ScanResult> results, boolean mPatternMatch) {
        for (ScanResult scanResult : results) {
            String str = scanResult.SSID;
            if (mPatternMatch) {
                if (str.startsWith(ssid)) {
                    return scanResult;
                }
            } else if (Objects.equals(str, ssid)) {
                return scanResult;
            }
        }
        return null;
    }

    static ScanResult matchScanResult(String ssid, String bssid, Iterable<ScanResult> results) {
        for (ScanResult scanResult : results) {
            if (Objects.equals(scanResult.SSID, ssid) && Objects.equals(scanResult.BSSID, bssid)) {
                return scanResult;
            }
        }
        return null;
    }

    static ScanResult matchScanResultBssid(String bssid, Iterable<ScanResult> results) {
        for (ScanResult scanResult : results) {
            if (Objects.equals(scanResult.BSSID, bssid)) {
                return scanResult;
            }
        }
        return null;
    }

    static Intent checkVersionAndGetIntent() {
        if (VersionUtil.INSTANCE.is29AndAbove()) {
            return VersionUtil.INSTANCE.getPanelIntent();
        }
        return null;
    }
}
