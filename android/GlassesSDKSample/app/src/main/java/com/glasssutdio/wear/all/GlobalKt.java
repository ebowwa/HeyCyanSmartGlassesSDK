package com.glasssutdio.wear.all;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.constraintlayout.core.motion.utils.TypedValues;
import androidx.exifinterface.media.ExifInterface;
import androidx.fragment.app.Fragment;
import com.glasssutdio.wear.GlassApplication;
import com.glasssutdio.wear.GlassesWearJavaApplication;
import com.glasssutdio.wear.all.pref.UserConfig;
import com.glasssutdio.wear.all.utils.NetWorkUtils;
import com.google.android.gms.common.internal.ServiceSpecificExtraArgs;
import java.util.Locale;
import kotlin.Metadata;
import kotlin.Result;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import kotlinx.coroutines.CancellableContinuation;

/* compiled from: Global.kt */
@Metadata(m606d1 = {"\u0000r\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\u0010\u0004\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0007\n\u0000\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u0011\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\r\n\u0002\b\u0002\u001a\u0006\u0010\u000b\u001a\u00020\f\u001a\u0016\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0004\u001a\u00020\u000e\u001a\u000e\u0010\u0002\u001a\u00020\u00012\u0006\u0010\u000f\u001a\u00020\u0010\u001a\u000e\u0010\u0011\u001a\u00020\u00012\u0006\u0010\u000f\u001a\u00020\u0010\u001a\u000e\u0010\u0012\u001a\u00020\u00012\u0006\u0010\u0013\u001a\u00020\u0005\u001a\u000e\u0010\u0014\u001a\u00020\u00052\u0006\u0010\u000f\u001a\u00020\u0010\u001a\u000e\u0010\u0015\u001a\u00020\u00012\u0006\u0010\u000f\u001a\u00020\u0010\u001a\u0016\u0010\u0016\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0017\u001a\u00020\u000e\u001a\u0016\u0010\u0018\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0017\u001a\u00020\u000e\u001a<\u0010\u0019\u001a\u00020\u001a2\u0016\u0010\u001b\u001a\f\u0012\b\b\u0001\u0012\u0004\u0018\u00010\u001d0\u001c\"\u0004\u0018\u00010\u001d2\u0017\u0010\u001e\u001a\u0013\u0012\u0004\u0012\u00020\u001d\u0012\u0004\u0012\u00020\u001a0\u001f¢\u0006\u0002\b ¢\u0006\u0002\u0010!\u001a+\u0010\u0019\u001a\u00020\u001a2\u0016\u0010\u001b\u001a\f\u0012\b\b\u0001\u0012\u0004\u0018\u00010\u001d0\u001c\"\u0004\u0018\u00010\u001d2\u0006\u0010\"\u001a\u00020#¢\u0006\u0002\u0010$\u001a\u000e\u0010%\u001a\u00020\u001a2\u0006\u0010&\u001a\u00020'\u001a\f\u0010(\u001a\u00020\f*\u0004\u0018\u00010)\u001a\f\u0010*\u001a\u00020\f*\u0004\u0018\u00010)\u001a\f\u0010*\u001a\u00020\f*\u0004\u0018\u00010+\u001a\n\u0010,\u001a\u00020\f*\u00020)\u001a\n\u0010,\u001a\u00020\f*\u00020+\u001a#\u0010-\u001a\u00020\u001a\"\u0004\b\u0000\u0010.*\b\u0012\u0004\u0012\u0002H.0/2\u0006\u00100\u001a\u0002H.¢\u0006\u0002\u00101\u001a\u0014\u00102\u001a\u00020\u001a*\u0002032\b\b\u0002\u00104\u001a\u00020\u0005\"\u0011\u0010\u0000\u001a\u00020\u00018F¢\u0006\u0006\u001a\u0004\b\u0002\u0010\u0003\"\u0015\u0010\u0004\u001a\u00020\u0005*\u00020\u00068F¢\u0006\u0006\u001a\u0004\b\u0007\u0010\b\"\u0015\u0010\t\u001a\u00020\u0005*\u00020\u00068F¢\u0006\u0006\u001a\u0004\b\n\u0010\b¨\u00065"}, m607d2 = {"appName", "", "getAppName", "()Ljava/lang/String;", "dp", "", "", "getDp", "(Ljava/lang/Number;)I", "sp", "getSp", "dateLocalization", "", "dp2px", "", "context", "Landroid/content/Context;", "getPackageName", "getString", "resId", "getVersionCode", "getVersionName", "px2dp", "px", "pxToSp", "setOnClickListener", "", "v", "", "Landroid/view/View;", "block", "Lkotlin/Function1;", "Lkotlin/ExtensionFunctionType;", "([Landroid/view/View;Lkotlin/jvm/functions/Function1;)V", ServiceSpecificExtraArgs.CastExtraArgs.LISTENER, "Landroid/view/View$OnClickListener;", "([Landroid/view/View;Landroid/view/View$OnClickListener;)V", "textViewScore", "view", "Landroid/widget/TextView;", "isDestroy", "Landroid/app/Activity;", "isLogin", "Landroidx/fragment/app/Fragment;", "isNetworkAvailable", "resumeIfActive", ExifInterface.GPS_DIRECTION_TRUE, "Lkotlinx/coroutines/CancellableContinuation;", "value", "(Lkotlinx/coroutines/CancellableContinuation;Ljava/lang/Object;)V", "showToast", "", TypedValues.TransitionType.S_DURATION, "app_release"}, m608k = 2, m609mv = {1, 9, 0}, m611xi = 48)
/* loaded from: classes.dex */
public final class GlobalKt {
    public static final void setOnClickListener(View[] v, final Function1<? super View, Unit> block) {
        Intrinsics.checkNotNullParameter(v, "v");
        Intrinsics.checkNotNullParameter(block, "block");
        View.OnClickListener onClickListener = new View.OnClickListener() { // from class: com.glasssutdio.wear.all.GlobalKt$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                GlobalKt.setOnClickListener$lambda$0(block, view);
            }
        };
        for (View view : v) {
            if (view != null) {
                view.setOnClickListener(onClickListener);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void setOnClickListener$lambda$0(Function1 block, View view) {
        Intrinsics.checkNotNullParameter(block, "$block");
        Intrinsics.checkNotNull(view);
        block.invoke(view);
    }

    public static final void textViewScore(TextView view) {
        Intrinsics.checkNotNullParameter(view, "view");
        view.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        view.setSingleLine();
        view.setSelected(true);
        view.setFocusable(true);
        view.setFocusableInTouchMode(true);
    }

    public static final int getDp(Number number) {
        Intrinsics.checkNotNullParameter(number, "<this>");
        return (int) (number.floatValue() * Resources.getSystem().getDisplayMetrics().density);
    }

    public static final int getSp(Number number) {
        Intrinsics.checkNotNullParameter(number, "<this>");
        return (int) (number.floatValue() * Resources.getSystem().getDisplayMetrics().scaledDensity);
    }

    public static final String getString(int i) throws Resources.NotFoundException {
        String string = GlassesWearJavaApplication.getInstance().getApplication().getResources().getString(i);
        Intrinsics.checkNotNullExpressionValue(string, "getString(...)");
        return string;
    }

    public static final String getAppName() throws Resources.NotFoundException {
        String string = GlassesWearJavaApplication.getInstance().getApplication().getResources().getString(GlassApplication.INSTANCE.getCONTEXT().getApplicationInfo().labelRes);
        Intrinsics.checkNotNullExpressionValue(string, "getString(...)");
        return string;
    }

    public static /* synthetic */ void showToast$default(CharSequence charSequence, int i, int i2, Object obj) {
        if ((i2 & 1) != 0) {
            i = 0;
        }
        showToast(charSequence, i);
    }

    public static final void showToast(CharSequence charSequence, int i) {
        Intrinsics.checkNotNullParameter(charSequence, "<this>");
        Toast.makeText(GlassesWearJavaApplication.getInstance().getApplication(), charSequence, i).show();
    }

    public static final float px2dp(Context context, float f) {
        Intrinsics.checkNotNullParameter(context, "context");
        return (f / context.getResources().getDisplayMetrics().density) + 0.5f;
    }

    public static final float dp2px(Context context, float f) {
        Intrinsics.checkNotNullParameter(context, "context");
        return (f * context.getResources().getDisplayMetrics().density) + 0.5f;
    }

    public static final float pxToSp(Context context, float f) {
        Intrinsics.checkNotNullParameter(context, "context");
        return f / context.getResources().getDisplayMetrics().scaledDensity;
    }

    public static final boolean isDestroy(Activity activity) {
        return activity == null || activity.isFinishing() || activity.isDestroyed();
    }

    public static final boolean isLogin(Activity activity) {
        return UserConfig.INSTANCE.getInstance().getUserToken().length() > 0 && !Intrinsics.areEqual(UserConfig.INSTANCE.getInstance().getUserToken(), "15ef6eb5403406c1da0dc4a4defa2ea1");
    }

    public static final boolean isLogin(Fragment fragment) {
        return UserConfig.INSTANCE.getInstance().getUserToken().length() > 0 && !Intrinsics.areEqual(UserConfig.INSTANCE.getInstance().getUserToken(), "15ef6eb5403406c1da0dc4a4defa2ea1");
    }

    public static final boolean isNetworkAvailable(Activity activity) {
        Intrinsics.checkNotNullParameter(activity, "<this>");
        return NetWorkUtils.INSTANCE.isNetworkAvailable(activity);
    }

    public static final boolean isNetworkAvailable(Fragment fragment) {
        Intrinsics.checkNotNullParameter(fragment, "<this>");
        NetWorkUtils.Companion companion = NetWorkUtils.INSTANCE;
        Context contextRequireContext = fragment.requireContext();
        Intrinsics.checkNotNullExpressionValue(contextRequireContext, "requireContext(...)");
        return companion.isNetworkAvailable(contextRequireContext);
    }

    public static final <T> void resumeIfActive(CancellableContinuation<? super T> cancellableContinuation, T t) {
        Intrinsics.checkNotNullParameter(cancellableContinuation, "<this>");
        if (cancellableContinuation.isActive()) {
            Result.Companion companion = Result.INSTANCE;
            cancellableContinuation.resumeWith(Result.m903constructorimpl(t));
        }
    }

    public static final String getVersionName(Context context) throws PackageManager.NameNotFoundException {
        Intrinsics.checkNotNullParameter(context, "context");
        PackageManager packageManager = context.getPackageManager();
        Intrinsics.checkNotNullExpressionValue(packageManager, "getPackageManager(...)");
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            Intrinsics.checkNotNullExpressionValue(packageInfo, "getPackageInfo(...)");
            String str = packageInfo.versionName;
            return str == null ? "1.0.0.0" : str;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "1.0.0.0";
        }
    }

    public static final int getVersionCode(Context context) throws PackageManager.NameNotFoundException {
        Intrinsics.checkNotNullParameter(context, "context");
        PackageManager packageManager = context.getPackageManager();
        Intrinsics.checkNotNullExpressionValue(packageManager, "getPackageManager(...)");
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            Intrinsics.checkNotNullExpressionValue(packageInfo, "getPackageInfo(...)");
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static final boolean dateLocalization() {
        String language = Locale.getDefault().getLanguage();
        Intrinsics.checkNotNull(language);
        return StringsKt.contains$default((CharSequence) Localization.language, (CharSequence) language, false, 2, (Object) null);
    }

    public static final String getPackageName(Context context) throws PackageManager.NameNotFoundException {
        Intrinsics.checkNotNullParameter(context, "context");
        PackageManager packageManager = context.getPackageManager();
        Intrinsics.checkNotNullExpressionValue(packageManager, "getPackageManager(...)");
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            Intrinsics.checkNotNullExpressionValue(packageInfo, "getPackageInfo(...)");
            String packageName = packageInfo.packageName;
            Intrinsics.checkNotNullExpressionValue(packageName, "packageName");
            return packageName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "com.qc";
        }
    }

    public static final String getAppName(Context context) throws Resources.NotFoundException, PackageManager.NameNotFoundException {
        Intrinsics.checkNotNullParameter(context, "context");
        PackageManager packageManager = context.getPackageManager();
        Intrinsics.checkNotNullExpressionValue(packageManager, "getPackageManager(...)");
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            Intrinsics.checkNotNullExpressionValue(packageInfo, "getPackageInfo(...)");
            ApplicationInfo applicationInfo = packageInfo.applicationInfo;
            Intrinsics.checkNotNull(applicationInfo);
            String string = context.getResources().getString(applicationInfo.labelRes);
            Intrinsics.checkNotNullExpressionValue(string, "getString(...)");
            return string;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "others";
        }
    }

    public static final void setOnClickListener(View[] v, View.OnClickListener listener) {
        Intrinsics.checkNotNullParameter(v, "v");
        Intrinsics.checkNotNullParameter(listener, "listener");
        for (View view : v) {
            if (view != null) {
                view.setOnClickListener(listener);
            }
        }
    }
}
