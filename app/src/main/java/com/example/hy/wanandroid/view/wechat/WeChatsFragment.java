package com.example.hy.wanandroid.view.wechat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;

import com.example.commonlib.utils.AnimUtil;
import com.example.commonlib.utils.CommonUtil;
import com.example.hy.wanandroid.R;
import com.example.hy.wanandroid.adapter.ArticlesAdapter;
import com.example.hy.wanandroid.adapter.WeChatAdapter;
import com.example.hy.wanandroid.base.fragment.BaseLoadFragment;
import com.example.hy.wanandroid.bean.ArticleBean;
import com.example.hy.wanandroid.config.Constant;
import com.example.hy.wanandroid.config.User;
import com.example.hy.wanandroid.contract.wechat.WeChatsContract;
import com.example.hy.wanandroid.di.module.fragment.WeChatsFragmentModule;
import com.example.hy.wanandroid.model.network.entity.Article;
import com.example.hy.wanandroid.presenter.wechat.WeChatsPresenter;
import com.example.hy.wanandroid.view.MainActivity;
import com.example.hy.wanandroid.view.homepager.ArticleActivity;
import com.example.hy.wanandroid.view.mine.LoginActivity;
import com.example.hy.wanandroid.widget.popup.PressPopup;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.util.List;

import javax.inject.Inject;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import dagger.Lazy;

import static android.app.Activity.RESULT_OK;

/**
 * WeChatsFragment
 * Created by 陈健宇 at 2018/12/19
 */
public class WeChatsFragment extends BaseLoadFragment<WeChatsPresenter> implements WeChatsContract.View {

    @BindView(R.id.rv_wechats)
    RecyclerView rvWechats;
    @BindView(R.id.normal_view)
    SmartRefreshLayout srlWeChats;

    @Inject
    WeChatsPresenter mPresenter;
    @Inject
    LinearLayoutManager mLinearLayoutManager;
    @Inject
    WeChatAdapter mArticlesAdapter;
    @Inject
    List<Article> mArticles;
    @Inject
    Lazy<PressPopup> mPopupWindow;

    private int mPageNum = 1;
    private int mId;
    private boolean isLoadMore = false;
    private int mArticlePosition = 0;//点击的位置
    private Article mArticle;
    private boolean isPress = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if(bundle != null){
            mId = bundle.getInt(Constant.KEY_WECHAT_ID, -1);
        }
    }

    @Override
    protected WeChatsPresenter getPresenter() {
        return mPresenter;
    }

    @Override
    protected void inject() {
        if (!(getActivity() instanceof MainActivity)) return;
        ((MainActivity) getActivity()).getComponent().getWeChatsFragmentComponent(new WeChatsFragmentModule()).inject(this);
    }

    @Override
    protected void initView() {
        super.initView();
        initRecyclerView();
        initRefreshView();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initRecyclerView() {
        //项目列表
        rvWechats.setLayoutManager(mLinearLayoutManager);
        mArticlesAdapter.openLoadAnimation();
        rvWechats.setAdapter(mArticlesAdapter);
        mArticlesAdapter.setOnItemClickListener((adapter, view, position) -> {//跳转文章
            mArticlePosition = position;
            mArticle =  mArticles.get(position);
            ArticleBean articleBean = new ArticleBean(mArticle);
            ArticleActivity.startActicityForResultByFragment(mActivity, this, articleBean, false, Constant.REQUEST_REFRESH_ARTICLE);
        });
        mArticlesAdapter.setOnItemChildClickListener((adapter, view, position) -> {//收藏
            mArticlePosition = position;
            mArticle =  mArticles.get(position);
            if(!User.getInstance().isLoginStatus()){
                LoginActivity.startActivityForResultByFragment(mActivity, this, Constant.REQUEST_COLLECT_ARTICLE);
                showToast(getString(R.string.first_login));
                return;
            }
            collect();
            AnimUtil.scale(view, -1);
        });
        mArticlesAdapter.setOnItemLongClickListener((adapter, view, position) -> {
            Article article = mArticles.get(position);
            view.setOnTouchListener((v, event) -> {
                if(event.getAction() == MotionEvent.ACTION_UP && isPress){
                    mPopupWindow.get().show(srlWeChats, event.getRawX(), event.getRawY());
                    mPopupWindow.get().setMessage(article.getTitle(), article.getLink());
                    isPress = false;
                }
                return false;
            });
            isPress = true;
            return true;
        });
    }

    private void initRefreshView() {
        srlWeChats.setOnLoadMoreListener(refreshLayout -> {
            mPageNum++;
            mPresenter.loadMoreMoreWeChats(mPageNum, mId);
            isLoadMore = true;
        });
        srlWeChats.setOnRefreshListener(refreshLayout -> {
            mPresenter.loadMoreMoreWeChats(1, mId);
            isLoadMore = false;
        });
    }

    @Override
    protected void loadData() {
        mPresenter.subscribleEvent();
        mPresenter.loadWeChats(1, mId);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_wechats;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != RESULT_OK) return;
        Article article = mArticles.get(mArticlePosition);
        switch (requestCode){
            case Constant.REQUEST_COLLECT_ARTICLE:
                if(article.isCollect()) mPresenter.unCollectArticle(article.getId());
                else mPresenter.collectArticle(article.getId());
                break;
            case Constant.REQUEST_REFRESH_ARTICLE:
                boolean isCollect = data.getBooleanExtra(Constant.KEY_DATA_RETURN, false);
                if(article.isCollect() != isCollect){
                    article.setCollect(isCollect);
                    mArticlesAdapter.notifyItemChanged(mArticlePosition + mArticlesAdapter.getHeaderLayoutCount());
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void reLoad() {
        super.reLoad();
        mPresenter.loadWeChats(1, mId);
    }

    @Override
    public void showWeChats(List<Article> articleList) {
        if(!CommonUtil.isEmptyList(mArticles)) mArticles.clear();
        mArticles.addAll(articleList);
        mArticlesAdapter.notifyDataSetChanged();
    }

    @Override
    public void showMoreWeChats(List<Article> articleList) {
        if(isLoadMore){
            srlWeChats.finishLoadMore();
        }else {
            if(!CommonUtil.isEmptyList(mArticles)) mArticles.clear();
            srlWeChats.finishRefresh();
        }
        mArticles.addAll(articleList);
        mArticlesAdapter.notifyDataSetChanged();
    }

    @Override
    public void topping() {
        if(rvWechats != null) rvWechats.smoothScrollToPosition(0);
    }

    @Override
    public void collectArticleSuccess() {
        showToast(getString(R.string.common_collection_success));
        mArticles.get(mArticlePosition).setCollect(true);
        mArticlesAdapter.notifyItemChanged(mArticlePosition + mArticlesAdapter.getHeaderLayoutCount());
    }

    @Override
    public void unCollectArticleSuccess() {
        showToast(getString(R.string.common_uncollection_success));
        mArticles.get(mArticlePosition).setCollect(false);
        mArticlesAdapter.notifyItemChanged(mArticlePosition + mArticlesAdapter.getHeaderLayoutCount());
    }

    @Override
    public void collect() {
        if (mArticle == null) return;
        if(mArticle.isCollect())
            mPresenter.unCollectArticle(mArticle.getId());
        else
            mPresenter.collectArticle(mArticle.getId());
    }

    @Override
    public void refreshCollections(List<Integer> ids) {
        for(int i = 0; i < ids.size(); i++){
            for(int j = 0; j < mArticles.size(); j++){
                if(mArticles.get(j).getId() == ids.get(i)){
                    mArticles.get(j).setCollect(false);
                    mArticlesAdapter.notifyItemChanged(j + mArticlesAdapter.getHeaderLayoutCount());
                    break;
                }
            }
        }
    }

    @Override
    public void autoRefresh() {
        srlWeChats.autoRefresh();
    }

    public static Fragment newInstance(int id){
        Bundle bundle = new Bundle();
        bundle.putInt(Constant.KEY_WECHAT_ID, id);
        Fragment fragment = new WeChatsFragment();
        fragment.setArguments(bundle);
        return fragment;
    }
}
