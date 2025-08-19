package com.glasssutdio.wear.depository.bean;

import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.cookie.ClientCookie;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: PictureDownloadBean.kt */
@Metadata(m606d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u000b\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003¢\u0006\u0002\u0010\u0005J\t\u0010\u000b\u001a\u00020\u0003HÆ\u0003J\t\u0010\f\u001a\u00020\u0003HÆ\u0003J\u001d\u0010\r\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0003HÆ\u0001J\u0013\u0010\u000e\u001a\u00020\u000f2\b\u0010\u0010\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010\u0011\u001a\u00020\u0012HÖ\u0001J\t\u0010\u0013\u001a\u00020\u0003HÖ\u0001R\u0011\u0010\u0004\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007R\u001a\u0010\u0002\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\b\u0010\u0007\"\u0004\b\t\u0010\n¨\u0006\u0014"}, m607d2 = {"Lcom/glasssutdio/wear/depository/bean/PictureDownloadBean;", "", ClientCookie.PATH_ATTR, "", "fileName", "(Ljava/lang/String;Ljava/lang/String;)V", "getFileName", "()Ljava/lang/String;", "getPath", "setPath", "(Ljava/lang/String;)V", "component1", "component2", "copy", "equals", "", "other", "hashCode", "", "toString", "app_release"}, m608k = 1, m609mv = {1, 9, 0}, m611xi = 48)
/* loaded from: classes.dex */
public final /* data */ class PictureDownloadBean {
    private final String fileName;
    private String path;

    public static /* synthetic */ PictureDownloadBean copy$default(PictureDownloadBean pictureDownloadBean, String str, String str2, int i, Object obj) {
        if ((i & 1) != 0) {
            str = pictureDownloadBean.path;
        }
        if ((i & 2) != 0) {
            str2 = pictureDownloadBean.fileName;
        }
        return pictureDownloadBean.copy(str, str2);
    }

    /* renamed from: component1, reason: from getter */
    public final String getPath() {
        return this.path;
    }

    /* renamed from: component2, reason: from getter */
    public final String getFileName() {
        return this.fileName;
    }

    public final PictureDownloadBean copy(String path, String fileName) {
        Intrinsics.checkNotNullParameter(path, "path");
        Intrinsics.checkNotNullParameter(fileName, "fileName");
        return new PictureDownloadBean(path, fileName);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof PictureDownloadBean)) {
            return false;
        }
        PictureDownloadBean pictureDownloadBean = (PictureDownloadBean) other;
        return Intrinsics.areEqual(this.path, pictureDownloadBean.path) && Intrinsics.areEqual(this.fileName, pictureDownloadBean.fileName);
    }

    public int hashCode() {
        return (this.path.hashCode() * 31) + this.fileName.hashCode();
    }

    public String toString() {
        return "PictureDownloadBean(path=" + this.path + ", fileName=" + this.fileName + ')';
    }

    public PictureDownloadBean(String path, String fileName) {
        Intrinsics.checkNotNullParameter(path, "path");
        Intrinsics.checkNotNullParameter(fileName, "fileName");
        this.path = path;
        this.fileName = fileName;
    }

    public final String getFileName() {
        return this.fileName;
    }

    public final String getPath() {
        return this.path;
    }

    public final void setPath(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.path = str;
    }
}
