package com.glasssutdio.wear.wifi.wifiConnect;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import androidx.arch.core.util.Function;
import com.glasssutdio.wear.wifi.ConnectorUtils;
import com.glasssutdio.wear.wifi.WeakHandler;
import com.glasssutdio.wear.wifi.WifiUtils;
import com.glasssutdio.wear.wifi.utils.Elvis;
import com.glasssutdio.wear.wifi.utils.VersionUtils;

/* loaded from: classes.dex */
public class TimeoutHandler {
    private final WeakHandler mHandler;
    private ScanResult mScanResult;
    private final WifiConnectionCallback mWifiConnectionCallback;
    private final WifiManager mWifiManager;
    private final Runnable timeoutCallback = new RunnableC11491();

    /* renamed from: com.glasssutdio.wear.wifi.wifiConnect.TimeoutHandler$1 */
    class RunnableC11491 implements Runnable {
        RunnableC11491() {
        }

        @Override // java.lang.Runnable
        public void run() {
            WifiUtils.wifiLog("Connection Timed out...");
            if (!VersionUtils.isAndroidQOrLater()) {
                ConnectorUtils.reEnableNetworkIfPossible(TimeoutHandler.this.mWifiManager, TimeoutHandler.this.mScanResult);
            }
            if (ConnectorUtils.isAlreadyConnected(TimeoutHandler.this.mWifiManager, (String) Elvis.m176of(TimeoutHandler.this.mScanResult).next(new Function() { // from class: com.glasssutdio.wear.wifi.wifiConnect.TimeoutHandler$1$$ExternalSyntheticLambda0
                @Override // androidx.arch.core.util.Function
                public final Object apply(Object obj) {
                    return ((ScanResult) obj).BSSID;
                }
            }).get())) {
                TimeoutHandler.this.mWifiConnectionCallback.successfulConnect();
            } else {
                TimeoutHandler.this.mWifiConnectionCallback.errorConnect(ConnectionErrorCode.TIMEOUT_OCCURRED);
            }
            TimeoutHandler.this.mHandler.removeCallbacks(this);
        }
    }

    public TimeoutHandler(WifiManager wifiManager, WeakHandler handler, final WifiConnectionCallback wifiConnectionCallback) {
        this.mWifiManager = wifiManager;
        this.mHandler = handler;
        this.mWifiConnectionCallback = wifiConnectionCallback;
    }

    public void startTimeout(final ScanResult scanResult, final long timeout) {
        this.mHandler.removeCallbacks(this.timeoutCallback);
        this.mScanResult = scanResult;
        this.mHandler.postDelayed(this.timeoutCallback, timeout);
    }

    public void stopTimeout() {
        this.mHandler.removeCallbacks(this.timeoutCallback);
    }
}
