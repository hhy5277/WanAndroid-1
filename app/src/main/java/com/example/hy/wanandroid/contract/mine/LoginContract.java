package com.example.hy.wanandroid.contract.mine;

import com.example.hy.wanandroid.base.presenter.IPresenter;
import com.example.hy.wanandroid.base.view.IView;
import com.example.hy.wanandroid.core.network.entity.BaseResponse;
import com.example.hy.wanandroid.core.network.entity.mine.Login;

import io.reactivex.Observable;

/**
 * 登陆界面的Contract
 * Created by 陈健宇 at 2018/11/16
 */
public interface LoginContract {

    interface View extends IView{

        void setAccountErrorView(String error);//设置用户名错误提示
        void setPasswordErrorView(String error);//设置密码错误提示
        void requestFocus(boolean cancel);//输入框获取焦点
        void loginSuccess();//登陆成功

    }

    interface Presenter extends IPresenter<View>{
        void login(String account, String password);//登陆
    }

    interface Model{
        Observable<BaseResponse<Login>> getLoginRequest(String username, String password);
    }

}
