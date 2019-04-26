package com.yc.library.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.ViewGroup;

import com.yc.library.holder.HeaderViewHolder;
import com.yc.library.listener.AgileViewCallback;

import java.util.List;

public class AgileWrapperAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private AgileViewCallback mAgileView;

    private final RecyclerView.Adapter mInnerAdapter;

    private int VIEW_TYPE_HEADER = Integer.MAX_VALUE / 2;
    private int VIEW_TYPE_FOOTER = VIEW_TYPE_HEADER - 1;

    public AgileWrapperAdapter(@NonNull RecyclerView.Adapter adapter, @NonNull AgileViewCallback agileViewView) {
        this.mInnerAdapter = adapter;
        this.mAgileView = agileViewView;
    }

    /**
     * 当HeaderView更换的时候 需要刷新VIEW_TYPE_HEADER 否则不会重新调用onCreateViewHolder
     */
    public void refreshHeaderType() {
        VIEW_TYPE_HEADER++;
    }

    /**
     * 当FooterView更换的时候 需要刷新VIEW_TYPE_FOOTER 否则不会重新调用onCreateViewHolder
     */
    public void refreshFooterType() {
        VIEW_TYPE_FOOTER--;
    }

    @NonNull
    public RecyclerView.Adapter getInnerAdapter() {
        return mInnerAdapter;
    }

    @Override
    public int getItemViewType(int position) {
        if (isHeader(position)) {
            return VIEW_TYPE_HEADER;
        } else if (isFooter(position)) {
            return VIEW_TYPE_FOOTER;
        } else {
            return mInnerAdapter.getItemViewType(getInnerPosition(position));
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_HEADER) {
            return new HeaderViewHolder(mAgileView.getHeaderView());
        } else if (viewType == VIEW_TYPE_FOOTER) {
            return new HeaderViewHolder(mAgileView.getFooterView());
        } else {
            return mInnerAdapter.onCreateViewHolder(parent, viewType);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (isInner(position) && isInnerHolder(holder)) {
            mInnerAdapter.onBindViewHolder(holder, getInnerPosition(position), payloads);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (isInner(position) && isInnerHolder(holder)) {
            mInnerAdapter.onBindViewHolder(holder, getInnerPosition(position));
        }
    }

    @Override
    public int getItemCount() {
        return mInnerAdapter.getItemCount() + mAgileView.getHeaderCount() + mAgileView.getFooterCount();
    }

    public boolean isHeader(int position) {
        final int headerCount = mAgileView.getHeaderCount();
        if (headerCount > 0) {
            return position < headerCount;
        }
        return false;
    }

    public boolean isFooter(int position) {
        final int footerCount = mAgileView.getFooterCount();
        if (footerCount > 0) {
            return position >= this.getItemCount() - footerCount;
        } else {
            return false;
        }
    }

    protected boolean isInner(int position) {
        return !isHeader(position) && !isFooter(position);
    }

    protected boolean isInnerHolder(@NonNull RecyclerView.ViewHolder holder) {
        return !(holder instanceof HeaderViewHolder);
    }


    /**
     * 通过实际位置或者内部位置
     *
     * @param position
     * @return
     */
    protected int getInnerPosition(int position) {
        return position - mAgileView.getHeaderCount();
    }

    /**
     * 通过内部位置获取实际位置
     *
     * @param innerPosition
     * @return
     */
    public int getWrapperPosition(int innerPosition) {
        return innerPosition + mAgileView.getHeaderCount();
    }

    @Override
    public long getItemId(int position) {
        if (isInner(position)) {
            return mInnerAdapter.getItemId(getInnerPosition(position));
        } else {
            return super.getItemId(position);
        }
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mInnerAdapter.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        mInnerAdapter.onDetachedFromRecyclerView(recyclerView);
    }

    @Override
    public void onViewAttachedToWindow(@NonNull final RecyclerView.ViewHolder holder) {
        final int layoutPosition = holder.getLayoutPosition();
        if (isInnerHolder(holder)) {
            //如果有实现接口 就回调带内部position的方法
            if (mInnerAdapter instanceof AgileInnerAdapter) {
                ((AgileInnerAdapter) mInnerAdapter).onViewAttachedToWindow(holder, getInnerPosition(layoutPosition));
            } else {
                mInnerAdapter.onViewAttachedToWindow(holder);
            }
        } else {
            //兼容瀑布流
            ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
            if (lp instanceof StaggeredGridLayoutManager.LayoutParams) {
                StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
                p.setFullSpan(true);
            }
            super.onViewAttachedToWindow(holder);
        }
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull final RecyclerView.ViewHolder holder) {
        final int layoutPosition = holder.getLayoutPosition();
        if (isInnerHolder(holder)) {
            //如果有实现接口 就回调带内部position的方法
            if (mInnerAdapter instanceof AgileInnerAdapter) {
                ((AgileInnerAdapter) mInnerAdapter).onViewDetachedFromWindow(holder, getInnerPosition(layoutPosition));
            } else {
                mInnerAdapter.onViewDetachedFromWindow(holder);
            }
        } else {
            super.onViewDetachedFromWindow(holder);
        }
    }

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        final int layoutPosition = holder.getLayoutPosition();
        if (isInnerHolder(holder)) {
            //如果有实现接口 就回调带内部position的方法
            if (mInnerAdapter instanceof AgileInnerAdapter) {
                ((AgileInnerAdapter) mInnerAdapter).onViewRecycled(holder, getInnerPosition(layoutPosition));
            } else {
                mInnerAdapter.onViewRecycled(holder);
            }
        } else {
            super.onViewRecycled(holder);
        }
    }

    @Override
    public boolean onFailedToRecycleView(@NonNull RecyclerView.ViewHolder holder) {
        final int layoutPosition = holder.getLayoutPosition();
        if (isInnerHolder(holder)) {
            //如果有实现接口 就回调带内部position的方法
            if (mInnerAdapter instanceof AgileInnerAdapter) {
                return ((AgileInnerAdapter) mInnerAdapter).onFailedToRecycleView(holder, getInnerPosition(layoutPosition));
            } else {
                return mInnerAdapter.onFailedToRecycleView(holder);
            }
        } else {
            return super.onFailedToRecycleView(holder);
        }
    }

}