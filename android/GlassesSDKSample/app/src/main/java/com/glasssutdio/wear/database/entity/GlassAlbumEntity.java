package com.glasssutdio.wear.database.entity;

import android.os.Parcel;
import android.os.Parcelable;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: GlassAlbumEntity.kt */
@Metadata(m606d1 = {"\u0000B\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\t\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b%\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0007\u0018\u00002\u00020\u0001B\u0007\b\u0016¢\u0006\u0002\u0010\u0002Bi\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\u0006\u0010\u0005\u001a\u00020\u0004\u0012\u0006\u0010\u0006\u001a\u00020\u0004\u0012\u0006\u0010\u0007\u001a\u00020\u0004\u0012\u0006\u0010\b\u001a\u00020\t\u0012\u0006\u0010\n\u001a\u00020\t\u0012\u0006\u0010\u000b\u001a\u00020\u0004\u0012\u0006\u0010\f\u001a\u00020\r\u0012\u0006\u0010\u000e\u001a\u00020\t\u0012\u0006\u0010\u000f\u001a\u00020\t\u0012\b\b\u0002\u0010\u0010\u001a\u00020\u0011\u0012\b\b\u0002\u0010\u0012\u001a\u00020\u0011¢\u0006\u0002\u0010\u0013J\t\u00104\u001a\u00020\tHÖ\u0001J\u0013\u00105\u001a\u00020\u00112\b\u00106\u001a\u0004\u0018\u000107H\u0096\u0002J\b\u00108\u001a\u00020\tH\u0016J\u0019\u00109\u001a\u00020:2\u0006\u0010;\u001a\u00020<2\u0006\u0010=\u001a\u00020\tHÖ\u0001R\u001e\u0010\u0012\u001a\u00020\u00118\u0006@\u0006X\u0087\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0014\u0010\u0015\"\u0004\b\u0016\u0010\u0017R\u001e\u0010\u0010\u001a\u00020\u00118\u0006@\u0006X\u0087\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0018\u0010\u0015\"\u0004\b\u0019\u0010\u0017R\u001e\u0010\u000b\u001a\u00020\u00048\u0006@\u0006X\u0087\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001a\u0010\u001b\"\u0004\b\u001c\u0010\u001dR\u001e\u0010\u0003\u001a\u00020\u00048\u0006@\u0006X\u0087\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001e\u0010\u001b\"\u0004\b\u001f\u0010\u001dR\u001e\u0010\u0006\u001a\u00020\u00048\u0006@\u0006X\u0087\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b \u0010\u001b\"\u0004\b!\u0010\u001dR\u001e\u0010\b\u001a\u00020\t8\u0006@\u0006X\u0087\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\"\u0010#\"\u0004\b$\u0010%R\u001e\u0010\u000e\u001a\u00020\t8\u0006@\u0006X\u0087\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b&\u0010#\"\u0004\b'\u0010%R\u001e\u0010\u0005\u001a\u00020\u00048\u0006@\u0006X\u0087\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b(\u0010\u001b\"\u0004\b)\u0010\u001dR\u001e\u0010\f\u001a\u00020\r8\u0006@\u0006X\u0087\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b*\u0010+\"\u0004\b,\u0010-R\u001e\u0010\u000f\u001a\u00020\t8\u0006@\u0006X\u0087\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b.\u0010#\"\u0004\b/\u0010%R\u001e\u0010\u0007\u001a\u00020\u00048\u0006@\u0006X\u0087\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b0\u0010\u001b\"\u0004\b1\u0010\u001dR\u001e\u0010\n\u001a\u00020\t8\u0006@\u0006X\u0087\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b2\u0010#\"\u0004\b3\u0010%¨\u0006>"}, m607d2 = {"Lcom/glasssutdio/wear/database/entity/GlassAlbumEntity;", "Landroid/os/Parcelable;", "()V", "fileName", "", "mac", "filePath", "videoFirstFrame", "fileType", "", "videoLength", "fileDate", "timestamp", "", "horizontalCalibration", "userLike", "eisInProgress", "", "editSelect", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;IILjava/lang/String;JIIZZ)V", "getEditSelect", "()Z", "setEditSelect", "(Z)V", "getEisInProgress", "setEisInProgress", "getFileDate", "()Ljava/lang/String;", "setFileDate", "(Ljava/lang/String;)V", "getFileName", "setFileName", "getFilePath", "setFilePath", "getFileType", "()I", "setFileType", "(I)V", "getHorizontalCalibration", "setHorizontalCalibration", "getMac", "setMac", "getTimestamp", "()J", "setTimestamp", "(J)V", "getUserLike", "setUserLike", "getVideoFirstFrame", "setVideoFirstFrame", "getVideoLength", "setVideoLength", "describeContents", "equals", "other", "", "hashCode", "writeToParcel", "", "parcel", "Landroid/os/Parcel;", "flags", "app_release"}, m608k = 1, m609mv = {1, 9, 0}, m611xi = 48)
/* loaded from: classes.dex */
public final class GlassAlbumEntity implements Parcelable {
    public static final Parcelable.Creator<GlassAlbumEntity> CREATOR = new Creator();
    private boolean editSelect;
    private boolean eisInProgress;
    private String fileDate;
    private String fileName;
    private String filePath;
    private int fileType;
    private int horizontalCalibration;
    private String mac;
    private long timestamp;
    private int userLike;
    private String videoFirstFrame;
    private int videoLength;

    /* compiled from: GlassAlbumEntity.kt */
    @Metadata(m608k = 3, m609mv = {1, 9, 0}, m611xi = 48)
    public static final class Creator implements Parcelable.Creator<GlassAlbumEntity> {
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public final GlassAlbumEntity createFromParcel(Parcel parcel) {
            Intrinsics.checkNotNullParameter(parcel, "parcel");
            return new GlassAlbumEntity(parcel.readString(), parcel.readString(), parcel.readString(), parcel.readString(), parcel.readInt(), parcel.readInt(), parcel.readString(), parcel.readLong(), parcel.readInt(), parcel.readInt(), parcel.readInt() != 0, parcel.readInt() != 0);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public final GlassAlbumEntity[] newArray(int i) {
            return new GlassAlbumEntity[i];
        }
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        Intrinsics.checkNotNullParameter(parcel, "out");
        parcel.writeString(this.fileName);
        parcel.writeString(this.mac);
        parcel.writeString(this.filePath);
        parcel.writeString(this.videoFirstFrame);
        parcel.writeInt(this.fileType);
        parcel.writeInt(this.videoLength);
        parcel.writeString(this.fileDate);
        parcel.writeLong(this.timestamp);
        parcel.writeInt(this.horizontalCalibration);
        parcel.writeInt(this.userLike);
        parcel.writeInt(this.eisInProgress ? 1 : 0);
        parcel.writeInt(this.editSelect ? 1 : 0);
    }

    public GlassAlbumEntity(String fileName, String mac, String filePath, String videoFirstFrame, int i, int i2, String fileDate, long j, int i3, int i4, boolean z, boolean z2) {
        Intrinsics.checkNotNullParameter(fileName, "fileName");
        Intrinsics.checkNotNullParameter(mac, "mac");
        Intrinsics.checkNotNullParameter(filePath, "filePath");
        Intrinsics.checkNotNullParameter(videoFirstFrame, "videoFirstFrame");
        Intrinsics.checkNotNullParameter(fileDate, "fileDate");
        this.fileName = fileName;
        this.mac = mac;
        this.filePath = filePath;
        this.videoFirstFrame = videoFirstFrame;
        this.fileType = i;
        this.videoLength = i2;
        this.fileDate = fileDate;
        this.timestamp = j;
        this.horizontalCalibration = i3;
        this.userLike = i4;
        this.eisInProgress = z;
        this.editSelect = z2;
    }

    public /* synthetic */ GlassAlbumEntity(String str, String str2, String str3, String str4, int i, int i2, String str5, long j, int i3, int i4, boolean z, boolean z2, int i5, DefaultConstructorMarker defaultConstructorMarker) {
        this(str, str2, str3, str4, i, i2, str5, j, i3, i4, (i5 & 1024) != 0 ? false : z, (i5 & 2048) != 0 ? false : z2);
    }

    public final String getFileName() {
        return this.fileName;
    }

    public final void setFileName(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.fileName = str;
    }

    public final String getMac() {
        return this.mac;
    }

    public final void setMac(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.mac = str;
    }

    public final String getFilePath() {
        return this.filePath;
    }

    public final void setFilePath(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.filePath = str;
    }

    public final String getVideoFirstFrame() {
        return this.videoFirstFrame;
    }

    public final void setVideoFirstFrame(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.videoFirstFrame = str;
    }

    public final int getFileType() {
        return this.fileType;
    }

    public final void setFileType(int i) {
        this.fileType = i;
    }

    public final int getVideoLength() {
        return this.videoLength;
    }

    public final void setVideoLength(int i) {
        this.videoLength = i;
    }

    public final String getFileDate() {
        return this.fileDate;
    }

    public final void setFileDate(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.fileDate = str;
    }

    public final long getTimestamp() {
        return this.timestamp;
    }

    public final void setTimestamp(long j) {
        this.timestamp = j;
    }

    public final int getHorizontalCalibration() {
        return this.horizontalCalibration;
    }

    public final void setHorizontalCalibration(int i) {
        this.horizontalCalibration = i;
    }

    public final int getUserLike() {
        return this.userLike;
    }

    public final void setUserLike(int i) {
        this.userLike = i;
    }

    public final boolean getEisInProgress() {
        return this.eisInProgress;
    }

    public final void setEisInProgress(boolean z) {
        this.eisInProgress = z;
    }

    public final boolean getEditSelect() {
        return this.editSelect;
    }

    public final void setEditSelect(boolean z) {
        this.editSelect = z;
    }

    public GlassAlbumEntity() {
        this("", "", "", "", 0, 0, "", 0L, 0, 0, false, false);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!Intrinsics.areEqual(getClass(), other != null ? other.getClass() : null)) {
            return false;
        }
        Intrinsics.checkNotNull(other, "null cannot be cast to non-null type com.glasssutdio.wear.database.entity.GlassAlbumEntity");
        GlassAlbumEntity glassAlbumEntity = (GlassAlbumEntity) other;
        return Intrinsics.areEqual(this.fileName, glassAlbumEntity.fileName) && Intrinsics.areEqual(this.mac, glassAlbumEntity.mac) && Intrinsics.areEqual(this.filePath, glassAlbumEntity.filePath) && Intrinsics.areEqual(this.videoFirstFrame, glassAlbumEntity.videoFirstFrame) && this.fileType == glassAlbumEntity.fileType && this.videoLength == glassAlbumEntity.videoLength && Intrinsics.areEqual(this.fileDate, glassAlbumEntity.fileDate) && this.timestamp == glassAlbumEntity.timestamp && this.horizontalCalibration == glassAlbumEntity.horizontalCalibration && this.userLike == glassAlbumEntity.userLike && this.eisInProgress == glassAlbumEntity.eisInProgress && this.editSelect == glassAlbumEntity.editSelect;
    }

    public int hashCode() {
        return (((((((((((((((((((((this.fileName.hashCode() * 31) + this.mac.hashCode()) * 31) + this.filePath.hashCode()) * 31) + this.videoFirstFrame.hashCode()) * 31) + this.fileType) * 31) + this.videoLength) * 31) + this.fileDate.hashCode()) * 31) + Long.hashCode(this.timestamp)) * 31) + this.horizontalCalibration) * 31) + this.userLike) * 31) + Boolean.hashCode(this.eisInProgress)) * 31) + Boolean.hashCode(this.editSelect);
    }
}
