package com.glasssutdio.wear.wifi.wifiConnect;

/* loaded from: classes.dex */
public interface WifiConnectionCallback {
    void errorConnect(ConnectionErrorCode connectionErrorCode);

    void successfulConnect();
}
