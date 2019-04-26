package com.yc.library;

import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;

public abstract class AgileSpanSizeLookup extends GridLayoutManager.SpanSizeLookup {

    private int mSpanCount;
    private GridLayoutManager.SpanSizeLookup mInnerSizeLookup;

    public AgileSpanSizeLookup(@NonNull GridLayoutManager layoutManager) {
        this.mInnerSizeLookup = layoutManager.getSpanSizeLookup();
        this.mSpanCount = layoutManager.getSpanCount();
    }

    @Override
    public int getSpanSize(int position) {
        if (isHeaderOrFooter(position)) {
            return mSpanCount;
        } else {
            return mInnerSizeLookup == null ? 1 : mInnerSizeLookup.getSpanSize(position);
        }
    }

    public abstract boolean isHeaderOrFooter(int position);
}