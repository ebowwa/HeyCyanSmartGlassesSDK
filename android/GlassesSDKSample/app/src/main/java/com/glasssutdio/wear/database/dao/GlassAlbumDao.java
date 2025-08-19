package com.glasssutdio.wear.database.dao;

import com.glasssutdio.wear.database.entity.GlassAlbumEntity;
import java.util.List;
import kotlin.Metadata;

/* compiled from: GlassAlbumDao.kt */
@Metadata(m606d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0010!\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\bg\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001J\u001a\u0010\u0003\u001a\u0004\u0018\u00010\u00022\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0005H'J\u0018\u0010\u0007\u001a\u00020\u00022\u0006\u0010\b\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0005H'J\u0016\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00020\n2\u0006\u0010\u0006\u001a\u00020\u0005H'J\u001e\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00020\n2\u0006\u0010\u0006\u001a\u00020\u00052\u0006\u0010\f\u001a\u00020\rH'J\u0016\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00020\n2\u0006\u0010\u0006\u001a\u00020\u0005H'¨\u0006\u000f"}, m607d2 = {"Lcom/glasssutdio/wear/database/dao/GlassAlbumDao;", "Lcom/glasssutdio/wear/database/dao/BaseDao;", "Lcom/glasssutdio/wear/database/entity/GlassAlbumEntity;", "queryAlbumFileByName", "name", "", "mac", "queryAlbumFilesByDate", "date", "queryAllFile", "", "queryImageFileByteType", "type", "", "queryLikeMedia", "app_release"}, m608k = 1, m609mv = {1, 9, 0}, m611xi = 48)
/* loaded from: classes.dex */
public interface GlassAlbumDao extends BaseDao<GlassAlbumEntity> {
    GlassAlbumEntity queryAlbumFileByName(String name, String mac);

    GlassAlbumEntity queryAlbumFilesByDate(String date, String mac);

    List<GlassAlbumEntity> queryAllFile(String mac);

    List<GlassAlbumEntity> queryImageFileByteType(String mac, int type);

    List<GlassAlbumEntity> queryLikeMedia(String mac);
}
