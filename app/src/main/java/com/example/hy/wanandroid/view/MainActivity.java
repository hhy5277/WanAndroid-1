package com.example.hy.wanandroid.view;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.widget.FrameLayout;

import com.example.commonlib.utils.FileProvider7;
import com.example.commonlib.utils.LogUtil;
import com.example.commonlib.utils.ShareUtil;
import com.example.hy.wanandroid.R;
import com.example.hy.wanandroid.base.activity.BaseActivity;
import com.example.hy.wanandroid.base.activity.BaseMvpActivity;
import com.example.hy.wanandroid.base.fragment.BaseFragment;
import com.example.hy.wanandroid.config.App;
import com.example.hy.wanandroid.config.Constant;
import com.example.hy.wanandroid.contract.MainContract;
import com.example.hy.wanandroid.di.component.activity.DaggerMainActivityComponent;
import com.example.hy.wanandroid.di.component.activity.MainActivityComponent;
import com.example.hy.wanandroid.event.ToppingEvent;
import com.example.hy.wanandroid.presenter.MainPresenter;
import com.example.hy.wanandroid.config.RxBus;
import com.example.hy.wanandroid.utlis.DownloadUtil;
import com.example.commonlib.utils.StatusBarUtil;
import com.example.commonlib.utils.ToastUtil;
import com.example.hy.wanandroid.view.hierarchy.HierarchyFragment;
import com.example.hy.wanandroid.view.homepager.HomeFragment;
import com.example.hy.wanandroid.view.mine.MineFragment;
import com.example.hy.wanandroid.view.project.ProjectFragment;
import com.example.hy.wanandroid.view.wechat.WeChatFragment;
import com.example.hy.wanandroid.widget.dialog.OpenBrowseDialog;
import com.example.hy.wanandroid.widget.dialog.VersionDialog;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import butterknife.BindView;
import dagger.Lazy;

import android.os.Handler;


import java.io.File;

import javax.inject.Inject;


public class MainActivity extends BaseMvpActivity<MainPresenter> implements MainContract.View {

    @BindView(R.id.fl_container)
    FrameLayout flContainer;
    @BindView(R.id.fbtn_up)
    FloatingActionButton fbtnUp;
    @BindView(R.id.bnv_btm)
    BottomNavigationView bnvBtm;

    private int mPreFragmentPosition = 0;//上一个被选中的Fragment位置
    private MainActivityComponent mMainActivityComponent;
    private ObjectAnimator mShowNavAnimator;
    private ViewPropertyAnimator mHideFbtnAnimator, mShowFbtnAnimator;
    private boolean isPermissionDeniedRequest;

    @Inject
    MainPresenter mPresenter;
    @Inject
    Fragment[] mFragments;
    @Inject
    Lazy<VersionDialog> mVersionDialog;
    @Inject
    OpenBrowseDialog mOpenBrowseDialog;

    private String mNewVersionName;

    @Override
    protected void inject() {
        mMainActivityComponent = DaggerMainActivityComponent.builder()
                .appComponent(getAppComponent())
                .build();
        mMainActivityComponent.inject(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFragments = new BaseFragment[5];
        if(savedInstanceState == null) {
            mFragments[0] = HomeFragment.newInstance();
            mFragments[1] = HierarchyFragment.newInstance();
            mFragments[2] = WeChatFragment.newInstance();
            mFragments[3] = ProjectFragment.newInstance();
            mFragments[4] = MineFragment.newInstance();
            loadMultipleFragment(R.id.fl_container, 0, mFragments);
            AppCompatDelegate.setDefaultNightMode(mPresenter.getNightModeState() ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
        }else {
            mFragments[0] = findFragmentByTag(HomeFragment.class.getName());
            mFragments[1] = findFragmentByTag(HierarchyFragment.class.getName());
            mFragments[2] = findFragmentByTag(WeChatFragment.class.getName());
            mFragments[3] = findFragmentByTag(ProjectFragment.class.getName());
            mFragments[4] = findFragmentByTag(MineFragment.class.getName());
            bnvBtm.setSelectedItemId(getSelectedId(mPresenter.getCurrentItem()));
        }
    }

    @Override
    protected void initView() {
        super.initView();
        bnvBtm.setOnNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()){
                case R.id.item_home:
                    showAndHideFragment(mFragments[0], mFragments[mPreFragmentPosition]);
                    mPreFragmentPosition = 0;
                    showFloatingButton();
                    setStatusBarColor(mPresenter.getStatusBarState());
                    break;
                case R.id.item_hierarchy:
                    showAndHideFragment(mFragments[1], mFragments[mPreFragmentPosition]);
                    mPreFragmentPosition = 1;
                    showFloatingButton();
                    setStatusBarColor(mPresenter.getStatusBarState());
                    break;
                case R.id.item_wechat:
                    showAndHideFragment(mFragments[2], mFragments[mPreFragmentPosition]);
                    mPreFragmentPosition = 2;
                    showFloatingButton();
                    setStatusBarColor(mPresenter.getStatusBarState());
                    break;
                case R.id.item_project:
                    showAndHideFragment(mFragments[3], mFragments[mPreFragmentPosition]);
                    mPreFragmentPosition = 3;
                    showFloatingButton();
                    setStatusBarColor(mPresenter.getStatusBarState());
                    break;
                case R.id.item_mine:
                    showAndHideFragment(mFragments[4], mFragments[mPreFragmentPosition]);
                    mPreFragmentPosition = 4;
                    hideFloatingButton();
                    StatusBarUtil.immersiveInImage(this);
                    break;
                default:
                    break;
            }
            return true;
        });

        fbtnUp.setOnClickListener(v -> {
            RxBus.getInstance().post(new ToppingEvent());
            show(bnvBtm);
        });

        if(mPresenter.getAutoUpdataState())
            mPresenter.checkVersion(DownloadUtil.getVersionName(this));
    }

    @Override
    protected void initData() {
        mPresenter.subscribleEvent();
    }

    @Override
    protected MainPresenter getPresenter() {
        return mPresenter;
    }

    @Override
    public void onBackPressed() {
        if(System.currentTimeMillis() - Constant.TOUCH_TIME < Constant.WAIT_TIME){
            finish();
        }else {
            Constant.TOUCH_TIME = System.currentTimeMillis();
            ToastUtil.toastInCenter(this, getString(R.string.mainActivity_back));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mPresenter.setCurrentItem(mPreFragmentPosition);
    }

    @Override
    protected void onStop() {
        if(mHideFbtnAnimator != null) mHideFbtnAnimator.cancel();
        if(mShowFbtnAnimator != null) mShowFbtnAnimator.cancel();
        if(mShowNavAnimator != null) mShowNavAnimator.cancel();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if(mOpenBrowseDialog != null)
            mOpenBrowseDialog = null;
        if(mVersionDialog != null)
            mVersionDialog = null;
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK && requestCode == Constant.REQUEST_CODE_UNKNOWN_APP)
            installApk(this);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == Constant.REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE
                && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            DownloadUtil.downloadApk(this, mNewVersionName);
        else{
            if(!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)){
                if(!isPermissionDeniedRequest){
                    isPermissionDeniedRequest = true;
                    return;
                }
                ShareUtil.gotoAppDetailIntent(this);
                showToast(getString(R.string.settingsActivity_permission_denied_request));
                return;
            }
            showToast(getString(R.string.settingsActivity_permission_denied));
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void showUpdataDialog(String content) {
        mVersionDialog.get().setContentText(content);
        mVersionDialog.get().setIsMain(true);
        mVersionDialog.get().show(getSupportFragmentManager(), "tag5");
    }

    @Override
    public void setNewVersionName(String versionName) {
        mNewVersionName = versionName;
    }

    @Override
    public void upDataVersion() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    Constant.REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE);
        }else {
            DownloadUtil.downloadApk(this, mNewVersionName);
        }
    }

    @Override
    public void showOpenBrowseDialog() {
        mOpenBrowseDialog.show(getSupportFragmentManager(), "tag10");
    }

    @Override
    public void installApk() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            boolean hasInstallPermission = getPackageManager().canRequestPackageInstalls();
            if (hasInstallPermission)
                installApk(this);
             else {
                //跳转至“安装未知应用”权限界面，引导用户开启权限
                Uri selfPackageUri = Uri.parse("package:" + this.getPackageName());
                Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, selfPackageUri);
                startActivityForResult(intent, Constant.REQUEST_CODE_UNKNOWN_APP);
            }
        }else
            installApk(this);
    }

    public MainActivityComponent getComponent(){
        return mMainActivityComponent;
    }

    @Override
    public void setStatusBarColor(boolean isSet) {
        if(isSet){
            StatusBarUtil.immersiveInFragments(this, getResources().getColor(R.color.colorPrimary), 1);
        }else {
            StatusBarUtil.immersiveInFragments(this, getResources().getColor(R.color.colorPrimaryDark), 1);
        }
    }

    /**
     * 装载Fragments
     */
    private void loadMultipleFragment(int containerId, int showFragment, Fragment... fragments){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        for(int i = 0; i < fragments.length; i++){
            Fragment fragment = fragments[i];
            transaction.add(containerId, fragment, fragment.getClass().getName());
            if(i != showFragment){
                transaction.hide(fragment);
            }
        }
        transaction.commitAllowingStateLoss();
    }

    /**
     * 根据tag找到fragment实例
     */
    private Fragment findFragmentByTag(String tag){
        return getSupportFragmentManager().findFragmentByTag(tag);
    }

    /**
     * 显示和隐藏fragment
     */
    private void showAndHideFragment(Fragment show, Fragment hide){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if(show != hide)
            transaction.show(show).hide(hide).commitAllowingStateLoss();
    }

    /**
     * 获得被选中的item
     */
    private int getSelectedId(int currentItem) {
        int id;
        switch (currentItem){
            case 0:
                id = R.id.item_home;
                break;
            case 1:
                id = R.id.item_hierarchy;
                break;
            case 2:
                id = R.id.item_wechat;
                break;
            case 3:
                id = R.id.item_project;
                break;
            case 4:
                id = R.id.item_mine;
                break;
            default:
                id = R.id.item_home;
                break;
        }
        return id;
    }


    /**
     * 显示floatingButton
     */
    @SuppressLint("RestrictedApi")
    private void showFloatingButton(){
        if(fbtnUp.getVisibility() == View.INVISIBLE){
            fbtnUp.setVisibility(View.VISIBLE);
            mShowFbtnAnimator = fbtnUp.animate().setDuration(500).setInterpolator(new BounceInterpolator()).translationY(0);
        }
    }

    /**
     * 隐藏floatingButton
     */
    @SuppressLint("RestrictedApi")
    private void hideFloatingButton(){
        if(fbtnUp.getVisibility() == View.VISIBLE){
            mHideFbtnAnimator = fbtnUp.animate().setDuration(300).setInterpolator(new AccelerateDecelerateInterpolator()).translationY(
                    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, 400, getResources().getDisplayMetrics())
            );
            new Handler().postDelayed(() -> fbtnUp.setVisibility(View.INVISIBLE), 301);
            mHideFbtnAnimator.start();
        }
    }

    /**
     * 底部导航栏显示
     */
    private void show(View child) {
        if(mShowNavAnimator == null){
            mShowNavAnimator = ObjectAnimator.ofFloat(child, "translationY", child.getHeight(), 0)
                    .setDuration(200);
            mShowNavAnimator.setInterpolator(new FastOutSlowInInterpolator());
        }
        if(!mShowNavAnimator.isRunning() && child.getTranslationY() >= child.getHeight()){
            mShowNavAnimator.start();
        }
    }

    /**
     * 安装应用
     */
    private void installApk(Context context) {
        LogUtil.d(LogUtil.TAG_COMMON, "安装应用");
        File file = new File(Constant.PATH_APK_1);
        if (file.exists()) {
            Intent install = new Intent("android.intent.action.VIEW");
            FileProvider7.setIntentDataAndType(
                    App.getContext(),
                    install,
                    "application/vnd.android.package-archive",
                    file,
                    false
            );
            install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(install);
        }else {
            LogUtil.d(LogUtil.TAG_COMMON, "应用路径不存在");
            ToastUtil.toastInBottom(context, context.getString(R.string.setup_fail));
        }
    }

}
