package com.example.myapplication.viewgroup;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

/**
 * 一个流式标签 View
 */
public class TagFlowView extends ViewGroup {

    public TagFlowView(Context context) {
        super(context);
    }

    public TagFlowView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 获取自身测量模式及尺寸数据
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int realWidth = 0;
        int realHeight = getPaddingTop() + getPaddingBottom();

        // 测量子View,根据子view及自身测量模式完成测量
        int childCount = getChildCount();

        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            // 测量 子View
            measureChild(child, widthMeasureSpec, heightMeasureSpec);

            // 获取子View的宽高
            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();

            // 根据 子 View 的数据计算自身的高度
            realHeight = realHeight + childHeight;
        }
        // 高度最终决定
        switch (heightMode) {
            // 若设置了精确数值
            case MeasureSpec.AT_MOST:
                realHeight = heightSize;
                break;
            // 若是包裹内容
            case MeasureSpec.EXACTLY:
                if (realHeight > heightSize) {
                    realHeight = heightSize;
                }
                break;
        }
        // 宽度最终决定
        switch (widthMode) {
            case MeasureSpec.AT_MOST:
                realWidth = widthSize;
                break;
            case MeasureSpec.EXACTLY:
                realWidth = widthSize;
                break;
        }
        // 设置最终宽高
        setMeasuredDimension(realWidth, realHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        int width = getMeasuredWidth();
        // 已经使用的高度

        // 下一行开始的 top 位置
        int nextTopPoint = getPaddingTop();

        // 已经使用的宽度
        int useWidth = getPaddingLeft();
        // 已经使用的高度
        int useHeight = getPaddingTop();

        int childCount = getChildCount();

        MarginLayoutParams marginLayoutParams;
        for (int i = 0;i < childCount;i++){
            View child = getChildAt(i);

            Log.e("sun",child.getLayoutParams().getClass().getName());
            marginLayoutParams = (MarginLayoutParams) child.getLayoutParams();

            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();

            int left = useWidth + marginLayoutParams.leftMargin;
            int top = nextTopPoint + marginLayoutParams.topMargin;
            int right = left + childWidth + marginLayoutParams.rightMargin;
            int bottom = top + childHeight + marginLayoutParams.bottomMargin;

            // 是否需要换行
            if (right > width){
                left = getPaddingLeft() + marginLayoutParams.leftMargin;
                top = top + childHeight + marginLayoutParams.topMargin;
                right = left + childWidth + marginLayoutParams.rightMargin;
                bottom = top + childHeight + marginLayoutParams.bottomMargin;
                nextTopPoint = top;
            }
            useWidth = right;


            Log.e("sun",i+"-"+top+"-"+right+"-"+width+"-"+nextTopPoint);
            child.layout(left,top,right,bottom);
        }
    }

    /**
     * 与当前ViewGroup对应的LayoutParams
     */
    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new MarginLayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
    }

    @Override
    protected LayoutParams generateLayoutParams(LayoutParams p) {
        return new MarginLayoutParams(p);
    }
}
