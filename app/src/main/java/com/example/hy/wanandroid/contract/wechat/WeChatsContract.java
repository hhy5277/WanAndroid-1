package com.example.hy.wanandroid.contract.wechat;

import com.example.hy.wanandroid.base.presenter.IPresenter;
import com.example.hy.wanandroid.base.view.BaseView;
import com.example.hy.wanandroid.contract.project.ProjectsContract;
import com.example.hy.wanandroid.model.network.entity.Article;

import java.util.List;

/**
 * WeChats的Contract
 * Created by 陈健宇 at 2018/12/19
 */
public interface WeChatsContract {

    interface View extends BaseView {

        void showWeChats(List<Article> articleList);//展示公众号文章列表
        void showMoreWeChats(List<Article> articleList);//展示更多公众号文章列表
        void topping();//置顶
        void collectArticleSuccess();//收藏文章成功
        void unCollectArticleSuccess();//取消收藏成功
        void collect();
        void refreshCollections(List<Integer> ids);//刷新文章列表中的收藏
        void autoRefresh();//自动刷新
    }

    interface Presenter extends IPresenter<View> {
        void loadWeChats(int pageNum, int id);//加载项目列表
        void loadMoreMoreWeChats(int pageNum, int id);//加载更多项目列表
        void collectArticle(int id);//收藏文章
        void unCollectArticle(int id);//取消收藏
    }

}
