package com.glasssutdio.wear.home.album;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.LifecycleOwnerKt;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.elvishew.xlog.XLog;
import com.glasssutdio.wear.C0775R;
import com.glasssutdio.wear.all.GlobalKt;
import com.glasssutdio.wear.all.TextViewExtKt;
import com.glasssutdio.wear.all.dialog.DeleteOrNotDialog;
import com.glasssutdio.wear.all.pref.UserConfig;
import com.glasssutdio.wear.all.utils.GFileUtilKt;
import com.glasssutdio.wear.bus.EventType;
import com.glasssutdio.wear.database.entity.GlassAlbumEntity;
import com.glasssutdio.wear.databinding.ActivityEditAlbumBinding;
import com.glasssutdio.wear.home.activity.LoginActivity;
import com.glasssutdio.wear.home.adapter.AlbumAdapter;
import com.glasssutdio.wear.home.album.p005vm.AlbumListViewModel;
import com.glasssutdio.wear.home.album.update.PcmToMp3Kt;
import com.glasssutdio.wear.home.album.water.WatermarkGenerator;
import com.glasssutdio.wear.manager.BaseSettingActivity;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.p008io.FilenameUtils;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.p008io.IOUtils;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpStatus;
import com.hjq.permissions.Permission;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import kotlin.Lazy;
import kotlin.LazyKt;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.ResultKt;
import kotlin.TuplesKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Reflection;
import kotlin.jvm.internal.StringCompanionObject;
import kotlin.p014io.ByteStreamsKt;
import kotlin.p014io.CloseableKt;
import kotlin.text.StringsKt;
import kotlinx.coroutines.BuildersKt;
import kotlinx.coroutines.BuildersKt__Builders_commonKt;
import kotlinx.coroutines.CoroutineDispatcher;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.Dispatchers;
import kotlinx.coroutines.flow.Flow;
import kotlinx.coroutines.flow.FlowCollector;
import kotlinx.coroutines.flow.FlowKt;
import org.greenrobot.eventbus.EventBus;
import org.koin.androidx.viewmodel.ext.android.LifecycleOwnerExtKt;
import org.koin.core.qualifier.Qualifier;

/* compiled from: EditAlbumActivity.kt */
@Metadata(m606d1 = {"\u0000x\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010%\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0007\u0018\u00002\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0002J\b\u0010\u0016\u001a\u00020\u0017H\u0002J\u0018\u0010\u0018\u001a\u00020\u00192\u0006\u0010\u001a\u001a\u00020\u001b2\u0006\u0010\u001c\u001a\u00020\u001dH\u0002J\b\u0010\u001e\u001a\u00020\u0019H\u0002J\u001a\u0010\u001f\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0011\u0012\u0004\u0012\u00020\u00120\u00100 H\u0003J\u0010\u0010!\u001a\u00020\u00112\u0006\u0010\"\u001a\u00020\u0011H\u0002J\u0010\u0010#\u001a\u00020\u00112\u0006\u0010$\u001a\u00020%H\u0002J\b\u0010&\u001a\u00020\u0019H\u0002J\u0012\u0010'\u001a\u00020\u00192\b\u0010(\u001a\u0004\u0018\u00010)H\u0014J\u0018\u0010*\u001a\u00020\u00192\u0006\u0010+\u001a\u00020,2\u0006\u0010-\u001a\u00020%H\u0002J\u0018\u0010.\u001a\u00020\u00192\u0006\u0010+\u001a\u00020,2\u0006\u0010/\u001a\u00020%H\u0002J\b\u00100\u001a\u00020\u0019H\u0003J\b\u00101\u001a\u00020\u0019H\u0002J\b\u00102\u001a\u00020\u0019H\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082.¢\u0006\u0002\n\u0000R\u001b\u0010\u0005\u001a\u00020\u00068BX\u0082\u0084\u0002¢\u0006\f\n\u0004\b\t\u0010\n\u001a\u0004\b\u0007\u0010\bR\u000e\u0010\u000b\u001a\u00020\fX\u0082.¢\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u000eX\u0082\u000e¢\u0006\u0002\n\u0000R\u001a\u0010\u000f\u001a\u000e\u0012\u0004\u0012\u00020\u0011\u0012\u0004\u0012\u00020\u00120\u0010X\u0082\u000e¢\u0006\u0002\n\u0000R\u0014\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00150\u0014X\u0082\u0004¢\u0006\u0002\n\u0000¨\u00063"}, m607d2 = {"Lcom/glasssutdio/wear/home/album/EditAlbumActivity;", "Lcom/glasssutdio/wear/manager/BaseSettingActivity;", "()V", "adapter", "Lcom/glasssutdio/wear/home/adapter/AlbumAdapter;", "albumViewModel", "Lcom/glasssutdio/wear/home/album/vm/AlbumListViewModel;", "getAlbumViewModel", "()Lcom/glasssutdio/wear/home/album/vm/AlbumListViewModel;", "albumViewModel$delegate", "Lkotlin/Lazy;", "binding", "Lcom/glasssutdio/wear/databinding/ActivityEditAlbumBinding;", "inputType", "", "mapSelectMedia", "", "", "Lcom/glasssutdio/wear/database/entity/GlassAlbumEntity;", "shareLauncher", "Landroidx/activity/result/ActivityResultLauncher;", "Landroid/content/Intent;", "checkAllLiked", "", "copyFile", "", "inputStream", "Ljava/io/InputStream;", "outputStream", "Ljava/io/OutputStream;", "deleteMedia", "fetchWatermarkData", "Lkotlinx/coroutines/flow/Flow;", "getFileExtension", "fileName", "getMimeType", "file", "Ljava/io/File;", "initView", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "saveFileToAppGalleryFolder", "context", "Landroid/content/Context;", "sourceFile", "savePcmAsWavAndAddToMediaStore", "pcmFile", "saveToSystemAlbum", "shareToSystem", "updateSelectCount", "app_release"}, m608k = 1, m609mv = {1, 9, 0}, m611xi = 48)
/* loaded from: classes.dex */
public final class EditAlbumActivity extends BaseSettingActivity {
    private AlbumAdapter adapter;

    /* renamed from: albumViewModel$delegate, reason: from kotlin metadata */
    private final Lazy albumViewModel;
    private ActivityEditAlbumBinding binding;
    private int inputType;
    private Map<String, GlassAlbumEntity> mapSelectMedia;
    private final ActivityResultLauncher<Intent> shareLauncher;

    /* JADX INFO: Access modifiers changed from: private */
    public static final void savePcmAsWavAndAddToMediaStore$lambda$18$lambda$17(String str, Uri uri) {
    }

    /* JADX WARN: Multi-variable type inference failed */
    public EditAlbumActivity() {
        final EditAlbumActivity editAlbumActivity = this;
        final Qualifier qualifier = null;
        final Object[] objArr = 0 == true ? 1 : 0;
        this.albumViewModel = LazyKt.lazy(new Function0<AlbumListViewModel>() { // from class: com.glasssutdio.wear.home.album.EditAlbumActivity$special$$inlined$viewModel$default$1
            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super(0);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            /* JADX WARN: Type inference failed for: r0v1, types: [androidx.lifecycle.ViewModel, com.glasssutdio.wear.home.album.vm.AlbumListViewModel] */
            @Override // kotlin.jvm.functions.Function0
            public final AlbumListViewModel invoke() {
                return LifecycleOwnerExtKt.getViewModel(editAlbumActivity, Reflection.getOrCreateKotlinClass(AlbumListViewModel.class), qualifier, objArr);
            }
        });
        this.mapSelectMedia = new LinkedHashMap();
        this.shareLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback() { // from class: com.glasssutdio.wear.home.album.EditAlbumActivity$$ExternalSyntheticLambda1
            @Override // androidx.activity.result.ActivityResultCallback
            public final void onActivityResult(Object obj) {
                EditAlbumActivity.shareLauncher$lambda$0((ActivityResult) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final AlbumListViewModel getAlbumViewModel() {
        return (AlbumListViewModel) this.albumViewModel.getValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void shareLauncher$lambda$0(ActivityResult result) {
        Intrinsics.checkNotNullParameter(result, "result");
        if (result.getResultCode() == -1) {
            XLog.m137i("用户可能完成了分享（但不保证）");
        } else {
            XLog.m137i("用户可能取消了分享");
        }
    }

    @Override // com.glasssutdio.wear.manager.BaseSettingActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    protected void onCreate(Bundle savedInstanceState) throws InterruptedException, SecurityException {
        super.onCreate(savedInstanceState);
        ActivityEditAlbumBinding activityEditAlbumBindingInflate = ActivityEditAlbumBinding.inflate(getLayoutInflater());
        Intrinsics.checkNotNullExpressionValue(activityEditAlbumBindingInflate, "inflate(...)");
        this.binding = activityEditAlbumBindingInflate;
        if (activityEditAlbumBindingInflate == null) {
            Intrinsics.throwUninitializedPropertyAccessException("binding");
            activityEditAlbumBindingInflate = null;
        }
        setContentView(activityEditAlbumBindingInflate.getRoot());
        initView();
    }

    private final void initView() throws InterruptedException {
        Bundle extras = getIntent().getExtras();
        ActivityEditAlbumBinding activityEditAlbumBinding = null;
        Integer numValueOf = extras != null ? Integer.valueOf(extras.getInt("input_type")) : null;
        Intrinsics.checkNotNull(numValueOf);
        this.inputType = numValueOf.intValue();
        AlbumAdapter albumAdapter = new AlbumAdapter(getAlbumViewModel().getData());
        this.adapter = albumAdapter;
        albumAdapter.setEdit(true);
        final ActivityEditAlbumBinding activityEditAlbumBinding2 = this.binding;
        if (activityEditAlbumBinding2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("binding");
            activityEditAlbumBinding2 = null;
        }
        TextView tvTitle = activityEditAlbumBinding2.tvTitle;
        Intrinsics.checkNotNullExpressionValue(tvTitle, "tvTitle");
        TextViewExtKt.setupMarquee(tvTitle);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 4);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() { // from class: com.glasssutdio.wear.home.album.EditAlbumActivity$initView$1$1
            @Override // androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
            public int getSpanSize(int position) {
                RecyclerView.Adapter adapter = activityEditAlbumBinding2.rcvAlbumAll.getAdapter();
                Intrinsics.checkNotNull(adapter, "null cannot be cast to non-null type com.glasssutdio.wear.home.adapter.AlbumAdapter");
                AlbumAdapter albumAdapter2 = (AlbumAdapter) adapter;
                return (albumAdapter2.getItemViewType(position) == 0 || albumAdapter2.getItemViewType(position) == 2) ? 4 : 1;
            }
        });
        activityEditAlbumBinding2.rcvAlbumAll.setLayoutManager(gridLayoutManager);
        RecyclerView recyclerView = activityEditAlbumBinding2.rcvAlbumAll;
        AlbumAdapter albumAdapter2 = this.adapter;
        if (albumAdapter2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("adapter");
            albumAdapter2 = null;
        }
        recyclerView.setAdapter(albumAdapter2);
        AlbumAdapter albumAdapter3 = this.adapter;
        if (albumAdapter3 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("adapter");
            albumAdapter3 = null;
        }
        albumAdapter3.setOnItemClickListener(new AlbumAdapter.OnItemClickListener() { // from class: com.glasssutdio.wear.home.album.EditAlbumActivity$initView$1$2
            @Override // com.glasssutdio.wear.home.adapter.AlbumAdapter.OnItemClickListener
            public void onItemClick(int position, GlassAlbumEntity item) {
                Intrinsics.checkNotNullParameter(item, "item");
                item.setEditSelect(!item.getEditSelect());
                AlbumAdapter albumAdapter4 = this.this$0.adapter;
                ActivityEditAlbumBinding activityEditAlbumBinding3 = null;
                if (albumAdapter4 == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("adapter");
                    albumAdapter4 = null;
                }
                albumAdapter4.notifyItemChanged(position);
                if (item.getEditSelect()) {
                    this.this$0.mapSelectMedia.put(item.getFileName(), item);
                } else {
                    this.this$0.mapSelectMedia.remove(item.getFileName());
                }
                ActivityEditAlbumBinding activityEditAlbumBinding4 = this.this$0.binding;
                if (activityEditAlbumBinding4 == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("binding");
                } else {
                    activityEditAlbumBinding3 = activityEditAlbumBinding4;
                }
                activityEditAlbumBinding3.tvLike.setSelected(this.this$0.checkAllLiked());
                this.this$0.updateSelectCount();
            }
        });
        getAlbumViewModel().getUiState().observe(this, new EditAlbumActivity$sam$androidx_lifecycle_Observer$0(new Function1<AlbumListViewModel.DeviceAlbumUI, Unit>() { // from class: com.glasssutdio.wear.home.album.EditAlbumActivity.initView.2
            {
                super(1);
            }

            @Override // kotlin.jvm.functions.Function1
            public /* bridge */ /* synthetic */ Unit invoke(AlbumListViewModel.DeviceAlbumUI deviceAlbumUI) throws Resources.NotFoundException {
                invoke2(deviceAlbumUI);
                return Unit.INSTANCE;
            }

            /* renamed from: invoke, reason: avoid collision after fix types in other method */
            public final void invoke2(AlbumListViewModel.DeviceAlbumUI deviceAlbumUI) throws Resources.NotFoundException {
                AlbumAdapter albumAdapter4 = EditAlbumActivity.this.adapter;
                AlbumAdapter albumAdapter5 = null;
                if (albumAdapter4 == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("adapter");
                    albumAdapter4 = null;
                }
                albumAdapter4.refresh(EditAlbumActivity.this.getAlbumViewModel().getData());
                AlbumAdapter albumAdapter6 = EditAlbumActivity.this.adapter;
                if (albumAdapter6 == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("adapter");
                    albumAdapter6 = null;
                }
                albumAdapter6.notifyDataSetChanged();
                AlbumListViewModel albumViewModel = EditAlbumActivity.this.getAlbumViewModel();
                AlbumAdapter albumAdapter7 = EditAlbumActivity.this.adapter;
                if (albumAdapter7 == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("adapter");
                } else {
                    albumAdapter5 = albumAdapter7;
                }
                albumViewModel.initDetailList(albumAdapter5.getAllItemsNoKey());
            }
        }));
        int i = this.inputType;
        if (i == 0) {
            getAlbumViewModel().initAllData();
        } else if (i == 1) {
            getAlbumViewModel().initImageData();
        } else if (i == 2) {
            getAlbumViewModel().initVideoData();
        } else if (i == 3) {
            getAlbumViewModel().initRecordData();
        } else if (i == 4) {
            getAlbumViewModel().initLikeData();
        }
        View[] viewArr = new View[7];
        ActivityEditAlbumBinding activityEditAlbumBinding3 = this.binding;
        if (activityEditAlbumBinding3 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("binding");
            activityEditAlbumBinding3 = null;
        }
        viewArr[0] = activityEditAlbumBinding3.tvSave;
        ActivityEditAlbumBinding activityEditAlbumBinding4 = this.binding;
        if (activityEditAlbumBinding4 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("binding");
            activityEditAlbumBinding4 = null;
        }
        viewArr[1] = activityEditAlbumBinding4.tvShare;
        ActivityEditAlbumBinding activityEditAlbumBinding5 = this.binding;
        if (activityEditAlbumBinding5 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("binding");
            activityEditAlbumBinding5 = null;
        }
        viewArr[2] = activityEditAlbumBinding5.tvDelete;
        ActivityEditAlbumBinding activityEditAlbumBinding6 = this.binding;
        if (activityEditAlbumBinding6 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("binding");
            activityEditAlbumBinding6 = null;
        }
        viewArr[3] = activityEditAlbumBinding6.tvLike;
        ActivityEditAlbumBinding activityEditAlbumBinding7 = this.binding;
        if (activityEditAlbumBinding7 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("binding");
            activityEditAlbumBinding7 = null;
        }
        viewArr[4] = activityEditAlbumBinding7.tvEdit;
        ActivityEditAlbumBinding activityEditAlbumBinding8 = this.binding;
        if (activityEditAlbumBinding8 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("binding");
            activityEditAlbumBinding8 = null;
        }
        viewArr[5] = activityEditAlbumBinding8.tvSelectSave;
        ActivityEditAlbumBinding activityEditAlbumBinding9 = this.binding;
        if (activityEditAlbumBinding9 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("binding");
        } else {
            activityEditAlbumBinding = activityEditAlbumBinding9;
        }
        viewArr[6] = activityEditAlbumBinding.tvSelectAll;
        GlobalKt.setOnClickListener(viewArr, new Function1<View, Unit>() { // from class: com.glasssutdio.wear.home.album.EditAlbumActivity.initView.3
            {
                super(1);
            }

            @Override // kotlin.jvm.functions.Function1
            public /* bridge */ /* synthetic */ Unit invoke(View view) throws Resources.NotFoundException, PackageManager.NameNotFoundException, IOException {
                invoke2(view);
                return Unit.INSTANCE;
            }

            /* renamed from: invoke, reason: avoid collision after fix types in other method */
            public final void invoke2(View setOnClickListener) throws Resources.NotFoundException, PackageManager.NameNotFoundException, IOException {
                Intrinsics.checkNotNullParameter(setOnClickListener, "$this$setOnClickListener");
                ActivityEditAlbumBinding activityEditAlbumBinding10 = EditAlbumActivity.this.binding;
                AlbumAdapter albumAdapter4 = null;
                ActivityEditAlbumBinding activityEditAlbumBinding11 = null;
                if (activityEditAlbumBinding10 == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("binding");
                    activityEditAlbumBinding10 = null;
                }
                if (!Intrinsics.areEqual(setOnClickListener, activityEditAlbumBinding10.tvSave)) {
                    ActivityEditAlbumBinding activityEditAlbumBinding12 = EditAlbumActivity.this.binding;
                    if (activityEditAlbumBinding12 == null) {
                        Intrinsics.throwUninitializedPropertyAccessException("binding");
                        activityEditAlbumBinding12 = null;
                    }
                    if (Intrinsics.areEqual(setOnClickListener, activityEditAlbumBinding12.tvShare)) {
                        EditAlbumActivity.this.shareToSystem();
                        return;
                    }
                    ActivityEditAlbumBinding activityEditAlbumBinding13 = EditAlbumActivity.this.binding;
                    if (activityEditAlbumBinding13 == null) {
                        Intrinsics.throwUninitializedPropertyAccessException("binding");
                        activityEditAlbumBinding13 = null;
                    }
                    if (Intrinsics.areEqual(setOnClickListener, activityEditAlbumBinding13.tvDelete)) {
                        EditAlbumActivity.this.deleteMedia();
                        return;
                    }
                    ActivityEditAlbumBinding activityEditAlbumBinding14 = EditAlbumActivity.this.binding;
                    if (activityEditAlbumBinding14 == null) {
                        Intrinsics.throwUninitializedPropertyAccessException("binding");
                        activityEditAlbumBinding14 = null;
                    }
                    if (!Intrinsics.areEqual(setOnClickListener, activityEditAlbumBinding14.tvLike)) {
                        ActivityEditAlbumBinding activityEditAlbumBinding15 = EditAlbumActivity.this.binding;
                        if (activityEditAlbumBinding15 == null) {
                            Intrinsics.throwUninitializedPropertyAccessException("binding");
                            activityEditAlbumBinding15 = null;
                        }
                        if (Intrinsics.areEqual(setOnClickListener, activityEditAlbumBinding15.tvEdit)) {
                            return;
                        }
                        ActivityEditAlbumBinding activityEditAlbumBinding16 = EditAlbumActivity.this.binding;
                        if (activityEditAlbumBinding16 == null) {
                            Intrinsics.throwUninitializedPropertyAccessException("binding");
                            activityEditAlbumBinding16 = null;
                        }
                        if (!Intrinsics.areEqual(setOnClickListener, activityEditAlbumBinding16.tvSelectSave)) {
                            ActivityEditAlbumBinding activityEditAlbumBinding17 = EditAlbumActivity.this.binding;
                            if (activityEditAlbumBinding17 == null) {
                                Intrinsics.throwUninitializedPropertyAccessException("binding");
                                activityEditAlbumBinding17 = null;
                            }
                            if (Intrinsics.areEqual(setOnClickListener, activityEditAlbumBinding17.tvSelectAll)) {
                                int size = EditAlbumActivity.this.mapSelectMedia.values().size();
                                AlbumAdapter albumAdapter5 = EditAlbumActivity.this.adapter;
                                if (albumAdapter5 == null) {
                                    Intrinsics.throwUninitializedPropertyAccessException("adapter");
                                    albumAdapter5 = null;
                                }
                                if (size == albumAdapter5.getAllItemsNoKey().size()) {
                                    ActivityEditAlbumBinding activityEditAlbumBinding18 = EditAlbumActivity.this.binding;
                                    if (activityEditAlbumBinding18 == null) {
                                        Intrinsics.throwUninitializedPropertyAccessException("binding");
                                        activityEditAlbumBinding18 = null;
                                    }
                                    activityEditAlbumBinding18.tvSelectAll.setText(EditAlbumActivity.this.getString(C0775R.string.album_glass_16));
                                    EditAlbumActivity.this.mapSelectMedia.clear();
                                    AlbumAdapter albumAdapter6 = EditAlbumActivity.this.adapter;
                                    if (albumAdapter6 == null) {
                                        Intrinsics.throwUninitializedPropertyAccessException("adapter");
                                        albumAdapter6 = null;
                                    }
                                    Iterator<T> it = albumAdapter6.getAllItemsNoKey().iterator();
                                    while (it.hasNext()) {
                                        ((GlassAlbumEntity) it.next()).setEditSelect(false);
                                    }
                                } else {
                                    ActivityEditAlbumBinding activityEditAlbumBinding19 = EditAlbumActivity.this.binding;
                                    if (activityEditAlbumBinding19 == null) {
                                        Intrinsics.throwUninitializedPropertyAccessException("binding");
                                        activityEditAlbumBinding19 = null;
                                    }
                                    activityEditAlbumBinding19.tvSelectAll.setText(EditAlbumActivity.this.getString(C0775R.string.album_glass_19));
                                    ActivityEditAlbumBinding activityEditAlbumBinding20 = EditAlbumActivity.this.binding;
                                    if (activityEditAlbumBinding20 == null) {
                                        Intrinsics.throwUninitializedPropertyAccessException("binding");
                                        activityEditAlbumBinding20 = null;
                                    }
                                    activityEditAlbumBinding20.tvLike.setSelected(true);
                                    AlbumAdapter albumAdapter7 = EditAlbumActivity.this.adapter;
                                    if (albumAdapter7 == null) {
                                        Intrinsics.throwUninitializedPropertyAccessException("adapter");
                                        albumAdapter7 = null;
                                    }
                                    List<GlassAlbumEntity> allItemsNoKey = albumAdapter7.getAllItemsNoKey();
                                    EditAlbumActivity editAlbumActivity = EditAlbumActivity.this;
                                    for (GlassAlbumEntity glassAlbumEntity : allItemsNoKey) {
                                        glassAlbumEntity.setEditSelect(true);
                                        editAlbumActivity.mapSelectMedia.put(glassAlbumEntity.getFileName(), glassAlbumEntity);
                                    }
                                    ActivityEditAlbumBinding activityEditAlbumBinding21 = EditAlbumActivity.this.binding;
                                    if (activityEditAlbumBinding21 == null) {
                                        Intrinsics.throwUninitializedPropertyAccessException("binding");
                                        activityEditAlbumBinding21 = null;
                                    }
                                    activityEditAlbumBinding21.tvLike.setSelected(EditAlbumActivity.this.checkAllLiked());
                                }
                                EditAlbumActivity.this.updateSelectCount();
                                AlbumAdapter albumAdapter8 = EditAlbumActivity.this.adapter;
                                if (albumAdapter8 == null) {
                                    Intrinsics.throwUninitializedPropertyAccessException("adapter");
                                } else {
                                    albumAdapter4 = albumAdapter8;
                                }
                                albumAdapter4.notifyDataSetChanged();
                                return;
                            }
                            return;
                        }
                        EditAlbumActivity.this.finish();
                        return;
                    }
                    if (GlobalKt.isLogin(EditAlbumActivity.this)) {
                        ActivityEditAlbumBinding activityEditAlbumBinding22 = EditAlbumActivity.this.binding;
                        if (activityEditAlbumBinding22 == null) {
                            Intrinsics.throwUninitializedPropertyAccessException("binding");
                            activityEditAlbumBinding22 = null;
                        }
                        boolean zIsSelected = activityEditAlbumBinding22.tvLike.isSelected();
                        Collection<GlassAlbumEntity> collectionValues = EditAlbumActivity.this.mapSelectMedia.values();
                        EditAlbumActivity editAlbumActivity2 = EditAlbumActivity.this;
                        for (GlassAlbumEntity glassAlbumEntity2 : collectionValues) {
                            if (zIsSelected) {
                                glassAlbumEntity2.setUserLike(2);
                            } else {
                                glassAlbumEntity2.setUserLike(1);
                            }
                            editAlbumActivity2.getAlbumViewModel().updateLikeOrNot(glassAlbumEntity2);
                        }
                        ActivityEditAlbumBinding activityEditAlbumBinding23 = EditAlbumActivity.this.binding;
                        if (activityEditAlbumBinding23 == null) {
                            Intrinsics.throwUninitializedPropertyAccessException("binding");
                            activityEditAlbumBinding23 = null;
                        }
                        TextView textView = activityEditAlbumBinding23.tvLike;
                        ActivityEditAlbumBinding activityEditAlbumBinding24 = EditAlbumActivity.this.binding;
                        if (activityEditAlbumBinding24 == null) {
                            Intrinsics.throwUninitializedPropertyAccessException("binding");
                        } else {
                            activityEditAlbumBinding11 = activityEditAlbumBinding24;
                        }
                        textView.setSelected(!activityEditAlbumBinding11.tvLike.isSelected());
                        EventBus.getDefault().post(new EventType(10));
                        EditAlbumActivity.this.finish();
                        return;
                    }
                    EditAlbumActivity editAlbumActivity3 = EditAlbumActivity.this;
                    ArrayList<Pair> arrayList = new ArrayList();
                    Intent intent = new Intent(editAlbumActivity3, (Class<?>) LoginActivity.class);
                    for (Pair pair : arrayList) {
                        if (pair != null) {
                            String str = (String) pair.getFirst();
                            Object second = pair.getSecond();
                            if (second instanceof Integer) {
                                Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, ((Number) second).intValue()), "putExtra(...)");
                            } else if (second instanceof Byte) {
                                Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, ((Number) second).byteValue()), "putExtra(...)");
                            } else if (second instanceof Character) {
                                Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, ((Character) second).charValue()), "putExtra(...)");
                            } else if (second instanceof Short) {
                                Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, ((Number) second).shortValue()), "putExtra(...)");
                            } else if (second instanceof Boolean) {
                                Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, ((Boolean) second).booleanValue()), "putExtra(...)");
                            } else if (second instanceof Long) {
                                Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, ((Number) second).longValue()), "putExtra(...)");
                            } else if (second instanceof Float) {
                                Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, ((Number) second).floatValue()), "putExtra(...)");
                            } else if (second instanceof Double) {
                                Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, ((Number) second).doubleValue()), "putExtra(...)");
                            } else if (second instanceof String) {
                                Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, (String) second), "putExtra(...)");
                            } else if (second instanceof CharSequence) {
                                Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, (CharSequence) second), "putExtra(...)");
                            } else if (second instanceof Parcelable) {
                                Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, (Parcelable) second), "putExtra(...)");
                            } else if (second instanceof Object[]) {
                                Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, (Serializable) second), "putExtra(...)");
                            } else if (second instanceof ArrayList) {
                                Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, (Serializable) second), "putExtra(...)");
                            } else if (second instanceof Serializable) {
                                Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, (Serializable) second), "putExtra(...)");
                            } else if (second instanceof boolean[]) {
                                Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, (boolean[]) second), "putExtra(...)");
                            } else if (second instanceof byte[]) {
                                Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, (byte[]) second), "putExtra(...)");
                            } else if (second instanceof short[]) {
                                Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, (short[]) second), "putExtra(...)");
                            } else if (second instanceof char[]) {
                                Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, (char[]) second), "putExtra(...)");
                            } else if (second instanceof int[]) {
                                Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, (int[]) second), "putExtra(...)");
                            } else if (second instanceof long[]) {
                                Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, (long[]) second), "putExtra(...)");
                            } else if (second instanceof float[]) {
                                Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, (float[]) second), "putExtra(...)");
                            } else if (second instanceof double[]) {
                                Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, (double[]) second), "putExtra(...)");
                            } else if (second instanceof Bundle) {
                                Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, (Bundle) second), "putExtra(...)");
                            } else if (second instanceof Intent) {
                                Intrinsics.checkNotNullExpressionValue(intent.putExtra(str, (Parcelable) second), "putExtra(...)");
                            } else {
                                Unit unit = Unit.INSTANCE;
                            }
                        }
                    }
                    editAlbumActivity3.startActivity(intent);
                    return;
                }
                EditAlbumActivity.this.showLoadingDialog();
                EditAlbumActivity.this.saveToSystemAlbum();
            }
        });
        updateSelectCount();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final boolean checkAllLiked() {
        Iterator<T> it = this.mapSelectMedia.values().iterator();
        while (it.hasNext()) {
            if (((GlassAlbumEntity) it.next()).getUserLike() != 1) {
                return false;
            }
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void updateSelectCount() {
        ActivityEditAlbumBinding activityEditAlbumBinding = this.binding;
        ActivityEditAlbumBinding activityEditAlbumBinding2 = null;
        if (activityEditAlbumBinding == null) {
            Intrinsics.throwUninitializedPropertyAccessException("binding");
            activityEditAlbumBinding = null;
        }
        TextView textView = activityEditAlbumBinding.tvTitle;
        StringCompanionObject stringCompanionObject = StringCompanionObject.INSTANCE;
        String string = getString(C0775R.string.album_glass_18);
        Intrinsics.checkNotNullExpressionValue(string, "getString(...)");
        String str = String.format(string, Arrays.copyOf(new Object[]{String.valueOf(this.mapSelectMedia.values().size())}, 1));
        Intrinsics.checkNotNullExpressionValue(str, "format(...)");
        textView.setText(str);
        if (this.mapSelectMedia.values().isEmpty()) {
            ActivityEditAlbumBinding activityEditAlbumBinding3 = this.binding;
            if (activityEditAlbumBinding3 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("binding");
            } else {
                activityEditAlbumBinding2 = activityEditAlbumBinding3;
            }
            activityEditAlbumBinding2.tvSave.setEnabled(false);
            activityEditAlbumBinding2.tvShare.setEnabled(false);
            activityEditAlbumBinding2.tvLike.setEnabled(false);
            activityEditAlbumBinding2.tvDelete.setEnabled(false);
            return;
        }
        int size = this.mapSelectMedia.values().size();
        AlbumAdapter albumAdapter = this.adapter;
        if (albumAdapter == null) {
            Intrinsics.throwUninitializedPropertyAccessException("adapter");
            albumAdapter = null;
        }
        if (size == albumAdapter.getAllItemsNoKey().size()) {
            ActivityEditAlbumBinding activityEditAlbumBinding4 = this.binding;
            if (activityEditAlbumBinding4 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("binding");
                activityEditAlbumBinding4 = null;
            }
            activityEditAlbumBinding4.tvSelectAll.setText(getString(C0775R.string.album_glass_19));
        } else {
            ActivityEditAlbumBinding activityEditAlbumBinding5 = this.binding;
            if (activityEditAlbumBinding5 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("binding");
                activityEditAlbumBinding5 = null;
            }
            activityEditAlbumBinding5.tvSelectAll.setText(getString(C0775R.string.album_glass_16));
        }
        ActivityEditAlbumBinding activityEditAlbumBinding6 = this.binding;
        if (activityEditAlbumBinding6 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("binding");
        } else {
            activityEditAlbumBinding2 = activityEditAlbumBinding6;
        }
        if (this.mapSelectMedia.values().size() > 9) {
            activityEditAlbumBinding2.tvSave.setEnabled(false);
            activityEditAlbumBinding2.tvShare.setEnabled(false);
        } else {
            activityEditAlbumBinding2.tvSave.setEnabled(true);
            activityEditAlbumBinding2.tvShare.setEnabled(true);
        }
        activityEditAlbumBinding2.tvLike.setEnabled(true);
        activityEditAlbumBinding2.tvDelete.setEnabled(true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void deleteMedia() {
        DeleteOrNotDialog.Builder builder = new DeleteOrNotDialog.Builder();
        String string = getString(C0775R.string.album_glass_15);
        Intrinsics.checkNotNullExpressionValue(string, "getString(...)");
        DeleteOrNotDialog.Builder content = builder.setTitle(string).setContent("");
        String string2 = getString(C0775R.string.album_glass_9);
        Intrinsics.checkNotNullExpressionValue(string2, "getString(...)");
        DeleteOrNotDialog.Builder confirmMessage = content.setConfirmMessage(string2);
        String string3 = getString(C0775R.string.h_glass_cancel);
        Intrinsics.checkNotNullExpressionValue(string3, "getString(...)");
        DeleteOrNotDialog deleteOrNotDialogBuild = confirmMessage.setCancelMessage(string3).build();
        deleteOrNotDialogBuild.show(getSupportFragmentManager(), "showRestFactoryDialog");
        deleteOrNotDialogBuild.setOnConfirmListener(new Function1<View, Unit>() { // from class: com.glasssutdio.wear.home.album.EditAlbumActivity.deleteMedia.1
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
                Collection collectionValues = EditAlbumActivity.this.mapSelectMedia.values();
                EditAlbumActivity editAlbumActivity = EditAlbumActivity.this;
                Iterator it2 = collectionValues.iterator();
                while (it2.hasNext()) {
                    editAlbumActivity.getAlbumViewModel().deleteMediaFile((GlassAlbumEntity) it2.next());
                }
                EventBus.getDefault().post(new EventType(9));
                EditAlbumActivity.this.finish();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void saveToSystemAlbum() throws Resources.NotFoundException, PackageManager.NameNotFoundException, IOException {
        for (GlassAlbumEntity glassAlbumEntity : this.mapSelectMedia.values()) {
            if (UserConfig.INSTANCE.getInstance().getPictureWatermark()) {
                if (glassAlbumEntity.getFileType() == 1) {
                    WatermarkGenerator.WatermarkConfig watermarkConfig = new WatermarkGenerator.WatermarkConfig(null, null, null, null, 0, 31, null);
                    watermarkConfig.setNameImage(BitmapFactory.decodeResource(getResources(), C0775R.mipmap.app_name_icon_black));
                    String str = new SimpleDateFormat("yyyy.MM.dd  HH:mm:ss").format(new Date());
                    Intrinsics.checkNotNullExpressionValue(str, "format(...)");
                    watermarkConfig.setDateTime(str);
                    watermarkConfig.setLogo(BitmapFactory.decodeResource(getResources(), C0775R.mipmap.ic_album_logo));
                    String string = getString(C0775R.string.album_glass_32);
                    Intrinsics.checkNotNullExpressionValue(string, "getString(...)");
                    watermarkConfig.setExtraText(string);
                    Bitmap bitmapDecodeFile = BitmapFactory.decodeFile(glassAlbumEntity.getFilePath());
                    Intrinsics.checkNotNull(bitmapDecodeFile);
                    Bitmap bitmapAddBottomWatermark = WatermarkGenerator.INSTANCE.addBottomWatermark(this, bitmapDecodeFile, watermarkConfig);
                    BuildersKt__Builders_commonKt.launch$default(LifecycleOwnerKt.getLifecycleScope(this), null, null, new EditAlbumActivity$saveToSystemAlbum$1$1(this, GFileUtilKt.getDCIMFile().getAbsolutePath(), bitmapAddBottomWatermark, null), 3, null);
                } else {
                    saveFileToAppGalleryFolder(this, new File(glassAlbumEntity.getFilePath()));
                }
            } else {
                saveFileToAppGalleryFolder(this, new File(glassAlbumEntity.getFilePath()));
            }
        }
        String string2 = getString(C0775R.string.album_glass_10);
        Intrinsics.checkNotNullExpressionValue(string2, "getString(...)");
        GlobalKt.showToast$default(string2, 0, 1, null);
        finish();
    }

    /* compiled from: EditAlbumActivity.kt */
    @Metadata(m606d1 = {"\u0000\u0016\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\u0010%\n\u0002\u0010\u000e\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00050\u00030\u0002H\u008a@"}, m607d2 = {"<anonymous>", "", "Lkotlinx/coroutines/flow/FlowCollector;", "", "", "Lcom/glasssutdio/wear/database/entity/GlassAlbumEntity;"}, m608k = 3, m609mv = {1, 9, 0}, m611xi = 48)
    @DebugMetadata(m619c = "com.glasssutdio.wear.home.album.EditAlbumActivity$fetchWatermarkData$1", m620f = "EditAlbumActivity.kt", m621i = {0, 0, 0}, m622l = {374, 390, 392}, m623m = "invokeSuspend", m624n = {"$this$flow", "mapSelectMediaWater", "it"}, m625s = {"L$0", "L$1", "L$4"})
    /* renamed from: com.glasssutdio.wear.home.album.EditAlbumActivity$fetchWatermarkData$1 */
    static final class C10461 extends SuspendLambda implements Function2<FlowCollector<? super Map<String, GlassAlbumEntity>>, Continuation<? super Unit>, Object> {
        private /* synthetic */ Object L$0;
        Object L$1;
        Object L$2;
        Object L$3;
        Object L$4;
        int label;

        C10461(Continuation<? super C10461> continuation) {
            super(2, continuation);
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
            C10461 c10461 = EditAlbumActivity.this.new C10461(continuation);
            c10461.L$0 = obj;
            return c10461;
        }

        @Override // kotlin.jvm.functions.Function2
        public final Object invoke(FlowCollector<? super Map<String, GlassAlbumEntity>> flowCollector, Continuation<? super Unit> continuation) {
            return ((C10461) create(flowCollector, continuation)).invokeSuspend(Unit.INSTANCE);
        }

        /* JADX WARN: Removed duplicated region for block: B:21:0x0079  */
        /* JADX WARN: Removed duplicated region for block: B:33:0x01b9 A[RETURN] */
        /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:25:0x016f -> B:27:0x0172). Please report as a decompilation issue!!! */
        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        /*
            Code decompiled incorrectly, please refer to instructions dump.
        */
        public final Object invokeSuspend(Object obj) throws Throwable {
            FlowCollector flowCollector;
            Map map;
            Iterator it;
            EditAlbumActivity editAlbumActivity;
            Object objWithContext;
            Object coroutine_suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED();
            int i = this.label;
            if (i == 0) {
                ResultKt.throwOnFailure(obj);
                FlowCollector flowCollector2 = (FlowCollector) this.L$0;
                if (!UserConfig.INSTANCE.getInstance().getPictureWatermark()) {
                    this.label = 3;
                    if (flowCollector2.emit(EditAlbumActivity.this.mapSelectMedia, this) == coroutine_suspended) {
                        return coroutine_suspended;
                    }
                } else {
                    LinkedHashMap linkedHashMap = new LinkedHashMap();
                    Collection collectionValues = EditAlbumActivity.this.mapSelectMedia.values();
                    EditAlbumActivity editAlbumActivity2 = EditAlbumActivity.this;
                    flowCollector = flowCollector2;
                    map = linkedHashMap;
                    it = collectionValues.iterator();
                    editAlbumActivity = editAlbumActivity2;
                    while (it.hasNext()) {
                    }
                    this.L$0 = null;
                    this.L$1 = null;
                    this.L$2 = null;
                    this.L$3 = null;
                    this.L$4 = null;
                    this.label = 2;
                    if (flowCollector.emit(map, this) == coroutine_suspended) {
                    }
                }
            } else if (i == 1) {
                GlassAlbumEntity glassAlbumEntity = (GlassAlbumEntity) this.L$4;
                it = (Iterator) this.L$3;
                editAlbumActivity = (EditAlbumActivity) this.L$2;
                map = (Map) this.L$1;
                flowCollector = (FlowCollector) this.L$0;
                try {
                    ResultKt.throwOnFailure(obj);
                    objWithContext = obj;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                File file = (File) objWithContext;
                Object obj2 = map.get(glassAlbumEntity.getFileName());
                Intrinsics.checkNotNull(obj2);
                String absolutePath = file.getAbsolutePath();
                Intrinsics.checkNotNullExpressionValue(absolutePath, "getAbsolutePath(...)");
                ((GlassAlbumEntity) obj2).setFilePath(absolutePath);
                XLog.m137i(file.getAbsolutePath());
                while (it.hasNext()) {
                    glassAlbumEntity = (GlassAlbumEntity) it.next();
                    if (glassAlbumEntity.getFileType() == 1) {
                        GlassAlbumEntity glassAlbumEntity2 = new GlassAlbumEntity();
                        glassAlbumEntity2.setFilePath(glassAlbumEntity.getFilePath());
                        glassAlbumEntity2.setFileName(glassAlbumEntity.getFileName());
                        glassAlbumEntity2.setMac(glassAlbumEntity.getMac());
                        glassAlbumEntity2.setVideoFirstFrame(glassAlbumEntity.getVideoFirstFrame());
                        glassAlbumEntity2.setFileType(glassAlbumEntity.getFileType());
                        glassAlbumEntity2.setVideoLength(glassAlbumEntity.getVideoLength());
                        glassAlbumEntity2.setFileDate(glassAlbumEntity.getFileDate());
                        glassAlbumEntity2.setTimestamp(glassAlbumEntity.getTimestamp());
                        glassAlbumEntity2.setHorizontalCalibration(glassAlbumEntity.getHorizontalCalibration());
                        glassAlbumEntity2.setUserLike(glassAlbumEntity.getUserLike());
                        glassAlbumEntity2.setEisInProgress(glassAlbumEntity.getEisInProgress());
                        glassAlbumEntity2.setEditSelect(glassAlbumEntity.getEditSelect());
                        map.put(glassAlbumEntity.getFileName(), glassAlbumEntity2);
                        WatermarkGenerator.WatermarkConfig watermarkConfig = new WatermarkGenerator.WatermarkConfig(null, null, null, null, 0, 31, null);
                        watermarkConfig.setNameImage(BitmapFactory.decodeResource(editAlbumActivity.getResources(), C0775R.mipmap.app_name_icon_black));
                        String str = new SimpleDateFormat("yyyy.MM.dd  HH:mm:ss").format(new Date());
                        Intrinsics.checkNotNullExpressionValue(str, "format(...)");
                        watermarkConfig.setDateTime(str);
                        watermarkConfig.setLogo(BitmapFactory.decodeResource(editAlbumActivity.getResources(), C0775R.mipmap.ic_album_logo));
                        String string = editAlbumActivity.getString(C0775R.string.album_glass_32);
                        Intrinsics.checkNotNullExpressionValue(string, "getString(...)");
                        watermarkConfig.setExtraText(string);
                        Bitmap bitmapDecodeFile = BitmapFactory.decodeFile(glassAlbumEntity.getFilePath());
                        Intrinsics.checkNotNull(bitmapDecodeFile);
                        Bitmap bitmapAddBottomWatermark = WatermarkGenerator.INSTANCE.addBottomWatermark(editAlbumActivity, bitmapDecodeFile, watermarkConfig);
                        String absolutePath2 = GFileUtilKt.getAlbumDirFile().getAbsolutePath();
                        CoroutineDispatcher io2 = Dispatchers.getIO();
                        EditAlbumActivity$fetchWatermarkData$1$1$compressedFile$1 editAlbumActivity$fetchWatermarkData$1$1$compressedFile$1 = new EditAlbumActivity$fetchWatermarkData$1$1$compressedFile$1(editAlbumActivity, absolutePath2, bitmapAddBottomWatermark, null);
                        this.L$0 = flowCollector;
                        this.L$1 = map;
                        this.L$2 = editAlbumActivity;
                        this.L$3 = it;
                        this.L$4 = glassAlbumEntity;
                        this.label = 1;
                        objWithContext = BuildersKt.withContext(io2, editAlbumActivity$fetchWatermarkData$1$1$compressedFile$1, this);
                        if (objWithContext == coroutine_suspended) {
                            return coroutine_suspended;
                        }
                        File file2 = (File) objWithContext;
                        Object obj22 = map.get(glassAlbumEntity.getFileName());
                        Intrinsics.checkNotNull(obj22);
                        String absolutePath3 = file2.getAbsolutePath();
                        Intrinsics.checkNotNullExpressionValue(absolutePath3, "getAbsolutePath(...)");
                        ((GlassAlbumEntity) obj22).setFilePath(absolutePath3);
                        XLog.m137i(file2.getAbsolutePath());
                        while (it.hasNext()) {
                        }
                    } else {
                        map.put(glassAlbumEntity.getFileName(), glassAlbumEntity);
                    }
                }
                this.L$0 = null;
                this.L$1 = null;
                this.L$2 = null;
                this.L$3 = null;
                this.L$4 = null;
                this.label = 2;
                if (flowCollector.emit(map, this) == coroutine_suspended) {
                    return coroutine_suspended;
                }
            } else {
                if (i != 2 && i != 3) {
                    throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                }
                ResultKt.throwOnFailure(obj);
            }
            return Unit.INSTANCE;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final Flow<Map<String, GlassAlbumEntity>> fetchWatermarkData() {
        return FlowKt.flow(new C10461(null));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void shareToSystem() {
        BuildersKt__Builders_commonKt.launch$default(LifecycleOwnerKt.getLifecycleScope(this), null, null, new C10491(new ArrayList(), new LinkedHashSet(), null), 3, null);
    }

    /* compiled from: EditAlbumActivity.kt */
    @Metadata(m606d1 = {"\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\u008a@"}, m607d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"}, m608k = 3, m609mv = {1, 9, 0}, m611xi = 48)
    @DebugMetadata(m619c = "com.glasssutdio.wear.home.album.EditAlbumActivity$shareToSystem$1", m620f = "EditAlbumActivity.kt", m621i = {}, m622l = {HttpStatus.SC_BAD_REQUEST}, m623m = "invokeSuspend", m624n = {}, m625s = {})
    /* renamed from: com.glasssutdio.wear.home.album.EditAlbumActivity$shareToSystem$1 */
    static final class C10491 extends SuspendLambda implements Function2<CoroutineScope, Continuation<? super Unit>, Object> {
        final /* synthetic */ Set<String> $mimeTypeSet;
        final /* synthetic */ List<Uri> $uris;
        int label;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        C10491(List<Uri> list, Set<String> set, Continuation<? super C10491> continuation) {
            super(2, continuation);
            this.$uris = list;
            this.$mimeTypeSet = set;
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
            return EditAlbumActivity.this.new C10491(this.$uris, this.$mimeTypeSet, continuation);
        }

        @Override // kotlin.jvm.functions.Function2
        public final Object invoke(CoroutineScope coroutineScope, Continuation<? super Unit> continuation) {
            return ((C10491) create(coroutineScope, continuation)).invokeSuspend(Unit.INSTANCE);
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Object invokeSuspend(Object obj) {
            Object coroutine_suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED();
            int i = this.label;
            if (i == 0) {
                ResultKt.throwOnFailure(obj);
                Flow flowFetchWatermarkData = EditAlbumActivity.this.fetchWatermarkData();
                final List<Uri> list = this.$uris;
                final EditAlbumActivity editAlbumActivity = EditAlbumActivity.this;
                final Set<String> set = this.$mimeTypeSet;
                this.label = 1;
                if (flowFetchWatermarkData.collect(new FlowCollector() { // from class: com.glasssutdio.wear.home.album.EditAlbumActivity.shareToSystem.1.1
                    @Override // kotlinx.coroutines.flow.FlowCollector
                    public /* bridge */ /* synthetic */ Object emit(Object obj2, Continuation continuation) {
                        return emit((Map<String, GlassAlbumEntity>) obj2, (Continuation<? super Unit>) continuation);
                    }

                    /* JADX WARN: Failed to restore switch over string. Please report as a decompilation issue
                    java.lang.NullPointerException: Cannot invoke "java.util.List.iterator()" because the return value of "jadx.core.dex.visitors.regions.SwitchOverStringVisitor$SwitchData.getNewCases()" is null
                    	at jadx.core.dex.visitors.regions.SwitchOverStringVisitor.restoreSwitchOverString(SwitchOverStringVisitor.java:109)
                    	at jadx.core.dex.visitors.regions.SwitchOverStringVisitor.visitRegion(SwitchOverStringVisitor.java:66)
                    	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseIterativeStepInternal(DepthRegionTraversal.java:77)
                    	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseIterativeStepInternal(DepthRegionTraversal.java:82)
                     */
                    /* JADX WARN: Removed duplicated region for block: B:38:0x00b9  */
                    /* JADX WARN: Removed duplicated region for block: B:42:0x00c5  */
                    /*
                        Code decompiled incorrectly, please refer to instructions dump.
                    */
                    public final Object emit(Map<String, GlassAlbumEntity> map, Continuation<? super Unit> continuation) {
                        String str;
                        Collection<GlassAlbumEntity> collectionValues = map.values();
                        EditAlbumActivity editAlbumActivity2 = editAlbumActivity;
                        Set<String> set2 = set;
                        List<Uri> list2 = list;
                        Iterator<T> it = collectionValues.iterator();
                        while (true) {
                            str = "*/*";
                            if (it.hasNext()) {
                                File file = new File(((GlassAlbumEntity) it.next()).getFilePath());
                                if (file.exists()) {
                                    try {
                                        Uri uriForFile = FileProvider.getUriForFile(editAlbumActivity2, GlobalKt.getPackageName(editAlbumActivity2) + ".provider", file);
                                        String name = file.getName();
                                        Intrinsics.checkNotNullExpressionValue(name, "getName(...)");
                                        String fileExtension = editAlbumActivity2.getFileExtension(name);
                                        switch (fileExtension.hashCode()) {
                                            case 96980:
                                                if (!fileExtension.equals("avi")) {
                                                    break;
                                                } else {
                                                    str = "video/*";
                                                    break;
                                                }
                                            case 97669:
                                                if (!fileExtension.equals("bmp")) {
                                                    break;
                                                } else {
                                                    str = "image/*";
                                                    break;
                                                }
                                            case 102340:
                                                if (!fileExtension.equals("gif")) {
                                                    break;
                                                }
                                                break;
                                            case 105441:
                                                if (!fileExtension.equals("jpg")) {
                                                    break;
                                                }
                                                break;
                                            case 108184:
                                                if (!fileExtension.equals("mkv")) {
                                                    break;
                                                }
                                                break;
                                            case 108273:
                                                if (!fileExtension.equals("mp4")) {
                                                    break;
                                                }
                                                break;
                                            case 108308:
                                                if (!fileExtension.equals("mov")) {
                                                    break;
                                                }
                                                break;
                                            case 110810:
                                                if (!fileExtension.equals("pcm")) {
                                                    break;
                                                } else {
                                                    str = "audio/*";
                                                    break;
                                                }
                                            case 111145:
                                                if (!fileExtension.equals("png")) {
                                                    break;
                                                }
                                                break;
                                            case 3268712:
                                                if (!fileExtension.equals("jpeg")) {
                                                    break;
                                                }
                                                break;
                                        }
                                        XLog.m136i(uriForFile);
                                        if (StringsKt.startsWith$default(str, "audio", false, 2, (Object) null)) {
                                            try {
                                                File fileConvertPcmToWav = PcmToMp3Kt.convertPcmToWav(file);
                                                String str2 = GlobalKt.getPackageName(editAlbumActivity2) + ".provider";
                                                Intrinsics.checkNotNull(fileConvertPcmToWav);
                                                uriForFile = FileProvider.getUriForFile(editAlbumActivity2, str2, fileConvertPcmToWav);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        set2.add(str);
                                        Intrinsics.checkNotNull(uriForFile);
                                        list2.add(uriForFile);
                                    } catch (IllegalArgumentException e2) {
                                        e2.printStackTrace();
                                    }
                                }
                            } else {
                                if (!list.isEmpty()) {
                                    if (list.size() > 1) {
                                        Intent intent = new Intent("android.intent.action.SEND_MULTIPLE");
                                        Set<String> set3 = set;
                                        List<Uri> list3 = list;
                                        intent.setType(set3.size() == 1 ? (String) CollectionsKt.first(set3) : "*/*");
                                        intent.putParcelableArrayListExtra("android.intent.extra.STREAM", new ArrayList<>(list3));
                                        intent.addFlags(1);
                                        Intent intentCreateChooser = Intent.createChooser(intent, "分享到");
                                        ActivityResultLauncher activityResultLauncher = editAlbumActivity.shareLauncher;
                                        Intrinsics.checkNotNull(intentCreateChooser);
                                        activityResultLauncher.launch(intentCreateChooser);
                                    } else {
                                        Intent intent2 = new Intent("android.intent.action.SEND");
                                        Set<String> set4 = set;
                                        List<Uri> list4 = list;
                                        intent2.setType((String) CollectionsKt.first(set4));
                                        intent2.putExtra("android.intent.extra.STREAM", list4.get(0));
                                        intent2.addFlags(1);
                                        List<ResolveInfo> listQueryIntentActivities = editAlbumActivity.getPackageManager().queryIntentActivities(intent2, 65536);
                                        Intrinsics.checkNotNullExpressionValue(listQueryIntentActivities, "queryIntentActivities(...)");
                                        Iterator<ResolveInfo> it2 = listQueryIntentActivities.iterator();
                                        while (it2.hasNext()) {
                                            editAlbumActivity.grantUriPermission(it2.next().activityInfo.packageName, list.get(0), 1);
                                        }
                                        editAlbumActivity.startActivity(Intent.createChooser(intent2, "分享文件到"));
                                    }
                                } else {
                                    XLog.m137i("没有可分享的文件");
                                }
                                return Unit.INSTANCE;
                            }
                        }
                    }
                }, this) == coroutine_suspended) {
                    return coroutine_suspended;
                }
            } else {
                if (i != 1) {
                    throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                }
                ResultKt.throwOnFailure(obj);
            }
            return Unit.INSTANCE;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final String getFileExtension(String fileName) {
        int iLastIndexOf$default = StringsKt.lastIndexOf$default((CharSequence) fileName, FilenameUtils.EXTENSION_SEPARATOR, 0, false, 6, (Object) null);
        if (iLastIndexOf$default != -1 && iLastIndexOf$default < fileName.length() - 1) {
            String strSubstring = fileName.substring(iLastIndexOf$default + 1);
            Intrinsics.checkNotNullExpressionValue(strSubstring, "substring(...)");
            Locale ROOT = Locale.ROOT;
            Intrinsics.checkNotNullExpressionValue(ROOT, "ROOT");
            String lowerCase = strSubstring.toLowerCase(ROOT);
            Intrinsics.checkNotNullExpressionValue(lowerCase, "toLowerCase(...)");
            return lowerCase;
        }
        return "";
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void saveFileToAppGalleryFolder(Context context, File sourceFile) throws Resources.NotFoundException, PackageManager.NameNotFoundException, IOException {
        FileInputStream fileInputStream;
        FileOutputStream fileOutputStream;
        Pair pairM614to;
        String appName = GlobalKt.getAppName(context);
        String mimeType = getMimeType(sourceFile);
        if (StringsKt.startsWith$default(mimeType, "audio/", false, 2, (Object) null)) {
            savePcmAsWavAndAddToMediaStore(context, sourceFile);
            return;
        }
        if (Build.VERSION.SDK_INT >= 29) {
            if (StringsKt.startsWith$default(mimeType, "image/", false, 2, (Object) null)) {
                ContentValues contentValues = new ContentValues();
                contentValues.put("_display_name", sourceFile.getName());
                contentValues.put("mime_type", mimeType);
                contentValues.put("relative_path", Environment.DIRECTORY_DCIM + IOUtils.DIR_SEPARATOR_UNIX + appName);
                pairM614to = TuplesKt.m614to(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            } else if (StringsKt.startsWith$default(mimeType, "video/", false, 2, (Object) null)) {
                ContentValues contentValues2 = new ContentValues();
                contentValues2.put("_display_name", sourceFile.getName());
                contentValues2.put("mime_type", mimeType);
                contentValues2.put("relative_path", Environment.DIRECTORY_DCIM + IOUtils.DIR_SEPARATOR_UNIX + appName);
                pairM614to = TuplesKt.m614to(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues2);
            } else {
                Toast.makeText(context, "不支持的文件类型", 0).show();
                return;
            }
            Uri uri = (Uri) pairM614to.component1();
            ContentValues contentValues3 = (ContentValues) pairM614to.component2();
            ContentResolver contentResolver = context.getContentResolver();
            Uri uriInsert = contentResolver.insert(uri, contentValues3);
            if (uriInsert != null) {
                try {
                    fileOutputStream = new FileInputStream(sourceFile);
                    try {
                        FileInputStream fileInputStream2 = fileOutputStream;
                        OutputStream outputStreamOpenOutputStream = contentResolver.openOutputStream(uriInsert);
                        if (outputStreamOpenOutputStream != null) {
                            fileInputStream = outputStreamOpenOutputStream;
                            try {
                                OutputStream outputStream = fileInputStream;
                                Intrinsics.checkNotNull(outputStream);
                                copyFile(fileInputStream2, outputStream);
                                Unit unit = Unit.INSTANCE;
                                CloseableKt.closeFinally(fileInputStream, null);
                                Unit unit2 = Unit.INSTANCE;
                            } finally {
                                try {
                                    throw th;
                                } finally {
                                }
                            }
                        }
                        CloseableKt.closeFinally(fileOutputStream, null);
                        XLog.m137i("文件保存成功");
                        dismissLoadingDialog();
                        context.sendBroadcast(new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE", uriInsert));
                        String string = getString(C0775R.string.album_glass_10);
                        Intrinsics.checkNotNullExpressionValue(string, "getString(...)");
                        GlobalKt.showToast$default(string, 0, 1, null);
                        return;
                    } finally {
                        try {
                            throw th;
                        } finally {
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    dismissLoadingDialog();
                    XLog.m137i("文件保存失败");
                    return;
                }
            }
            return;
        }
        if (ContextCompat.checkSelfPermission(context, Permission.WRITE_EXTERNAL_STORAGE) != 0) {
            XLog.m137i("缺少写入外部存储权限");
            return;
        }
        if (StringsKt.startsWith$default(mimeType, "image/", false, 2, (Object) null) || StringsKt.startsWith$default(mimeType, "video/", false, 2, (Object) null)) {
            File externalStoragePublicDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
            File file = new File(externalStoragePublicDirectory, appName);
            if (!file.exists()) {
                file.mkdirs();
            }
            File file2 = new File(file, sourceFile.getName());
            try {
                fileInputStream = new FileInputStream(sourceFile);
                try {
                    FileInputStream fileInputStream3 = fileInputStream;
                    fileOutputStream = new FileOutputStream(file2);
                    try {
                        copyFile(fileInputStream3, fileOutputStream);
                        Unit unit3 = Unit.INSTANCE;
                        CloseableKt.closeFinally(fileOutputStream, null);
                        Unit unit4 = Unit.INSTANCE;
                        CloseableKt.closeFinally(fileInputStream, null);
                        context.sendBroadcast(new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE", Uri.fromFile(file2)));
                        String string2 = getString(C0775R.string.album_glass_10);
                        Intrinsics.checkNotNullExpressionValue(string2, "getString(...)");
                        GlobalKt.showToast$default(string2, 0, 1, null);
                    } finally {
                    }
                } finally {
                }
            } catch (Exception e2) {
                e2.printStackTrace();
                dismissLoadingDialog();
                XLog.m137i("文件保存失败");
            }
        } else {
            XLog.m137i("不支持的文件类型");
        }
    }

    private final void savePcmAsWavAndAddToMediaStore(Context context, File pcmFile) throws Resources.NotFoundException, PackageManager.NameNotFoundException, IOException {
        File fileConvertPcmToWav = PcmToMp3Kt.convertPcmToWav(pcmFile);
        String appName = GlobalKt.getAppName(context);
        if (fileConvertPcmToWav != null) {
            if (Build.VERSION.SDK_INT >= 29) {
                ContentValues contentValues = new ContentValues();
                contentValues.put("_display_name", fileConvertPcmToWav.getName());
                contentValues.put("mime_type", "audio/x-wav");
                contentValues.put("relative_path", Environment.DIRECTORY_MUSIC + IOUtils.DIR_SEPARATOR_UNIX + appName);
                ContentResolver contentResolver = context.getContentResolver();
                Uri uriInsert = contentResolver.insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, contentValues);
                if (uriInsert != null) {
                    try {
                        FileInputStream fileInputStream = new FileInputStream(fileConvertPcmToWav);
                        OutputStream outputStreamOpenOutputStream = contentResolver.openOutputStream(uriInsert);
                        if (outputStreamOpenOutputStream != null) {
                            OutputStream outputStream = outputStreamOpenOutputStream;
                            try {
                                Long.valueOf(ByteStreamsKt.copyTo$default(fileInputStream, outputStream, 0, 2, null));
                                CloseableKt.closeFinally(outputStream, null);
                            } finally {
                            }
                        }
                        fileInputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                MediaScannerConnection.scanFile(context, new String[]{fileConvertPcmToWav.getAbsolutePath()}, new String[]{"audio/x-wav"}, new MediaScannerConnection.OnScanCompletedListener() { // from class: com.glasssutdio.wear.home.album.EditAlbumActivity$$ExternalSyntheticLambda0
                    @Override // android.media.MediaScannerConnection.OnScanCompletedListener
                    public final void onScanCompleted(String str, Uri uri) {
                        EditAlbumActivity.savePcmAsWavAndAddToMediaStore$lambda$18$lambda$17(str, uri);
                    }
                });
            }
        }
        dismissLoadingDialog();
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Failed to restore switch over string. Please report as a decompilation issue
    java.lang.NullPointerException: Cannot invoke "java.util.List.iterator()" because the return value of "jadx.core.dex.visitors.regions.SwitchOverStringVisitor$SwitchData.getNewCases()" is null
    	at jadx.core.dex.visitors.regions.SwitchOverStringVisitor.restoreSwitchOverString(SwitchOverStringVisitor.java:109)
    	at jadx.core.dex.visitors.regions.SwitchOverStringVisitor.visitRegion(SwitchOverStringVisitor.java:66)
    	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseIterativeStepInternal(DepthRegionTraversal.java:77)
    	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseIterativeStepInternal(DepthRegionTraversal.java:82)
    	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseIterative(DepthRegionTraversal.java:31)
    	at jadx.core.dex.visitors.regions.SwitchOverStringVisitor.visit(SwitchOverStringVisitor.java:60)
     */
    /* JADX WARN: Removed duplicated region for block: B:20:0x0054 A[RETURN, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:27:0x0069 A[RETURN, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:31:0x0075 A[RETURN, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:32:0x0078 A[ORIG_RETURN, RETURN] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private final String getMimeType(File file) {
        String name = file.getName();
        Intrinsics.checkNotNull(name);
        String strSubstringAfterLast = StringsKt.substringAfterLast(name, FilenameUtils.EXTENSION_SEPARATOR, "");
        Locale ROOT = Locale.ROOT;
        Intrinsics.checkNotNullExpressionValue(ROOT, "ROOT");
        String lowerCase = strSubstringAfterLast.toLowerCase(ROOT);
        Intrinsics.checkNotNullExpressionValue(lowerCase, "toLowerCase(...)");
        switch (lowerCase.hashCode()) {
            case 52316:
                if (lowerCase.equals("3gp")) {
                    return "video/*";
                }
                return "application/octet-stream";
            case 102340:
                if (lowerCase.equals("gif")) {
                    return "image/*";
                }
                break;
            case 105441:
                if (!lowerCase.equals("jpg")) {
                }
                break;
            case 108272:
                if (lowerCase.equals("mp3")) {
                    return "audio/*";
                }
                break;
            case 108273:
                if (!lowerCase.equals("mp4")) {
                }
                break;
            case 110810:
                if (!lowerCase.equals("pcm")) {
                }
                break;
            case 111145:
                if (!lowerCase.equals("png")) {
                }
                break;
            case 3268712:
                if (!lowerCase.equals("jpeg")) {
                }
                break;
        }
    }

    private final void copyFile(InputStream inputStream, OutputStream outputStream) throws IOException {
        byte[] bArr = new byte[1024];
        while (true) {
            int i = inputStream.read(bArr);
            if (i <= 0) {
                return;
            } else {
                outputStream.write(bArr, 0, i);
            }
        }
    }
}
