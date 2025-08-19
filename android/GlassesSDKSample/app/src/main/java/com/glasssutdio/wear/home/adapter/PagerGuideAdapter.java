package com.glasssutdio.wear.home.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.glasssutdio.wear.home.BaseFragment;
import com.glasssutdio.wear.home.album.AlbumAllFragment;
import com.glasssutdio.wear.home.album.AlbumImageFragment;
import com.glasssutdio.wear.home.album.AlbumLikeFragment;
import com.glasssutdio.wear.home.album.AlbumRecordFragment;
import com.glasssutdio.wear.home.album.AlbumVideoFragment;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: PagerGuideAdapter.kt */
@Metadata(m606d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0002\u0010\u0004J\u0010\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000bH\u0016J\u0010\u0010\f\u001a\u0004\u0018\u00010\t2\u0006\u0010\n\u001a\u00020\u000bJ\b\u0010\r\u001a\u00020\u000bH\u0016R\u0014\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006X\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006\u000e"}, m607d2 = {"Lcom/glasssutdio/wear/home/adapter/PagerGuideAdapter;", "Landroidx/viewpager2/adapter/FragmentStateAdapter;", "fragmentActivity", "Landroidx/fragment/app/FragmentActivity;", "(Landroidx/fragment/app/FragmentActivity;)V", "fragmentList", "", "Lcom/glasssutdio/wear/home/BaseFragment;", "createFragment", "Landroidx/fragment/app/Fragment;", "position", "", "getFragmentAtPosition", "getItemCount", "app_release"}, m608k = 1, m609mv = {1, 9, 0}, m611xi = 48)
/* loaded from: classes.dex */
public final class PagerGuideAdapter extends FragmentStateAdapter {
    private List<? extends BaseFragment> fragmentList;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public PagerGuideAdapter(FragmentActivity fragmentActivity) {
        super(fragmentActivity);
        Intrinsics.checkNotNullParameter(fragmentActivity, "fragmentActivity");
        this.fragmentList = CollectionsKt.listOf((Object[]) new BaseFragment[]{new AlbumAllFragment(), new AlbumImageFragment(), new AlbumVideoFragment(), new AlbumRecordFragment(), new AlbumLikeFragment()});
    }

    @Override // androidx.viewpager2.adapter.FragmentStateAdapter
    public Fragment createFragment(int position) {
        return this.fragmentList.get(position);
    }

    public final Fragment getFragmentAtPosition(int position) {
        return (Fragment) CollectionsKt.getOrNull(this.fragmentList, position);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        return this.fragmentList.size();
    }
}
