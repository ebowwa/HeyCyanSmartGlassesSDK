package com.glasssutdio.wear.wifi.utils;

import android.os.Build;

/* loaded from: classes.dex */
public class VersionUtils {
    public static boolean isJellyBeanOrLater() {
        return true;
    }

    public static boolean isLollipopOrLater() {
        return true;
    }

    public static boolean isMarshmallowOrLater() {
        return true;
    }

    private VersionUtils() {
    }

    public static boolean isAndroidQOrLater() {
        return Build.VERSION.SDK_INT >= 29;
    }
}
