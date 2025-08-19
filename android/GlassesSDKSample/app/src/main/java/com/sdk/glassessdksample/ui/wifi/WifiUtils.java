package com.glasssutdio.wear.wifi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Toast;
import androidx.core.util.Consumer;
import com.glasssutdio.wear.wifi.WifiConnectorBuilder;
import com.glasssutdio.wear.wifi.WifiUtils;
import com.glasssutdio.wear.wifi.utils.Elvis;
import com.glasssutdio.wear.wifi.utils.VersionUtils;
import com.glasssutdio.wear.wifi.wifiConnect.ConnectionErrorCode;
import com.glasssutdio.wear.wifi.wifiConnect.ConnectionScanResultsListener;
import com.glasssutdio.wear.wifi.wifiConnect.ConnectionSuccessListener;
import com.glasssutdio.wear.wifi.wifiConnect.DisconnectCallbackHolder;
import com.glasssutdio.wear.wifi.wifiConnect.TimeoutHandler;
import com.glasssutdio.wear.wifi.wifiConnect.WifiConnectionCallback;
import com.glasssutdio.wear.wifi.wifiConnect.WifiConnectionReceiver;
import com.glasssutdio.wear.wifi.wifiDisconnect.DisconnectionErrorCode;
import com.glasssutdio.wear.wifi.wifiDisconnect.DisconnectionSuccessListener;
import com.glasssutdio.wear.wifi.wifiRemove.RemoveErrorCode;
import com.glasssutdio.wear.wifi.wifiRemove.RemoveSuccessListener;
import com.glasssutdio.wear.wifi.wifiScan.ScanResultsListener;
import com.glasssutdio.wear.wifi.wifiScan.WifiScanCallback;
import com.glasssutdio.wear.wifi.wifiScan.WifiScanReceiver;
import com.glasssutdio.wear.wifi.wifiState.WifiStateCallback;
import com.glasssutdio.wear.wifi.wifiState.WifiStateListener;
import com.glasssutdio.wear.wifi.wifiState.WifiStateReceiver;
import com.glasssutdio.wear.wifi.wifiWps.ConnectionWpsListener;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public final class WifiUtils implements WifiConnectorBuilder, WifiConnectorBuilder.WifiUtilsBuilder, WifiConnectorBuilder.WifiSuccessListener, WifiConnectorBuilder.WifiWpsSuccessListener {
    private static final String TAG = "WifiUtils";
    private static Logger customLogger = null;
    private static boolean mEnableLog = true;
    private String mBssid;
    private ConnectionScanResultsListener mConnectionScanResultsListener;
    private ConnectionSuccessListener mConnectionSuccessListener;
    private ConnectionWpsListener mConnectionWpsListener;
    private final ConnectivityManager mConnectivityManager;
    private final Context mContext;
    private WeakHandler mHandler;
    private String mPassword;
    private boolean mPatternMatch;
    private ScanResultsListener mScanResultsListener;
    private ScanResult mSingleScanResult;
    private String mSsid;
    private final TimeoutHandler mTimeoutHandler;
    private final WifiConnectionCallback mWifiConnectionCallback;
    private final WifiConnectionReceiver mWifiConnectionReceiver;
    private final WifiManager mWifiManager;
    private final WifiScanReceiver mWifiScanReceiver;
    private final WifiScanCallback mWifiScanResultsCallback;
    private final WifiStateCallback mWifiStateCallback;
    private WifiStateListener mWifiStateListener;
    private final WifiStateReceiver mWifiStateReceiver;
    private String type;
    private long mWpsTimeoutMillis = 30000;
    private long mTimeoutMillis = 30000;

    /* renamed from: com.glasssutdio.wear.wifi.WifiUtils$1 */
    class C11431 implements WifiStateCallback {
        C11431() {
        }

        @Override // com.glasssutdio.wear.wifi.wifiState.WifiStateCallback
        public void onWifiEnabled() {
            WifiUtils.wifiLog("WIFI ENABLED...");
            ConnectorUtils.unregisterReceiver(WifiUtils.this.mContext, WifiUtils.this.mWifiStateReceiver);
            Elvis.m176of(WifiUtils.this.mWifiStateListener).ifPresent(new Consumer() { // from class: com.glasssutdio.wear.wifi.WifiUtils$1$$ExternalSyntheticLambda0
                @Override // androidx.core.util.Consumer
                public final void accept(Object obj) {
                    ((WifiStateListener) obj).isSuccess(true);
                }
            });
            if (WifiUtils.this.mScanResultsListener == null && WifiUtils.this.mPassword == null) {
                return;
            }
            WifiUtils.wifiLog("START SCANNING....");
            if (WifiUtils.this.mWifiManager.startScan()) {
                ConnectorUtils.registerReceiver(WifiUtils.this.mContext, WifiUtils.this.mWifiScanReceiver, new IntentFilter("android.net.wifi.SCAN_RESULTS"));
                return;
            }
            Elvis.m176of(WifiUtils.this.mScanResultsListener).ifPresent(new Consumer() { // from class: com.glasssutdio.wear.wifi.WifiUtils$1$$ExternalSyntheticLambda1
                @Override // androidx.core.util.Consumer
                public final void accept(Object obj) {
                    ((ScanResultsListener) obj).onScanResults(new ArrayList());
                }
            });
            Elvis.m176of(WifiUtils.this.mConnectionWpsListener).ifPresent(new Consumer() { // from class: com.glasssutdio.wear.wifi.WifiUtils$1$$ExternalSyntheticLambda2
                @Override // androidx.core.util.Consumer
                public final void accept(Object obj) {
                    ((ConnectionWpsListener) obj).isSuccessful(false);
                }
            });
            WifiUtils.this.mWifiConnectionCallback.errorConnect(ConnectionErrorCode.COULD_NOT_SCAN);
            WifiUtils.wifiLog("ERROR COULDN'T SCAN");
        }
    }

    /* renamed from: com.glasssutdio.wear.wifi.WifiUtils$2 */
    class C11442 implements WifiScanCallback {
        C11442() {
        }

        @Override // com.glasssutdio.wear.wifi.wifiScan.WifiScanCallback
        public void onScanResultsReady() {
            WifiUtils.wifiLog("GOT SCAN RESULTS");
            ConnectorUtils.unregisterReceiver(WifiUtils.this.mContext, WifiUtils.this.mWifiScanReceiver);
            final List<ScanResult> scanResults = WifiUtils.this.mWifiManager.getScanResults();
            Elvis.m176of(WifiUtils.this.mScanResultsListener).ifPresent(new Consumer() { // from class: com.glasssutdio.wear.wifi.WifiUtils$2$$ExternalSyntheticLambda0
                @Override // androidx.core.util.Consumer
                public final void accept(Object obj) {
                    ((ScanResultsListener) obj).onScanResults(scanResults);
                }
            });
            Elvis.m176of(WifiUtils.this.mConnectionScanResultsListener).ifPresent(new Consumer() { // from class: com.glasssutdio.wear.wifi.WifiUtils$2$$ExternalSyntheticLambda1
                @Override // androidx.core.util.Consumer
                public final void accept(Object obj) {
                    this.f$0.m175x86baa88a(scanResults, (ConnectionScanResultsListener) obj);
                }
            });
            if (WifiUtils.this.mConnectionWpsListener == null || WifiUtils.this.mBssid == null || WifiUtils.this.mPassword == null) {
                if (WifiUtils.this.mSsid != null) {
                    if (WifiUtils.this.mBssid != null) {
                        WifiUtils wifiUtils = WifiUtils.this;
                        wifiUtils.mSingleScanResult = ConnectorUtils.matchScanResult(wifiUtils.mSsid, WifiUtils.this.mBssid, scanResults);
                    } else {
                        WifiUtils wifiUtils2 = WifiUtils.this;
                        wifiUtils2.mSingleScanResult = ConnectorUtils.matchScanResultSsid(wifiUtils2.mSsid, scanResults, WifiUtils.this.mPatternMatch);
                    }
                }
                if (WifiUtils.this.mSingleScanResult == null || WifiUtils.this.mPassword == null) {
                    if (ConnectorUtils.connectToWifiHidden(WifiUtils.this.mContext, WifiUtils.this.mWifiManager, WifiUtils.this.mConnectivityManager, WifiUtils.this.mHandler, WifiUtils.this.mSsid, WifiUtils.this.type, WifiUtils.this.mPassword, WifiUtils.this.mWifiConnectionCallback)) {
                        ConnectorUtils.registerReceiver(WifiUtils.this.mContext, WifiUtils.this.mWifiConnectionReceiver.connectWith(WifiUtils.this.mSsid, WifiUtils.this.mPassword, WifiUtils.this.mConnectivityManager), new IntentFilter("android.net.wifi.supplicant.STATE_CHANGE"));
                        ConnectorUtils.registerReceiver(WifiUtils.this.mContext, WifiUtils.this.mWifiConnectionReceiver, new IntentFilter("android.net.wifi.STATE_CHANGE"));
                        WifiUtils.this.mTimeoutHandler.startTimeout(WifiUtils.this.mSingleScanResult, WifiUtils.this.mTimeoutMillis);
                        return;
                    }
                    WifiUtils.this.mWifiConnectionCallback.errorConnect(ConnectionErrorCode.COULD_NOT_CONNECT);
                    return;
                }
                if (ConnectorUtils.connectToWifi(WifiUtils.this.mContext, WifiUtils.this.mWifiManager, WifiUtils.this.mConnectivityManager, WifiUtils.this.mHandler, WifiUtils.this.mSingleScanResult, WifiUtils.this.mPassword, WifiUtils.this.mWifiConnectionCallback, WifiUtils.this.mPatternMatch, WifiUtils.this.mSsid)) {
                    ConnectorUtils.registerReceiver(WifiUtils.this.mContext, WifiUtils.this.mWifiConnectionReceiver.connectWith(WifiUtils.this.mSingleScanResult, WifiUtils.this.mPassword, WifiUtils.this.mConnectivityManager), new IntentFilter("android.net.wifi.supplicant.STATE_CHANGE"));
                    ConnectorUtils.registerReceiver(WifiUtils.this.mContext, WifiUtils.this.mWifiConnectionReceiver, new IntentFilter("android.net.wifi.STATE_CHANGE"));
                    WifiUtils.this.mTimeoutHandler.startTimeout(WifiUtils.this.mSingleScanResult, WifiUtils.this.mTimeoutMillis);
                    return;
                }
                WifiUtils.this.mWifiConnectionCallback.errorConnect(ConnectionErrorCode.COULD_NOT_CONNECT);
                return;
            }
            WifiUtils wifiUtils3 = WifiUtils.this;
            wifiUtils3.mSingleScanResult = ConnectorUtils.matchScanResultBssid(wifiUtils3.mBssid, scanResults);
            if (WifiUtils.this.mSingleScanResult == null || !VersionUtils.isLollipopOrLater()) {
                if (WifiUtils.this.mSingleScanResult == null) {
                    WifiUtils.wifiLog("Couldn't find network. Possibly out of range");
                }
                WifiUtils.this.mConnectionWpsListener.isSuccessful(false);
                return;
            }
            ConnectorUtils.connectWps(WifiUtils.this.mWifiManager, WifiUtils.this.mHandler, WifiUtils.this.mSingleScanResult, WifiUtils.this.mPassword, WifiUtils.this.mWpsTimeoutMillis, WifiUtils.this.mConnectionWpsListener);
        }

        /* renamed from: lambda$onScanResultsReady$1$com-glasssutdio-wear-wifi-WifiUtils$2 */
        /* synthetic */ void m175x86baa88a(List list, ConnectionScanResultsListener connectionScanResultsListener) {
            WifiUtils.this.mSingleScanResult = connectionScanResultsListener.onConnectWithScanResult(list);
        }
    }

    /* renamed from: com.glasssutdio.wear.wifi.WifiUtils$3 */
    class C11453 implements WifiConnectionCallback {
        C11453() {
        }

        @Override // com.glasssutdio.wear.wifi.wifiConnect.WifiConnectionCallback
        public void successfulConnect() {
            WifiUtils.wifiLog("CONNECTED SUCCESSFULLY");
            ConnectorUtils.unregisterReceiver(WifiUtils.this.mContext, WifiUtils.this.mWifiConnectionReceiver);
            WifiUtils.this.mTimeoutHandler.stopTimeout();
            Elvis.m176of(WifiUtils.this.mConnectionSuccessListener).ifPresent(new Consumer() { // from class: com.glasssutdio.wear.wifi.WifiUtils$3$$ExternalSyntheticLambda0
                @Override // androidx.core.util.Consumer
                public final void accept(Object obj) {
                    ((ConnectionSuccessListener) obj).success();
                }
            });
        }

        @Override // com.glasssutdio.wear.wifi.wifiConnect.WifiConnectionCallback
        public void errorConnect(final ConnectionErrorCode connectionErrorCode) {
            ConnectorUtils.unregisterReceiver(WifiUtils.this.mContext, WifiUtils.this.mWifiConnectionReceiver);
            WifiUtils.this.mTimeoutHandler.stopTimeout();
            if (VersionUtils.isAndroidQOrLater()) {
                DisconnectCallbackHolder.getInstance().disconnect();
            }
            ConnectorUtils.reenableAllHotspots(WifiUtils.this.mWifiManager);
            Elvis.m176of(WifiUtils.this.mConnectionSuccessListener).ifPresent(new Consumer() { // from class: com.glasssutdio.wear.wifi.WifiUtils$3$$ExternalSyntheticLambda1
                @Override // androidx.core.util.Consumer
                public final void accept(Object obj) {
                    WifiUtils.C11453.lambda$errorConnect$0(connectionErrorCode, (ConnectionSuccessListener) obj);
                }
            });
        }

        static /* synthetic */ void lambda$errorConnect$0(ConnectionErrorCode connectionErrorCode, ConnectionSuccessListener connectionSuccessListener) {
            connectionSuccessListener.failed(connectionErrorCode);
            WifiUtils.wifiLog("DIDN'T CONNECT TO WIFI " + connectionErrorCode);
        }
    }

    private WifiUtils(Context context) {
        C11431 c11431 = new C11431();
        this.mWifiStateCallback = c11431;
        C11442 c11442 = new C11442();
        this.mWifiScanResultsCallback = c11442;
        C11453 c11453 = new C11453();
        this.mWifiConnectionCallback = c11453;
        this.mContext = context;
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService("wifi");
        this.mWifiManager = wifiManager;
        if (wifiManager == null) {
            throw new RuntimeException("WifiManager is not supposed to be null");
        }
        this.mConnectivityManager = (ConnectivityManager) context.getSystemService("connectivity");
        this.mWifiStateReceiver = new WifiStateReceiver(c11431);
        this.mWifiScanReceiver = new WifiScanReceiver(c11442);
        this.mHandler = new WeakHandler();
        this.mWifiConnectionReceiver = new WifiConnectionReceiver(c11453, wifiManager);
        this.mTimeoutHandler = new TimeoutHandler(wifiManager, this.mHandler, c11453);
    }

    private WifiUtils(Context context, Activity activityContext) {
        C11431 c11431 = new C11431();
        this.mWifiStateCallback = c11431;
        C11442 c11442 = new C11442();
        this.mWifiScanResultsCallback = c11442;
        C11453 c11453 = new C11453();
        this.mWifiConnectionCallback = c11453;
        this.mContext = context;
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService("wifi");
        this.mWifiManager = wifiManager;
        if (wifiManager == null) {
            throw new RuntimeException("WifiManager is not supposed to be null");
        }
        this.mConnectivityManager = (ConnectivityManager) context.getSystemService("connectivity");
        this.mWifiStateReceiver = new WifiStateReceiver(c11431);
        this.mWifiScanReceiver = new WifiScanReceiver(c11442);
        this.mHandler = new WeakHandler();
        this.mWifiConnectionReceiver = new WifiConnectionReceiver(c11453, wifiManager);
        this.mTimeoutHandler = new TimeoutHandler(wifiManager, this.mHandler, c11453);
    }

    public static WifiConnectorBuilder.WifiUtilsBuilder withContext(final Context context) {
        return new WifiUtils(context);
    }

    public static WifiConnectorBuilder.WifiUtilsBuilder withActivityContext(final Context context, Activity activity) {
        return new WifiUtils(context, activity);
    }

    public static void wifiLog(final String text) {
        if (mEnableLog) {
            ((Logger) Elvis.m176of(customLogger).orElse(new Logger() { // from class: com.glasssutdio.wear.wifi.WifiUtils$$ExternalSyntheticLambda4
                @Override // com.glasssutdio.wear.wifi.Logger
                public final void log(int i, String str, String str2) {
                    Log.println(i, WifiUtils.TAG, str2);
                }
            })).log(2, TAG, text);
        }
    }

    public static void enableLog(final boolean enabled) {
        mEnableLog = enabled;
    }

    public static void forwardLog(Logger logger) {
        customLogger = logger;
    }

    @Override // com.glasssutdio.wear.wifi.WifiConnectorBuilder.WifiUtilsBuilder
    public void enableWifi(final WifiStateListener wifiStateListener) {
        this.mWifiStateListener = wifiStateListener;
        if (this.mWifiManager.isWifiEnabled()) {
            this.mWifiStateCallback.onWifiEnabled();
            return;
        }
        Intent intentCheckVersionAndGetIntent = ConnectorUtils.checkVersionAndGetIntent();
        if (intentCheckVersionAndGetIntent == null) {
            if (this.mWifiManager.setWifiEnabled(true)) {
                ConnectorUtils.registerReceiver(this.mContext, this.mWifiStateReceiver, new IntentFilter("android.net.wifi.WIFI_STATE_CHANGED"));
                return;
            }
            Elvis.m176of(wifiStateListener).ifPresent(new Consumer() { // from class: com.glasssutdio.wear.wifi.WifiUtils$$ExternalSyntheticLambda0
                @Override // androidx.core.util.Consumer
                public final void accept(Object obj) {
                    ((WifiStateListener) obj).isSuccess(false);
                }
            });
            Elvis.m176of(this.mScanResultsListener).ifPresent(new Consumer() { // from class: com.glasssutdio.wear.wifi.WifiUtils$$ExternalSyntheticLambda1
                @Override // androidx.core.util.Consumer
                public final void accept(Object obj) {
                    ((ScanResultsListener) obj).onScanResults(new ArrayList());
                }
            });
            Elvis.m176of(this.mConnectionWpsListener).ifPresent(new Consumer() { // from class: com.glasssutdio.wear.wifi.WifiUtils$$ExternalSyntheticLambda2
                @Override // androidx.core.util.Consumer
                public final void accept(Object obj) {
                    ((ConnectionWpsListener) obj).isSuccessful(false);
                }
            });
            this.mWifiConnectionCallback.errorConnect(ConnectionErrorCode.COULD_NOT_ENABLE_WIFI);
            wifiLog("COULDN'T ENABLE WIFI");
            return;
        }
        startWifiSettingsIntent(intentCheckVersionAndGetIntent, false);
    }

    private void startWifiSettingsIntent(Intent intent, Boolean isSwitchingOff) {
        Context applicationContext = this.mContext.getApplicationContext();
        intent.setFlags(268435456);
        applicationContext.startActivity(intent);
        if (isSwitchingOff.booleanValue()) {
            return;
        }
        Toast.makeText(applicationContext, "Enable Wifi to proceed", 0).show();
    }

    @Override // com.glasssutdio.wear.wifi.WifiConnectorBuilder.WifiUtilsBuilder
    public void enableWifi() {
        enableWifi(null);
    }

    @Override // com.glasssutdio.wear.wifi.WifiConnectorBuilder.WifiUtilsBuilder
    public WifiConnectorBuilder scanWifi(final ScanResultsListener scanResultsListener) {
        this.mScanResultsListener = scanResultsListener;
        return this;
    }

    @Override // com.glasssutdio.wear.wifi.WifiConnectorBuilder.WifiUtilsBuilder
    @Deprecated
    public void disconnectFrom(final String ssid, final DisconnectionSuccessListener disconnectionSuccessListener) {
        disconnect(disconnectionSuccessListener);
    }

    @Override // com.glasssutdio.wear.wifi.WifiConnectorBuilder.WifiUtilsBuilder
    public void disconnect(DisconnectionSuccessListener disconnectionSuccessListener) {
        if (this.mConnectivityManager == null) {
            disconnectionSuccessListener.failed(DisconnectionErrorCode.COULD_NOT_GET_CONNECTIVITY_MANAGER);
            return;
        }
        if (this.mWifiManager == null) {
            disconnectionSuccessListener.failed(DisconnectionErrorCode.COULD_NOT_GET_WIFI_MANAGER);
            return;
        }
        if (VersionUtils.isAndroidQOrLater()) {
            DisconnectCallbackHolder.getInstance().unbindProcessFromNetwork();
            DisconnectCallbackHolder.getInstance().disconnect();
            disconnectionSuccessListener.success();
        } else if (ConnectorUtils.disconnectFromWifi(this.mWifiManager)) {
            disconnectionSuccessListener.success();
        } else {
            disconnectionSuccessListener.failed(DisconnectionErrorCode.COULD_NOT_DISCONNECT);
        }
    }

    @Override // com.glasssutdio.wear.wifi.WifiConnectorBuilder.WifiUtilsBuilder
    public void remove(String ssid, RemoveSuccessListener removeSuccessListener) {
        if (this.mConnectivityManager == null) {
            removeSuccessListener.failed(RemoveErrorCode.COULD_NOT_GET_CONNECTIVITY_MANAGER);
            return;
        }
        if (this.mWifiManager == null) {
            removeSuccessListener.failed(RemoveErrorCode.COULD_NOT_GET_WIFI_MANAGER);
            return;
        }
        if (VersionUtils.isAndroidQOrLater()) {
            DisconnectCallbackHolder.getInstance().unbindProcessFromNetwork();
            DisconnectCallbackHolder.getInstance().disconnect();
            removeSuccessListener.success();
        } else if (ConnectorUtils.removeWifi(this.mWifiManager, ssid)) {
            removeSuccessListener.success();
        } else {
            removeSuccessListener.failed(RemoveErrorCode.COULD_NOT_REMOVE);
        }
    }

    @Override // com.glasssutdio.wear.wifi.WifiConnectorBuilder.WifiUtilsBuilder
    public WifiConnectorBuilder.WifiUtilsBuilder patternMatch() {
        this.mPatternMatch = true;
        return this;
    }

    @Override // com.glasssutdio.wear.wifi.WifiConnectorBuilder.WifiUtilsBuilder
    public WifiConnectorBuilder.WifiSuccessListener connectWith(final String ssid) {
        this.mSsid = ssid;
        this.mPassword = "";
        return this;
    }

    @Override // com.glasssutdio.wear.wifi.WifiConnectorBuilder.WifiUtilsBuilder
    public WifiConnectorBuilder.WifiSuccessListener connectWith(final String ssid, final String password) {
        this.mSsid = ssid;
        this.mPassword = password;
        return this;
    }

    @Override // com.glasssutdio.wear.wifi.WifiConnectorBuilder.WifiUtilsBuilder
    public WifiConnectorBuilder.WifiSuccessListener connectWith(final String ssid, final String password, final TypeEnum type) {
        this.mSsid = ssid;
        this.mPassword = password;
        this.type = type.name();
        return this;
    }

    @Override // com.glasssutdio.wear.wifi.WifiConnectorBuilder.WifiUtilsBuilder
    public WifiConnectorBuilder.WifiSuccessListener connectWith(final String ssid, final String bssid, final String password) {
        this.mSsid = ssid;
        this.mBssid = bssid;
        this.mPassword = password;
        return this;
    }

    @Override // com.glasssutdio.wear.wifi.WifiConnectorBuilder.WifiUtilsBuilder
    public WifiConnectorBuilder.WifiSuccessListener connectWithScanResult(final String password, final ConnectionScanResultsListener connectionScanResultsListener) {
        this.mConnectionScanResultsListener = connectionScanResultsListener;
        this.mPassword = password;
        return this;
    }

    @Override // com.glasssutdio.wear.wifi.WifiConnectorBuilder.WifiUtilsBuilder
    public WifiConnectorBuilder.WifiWpsSuccessListener connectWithWps(final String bssid, final String password) {
        this.mBssid = bssid;
        this.mPassword = password;
        return this;
    }

    @Override // com.glasssutdio.wear.wifi.WifiConnectorBuilder.WifiUtilsBuilder
    public void cancelAutoConnect() {
        ConnectorUtils.unregisterReceiver(this.mContext, this.mWifiStateReceiver);
        ConnectorUtils.unregisterReceiver(this.mContext, this.mWifiScanReceiver);
        ConnectorUtils.unregisterReceiver(this.mContext, this.mWifiConnectionReceiver);
        Elvis.m176of(this.mSingleScanResult).ifPresent(new Consumer() { // from class: com.glasssutdio.wear.wifi.WifiUtils$$ExternalSyntheticLambda3
            @Override // androidx.core.util.Consumer
            public final void accept(Object obj) {
                this.f$0.m879lambda$cancelAutoConnect$4$comglasssutdiowearwifiWifiUtils((ScanResult) obj);
            }
        });
        ConnectorUtils.reenableAllHotspots(this.mWifiManager);
    }

    /* renamed from: lambda$cancelAutoConnect$4$com-glasssutdio-wear-wifi-WifiUtils, reason: not valid java name */
    /* synthetic */ void m879lambda$cancelAutoConnect$4$comglasssutdiowearwifiWifiUtils(ScanResult scanResult) {
        ConnectorUtils.cleanPreviousConfiguration(this.mWifiManager, scanResult);
    }

    @Override // com.glasssutdio.wear.wifi.WifiConnectorBuilder.WifiUtilsBuilder
    public boolean isWifiConnected(String ssid) {
        return ConnectorUtils.isAlreadyConnected(this.mWifiManager, this.mConnectivityManager, ssid);
    }

    @Override // com.glasssutdio.wear.wifi.WifiConnectorBuilder.WifiUtilsBuilder
    public boolean isWifiConnected() {
        return ConnectorUtils.isAlreadyConnected(this.mConnectivityManager);
    }

    @Override // com.glasssutdio.wear.wifi.WifiConnectorBuilder.WifiSuccessListener
    public WifiConnectorBuilder.WifiSuccessListener setTimeout(final long timeOutMillis) {
        this.mTimeoutMillis = timeOutMillis;
        return this;
    }

    @Override // com.glasssutdio.wear.wifi.WifiConnectorBuilder.WifiWpsSuccessListener
    public WifiConnectorBuilder.WifiWpsSuccessListener setWpsTimeout(final long timeOutMillis) {
        this.mWpsTimeoutMillis = timeOutMillis;
        return this;
    }

    @Override // com.glasssutdio.wear.wifi.WifiConnectorBuilder.WifiWpsSuccessListener
    public WifiConnectorBuilder onConnectionWpsResult(final ConnectionWpsListener successListener) {
        this.mConnectionWpsListener = successListener;
        return this;
    }

    @Override // com.glasssutdio.wear.wifi.WifiConnectorBuilder.WifiSuccessListener
    public WifiConnectorBuilder onConnectionResult(final ConnectionSuccessListener successListener) {
        this.mConnectionSuccessListener = successListener;
        return this;
    }

    @Override // com.glasssutdio.wear.wifi.WifiConnectorBuilder
    public void start() {
        ConnectorUtils.unregisterReceiver(this.mContext, this.mWifiStateReceiver);
        ConnectorUtils.unregisterReceiver(this.mContext, this.mWifiScanReceiver);
        ConnectorUtils.unregisterReceiver(this.mContext, this.mWifiConnectionReceiver);
        enableWifi(null);
    }

    @Override // com.glasssutdio.wear.wifi.WifiConnectorBuilder.WifiUtilsBuilder
    public void disableWifi() {
        if (this.mWifiManager.isWifiEnabled()) {
            Intent intentCheckVersionAndGetIntent = ConnectorUtils.checkVersionAndGetIntent();
            if (intentCheckVersionAndGetIntent == null) {
                this.mWifiManager.setWifiEnabled(false);
                ConnectorUtils.unregisterReceiver(this.mContext, this.mWifiStateReceiver);
                ConnectorUtils.unregisterReceiver(this.mContext, this.mWifiScanReceiver);
                ConnectorUtils.unregisterReceiver(this.mContext, this.mWifiConnectionReceiver);
            } else {
                startWifiSettingsIntent(intentCheckVersionAndGetIntent, true);
            }
        }
        wifiLog("WiFi Disabled");
    }
}
