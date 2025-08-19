package com.glasssutdio.wear.database;

import android.content.Context;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.glasssutdio.wear.database.dao.GlassAiChatDao;
import com.glasssutdio.wear.database.dao.GlassAlbumDao;
import com.glasssutdio.wear.database.dao.GlassDeviceSettingDao;
import com.glasssutdio.wear.database.dao.TranslateDao;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: GlassDatabase.kt */
@Metadata(m606d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b'\u0018\u0000 \u000b2\u00020\u0001:\u0001\u000bB\u0005¢\u0006\u0002\u0010\u0002J\b\u0010\u0003\u001a\u00020\u0004H&J\b\u0010\u0005\u001a\u00020\u0006H&J\b\u0010\u0007\u001a\u00020\bH&J\b\u0010\t\u001a\u00020\nH&¨\u0006\f"}, m607d2 = {"Lcom/glasssutdio/wear/database/GlassDatabase;", "Landroidx/room/RoomDatabase;", "()V", "glassAiChatDao", "Lcom/glasssutdio/wear/database/dao/GlassAiChatDao;", "glassAlbumDao", "Lcom/glasssutdio/wear/database/dao/GlassAlbumDao;", "glassDeviceSettingDao", "Lcom/glasssutdio/wear/database/dao/GlassDeviceSettingDao;", "translateDao", "Lcom/glasssutdio/wear/database/dao/TranslateDao;", "Companion", "app_release"}, m608k = 1, m609mv = {1, 9, 0}, m611xi = 48)
/* loaded from: classes.dex */
public abstract class GlassDatabase extends RoomDatabase {

    /* renamed from: Companion, reason: from kotlin metadata */
    public static final Companion INSTANCE = new Companion(null);
    private static volatile GlassDatabase INSTANCE;

    public abstract GlassAiChatDao glassAiChatDao();

    public abstract GlassAlbumDao glassAlbumDao();

    public abstract GlassDeviceSettingDao glassDeviceSettingDao();

    public abstract TranslateDao translateDao();

    /* compiled from: GlassDatabase.kt */
    @Metadata(m606d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u000e\u0010\u0005\u001a\u00020\u00042\u0006\u0010\u0006\u001a\u00020\u0007R\u0010\u0010\u0003\u001a\u0004\u0018\u00010\u0004X\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006\b"}, m607d2 = {"Lcom/glasssutdio/wear/database/GlassDatabase$Companion;", "", "()V", "INSTANCE", "Lcom/glasssutdio/wear/database/GlassDatabase;", "getDatabase", "context", "Landroid/content/Context;", "app_release"}, m608k = 1, m609mv = {1, 9, 0}, m611xi = 48)
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        public final GlassDatabase getDatabase(Context context) {
            GlassDatabase glassDatabase;
            Intrinsics.checkNotNullParameter(context, "context");
            GlassDatabase glassDatabase2 = GlassDatabase.INSTANCE;
            if (glassDatabase2 != null) {
                return glassDatabase2;
            }
            synchronized (this) {
                Context applicationContext = context.getApplicationContext();
                Intrinsics.checkNotNullExpressionValue(applicationContext, "getApplicationContext(...)");
                glassDatabase = (GlassDatabase) Room.databaseBuilder(applicationContext, GlassDatabase.class, "glass_w_database.db").fallbackToDestructiveMigration().build();
                Companion companion = GlassDatabase.INSTANCE;
                GlassDatabase.INSTANCE = glassDatabase;
            }
            return glassDatabase;
        }
    }
}
