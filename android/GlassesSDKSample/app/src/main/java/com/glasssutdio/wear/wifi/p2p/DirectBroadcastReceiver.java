package com.glasssutdio.wear.wifi.p2p;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import com.elvishew.xlog.XLog;
import java.util.Collection;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: DirectBroadcastReceiver.kt */
@Metadata(m606d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u0000 \u000f2\u00020\u0001:\u0001\u000fB\u001d\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007¢\u0006\u0002\u0010\bJ\u0018\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000eH\u0017R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0010"}, m607d2 = {"Lcom/glasssutdio/wear/wifi/p2p/DirectBroadcastReceiver;", "Landroid/content/BroadcastReceiver;", "wifiP2pManager", "Landroid/net/wifi/p2p/WifiP2pManager;", "wifiP2pChannel", "Landroid/net/wifi/p2p/WifiP2pManager$Channel;", "directActionListener", "Lcom/glasssutdio/wear/wifi/p2p/DirectActionListener;", "(Landroid/net/wifi/p2p/WifiP2pManager;Landroid/net/wifi/p2p/WifiP2pManager$Channel;Lcom/glasssutdio/wear/wifi/p2p/DirectActionListener;)V", "onReceive", "", "context", "Landroid/content/Context;", "intent", "Landroid/content/Intent;", "Companion", "app_release"}, m608k = 1, m609mv = {1, 9, 0}, m611xi = 48)
/* loaded from: classes.dex */
public final class DirectBroadcastReceiver extends BroadcastReceiver {

    /* renamed from: Companion, reason: from kotlin metadata */
    public static final Companion INSTANCE = new Companion(null);
    private final DirectActionListener directActionListener;
    private final WifiP2pManager.Channel wifiP2pChannel;
    private final WifiP2pManager wifiP2pManager;

    public DirectBroadcastReceiver(WifiP2pManager wifiP2pManager, WifiP2pManager.Channel wifiP2pChannel, DirectActionListener directActionListener) {
        Intrinsics.checkNotNullParameter(wifiP2pManager, "wifiP2pManager");
        Intrinsics.checkNotNullParameter(wifiP2pChannel, "wifiP2pChannel");
        Intrinsics.checkNotNullParameter(directActionListener, "directActionListener");
        this.wifiP2pManager = wifiP2pManager;
        this.wifiP2pChannel = wifiP2pChannel;
        this.directActionListener = directActionListener;
    }

    /* compiled from: DirectBroadcastReceiver.kt */
    @Metadata(m606d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u0006\u0010\u0003\u001a\u00020\u0004¨\u0006\u0005"}, m607d2 = {"Lcom/glasssutdio/wear/wifi/p2p/DirectBroadcastReceiver$Companion;", "", "()V", "getIntentFilter", "Landroid/content/IntentFilter;", "app_release"}, m608k = 1, m609mv = {1, 9, 0}, m611xi = 48)
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        public final IntentFilter getIntentFilter() {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.net.wifi.p2p.STATE_CHANGED");
            intentFilter.addAction("android.net.wifi.p2p.PEERS_CHANGED");
            intentFilter.addAction("android.net.wifi.p2p.CONNECTION_STATE_CHANGE");
            intentFilter.addAction("android.net.wifi.p2p.THIS_DEVICE_CHANGED");
            return intentFilter;
        }
    }

    /* JADX WARN: Failed to restore switch over string. Please report as a decompilation issue */
    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        WifiP2pDevice wifiP2pDevice;
        Intrinsics.checkNotNullParameter(context, "context");
        Intrinsics.checkNotNullParameter(intent, "intent");
        String action = intent.getAction();
        if (action != null) {
            switch (action.hashCode()) {
                case -1772632330:
                    if (action.equals("android.net.wifi.p2p.CONNECTION_STATE_CHANGE")) {
                        NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra("networkInfo");
                        XLog.m137i("WIFI_P2P_CONNECTION_CHANGED_ACTION ： " + (networkInfo != null ? Boolean.valueOf(networkInfo.isConnected()) : null));
                        if (networkInfo != null && networkInfo.isConnected()) {
                            this.wifiP2pManager.requestConnectionInfo(this.wifiP2pChannel, new WifiP2pManager.ConnectionInfoListener() { // from class: com.glasssutdio.wear.wifi.p2p.DirectBroadcastReceiver$$ExternalSyntheticLambda1
                                @Override // android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener
                                public final void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
                                    DirectBroadcastReceiver.onReceive$lambda$1(this.f$0, wifiP2pInfo);
                                }
                            });
                            XLog.m137i("已连接 P2P 设备");
                            break;
                        } else {
                            this.directActionListener.onDisconnection();
                            break;
                        }
                    }
                    break;
                case -1566767901:
                    if (action.equals("android.net.wifi.p2p.THIS_DEVICE_CHANGED") && (wifiP2pDevice = (WifiP2pDevice) intent.getParcelableExtra("wifiP2pDevice")) != null) {
                        this.directActionListener.onSelfDeviceAvailable(wifiP2pDevice);
                        break;
                    }
                    break;
                case -1394739139:
                    if (action.equals("android.net.wifi.p2p.PEERS_CHANGED")) {
                        this.wifiP2pManager.requestPeers(this.wifiP2pChannel, new WifiP2pManager.PeerListListener() { // from class: com.glasssutdio.wear.wifi.p2p.DirectBroadcastReceiver$$ExternalSyntheticLambda0
                            @Override // android.net.wifi.p2p.WifiP2pManager.PeerListListener
                            public final void onPeersAvailable(WifiP2pDeviceList wifiP2pDeviceList) {
                                DirectBroadcastReceiver.onReceive$lambda$0(this.f$0, wifiP2pDeviceList);
                            }
                        });
                        break;
                    }
                    break;
                case 1695662461:
                    if (action.equals("android.net.wifi.p2p.STATE_CHANGED")) {
                        boolean z = intent.getIntExtra("wifi_p2p_state", -1) == 2;
                        this.directActionListener.wifiP2pEnabled(z);
                        if (!z) {
                            this.directActionListener.onPeersAvailable(CollectionsKt.emptyList());
                        }
                        XLog.m137i("WIFI_P2P_STATE_CHANGED_ACTION： " + z);
                        break;
                    }
                    break;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void onReceive$lambda$0(DirectBroadcastReceiver this$0, WifiP2pDeviceList wifiP2pDeviceList) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        DirectActionListener directActionListener = this$0.directActionListener;
        Collection<WifiP2pDevice> deviceList = wifiP2pDeviceList.getDeviceList();
        Intrinsics.checkNotNullExpressionValue(deviceList, "getDeviceList(...)");
        directActionListener.onPeersAvailable(CollectionsKt.toList(deviceList));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void onReceive$lambda$1(DirectBroadcastReceiver this$0, WifiP2pInfo wifiP2pInfo) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        if (wifiP2pInfo != null) {
            this$0.directActionListener.onConnectionInfoAvailable(wifiP2pInfo);
        }
    }
}
