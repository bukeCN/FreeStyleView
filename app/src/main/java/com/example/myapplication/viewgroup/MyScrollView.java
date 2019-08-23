package com.example.myapplication.viewgroup;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.LinearLayout;

public class MyScrollView extends LinearLayout {
    /**
     * 手指上一次的 Y 点
     */
    private float lastY;

    private float mTouchSlop;

    public MyScrollView(Context context) {
        super(context);
        init(context);
    }

    public MyScrollView(Context context,AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MyScrollView(Context context,AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        ViewConfiguration configuration = ViewConfiguration.get(context);
        mTouchSlop = configuration.getScaledTouchSlop();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action){
            case MotionEvent.ACTION_DOWN:
                lastY = getY();
                break;
            case MotionEvent.ACTION_MOVE:

                // 如果滑动距离大与最小滑动距离，拦截事件
                float nowY = ev.getY();
                float yMoveDis = nowY - lastY;
                if (Math.abs(yMoveDis) > mTouchSlop){
                    return true;
                }
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();

        switch (action){
            case MotionEvent.ACTION_MOVE:
                float nowY = event.getY();
                // 计算 Y 需要滑动的距离
                float yMoveDis = nowY - lastY;
                overScrollBy(0,(int) -yMoveDis,0,getScrollY(),0,getRangScrollY(),0,0,true);
                lastY = nowY;
                break;
        }

        return super.onTouchEvent(event);
    }

    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        scrollTo(scrollX,scrollY);
    }

    private int getRangScrollY(){
        View child = getChildAt(0);
        return child.getHeight() + getPaddingTop() + getPaddingBottom();
    }
}
