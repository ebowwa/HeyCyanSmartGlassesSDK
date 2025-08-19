package com.glasssutdio.wear.wifi.wifiConnect;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiManager;
import androidx.arch.core.util.Function;
import com.glasssutdio.wear.wifi.ConnectorUtils;
import com.glasssutdio.wear.wifi.WifiUtils;
import com.glasssutdio.wear.wifi.utils.Elvis;
import com.glasssutdio.wear.wifi.utils.VersionUtils;
import java.util.Objects;

/* loaded from: classes.dex */
public final class WifiConnectionReceiver extends BroadcastReceiver {
    private ScanResult mScanResult;
    private final WifiConnectionCallback mWifiConnectionCallback;
    private final WifiManager mWifiManager;
    private String ssid;

    public WifiConnectionReceiver(final WifiConnectionCallback callback, final WifiManager wifiManager) {
        this.mWifiConnectionCallback = callback;
        this.mWifiManager = wifiManager;
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(final Context context, final Intent intent) {
        String action = intent.getAction();
        WifiUtils.wifiLog("Connection Broadcast action: " + action);
        if (VersionUtils.isAndroidQOrLater()) {
            if (Objects.equals("android.net.wifi.supplicant.STATE_CHANGE", action)) {
                SupplicantState supplicantState = (SupplicantState) intent.getParcelableExtra("newState");
                int intExtra = intent.getIntExtra("supplicantError", -1);
                WifiUtils.wifiLog("Connection Broadcast state: " + supplicantState);
                WifiUtils.wifiLog("suppl_error: " + intExtra);
                if (this.mScanResult == null && isAlreadyConnected2(this.mWifiManager, this.ssid)) {
                    this.mWifiConnectionCallback.successfulConnect();
                }
                if (supplicantState == SupplicantState.DISCONNECTED && intExtra == 1) {
                    this.mWifiConnectionCallback.errorConnect(ConnectionErrorCode.AUTHENTICATION_ERROR_OCCURRED);
                    return;
                }
                return;
            }
            return;
        }
        if (Objects.equals("android.net.wifi.STATE_CHANGE", action)) {
            if (ConnectorUtils.isAlreadyConnected(this.mWifiManager, (String) Elvis.m176of(this.mScanResult).next(new Function() { // from class: com.glasssutdio.wear.wifi.wifiConnect.WifiConnectionReceiver$$ExternalSyntheticLambda0
                @Override // androidx.arch.core.util.Function
                public final Object apply(Object obj) {
                    return ((ScanResult) obj).BSSID;
                }
            }).get())) {
                this.mWifiConnectionCallback.successfulConnect();
                return;
            }
            return;
        }
        if (Objects.equals("android.net.wifi.supplicant.STATE_CHANGE", action)) {
            SupplicantState supplicantState2 = (SupplicantState) intent.getParcelableExtra("newState");
            int intExtra2 = intent.getIntExtra("supplicantError", -1);
            if (supplicantState2 == null) {
                this.mWifiConnectionCallback.errorConnect(ConnectionErrorCode.COULD_NOT_CONNECT);
                return;
            }
            WifiUtils.wifiLog("Connection Broadcast state: " + supplicantState2);
            int i = C11501.$SwitchMap$android$net$wifi$SupplicantState[supplicantState2.ordinal()];
            if (i == 1 || i == 2) {
                if (this.mScanResult == null && isAlreadyConnected2(this.mWifiManager, this.ssid)) {
                    this.mWifiConnectionCallback.successfulConnect();
                    return;
                } else {
                    if (ConnectorUtils.isAlreadyConnected(this.mWifiManager, (String) Elvis.m176of(this.mScanResult).next(new Function() { // from class: com.glasssutdio.wear.wifi.wifiConnect.WifiConnectionReceiver$$ExternalSyntheticLambda1
                        @Override // androidx.arch.core.util.Function
                        public final Object apply(Object obj) {
                            return ((ScanResult) obj).BSSID;
                        }
                    }).get())) {
                        this.mWifiConnectionCallback.successfulConnect();
                        return;
                    }
                    return;
                }
            }
            if (i != 3) {
                return;
            }
            if (intExtra2 == 1) {
                WifiUtils.wifiLog("Authentication error...");
                this.mWifiConnectionCallback.errorConnect(ConnectionErrorCode.AUTHENTICATION_ERROR_OCCURRED);
            } else {
                WifiUtils.wifiLog("Disconnected. Re-attempting to connect...");
                ConnectorUtils.reEnableNetworkIfPossible(this.mWifiManager, this.mScanResult);
            }
        }
    }

    /* renamed from: com.glasssutdio.wear.wifi.wifiConnect.WifiConnectionReceiver$1 */
    static /* synthetic */ class C11501 {
        static final /* synthetic */ int[] $SwitchMap$android$net$wifi$SupplicantState;

        static {
            int[] iArr = new int[SupplicantState.values().length];
            $SwitchMap$android$net$wifi$SupplicantState = iArr;
            try {
                iArr[SupplicantState.COMPLETED.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$android$net$wifi$SupplicantState[SupplicantState.FOUR_WAY_HANDSHAKE.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$android$net$wifi$SupplicantState[SupplicantState.DISCONNECTED.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
        }
    }

    public static boolean isAlreadyConnected2(WifiManager wifiManager, String ssid) {
        if (ssid == null || wifiManager == null || wifiManager.getConnectionInfo() == null || wifiManager.getConnectionInfo().getSSID() == null || wifiManager.getConnectionInfo().getIpAddress() == 0 || !Objects.equals(ssid, wifiManager.getConnectionInfo().getSSID())) {
            return false;
        }
        WifiUtils.wifiLog("Already connected to: " + wifiManager.getConnectionInfo().getSSID() + "  BSSID: " + wifiManager.getConnectionInfo().getBSSID());
        return true;
    }

    public WifiConnectionReceiver connectWith(ScanResult result, String password, ConnectivityManager connectivityManager) {
        this.mScanResult = result;
        return this;
    }

    public WifiConnectionReceiver connectWith(String ssid, String password, ConnectivityManager connectivityManager) {
        this.ssid = ssid;
        return this;
    }
}
