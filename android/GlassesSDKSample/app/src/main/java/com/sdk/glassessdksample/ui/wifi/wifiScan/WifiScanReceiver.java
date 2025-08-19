package com.glasssutdio.wear.wifi.wifiScan;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/* loaded from: classes.dex */
public class WifiScanReceiver extends BroadcastReceiver {
    private final WifiScanCallback callback;

    public WifiScanReceiver(WifiScanCallback callback) {
        this.callback = callback;
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        this.callback.onScanResultsReady();
    }
}
