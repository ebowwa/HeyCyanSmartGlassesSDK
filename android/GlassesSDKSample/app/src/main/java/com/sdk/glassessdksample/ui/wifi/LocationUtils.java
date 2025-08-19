package com.glasssutdio.wear.wifi;

import android.content.Context;
import android.location.LocationManager;
import android.util.Log;
import androidx.arch.core.util.Function;
import com.glasssutdio.wear.wifi.utils.Elvis;

/* loaded from: classes.dex */
public class LocationUtils {
    public static final int GOOD_TO_GO = 1000;
    public static final int LOCATION_DISABLED = 1112;
    public static final int NO_LOCATION_AVAILABLE = 1111;
    private static final String TAG = "LocationUtils";

    public static boolean isGPSEnable(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService("location");
        return locationManager != null && locationManager.isProviderEnabled("gps");
    }

    public static int checkLocationAvailability(final Context context) {
        if (context.getPackageManager().hasSystemFeature("android.hardware.location")) {
            if (!isLocationEnabled(context)) {
                Log.d(TAG, "Location DISABLED");
                return LOCATION_DISABLED;
            }
            Log.d(TAG, "GPS GOOD TO GO");
            return 1000;
        }
        Log.d(TAG, "NO GPS SENSOR");
        return NO_LOCATION_AVAILABLE;
    }

    private static boolean isLocationEnabled(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService("location");
        return Elvis.m176of(locationManager).next(new Function() { // from class: com.glasssutdio.wear.wifi.LocationUtils$$ExternalSyntheticLambda0
            @Override // androidx.arch.core.util.Function
            public final Object apply(Object obj) {
                return Boolean.valueOf(((LocationManager) obj).isProviderEnabled("gps"));
            }
        }).getBoolean() || Elvis.m176of(locationManager).next(new Function() { // from class: com.glasssutdio.wear.wifi.LocationUtils$$ExternalSyntheticLambda1
            @Override // androidx.arch.core.util.Function
            public final Object apply(Object obj) {
                return Boolean.valueOf(((LocationManager) obj).isProviderEnabled("network"));
            }
        }).getBoolean();
    }
}
