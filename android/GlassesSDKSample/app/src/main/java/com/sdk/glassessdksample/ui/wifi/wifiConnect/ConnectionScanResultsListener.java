package com.glasssutdio.wear.wifi.wifiConnect;

import android.net.wifi.ScanResult;
import java.util.List;

/* loaded from: classes.dex */
public interface ConnectionScanResultsListener {
    ScanResult onConnectWithScanResult(List<ScanResult> scanResults);
}
