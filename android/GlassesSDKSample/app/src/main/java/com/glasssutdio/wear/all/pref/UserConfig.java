package com.glasssutdio.wear.all.pref;

import com.glasssutdio.wear.all.utils.DateUtil;
import com.google.firebase.crashlytics.buildtools.ndk.internal.elf.EMachine;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.MutablePropertyReference1Impl;
import kotlin.jvm.internal.Reflection;
import kotlin.reflect.KProperty;

/* compiled from: UserConfig.kt */
@Metadata(m606d1 = {"\u00005\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0007\n\u0002\u0010\u000e\n\u0002\b\u0006\n\u0002\u0010\t\n\u0002\b\u0006\n\u0002\u0010\b\n\u0002\b\u0013\n\u0002\u0018\u0002\n\u0003\bÉ\u0001\u0018\u0000 ö\u00012\u00020\u0001:\u0002ö\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002R+\u0010\u0005\u001a\u00020\u00042\u0006\u0010\u0003\u001a\u00020\u00048F@FX\u0086\u008e\u0002¢\u0006\u0012\n\u0004\b\n\u0010\u000b\u001a\u0004\b\u0006\u0010\u0007\"\u0004\b\b\u0010\tR+\u0010\r\u001a\u00020\f2\u0006\u0010\u0003\u001a\u00020\f8F@FX\u0086\u008e\u0002¢\u0006\u0012\n\u0004\b\u0012\u0010\u000b\u001a\u0004\b\u000e\u0010\u000f\"\u0004\b\u0010\u0010\u0011R+\u0010\u0014\u001a\u00020\u00132\u0006\u0010\u0003\u001a\u00020\u00138F@FX\u0086\u008e\u0002¢\u0006\u0012\n\u0004\b\u0019\u0010\u000b\u001a\u0004\b\u0015\u0010\u0016\"\u0004\b\u0017\u0010\u0018R+\u0010\u001b\u001a\u00020\u001a2\u0006\u0010\u0003\u001a\u00020\u001a8F@FX\u0086\u008e\u0002¢\u0006\u0012\n\u0004\b \u0010\u000b\u001a\u0004\b\u001c\u0010\u001d\"\u0004\b\u001e\u0010\u001fR+\u0010!\u001a\u00020\u00042\u0006\u0010\u0003\u001a\u00020\u00048F@FX\u0086\u008e\u0002¢\u0006\u0012\n\u0004\b$\u0010\u000b\u001a\u0004\b\"\u0010\u0007\"\u0004\b#\u0010\tR+\u0010%\u001a\u00020\u00042\u0006\u0010\u0003\u001a\u00020\u00048F@FX\u0086\u008e\u0002¢\u0006\u0012\n\u0004\b(\u0010\u000b\u001a\u0004\b&\u0010\u0007\"\u0004\b'\u0010\tR+\u0010)\u001a\u00020\f2\u0006\u0010\u0003\u001a\u00020\f8F@FX\u0086\u008e\u0002¢\u0006\u0012\n\u0004\b,\u0010\u000b\u001a\u0004\b*\u0010\u000f\"\u0004\b+\u0010\u0011R\u000e\u0010-\u001a\u00020.X\u0082\u0004¢\u0006\u0002\n\u0000R+\u0010/\u001a\u00020\u00042\u0006\u0010\u0003\u001a\u00020\u00048F@FX\u0086\u008e\u0002¢\u0006\u0012\n\u0004\b2\u0010\u000b\u001a\u0004\b0\u0010\u0007\"\u0004\b1\u0010\tR+\u00103\u001a\u00020\f2\u0006\u0010\u0003\u001a\u00020\f8F@FX\u0086\u008e\u0002¢\u0006\u0012\n\u0004\b6\u0010\u000b\u001a\u0004\b4\u0010\u000f\"\u0004\b5\u0010\u0011R+\u00107\u001a\u00020\f2\u0006\u0010\u0003\u001a\u00020\f8F@FX\u0086\u008e\u0002¢\u0006\u0012\n\u0004\b:\u0010\u000b\u001a\u0004\b8\u0010\u000f\"\u0004\b9\u0010\u0011R+\u0010;\u001a\u00020\u00042\u0006\u0010\u0003\u001a\u00020\u00048F@FX\u0086\u008e\u0002¢\u0006\u0012\n\u0004\b>\u0010\u000b\u001a\u0004\b<\u0010\u0007\"\u0004\b=\u0010\tR+\u0010?\u001a\u00020\f2\u0006\u0010\u0003\u001a\u00020\f8F@FX\u0086\u008e\u0002¢\u0006\u0012\n\u0004\bB\u0010\u000b\u001a\u0004\b@\u0010\u000f\"\u0004\bA\u0010\u0011R+\u0010C\u001a\u00020\f2\u0006\u0010\u0003\u001a\u00020\f8F@FX\u0086\u008e\u0002¢\u0006\u0012\n\u0004\bF\u0010\u000b\u001a\u0004\bD\u0010\u000f\"\u0004\bE\u0010\u0011R+\u0010G\u001a\u00020\f2\u0006\u0010\u0003\u001a\u00020\f8F@FX\u0086\u008e\u0002¢\u0006\u0012\n\u0004\bJ\u0010\u000b\u001a\u0004\bH\u0010\u000f\"\u0004\bI\u0010\u0011R+\u0010K\u001a\u00020\f2\u0006\u0010\u0003\u001a\u00020\f8F@FX\u0086\u008e\u0002¢\u0006\u0012\n\u0004\bN\u0010\u000b\u001a\u0004\bL\u0010\u000f\"\u0004\bM\u0010\u0011R+\u0010O\u001a\u00020\f2\u0006\u0010\u0003\u001a\u00020\f8F@FX\u0086\u008e\u0002¢\u0006\u0012\n\u0004\bR\u0010\u000b\u001a\u0004\bP\u0010\u000f\"\u0004\bQ\u0010\u0011R+\u0010S\u001a\u00020\f2\u0006\u0010\u0003\u001a\u00020\f8F@FX\u0086\u008e\u0002¢\u0006\u0012\n\u0004\bV\u0010\u000b\u001a\u0004\bT\u0010\u000f\"\u0004\bU\u0010\u0011R+\u0010W\u001a\u00020\f2\u0006\u0010\u0003\u001a\u00020\f8F@FX\u0086\u008e\u0002¢\u0006\u0012\n\u0004\bZ\u0010\u000b\u001a\u0004\bX\u0010\u000f\"\u0004\bY\u0010\u0011R+\u0010[\u001a\u00020\f2\u0006\u0010\u0003\u001a\u00020\f8F@FX\u0086\u008e\u0002¢\u0006\u0012\n\u0004\b^\u0010\u000b\u001a\u0004\b\\\u0010\u000f\"\u0004\b]\u0010\u0011R+\u0010_\u001a\u00020\u00042\u0006\u0010\u0003\u001a\u00020\u00048F@FX\u0086\u008e\u0002¢\u0006\u0012\n\u0004\bb\u0010\u000b\u001a\u0004\b`\u0010\u0007\"\u0004\ba\u0010\tR+\u0010c\u001a\u00020\u001a2\u0006\u0010\u0003\u001a\u00020\u001a8F@FX\u0086\u008e\u0002¢\u0006\u0012\n\u0004\bf\u0010\u000b\u001a\u0004\bd\u0010\u001d\"\u0004\be\u0010\u001fR+\u0010g\u001a\u00020\u00042\u0006\u0010\u0003\u001a\u00020\u00048F@FX\u0086\u008e\u0002¢\u0006\u0012\n\u0004\bj\u0010\u000b\u001a\u0004\bh\u0010\u0007\"\u0004\bi\u0010\tR+\u0010k\u001a\u00020\f2\u0006\u0010\u0003\u001a\u00020\f8F@FX\u0086\u008e\u0002¢\u0006\u0012\n\u0004\bn\u0010\u000b\u001a\u0004\bl\u0010\u000f\"\u0004\bm\u0010\u0011R+\u0010o\u001a\u00020\f2\u0006\u0010\u0003\u001a\u00020\f8F@FX\u0086\u008e\u0002¢\u0006\u0012\n\u0004\br\u0010\u000b\u001a\u0004\bp\u0010\u000f\"\u0004\bq\u0010\u0011R+\u0010s\u001a\u00020\u00042\u0006\u0010\u0003\u001a\u00020\u00048F@FX\u0086\u008e\u0002¢\u0006\u0012\n\u0004\bu\u0010\u000b\u001a\u0004\bs\u0010\u0007\"\u0004\bt\u0010\tR+\u0010v\u001a\u00020\u00042\u0006\u0010\u0003\u001a\u00020\u00048F@FX\u0086\u008e\u0002¢\u0006\u0012\n\u0004\bx\u0010\u000b\u001a\u0004\bv\u0010\u0007\"\u0004\bw\u0010\tR+\u0010y\u001a\u00020\u00042\u0006\u0010\u0003\u001a\u00020\u00048F@FX\u0086\u008e\u0002¢\u0006\u0012\n\u0004\b{\u0010\u000b\u001a\u0004\by\u0010\u0007\"\u0004\bz\u0010\tR+\u0010|\u001a\u00020\u00042\u0006\u0010\u0003\u001a\u00020\u00048F@FX\u0086\u008e\u0002¢\u0006\u0012\n\u0004\b~\u0010\u000b\u001a\u0004\b|\u0010\u0007\"\u0004\b}\u0010\tR-\u0010\u007f\u001a\u00020\u00042\u0006\u0010\u0003\u001a\u00020\u00048F@FX\u0086\u008e\u0002¢\u0006\u0014\n\u0005\b\u0081\u0001\u0010\u000b\u001a\u0004\b\u007f\u0010\u0007\"\u0005\b\u0080\u0001\u0010\tR/\u0010\u0082\u0001\u001a\u00020\f2\u0006\u0010\u0003\u001a\u00020\f8F@FX\u0086\u008e\u0002¢\u0006\u0015\n\u0005\b\u0085\u0001\u0010\u000b\u001a\u0005\b\u0083\u0001\u0010\u000f\"\u0005\b\u0084\u0001\u0010\u0011R/\u0010\u0086\u0001\u001a\u00020\f2\u0006\u0010\u0003\u001a\u00020\f8F@FX\u0086\u008e\u0002¢\u0006\u0015\n\u0005\b\u0089\u0001\u0010\u000b\u001a\u0005\b\u0087\u0001\u0010\u000f\"\u0005\b\u0088\u0001\u0010\u0011R/\u0010\u008a\u0001\u001a\u00020\f2\u0006\u0010\u0003\u001a\u00020\f8F@FX\u0086\u008e\u0002¢\u0006\u0015\n\u0005\b\u008d\u0001\u0010\u000b\u001a\u0005\b\u008b\u0001\u0010\u000f\"\u0005\b\u008c\u0001\u0010\u0011R/\u0010\u008e\u0001\u001a\u00020\u00132\u0006\u0010\u0003\u001a\u00020\u00138F@FX\u0086\u008e\u0002¢\u0006\u0015\n\u0005\b\u0091\u0001\u0010\u000b\u001a\u0005\b\u008f\u0001\u0010\u0016\"\u0005\b\u0090\u0001\u0010\u0018R/\u0010\u0092\u0001\u001a\u00020\u001a2\u0006\u0010\u0003\u001a\u00020\u001a8F@FX\u0086\u008e\u0002¢\u0006\u0015\n\u0005\b\u0095\u0001\u0010\u000b\u001a\u0005\b\u0093\u0001\u0010\u001d\"\u0005\b\u0094\u0001\u0010\u001fR/\u0010\u0096\u0001\u001a\u00020\u00132\u0006\u0010\u0003\u001a\u00020\u00138F@FX\u0086\u008e\u0002¢\u0006\u0015\n\u0005\b\u0099\u0001\u0010\u000b\u001a\u0005\b\u0097\u0001\u0010\u0016\"\u0005\b\u0098\u0001\u0010\u0018R/\u0010\u009a\u0001\u001a\u00020\u00132\u0006\u0010\u0003\u001a\u00020\u00138F@FX\u0086\u008e\u0002¢\u0006\u0015\n\u0005\b\u009d\u0001\u0010\u000b\u001a\u0005\b\u009b\u0001\u0010\u0016\"\u0005\b\u009c\u0001\u0010\u0018R/\u0010\u009e\u0001\u001a\u00020\u00042\u0006\u0010\u0003\u001a\u00020\u00048F@FX\u0086\u008e\u0002¢\u0006\u0015\n\u0005\b¡\u0001\u0010\u000b\u001a\u0005\b\u009f\u0001\u0010\u0007\"\u0005\b \u0001\u0010\tR/\u0010¢\u0001\u001a\u00020\u00042\u0006\u0010\u0003\u001a\u00020\u00048F@FX\u0086\u008e\u0002¢\u0006\u0015\n\u0005\b¥\u0001\u0010\u000b\u001a\u0005\b£\u0001\u0010\u0007\"\u0005\b¤\u0001\u0010\tR/\u0010¦\u0001\u001a\u00020\u00042\u0006\u0010\u0003\u001a\u00020\u00048F@FX\u0086\u008e\u0002¢\u0006\u0015\n\u0005\b©\u0001\u0010\u000b\u001a\u0005\b§\u0001\u0010\u0007\"\u0005\b¨\u0001\u0010\tR/\u0010ª\u0001\u001a\u00020\u00042\u0006\u0010\u0003\u001a\u00020\u00048F@FX\u0086\u008e\u0002¢\u0006\u0015\n\u0005\b\u00ad\u0001\u0010\u000b\u001a\u0005\b«\u0001\u0010\u0007\"\u0005\b¬\u0001\u0010\tR/\u0010®\u0001\u001a\u00020\u00042\u0006\u0010\u0003\u001a\u00020\u00048F@FX\u0086\u008e\u0002¢\u0006\u0015\n\u0005\b±\u0001\u0010\u000b\u001a\u0005\b¯\u0001\u0010\u0007\"\u0005\b°\u0001\u0010\tR/\u0010²\u0001\u001a\u00020\u001a2\u0006\u0010\u0003\u001a\u00020\u001a8F@FX\u0086\u008e\u0002¢\u0006\u0015\n\u0005\bµ\u0001\u0010\u000b\u001a\u0005\b³\u0001\u0010\u001d\"\u0005\b´\u0001\u0010\u001fR/\u0010¶\u0001\u001a\u00020\u001a2\u0006\u0010\u0003\u001a\u00020\u001a8F@FX\u0086\u008e\u0002¢\u0006\u0015\n\u0005\b¹\u0001\u0010\u000b\u001a\u0005\b·\u0001\u0010\u001d\"\u0005\b¸\u0001\u0010\u001fR/\u0010º\u0001\u001a\u00020\f2\u0006\u0010\u0003\u001a\u00020\f8F@FX\u0086\u008e\u0002¢\u0006\u0015\n\u0005\b½\u0001\u0010\u000b\u001a\u0005\b»\u0001\u0010\u000f\"\u0005\b¼\u0001\u0010\u0011R/\u0010¾\u0001\u001a\u00020\u00042\u0006\u0010\u0003\u001a\u00020\u00048F@FX\u0086\u008e\u0002¢\u0006\u0015\n\u0005\bÁ\u0001\u0010\u000b\u001a\u0005\b¿\u0001\u0010\u0007\"\u0005\bÀ\u0001\u0010\tR/\u0010Â\u0001\u001a\u00020\u00042\u0006\u0010\u0003\u001a\u00020\u00048F@FX\u0086\u008e\u0002¢\u0006\u0015\n\u0005\bÅ\u0001\u0010\u000b\u001a\u0005\bÃ\u0001\u0010\u0007\"\u0005\bÄ\u0001\u0010\tR/\u0010Æ\u0001\u001a\u00020\u00042\u0006\u0010\u0003\u001a\u00020\u00048F@FX\u0086\u008e\u0002¢\u0006\u0015\n\u0005\bÉ\u0001\u0010\u000b\u001a\u0005\bÇ\u0001\u0010\u0007\"\u0005\bÈ\u0001\u0010\tR/\u0010Ê\u0001\u001a\u00020\u001a2\u0006\u0010\u0003\u001a\u00020\u001a8F@FX\u0086\u008e\u0002¢\u0006\u0015\n\u0005\bÍ\u0001\u0010\u000b\u001a\u0005\bË\u0001\u0010\u001d\"\u0005\bÌ\u0001\u0010\u001fR/\u0010Î\u0001\u001a\u00020\f2\u0006\u0010\u0003\u001a\u00020\f8F@FX\u0086\u008e\u0002¢\u0006\u0015\n\u0005\bÑ\u0001\u0010\u000b\u001a\u0005\bÏ\u0001\u0010\u000f\"\u0005\bÐ\u0001\u0010\u0011R/\u0010Ò\u0001\u001a\u00020\f2\u0006\u0010\u0003\u001a\u00020\f8F@FX\u0086\u008e\u0002¢\u0006\u0015\n\u0005\bÕ\u0001\u0010\u000b\u001a\u0005\bÓ\u0001\u0010\u000f\"\u0005\bÔ\u0001\u0010\u0011R/\u0010Ö\u0001\u001a\u00020\u00132\u0006\u0010\u0003\u001a\u00020\u00138F@FX\u0086\u008e\u0002¢\u0006\u0015\n\u0005\bÙ\u0001\u0010\u000b\u001a\u0005\b×\u0001\u0010\u0016\"\u0005\bØ\u0001\u0010\u0018R/\u0010Ú\u0001\u001a\u00020\f2\u0006\u0010\u0003\u001a\u00020\f8F@FX\u0086\u008e\u0002¢\u0006\u0015\n\u0005\bÝ\u0001\u0010\u000b\u001a\u0005\bÛ\u0001\u0010\u000f\"\u0005\bÜ\u0001\u0010\u0011R/\u0010Þ\u0001\u001a\u00020\u00042\u0006\u0010\u0003\u001a\u00020\u00048F@FX\u0086\u008e\u0002¢\u0006\u0015\n\u0005\bá\u0001\u0010\u000b\u001a\u0005\bß\u0001\u0010\u0007\"\u0005\bà\u0001\u0010\tR/\u0010â\u0001\u001a\u00020\f2\u0006\u0010\u0003\u001a\u00020\f8F@FX\u0086\u008e\u0002¢\u0006\u0015\n\u0005\bå\u0001\u0010\u000b\u001a\u0005\bã\u0001\u0010\u000f\"\u0005\bä\u0001\u0010\u0011R/\u0010æ\u0001\u001a\u00020\f2\u0006\u0010\u0003\u001a\u00020\f8F@FX\u0086\u008e\u0002¢\u0006\u0015\n\u0005\bé\u0001\u0010\u000b\u001a\u0005\bç\u0001\u0010\u000f\"\u0005\bè\u0001\u0010\u0011R/\u0010ê\u0001\u001a\u00020\u001a2\u0006\u0010\u0003\u001a\u00020\u001a8F@FX\u0086\u008e\u0002¢\u0006\u0015\n\u0005\bí\u0001\u0010\u000b\u001a\u0005\bë\u0001\u0010\u001d\"\u0005\bì\u0001\u0010\u001fR/\u0010î\u0001\u001a\u00020\u00042\u0006\u0010\u0003\u001a\u00020\u00048F@FX\u0086\u008e\u0002¢\u0006\u0015\n\u0005\bñ\u0001\u0010\u000b\u001a\u0005\bï\u0001\u0010\u0007\"\u0005\bð\u0001\u0010\tR/\u0010ò\u0001\u001a\u00020\f2\u0006\u0010\u0003\u001a\u00020\f8F@FX\u0086\u008e\u0002¢\u0006\u0015\n\u0005\bõ\u0001\u0010\u000b\u001a\u0005\bó\u0001\u0010\u000f\"\u0005\bô\u0001\u0010\u0011¨\u0006÷\u0001"}, m607d2 = {"Lcom/glasssutdio/wear/all/pref/UserConfig;", "", "()V", "<set-?>", "", "aiIsSystemLanguage", "getAiIsSystemLanguage", "()Z", "setAiIsSystemLanguage", "(Z)V", "aiIsSystemLanguage$delegate", "Lcom/glasssutdio/wear/all/pref/mmkvDelegate;", "", "aiLanguageCode", "getAiLanguageCode", "()Ljava/lang/String;", "setAiLanguageCode", "(Ljava/lang/String;)V", "aiLanguageCode$delegate", "", "appLastUserTime", "getAppLastUserTime", "()J", "setAppLastUserTime", "(J)V", "appLastUserTime$delegate", "", "battery", "getBattery", "()I", "setBattery", "(I)V", "battery$delegate", "camera8Mp", "getCamera8Mp", "setCamera8Mp", "camera8Mp$delegate", "changing", "getChanging", "setChanging", "changing$delegate", "classicBluetoothMac", "getClassicBluetoothMac", "setClassicBluetoothMac", "classicBluetoothMac$delegate", "config", "Lcom/glasssutdio/wear/all/pref/MMKVConfig;", "debug", "getDebug", "setDebug", "debug$delegate", "deviceAddress", "getDeviceAddress", "setDeviceAddress", "deviceAddress$delegate", "deviceAddressNoClear", "getDeviceAddressNoClear", "setDeviceAddressNoClear", "deviceAddressNoClear$delegate", "deviceBind", "getDeviceBind", "setDeviceBind", "deviceBind$delegate", "deviceName", "getDeviceName", "setDeviceName", "deviceName$delegate", "deviceNameNoClear", "getDeviceNameNoClear", "setDeviceNameNoClear", "deviceNameNoClear$delegate", "fmVersion", "getFmVersion", "setFmVersion", "fmVersion$delegate", "fmVersionWifi", "getFmVersionWifi", "setFmVersionWifi", "fmVersionWifi$delegate", "glassDeviceWifiIP", "getGlassDeviceWifiIP", "setGlassDeviceWifiIP", "glassDeviceWifiIP$delegate", "glassDeviceWifiMac", "getGlassDeviceWifiMac", "setGlassDeviceWifiMac", "glassDeviceWifiMac$delegate", "glassDeviceWifiName", "getGlassDeviceWifiName", "setGlassDeviceWifiName", "glassDeviceWifiName$delegate", "glassDeviceWifiPassword", "getGlassDeviceWifiPassword", "setGlassDeviceWifiPassword", "glassDeviceWifiPassword$delegate", "glassesLogs", "getGlassesLogs", "setGlassesLogs", "glassesLogs$delegate", "glassesModel", "getGlassesModel", "setGlassesModel", "glassesModel$delegate", "hasNewVersion", "getHasNewVersion", "setHasNewVersion", "hasNewVersion$delegate", "hwVersion", "getHwVersion", "setHwVersion", "hwVersion$delegate", "hwVersionWifi", "getHwVersionWifi", "setHwVersionWifi", "hwVersionWifi$delegate", "isShowedAiGuided", "setShowedAiGuided", "isShowedAiGuided$delegate", "isShowedBatteryLow", "setShowedBatteryLow", "isShowedBatteryLow$delegate", "isShowedDeviceGuided", "setShowedDeviceGuided", "isShowedDeviceGuided$delegate", "isShowedDeviceGuidedNew", "setShowedDeviceGuidedNew", "isShowedDeviceGuidedNew$delegate", "isShowedMemoryLow", "setShowedMemoryLow", "isShowedMemoryLow$delegate", "languageJson", "getLanguageJson", "setLanguageJson", "languageJson$delegate", "lastLoginAccount", "getLastLoginAccount", "setLastLoginAccount", "lastLoginAccount$delegate", "lastLoginPwd", "getLastLoginPwd", "setLastLoginPwd", "lastLoginPwd$delegate", "lastLoginTimeStamp", "getLastLoginTimeStamp", "setLastLoginTimeStamp", "lastLoginTimeStamp$delegate", "lastLoginType", "getLastLoginType", "setLastLoginType", "lastLoginType$delegate", "lastQuestionTime", "getLastQuestionTime", "setLastQuestionTime", "lastQuestionTime$delegate", "lastShowAppUpgradeTimeStamp", "getLastShowAppUpgradeTimeStamp", "setLastShowAppUpgradeTimeStamp", "lastShowAppUpgradeTimeStamp$delegate", "lowBattery", "getLowBattery", "setLowBattery", "lowBattery$delegate", "needShowLogin", "getNeedShowLogin", "setNeedShowLogin", "needShowLogin$delegate", "otaDown", "getOtaDown", "setOtaDown", "otaDown$delegate", "pictureAutoSave", "getPictureAutoSave", "setPictureAutoSave", "pictureAutoSave$delegate", "pictureWatermark", "getPictureWatermark", "setPictureWatermark", "pictureWatermark$delegate", "recordAudioDuration", "getRecordAudioDuration", "setRecordAudioDuration", "recordAudioDuration$delegate", "recordVideoDuration", "getRecordVideoDuration", "setRecordVideoDuration", "recordVideoDuration$delegate", "scanKeyFilter", "getScanKeyFilter", "setScanKeyFilter", "scanKeyFilter$delegate", "supportTranslate", "getSupportTranslate", "setSupportTranslate", "supportTranslate$delegate", "supportVolumeControl", "getSupportVolumeControl", "setSupportVolumeControl", "supportVolumeControl$delegate", "supportWear", "getSupportWear", "setSupportWear", "supportWear$delegate", "thumbnailSize", "getThumbnailSize", "setThumbnailSize", "thumbnailSize$delegate", "translateFromDefault", "getTranslateFromDefault", "setTranslateFromDefault", "translateFromDefault$delegate", "translateToDefault", "getTranslateToDefault", "setTranslateToDefault", "translateToDefault$delegate", "uid", "getUid", "setUid", "uid$delegate", "uniqueIdHw", "getUniqueIdHw", "setUniqueIdHw", "uniqueIdHw$delegate", "useGyro", "getUseGyro", "setUseGyro", "useGyro$delegate", "userJson", "getUserJson", "setUserJson", "userJson$delegate", "userToken", "getUserToken", "setUserToken", "userToken$delegate", "videoDuration", "getVideoDuration", "setVideoDuration", "videoDuration$delegate", "videoIsLandscape", "getVideoIsLandscape", "setVideoIsLandscape", "videoIsLandscape$delegate", "volumeControl", "getVolumeControl", "setVolumeControl", "volumeControl$delegate", "Companion", "app_release"}, m608k = 1, m609mv = {1, 9, 0}, m611xi = 48)
/* loaded from: classes.dex */
public final class UserConfig {
    static final /* synthetic */ KProperty<Object>[] $$delegatedProperties = {Reflection.mutableProperty1(new MutablePropertyReference1Impl(UserConfig.class, "userToken", "getUserToken()Ljava/lang/String;", 0)), Reflection.mutableProperty1(new MutablePropertyReference1Impl(UserConfig.class, "uid", "getUid()J", 0)), Reflection.mutableProperty1(new MutablePropertyReference1Impl(UserConfig.class, "deviceAddress", "getDeviceAddress()Ljava/lang/String;", 0)), Reflection.mutableProperty1(new MutablePropertyReference1Impl(UserConfig.class, "deviceAddressNoClear", "getDeviceAddressNoClear()Ljava/lang/String;", 0)), Reflection.mutableProperty1(new MutablePropertyReference1Impl(UserConfig.class, "deviceName", "getDeviceName()Ljava/lang/String;", 0)), Reflection.mutableProperty1(new MutablePropertyReference1Impl(UserConfig.class, "deviceNameNoClear", "getDeviceNameNoClear()Ljava/lang/String;", 0)), Reflection.mutableProperty1(new MutablePropertyReference1Impl(UserConfig.class, "deviceBind", "getDeviceBind()Z", 0)), Reflection.mutableProperty1(new MutablePropertyReference1Impl(UserConfig.class, "videoDuration", "getVideoDuration()I", 0)), Reflection.mutableProperty1(new MutablePropertyReference1Impl(UserConfig.class, "battery", "getBattery()I", 0)), Reflection.mutableProperty1(new MutablePropertyReference1Impl(UserConfig.class, "lowBattery", "getLowBattery()Z", 0)), Reflection.mutableProperty1(new MutablePropertyReference1Impl(UserConfig.class, "changing", "getChanging()Z", 0)), Reflection.mutableProperty1(new MutablePropertyReference1Impl(UserConfig.class, "fmVersion", "getFmVersion()Ljava/lang/String;", 0)), Reflection.mutableProperty1(new MutablePropertyReference1Impl(UserConfig.class, "hwVersion", "getHwVersion()Ljava/lang/String;", 0)), Reflection.mutableProperty1(new MutablePropertyReference1Impl(UserConfig.class, "fmVersionWifi", "getFmVersionWifi()Ljava/lang/String;", 0)), Reflection.mutableProperty1(new MutablePropertyReference1Impl(UserConfig.class, "hwVersionWifi", "getHwVersionWifi()Ljava/lang/String;", 0)), Reflection.mutableProperty1(new MutablePropertyReference1Impl(UserConfig.class, "classicBluetoothMac", "getClassicBluetoothMac()Ljava/lang/String;", 0)), Reflection.mutableProperty1(new MutablePropertyReference1Impl(UserConfig.class, "scanKeyFilter", "getScanKeyFilter()Ljava/lang/String;", 0)), Reflection.mutableProperty1(new MutablePropertyReference1Impl(UserConfig.class, "glassDeviceWifiIP", "getGlassDeviceWifiIP()Ljava/lang/String;", 0)), Reflection.mutableProperty1(new MutablePropertyReference1Impl(UserConfig.class, "glassDeviceWifiName", "getGlassDeviceWifiName()Ljava/lang/String;", 0)), Reflection.mutableProperty1(new MutablePropertyReference1Impl(UserConfig.class, "glassDeviceWifiPassword", "getGlassDeviceWifiPassword()Ljava/lang/String;", 0)), Reflection.mutableProperty1(new MutablePropertyReference1Impl(UserConfig.class, "glassDeviceWifiMac", "getGlassDeviceWifiMac()Ljava/lang/String;", 0)), Reflection.mutableProperty1(new MutablePropertyReference1Impl(UserConfig.class, "lastLoginTimeStamp", "getLastLoginTimeStamp()J", 0)), Reflection.mutableProperty1(new MutablePropertyReference1Impl(UserConfig.class, "needShowLogin", "getNeedShowLogin()Z", 0)), Reflection.mutableProperty1(new MutablePropertyReference1Impl(UserConfig.class, "lastLoginAccount", "getLastLoginAccount()Ljava/lang/String;", 0)), Reflection.mutableProperty1(new MutablePropertyReference1Impl(UserConfig.class, "lastLoginPwd", "getLastLoginPwd()Ljava/lang/String;", 0)), Reflection.mutableProperty1(new MutablePropertyReference1Impl(UserConfig.class, "lastLoginType", "getLastLoginType()I", 0)), Reflection.mutableProperty1(new MutablePropertyReference1Impl(UserConfig.class, "userJson", "getUserJson()Ljava/lang/String;", 0)), Reflection.mutableProperty1(new MutablePropertyReference1Impl(UserConfig.class, "lastShowAppUpgradeTimeStamp", "getLastShowAppUpgradeTimeStamp()J", 0)), Reflection.mutableProperty1(new MutablePropertyReference1Impl(UserConfig.class, "hasNewVersion", "getHasNewVersion()Z", 0)), Reflection.mutableProperty1(new MutablePropertyReference1Impl(UserConfig.class, "pictureWatermark", "getPictureWatermark()Z", 0)), Reflection.mutableProperty1(new MutablePropertyReference1Impl(UserConfig.class, "pictureAutoSave", "getPictureAutoSave()Z", 0)), Reflection.mutableProperty1(new MutablePropertyReference1Impl(UserConfig.class, "recordVideoDuration", "getRecordVideoDuration()I", 0)), Reflection.mutableProperty1(new MutablePropertyReference1Impl(UserConfig.class, "recordAudioDuration", "getRecordAudioDuration()I", 0)), Reflection.mutableProperty1(new MutablePropertyReference1Impl(UserConfig.class, "videoIsLandscape", "getVideoIsLandscape()Z", 0)), Reflection.mutableProperty1(new MutablePropertyReference1Impl(UserConfig.class, "isShowedAiGuided", "isShowedAiGuided()Z", 0)), Reflection.mutableProperty1(new MutablePropertyReference1Impl(UserConfig.class, "isShowedDeviceGuided", "isShowedDeviceGuided()Z", 0)), Reflection.mutableProperty1(new MutablePropertyReference1Impl(UserConfig.class, "isShowedDeviceGuidedNew", "isShowedDeviceGuidedNew()Z", 0)), Reflection.mutableProperty1(new MutablePropertyReference1Impl(UserConfig.class, "isShowedBatteryLow", "isShowedBatteryLow()Z", 0)), Reflection.mutableProperty1(new MutablePropertyReference1Impl(UserConfig.class, "isShowedMemoryLow", "isShowedMemoryLow()Z", 0)), Reflection.mutableProperty1(new MutablePropertyReference1Impl(UserConfig.class, "debug", "getDebug()Z", 0)), Reflection.mutableProperty1(new MutablePropertyReference1Impl(UserConfig.class, "glassesLogs", "getGlassesLogs()Z", 0)), Reflection.mutableProperty1(new MutablePropertyReference1Impl(UserConfig.class, "otaDown", "getOtaDown()Z", 0)), Reflection.mutableProperty1(new MutablePropertyReference1Impl(UserConfig.class, "appLastUserTime", "getAppLastUserTime()J", 0)), Reflection.mutableProperty1(new MutablePropertyReference1Impl(UserConfig.class, "supportTranslate", "getSupportTranslate()Z", 0)), Reflection.mutableProperty1(new MutablePropertyReference1Impl(UserConfig.class, "supportWear", "getSupportWear()Z", 0)), Reflection.mutableProperty1(new MutablePropertyReference1Impl(UserConfig.class, "supportVolumeControl", "getSupportVolumeControl()Z", 0)), Reflection.mutableProperty1(new MutablePropertyReference1Impl(UserConfig.class, "glassesModel", "getGlassesModel()I", 0)), Reflection.mutableProperty1(new MutablePropertyReference1Impl(UserConfig.class, "translateFromDefault", "getTranslateFromDefault()Ljava/lang/String;", 0)), Reflection.mutableProperty1(new MutablePropertyReference1Impl(UserConfig.class, "translateToDefault", "getTranslateToDefault()Ljava/lang/String;", 0)), Reflection.mutableProperty1(new MutablePropertyReference1Impl(UserConfig.class, "uniqueIdHw", "getUniqueIdHw()Ljava/lang/String;", 0)), Reflection.mutableProperty1(new MutablePropertyReference1Impl(UserConfig.class, "lastQuestionTime", "getLastQuestionTime()J", 0)), Reflection.mutableProperty1(new MutablePropertyReference1Impl(UserConfig.class, "languageJson", "getLanguageJson()Ljava/lang/String;", 0)), Reflection.mutableProperty1(new MutablePropertyReference1Impl(UserConfig.class, "aiLanguageCode", "getAiLanguageCode()Ljava/lang/String;", 0)), Reflection.mutableProperty1(new MutablePropertyReference1Impl(UserConfig.class, "aiIsSystemLanguage", "getAiIsSystemLanguage()Z", 0)), Reflection.mutableProperty1(new MutablePropertyReference1Impl(UserConfig.class, "useGyro", "getUseGyro()Z", 0)), Reflection.mutableProperty1(new MutablePropertyReference1Impl(UserConfig.class, "thumbnailSize", "getThumbnailSize()I", 0)), Reflection.mutableProperty1(new MutablePropertyReference1Impl(UserConfig.class, "camera8Mp", "getCamera8Mp()Z", 0)), Reflection.mutableProperty1(new MutablePropertyReference1Impl(UserConfig.class, "volumeControl", "getVolumeControl()Ljava/lang/String;", 0))};

    /* renamed from: Companion, reason: from kotlin metadata */
    public static final Companion INSTANCE = new Companion(null);
    private static UserConfig instance;

    /* renamed from: aiIsSystemLanguage$delegate, reason: from kotlin metadata */
    private final mmkvDelegate aiIsSystemLanguage;

    /* renamed from: aiLanguageCode$delegate, reason: from kotlin metadata */
    private final mmkvDelegate aiLanguageCode;

    /* renamed from: appLastUserTime$delegate, reason: from kotlin metadata */
    private final mmkvDelegate appLastUserTime;

    /* renamed from: battery$delegate, reason: from kotlin metadata */
    private final mmkvDelegate battery;

    /* renamed from: camera8Mp$delegate, reason: from kotlin metadata */
    private final mmkvDelegate camera8Mp;

    /* renamed from: changing$delegate, reason: from kotlin metadata */
    private final mmkvDelegate changing;

    /* renamed from: classicBluetoothMac$delegate, reason: from kotlin metadata */
    private final mmkvDelegate classicBluetoothMac;
    private final MMKVConfig config;

    /* renamed from: debug$delegate, reason: from kotlin metadata */
    private final mmkvDelegate debug;

    /* renamed from: deviceAddress$delegate, reason: from kotlin metadata */
    private final mmkvDelegate deviceAddress;

    /* renamed from: deviceAddressNoClear$delegate, reason: from kotlin metadata */
    private final mmkvDelegate deviceAddressNoClear;

    /* renamed from: deviceBind$delegate, reason: from kotlin metadata */
    private final mmkvDelegate deviceBind;

    /* renamed from: deviceName$delegate, reason: from kotlin metadata */
    private final mmkvDelegate deviceName;

    /* renamed from: deviceNameNoClear$delegate, reason: from kotlin metadata */
    private final mmkvDelegate deviceNameNoClear;

    /* renamed from: fmVersion$delegate, reason: from kotlin metadata */
    private final mmkvDelegate fmVersion;

    /* renamed from: fmVersionWifi$delegate, reason: from kotlin metadata */
    private final mmkvDelegate fmVersionWifi;

    /* renamed from: glassDeviceWifiIP$delegate, reason: from kotlin metadata */
    private final mmkvDelegate glassDeviceWifiIP;

    /* renamed from: glassDeviceWifiMac$delegate, reason: from kotlin metadata */
    private final mmkvDelegate glassDeviceWifiMac;

    /* renamed from: glassDeviceWifiName$delegate, reason: from kotlin metadata */
    private final mmkvDelegate glassDeviceWifiName;

    /* renamed from: glassDeviceWifiPassword$delegate, reason: from kotlin metadata */
    private final mmkvDelegate glassDeviceWifiPassword;

    /* renamed from: glassesLogs$delegate, reason: from kotlin metadata */
    private final mmkvDelegate glassesLogs;

    /* renamed from: glassesModel$delegate, reason: from kotlin metadata */
    private final mmkvDelegate glassesModel;

    /* renamed from: hasNewVersion$delegate, reason: from kotlin metadata */
    private final mmkvDelegate hasNewVersion;

    /* renamed from: hwVersion$delegate, reason: from kotlin metadata */
    private final mmkvDelegate hwVersion;

    /* renamed from: hwVersionWifi$delegate, reason: from kotlin metadata */
    private final mmkvDelegate hwVersionWifi;

    /* renamed from: isShowedAiGuided$delegate, reason: from kotlin metadata */
    private final mmkvDelegate isShowedAiGuided;

    /* renamed from: isShowedBatteryLow$delegate, reason: from kotlin metadata */
    private final mmkvDelegate isShowedBatteryLow;

    /* renamed from: isShowedDeviceGuided$delegate, reason: from kotlin metadata */
    private final mmkvDelegate isShowedDeviceGuided;

    /* renamed from: isShowedDeviceGuidedNew$delegate, reason: from kotlin metadata */
    private final mmkvDelegate isShowedDeviceGuidedNew;

    /* renamed from: isShowedMemoryLow$delegate, reason: from kotlin metadata */
    private final mmkvDelegate isShowedMemoryLow;

    /* renamed from: languageJson$delegate, reason: from kotlin metadata */
    private final mmkvDelegate languageJson;

    /* renamed from: lastLoginAccount$delegate, reason: from kotlin metadata */
    private final mmkvDelegate lastLoginAccount;

    /* renamed from: lastLoginPwd$delegate, reason: from kotlin metadata */
    private final mmkvDelegate lastLoginPwd;

    /* renamed from: lastLoginTimeStamp$delegate, reason: from kotlin metadata */
    private final mmkvDelegate lastLoginTimeStamp;

    /* renamed from: lastLoginType$delegate, reason: from kotlin metadata */
    private final mmkvDelegate lastLoginType;

    /* renamed from: lastQuestionTime$delegate, reason: from kotlin metadata */
    private final mmkvDelegate lastQuestionTime;

    /* renamed from: lastShowAppUpgradeTimeStamp$delegate, reason: from kotlin metadata */
    private final mmkvDelegate lastShowAppUpgradeTimeStamp;

    /* renamed from: lowBattery$delegate, reason: from kotlin metadata */
    private final mmkvDelegate lowBattery;

    /* renamed from: needShowLogin$delegate, reason: from kotlin metadata */
    private final mmkvDelegate needShowLogin;

    /* renamed from: otaDown$delegate, reason: from kotlin metadata */
    private final mmkvDelegate otaDown;

    /* renamed from: pictureAutoSave$delegate, reason: from kotlin metadata */
    private final mmkvDelegate pictureAutoSave;

    /* renamed from: pictureWatermark$delegate, reason: from kotlin metadata */
    private final mmkvDelegate pictureWatermark;

    /* renamed from: recordAudioDuration$delegate, reason: from kotlin metadata */
    private final mmkvDelegate recordAudioDuration;

    /* renamed from: recordVideoDuration$delegate, reason: from kotlin metadata */
    private final mmkvDelegate recordVideoDuration;

    /* renamed from: scanKeyFilter$delegate, reason: from kotlin metadata */
    private final mmkvDelegate scanKeyFilter;

    /* renamed from: supportTranslate$delegate, reason: from kotlin metadata */
    private final mmkvDelegate supportTranslate;

    /* renamed from: supportVolumeControl$delegate, reason: from kotlin metadata */
    private final mmkvDelegate supportVolumeControl;

    /* renamed from: supportWear$delegate, reason: from kotlin metadata */
    private final mmkvDelegate supportWear;

    /* renamed from: thumbnailSize$delegate, reason: from kotlin metadata */
    private final mmkvDelegate thumbnailSize;

    /* renamed from: translateFromDefault$delegate, reason: from kotlin metadata */
    private final mmkvDelegate translateFromDefault;

    /* renamed from: translateToDefault$delegate, reason: from kotlin metadata */
    private final mmkvDelegate translateToDefault;

    /* renamed from: uid$delegate, reason: from kotlin metadata */
    private final mmkvDelegate uid;

    /* renamed from: uniqueIdHw$delegate, reason: from kotlin metadata */
    private final mmkvDelegate uniqueIdHw;

    /* renamed from: useGyro$delegate, reason: from kotlin metadata */
    private final mmkvDelegate useGyro;

    /* renamed from: userJson$delegate, reason: from kotlin metadata */
    private final mmkvDelegate userJson;

    /* renamed from: userToken$delegate, reason: from kotlin metadata */
    private final mmkvDelegate userToken;

    /* renamed from: videoDuration$delegate, reason: from kotlin metadata */
    private final mmkvDelegate videoDuration;

    /* renamed from: videoIsLandscape$delegate, reason: from kotlin metadata */
    private final mmkvDelegate videoIsLandscape;

    /* renamed from: volumeControl$delegate, reason: from kotlin metadata */
    private final mmkvDelegate volumeControl;

    public /* synthetic */ UserConfig(DefaultConstructorMarker defaultConstructorMarker) {
        this();
    }

    private UserConfig() {
        MMKVConfig companion = MMKVConfig.INSTANCE.getInstance();
        this.config = companion;
        int i = 4;
        DefaultConstructorMarker defaultConstructorMarker = null;
        String str = null;
        this.userToken = new mmkvDelegate(companion, "15ef6eb5403406c1da0dc4a4defa2ea1", str, i, defaultConstructorMarker);
        this.uid = new mmkvDelegate(companion, 51888L, str, i, defaultConstructorMarker);
        this.deviceAddress = new mmkvDelegate(companion, "", str, i, defaultConstructorMarker);
        this.deviceAddressNoClear = new mmkvDelegate(companion, "", str, i, defaultConstructorMarker);
        this.deviceName = new mmkvDelegate(companion, "", str, i, defaultConstructorMarker);
        this.deviceNameNoClear = new mmkvDelegate(companion, "", str, i, defaultConstructorMarker);
        int i2 = 0;
        boolean z = false;
        this.deviceBind = new mmkvDelegate(companion, z, str, i, defaultConstructorMarker);
        int i3 = 60;
        this.videoDuration = new mmkvDelegate(companion, i3, str, i, defaultConstructorMarker);
        this.battery = new mmkvDelegate(companion, i2, str, i, defaultConstructorMarker);
        this.lowBattery = new mmkvDelegate(companion, z, str, i, defaultConstructorMarker);
        this.changing = new mmkvDelegate(companion, z, str, i, defaultConstructorMarker);
        this.fmVersion = new mmkvDelegate(companion, "", str, i, defaultConstructorMarker);
        this.hwVersion = new mmkvDelegate(companion, "", str, i, defaultConstructorMarker);
        this.fmVersionWifi = new mmkvDelegate(companion, "", str, i, defaultConstructorMarker);
        this.hwVersionWifi = new mmkvDelegate(companion, "", str, i, defaultConstructorMarker);
        this.classicBluetoothMac = new mmkvDelegate(companion, "", str, i, defaultConstructorMarker);
        this.scanKeyFilter = new mmkvDelegate(companion, "", str, i, defaultConstructorMarker);
        this.glassDeviceWifiIP = new mmkvDelegate(companion, "", str, i, defaultConstructorMarker);
        this.glassDeviceWifiName = new mmkvDelegate(companion, "", str, i, defaultConstructorMarker);
        this.glassDeviceWifiPassword = new mmkvDelegate(companion, "", str, i, defaultConstructorMarker);
        this.glassDeviceWifiMac = new mmkvDelegate(companion, "", str, i, defaultConstructorMarker);
        long j = 0L;
        this.lastLoginTimeStamp = new mmkvDelegate(companion, j, str, i, defaultConstructorMarker);
        boolean z2 = true;
        this.needShowLogin = new mmkvDelegate(companion, z2, str, i, defaultConstructorMarker);
        this.lastLoginAccount = new mmkvDelegate(companion, "", str, i, defaultConstructorMarker);
        this.lastLoginPwd = new mmkvDelegate(companion, "", str, i, defaultConstructorMarker);
        this.lastLoginType = new mmkvDelegate(companion, i2, str, i, defaultConstructorMarker);
        this.userJson = new mmkvDelegate(companion, "", str, i, defaultConstructorMarker);
        this.lastShowAppUpgradeTimeStamp = new mmkvDelegate(companion, j, str, i, defaultConstructorMarker);
        this.hasNewVersion = new mmkvDelegate(companion, z, str, i, defaultConstructorMarker);
        this.pictureWatermark = new mmkvDelegate(companion, z2, str, i, defaultConstructorMarker);
        this.pictureAutoSave = new mmkvDelegate(companion, z, str, i, defaultConstructorMarker);
        this.recordVideoDuration = new mmkvDelegate(companion, Integer.valueOf(EMachine.EM_L10M), str, i, defaultConstructorMarker);
        this.recordAudioDuration = new mmkvDelegate(companion, i3, str, i, defaultConstructorMarker);
        this.videoIsLandscape = new mmkvDelegate(companion, z2, str, i, defaultConstructorMarker);
        this.isShowedAiGuided = new mmkvDelegate(companion, z, str, i, defaultConstructorMarker);
        this.isShowedDeviceGuided = new mmkvDelegate(companion, z, str, i, defaultConstructorMarker);
        this.isShowedDeviceGuidedNew = new mmkvDelegate(companion, z, str, i, defaultConstructorMarker);
        this.isShowedBatteryLow = new mmkvDelegate(companion, z, str, i, defaultConstructorMarker);
        this.isShowedMemoryLow = new mmkvDelegate(companion, z, str, i, defaultConstructorMarker);
        this.debug = new mmkvDelegate(companion, z, str, i, defaultConstructorMarker);
        this.glassesLogs = new mmkvDelegate(companion, z, str, i, defaultConstructorMarker);
        this.otaDown = new mmkvDelegate(companion, z, str, i, defaultConstructorMarker);
        this.appLastUserTime = new mmkvDelegate(companion, Long.valueOf(new DateUtil().getUnixTimestamp()), str, i, defaultConstructorMarker);
        this.supportTranslate = new mmkvDelegate(companion, z, str, i, defaultConstructorMarker);
        this.supportWear = new mmkvDelegate(companion, z, str, i, defaultConstructorMarker);
        this.supportVolumeControl = new mmkvDelegate(companion, z, str, i, defaultConstructorMarker);
        this.glassesModel = new mmkvDelegate(companion, i2, str, i, defaultConstructorMarker);
        this.translateFromDefault = new mmkvDelegate(companion, "cn", str, i, defaultConstructorMarker);
        this.translateToDefault = new mmkvDelegate(companion, "en", str, i, defaultConstructorMarker);
        this.uniqueIdHw = new mmkvDelegate(companion, "", str, i, defaultConstructorMarker);
        this.lastQuestionTime = new mmkvDelegate(companion, j, str, i, defaultConstructorMarker);
        this.languageJson = new mmkvDelegate(companion, "", str, i, defaultConstructorMarker);
        this.aiLanguageCode = new mmkvDelegate(companion, "", str, i, defaultConstructorMarker);
        this.aiIsSystemLanguage = new mmkvDelegate(companion, z2, str, i, defaultConstructorMarker);
        this.useGyro = new mmkvDelegate(companion, z, str, i, defaultConstructorMarker);
        this.thumbnailSize = new mmkvDelegate(companion, 2, str, i, defaultConstructorMarker);
        this.camera8Mp = new mmkvDelegate(companion, z, str, i, defaultConstructorMarker);
        this.volumeControl = new mmkvDelegate(companion, "", str, i, defaultConstructorMarker);
    }

    public final String getUserToken() {
        return (String) this.userToken.getValue(this, $$delegatedProperties[0]);
    }

    public final void setUserToken(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.userToken.setValue(this, $$delegatedProperties[0], str);
    }

    public final long getUid() {
        return ((Number) this.uid.getValue(this, $$delegatedProperties[1])).longValue();
    }

    public final void setUid(long j) {
        this.uid.setValue(this, $$delegatedProperties[1], Long.valueOf(j));
    }

    public final String getDeviceAddress() {
        return (String) this.deviceAddress.getValue(this, $$delegatedProperties[2]);
    }

    public final void setDeviceAddress(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.deviceAddress.setValue(this, $$delegatedProperties[2], str);
    }

    public final String getDeviceAddressNoClear() {
        return (String) this.deviceAddressNoClear.getValue(this, $$delegatedProperties[3]);
    }

    public final void setDeviceAddressNoClear(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.deviceAddressNoClear.setValue(this, $$delegatedProperties[3], str);
    }

    public final String getDeviceName() {
        return (String) this.deviceName.getValue(this, $$delegatedProperties[4]);
    }

    public final void setDeviceName(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.deviceName.setValue(this, $$delegatedProperties[4], str);
    }

    public final String getDeviceNameNoClear() {
        return (String) this.deviceNameNoClear.getValue(this, $$delegatedProperties[5]);
    }

    public final void setDeviceNameNoClear(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.deviceNameNoClear.setValue(this, $$delegatedProperties[5], str);
    }

    public final boolean getDeviceBind() {
        return ((Boolean) this.deviceBind.getValue(this, $$delegatedProperties[6])).booleanValue();
    }

    public final void setDeviceBind(boolean z) {
        this.deviceBind.setValue(this, $$delegatedProperties[6], Boolean.valueOf(z));
    }

    public final int getVideoDuration() {
        return ((Number) this.videoDuration.getValue(this, $$delegatedProperties[7])).intValue();
    }

    public final void setVideoDuration(int i) {
        this.videoDuration.setValue(this, $$delegatedProperties[7], Integer.valueOf(i));
    }

    public final int getBattery() {
        return ((Number) this.battery.getValue(this, $$delegatedProperties[8])).intValue();
    }

    public final void setBattery(int i) {
        this.battery.setValue(this, $$delegatedProperties[8], Integer.valueOf(i));
    }

    public final boolean getLowBattery() {
        return ((Boolean) this.lowBattery.getValue(this, $$delegatedProperties[9])).booleanValue();
    }

    public final void setLowBattery(boolean z) {
        this.lowBattery.setValue(this, $$delegatedProperties[9], Boolean.valueOf(z));
    }

    public final boolean getChanging() {
        return ((Boolean) this.changing.getValue(this, $$delegatedProperties[10])).booleanValue();
    }

    public final void setChanging(boolean z) {
        this.changing.setValue(this, $$delegatedProperties[10], Boolean.valueOf(z));
    }

    public final String getFmVersion() {
        return (String) this.fmVersion.getValue(this, $$delegatedProperties[11]);
    }

    public final void setFmVersion(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.fmVersion.setValue(this, $$delegatedProperties[11], str);
    }

    public final String getHwVersion() {
        return (String) this.hwVersion.getValue(this, $$delegatedProperties[12]);
    }

    public final void setHwVersion(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.hwVersion.setValue(this, $$delegatedProperties[12], str);
    }

    public final String getFmVersionWifi() {
        return (String) this.fmVersionWifi.getValue(this, $$delegatedProperties[13]);
    }

    public final void setFmVersionWifi(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.fmVersionWifi.setValue(this, $$delegatedProperties[13], str);
    }

    public final String getHwVersionWifi() {
        return (String) this.hwVersionWifi.getValue(this, $$delegatedProperties[14]);
    }

    public final void setHwVersionWifi(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.hwVersionWifi.setValue(this, $$delegatedProperties[14], str);
    }

    public final String getClassicBluetoothMac() {
        return (String) this.classicBluetoothMac.getValue(this, $$delegatedProperties[15]);
    }

    public final void setClassicBluetoothMac(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.classicBluetoothMac.setValue(this, $$delegatedProperties[15], str);
    }

    public final String getScanKeyFilter() {
        return (String) this.scanKeyFilter.getValue(this, $$delegatedProperties[16]);
    }

    public final void setScanKeyFilter(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.scanKeyFilter.setValue(this, $$delegatedProperties[16], str);
    }

    public final String getGlassDeviceWifiIP() {
        return (String) this.glassDeviceWifiIP.getValue(this, $$delegatedProperties[17]);
    }

    public final void setGlassDeviceWifiIP(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.glassDeviceWifiIP.setValue(this, $$delegatedProperties[17], str);
    }

    public final String getGlassDeviceWifiName() {
        return (String) this.glassDeviceWifiName.getValue(this, $$delegatedProperties[18]);
    }

    public final void setGlassDeviceWifiName(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.glassDeviceWifiName.setValue(this, $$delegatedProperties[18], str);
    }

    public final String getGlassDeviceWifiPassword() {
        return (String) this.glassDeviceWifiPassword.getValue(this, $$delegatedProperties[19]);
    }

    public final void setGlassDeviceWifiPassword(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.glassDeviceWifiPassword.setValue(this, $$delegatedProperties[19], str);
    }

    public final String getGlassDeviceWifiMac() {
        return (String) this.glassDeviceWifiMac.getValue(this, $$delegatedProperties[20]);
    }

    public final void setGlassDeviceWifiMac(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.glassDeviceWifiMac.setValue(this, $$delegatedProperties[20], str);
    }

    public final long getLastLoginTimeStamp() {
        return ((Number) this.lastLoginTimeStamp.getValue(this, $$delegatedProperties[21])).longValue();
    }

    public final void setLastLoginTimeStamp(long j) {
        this.lastLoginTimeStamp.setValue(this, $$delegatedProperties[21], Long.valueOf(j));
    }

    public final boolean getNeedShowLogin() {
        return ((Boolean) this.needShowLogin.getValue(this, $$delegatedProperties[22])).booleanValue();
    }

    public final void setNeedShowLogin(boolean z) {
        this.needShowLogin.setValue(this, $$delegatedProperties[22], Boolean.valueOf(z));
    }

    public final String getLastLoginAccount() {
        return (String) this.lastLoginAccount.getValue(this, $$delegatedProperties[23]);
    }

    public final void setLastLoginAccount(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.lastLoginAccount.setValue(this, $$delegatedProperties[23], str);
    }

    public final String getLastLoginPwd() {
        return (String) this.lastLoginPwd.getValue(this, $$delegatedProperties[24]);
    }

    public final void setLastLoginPwd(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.lastLoginPwd.setValue(this, $$delegatedProperties[24], str);
    }

    public final int getLastLoginType() {
        return ((Number) this.lastLoginType.getValue(this, $$delegatedProperties[25])).intValue();
    }

    public final void setLastLoginType(int i) {
        this.lastLoginType.setValue(this, $$delegatedProperties[25], Integer.valueOf(i));
    }

    public final String getUserJson() {
        return (String) this.userJson.getValue(this, $$delegatedProperties[26]);
    }

    public final void setUserJson(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.userJson.setValue(this, $$delegatedProperties[26], str);
    }

    public final long getLastShowAppUpgradeTimeStamp() {
        return ((Number) this.lastShowAppUpgradeTimeStamp.getValue(this, $$delegatedProperties[27])).longValue();
    }

    public final void setLastShowAppUpgradeTimeStamp(long j) {
        this.lastShowAppUpgradeTimeStamp.setValue(this, $$delegatedProperties[27], Long.valueOf(j));
    }

    public final boolean getHasNewVersion() {
        return ((Boolean) this.hasNewVersion.getValue(this, $$delegatedProperties[28])).booleanValue();
    }

    public final void setHasNewVersion(boolean z) {
        this.hasNewVersion.setValue(this, $$delegatedProperties[28], Boolean.valueOf(z));
    }

    public final boolean getPictureWatermark() {
        return ((Boolean) this.pictureWatermark.getValue(this, $$delegatedProperties[29])).booleanValue();
    }

    public final void setPictureWatermark(boolean z) {
        this.pictureWatermark.setValue(this, $$delegatedProperties[29], Boolean.valueOf(z));
    }

    public final boolean getPictureAutoSave() {
        return ((Boolean) this.pictureAutoSave.getValue(this, $$delegatedProperties[30])).booleanValue();
    }

    public final void setPictureAutoSave(boolean z) {
        this.pictureAutoSave.setValue(this, $$delegatedProperties[30], Boolean.valueOf(z));
    }

    public final int getRecordVideoDuration() {
        return ((Number) this.recordVideoDuration.getValue(this, $$delegatedProperties[31])).intValue();
    }

    public final void setRecordVideoDuration(int i) {
        this.recordVideoDuration.setValue(this, $$delegatedProperties[31], Integer.valueOf(i));
    }

    public final int getRecordAudioDuration() {
        return ((Number) this.recordAudioDuration.getValue(this, $$delegatedProperties[32])).intValue();
    }

    public final void setRecordAudioDuration(int i) {
        this.recordAudioDuration.setValue(this, $$delegatedProperties[32], Integer.valueOf(i));
    }

    public final boolean getVideoIsLandscape() {
        return ((Boolean) this.videoIsLandscape.getValue(this, $$delegatedProperties[33])).booleanValue();
    }

    public final void setVideoIsLandscape(boolean z) {
        this.videoIsLandscape.setValue(this, $$delegatedProperties[33], Boolean.valueOf(z));
    }

    public final boolean isShowedAiGuided() {
        return ((Boolean) this.isShowedAiGuided.getValue(this, $$delegatedProperties[34])).booleanValue();
    }

    public final void setShowedAiGuided(boolean z) {
        this.isShowedAiGuided.setValue(this, $$delegatedProperties[34], Boolean.valueOf(z));
    }

    public final boolean isShowedDeviceGuided() {
        return ((Boolean) this.isShowedDeviceGuided.getValue(this, $$delegatedProperties[35])).booleanValue();
    }

    public final void setShowedDeviceGuided(boolean z) {
        this.isShowedDeviceGuided.setValue(this, $$delegatedProperties[35], Boolean.valueOf(z));
    }

    public final boolean isShowedDeviceGuidedNew() {
        return ((Boolean) this.isShowedDeviceGuidedNew.getValue(this, $$delegatedProperties[36])).booleanValue();
    }

    public final void setShowedDeviceGuidedNew(boolean z) {
        this.isShowedDeviceGuidedNew.setValue(this, $$delegatedProperties[36], Boolean.valueOf(z));
    }

    public final boolean isShowedBatteryLow() {
        return ((Boolean) this.isShowedBatteryLow.getValue(this, $$delegatedProperties[37])).booleanValue();
    }

    public final void setShowedBatteryLow(boolean z) {
        this.isShowedBatteryLow.setValue(this, $$delegatedProperties[37], Boolean.valueOf(z));
    }

    public final boolean isShowedMemoryLow() {
        return ((Boolean) this.isShowedMemoryLow.getValue(this, $$delegatedProperties[38])).booleanValue();
    }

    public final void setShowedMemoryLow(boolean z) {
        this.isShowedMemoryLow.setValue(this, $$delegatedProperties[38], Boolean.valueOf(z));
    }

    public final boolean getDebug() {
        return ((Boolean) this.debug.getValue(this, $$delegatedProperties[39])).booleanValue();
    }

    public final void setDebug(boolean z) {
        this.debug.setValue(this, $$delegatedProperties[39], Boolean.valueOf(z));
    }

    public final boolean getGlassesLogs() {
        return ((Boolean) this.glassesLogs.getValue(this, $$delegatedProperties[40])).booleanValue();
    }

    public final void setGlassesLogs(boolean z) {
        this.glassesLogs.setValue(this, $$delegatedProperties[40], Boolean.valueOf(z));
    }

    public final boolean getOtaDown() {
        return ((Boolean) this.otaDown.getValue(this, $$delegatedProperties[41])).booleanValue();
    }

    public final void setOtaDown(boolean z) {
        this.otaDown.setValue(this, $$delegatedProperties[41], Boolean.valueOf(z));
    }

    public final long getAppLastUserTime() {
        return ((Number) this.appLastUserTime.getValue(this, $$delegatedProperties[42])).longValue();
    }

    public final void setAppLastUserTime(long j) {
        this.appLastUserTime.setValue(this, $$delegatedProperties[42], Long.valueOf(j));
    }

    public final boolean getSupportTranslate() {
        return ((Boolean) this.supportTranslate.getValue(this, $$delegatedProperties[43])).booleanValue();
    }

    public final void setSupportTranslate(boolean z) {
        this.supportTranslate.setValue(this, $$delegatedProperties[43], Boolean.valueOf(z));
    }

    public final boolean getSupportWear() {
        return ((Boolean) this.supportWear.getValue(this, $$delegatedProperties[44])).booleanValue();
    }

    public final void setSupportWear(boolean z) {
        this.supportWear.setValue(this, $$delegatedProperties[44], Boolean.valueOf(z));
    }

    public final boolean getSupportVolumeControl() {
        return ((Boolean) this.supportVolumeControl.getValue(this, $$delegatedProperties[45])).booleanValue();
    }

    public final void setSupportVolumeControl(boolean z) {
        this.supportVolumeControl.setValue(this, $$delegatedProperties[45], Boolean.valueOf(z));
    }

    public final int getGlassesModel() {
        return ((Number) this.glassesModel.getValue(this, $$delegatedProperties[46])).intValue();
    }

    public final void setGlassesModel(int i) {
        this.glassesModel.setValue(this, $$delegatedProperties[46], Integer.valueOf(i));
    }

    public final String getTranslateFromDefault() {
        return (String) this.translateFromDefault.getValue(this, $$delegatedProperties[47]);
    }

    public final void setTranslateFromDefault(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.translateFromDefault.setValue(this, $$delegatedProperties[47], str);
    }

    public final String getTranslateToDefault() {
        return (String) this.translateToDefault.getValue(this, $$delegatedProperties[48]);
    }

    public final void setTranslateToDefault(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.translateToDefault.setValue(this, $$delegatedProperties[48], str);
    }

    public final String getUniqueIdHw() {
        return (String) this.uniqueIdHw.getValue(this, $$delegatedProperties[49]);
    }

    public final void setUniqueIdHw(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.uniqueIdHw.setValue(this, $$delegatedProperties[49], str);
    }

    public final long getLastQuestionTime() {
        return ((Number) this.lastQuestionTime.getValue(this, $$delegatedProperties[50])).longValue();
    }

    public final void setLastQuestionTime(long j) {
        this.lastQuestionTime.setValue(this, $$delegatedProperties[50], Long.valueOf(j));
    }

    public final String getLanguageJson() {
        return (String) this.languageJson.getValue(this, $$delegatedProperties[51]);
    }

    public final void setLanguageJson(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.languageJson.setValue(this, $$delegatedProperties[51], str);
    }

    public final String getAiLanguageCode() {
        return (String) this.aiLanguageCode.getValue(this, $$delegatedProperties[52]);
    }

    public final void setAiLanguageCode(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.aiLanguageCode.setValue(this, $$delegatedProperties[52], str);
    }

    public final boolean getAiIsSystemLanguage() {
        return ((Boolean) this.aiIsSystemLanguage.getValue(this, $$delegatedProperties[53])).booleanValue();
    }

    public final void setAiIsSystemLanguage(boolean z) {
        this.aiIsSystemLanguage.setValue(this, $$delegatedProperties[53], Boolean.valueOf(z));
    }

    public final boolean getUseGyro() {
        return ((Boolean) this.useGyro.getValue(this, $$delegatedProperties[54])).booleanValue();
    }

    public final void setUseGyro(boolean z) {
        this.useGyro.setValue(this, $$delegatedProperties[54], Boolean.valueOf(z));
    }

    public final int getThumbnailSize() {
        return ((Number) this.thumbnailSize.getValue(this, $$delegatedProperties[55])).intValue();
    }

    public final void setThumbnailSize(int i) {
        this.thumbnailSize.setValue(this, $$delegatedProperties[55], Integer.valueOf(i));
    }

    public final boolean getCamera8Mp() {
        return ((Boolean) this.camera8Mp.getValue(this, $$delegatedProperties[56])).booleanValue();
    }

    public final void setCamera8Mp(boolean z) {
        this.camera8Mp.setValue(this, $$delegatedProperties[56], Boolean.valueOf(z));
    }

    public final String getVolumeControl() {
        return (String) this.volumeControl.getValue(this, $$delegatedProperties[57]);
    }

    public final void setVolumeControl(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.volumeControl.setValue(this, $$delegatedProperties[57], str);
    }

    /* compiled from: UserConfig.kt */
    @Metadata(m606d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u0006\u0010\u0005\u001a\u00020\u0004R\u0010\u0010\u0003\u001a\u0004\u0018\u00010\u0004X\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006\u0006"}, m607d2 = {"Lcom/glasssutdio/wear/all/pref/UserConfig$Companion;", "", "()V", "instance", "Lcom/glasssutdio/wear/all/pref/UserConfig;", "getInstance", "app_release"}, m608k = 1, m609mv = {1, 9, 0}, m611xi = 48)
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        public final UserConfig getInstance() {
            if (UserConfig.instance == null) {
                synchronized (UserConfig.class) {
                    if (UserConfig.instance == null) {
                        Companion companion = UserConfig.INSTANCE;
                        UserConfig.instance = new UserConfig(null);
                    }
                    Unit unit = Unit.INSTANCE;
                }
            }
            UserConfig userConfig = UserConfig.instance;
            Intrinsics.checkNotNull(userConfig);
            return userConfig;
        }
    }
}
