package com.glasssutdio.wear.wifi.wifiConnect;

/* loaded from: classes.dex */
public interface ConnectionSuccessListener {
    void failed(ConnectionErrorCode errorCode);

    void success();
}
