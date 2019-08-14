package com.example.myapplication.viewgroup;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;

public class TestLayout extends LinearLayout {
    public TestLayout(Context context) {
        super(context);
    }

    public TestLayout(Context context,  AttributeSet attrs) {
        super(context, attrs);
    }

    public TestLayout(Context context,  AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.i("sun","Layout_onMeasure");

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.i("sun","Layout_onLayout");

        super.onLayout(changed, l, t, r, b);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.i("sun","Layout_onDraw");

        super.onDraw(canvas);
    }
}
