package com.yc.library.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

public interface AgileInnerAdapter<VH extends RecyclerView.ViewHolder> {

    void onViewAttachedToWindow(@NonNull VH holder, int innerPosition);

    void onViewDetachedFromWindow(@NonNull VH holder, int innerPosition);

    void onViewRecycled(@NonNull VH holder, int innerPosition);

    boolean onFailedToRecycleView(@NonNull VH holder, int innerPosition);
}
