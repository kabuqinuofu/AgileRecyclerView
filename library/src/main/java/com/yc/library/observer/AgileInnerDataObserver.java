package com.yc.library.observer;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import com.yc.library.adapter.AgileWrapperAdapter;
import com.yc.library.listener.OnItemCountChangedListener;

/**
 * 给InnerAdapter注册 通过WrapperAdapter更新Inner
 * InnerAdapter调用notify进入到这里
 */
public class AgileInnerDataObserver extends RecyclerView.AdapterDataObserver {

    private int mLastItemCount = -1;
    private AgileWrapperAdapter mWrapperAdapter;
    private OnItemCountChangedListener mOnItemCountChangedListener;

    public AgileInnerDataObserver(@NonNull AgileWrapperAdapter wrapperAdapter) {
        this.mWrapperAdapter = wrapperAdapter;
    }

    public void setOnItemCountChangedListener(OnItemCountChangedListener listener) {
        this.mOnItemCountChangedListener = listener;
    }

    @Override
    public void onChanged() {
        mWrapperAdapter.notifyDataSetChanged();
        callItemCountChanged();
    }


    @Override
    public void onItemRangeInserted(int positionStart, int itemCount) {
        //转换为实际位置 再刷新
        mWrapperAdapter.notifyItemRangeInserted(mWrapperAdapter.getWrapperPosition(positionStart), itemCount);
        callItemCountChanged();
    }

    @Override
    public void onItemRangeChanged(int positionStart, int itemCount) {
        mWrapperAdapter.notifyItemRangeChanged(mWrapperAdapter.getWrapperPosition(positionStart), itemCount);
    }

    @Override
    public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
        mWrapperAdapter.notifyItemRangeChanged(mWrapperAdapter.getWrapperPosition(positionStart), itemCount, payload);
    }

    @Override
    public void onItemRangeRemoved(int positionStart, int itemCount) {
        mWrapperAdapter.notifyItemRangeRemoved(mWrapperAdapter.getWrapperPosition(positionStart), itemCount);
        callItemCountChanged();
    }

    @Override
    public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
        mWrapperAdapter.notifyItemMoved(mWrapperAdapter.getWrapperPosition(fromPosition), mWrapperAdapter.getWrapperPosition(toPosition));
    }

    protected void callItemCountChanged() {
        if (mOnItemCountChangedListener != null) {
            //数量发生变化——>触发回调
            final int innerItemCount = mWrapperAdapter.getInnerAdapter().getItemCount();
            if (innerItemCount != mLastItemCount) {
                this.mLastItemCount = innerItemCount;
                mOnItemCountChangedListener.onItemCountChanged(innerItemCount);
            }
        }
    }
}