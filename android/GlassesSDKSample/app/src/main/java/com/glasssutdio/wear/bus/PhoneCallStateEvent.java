package com.glasssutdio.wear.bus;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: PhoneCallStateEvent.kt */
@Metadata(m606d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0006\u0018\u00002\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0002\u0010\u0006R\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\n¨\u0006\u000b"}, m607d2 = {"Lcom/glasssutdio/wear/bus/PhoneCallStateEvent;", "Lcom/glasssutdio/wear/bus/BusEvent;", "state", "", "content", "", "(ILjava/lang/String;)V", "getContent", "()Ljava/lang/String;", "getState", "()I", "app_release"}, m608k = 1, m609mv = {1, 9, 0}, m611xi = 48)
/* loaded from: classes.dex */
public final class PhoneCallStateEvent extends BusEvent {
    private final String content;
    private final int state;

    public PhoneCallStateEvent(int i, String content) {
        Intrinsics.checkNotNullParameter(content, "content");
        this.state = i;
        this.content = content;
    }

    public final String getContent() {
        return this.content;
    }

    public final int getState() {
        return this.state;
    }
}
