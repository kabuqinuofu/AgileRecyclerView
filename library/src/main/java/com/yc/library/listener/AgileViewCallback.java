package com.yc.library.listener;

import android.view.View;

public interface AgileViewCallback {

    View getHeaderView();

    View getFooterView();

    int getHeaderCount();

    int getFooterCount();

    boolean isLoadMoreEnabled();
}
