package com.example.myapplication.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class FreeStyleView extends View {
    private Paint paint;
    /**
     * 需要绘制圆的半径
     */
    private int radius = 100;

    public FreeStyleView(Context context) {
        super(context);
        init();
    }

    public FreeStyleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FreeStyleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint();
        // 设置画笔模式，FILL 填充，STROKE 描边
        paint.setStyle(Paint.Style.STROKE);
        // 设置画笔颜色
        paint.setColor(Color.RED);
        // 设置画笔宽度，px 为单位，实际需要转换成 dp 值
        paint.setStrokeWidth(8);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureWidth(widthMeasureSpec),measureHieght(heightMeasureSpec));
    }

    private int measureWidth(int widthMeasureSpec) {
        // 测量宽度
        // 获取宽度模式
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        // 获取父View建议宽度
        int recommendWidth = MeasureSpec.getSize(widthMeasureSpec);
        // 最终的宽度
        int finalWidth = recommendWidth;
        switch (widthMode) {
            case MeasureSpec.EXACTLY:
                // 已经准确的给了值，直接使用即可
                break;
            case MeasureSpec.AT_MOST:
                // 这里对应 wrap_content，但是父view将尺寸设为了自身对应的尺寸，需要我们自行处理
                // 处理逻辑是我们自身设定的最小需要尺寸+对应尺寸的内边距，外边距不用考虑
                finalWidth = radius*2 + getPaddingLeft() + getPaddingRight();
                break;
            case MeasureSpec.UNSPECIFIED:
                // 没有限制尺寸，保持父view大小即可
                break;
        }
        return finalWidth;
    }
    private int measureHieght(int heightMeasureSpec) {
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int recommendHeight = MeasureSpec.getSize(heightMeasureSpec);
        // 最终的宽度
        int finalHeight = recommendHeight;
        switch (heightMode) {
            case MeasureSpec.EXACTLY:
                // 已经准确的给了值，直接使用即可
                break;
            case MeasureSpec.AT_MOST:
                // 这里对应 wrap_content，但是父view将尺寸设为了自身对应的尺寸，需要我们自行处理
                // 处理逻辑是我们自身设定的最小需要尺寸+对应尺寸的内边距，外边距不用考虑
                finalHeight = radius*2 + getPaddingTop() + getPaddingBottom();
                break;
            case MeasureSpec.UNSPECIFIED:
                // 没有限制尺寸，保持父view大小即可
                break;
        }
        return finalHeight;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 绘制一个圆形
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, radius, paint);
    }
}
