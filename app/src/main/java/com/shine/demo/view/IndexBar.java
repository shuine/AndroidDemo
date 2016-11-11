package com.shine.demo.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.shine.demo.R;

/**
 * TODO: document your custom view class.
 */
public class IndexBar extends View {
    private String[] DEFAULT_INDEX = {"#", "A", "B", "C", "D", "E", "F", "G", "H",
            "J", "K", "L", "M", "N", "P", "Q", "R", "S", "T", "W",
            "X", "Y", "Z"};
    private String[] mIndexs = DEFAULT_INDEX;
    private int mTextColor = 0xff424242;
    private float mTextSize = 12f;
    private float mPaddingTopBottom = 4f;
    private float mPaddingLeftRight = 4f;
    private TextPaint mTextPaint;

    private int choose = -1;
    private TextView mTextDialog;

    private OnIndexChangedListener mListener;

    public IndexBar(Context context) {
        super(context);
        init(null, 0);
    }

    public IndexBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public IndexBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.IndexBar, defStyle, 0);

        DisplayMetrics dm = getResources().getDisplayMetrics();
        mPaddingLeftRight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mPaddingLeftRight, dm);
        mPaddingTopBottom = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mPaddingTopBottom, dm);
        mTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, mTextSize, dm);

        mTextColor = a.getColor(R.styleable.IndexBar_android_textColor, mTextColor);
        mTextSize = a.getDimension(R.styleable.IndexBar_android_textSize, mTextSize);
        mPaddingTopBottom = a.getDimension(R.styleable.IndexBar_paddingTopBottom, mPaddingTopBottom);
        mPaddingLeftRight = a.getDimension(R.styleable.IndexBar_paddingLeftRight, mPaddingLeftRight);
        a.recycle();
        setVerticalScrollBarEnabled(false);
        // Set up a default TextPaint object
        mTextPaint = new TextPaint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        // Update TextPaint and text measurements from attributes
        invalidateTextPaintAndMeasurements();

    }

    private void invalidateTextPaintAndMeasurements() {
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setColor(mTextColor);
    }

    public void setIndex(String[] index) {
        this.mIndexs = index;
        requestLayout();
        invalidate();
    }

    public void setTextDialog(TextView textView) {
        this.mTextDialog = textView;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //MeasureSpec.
        int desiredWidth = (int) (mTextSize + mPaddingLeftRight * 2);
        int desiredHeight = (int) (mTextSize + mPaddingTopBottom * 2) * mIndexs.length;
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int width;
        int height;
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            width = Math.min(desiredWidth, widthSize);
        } else {
            width = desiredWidth;
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            height = Math.min(desiredHeight, heightSize);
        } else {
            height = heightSize;
        }

        setMeasuredDimension(width, height);

    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int height = getHeight();
        int width = getWidth();
        int singleHeight = height / mIndexs.length;// 获取每一个字母的高度
        for (int i = 0; i < mIndexs.length; i++) {
            float xPos = width / 2;
            float yPos = singleHeight * i + singleHeight / 2 - ((mTextPaint.descent() + mTextPaint.ascent()) / 2);
            canvas.drawText(mIndexs[i], xPos, yPos, mTextPaint);
        }

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        final float y = event.getY();// 点击y坐标
        final int oldChoose = choose;
        final OnIndexChangedListener listener = mListener;
        final int c = (int) (y / getHeight() * mIndexs.length);// 点击y坐标所占总高度的比例*b数组的长度就等于点击b中的个数.

        switch (action) {
            case MotionEvent.ACTION_UP:
                choose = -1;//
                if (mTextDialog != null) {
                    mTextDialog.setVisibility(View.INVISIBLE);
                }
                break;

            default:
                if (oldChoose != c) {
                    if (c >= 0 && c < mIndexs.length) {
                        if (listener != null) {
                            listener.onIndexChanged(c, mIndexs[c]);
                        }
                        if (mTextDialog != null) {
                            mTextDialog.setText(mIndexs[c]);
                            mTextDialog.setVisibility(View.VISIBLE);
                        }

                        choose = c;
                    }
                }

                break;
        }
        return true;
    }

    public void setOnIndexChangedListener(OnIndexChangedListener listener) {
        this.mListener = listener;
    }

    public interface OnIndexChangedListener {
        public void onIndexChanged(int position, String item);
    }
}
