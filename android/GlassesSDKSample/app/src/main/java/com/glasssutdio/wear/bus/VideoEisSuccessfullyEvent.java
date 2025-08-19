package com.glasssutdio.wear.bus;

import com.androidnetworking.common.ANConstants;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.cookie.ClientCookie;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: VideoEisSuccessfullyEvent.kt */
@Metadata(m606d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0007\b\u0016\u0018\u00002\u00020\u0001B\u001d\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006¢\u0006\u0002\u0010\u0007R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0011\u0010\u0004\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\tR\u0011\u0010\u0005\u001a\u00020\u0006¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\f¨\u0006\r"}, m607d2 = {"Lcom/glasssutdio/wear/bus/VideoEisSuccessfullyEvent;", "Lcom/glasssutdio/wear/bus/BusEvent;", "fileName", "", ClientCookie.PATH_ATTR, ANConstants.SUCCESS, "", "(Ljava/lang/String;Ljava/lang/String;Z)V", "getFileName", "()Ljava/lang/String;", "getPath", "getSuccess", "()Z", "app_release"}, m608k = 1, m609mv = {1, 9, 0}, m611xi = 48)
/* loaded from: classes.dex */
public class VideoEisSuccessfullyEvent extends BusEvent {
    private final String fileName;
    private final String path;
    private final boolean success;

    public VideoEisSuccessfullyEvent(String fileName, String path, boolean z) {
        Intrinsics.checkNotNullParameter(fileName, "fileName");
        Intrinsics.checkNotNullParameter(path, "path");
        this.fileName = fileName;
        this.path = path;
        this.success = z;
    }

    public final String getFileName() {
        return this.fileName;
    }

    public final String getPath() {
        return this.path;
    }

    public final boolean getSuccess() {
        return this.success;
    }
}
