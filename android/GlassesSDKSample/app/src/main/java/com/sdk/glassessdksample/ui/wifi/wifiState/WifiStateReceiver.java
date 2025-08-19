package com.glasssutdio.wear.wifi.wifiState;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/* loaded from: classes.dex */
public final class WifiStateReceiver extends BroadcastReceiver {
    private final WifiStateCallback wifiStateCallback;

    public WifiStateReceiver(WifiStateCallback callbacks) {
        this.wifiStateCallback = callbacks;
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        if (intent.getIntExtra("wifi_state", 0) != 3) {
            return;
        }
        this.wifiStateCallback.onWifiEnabled();
    }
}
