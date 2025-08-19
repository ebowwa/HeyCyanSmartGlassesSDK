package com.glasssutdio.wear.wifi;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiNetworkSpecifier;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.format.Formatter;
import com.elvishew.xlog.XLog;
import com.hjq.permissions.Permission;
import com.liulishuo.okdownload.DownloadTask;
import java.io.IOException;
import java.net.InetAddress;
import kotlin.Lazy;
import kotlin.LazyKt;
import kotlin.Metadata;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: WifiConnector.kt */
@Metadata(m606d1 = {"\u0000\\\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0002\b\u0007\u0018\u0000 -2\u00020\u0001:\u0002-.B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0002\u0010\u0004J\u000e\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0002\u001a\u00020\u0003J \u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u00132\u0006\u0010\u0017\u001a\u00020\u00132\u0006\u0010\u0018\u001a\u00020\u0019H\u0007J0\u0010\u001a\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u00132\u0006\u0010\u0017\u001a\u00020\u00132\u0006\u0010\u0018\u001a\u00020\u00192\u0006\u0010\u001b\u001a\u00020\u001c2\u0006\u0010\u001d\u001a\u00020\u001eH\u0003J0\u0010\u001f\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u00132\u0006\u0010\u0017\u001a\u00020\u00132\u0006\u0010\u0018\u001a\u00020\u00192\u0006\u0010\u001b\u001a\u00020\u001c2\u0006\u0010\u001d\u001a\u00020\u001eH\u0003J\u0018\u0010 \u001a\u00020!2\u0006\u0010\u0016\u001a\u00020\u00132\u0006\u0010\u0017\u001a\u00020\u0013H\u0002J\u0006\u0010\"\u001a\u00020\u0015J\u0012\u0010#\u001a\u0004\u0018\u00010\u00132\u0006\u0010\u0002\u001a\u00020\u0003H\u0002J\u0010\u0010$\u001a\u00020\u00132\u0006\u0010\u0002\u001a\u00020\u0003H\u0002J\u0006\u0010%\u001a\u00020\u0013J0\u0010&\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u00132\u0006\u0010\u0017\u001a\u00020\u00132\u0006\u0010\u0018\u001a\u00020\u00192\u0006\u0010\u001b\u001a\u00020\u001c2\u0006\u0010\u001d\u001a\u00020\u001eH\u0002J\u0010\u0010'\u001a\u00020(2\u0006\u0010\u0002\u001a\u00020\u0003H\u0002J\b\u0010)\u001a\u00020(H\u0002J\b\u0010*\u001a\u00020(H\u0002J\u0010\u0010+\u001a\u00020(2\u0006\u0010\u0002\u001a\u00020\u0003H\u0002J\u0006\u0010,\u001a\u00020(R\u001b\u0010\u0005\u001a\u00020\u00068BX\u0082\u0084\u0002¢\u0006\f\n\u0004\b\t\u0010\n\u001a\u0004\b\u0007\u0010\bR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\fX\u0082.¢\u0006\u0002\n\u0000R\u001b\u0010\r\u001a\u00020\u000e8BX\u0082\u0084\u0002¢\u0006\f\n\u0004\b\u0011\u0010\n\u001a\u0004\b\u000f\u0010\u0010¨\u0006/"}, m607d2 = {"Lcom/glasssutdio/wear/wifi/WifiConnector;", "", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "connectivityManager", "Landroid/net/ConnectivityManager;", "getConnectivityManager", "()Landroid/net/ConnectivityManager;", "connectivityManager$delegate", "Lkotlin/Lazy;", "networkCallback", "Landroid/net/ConnectivityManager$NetworkCallback;", "wifiManager", "Landroid/net/wifi/WifiManager;", "getWifiManager", "()Landroid/net/wifi/WifiManager;", "wifiManager$delegate", "checkNetworkStatus", "", "connectToWifi", "", "ssid", "password", "callback", "Lcom/glasssutdio/wear/wifi/WifiConnector$WifiConnectCallback;", "connectWithLegacyApi", "timeoutHandler", "Landroid/os/Handler;", "timeoutRunnable", "Ljava/lang/Runnable;", "connectWithNewApi", "createWifiConfig", "Landroid/net/wifi/WifiConfiguration;", "disconnect", "getCurrentSSID", "getDeviceIpAddress", "getNetworkDebugInfo", "handleLegacyConnection", "isConnectedToWifi", "", "isDnsWorking", "isInternetAvailable", "isIpAddressValid", "isNetworkValid", "Companion", "WifiConnectCallback", "app_release"}, m608k = 1, m609mv = {1, 9, 0}, m611xi = 48)
/* loaded from: classes.dex */
public final class WifiConnector {

    /* renamed from: Companion, reason: from kotlin metadata */
    public static final Companion INSTANCE = new Companion(null);
    private static final long TIMEOUT_DURATION = 15000;

    /* renamed from: connectivityManager$delegate, reason: from kotlin metadata */
    private final Lazy connectivityManager;
    private final Context context;
    private ConnectivityManager.NetworkCallback networkCallback;

    /* renamed from: wifiManager$delegate, reason: from kotlin metadata */
    private final Lazy wifiManager;

    /* compiled from: WifiConnector.kt */
    @Metadata(m606d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\bf\u0018\u00002\u00020\u0001J\u0010\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H&J\u0012\u0010\u0006\u001a\u00020\u00032\b\u0010\u0007\u001a\u0004\u0018\u00010\bH&¨\u0006\t"}, m607d2 = {"Lcom/glasssutdio/wear/wifi/WifiConnector$WifiConnectCallback;", "", "onFailure", "", "errorMessage", "", "onSuccess", "network", "Landroid/net/Network;", "app_release"}, m608k = 1, m609mv = {1, 9, 0}, m611xi = 48)
    public interface WifiConnectCallback {
        void onFailure(String errorMessage);

        void onSuccess(Network network);
    }

    public WifiConnector(Context context) {
        Intrinsics.checkNotNullParameter(context, "context");
        this.context = context;
        this.wifiManager = LazyKt.lazy(new Function0<WifiManager>() { // from class: com.glasssutdio.wear.wifi.WifiConnector$wifiManager$2
            {
                super(0);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // kotlin.jvm.functions.Function0
            public final WifiManager invoke() {
                Object systemService = this.this$0.context.getApplicationContext().getSystemService("wifi");
                Intrinsics.checkNotNull(systemService, "null cannot be cast to non-null type android.net.wifi.WifiManager");
                return (WifiManager) systemService;
            }
        });
        this.connectivityManager = LazyKt.lazy(new Function0<ConnectivityManager>() { // from class: com.glasssutdio.wear.wifi.WifiConnector$connectivityManager$2
            {
                super(0);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // kotlin.jvm.functions.Function0
            public final ConnectivityManager invoke() {
                Object systemService = this.this$0.context.getApplicationContext().getSystemService("connectivity");
                Intrinsics.checkNotNull(systemService, "null cannot be cast to non-null type android.net.ConnectivityManager");
                return (ConnectivityManager) systemService;
            }
        });
    }

    private final WifiManager getWifiManager() {
        return (WifiManager) this.wifiManager.getValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final ConnectivityManager getConnectivityManager() {
        return (ConnectivityManager) this.connectivityManager.getValue();
    }

    public final void connectToWifi(String ssid, String password, final WifiConnectCallback callback) {
        Intrinsics.checkNotNullParameter(ssid, "ssid");
        Intrinsics.checkNotNullParameter(password, "password");
        Intrinsics.checkNotNullParameter(callback, "callback");
        XLog.m127d("开始连接: " + ssid);
        Handler handler = new Handler(Looper.getMainLooper());
        Runnable runnable = new Runnable() { // from class: com.glasssutdio.wear.wifi.WifiConnector$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                WifiConnector.connectToWifi$lambda$0(callback);
            }
        };
        handler.postDelayed(runnable, TIMEOUT_DURATION);
        if (Build.VERSION.SDK_INT >= 29) {
            connectWithNewApi(ssid, password, callback, handler, runnable);
        } else {
            handleLegacyConnection(ssid, password, callback, handler, runnable);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void connectToWifi$lambda$0(WifiConnectCallback callback) {
        Intrinsics.checkNotNullParameter(callback, "$callback");
        callback.onFailure("连接超时");
    }

    private final void connectWithNewApi(String ssid, String password, final WifiConnectCallback callback, final Handler timeoutHandler, final Runnable timeoutRunnable) {
        WifiNetworkSpecifier wifiNetworkSpecifierBuild = new WifiNetworkSpecifier.Builder().setSsid(ssid).setWpa2Passphrase(password).build();
        Intrinsics.checkNotNullExpressionValue(wifiNetworkSpecifierBuild, "build(...)");
        getConnectivityManager().requestNetwork(new NetworkRequest.Builder().addTransportType(1).removeCapability(12).setNetworkSpecifier(wifiNetworkSpecifierBuild).build(), new ConnectivityManager.NetworkCallback() { // from class: com.glasssutdio.wear.wifi.WifiConnector.connectWithNewApi.1
            @Override // android.net.ConnectivityManager.NetworkCallback
            public void onAvailable(Network network) {
                Intrinsics.checkNotNullParameter(network, "network");
                timeoutHandler.removeCallbacks(timeoutRunnable);
                this.getConnectivityManager().bindProcessToNetwork(network);
                XLog.m127d("新API连接成功");
                callback.onSuccess(network);
            }

            @Override // android.net.ConnectivityManager.NetworkCallback
            public void onUnavailable() {
                timeoutHandler.removeCallbacks(timeoutRunnable);
                XLog.m132e("新API连接失败");
                callback.onFailure("网络不可用");
            }
        });
    }

    private final void handleLegacyConnection(final String ssid, final String password, final WifiConnectCallback callback, final Handler timeoutHandler, final Runnable timeoutRunnable) {
        if (!getWifiManager().isWifiEnabled()) {
            getWifiManager().setWifiEnabled(true);
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() { // from class: com.glasssutdio.wear.wifi.WifiConnector$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    WifiConnector.handleLegacyConnection$lambda$1(this.f$0, ssid, password, callback, timeoutHandler, timeoutRunnable);
                }
            }, 1000L);
        } else {
            connectWithLegacyApi(ssid, password, callback, timeoutHandler, timeoutRunnable);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void handleLegacyConnection$lambda$1(WifiConnector this$0, String ssid, String password, WifiConnectCallback callback, Handler timeoutHandler, Runnable timeoutRunnable) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        Intrinsics.checkNotNullParameter(ssid, "$ssid");
        Intrinsics.checkNotNullParameter(password, "$password");
        Intrinsics.checkNotNullParameter(callback, "$callback");
        Intrinsics.checkNotNullParameter(timeoutHandler, "$timeoutHandler");
        Intrinsics.checkNotNullParameter(timeoutRunnable, "$timeoutRunnable");
        this$0.connectWithLegacyApi(ssid, password, callback, timeoutHandler, timeoutRunnable);
    }

    private final void connectWithLegacyApi(String ssid, String password, WifiConnectCallback callback, Handler timeoutHandler, Runnable timeoutRunnable) {
        int iAddNetwork = getWifiManager().addNetwork(createWifiConfig(ssid, password));
        if (iAddNetwork == -1) {
            timeoutHandler.removeCallbacks(timeoutRunnable);
            callback.onFailure("添加网络失败");
            return;
        }
        boolean z = getWifiManager().enableNetwork(iAddNetwork, true) && getWifiManager().reconnect();
        timeoutHandler.removeCallbacks(timeoutRunnable);
        if (z) {
            XLog.m127d("旧API连接成功");
            callback.onSuccess(null);
        } else {
            callback.onFailure("启用网络失败");
        }
    }

    private final WifiConfiguration createWifiConfig(String ssid, String password) {
        WifiConfiguration wifiConfiguration = new WifiConfiguration();
        wifiConfiguration.SSID = "\"" + ssid + '\"';
        wifiConfiguration.preSharedKey = "\"" + password + '\"';
        wifiConfiguration.status = 2;
        wifiConfiguration.allowedKeyManagement.set(1);
        wifiConfiguration.allowedProtocols.set(1);
        wifiConfiguration.allowedProtocols.set(0);
        wifiConfiguration.allowedGroupCiphers.set(2);
        wifiConfiguration.allowedGroupCiphers.set(3);
        wifiConfiguration.allowedPairwiseCiphers.set(1);
        wifiConfiguration.allowedPairwiseCiphers.set(2);
        return wifiConfiguration;
    }

    public final boolean isNetworkValid() {
        NetworkCapabilities networkCapabilities;
        Network activeNetwork = getConnectivityManager().getActiveNetwork();
        return activeNetwork != null && (networkCapabilities = getConnectivityManager().getNetworkCapabilities(activeNetwork)) != null && networkCapabilities.hasCapability(16) && networkCapabilities.hasTransport(1);
    }

    public final void disconnect() {
        if (Build.VERSION.SDK_INT >= 29) {
            getConnectivityManager().bindProcessToNetwork(null);
        }
        getWifiManager().disconnect();
        XLog.m127d("已断开连接");
    }

    public final String getNetworkDebugInfo() {
        Network activeNetwork = getConnectivityManager().getActiveNetwork();
        if (activeNetwork == null) {
            return "无活动网络";
        }
        NetworkCapabilities networkCapabilities = getConnectivityManager().getNetworkCapabilities(activeNetwork);
        if (networkCapabilities == null) {
            return "无网络能力信息";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(", 已验证: " + networkCapabilities.hasCapability(16));
        sb.append(", VPN: " + networkCapabilities.hasCapability(15));
        String string = sb.toString();
        Intrinsics.checkNotNullExpressionValue(string, "toString(...)");
        return string;
    }

    private final boolean isConnectedToWifi(Context context) {
        Object systemService = context.getApplicationContext().getSystemService("wifi");
        Intrinsics.checkNotNull(systemService, "null cannot be cast to non-null type android.net.wifi.WifiManager");
        WifiInfo connectionInfo = ((WifiManager) systemService).getConnectionInfo();
        Intrinsics.checkNotNullExpressionValue(connectionInfo, "getConnectionInfo(...)");
        return connectionInfo.getNetworkId() != -1;
    }

    private final String getCurrentSSID(Context context) {
        Object systemService = context.getApplicationContext().getSystemService("wifi");
        Intrinsics.checkNotNull(systemService, "null cannot be cast to non-null type android.net.wifi.WifiManager");
        WifiInfo connectionInfo = ((WifiManager) systemService).getConnectionInfo();
        Intrinsics.checkNotNullExpressionValue(connectionInfo, "getConnectionInfo(...)");
        return connectionInfo.getSSID();
    }

    private final String getDeviceIpAddress(Context context) {
        Object systemService = context.getApplicationContext().getSystemService("wifi");
        Intrinsics.checkNotNull(systemService, "null cannot be cast to non-null type android.net.wifi.WifiManager");
        WifiInfo connectionInfo = ((WifiManager) systemService).getConnectionInfo();
        Intrinsics.checkNotNullExpressionValue(connectionInfo, "getConnectionInfo(...)");
        String ipAddress = Formatter.formatIpAddress(connectionInfo.getIpAddress());
        Intrinsics.checkNotNullExpressionValue(ipAddress, "formatIpAddress(...)");
        return ipAddress;
    }

    private final boolean isIpAddressValid(Context context) {
        XLog.m137i(getDeviceIpAddress(context));
        return !Intrinsics.areEqual(r2, "0.0.0.0");
    }

    private final boolean isInternetAvailable() {
        try {
            return InetAddress.getByName("8.8.8.8").isReachable(DownloadTask.Builder.DEFAULT_SYNC_BUFFER_INTERVAL_MILLIS);
        } catch (IOException unused) {
            return false;
        }
    }

    private final boolean isDnsWorking() {
        try {
            return InetAddress.getByName("www.google.com").isReachable(DownloadTask.Builder.DEFAULT_SYNC_BUFFER_INTERVAL_MILLIS);
        } catch (IOException unused) {
            return false;
        }
    }

    public final String checkNetworkStatus(Context context) {
        Intrinsics.checkNotNullParameter(context, "context");
        if (!isConnectedToWifi(context)) {
            return "设备未连接到 Wi-Fi 网络";
        }
        if (!isIpAddressValid(context)) {
            return "设备未获得有效的 IP 地址";
        }
        if (!isInternetAvailable()) {
            return "设备无法访问互联网";
        }
        if (!isDnsWorking()) {
            return "DNS 配置错误，无法解析域名";
        }
        return "网络连接正常，能够访问互联网";
    }

    /* compiled from: WifiConnector.kt */
    @Metadata(m606d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u0011\n\u0002\u0010\u000e\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u0011\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006¢\u0006\u0002\u0010\bR\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T¢\u0006\u0002\n\u0000¨\u0006\t"}, m607d2 = {"Lcom/glasssutdio/wear/wifi/WifiConnector$Companion;", "", "()V", "TIMEOUT_DURATION", "", "requiredPermissions", "", "", "()[Ljava/lang/String;", "app_release"}, m608k = 1, m609mv = {1, 9, 0}, m611xi = 48)
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        public final String[] requiredPermissions() {
            return new String[]{"android.permission.ACCESS_WIFI_STATE", "android.permission.CHANGE_WIFI_STATE", Permission.ACCESS_FINE_LOCATION};
        }
    }
}
