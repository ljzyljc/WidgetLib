package com.finance.widgetlib.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;


import com.finance.commonlib.util.ProportionUtils;

import static android.graphics.Paint.ANTI_ALIAS_FLAG;

/**
 * Created by Allen on 2017/5/7.
 * 自定义支付密码输入框
 */

public class PayPsdInputView extends android.support.v7.widget.AppCompatEditText {

    /**
     * 第一个圆开始绘制的圆心坐标
     */
    private float startX;
    private float startY;


    private float cX;


    /**
     * 实心圆的半径
     */
    private int radius = ProportionUtils.getInstance().adapterInt(10);

    /**
     * view的高度
     */
    private int height;
    private int width;

    /**
     * 当前输入密码位数
     */
    private int textLength = 0;
    private int bottomLineLength;
    /**
     * 最大输入位数。控制网格的个数。
     */
    private int maxCount = 6;
    /**
     * 圆的颜色   默认BLACK
     */
    private int circleColor = Color.BLACK;
    /**
     * 默认文本字体大小
     */
    private int textSize = ProportionUtils.getInstance().adapterInt(30);
    /**
     * 底部线的颜色   默认GRAY
     */
    private int bottomLineColor = Color.GRAY;

    /**
     * 分割线的颜色
     */
    private int borderColor = Color.GRAY;
    /**
     * 分割线的画笔
     */
    private Paint borderPaint;
    /**
     * 分割线开始的坐标x
     */
    private int divideLineWStartX;

    /**
     * 分割线的宽度  默认2
     */
    private int divideLineWidth = 2;
    /**
     * 竖直分割线的颜色
     */
    private int divideLineColor = Color.GRAY;
    private int focusedColor = Color.BLUE;
    private RectF rectF = new RectF();
    private RectF focusedRecF = new RectF();
    private int psdType = 0;
    private final static int psdType_weChat = 0;
    private final static int psdType_bottomLine = 1;

    /**
     * 矩形边框的圆角
     */
    private int rectAngle = ProportionUtils.getInstance().adapterInt(20);

    /**
     * 边框宽度
     */
    private int strokeWidth = ProportionUtils.getInstance().adapterInt(2);

    /**
     * 密码框里的圆点半径
     */
    private int circleRadius = ProportionUtils.getInstance().adapterInt(5);

    /**
     * 竖直分割线的画笔
     */
    private Paint divideLinePaint;
    /**
     * 圆的画笔
     */
    private Paint circlePaint;
    /**
     * 底部线的画笔
     */
    private Paint bottomLinePaint;

    /**
     * 需要对比的密码  一般为上次输入的
     */
    private String mComparePassword = null;


    /**
     * 当前输入的位置索引
     */
    private int position = 0;

    private onPasswordListener mListener;

    public PayPsdInputView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //小圆点的颜色同步文本颜色
        circleColor = getCurrentTextColor();
        //文本字体大小
        textSize = (int) getTextSize();

        init();
        initPaint();

        this.setBackgroundColor(Color.TRANSPARENT);
        this.setCursorVisible(false);
        //设置最大输入长度
        this.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxCount)});
        //设置输入类型为数字键盘类型。
        setInputType(InputType.TYPE_CLASS_NUMBER);
    }

    private void init() {
        //取消长按事件。禁止复制黏贴
        setLongClickable(false);
        setTextIsSelectable(false);
        setLayerType(View.LAYER_TYPE_HARDWARE, null);
        setPadding(0, 0, 0, 0);
    }

    /**
     * 初始化画笔
     */
    private void initPaint() {

        circlePaint = getPaint(circleRadius, Paint.Style.FILL, circleColor);

        bottomLinePaint = getPaint(strokeWidth, Paint.Style.FILL, bottomLineColor);

        borderPaint = getPaint(strokeWidth, Paint.Style.STROKE, borderColor);

        divideLinePaint = getPaint(divideLineWidth, Paint.Style.FILL, borderColor);

        circlePaint.setAntiAlias(true);
        circlePaint.setDither(true);
        bottomLinePaint.setAntiAlias(true);
        bottomLinePaint.setDither(true);
        borderPaint.setAntiAlias(true);
        borderPaint.setDither(true);
        divideLinePaint.setAntiAlias(true);
        divideLinePaint.setDither(true);
    }

    /**
     * 设置画笔
     *
     * @param strokeWidth 画笔宽度
     * @param style       画笔风格
     * @param color       画笔颜色
     * @return
     */
    private Paint getPaint(int strokeWidth, Paint.Style style, int color) {
        Paint paint = new Paint(ANTI_ALIAS_FLAG);
        paint.setDither(true);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(strokeWidth);
        paint.setStyle(style);
        paint.setColor(color);
        paint.setTextSize(textSize);
        paint.setTextAlign(Paint.Align.CENTER);
        return paint;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        height = h;
        width = w;

        divideLineWStartX = w / maxCount;

        startX = w / maxCount / 2;
        startY = h / 2;

        bottomLineLength = w / (maxCount + 2);
        rectF.set(strokeWidth / 2, strokeWidth / 2, width - (strokeWidth / 2), height - (strokeWidth / 2));

    }

    @Override
    protected void onDraw(Canvas canvas) {
        //不删除的画会默认绘制输入的文字
        //super.onDraw(canvas);

        switch (psdType) {
            case psdType_weChat:
                drawWeChatBorder(canvas);
                //屏蔽聚焦時的樣式
                //drawItemFocused(canvas, position);
                break;
            case psdType_bottomLine:
                drawBottomBorder(canvas);
                break;
        }
        //画圆点或文本
        drawPsdCircle(canvas);
    }

    /**
     * 画微信支付密码的样式
     *
     * @param canvas
     */
    private void drawWeChatBorder(Canvas canvas) {

        //画矩形
        canvas.drawRoundRect(rectF, rectAngle, rectAngle, borderPaint);
        //画垂直的线
        for (int i = 0; i < maxCount - 1; i++) {
            canvas.drawLine((i + 1) * divideLineWStartX,
                    0,
                    (i + 1) * divideLineWStartX,
                    height,
                    divideLinePaint);
        }

    }

    private void drawItemFocused(Canvas canvas, int position) {
        if (position > maxCount - 1) {
            return;
        }
        focusedRecF.set(position * divideLineWStartX, 0, (position + 1) * divideLineWStartX,
                height);
        canvas.drawRoundRect(focusedRecF, rectAngle, rectAngle, getPaint(3, Paint.Style.STROKE, focusedColor));
    }

    /**
     * 画底部显示的分割线
     *
     * @param canvas
     */
    private void drawBottomBorder(Canvas canvas) {

        for (int i = 0; i < maxCount; i++) {
            cX = startX + i * 2 * startX;
            canvas.drawLine(cX - bottomLineLength / 2,
                    height,
                    cX + bottomLineLength / 2,
                    height, bottomLinePaint);
        }
    }

    /**
     * 是否显示文本，false不显示，显示失效圆。
     */
    boolean isShowText = false;//默认不显示文本，显示实心圆。

    /**
     * 是否显示文本 true显示文本，fasle显示实心圆。
     *
     * @param showText
     */
    public void setShowText(boolean showText) {
        isShowText = showText;
        invalidate();
    }

    /**
     * 画密码实心圆和文本
     *
     * @param canvas
     */
    private void drawPsdCircle(Canvas canvas) {
        for (int i = 0; i < textLength; i++) {
            if (isShowText) {
                canvas.drawText(getPasswordString().substring(i, i + 1), startX + i * 2 * startX, startY + textSize / 2, circlePaint);
            } else {
                canvas.drawCircle(startX + i * 2 * startX,
                        startY,
                        radius,
                        circlePaint);
            }
        }
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        this.position = start + lengthAfter;
        textLength = text.toString().length();

        if (textLength == maxCount) {
            if (mListener != null) {
                if (TextUtils.isEmpty(mComparePassword)) {
                    mListener.inputFinished(getPasswordString());
                } else {
                    if (TextUtils.equals(mComparePassword, getPasswordString())) {
                        mListener.onEqual(getPasswordString());
                    } else {
                        mListener.onDifference(mComparePassword, getPasswordString());
                    }
                }
            }
        }

        invalidate();

    }

    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        super.onSelectionChanged(selStart, selEnd);

        //保证光标始终在最后
        if (selStart == selEnd) {
            setSelection(getText().length());
        }
    }

    /**
     * 获取输入的密码
     *
     * @return
     */
    public String getPasswordString() {
        return getText().toString().trim();
    }

    public void setComparePassword(String comparePassword, onPasswordListener listener) {
        mComparePassword = comparePassword;
        mListener = listener;
    }

    public void setComparePassword(onPasswordListener listener) {
        mListener = listener;
    }

    public void setComparePassword(String psd) {
        mComparePassword = psd;
    }

    /**
     * 清空密码
     */
    public void cleanPsd() {
        setText("");
    }

    /**
     * 密码比较监听
     */
    public interface onPasswordListener {
        void onDifference(String oldPsd, String newPsd);

        void onEqual(String psd);

        void inputFinished(String inputPsd);
    }
}