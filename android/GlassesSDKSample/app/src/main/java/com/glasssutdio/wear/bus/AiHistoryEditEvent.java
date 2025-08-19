package com.glasssutdio.wear.bus;

import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;

/* compiled from: AiHistoryEditEvent.kt */
@Metadata(m606d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0005\u0018\u0000 \u00072\u00020\u0001:\u0001\u0007B\u000f\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003¢\u0006\u0002\u0010\u0004R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006¨\u0006\b"}, m607d2 = {"Lcom/glasssutdio/wear/bus/AiHistoryEditEvent;", "Lcom/glasssutdio/wear/bus/BusEvent;", "type", "", "(I)V", "getType", "()I", "Companion", "app_release"}, m608k = 1, m609mv = {1, 9, 0}, m611xi = 48)
/* loaded from: classes.dex */
public final class AiHistoryEditEvent extends BusEvent {
    public static final int EDIT_REFRESH_UI = 6;
    public static final int EDIT_TYPE_DELETE = 2;
    public static final int EDIT_TYPE_SELECT_ALL = 1;
    public static final int EDIT_TYPE_SELECT_ALL_ADD = 5;
    public static final int EDIT_TYPE_SELECT_NO_ALL = 4;
    public static final int REFRESH_DATA = 3;
    private final int type;

    public AiHistoryEditEvent() {
        this(0, 1, null);
    }

    public AiHistoryEditEvent(int i) {
        this.type = i;
    }

    public /* synthetic */ AiHistoryEditEvent(int i, int i2, DefaultConstructorMarker defaultConstructorMarker) {
        this((i2 & 1) != 0 ? 1 : i);
    }

    public final int getType() {
        return this.type;
    }
}
