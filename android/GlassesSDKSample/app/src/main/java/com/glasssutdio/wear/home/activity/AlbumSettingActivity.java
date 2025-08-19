package com.glasssutdio.wear.home.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import com.glasssutdio.wear.C0775R;
import com.glasssutdio.wear.all.GlobalKt;
import com.glasssutdio.wear.all.dialog.CommonDialog;
import com.glasssutdio.wear.all.pref.UserConfig;
import com.glasssutdio.wear.databinding.ActivityAlbumSettingBinding;
import com.glasssutdio.wear.manager.BaseSettingActivity;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.XXPermissions;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: AlbumSettingActivity.kt */
@Metadata(m606d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\u0018\u00002\u00020\u0001:\u0001\fB\u0005¢\u0006\u0002\u0010\u0002J\b\u0010\u0005\u001a\u00020\u0006H\u0002J\u0012\u0010\u0007\u001a\u00020\u00062\b\u0010\b\u001a\u0004\u0018\u00010\tH\u0014J\b\u0010\n\u001a\u00020\u0006H\u0014J\b\u0010\u000b\u001a\u00020\u0006H\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082.¢\u0006\u0002\n\u0000¨\u0006\r"}, m607d2 = {"Lcom/glasssutdio/wear/home/activity/AlbumSettingActivity;", "Lcom/glasssutdio/wear/manager/BaseSettingActivity;", "()V", "binding", "Lcom/glasssutdio/wear/databinding/ActivityAlbumSettingBinding;", "initView", "", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "onResume", "showPermissionDialog", "AlBumPermissionCallback", "app_release"}, m608k = 1, m609mv = {1, 9, 0}, m611xi = 48)
/* loaded from: classes.dex */
public final class AlbumSettingActivity extends BaseSettingActivity {
    private ActivityAlbumSettingBinding binding;

    @Override // com.glasssutdio.wear.manager.BaseSettingActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    protected void onCreate(Bundle savedInstanceState) throws SecurityException {
        super.onCreate(savedInstanceState);
        ActivityAlbumSettingBinding activityAlbumSettingBindingInflate = ActivityAlbumSettingBinding.inflate(getLayoutInflater());
        Intrinsics.checkNotNullExpressionValue(activityAlbumSettingBindingInflate, "inflate(...)");
        this.binding = activityAlbumSettingBindingInflate;
        if (activityAlbumSettingBindingInflate == null) {
            Intrinsics.throwUninitializedPropertyAccessException("binding");
            activityAlbumSettingBindingInflate = null;
        }
        setContentView(activityAlbumSettingBindingInflate.getRoot());
        initView();
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onResume() {
        super.onResume();
        ActivityAlbumSettingBinding activityAlbumSettingBinding = this.binding;
        if (activityAlbumSettingBinding == null) {
            Intrinsics.throwUninitializedPropertyAccessException("binding");
            activityAlbumSettingBinding = null;
        }
        activityAlbumSettingBinding.gsc2.setChecked(UserConfig.INSTANCE.getInstance().getPictureAutoSave());
    }

    private final void initView() {
        ActivityAlbumSettingBinding activityAlbumSettingBinding = this.binding;
        if (activityAlbumSettingBinding == null) {
            Intrinsics.throwUninitializedPropertyAccessException("binding");
            activityAlbumSettingBinding = null;
        }
        activityAlbumSettingBinding.title.appBack.setOnClickListener(new View.OnClickListener() { // from class: com.glasssutdio.wear.home.activity.AlbumSettingActivity$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                AlbumSettingActivity.initView$lambda$3$lambda$0(this.f$0, view);
            }
        });
        activityAlbumSettingBinding.title.tvTitle.setText(getString(C0775R.string.album_glass_23));
        activityAlbumSettingBinding.gsc1.setChecked(UserConfig.INSTANCE.getInstance().getPictureWatermark());
        activityAlbumSettingBinding.gsc1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.glasssutdio.wear.home.activity.AlbumSettingActivity$$ExternalSyntheticLambda1
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                AlbumSettingActivity.initView$lambda$3$lambda$1(compoundButton, z);
            }
        });
        activityAlbumSettingBinding.gsc2.setChecked(UserConfig.INSTANCE.getInstance().getPictureAutoSave());
        activityAlbumSettingBinding.gsc2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.glasssutdio.wear.home.activity.AlbumSettingActivity$$ExternalSyntheticLambda2
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                AlbumSettingActivity.initView$lambda$3$lambda$2(compoundButton, z);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void initView$lambda$3$lambda$0(AlbumSettingActivity this$0, View view) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        this$0.finish();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void initView$lambda$3$lambda$1(CompoundButton compoundButton, boolean z) {
        UserConfig.INSTANCE.getInstance().setPictureWatermark(z);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void initView$lambda$3$lambda$2(CompoundButton compoundButton, boolean z) {
        UserConfig.INSTANCE.getInstance().setPictureAutoSave(z);
    }

    private final void showPermissionDialog() {
        CommonDialog.Builder builder = new CommonDialog.Builder();
        String string = getString(C0775R.string.album_glass_29);
        Intrinsics.checkNotNullExpressionValue(string, "getString(...)");
        CommonDialog.Builder title = builder.setTitle(string);
        String string2 = getString(C0775R.string.album_glass_30);
        Intrinsics.checkNotNullExpressionValue(string2, "getString(...)");
        CommonDialog.Builder content = title.setContent(string2);
        String string3 = getString(C0775R.string.album_glass_31);
        Intrinsics.checkNotNullExpressionValue(string3, "getString(...)");
        CommonDialog.Builder confirmMessage = content.setConfirmMessage(string3);
        String string4 = getString(C0775R.string.version_glass_10);
        Intrinsics.checkNotNullExpressionValue(string4, "getString(...)");
        CommonDialog commonDialogBuild = confirmMessage.setCancelMessage(string4).build();
        commonDialogBuild.setOnConfirmListener(new Function1<View, Unit>() { // from class: com.glasssutdio.wear.home.activity.AlbumSettingActivity.showPermissionDialog.1
            {
                super(1);
            }

            @Override // kotlin.jvm.functions.Function1
            public /* bridge */ /* synthetic */ Unit invoke(View view) {
                invoke2(view);
                return Unit.INSTANCE;
            }

            /* renamed from: invoke, reason: avoid collision after fix types in other method */
            public final void invoke2(View it) {
                Intrinsics.checkNotNullParameter(it, "it");
                ActivityAlbumSettingBinding activityAlbumSettingBinding = AlbumSettingActivity.this.binding;
                if (activityAlbumSettingBinding == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("binding");
                    activityAlbumSettingBinding = null;
                }
                activityAlbumSettingBinding.gsc2.setChecked(true);
                UserConfig.INSTANCE.getInstance().setPictureAutoSave(true);
            }
        });
        commonDialogBuild.setOnDismissListener(new Function0<Unit>() { // from class: com.glasssutdio.wear.home.activity.AlbumSettingActivity.showPermissionDialog.2
            {
                super(0);
            }

            @Override // kotlin.jvm.functions.Function0
            public /* bridge */ /* synthetic */ Unit invoke() {
                invoke2();
                return Unit.INSTANCE;
            }

            /* renamed from: invoke, reason: avoid collision after fix types in other method */
            public final void invoke2() {
                ActivityAlbumSettingBinding activityAlbumSettingBinding = AlbumSettingActivity.this.binding;
                if (activityAlbumSettingBinding == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("binding");
                    activityAlbumSettingBinding = null;
                }
                activityAlbumSettingBinding.gsc2.setChecked(true);
                UserConfig.INSTANCE.getInstance().setPictureAutoSave(true);
            }
        });
        commonDialogBuild.show(getSupportFragmentManager(), "showPermissionDialog");
    }

    /* compiled from: AlbumSettingActivity.kt */
    @Metadata(m606d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010!\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\b\u0086\u0004\u0018\u00002\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0002J\u001e\u0010\u0003\u001a\u00020\u00042\f\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u00062\u0006\u0010\b\u001a\u00020\tH\u0016J\u001e\u0010\n\u001a\u00020\u00042\f\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u00062\u0006\u0010\u000b\u001a\u00020\tH\u0016¨\u0006\f"}, m607d2 = {"Lcom/glasssutdio/wear/home/activity/AlbumSettingActivity$AlBumPermissionCallback;", "Lcom/hjq/permissions/OnPermissionCallback;", "(Lcom/glasssutdio/wear/home/activity/AlbumSettingActivity;)V", "onDenied", "", "permissions", "", "", "never", "", "onGranted", "all", "app_release"}, m608k = 1, m609mv = {1, 9, 0}, m611xi = 48)
    public final class AlBumPermissionCallback implements OnPermissionCallback {
        public AlBumPermissionCallback() {
        }

        @Override // com.hjq.permissions.OnPermissionCallback
        public void onGranted(List<String> permissions, boolean all) {
            Intrinsics.checkNotNullParameter(permissions, "permissions");
            ActivityAlbumSettingBinding activityAlbumSettingBinding = null;
            if (all) {
                ActivityAlbumSettingBinding activityAlbumSettingBinding2 = AlbumSettingActivity.this.binding;
                if (activityAlbumSettingBinding2 == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("binding");
                } else {
                    activityAlbumSettingBinding = activityAlbumSettingBinding2;
                }
                activityAlbumSettingBinding.gsc2.setChecked(true);
                UserConfig.INSTANCE.getInstance().setPictureAutoSave(true);
                return;
            }
            String string = AlbumSettingActivity.this.getString(C0775R.string.h_glass_101);
            Intrinsics.checkNotNullExpressionValue(string, "getString(...)");
            GlobalKt.showToast$default(string, 0, 1, null);
            ActivityAlbumSettingBinding activityAlbumSettingBinding3 = AlbumSettingActivity.this.binding;
            if (activityAlbumSettingBinding3 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("binding");
            } else {
                activityAlbumSettingBinding = activityAlbumSettingBinding3;
            }
            activityAlbumSettingBinding.gsc2.setChecked(false);
            UserConfig.INSTANCE.getInstance().setPictureAutoSave(false);
        }

        @Override // com.hjq.permissions.OnPermissionCallback
        public void onDenied(List<String> permissions, boolean never) {
            Intrinsics.checkNotNullParameter(permissions, "permissions");
            super.onDenied(permissions, never);
            if (never) {
                String string = AlbumSettingActivity.this.getString(C0775R.string.h_glass_103);
                Intrinsics.checkNotNullExpressionValue(string, "getString(...)");
                GlobalKt.showToast$default(string, 0, 1, null);
                XXPermissions.startPermissionActivity((Activity) AlbumSettingActivity.this, permissions);
            }
        }
    }
}
