package com.example.myapplication.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.example.myapplication.R;

public class FreeStyleView extends View {
    private Paint paint;
    /**
     * 需要绘制圆的半径
     */
    private int radius = 100;
    /**
     * 绘制圆的颜色
     */
    private int color = Color.RED;

    public FreeStyleView(Context context) {
        super(context);
        init(context,null);
    }

    public FreeStyleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    public FreeStyleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs) {
        // 用代码新建的没有xml定义的属性
        if (attrs != null){
            // 处理属性（Android系统自带的属性我们不用定义即可使用，but还是要处理的）
            // 这一步是解析属性，因为不这样操作通过循环我们也能拿到属性名字以及对应的值，但适配、以及获取资源不太易操作。
            TypedArray typedArray = context.obtainStyledAttributes(attrs,R.styleable.FreeStyleView);
            // 获取颜色，第一个参数是在 attr 定义的属性，系统做了处理，名称变成了FreeStyleView_color，
            // 第二个参数是默认值
            color = typedArray.getColor(R.styleable.FreeStyleView_color,color);
        }

        paint = new Paint();
        // 设置画笔模式，FILL 填充，STROKE 描边
        paint.setStyle(Paint.Style.STROKE);
        // 设置画笔颜色
        paint.setColor(color);
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
