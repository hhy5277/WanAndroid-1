package com.example.hy.wanandroid.view.homepager;

import android.annotation.SuppressLint;
import android.view.View;

import com.example.hy.wanandroid.R;
import com.example.hy.wanandroid.base.fragment.BaseFragment;
import com.example.hy.wanandroid.contract.homepager.HomeContract;
import com.example.hy.wanandroid.di.module.HomeFragmentModule;
import com.example.hy.wanandroid.network.entity.homepager.BannerData;
import com.example.hy.wanandroid.presenter.homepager.HomePresenter;
import com.example.hy.wanandroid.utils.BannerImageLoader;
import com.example.hy.wanandroid.view.MainActivity;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;

/**
 * 首页tab
 * Created by 陈健宇 at 2018/10/23
 */
public class HomeFragment extends BaseFragment implements HomeContract.View {

    @BindView(R.id.tl_common)
    Toolbar tlCommon;
    @BindView(R.id.rv_articles)
    RecyclerView rvArticles;
    @BindView(R.id.srl_home)
    SmartRefreshLayout srlHome;
    @BindView(R.id.fake_status_bar)
    View fakeStatusBar;
    @BindView(R.id.banner)
    Banner banner;

    @Inject
    @Named("bannerTitles")
    List<String> bannerTitles;

    @Inject
    @Named("bannerImages")
    List<String> bannerImages;

    @Inject
    HomePresenter mPresenter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_homepager;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    protected void initView() {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).getMainActivityComponent().getHomFragmentSubComponent(new HomeFragmentModule()).inject(this);
        }
        mPresenter.attachView(this);
        tlCommon.setTitle(R.string.menu_btm_nav_home);
    }

    @Override
    protected void initData() {
        mPresenter.loadHomePagerDatas();
    }

    @Override
    public void onStart() {
        super.onStart();
        banner.startAutoPlay();
    }

    @Override
    public void onStop() {
        super.onStop();
        banner.stopAutoPlay();
    }

    @Override
    public void showBannerDatas(List<BannerData> bannerDataList) {
        //获得标题,图片
        for (BannerData bannerData : bannerDataList) {
            bannerTitles.add(bannerData.getTitle());
            bannerImages.add(bannerData.getImagePath());
        }
        //设置banner
        banner.setImageLoader(new BannerImageLoader())
                .setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE)//显示圆形指示器和标题（水平显示）
                .setImages(bannerImages)//设置图片集合
                .setBannerAnimation(Transformer.BackgroundToForeground)//设置轮播动画
                .setBannerTitles(bannerTitles)//设置标题集合
                .setIndicatorGravity(BannerConfig.RIGHT)//设置指示器位置，右边
                .setDelayTime(2000)//设置轮播事件间隔
                .setOnBannerListener(position -> {
                        //跳转到详情
                })//设置点击事件，下标从零开始
                .start();
    }

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }
}
