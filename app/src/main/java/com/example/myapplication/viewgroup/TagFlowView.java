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
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.OverScroller;
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
    private int mOverscrollDistance;

    /**
     * view 内容的高度
     */
    private int viewContentHeight;
    /**
     * view 实际的高度
     */
    private int viewHeight;


    private VelocityTracker mVelocityTracker;

    // 系统给的最大触摸滑动速度
    private int mMaximumVelocity;
    // 系统给的最小触摸滑动速度
    private int mMinimumVelocity;


    public TagFlowView(Context context) {
        super(context);
        init(context);
    }

    public TagFlowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mScroller = new Scroller(context);

        ViewConfiguration configuration = ViewConfiguration.get(context);
        mTouchSlop = configuration.getScaledTouchSlop();
        mOverscrollDistance = configuration.getScaledOverscrollDistance();
        mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
        mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
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
        // 已经使用的高度
        viewHeight = getMeasuredHeight();
        // 下一行开始的 top 位置
        int nextTopPoint = getPaddingTop();

        // 已经使用的宽度
        int useWidth = getPaddingLeft();
        // 已经使用的高度
        int useHeight = getPaddingTop();

        int childCount = getChildCount();

        MarginLayoutParams marginLayoutParams;
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);

            Log.e("sun", child.getLayoutParams().getClass().getName());
            marginLayoutParams = (MarginLayoutParams) child.getLayoutParams();

            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();

            int left = useWidth + marginLayoutParams.leftMargin;
            int top = nextTopPoint + marginLayoutParams.topMargin;
            int right = left + childWidth + marginLayoutParams.rightMargin;
            int bottom = top + childHeight + marginLayoutParams.bottomMargin;

            // 是否需要换行
            if (right > width) {
                left = getPaddingLeft() + marginLayoutParams.leftMargin;
                top = top + childHeight + marginLayoutParams.topMargin;
                right = left + childWidth + marginLayoutParams.rightMargin;
                bottom = top + childHeight + marginLayoutParams.bottomMargin;
                nextTopPoint = top;
            }
            useWidth = right;

            viewContentHeight = nextTopPoint;

            Log.e("sun", i + "-" + top + "-" + right + "-" + width + "-" + nextTopPoint);
            child.layout(left, top, right, bottom);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int eventKey = ev.getAction();
        // 判断是何种事件
        switch (eventKey) {
            case MotionEvent.ACTION_DOWN:
                // 记下按下的屏幕坐标
                if (downPoint == null) {
                    downPoint = new PointF();
                }
                downPoint.x = ev.getRawX();
                downPoint.y = ev.getRawY();

                if (mVelocityTracker == null){
                    mVelocityTracker = VelocityTracker.obtain();
                }
                mVelocityTracker.addMovement(ev);

                break;
            case MotionEvent.ACTION_MOVE:
                float moveY = ev.getRawY();
                float moveSlop = Math.abs(moveY - downPoint.y);
                if (moveSlop > mTouchSlop) {
                    return true;
                }
                break;
        }

        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int eventKey = event.getAction();
        switch (eventKey) {
            case MotionEvent.ACTION_MOVE:
                float moveY = event.getRawY();
                // 判断滑动的距离不能大于 view 的
                int viewTopY = getScrollY();
                // 用负号因为原始坐标是正常的坐标系
                int moveDis = (int) (downPoint.y - moveY);
                Log.e("sun",getScrollY()+"-"+moveDis);
                // 几个参数的意思：需要滑动 x 的距离，需滑动 y 的距离，x 已经滑动的值，y 已经滑动的值，
                //      x 滑动的范围 ，y 滑动的范围，x 显示边缘效果时的最大值（待验证，可能是回弹间隙），y 显示边缘效果时的最大值（待验证，可能是回弹间隙）,执行完滚动之后是否继续处理后续事件(没用到的参数)
                int rangY = viewContentHeight-viewHeight;
                overScrollBy(0,moveDis,0,getScrollY(),0,rangY,0,0,true);
                // 记录最后一次的坐标
                downPoint.x = event.getRawX();
                downPoint.y = event.getRawY();
                return true;
            case MotionEvent.ACTION_UP:
                // 当快速滑动时，使用 scroller 完成流程滑动
                mVelocityTracker.computeCurrentVelocity(1000,mMaximumVelocity);
                float velocity = mVelocityTracker.getYVelocity();
                if (velocity > mMinimumVelocity){
                    mScroller.startScroll();
                }


//                int dis = (int) (downPoint.y - event.getRawY());
//                mScroller.startScroll(0,(int) (downPoint.y),0,dis);
//                invalidate();
//                downPoint.x = event.getRawX();
//                downPoint.y = event.getRawY();
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
    protected void onOverScrolled(int scrollX, int scrollY,
                                  boolean clampedX, boolean clampedY) {
        // Treat animating scrolls differently; see #computeScroll() for why.
//        if (!mScroller.isFinished()) {
//            final int oldX = mScrollX;
//            final int oldY = mScrollY;
//            mScrollX = scrollX;
//            mScrollY = scrollY;
//            invalidateParentIfNeeded();
//            onScrollChanged(mScrollX, mScrollY, oldX, oldY);
//            if (clampedY) {
//                mScroller.springBack(mScrollX, mScrollY, 0, 0, 0, getScrollRange());
//            }
//        } else {
            super.scrollTo(scrollX, scrollY);
//        }

//        awakenScrollBars();
    }

    @Override
    public void computeScroll() {
        // 第三步，重写computeScroll()方法，并在其内部完成平滑滚动的逻辑
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
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
