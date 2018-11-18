package com.example.hy.wanandroid.presenter.mine;

import com.example.hy.wanandroid.base.presenter.BasePresenter;
import com.example.hy.wanandroid.config.RxBus;
import com.example.hy.wanandroid.config.User;
import com.example.hy.wanandroid.contract.mine.MineContract;
import com.example.hy.wanandroid.core.network.entity.DefaultObserver;
import com.example.hy.wanandroid.core.network.entity.mine.Login;
import com.example.hy.wanandroid.event.LoginEvent;
import com.example.hy.wanandroid.model.mine.MineModel;
import com.example.hy.wanandroid.utils.RxUtils;

import javax.inject.Inject;

import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

/**
 * 我的界面的Presenter
 * Created by 陈健宇 at 2018/10/23
 */
public class MinePresenter extends BasePresenter<MineContract.View> implements MineContract.Presenter{

    private MineModel mModel;

    @Inject
    public MinePresenter(MineModel mineModel) {
        mModel = mineModel;
    }

    @Override
    public void subscribleEvent() {
        super.subscribleEvent();
        addSubcriber(
                RxBus.getInstance().toObservable(LoginEvent.class)
                        .filter(loginEvent -> loginEvent.isLogin())
                        .subscribe(loginEvent -> mView.showLoginView())
        );
        addSubcriber(
                RxBus.getInstance().toObservable(LoginEvent.class)
                .filter(loginEvent -> !loginEvent.isLogin())
                .subscribe(loginEvent -> logout())
        );
    }

    @Override
    public void logout() {
        addSubcriber(
                mModel.getLogoutRequest()
                .compose(RxUtils.switchSchedulers())
                .compose(RxUtils.handleRequest2())
                .subscribeWith(new DefaultObserver<Login>(mView, false, false){
                    @Override
                    public void onNext(Login login) {
                        super.onNext(login);
                        User.getInstance().reset();
                        mView.showLogoutView();
                    }
                })
        );
    }
}
