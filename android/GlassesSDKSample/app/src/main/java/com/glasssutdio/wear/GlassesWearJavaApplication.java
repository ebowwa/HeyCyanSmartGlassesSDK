package com.glasssutdio.wear;

import android.app.Application;

/* loaded from: classes.dex */
public class GlassesWearJavaApplication {
    public static final int OTA_BATTERY_VALUE = 20;
    private static GlassesWearJavaApplication instance;
    private Application application;
    private boolean otaUpgrading = false;
    private boolean translateDoing = false;

    public static GlassesWearJavaApplication getInstance() {
        GlassesWearJavaApplication glassesWearJavaApplication;
        GlassesWearJavaApplication glassesWearJavaApplication2 = instance;
        if (glassesWearJavaApplication2 != null) {
            return glassesWearJavaApplication2;
        }
        synchronized (GlassesWearJavaApplication.class) {
            if (instance == null) {
                instance = new GlassesWearJavaApplication();
            }
            glassesWearJavaApplication = instance;
        }
        return glassesWearJavaApplication;
    }

    public boolean isOtaUpgrading() {
        return this.otaUpgrading;
    }

    public void setOtaUpgrading(boolean otaUpgrading) {
        this.otaUpgrading = otaUpgrading;
    }

    public Application getApplication() {
        return this.application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public boolean isTranslateDoing() {
        return this.translateDoing;
    }

    public void setTranslateDoing(boolean translateDoing) {
        this.translateDoing = translateDoing;
    }
}
