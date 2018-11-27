package com.example.hy.wanandroid.presenter;

import com.example.hy.wanandroid.R;
import com.example.hy.wanandroid.base.presenter.BasePresenter;
import com.example.hy.wanandroid.config.App;
import com.example.hy.wanandroid.config.RxBus;
import com.example.hy.wanandroid.contract.MainContract;
import com.example.hy.wanandroid.event.NightModeEvent;
import com.example.hy.wanandroid.model.DataModel;
import com.example.hy.wanandroid.model.network.entity.DefaultObserver;
import com.example.hy.wanandroid.utils.RxUtils;

import javax.inject.Inject;

import io.reactivex.functions.Function;

/**
 * MainActivity的Presenter
 * Created by 陈健宇 at 2018/10/23
 */
public class MainPresenter extends BasePresenter<MainContract.View> implements MainContract.Presenter{

    @Inject
    public MainPresenter(DataModel dataModel) {
        super(dataModel);
    }

    @Override
    public void subscribleEvent() {
        addSubcriber(
                RxBus.getInstance().toObservable(NightModeEvent.class)
                       .compose(RxUtils.switchSchedulers())
                       .subscribeWith(new DefaultObserver<NightModeEvent>(mView, false, false){
                           @Override
                           public void onNext(NightModeEvent nightModeEvent) {
                               mView.userNightNode(nightModeEvent.isNight());
                           }

                           @Override
                           protected void unknown() {
                               mView.showToast(App.getContext().getString(R.string.error_switch_fail));
                           }
                       })
        );
    }

    @Override
    public void setCurrentItem(int position) {
        mModel.setCurrentItem(position);
    }

    @Override
    public int getCurrentItem() {
        return mModel.getCurrentItem();
    }

}
