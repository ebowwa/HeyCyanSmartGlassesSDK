package com.sdk.glassessdksample.ui.wifi.p2p;

import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import java.util.List;
import kotlin.Metadata;

/* compiled from: DirectBroadcastReceiver.kt */
@Metadata(m606d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0000\bf\u0018\u00002\u00020\u0001J\u0010\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H&J\b\u0010\u0006\u001a\u00020\u0003H&J\u0016\u0010\u0007\u001a\u00020\u00032\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\n0\tH&J\u0010\u0010\u000b\u001a\u00020\u00032\u0006\u0010\f\u001a\u00020\nH&J\u0010\u0010\r\u001a\u00020\u00032\u0006\u0010\u000e\u001a\u00020\u000fH&¨\u0006\u0010"}, m607d2 = {"Lcom/glasssutdio/wear/wifi/p2p/DirectActionListener;", "Landroid/net/wifi/p2p/WifiP2pManager$ChannelListener;", "onConnectionInfoAvailable", "", "wifiP2pInfo", "Landroid/net/wifi/p2p/WifiP2pInfo;", "onDisconnection", "onPeersAvailable", "devices", "", "Landroid/net/wifi/p2p/WifiP2pDevice;", "onSelfDeviceAvailable", "device", "wifiP2pEnabled", "enabled", "", "app_release"}, m608k = 1, m609mv = {1, 9, 0}, m611xi = 48)
/* loaded from: classes.dex */
public interface DirectActionListener extends WifiP2pManager.ChannelListener {
    void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo);

    void onDisconnection();

    void onPeersAvailable(List<? extends WifiP2pDevice> devices);

    void onSelfDeviceAvailable(WifiP2pDevice device);

    void wifiP2pEnabled(boolean enabled);
}
