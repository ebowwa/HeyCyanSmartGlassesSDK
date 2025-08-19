package com.glasssutdio.wear.bus;

import kotlin.Metadata;

/* compiled from: EventType.kt */
@Metadata(m606d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0004\b\u0016\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0002\u0010\u0004R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006¨\u0006\u0007"}, m607d2 = {"Lcom/glasssutdio/wear/bus/EventType;", "Lcom/glasssutdio/wear/bus/BusEvent;", "type", "", "(I)V", "getType", "()I", "app_release"}, m608k = 1, m609mv = {1, 9, 0}, m611xi = 48)
/* loaded from: classes.dex */
public class EventType extends BusEvent {
    private final int type;

    public EventType(int i) {
        this.type = i;
    }

    public final int getType() {
        return this.type;
    }
}
