package com.glasssutdio.wear.home.album.p005vm;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.glasssutdio.wear.all.ThreadExtKt;
import com.glasssutdio.wear.all.utils.GFileUtilKt;
import com.glasssutdio.wear.database.entity.GlassAlbumEntity;
import com.glasssutdio.wear.depository.AlbumDepository;
import java.util.List;
import java.util.TreeMap;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: AlbumListViewModel.kt */
@Metadata(m606d1 = {"\u0000H\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\b\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0010\u000e\n\u0002\b\u000f\u0018\u00002\u00020\u0001:\u0001)B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0002\u0010\u0004J\u000e\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0017\u001a\u00020\fJ\u0006\u0010\u0018\u001a\u00020\u0003J\u0015\u0010\u0019\u001a\u0004\u0018\u00010\n2\u0006\u0010\u001a\u001a\u00020\u001b¢\u0006\u0002\u0010\u001cJ\u001e\u0010\u001d\u001a\u00020\u00162\u0006\u0010\u001e\u001a\u00020\u001b2\u0006\u0010\u001f\u001a\u00020\u001b2\u0006\u0010\u001a\u001a\u00020\u001bJ\u0006\u0010 \u001a\u00020\u0016J\f\u0010!\u001a\b\u0012\u0004\u0012\u00020\f0\u000bJ\u0014\u0010\"\u001a\u00020\u00162\f\u0010#\u001a\b\u0012\u0004\u0012\u00020\f0\u000bJ\u0006\u0010$\u001a\u00020\u0016J\u0006\u0010%\u001a\u00020\u0016J\u0006\u0010&\u001a\u00020\u0016J\u0006\u0010'\u001a\u00020\u0016J\u000e\u0010(\u001a\u00020\u00162\u0006\u0010\u0017\u001a\u00020\fR\u0014\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R,\u0010\b\u001a\u0014\u0012\u0004\u0012\u00020\n\u0012\n\u0012\b\u0012\u0004\u0012\u00020\f0\u000b0\tX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\r\u0010\u000e\"\u0004\b\u000f\u0010\u0010R\u0017\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00070\u00128F¢\u0006\u0006\u001a\u0004\b\u0013\u0010\u0014¨\u0006*"}, m607d2 = {"Lcom/glasssutdio/wear/home/album/vm/AlbumListViewModel;", "Landroidx/lifecycle/ViewModel;", "albumDepository", "Lcom/glasssutdio/wear/depository/AlbumDepository;", "(Lcom/glasssutdio/wear/depository/AlbumDepository;)V", "_uiState", "Landroidx/lifecycle/MutableLiveData;", "Lcom/glasssutdio/wear/home/album/vm/AlbumListViewModel$DeviceAlbumUI;", "data", "Ljava/util/TreeMap;", "", "", "Lcom/glasssutdio/wear/database/entity/GlassAlbumEntity;", "getData", "()Ljava/util/TreeMap;", "setData", "(Ljava/util/TreeMap;)V", "uiState", "Landroidx/lifecycle/LiveData;", "getUiState", "()Landroidx/lifecycle/LiveData;", "deleteMediaFile", "", "album", "getAlbumDepositoryInstance", "getIndexByFileName", "fileName", "", "(Ljava/lang/String;)Ljava/lang/Integer;", "getPhotoTextFile", "url", "dirPath", "initAllData", "initDetailData", "initDetailList", "list", "initImageData", "initLikeData", "initRecordData", "initVideoData", "updateLikeOrNot", "DeviceAlbumUI", "app_release"}, m608k = 1, m609mv = {1, 9, 0}, m611xi = 48)
/* loaded from: classes.dex */
public final class AlbumListViewModel extends ViewModel {
    private final MutableLiveData<DeviceAlbumUI> _uiState;
    private final AlbumDepository albumDepository;
    private TreeMap<Integer, List<GlassAlbumEntity>> data;

    public AlbumListViewModel(AlbumDepository albumDepository) {
        Intrinsics.checkNotNullParameter(albumDepository, "albumDepository");
        this.albumDepository = albumDepository;
        this.data = new TreeMap<>();
        this._uiState = new MutableLiveData<>();
    }

    public final TreeMap<Integer, List<GlassAlbumEntity>> getData() {
        return this.data;
    }

    public final void setData(TreeMap<Integer, List<GlassAlbumEntity>> treeMap) {
        Intrinsics.checkNotNullParameter(treeMap, "<set-?>");
        this.data = treeMap;
    }

    public final LiveData<DeviceAlbumUI> getUiState() {
        return this._uiState;
    }

    public final void initDetailList(List<GlassAlbumEntity> list) {
        Intrinsics.checkNotNullParameter(list, "list");
        this.albumDepository.initDetailList(list);
    }

    /* renamed from: getAlbumDepositoryInstance, reason: from getter */
    public final AlbumDepository getAlbumDepository() {
        return this.albumDepository;
    }

    public final void initAllData() throws InterruptedException {
        ThreadExtKt.ktxRunOnBgSingleNetWork(this, new Function1<AlbumListViewModel, Unit>() { // from class: com.glasssutdio.wear.home.album.vm.AlbumListViewModel.initAllData.1
            @Override // kotlin.jvm.functions.Function1
            public /* bridge */ /* synthetic */ Unit invoke(AlbumListViewModel albumListViewModel) {
                invoke2(albumListViewModel);
                return Unit.INSTANCE;
            }

            /* renamed from: invoke, reason: avoid collision after fix types in other method */
            public final void invoke2(AlbumListViewModel ktxRunOnBgSingleNetWork) {
                Intrinsics.checkNotNullParameter(ktxRunOnBgSingleNetWork, "$this$ktxRunOnBgSingleNetWork");
                TreeMap<Integer, List<GlassAlbumEntity>> treeMapQueryAllMedia = ktxRunOnBgSingleNetWork.albumDepository.queryAllMedia();
                ktxRunOnBgSingleNetWork.getData().clear();
                ktxRunOnBgSingleNetWork.setData(treeMapQueryAllMedia);
                ktxRunOnBgSingleNetWork._uiState.postValue(new DeviceAlbumUI(true));
            }
        });
    }

    public final void initImageData() {
        ThreadExtKt.ktxRunOnBgSingle(this, new Function1<AlbumListViewModel, Unit>() { // from class: com.glasssutdio.wear.home.album.vm.AlbumListViewModel.initImageData.1
            @Override // kotlin.jvm.functions.Function1
            public /* bridge */ /* synthetic */ Unit invoke(AlbumListViewModel albumListViewModel) {
                invoke2(albumListViewModel);
                return Unit.INSTANCE;
            }

            /* renamed from: invoke, reason: avoid collision after fix types in other method */
            public final void invoke2(AlbumListViewModel ktxRunOnBgSingle) {
                Intrinsics.checkNotNullParameter(ktxRunOnBgSingle, "$this$ktxRunOnBgSingle");
                TreeMap<Integer, List<GlassAlbumEntity>> treeMapQueryImageMedia = ktxRunOnBgSingle.albumDepository.queryImageMedia(1);
                ktxRunOnBgSingle.getData().clear();
                ktxRunOnBgSingle.setData(treeMapQueryImageMedia);
                ktxRunOnBgSingle._uiState.postValue(new DeviceAlbumUI(true));
            }
        });
    }

    public final void initVideoData() {
        ThreadExtKt.ktxRunOnBgSingle(this, new Function1<AlbumListViewModel, Unit>() { // from class: com.glasssutdio.wear.home.album.vm.AlbumListViewModel.initVideoData.1
            @Override // kotlin.jvm.functions.Function1
            public /* bridge */ /* synthetic */ Unit invoke(AlbumListViewModel albumListViewModel) {
                invoke2(albumListViewModel);
                return Unit.INSTANCE;
            }

            /* renamed from: invoke, reason: avoid collision after fix types in other method */
            public final void invoke2(AlbumListViewModel ktxRunOnBgSingle) {
                Intrinsics.checkNotNullParameter(ktxRunOnBgSingle, "$this$ktxRunOnBgSingle");
                TreeMap<Integer, List<GlassAlbumEntity>> treeMapQueryImageMedia = ktxRunOnBgSingle.albumDepository.queryImageMedia(2);
                ktxRunOnBgSingle.getData().clear();
                ktxRunOnBgSingle.setData(treeMapQueryImageMedia);
                ktxRunOnBgSingle._uiState.postValue(new DeviceAlbumUI(true));
            }
        });
    }

    public final void initLikeData() {
        ThreadExtKt.ktxRunOnBgSingle(this, new Function1<AlbumListViewModel, Unit>() { // from class: com.glasssutdio.wear.home.album.vm.AlbumListViewModel.initLikeData.1
            @Override // kotlin.jvm.functions.Function1
            public /* bridge */ /* synthetic */ Unit invoke(AlbumListViewModel albumListViewModel) {
                invoke2(albumListViewModel);
                return Unit.INSTANCE;
            }

            /* renamed from: invoke, reason: avoid collision after fix types in other method */
            public final void invoke2(AlbumListViewModel ktxRunOnBgSingle) {
                Intrinsics.checkNotNullParameter(ktxRunOnBgSingle, "$this$ktxRunOnBgSingle");
                TreeMap<Integer, List<GlassAlbumEntity>> treeMapQueryLikeMedia = ktxRunOnBgSingle.albumDepository.queryLikeMedia();
                ktxRunOnBgSingle.getData().clear();
                ktxRunOnBgSingle.setData(treeMapQueryLikeMedia);
                ktxRunOnBgSingle._uiState.postValue(new DeviceAlbumUI(true));
            }
        });
    }

    public final void initRecordData() {
        ThreadExtKt.ktxRunOnBgSingle(this, new Function1<AlbumListViewModel, Unit>() { // from class: com.glasssutdio.wear.home.album.vm.AlbumListViewModel.initRecordData.1
            @Override // kotlin.jvm.functions.Function1
            public /* bridge */ /* synthetic */ Unit invoke(AlbumListViewModel albumListViewModel) {
                invoke2(albumListViewModel);
                return Unit.INSTANCE;
            }

            /* renamed from: invoke, reason: avoid collision after fix types in other method */
            public final void invoke2(AlbumListViewModel ktxRunOnBgSingle) {
                Intrinsics.checkNotNullParameter(ktxRunOnBgSingle, "$this$ktxRunOnBgSingle");
                TreeMap<Integer, List<GlassAlbumEntity>> treeMapQueryImageMedia = ktxRunOnBgSingle.albumDepository.queryImageMedia(3);
                ktxRunOnBgSingle.getData().clear();
                ktxRunOnBgSingle.setData(treeMapQueryImageMedia);
                ktxRunOnBgSingle._uiState.postValue(new DeviceAlbumUI(true));
            }
        });
    }

    public final Integer getIndexByFileName(String fileName) {
        Intrinsics.checkNotNullParameter(fileName, "fileName");
        return this.albumDepository.getIndexByFileName(fileName);
    }

    public final List<GlassAlbumEntity> initDetailData() {
        return this.albumDepository.getListData();
    }

    public final void getPhotoTextFile(String url, String dirPath, String fileName) {
        Intrinsics.checkNotNullParameter(url, "url");
        Intrinsics.checkNotNullParameter(dirPath, "dirPath");
        Intrinsics.checkNotNullParameter(fileName, "fileName");
        this.albumDepository.getPhotoTextFile(url, dirPath, fileName);
    }

    public final void updateLikeOrNot(final GlassAlbumEntity album) {
        Intrinsics.checkNotNullParameter(album, "album");
        ThreadExtKt.ktxRunOnBgSingleDao(this, new Function1<AlbumListViewModel, Unit>() { // from class: com.glasssutdio.wear.home.album.vm.AlbumListViewModel.updateLikeOrNot.1
            {
                super(1);
            }

            @Override // kotlin.jvm.functions.Function1
            public /* bridge */ /* synthetic */ Unit invoke(AlbumListViewModel albumListViewModel) {
                invoke2(albumListViewModel);
                return Unit.INSTANCE;
            }

            /* renamed from: invoke, reason: avoid collision after fix types in other method */
            public final void invoke2(AlbumListViewModel ktxRunOnBgSingleDao) {
                Intrinsics.checkNotNullParameter(ktxRunOnBgSingleDao, "$this$ktxRunOnBgSingleDao");
                ktxRunOnBgSingleDao.albumDepository.saveAlbum(album);
            }
        });
    }

    public final void deleteMediaFile(final GlassAlbumEntity album) {
        Intrinsics.checkNotNullParameter(album, "album");
        ThreadExtKt.ktxRunOnBgSingleDao(this, new Function1<AlbumListViewModel, Unit>() { // from class: com.glasssutdio.wear.home.album.vm.AlbumListViewModel.deleteMediaFile.1
            {
                super(1);
            }

            @Override // kotlin.jvm.functions.Function1
            public /* bridge */ /* synthetic */ Unit invoke(AlbumListViewModel albumListViewModel) {
                invoke2(albumListViewModel);
                return Unit.INSTANCE;
            }

            /* renamed from: invoke, reason: avoid collision after fix types in other method */
            public final void invoke2(AlbumListViewModel ktxRunOnBgSingleDao) {
                Intrinsics.checkNotNullParameter(ktxRunOnBgSingleDao, "$this$ktxRunOnBgSingleDao");
                ktxRunOnBgSingleDao.albumDepository.deleteFile(album);
                GFileUtilKt.deleteFile(album.getFilePath());
            }
        });
    }

    /* compiled from: AlbumListViewModel.kt */
    @Metadata(m606d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0002\b\b\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0002\u0010\u0004J\t\u0010\u0007\u001a\u00020\u0003HÆ\u0003J\u0013\u0010\b\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u0003HÆ\u0001J\u0013\u0010\t\u001a\u00020\u00032\b\u0010\n\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010\u000b\u001a\u00020\fHÖ\u0001J\t\u0010\r\u001a\u00020\u000eHÖ\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006¨\u0006\u000f"}, m607d2 = {"Lcom/glasssutdio/wear/home/album/vm/AlbumListViewModel$DeviceAlbumUI;", "", "refresh", "", "(Z)V", "getRefresh", "()Z", "component1", "copy", "equals", "other", "hashCode", "", "toString", "", "app_release"}, m608k = 1, m609mv = {1, 9, 0}, m611xi = 48)
    public static final /* data */ class DeviceAlbumUI {
        private final boolean refresh;

        public static /* synthetic */ DeviceAlbumUI copy$default(DeviceAlbumUI deviceAlbumUI, boolean z, int i, Object obj) {
            if ((i & 1) != 0) {
                z = deviceAlbumUI.refresh;
            }
            return deviceAlbumUI.copy(z);
        }

        /* renamed from: component1, reason: from getter */
        public final boolean getRefresh() {
            return this.refresh;
        }

        public final DeviceAlbumUI copy(boolean refresh) {
            return new DeviceAlbumUI(refresh);
        }

        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            return (other instanceof DeviceAlbumUI) && this.refresh == ((DeviceAlbumUI) other).refresh;
        }

        public int hashCode() {
            boolean z = this.refresh;
            if (z) {
                return 1;
            }
            return z ? 1 : 0;
        }

        public String toString() {
            return "DeviceAlbumUI(refresh=" + this.refresh + ')';
        }

        public DeviceAlbumUI(boolean z) {
            this.refresh = z;
        }

        public final boolean getRefresh() {
            return this.refresh;
        }
    }
}
