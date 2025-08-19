package com.glasssutdio.wear.bus;

import kotlin.Metadata;

/* compiled from: BluetoothEvent.kt */
@Metadata(m606d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0004\b\u0016\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0002\u0010\u0004R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006¨\u0006\u0007"}, m607d2 = {"Lcom/glasssutdio/wear/bus/BluetoothEvent;", "Lcom/glasssutdio/wear/bus/BusEvent;", "connect", "", "(Z)V", "getConnect", "()Z", "app_release"}, m608k = 1, m609mv = {1, 9, 0}, m611xi = 48)
/* loaded from: classes.dex */
public class BluetoothEvent extends BusEvent {
    private final boolean connect;

    public BluetoothEvent(boolean z) {
        this.connect = z;
    }

    public final boolean getConnect() {
        return this.connect;
    }
}
