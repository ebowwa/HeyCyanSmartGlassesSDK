package com.glasssutdio.wear.bus;

import com.glasssutdio.wear.database.entity.GlassAlbumEntity;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: AlbumDownloadSuccessfullyEvent.kt */
@Metadata(m606d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u0016\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0002\u0010\u0004R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006¨\u0006\u0007"}, m607d2 = {"Lcom/glasssutdio/wear/bus/AlbumDownloadSuccessfullyEvent;", "Lcom/glasssutdio/wear/bus/BusEvent;", "entity", "Lcom/glasssutdio/wear/database/entity/GlassAlbumEntity;", "(Lcom/glasssutdio/wear/database/entity/GlassAlbumEntity;)V", "getEntity", "()Lcom/glasssutdio/wear/database/entity/GlassAlbumEntity;", "app_release"}, m608k = 1, m609mv = {1, 9, 0}, m611xi = 48)
/* loaded from: classes.dex */
public class AlbumDownloadSuccessfullyEvent extends BusEvent {
    private final GlassAlbumEntity entity;

    public AlbumDownloadSuccessfullyEvent(GlassAlbumEntity entity) {
        Intrinsics.checkNotNullParameter(entity, "entity");
        this.entity = entity;
    }

    public final GlassAlbumEntity getEntity() {
        return this.entity;
    }
}
