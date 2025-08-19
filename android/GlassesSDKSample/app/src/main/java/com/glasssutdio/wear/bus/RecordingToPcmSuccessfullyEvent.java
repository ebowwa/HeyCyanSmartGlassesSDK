package com.glasssutdio.wear.bus;

import androidx.constraintlayout.core.motion.utils.TypedValues;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.cookie.ClientCookie;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: RecordingToPcmSuccessfullyEvent.kt */
@Metadata(m606d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0007\b\u0016\u0018\u00002\u00020\u0001B\u001d\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006¢\u0006\u0002\u0010\u0007R\u0011\u0010\u0005\u001a\u00020\u0006¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0011\u0010\u0004\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\u000b¨\u0006\r"}, m607d2 = {"Lcom/glasssutdio/wear/bus/RecordingToPcmSuccessfullyEvent;", "Lcom/glasssutdio/wear/bus/BusEvent;", "fileName", "", ClientCookie.PATH_ATTR, TypedValues.TransitionType.S_DURATION, "", "(Ljava/lang/String;Ljava/lang/String;I)V", "getDuration", "()I", "getFileName", "()Ljava/lang/String;", "getPath", "app_release"}, m608k = 1, m609mv = {1, 9, 0}, m611xi = 48)
/* loaded from: classes.dex */
public class RecordingToPcmSuccessfullyEvent extends BusEvent {
    private final int duration;
    private final String fileName;
    private final String path;

    public RecordingToPcmSuccessfullyEvent(String fileName, String path, int i) {
        Intrinsics.checkNotNullParameter(fileName, "fileName");
        Intrinsics.checkNotNullParameter(path, "path");
        this.fileName = fileName;
        this.path = path;
        this.duration = i;
    }

    public final int getDuration() {
        return this.duration;
    }

    public final String getFileName() {
        return this.fileName;
    }

    public final String getPath() {
        return this.path;
    }
}
