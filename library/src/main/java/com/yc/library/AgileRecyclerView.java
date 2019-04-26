package com.yc.library;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.yc.library.adapter.AgileWrapperAdapter;
import com.yc.library.listener.AgileViewCallback;
import com.yc.library.listener.LoadMoreListener;
import com.yc.library.listener.OnInterceptLoadMoreListener;
import com.yc.library.listener.OnItemCountChangedListener;
import com.yc.library.listener.OnLoadMoreListener;
import com.yc.library.observer.AgileInnerDataObserver;
import com.yc.library.view.DefaultLoadMoreView;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class AgileRecyclerView extends RecyclerView implements OnItemCountChangedListener {

    private final int DEFAULT_AUTO_LOAD_LAST = 1;

    private View mHeaderView;
    private LoadMoreListener mLoadMoreListener;
    private AgileWrapperAdapter mWrapperAdapter;
    private OnLoadMoreListener mOnLoadMoreListener;
    private OnItemCountChangedListener mOnItemCountChangedListener;
    private OnInterceptLoadMoreListener mOnInterceptListener;

    //是否开启加载更多
    private boolean mLoadMoreEnabled;
    //是否已经加载到底
    private boolean mLoadMoreEnd;
    //加载更多是否已经完成
    private boolean mLoadMoreCompleted = true;

    //显示到倒数第mAutoLoadByLastCount条开始自动加载
    private int mAutoLoadByLastCount;

    private AgileViewCallback mAgileViewCallback = new AgileViewCallback() {
        @Override
        public View getHeaderView() {
            return mHeaderView;
        }

        @Override
        public View getFooterView() {
            return mLoadMoreListener == null ? null : mLoadMoreListener.getView();
        }

        @Override
        public int getHeaderCount() {
            return mHeaderView == null ? 0 : 1;
        }

        @Override
        public int getFooterCount() {
            return mLoadMoreEnabled && mLoadMoreListener != null ? 1 : 0;
        }

        @Override
        public boolean isLoadMoreEnabled() {
            return mLoadMoreEnabled;
        }
    };

    public AgileRecyclerView(@NonNull Context context) {
        this(context, null);
    }

    public AgileRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AgileRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AgileRecyclerView);
        mLoadMoreEnabled = a.getBoolean(R.styleable.AgileRecyclerView_loadMoreEnabled, true);
        mAutoLoadByLastCount = a.getInt(R.styleable.AgileRecyclerView_autoLoadByLastCount, DEFAULT_AUTO_LOAD_LAST);
        String loadMoreClassName = a.getString(R.styleable.AgileRecyclerView_loadMoreViewName);
        a.recycle();
        try {
            if (!TextUtils.isEmpty(loadMoreClassName)) {
                final Class<?> footerClass = Class.forName(loadMoreClassName);
                if (LoadMoreListener.class.isAssignableFrom(footerClass)) {
                    Constructor con = footerClass.getConstructor(Context.class);
                    mLoadMoreListener = (LoadMoreListener) con.newInstance(context);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (mLoadMoreListener == null) {
            mLoadMoreListener = new DefaultLoadMoreView(context, attrs, defStyle);
        }
    }

    @Override
    public void setLayoutManager(@Nullable LayoutManager layout) {
        if (layout instanceof GridLayoutManager) {
            final GridLayoutManager gridManager = ((GridLayoutManager) layout);
            gridManager.setSpanSizeLookup(new AgileSpanSizeLookup(gridManager) {
                @Override
                public boolean isHeaderOrFooter(int position) {
                    return mWrapperAdapter != null && (mWrapperAdapter.isHeader(position) || mWrapperAdapter.isFooter(position));
                }
            });
        }
        super.setLayoutManager(layout);
    }

    @Override
    public void setAdapter(@Nullable Adapter adapter) {
        if (adapter == null) {
            super.setAdapter(null);
        } else {
            mWrapperAdapter = new AgileWrapperAdapter(adapter, mAgileViewCallback);
            final AgileInnerDataObserver observer = new AgileInnerDataObserver(mWrapperAdapter);
            observer.setOnItemCountChangedListener(this);
            adapter.registerAdapterDataObserver(observer);
            super.setAdapter(mWrapperAdapter);
        }
    }

    @Nullable
    public Adapter getInnerAdapter() {
        return mWrapperAdapter == null ? null : mWrapperAdapter.getInnerAdapter();
    }

    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
        //如果加载更多是开启的/处于空闲状态/数据没有到底/也有监听 就检查是否可以加载更多
        if (mLoadMoreEnabled && mLoadMoreCompleted && !mLoadMoreEnd && mOnLoadMoreListener != null) {
            //拦截事件通过 加载更多
            if (mOnInterceptListener == null || !mOnInterceptListener.onInterceptLoadMore()) {
                LayoutManager lm = super.getLayoutManager();
                if (lm != null && state == RecyclerView.SCROLL_STATE_IDLE) {
                    int lastVisibleItemPosition = findLastVisibleItemPosition(lm);
                    int itemCount = lm.getItemCount();

                    //如果最后显示的位置符合 加载更多也处于空闲状态 就回调加载更多
                    final int footerCount = mAgileViewCallback.getFooterCount();
                    if (itemCount > 0 && lastVisibleItemPosition >= itemCount - footerCount - mAutoLoadByLastCount) {
                        this.mLoadMoreCompleted = false;
                        this.setLoadMoreState(LoadMoreListener.STATE_LOADING);
                        this.mOnLoadMoreListener.onLoadMore();
                    }
                }
            }
        }
    }

    @Override
    public void onItemCountChanged(int itemCount) {
        if (mOnItemCountChangedListener != null) {
            mOnItemCountChangedListener.onItemCountChanged(itemCount);
        }
    }

    public void setHeaderView(View headerView) {
        this.mHeaderView = headerView;
        if (mWrapperAdapter != null) {
            mWrapperAdapter.refreshHeaderType();
            mWrapperAdapter.notifyDataSetChanged();
        }
    }

    public void removeHeaderView() {
        this.mHeaderView = null;
        if (mWrapperAdapter != null) {
            mWrapperAdapter.notifyDataSetChanged();
        }
    }

    public void setLoadMoreView(LoadMoreListener loadMoreView) {
        this.mLoadMoreListener = loadMoreView;
        if (mWrapperAdapter != null) {
            mWrapperAdapter.refreshFooterType();
            mWrapperAdapter.notifyDataSetChanged();
        }
    }

    public void setAutoLoadByLastCount(int autoLoadByLastCount) {
        this.mAutoLoadByLastCount = autoLoadByLastCount;
    }

    public void setLoadMoreEnabled(boolean enabled) {
        this.mLoadMoreEnabled = enabled;
    }

    public void setLoadMoreCompleted() {
        this.mLoadMoreCompleted = true;
    }

    public boolean isLoadMoreCompleted() {
        return mLoadMoreCompleted;
    }

    public void setLoadMoreState(@IntRange(from = LoadMoreListener.STATE_NORMAL, to = LoadMoreListener.STATE_FINISH) int state) {
        if (mLoadMoreListener != null) {
            mLoadMoreListener.setState(state);
        }
        if (LoadMoreListener.STATE_FINISH == state) {
            mLoadMoreEnd = true;
        }
    }

    public void resetLoadMoreState() {
        setLoadMoreState(LoadMoreListener.STATE_NORMAL);
        mLoadMoreCompleted = true;
        mLoadMoreEnd = false;
    }

    public int getLoadMoreState() {
        if (mLoadMoreListener != null) {
            return mLoadMoreListener.getState();
        }
        return LoadMoreListener.STATE_NORMAL;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener listener) {
        this.mOnLoadMoreListener = listener;
    }

    public void setOnItemCountChangedListener(OnItemCountChangedListener listener) {
        this.mOnItemCountChangedListener = listener;
    }

    public void setOnInterceptLoadMoreListener(OnInterceptLoadMoreListener listener) {
        this.mOnInterceptListener = listener;
    }

    /**
     * 查找最后一个显示的位置
     *
     * @param lm
     * @return
     */
    protected int findLastVisibleItemPosition(LayoutManager lm) {
        if (lm instanceof LinearLayoutManager) {
            return ((LinearLayoutManager) lm).findLastVisibleItemPosition();
        } else if (lm instanceof StaggeredGridLayoutManager) {
            final int[] lastPositions = ((StaggeredGridLayoutManager) lm).findLastVisibleItemPositions(null);
            return lastPositions[lastPositions.length - 1];
        } else if (lm.getClass().getName().equals("com.google.android.flexbox.FlexboxLayoutManager")) {
            try {
                final Method flexboxMethod = lm.getClass().getMethod("findLastVisibleItemPosition");
                return (int) flexboxMethod.invoke(lm);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return 0;
        } else {
            return 0;
        }
    }
}