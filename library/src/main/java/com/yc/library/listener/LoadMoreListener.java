package com.yc.library.listener;

import android.support.annotation.IntRange;
import android.view.View;

public interface LoadMoreListener {

    /**
     * 正常状态
     */
    int STATE_NORMAL = 0;
    /**
     * 正在加载状态
     */
    int STATE_LOADING = 1;
    /**
     * 加载失败状态
     */
    int STATE_ERROR = 2;
    /**
     * 加载完成状态
     */
    int STATE_FINISH = 3;

    void setState(@IntRange(from = LoadMoreListener.STATE_NORMAL, to = LoadMoreListener.STATE_FINISH) int state);

    @IntRange(from = LoadMoreListener.STATE_NORMAL, to = LoadMoreListener.STATE_FINISH)
    int getState();

    View getView();
}