package com.example.myapplication.viewgroup;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.PointF;
import android.support.v4.os.ConfigurationCompat;
import android.support.v4.view.ViewConfigurationCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

/**
 * 一个流式标签 View
 * 1. 2019、08、19 添加 Y 轴滑动。
 */
public class TagFlowView extends ViewGroup {

    /**
     * 滑动辅助类
     */
    private Scroller mScroller;
    /**
     * 第一次按下的坐标
     */
    private PointF downPoint;

    /**
     * 判定拖动的最小像素值
     */
    private float mTouchSlop;

    /**
     * view 的高度
     */
    private int vieHeight;


    public TagFlowView(Context context) {
        super(context);
        init(context);
    }

    public TagFlowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context){
        mScroller = new Scroller(context);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
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
            case MeasureSpec.EXACTLY:
                realHeight = heightSize;
                break;
            // 若是包裹内容
            case MeasureSpec.AT_MOST:
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
        vieHeight = width;
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

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int eventKey = ev.getAction();
        // 判断是何种事件
        switch (eventKey){
            case MotionEvent.ACTION_DOWN:
                // 记下按下的屏幕坐标
                if (downPoint == null){
                    downPoint = new PointF();
                }
                downPoint.x = ev.getRawX();
                downPoint.y = ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                float moveY = ev.getRawY();
                float moveSlop = Math.abs(moveY - downPoint.y);
                if (moveSlop > mTouchSlop){
                    return true;
                }
                break;
        }

        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int eventKey = event.getAction();
        switch (eventKey){
            case MotionEvent.ACTION_MOVE:
                float moveY = event.getRawY();
                Log.e("sun",moveY+"-"+downPoint.y+"-"+(downPoint.y - moveY));
                Log.e("sun","TOP-"+getScrollY());
                // 判断滑动的距离不能大于 view 的
                int viewTopY = getScrollY();
                // 用负号因为原始坐标是正常的坐标系
                int moveDis = (int) (downPoint.y - moveY);

                overScrollBy(0,(int) event.getRawY(),0,getScrollY(),0,getScrollRange(),0,moveDis,true);
//                mScroller.startScroll(0,(int) downPoint.y,0,(int) event.getRawY());
                // 记录最后一次的坐标
                downPoint.x = event.getRawX();
                downPoint.y = event.getRawY();
                return true;
            case MotionEvent.ACTION_UP:
                // 当快速滑动时，使用 scroller 完成流程滑动
//                mScroller.startScroll(0,(int) (downPoint.y),0,(int) (event.getRawY()));
                break;
        }
        return super.onTouchEvent(event);
    }

    private int getScrollRange() {
        int scrollRange = 0;
        if (getChildCount() > 0) {
            View child = getChildAt(0);
            scrollRange = Math.max(0,
                    child.getHeight() - (getHeight() - getPaddingBottom() - getPaddingTop()));
        }
        return scrollRange;
    }


    @Override
    public void computeScroll() {
        // 第三步，重写computeScroll()方法，并在其内部完成平滑滚动的逻辑
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            invalidate();
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
