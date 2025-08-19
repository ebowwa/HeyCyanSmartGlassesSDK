package com.glasssutdio.wear.wifi.wifiDisconnect;

/* loaded from: classes.dex */
public interface DisconnectionSuccessListener {
    void failed(DisconnectionErrorCode errorCode);

    void success();
}
