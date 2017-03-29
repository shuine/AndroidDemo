package com.shine.demo.scratch;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.shine.demo.R;

/**
 * @author yeshuxin on 17-3-27.
 */

public class ScrathView extends View {

    /**
     * 绘制线条的Paint,即用户手指绘制Path
     */
    private Paint mOutterPaint = new Paint();
    /**
     * 记录用户绘制的Path
     */
    private Path mPath = new Path();
    /**
     * 内存中创建的Canvas
     */
    private Canvas mCanvas;
    /**
     * mCanvas绘制内容在其上
     */
    private Bitmap mBitmap;
    private Bitmap mBitBack;
    private Paint mTextPaint = new Paint();
    private Rect mTextBound = new Rect();

    private int mLastX;
    private int mLastY;

    public ScrathView(Context context) {

        this(context, null);
    }

    public ScrathView(Context context, AttributeSet attri) {

        this(context, attri, 0);
    }

    public ScrathView(Context context, AttributeSet attri, int defStyle) {
        super(context, attri, defStyle);
    }

    private void init()
    {
        mPath = new Path();
        mBitBack = BitmapFactory.decodeResource(getResources(), R.drawable.scrath_bg);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        // 初始化bitmap
        mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
        // 设置画笔
        mOutterPaint.setColor(Color.RED);
        mOutterPaint.setAntiAlias(true);
        mOutterPaint.setDither(true);
        mOutterPaint.setStyle(Paint.Style.STROKE);
        mOutterPaint.setStrokeJoin(Paint.Join.ROUND); // 圆角
        mOutterPaint.setStrokeCap(Paint.Cap.ROUND); // 圆角

        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setTextScaleX(2f);
        mTextPaint.setColor(Color.DKGRAY);
        mTextPaint.setTextSize(22);
        mTextPaint.getTextBounds("恭喜你获得优惠券", 0, 8, mTextBound);
        // 设置画笔宽度
        mOutterPaint.setStrokeWidth(20);

        mCanvas.drawColor(Color.parseColor("#c0c0c0"));
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
//        canvas.drawBitmap(mBitBack,0,0,null);
        canvas.drawText("恭喜你获得优惠券",this.getWidth()/2,this.getHeight()/2,mTextPaint);
        drawPath();
        canvas.drawBitmap(mBitmap, 0, 0, null);

    }

    /**
     * 绘制线条
     */
    private void drawPath()
    {
        mOutterPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        mCanvas.drawPath(mPath, mOutterPaint);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        int action = event.getAction();
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (action)
        {
            case MotionEvent.ACTION_DOWN:
                mLastX = x;
                mLastY = y;
                mPath.moveTo(mLastX, mLastY);
                break;
            case MotionEvent.ACTION_MOVE:

                int dx = Math.abs(x - mLastX);
                int dy = Math.abs(y - mLastY);

                if (dx > 3 || dy > 3)
                    mPath.lineTo(x, y);

                mLastX = x;
                mLastY = y;
                break;
        }

        invalidate();
        return true;
    }


}
