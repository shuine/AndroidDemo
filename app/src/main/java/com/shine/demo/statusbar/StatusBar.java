package com.shine.demo.statusbar;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.shine.demo.R;

import java.util.List;

/**
 * @author yeshuxin on 16-11-8.
 */

public class StatusBar extends FrameLayout{

    private StatusView mStatusView;
    private FrameLayout mTitleLayout;
    private List<String> mTitleList;

    private int mProgress = -1;
    private Context mContext;
    private final float TEXT_SIZE = 11;

    private int FINISHED_COLOR = getResources().getColor(R.color.service_detail_status_finished_color);
    private int UNFINISHED_COLOR = getResources().getColor(R.color.service_detail_status_unfinished_color);

    public StatusBar(Context context) {
        this(context, null);
    }

    public StatusBar(Context context, AttributeSet attributeSet) {

        this(context, attributeSet, 0);
    }

    public StatusBar(Context context, AttributeSet attributeSet, int defStyle) {

        super(context, attributeSet, defStyle);
        mContext = context;
        initView();
    }
    private void initView() {
        LayoutInflater.from(mContext).inflate(R.layout.status_progress_view, this, true);
        mStatusView = (StatusView) findViewById(R.id.status_progress_bar);
        mTitleLayout = (FrameLayout) findViewById(R.id.status_progress_title);

        mStatusView.setLineHeight(1);
        mStatusView.setSmallRadius(15);
        mStatusView.setLargeRadius(mStatusView.DEFAULT_LARGE_CIRCLE_RADIUS);
        mStatusView.setUnDoneColor(UNFINISHED_COLOR);
        mStatusView.setDoneColor(FINISHED_COLOR);
        mStatusView.setStatusProgress(0);
        mStatusView.setStatusCount(4);

        mStatusView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                initStatusTitle();
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    mStatusView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    mStatusView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            }
        });
    }
    private void initStatusTitle() {
        if (mTitleList == null) {
            return;
        }
        mTitleLayout.removeAllViews();
        if (mTitleList.size() != mStatusView.getStatusCount()) {
            throw new IllegalStateException("设置的Title的个数和步骤数不一致！");
        }
        int stepNum = mStatusView.getStatusCount();
        for (int i = 1; i <= stepNum; i++) {
            final float stepPos = mStatusView.getPositionByProgress(i);
            final TextView title = new TextView(this.getContext());
            if (i <= mProgress) {
                title.setTextColor(FINISHED_COLOR);
            } else {
                title.setTextColor(UNFINISHED_COLOR);
            }
            title.setText(mTitleList.get(i - 1));
            title.setTextSize(TypedValue.COMPLEX_UNIT_SP,TEXT_SIZE);
            title.setSingleLine();
            title.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    title.setTranslationX(stepPos - title.getMeasuredWidth() / 2);
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                        title.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    } else {
                        title.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                }
            });
            LayoutParams lp = new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            mTitleLayout.addView(title, lp);
        }
    }

    public void setStatusTitle(List<String> title) {
        this.mTitleList = title;
    }

    public void setProgress(int progress) {

        if (progress > -1 && progress < mTitleList.size()) {
            mProgress = progress;
        }
        mStatusView.setStatusProgress(progress);
        invalidate();
    }
}
