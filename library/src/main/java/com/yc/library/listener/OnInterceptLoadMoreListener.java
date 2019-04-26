package com.yc.library.listener;

public interface OnInterceptLoadMoreListener {

    /**
     * @return true=拦截 不继续执行 false=不拦截 执行加载更多
     */
    boolean onInterceptLoadMore();
}