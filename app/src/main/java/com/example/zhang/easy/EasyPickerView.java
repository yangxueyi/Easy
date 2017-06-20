package com.example.zhang.easy;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Scroller;

import java.util.ArrayList;

/**
 * Created by Zhang on 2017/6/7.
 */

public class EasyPickerView extends View {

    // 文字大小
    private int textSize;
    // 颜色，默认Color.BLACK
    private int textColor;
    // 文字之间的间隔，默认10dp
    private int textPadding;
    // 文字最大放大比例，默认2.0f
    private float textMaxScale;
    // 文字最小alpha值，范围0.0f~1.0f，默认0.4f
    private float textMinAlpha;
    // 是否循环模式，默认是
    private boolean isRecycleMode;
    // 正常状态下最多显示几个文字，默认3（偶数时，边缘的文字会截断）
    private int maxShowNum;

    private TextPaint textPaint;
    private Paint.FontMetrics fm;

    private Scroller scroller;
    private VelocityTracker velocityTracker;
    private int minimumVelocity;
    private int maximumVelocity;
    private int scaledTouchSlop;

    // 数据
    private ArrayList<String> dataList = new ArrayList<>();
    // 中间x坐标
    private int cx;
    // 中间y坐标
    private int cy;
    // 文字最大宽度
    private float maxTextWidth;
    // 文字高度
    private int textHeight;
    // 实际内容宽度
    private int contentWidth;
    // 实际内容高度
    private int contentHeight;

    // 按下时的y坐标
    private float downY;
    // 本次滑动的y坐标偏移值
    private float offsetY;
    // 在fling之前的offsetY
    private float oldOffsetY;
    // 当前选中项
    private int curIndex;
    private int offsetIndex;

    // 回弹距离
    private float bounceDistance;
    // 是否正处于滑动状态
    private boolean isSliding = false;

    private TypedArray typedArray;
    private TypedArray tArray;

    public EasyPickerView(Context context) {
        this(context,null);
    }

    public EasyPickerView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public EasyPickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //获取配置属性
        tArray = typedArray;
        tArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.EasyPickerView, defStyleAttr, 0);
        textSize = tArray.getDimensionPixelSize(R.styleable.EasyPickerView_epvTextSize, (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, 16, getResources().getDisplayMetrics()));
        textColor = tArray.getColor(R.styleable.EasyPickerView_epvTextColor, Color.BLACK);
        textPadding = tArray.getDimensionPixelSize(R.styleable.EasyPickerView_epvTextPadding, (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics()));
        textMaxScale = tArray.getFloat(R.styleable.EasyPickerView_epvTextMaxScale, 2.0f);
        textMinAlpha = tArray.getFloat(R.styleable.EasyPickerView_epvTextMinAlpha, 0.4f);
        isRecycleMode = tArray.getBoolean(R.styleable.EasyPickerView_epvRecycleMode, true);
        maxShowNum = tArray.getInteger(R.styleable.EasyPickerView_epvMaxShowNum, 3);
        tArray.recycle();//回收资源

        textPaint = new TextPaint();
        textPaint.setColor(textColor);
        textPaint.setTextSize(textSize);
        textPaint.setAntiAlias(true);
        fm = textPaint.getFontMetrics();

        scroller = new Scroller(context);
        minimumVelocity = ViewConfiguration.get(getContext()).getScaledMinimumFlingVelocity();
        maximumVelocity = ViewConfiguration.get(getContext()).getScaledMaximumFlingVelocity();
        scaledTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int mode = MeasureSpec.getMode(widthMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        contentWidth = (int) (maxTextWidth + getPaddingLeft() + getPaddingRight());
        if (mode != MeasureSpec.EXACTLY) { // wrap_content
            width = contentWidth;
        }

        mode = MeasureSpec.getMode(heightMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        textHeight = (int) (fm.bottom - fm.top);
        contentHeight = textHeight * maxShowNum + textPadding * maxShowNum;
        if (mode != MeasureSpec.EXACTLY) { // wrap_content
            height = contentHeight + getPaddingTop() + getPaddingBottom();
        }

        cx = width / 2;
        cy = height / 2;

        setMeasuredDimension(width, height);

    }


    @Override
    protected void onDraw(Canvas canvas) {
        if (null != dataList && dataList.size() > 0) {
            canvas.clipRect(
                    cx - contentWidth / 2,
                    cy - contentHeight / 2,
                    cx + contentWidth / 2,
                    cy + contentHeight / 2
            );

            // 绘制文字，从当前中间项往前2个开始，往后一共绘制5个字
            int size = dataList.size();
            int centerPadding = textHeight + textPadding;
            int half = maxShowNum / 2 + 1;
            for (int i = -half; i <= half; i++) {
                int index = curIndex - offsetIndex + i;

                if (isRecycleMode) {
                    if (index < 0)
                        index = (index + 1) % dataList.size() + dataList.size() - 1;
                    else if (index > dataList.size() - 1)
                        index = index % dataList.size();
                }

                if (index >= 0 && index < size) {
                    // 计算每个字的中间y坐标
                    int tempY = cy + i * centerPadding;
                    tempY += offsetY % centerPadding;

                    // 根据每个字中间y坐标到cy的距离，计算出scale值
                    float scale = 1.0f - (1.0f * Math.abs(tempY - cy) / centerPadding);

                    // 根据textMaxScale，计算出tempScale值，即实际text应该放大的倍数，范围 1~textMaxScale
                    float tempScale = scale * (textMaxScale - 1.0f) + 1.0f;
                    tempScale = tempScale < 1.0f ? 1.0f : tempScale;
                    textPaint.setTextSize(textSize * tempScale);
                    textPaint.setAlpha((int) (255 * textMinAlpha * tempScale));

                    // 绘制
                    Paint.FontMetrics tempFm = textPaint.getFontMetrics();
                    String text = dataList.get(index);
                    float textWidth = textPaint.measureText(text);
                    canvas.drawText(text, cx - textWidth / 2, tempY - (tempFm.ascent + tempFm.descent) / 2, textPaint);
                }
            }
        }
    }

    /**
     * 设置要显示的数据
     *
     * @param dataList 要显示的数据
     */
    public void setDataList(ArrayList<String> dataList) {
        this.dataList.clear();
        this.dataList.addAll(dataList);

        // 更新maxTextWidth
        if (null != dataList && dataList.size() > 0) {
            int size = dataList.size();
            for (int i = 0; i < size; i++) {
                float tempWidth = textPaint.measureText(dataList.get(i));
                if (tempWidth > maxTextWidth)
                    maxTextWidth = tempWidth;
            }
            curIndex = 0;
        }
        requestLayout();
    }

}
