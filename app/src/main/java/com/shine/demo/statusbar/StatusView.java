package com.shine.demo.statusbar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.shine.demo.R;


/**
 * @author yeshuxin on 16-11-8.
 */

public class StatusView extends View {

    private float mCenterY = 0.0f;
    private float mLeftX = 0.0f;
    private float mLeftY = 0.0f;
    private float mRightX = 0.0f;
    private float mRightY = 0.0f;
    private float mDistance = 0.0f;

    private float mLineHeight = 0f;
    private float mSmallRadius = 0f;
    private float mLargeRadius = 0f;

    private int mStatusCount = 0;
    private int mProgress = 0;

    private int mUnDoneColor = getResources().getColor(R.color.service_detail_status_unfinished_color);
    private int mDoneColor = getResources().getColor(R.color.service_detail_status_finished_color);
    ;

    /**
     * 默认线条高度
     */
    public static final int DEFAULT_LINE_HEIGHT = 5;
    /**
     * 默认小圆的半径
     */
    public static final int DEFAULT_SMALL_CIRCLE_RADIUS = 10;
    /**
     * 默认大圆的半径
     */
    public static final int DEFAULT_LARGE_CIRCLE_RADIUS = 20;
    /**
     * 默认距离边缘的距离
     */
    public static final int DEFAULT_PADDING = 20;

    public StatusView(Context context) {
        this(context, null);
    }

    public StatusView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public StatusView(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);
    }

    public void setStatusCount(int count) {
        mStatusCount = count;
    }

    public int getStatusCount() {
        return mStatusCount;
    }

    public void setStatusProgress(int progress) {
        if (progress < 0 || progress > mStatusCount) {
            return;
        }
        mProgress = progress;
        invalidate();
    }

    public float getPositionByProgress(int progress) {
        if (progress < 1 || progress > mStatusCount) {
            return 0f;
        }
        return mLeftX + (progress - 1) * mDistance;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getDefaultWidth();
        if (MeasureSpec.UNSPECIFIED != MeasureSpec.getMode(widthMeasureSpec)) {
            width = MeasureSpec.getSize(widthMeasureSpec);
        }

        int height = 120;
        if (MeasureSpec.UNSPECIFIED != MeasureSpec.getMode(heightMeasureSpec)) {
            height = MeasureSpec.getSize(heightMeasureSpec);
        }
        setMeasuredDimension(width, height);

    }

    private int getDefaultWidth() {
        int screenWidth = this.getResources().getDisplayMetrics().widthPixels;
        return screenWidth - 2 * dp2px(DEFAULT_PADDING);
    }

    public int dp2px(int dp) {
        return (int) (this.getContext().getResources().getDisplayMetrics().density * dp + 0.5);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //计算位置
        mCenterY = this.getHeight() / 2;
        mLeftX = this.getLeft() + getPaddingLeft();
        mLeftY = mCenterY - mLineHeight / 2;
        mRightX = this.getRight() - getPaddingRight();
        mRightY = mCenterY + mLineHeight / 2;

        if (mStatusCount > 1) {
            mDistance = (mRightX - mLeftX) / (mStatusCount - 1);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mStatusCount <= 0 || mProgress < 0 || mProgress > mStatusCount) {
            return;
        }
        Paint mCirclePaint = new Paint();
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setStyle(Paint.Style.FILL);
        mCirclePaint.setColor(getResources().getColor(R.color.service_detail_status_unfinished_color));

        canvas.drawRect(mLeftX, mLeftY, mRightX, mRightY, mCirclePaint);
        float xLoc = mLeftX;
        //画所有的步骤(圆形)
        for (int i = 0; i < mStatusCount; i++) {
            canvas.drawCircle(xLoc, mLeftY, mSmallRadius, mCirclePaint);
            xLoc = xLoc + mDistance;
        }

        //画已经完成的步骤(圆形加矩形)
        xLoc = mLeftX;
        mCirclePaint.setColor(getResources().getColor(R.color.service_detail_status_finished_color));
        for (int i = 0; i < mProgress; i++) {
            if (i > 0) {
                canvas.drawRect(xLoc - mDistance, mLeftY, xLoc, mRightY, mCirclePaint);
            }
            canvas.drawCircle(xLoc, mLeftY, mSmallRadius, mCirclePaint);
            xLoc = xLoc + mDistance;

        }
    }

    public void setLineHeight(float mLineHeight) {
        this.mLineHeight = mLineHeight;
    }

    public void setSmallRadius(float mSmallRadius) {
        this.mSmallRadius = mSmallRadius;
    }

    public void setLargeRadius(float mLargeRadius) {
        this.mLargeRadius = mLargeRadius;
    }

    public void setUnDoneColor(int mUnDoneColor) {
        this.mUnDoneColor = mUnDoneColor;
    }

    public void setDoneColor(int mDoneColor) {
        this.mDoneColor = mDoneColor;
    }
}
