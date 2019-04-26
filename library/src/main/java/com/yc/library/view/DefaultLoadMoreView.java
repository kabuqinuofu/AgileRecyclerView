package com.yc.library.view;

import android.content.Context;
import android.os.Build;
import android.support.v4.content.res.ResourcesCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yc.library.R;
import com.yc.library.listener.LoadMoreListener;

public class DefaultLoadMoreView extends RelativeLayout implements LoadMoreListener {

    private int mState;
    private TextView mNoticeIconView;
    private TextView mNoticeTextView;
    private ProgressBar mProgressView;

    public DefaultLoadMoreView(Context context) {
        this(context, null);
    }

    public DefaultLoadMoreView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DefaultLoadMoreView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View.inflate(context, getLayoutId(), this);
        mProgressView = findViewById(R.id.load_notice_pb);
        mNoticeIconView = findViewById(R.id.load_notice_icon);
        mNoticeTextView = findViewById(R.id.load_notice_tv);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            mNoticeTextView.setTextColor(ResourcesCompat.getColor(getResources(), android.R.color.darker_gray, null));
            mNoticeIconView.setTextColor(ResourcesCompat.getColor(getResources(), android.R.color.darker_gray, null));
        }
        setState(LoadMoreListener.STATE_NORMAL);
    }

    protected int getLayoutId() {
        return R.layout.layout_default_load_more;
    }

    @Override
    public void setState(int state) {
        mState = state;
        if (LoadMoreListener.STATE_NORMAL == state) {
            super.setVisibility(View.GONE);
        } else {
            super.setVisibility(View.VISIBLE);
            if (LoadMoreListener.STATE_LOADING == state) {
                mProgressView.setVisibility(View.VISIBLE);
                mNoticeIconView.setVisibility(View.GONE);
                mNoticeTextView.setText(R.string.agile_label_loading);
            } else if (LoadMoreListener.STATE_FINISH == state) {
                mProgressView.setVisibility(View.GONE);
                mNoticeIconView.setVisibility(View.GONE);
                mNoticeTextView.setText(R.string.agile_label_finish);
            } else if (LoadMoreListener.STATE_ERROR == state) {
                mProgressView.setVisibility(View.GONE);
                mNoticeIconView.setVisibility(View.VISIBLE);
                mNoticeTextView.setText(R.string.agile_label_error);
            }
        }
    }

    @Override
    public int getState() {
        return mState;
    }

    @Override
    public View getView() {
        return this;
    }
}