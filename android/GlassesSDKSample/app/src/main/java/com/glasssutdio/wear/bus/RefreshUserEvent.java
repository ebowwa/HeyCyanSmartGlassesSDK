package com.glasssutdio.wear.bus;

import kotlin.Metadata;

/* compiled from: RefreshUserEvent.kt */
@Metadata(m606d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0006\u0018\u0000 \t2\u00020\u0001:\u0001\tB\u0005¢\u0006\u0002\u0010\u0002R\u001a\u0010\u0003\u001a\u00020\u0004X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0005\u0010\u0006\"\u0004\b\u0007\u0010\b¨\u0006\n"}, m607d2 = {"Lcom/glasssutdio/wear/bus/RefreshUserEvent;", "Lcom/glasssutdio/wear/bus/BusEvent;", "()V", "refreshType", "", "getRefreshType", "()I", "setRefreshType", "(I)V", "Companion", "app_release"}, m608k = 1, m609mv = {1, 9, 0}, m611xi = 48)
/* loaded from: classes.dex */
public final class RefreshUserEvent extends BusEvent {
    public static final int REFRESH_TYPE_AVATAR = 1;
    public static final int REFRESH_TYPE_LOGOUT = 2;
    private int refreshType;

    public final int getRefreshType() {
        return this.refreshType;
    }

    public final void setRefreshType(int i) {
        this.refreshType = i;
    }
}
