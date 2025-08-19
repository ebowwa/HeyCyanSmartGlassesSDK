package com.oudmon.qc_utils.date;

import java.util.Locale;

/* loaded from: classes2.dex */
public class LanguageUtil {
    public static String localLanguage = "nl";

    public static boolean changeDateFormat() {
        return true;
    }

    public static boolean isChina() {
        return false;
    }

    public static boolean isChinaReal() {
        return Locale.getDefault().getLanguage().contains("zh");
    }
}
