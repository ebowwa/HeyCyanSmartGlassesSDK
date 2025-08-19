package com.glasssutdio.wear.wifi.utils;

import android.text.TextUtils;

/* loaded from: classes.dex */
public class SSIDUtils {
    public static String convertToQuotedString(String ssid) {
        if (TextUtils.isEmpty(ssid)) {
            return "";
        }
        int length = ssid.length() - 1;
        return length >= 0 ? (ssid.charAt(0) == '\"' && ssid.charAt(length) == '\"') ? ssid : "\"" + ssid + "\"" : ssid;
    }
}
