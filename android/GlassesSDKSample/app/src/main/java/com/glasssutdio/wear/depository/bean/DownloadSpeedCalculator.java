package com.glasssutdio.wear.depository.bean;

import java.util.Arrays;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.RangesKt;

/* compiled from: DownloadSpeedCalculator.kt */
@Metadata(m606d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\u0006\n\u0000\n\u0002\u0010\u0002\n\u0000\u0018\u00002\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0002J\u000e\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\u0004J\u0010\u0010\t\u001a\u00020\u00072\u0006\u0010\n\u001a\u00020\u000bH\u0002J\u0006\u0010\f\u001a\u00020\rR\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006\u000e"}, m607d2 = {"Lcom/glasssutdio/wear/depository/bean/DownloadSpeedCalculator;", "", "()V", "lastBytes", "", "lastTime", "calculate", "", "currentBytes", "formatSpeed", "speedBps", "", "start", "", "app_release"}, m608k = 1, m609mv = {1, 9, 0}, m611xi = 48)
/* loaded from: classes.dex */
public final class DownloadSpeedCalculator {
    private long lastBytes;
    private long lastTime = -1;

    public final void start() {
        this.lastTime = System.currentTimeMillis();
        this.lastBytes = 0L;
    }

    public final String calculate(long currentBytes) {
        long jCurrentTimeMillis = System.currentTimeMillis();
        if (this.lastTime == -1) {
            return "-1";
        }
        double dCoerceAtLeast = ((currentBytes - this.lastBytes) * 1000.0d) / RangesKt.coerceAtLeast(jCurrentTimeMillis - r2, 1L);
        this.lastTime = jCurrentTimeMillis;
        this.lastBytes = currentBytes;
        return formatSpeed(dCoerceAtLeast);
    }

    private final String formatSpeed(double speedBps) {
        if (speedBps >= 1048576.0d) {
            String str = String.format("%.1f MB/s", Arrays.copyOf(new Object[]{Double.valueOf(speedBps / 1048576)}, 1));
            Intrinsics.checkNotNullExpressionValue(str, "format(...)");
            return str;
        }
        if (speedBps >= 1024.0d) {
            String str2 = String.format("%.1f KB/s", Arrays.copyOf(new Object[]{Double.valueOf(speedBps / 1024)}, 1));
            Intrinsics.checkNotNullExpressionValue(str2, "format(...)");
            return str2;
        }
        if (speedBps < 1.0d) {
            return "0 B/s";
        }
        String str3 = String.format("%.0f B/s", Arrays.copyOf(new Object[]{Double.valueOf(speedBps)}, 1));
        Intrinsics.checkNotNullExpressionValue(str3, "format(...)");
        return str3;
    }
}
