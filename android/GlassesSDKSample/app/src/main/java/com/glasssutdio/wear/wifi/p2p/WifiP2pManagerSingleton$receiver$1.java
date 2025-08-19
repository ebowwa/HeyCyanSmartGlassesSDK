package com.glasssutdio.wear.wifi.p2p;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import com.glasssutdio.wear.wifi.p2p.WifiP2pManagerSingleton;
import java.util.Collection;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: WifiP2pManagerSingleton.kt */
@Metadata(m606d1 = {"\u0000\u001d\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000*\u0001\u0000\b\n\u0018\u00002\u00020\u0001J\u0018\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\u0016¨\u0006\b"}, m607d2 = {"com/glasssutdio/wear/wifi/p2p/WifiP2pManagerSingleton$receiver$1", "Landroid/content/BroadcastReceiver;", "onReceive", "", "context", "Landroid/content/Context;", "intent", "Landroid/content/Intent;", "app_release"}, m608k = 1, m609mv = {1, 9, 0}, m611xi = 48)
/* loaded from: classes.dex */
public final class WifiP2pManagerSingleton$receiver$1 extends BroadcastReceiver {
    final /* synthetic */ WifiP2pManagerSingleton this$0;

    WifiP2pManagerSingleton$receiver$1(WifiP2pManagerSingleton wifiP2pManagerSingleton) {
        this.this$0 = wifiP2pManagerSingleton;
    }

    /* JADX WARN: Failed to restore switch over string. Please report as a decompilation issue */
    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        Intrinsics.checkNotNullParameter(context, "context");
        Intrinsics.checkNotNullParameter(intent, "intent");
        String action = intent.getAction();
        if (action != null) {
            switch (action.hashCode()) {
                case -1772632330:
                    if (action.equals("android.net.wifi.p2p.CONNECTION_STATE_CHANGE")) {
                        NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra("networkInfo");
                        WifiP2pInfo wifiP2pInfo = (WifiP2pInfo) intent.getParcelableExtra("wifiP2pInfo");
                        if (networkInfo != null && networkInfo.isConnected()) {
                            this.this$0.connecting = false;
                            WifiP2pManagerSingleton.WifiP2pCallback wifiP2pCallback = this.this$0.callback;
                            if (wifiP2pCallback != null) {
                                wifiP2pCallback.onConnected(wifiP2pInfo);
                            }
                            this.this$0.handler.removeCallbacks(this.this$0.connectTimeOut);
                            break;
                        } else {
                            WifiP2pManagerSingleton.WifiP2pCallback wifiP2pCallback2 = this.this$0.callback;
                            if (wifiP2pCallback2 != null) {
                                wifiP2pCallback2.onDisconnected();
                                break;
                            }
                        }
                    }
                    break;
                case -1566767901:
                    if (action.equals("android.net.wifi.p2p.THIS_DEVICE_CHANGED")) {
                        WifiP2pDevice wifiP2pDevice = (WifiP2pDevice) intent.getParcelableExtra("wifiP2pDevice");
                        WifiP2pManagerSingleton.WifiP2pCallback wifiP2pCallback3 = this.this$0.callback;
                        if (wifiP2pCallback3 != null) {
                            wifiP2pCallback3.onThisDeviceChanged(wifiP2pDevice);
                            break;
                        }
                    }
                    break;
                case -1394739139:
                    if (action.equals("android.net.wifi.p2p.PEERS_CHANGED")) {
                        WifiP2pManager wifiP2pManager = this.this$0.wifiP2pManager;
                        WifiP2pManager.Channel channel = this.this$0.wifiP2pChannel;
                        final WifiP2pManagerSingleton wifiP2pManagerSingleton = this.this$0;
                        wifiP2pManager.requestPeers(channel, new WifiP2pManager.PeerListListener() { // from class: com.glasssutdio.wear.wifi.p2p.WifiP2pManagerSingleton$receiver$1$$ExternalSyntheticLambda0
                            @Override // android.net.wifi.p2p.WifiP2pManager.PeerListListener
                            public final void onPeersAvailable(WifiP2pDeviceList wifiP2pDeviceList) {
                                WifiP2pManagerSingleton$receiver$1.onReceive$lambda$0(wifiP2pManagerSingleton, wifiP2pDeviceList);
                            }
                        });
                        break;
                    }
                    break;
                case 1695662461:
                    if (action.equals("android.net.wifi.p2p.STATE_CHANGED")) {
                        try {
                            if (intent.getIntExtra("wifi_p2p_state", -1) == 2) {
                                WifiP2pManagerSingleton.WifiP2pCallback wifiP2pCallback4 = this.this$0.callback;
                                if (wifiP2pCallback4 != null) {
                                    wifiP2pCallback4.onWifiP2pEnabled();
                                }
                            } else {
                                WifiP2pManagerSingleton.WifiP2pCallback wifiP2pCallback5 = this.this$0.callback;
                                if (wifiP2pCallback5 != null) {
                                    wifiP2pCallback5.onWifiP2pDisabled();
                                }
                            }
                            break;
                        } catch (Exception e) {
                            e.printStackTrace();
                            return;
                        }
                    }
                    break;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void onReceive$lambda$0(WifiP2pManagerSingleton this$0, WifiP2pDeviceList wifiP2pDeviceList) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        WifiP2pManagerSingleton.WifiP2pCallback wifiP2pCallback = this$0.callback;
        if (wifiP2pCallback != null) {
            Collection<WifiP2pDevice> deviceList = wifiP2pDeviceList.getDeviceList();
            Intrinsics.checkNotNullExpressionValue(deviceList, "getDeviceList(...)");
            wifiP2pCallback.onPeersChanged(deviceList);
        }
    }
}
