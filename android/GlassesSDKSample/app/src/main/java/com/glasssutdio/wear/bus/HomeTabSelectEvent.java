package com.glasssutdio.wear.bus;

import kotlin.Metadata;

/* compiled from: HomeTabSelectEvent.kt */
@Metadata(m606d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0004\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0002\u0010\u0004R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006¨\u0006\u0007"}, m607d2 = {"Lcom/glasssutdio/wear/bus/HomeTabSelectEvent;", "Lcom/glasssutdio/wear/bus/BusEvent;", "index", "", "(I)V", "getIndex", "()I", "app_release"}, m608k = 1, m609mv = {1, 9, 0}, m611xi = 48)
/* loaded from: classes.dex */
public final class HomeTabSelectEvent extends BusEvent {
    private final int index;

    public HomeTabSelectEvent(int i) {
        this.index = i;
    }

    public final int getIndex() {
        return this.index;
    }
}
