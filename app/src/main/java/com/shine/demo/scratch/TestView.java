package com.shine.demo.scratch;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * @author yeshuxin on 17-3-27.
 */

public class TestView extends View {

    private Paint mPaint = new Paint();
    private Path mPath = new Path();
    private Bitmap mBitmap;
    private Canvas mCanvas;

    private int mPointX;
    private int mPointY;
    private boolean isComplete;

    public TestView(Context context) {
        this(context, null);
    }

    public TestView(Context context, AttributeSet attri) {

        this(context, attri, 0);
    }

    public TestView(Context context, AttributeSet attri, int defStyle) {

        super(context, attri, defStyle);
        init();
    }

    private void init() {
        mPaint.setColor(Color.RED);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND); // 圆角
        mPaint.setStrokeCap(Paint.Cap.ROUND); // 圆角
        mPaint.setStrokeWidth(20f);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!isComplete) {
            drawPath();
            canvas.drawBitmap(mBitmap, 0, 0, null);
        }
    }

    private void drawPath() {
        mCanvas.drawColor(Color.parseColor("#c2c2c2"));
        mCanvas.drawPath(mPath, mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPointX = x;
                mPointY = y;
                mPath.moveTo(x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                if(mPointX == 0 && mPointY == 0){
                    mPointX = x;
                    mPointY = y;
                    mPath.moveTo(x, y);
                }
                if (Math.abs(x - mPointX) > 3 || Math.abs(y - mPointY) > 3) {
                    mPath.lineTo(x, y);
                }
                break;
            case MotionEvent.ACTION_UP:
                computeDrawPath();
                break;
        }
        invalidate();
        return true;
    }

    private void computeDrawPath() {
        int width = mBitmap.getWidth();
        int height = mBitmap.getHeight();
        float wipeArea = 0;
        float totalArea = width * height;
        int[] mPixels = new int[width * height];
        mBitmap.getPixels(mPixels, 0, width, 0, 0, width, height);
        for (int i = 0; i < mPixels.length; i++) {
            if (mPixels[i] == 0) {
                wipeArea++;
            }
        }

        if (wipeArea > 0 && totalArea > 0) {
            if (wipeArea > totalArea / 2) {
                isComplete = true;
                postInvalidate();
            }
        }
    }
}
