package com.glasssutdio.wear.wifi.p2p;

import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Handler;
import android.os.Looper;
import com.elvishew.xlog.XLog;
import com.glasssutdio.wear.GlassesWearJavaApplication;
import com.oudmon.ble.base.communication.ILargeDataResponse;
import com.oudmon.ble.base.communication.LargeDataHandler;
import com.oudmon.ble.base.communication.bigData.resp.BaseResponse;
import com.oudmon.ble.base.communication.bigData.resp.GlassModelControlResponse;
import java.util.Collection;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: WifiP2pManagerSingleton.kt */
@Metadata(m606d1 = {"\u0000_\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0011*\u0001\u0016\u0018\u0000 ,2\u00020\u0001:\u0004,-./B\u000f\b\u0002\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0002\u0010\u0004J\u000e\u0010\u001e\u001a\u00020\u001f2\u0006\u0010\u0005\u001a\u00020\u0006J\u0006\u0010 \u001a\u00020\u001fJ\u0010\u0010!\u001a\u00020\u001f2\u0006\u0010\"\u001a\u00020\u001bH\u0007J\b\u0010#\u001a\u00020\u001fH\u0002J\u0006\u0010$\u001a\u00020\u001fJ\u0006\u0010%\u001a\u00020\u001fJ\u0006\u0010&\u001a\u00020\u001fJ\u0006\u0010'\u001a\u00020\u001fJ\u0006\u0010(\u001a\u00020\u001fJ\u000e\u0010)\u001a\u00020\u001f2\u0006\u0010\u000b\u001a\u00020\fJ\b\u0010*\u001a\u00020\u001fH\u0007J\u0006\u0010+\u001a\u00020\u001fR\u0010\u0010\u0005\u001a\u0004\u0018\u00010\u0006X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\fX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\fX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\bX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\u0010X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0011\u001a\u00020\u0012X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0013\u001a\u00020\u0014X\u0082\u0004¢\u0006\u0002\n\u0000R\u0012\u0010\u0015\u001a\u00020\u00168\u0002X\u0083\u0004¢\u0006\u0004\n\u0002\u0010\u0017R\u000e\u0010\u0018\u001a\u00020\u0019X\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\u001a\u001a\u0004\u0018\u00010\u001bX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u001c\u001a\u00020\u001dX\u0082\u000e¢\u0006\u0002\n\u0000¨\u00060"}, m607d2 = {"Lcom/glasssutdio/wear/wifi/p2p/WifiP2pManagerSingleton;", "", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "callback", "Lcom/glasssutdio/wear/wifi/p2p/WifiP2pManagerSingleton$WifiP2pCallback;", "connectRetry", "", "connectTimeOut", "Lcom/glasssutdio/wear/wifi/p2p/WifiP2pManagerSingleton$ConnectTimeOut;", "connected", "", "connecting", "discoveryRetry", "discoveryTimeOut", "Lcom/glasssutdio/wear/wifi/p2p/WifiP2pManagerSingleton$DiscoveryTimeOut;", "handler", "Landroid/os/Handler;", "intentFilter", "Landroid/content/IntentFilter;", "receiver", "com/glasssutdio/wear/wifi/p2p/WifiP2pManagerSingleton$receiver$1", "Lcom/glasssutdio/wear/wifi/p2p/WifiP2pManagerSingleton$receiver$1;", "wifiP2pChannel", "Landroid/net/wifi/p2p/WifiP2pManager$Channel;", "wifiP2pDevice", "Landroid/net/wifi/p2p/WifiP2pDevice;", "wifiP2pManager", "Landroid/net/wifi/p2p/WifiP2pManager;", "addCallback", "", "cancelP2pConnection", "connectToDevice", "device", "initP2P", "registerReceiver", "removeCallback", "resetDeviceP2p", "resetFailCount", "resetPeerDiscovery", "setConnect", "startPeerDiscovery", "unregisterReceiver", "Companion", "ConnectTimeOut", "DiscoveryTimeOut", "WifiP2pCallback", "app_release"}, m608k = 1, m609mv = {1, 9, 0}, m611xi = 48)
/* loaded from: classes.dex */
public final class WifiP2pManagerSingleton {

    /* renamed from: Companion, reason: from kotlin metadata */
    public static final Companion INSTANCE = new Companion(null);
    private static volatile WifiP2pManagerSingleton instance;
    private WifiP2pCallback callback;
    private int connectRetry;
    private final ConnectTimeOut connectTimeOut;
    private boolean connected;
    private boolean connecting;
    private final Context context;
    private int discoveryRetry;
    private final DiscoveryTimeOut discoveryTimeOut;
    private final Handler handler;
    private final IntentFilter intentFilter;
    private final WifiP2pManagerSingleton$receiver$1 receiver;
    private WifiP2pManager.Channel wifiP2pChannel;
    private WifiP2pDevice wifiP2pDevice;
    private WifiP2pManager wifiP2pManager;
    private boolean receiverRegistered;

    /* compiled from: WifiP2pManagerSingleton.kt */
    @Metadata(m606d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u001e\n\u0002\u0018\u0002\n\u0002\b\u0006\bf\u0018\u00002\u00020\u0001J\b\u0010\u0002\u001a\u00020\u0003H&J\u0010\u0010\u0004\u001a\u00020\u00032\u0006\u0010\u0005\u001a\u00020\u0006H&J\b\u0010\u0007\u001a\u00020\u0003H&J\u0010\u0010\b\u001a\u00020\u00032\u0006\u0010\u0005\u001a\u00020\u0006H&J\b\u0010\t\u001a\u00020\u0003H&J\u0012\u0010\n\u001a\u00020\u00032\b\u0010\u000b\u001a\u0004\u0018\u00010\fH&J\b\u0010\r\u001a\u00020\u0003H&J\u0010\u0010\u000e\u001a\u00020\u00032\u0006\u0010\u0005\u001a\u00020\u0006H&J\b\u0010\u000f\u001a\u00020\u0003H&J\u0016\u0010\u0010\u001a\u00020\u00032\f\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00130\u0012H&J\u0012\u0010\u0014\u001a\u00020\u00032\b\u0010\u0015\u001a\u0004\u0018\u00010\u0013H&J\b\u0010\u0016\u001a\u00020\u0003H&J\b\u0010\u0017\u001a\u00020\u0003H&J\b\u0010\u0018\u001a\u00020\u0003H&¨\u0006\u0019"}, m607d2 = {"Lcom/glasssutdio/wear/wifi/p2p/WifiP2pManagerSingleton$WifiP2pCallback;", "", "cancelConnect", "", "cancelConnectFail", "reason", "", "connecting", "onConnectRequestFailed", "onConnectRequestSent", "onConnected", "info", "Landroid/net/wifi/p2p/WifiP2pInfo;", "onDisconnected", "onPeerDiscoveryFailed", "onPeerDiscoveryStarted", "onPeersChanged", "peers", "", "Landroid/net/wifi/p2p/WifiP2pDevice;", "onThisDeviceChanged", "device", "onWifiP2pDisabled", "onWifiP2pEnabled", "retryAlsoFailed", "app_release"}, m608k = 1, m609mv = {1, 9, 0}, m611xi = 48)
    public interface WifiP2pCallback {
        void cancelConnect();

        void cancelConnectFail(int reason);

        void connecting();

        void onConnectRequestFailed(int reason);

        void onConnectRequestSent();

        void onConnected(WifiP2pInfo info);

        void onDisconnected();

        void onPeerDiscoveryFailed(int reason);

        void onPeerDiscoveryStarted();

        void onPeersChanged(Collection<? extends WifiP2pDevice> peers);

        void onThisDeviceChanged(WifiP2pDevice device);

        void onWifiP2pDisabled();

        void onWifiP2pEnabled();

        void retryAlsoFailed();
    }

    public /* synthetic */ WifiP2pManagerSingleton(Context context, DefaultConstructorMarker defaultConstructorMarker) {
        this(context);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void resetDeviceP2p$lambda$3(int i, GlassModelControlResponse glassModelControlResponse) {
    }

    private WifiP2pManagerSingleton(Context context) {
        this.context = context;
        Object systemService = context.getSystemService("wifip2p");
        Intrinsics.checkNotNull(systemService, "null cannot be cast to non-null type android.net.wifi.p2p.WifiP2pManager");
        WifiP2pManager wifiP2pManager = (WifiP2pManager) systemService;
        this.wifiP2pManager = wifiP2pManager;
        WifiP2pManager.Channel channelInitialize = wifiP2pManager.initialize(context, context.getMainLooper(), new WifiP2pManager.ChannelListener() { // from class: com.glasssutdio.wear.wifi.p2p.WifiP2pManagerSingleton$$ExternalSyntheticLambda1
            @Override // android.net.wifi.p2p.WifiP2pManager.ChannelListener
            public final void onChannelDisconnected() {
                XLog.m132e("wifiP2pChannel disconnect");
            }
        });
        Intrinsics.checkNotNullExpressionValue(channelInitialize, "initialize(...)");
        this.wifiP2pChannel = channelInitialize;
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.wifi.p2p.STATE_CHANGED");
        intentFilter.addAction("android.net.wifi.p2p.PEERS_CHANGED");
        intentFilter.addAction("android.net.wifi.p2p.CONNECTION_STATE_CHANGE");
        intentFilter.addAction("android.net.wifi.p2p.THIS_DEVICE_CHANGED");
        this.intentFilter = intentFilter;
        this.handler = new Handler(context.getMainLooper());
        this.discoveryTimeOut = new DiscoveryTimeOut(this);
        this.connectTimeOut = new ConnectTimeOut(this);
        this.receiver = new WifiP2pManagerSingleton$receiver$1(this);
    }

    public final void registerReceiver() {
        if (this.receiverRegistered) {
            return;
        }
        try {
            this.context.registerReceiver(this.receiver, this.intentFilter);
            this.receiverRegistered = true;
        } catch (Exception unused) {
        }
    }

    public final void resetPeerDiscovery() {
        this.handler.removeCallbacks(this.discoveryTimeOut);
    }

    public final void resetFailCount() {
        this.connectRetry = 0;
        this.discoveryRetry = 0;
        this.connecting = false;
        setConnect(false);
        this.handler.removeCallbacks(this.discoveryTimeOut);
        this.handler.removeCallbacks(this.connectTimeOut);
    }

    public final void startPeerDiscovery() {
        this.handler.postDelayed(this.discoveryTimeOut, 16000L);
        this.wifiP2pManager.discoverPeers(this.wifiP2pChannel, new WifiP2pManager.ActionListener() { // from class: com.glasssutdio.wear.wifi.p2p.WifiP2pManagerSingleton.startPeerDiscovery.1
            @Override // android.net.wifi.p2p.WifiP2pManager.ActionListener
            public void onSuccess() {
                WifiP2pCallback wifiP2pCallback = WifiP2pManagerSingleton.this.callback;
                if (wifiP2pCallback != null) {
                    wifiP2pCallback.onPeerDiscoveryStarted();
                }
            }

            @Override // android.net.wifi.p2p.WifiP2pManager.ActionListener
            public void onFailure(int reason) {
                WifiP2pManagerSingleton.this.handler.removeCallbacks(WifiP2pManagerSingleton.this.discoveryTimeOut);
                WifiP2pManagerSingleton.this.handler.postDelayed(WifiP2pManagerSingleton.this.discoveryTimeOut, 2000L);
                WifiP2pCallback wifiP2pCallback = WifiP2pManagerSingleton.this.callback;
                if (wifiP2pCallback != null) {
                    wifiP2pCallback.onPeerDiscoveryFailed(reason);
                }
            }
        });
    }

    public final void setConnect(boolean connected) {
        this.connected = connected;
    }

    public final void connectToDevice(WifiP2pDevice device) {
        Intrinsics.checkNotNullParameter(device, "device");
        try {
            resetPeerDiscovery();
            if (this.connected) {
                XLog.m137i("P2P已经连接上了，直接返回");
                return;
            }
            if (this.connecting) {
                WifiP2pCallback wifiP2pCallback = this.callback;
                if (wifiP2pCallback != null) {
                    wifiP2pCallback.connecting();
                }
                XLog.m137i("P2P正在连接,不调用连接返回");
                return;
            }
            this.handler.postDelayed(this.connectTimeOut, 15000L);
            this.wifiP2pDevice = device;
            WifiP2pConfig wifiP2pConfig = new WifiP2pConfig();
            wifiP2pConfig.deviceAddress = device.deviceAddress;
            wifiP2pConfig.wps.setup = 0;
            this.connecting = true;
            XLog.m137i("已经在连接设备:" + device.deviceName);
            this.wifiP2pManager.connect(this.wifiP2pChannel, wifiP2pConfig, new WifiP2pManager.ActionListener() { // from class: com.glasssutdio.wear.wifi.p2p.WifiP2pManagerSingleton.connectToDevice.1
                @Override // android.net.wifi.p2p.WifiP2pManager.ActionListener
                public void onSuccess() {
                    WifiP2pCallback wifiP2pCallback2 = WifiP2pManagerSingleton.this.callback;
                    if (wifiP2pCallback2 != null) {
                        wifiP2pCallback2.onConnectRequestSent();
                    }
                }

                @Override // android.net.wifi.p2p.WifiP2pManager.ActionListener
                public void onFailure(int reason) {
                    WifiP2pManagerSingleton.this.connecting = false;
                    WifiP2pCallback wifiP2pCallback2 = WifiP2pManagerSingleton.this.callback;
                    if (wifiP2pCallback2 != null) {
                        wifiP2pCallback2.onConnectRequestFailed(reason);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public final void cancelP2pConnection() {
        try {
            initP2P();
            this.wifiP2pManager.cancelConnect(this.wifiP2pChannel, new WifiP2pManager.ActionListener() { // from class: com.glasssutdio.wear.wifi.p2p.WifiP2pManagerSingleton.cancelP2pConnection.1
                @Override // android.net.wifi.p2p.WifiP2pManager.ActionListener
                public void onSuccess() {
                    WifiP2pCallback wifiP2pCallback = WifiP2pManagerSingleton.this.callback;
                    if (wifiP2pCallback != null) {
                        wifiP2pCallback.cancelConnect();
                    }
                }

                @Override // android.net.wifi.p2p.WifiP2pManager.ActionListener
                public void onFailure(int reason) {
                    WifiP2pCallback wifiP2pCallback = WifiP2pManagerSingleton.this.callback;
                    if (wifiP2pCallback != null) {
                        wifiP2pCallback.cancelConnectFail(reason);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public final void addCallback(WifiP2pCallback callback) {
        Intrinsics.checkNotNullParameter(callback, "callback");
        this.callback = callback;
    }

    public final void removeCallback() {
        instance = null;
    }

    public final void unregisterReceiver() {
        if (!this.receiverRegistered) {
            return;
        }
        try {
            this.context.unregisterReceiver(this.receiver);
        } catch (Exception unused) {
        } finally {
            this.receiverRegistered = false;
        }
    }

    /* compiled from: WifiP2pManagerSingleton.kt */
    @Metadata(m606d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\b\u0002\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0002\u0010\u0004J\b\u0010\u0005\u001a\u00020\u0006H\u0016R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0007"}, m607d2 = {"Lcom/glasssutdio/wear/wifi/p2p/WifiP2pManagerSingleton$DiscoveryTimeOut;", "Ljava/lang/Runnable;", "outer", "Lcom/glasssutdio/wear/wifi/p2p/WifiP2pManagerSingleton;", "(Lcom/glasssutdio/wear/wifi/p2p/WifiP2pManagerSingleton;)V", "run", "", "app_release"}, m608k = 1, m609mv = {1, 9, 0}, m611xi = 48)
    private static final class DiscoveryTimeOut implements Runnable {
        private final WifiP2pManagerSingleton outer;

        public DiscoveryTimeOut(WifiP2pManagerSingleton outer) {
            Intrinsics.checkNotNullParameter(outer, "outer");
            this.outer = outer;
        }

        @Override // java.lang.Runnable
        public void run() {
            XLog.m137i("内部扫描重试连接:" + this.outer.discoveryRetry);
            if (this.outer.discoveryRetry < 1) {
                XLog.m137i("内部扫描重试连接一次");
                this.outer.resetDeviceP2p();
                this.outer.initP2P();
                this.outer.startPeerDiscovery();
                this.outer.discoveryRetry++;
            }
        }
    }

    public final void resetDeviceP2p() {
        LargeDataHandler.getInstance().glassesControl(new byte[]{2, 1, 15}, new ILargeDataResponse() { // from class: com.glasssutdio.wear.wifi.p2p.WifiP2pManagerSingleton$$ExternalSyntheticLambda2
            @Override // com.oudmon.ble.base.communication.ILargeDataResponse
            public final void parseData(int i, BaseResponse baseResponse) {
                WifiP2pManagerSingleton.resetDeviceP2p$lambda$3(i, (GlassModelControlResponse) baseResponse);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void initP2P() {
        this.wifiP2pChannel.close();
        Object systemService = this.context.getSystemService("wifip2p");
        Intrinsics.checkNotNull(systemService, "null cannot be cast to non-null type android.net.wifi.p2p.WifiP2pManager");
        WifiP2pManager wifiP2pManager = (WifiP2pManager) systemService;
        this.wifiP2pManager = wifiP2pManager;
        WifiP2pManager.Channel channelInitialize = wifiP2pManager.initialize(this.context, Looper.getMainLooper(), new WifiP2pManager.ChannelListener() { // from class: com.glasssutdio.wear.wifi.p2p.WifiP2pManagerSingleton$$ExternalSyntheticLambda0
            @Override // android.net.wifi.p2p.WifiP2pManager.ChannelListener
            public final void onChannelDisconnected() {
                XLog.m137i("wifiP2pChannel initP2P");
            }
        });
        Intrinsics.checkNotNullExpressionValue(channelInitialize, "initialize(...)");
        this.wifiP2pChannel = channelInitialize;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* compiled from: WifiP2pManagerSingleton.kt */
    @Metadata(m606d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\b\u0002\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0002\u0010\u0004J\b\u0010\u0005\u001a\u00020\u0006H\u0016R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0007"}, m607d2 = {"Lcom/glasssutdio/wear/wifi/p2p/WifiP2pManagerSingleton$ConnectTimeOut;", "Ljava/lang/Runnable;", "outer", "Lcom/glasssutdio/wear/wifi/p2p/WifiP2pManagerSingleton;", "(Lcom/glasssutdio/wear/wifi/p2p/WifiP2pManagerSingleton;)V", "run", "", "app_release"}, m608k = 1, m609mv = {1, 9, 0}, m611xi = 48)
    static final class ConnectTimeOut implements Runnable {
        private final WifiP2pManagerSingleton outer;

        public ConnectTimeOut(WifiP2pManagerSingleton outer) {
            Intrinsics.checkNotNullParameter(outer, "outer");
            this.outer = outer;
        }

        @Override // java.lang.Runnable
        public void run() {
            this.outer.connecting = false;
            if (this.outer.connectRetry < 1) {
                WifiP2pDevice wifiP2pDevice = this.outer.wifiP2pDevice;
                if (wifiP2pDevice != null) {
                    XLog.m137i("内部连接重试连接一次");
                    this.outer.connectToDevice(wifiP2pDevice);
                }
                this.outer.connectRetry++;
                return;
            }
            XLog.m137i("不重连，等外部超时");
            WifiP2pCallback wifiP2pCallback = this.outer.callback;
            if (wifiP2pCallback != null) {
                wifiP2pCallback.retryAlsoFailed();
            }
        }
    }

    /* compiled from: WifiP2pManagerSingleton.kt */
    @Metadata(m606d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u0006\u0010\u0005\u001a\u00020\u0004R\u0010\u0010\u0003\u001a\u0004\u0018\u00010\u0004X\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006\u0006"}, m607d2 = {"Lcom/glasssutdio/wear/wifi/p2p/WifiP2pManagerSingleton$Companion;", "", "()V", "instance", "Lcom/glasssutdio/wear/wifi/p2p/WifiP2pManagerSingleton;", "getInstance", "app_release"}, m608k = 1, m609mv = {1, 9, 0}, m611xi = 48)
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        public final WifiP2pManagerSingleton getInstance() {
            WifiP2pManagerSingleton wifiP2pManagerSingleton = WifiP2pManagerSingleton.instance;
            if (wifiP2pManagerSingleton == null) {
                synchronized (this) {
                    wifiP2pManagerSingleton = WifiP2pManagerSingleton.instance;
                    if (wifiP2pManagerSingleton == null) {
                        Application application = GlassesWearJavaApplication.getInstance().getApplication();
                        Intrinsics.checkNotNullExpressionValue(application, "getApplication(...)");
                        wifiP2pManagerSingleton = new WifiP2pManagerSingleton(application, null);
                        Companion companion = WifiP2pManagerSingleton.INSTANCE;
                        WifiP2pManagerSingleton.instance = wifiP2pManagerSingleton;
                    }
                }
            }
            return wifiP2pManagerSingleton;
        }
    }
}
