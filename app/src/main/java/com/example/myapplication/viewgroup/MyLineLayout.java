package com.example.myapplication.viewgroup;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * 练习用 ViewGroup，实现一个简单的垂直 Layout
 * 1、先测量自生
 */
public class MyLineLayout extends ViewGroup {
    public MyLineLayout(Context context) {
        super(context);
    }

    public MyLineLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyLineLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 首先测量自身
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int startTop = getPaddingTop();
        int endBottom = getPaddingBottom();
        int reallWidth = 0;
        int realHeight = startTop + endBottom;

        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            // 获取子View 的测量尺寸，垂直的主要是获取高度。
            int childHeight = child.getMeasuredHeight();
            realHeight = realHeight + childHeight;
        }

        // 如果自身指定了尺寸，直接指定+match_parent
        if (heightMode == MeasureSpec.EXACTLY) {
            realHeight = heightSize;
        }
        // 不能大于父容器的尺寸 warp_content
        if (heightMode == MeasureSpec.AT_MOST) {
            if (realHeight > heightSize) {
                realHeight = heightSize;
            }
        }

        // 最后设置最后计算的 子View
        setMeasuredDimension(widthSize, realHeight);

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        // 获取总高度
        int realHeight = getMeasuredHeight();
        int left = l;
        int top = getPaddingTop();
        int right = r;
        int bottom = b;

        // 已经使用的高度
        int useHeight = top;

        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);

            // 获取子Viw宽高
            int height = child.getMeasuredHeight();
            useHeight = useHeight + height;
            child.layout(left, useHeight - height, right, useHeight);
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
