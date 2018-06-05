package com.finance.widgetlib.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.finance.commonlib.util.ProportionUtils;
import com.finance.widgetlib.R;
import com.finance.widgetlib.widget.datepicker.WheelPicker;


import java.util.ArrayList;
import java.util.List;

/**
 * 滚轮选择器【新版】
 * Created by 彭治铭 on 2018/3/29.
 */

public class RollerView extends WheelPicker {

    /**
     * 设置中间两条线条的宽度
     *
     * @param lineWidth
     */
    public void setLineWidth(int lineWidth) {
        this.lineWidth = lineWidth;
    }

    /**
     * 设置中间两条线条的颜色
     *
     * @param lineColor
     */
    public RollerView setLineColor(int lineColor) {
        this.lineColor = lineColor;
        invalidate();
        return this;
    }

    /**
     * 设置线条的高度【边框的宽度】
     *
     * @param strokeWidth
     */
    public RollerView setStrokeWidth(int strokeWidth) {
        this.strokeWidth = strokeWidth;
        invalidate();
        return this;
    }

    /**
     * 设置选中字体的颜色
     *
     * @param selectTextColor
     */
    public RollerView setSelectTextColor(int selectTextColor) {
        setSelectedItemTextColor(selectTextColor);
        return this;
    }

    /**
     * 设置默认字体的颜色
     *
     * @param defaultTextColor
     */
    public RollerView setDefaultTextColor(int defaultTextColor) {
        setItemTextColor(defaultTextColor);
        return this;
    }

    int textSize = 0;

    //设置字体大小
    public RollerView setTextSize(float textSize) {
        setItemTextSize((int) textSize);
        this.textSize = (int) textSize;
        return this;
    }

    /**
     * 设置回调接口，返回选中的数据和下标
     *
     * @param itemSelectListener
     */
    public RollerView setItemSelectListener(final ItemSelectListener itemSelectListener) {
        setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(WheelPicker picker, Object data, int position) {
                itemSelectListener.onItemSelect(data.toString(), position);
            }
        });
        return this;
    }

    /**
     * 設置當前顯示item的個數【一定要在設置數據之前，設置。必須】
     *
     * @param count
     */
    public RollerView setCount(int count) {
        setVisibleItemCount(count);
        return this;
    }

    /**
     * fixme ======================================================================================= 设置数据集合
     *
     * @param items
     */
    public RollerView setItems(List<String> items) {
        if (this.items == null) {
            this.items = new ArrayList<>();//与参数传入的数据不会发生关联。
        }
        this.items.clear();
        this.items.addAll(items);
        setData(this.items);
        return this;
    }

    /**
     * 选中指定下标【数据集合的实际下标】。
     *
     * @param position
     * @return
     */
    public RollerView setCurrentPostion(int position) {
        setSelectedItemPosition(position);
        return this;
    }

    //获取当前选中下标【不算空格。是原数据的下标，即实际下标。】
    public int getCurrentPostion() {
        return getCurrentItemPosition();
    }

    //获取当前选中的值
    public String getCurrentItemValue() {
        return items.get(getCurrentItemPosition());
    }

    public RollerView(Context context) {
        super(context);
        setCurved(true);//设置卷尺效果
    }

    public RollerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setCurved(true);//设置卷尺效果
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (paint == null) {
            paint = new Paint();
            paint.setAntiAlias(true);
            paint.setDither(true);
        }
        setLayerType(View.LAYER_TYPE_SOFTWARE, paint);
        setOverScrollMode(OVER_SCROLL_NEVER);//设置滑动到边缘时无效果模式
        setVerticalScrollBarEnabled(false);//滚动条隐藏
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mVisibleItemCount <= 0) {
            mVisibleItemCount = 5;
        }
        int chidHeith = h / mVisibleItemCount;

        startLine = mDrawnCenterY - chidHeith / 2 - getItemTextSize() / 2;
        endLine = startLine + chidHeith;
        //设置默认字体大小
        if (textSize <= 0) {
            int textSize = (int) (chidHeith * 0.72);//字体大小。默认就是这个了。
            setItemTextSize(textSize);
        }
    }


    Paint paint;
    int startLine;
    int endLine;
    int lineColor = Color.parseColor("#000000");//线条颜色
    int strokeWidth = (int) ProportionUtils.getInstance().getDimension(R.dimen.x2);//线条的宽度
    private int lineWidth = 0;//线条的宽度

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(lineColor);//线条颜色
        paint.setStrokeWidth(strokeWidth);//线条宽度
        //中间两条线
        if (lineWidth <= 0) {
            canvas.drawLine(0, startLine, getWidth(), startLine, paint);
            canvas.drawLine(0, endLine, getWidth(), endLine, paint);
        } else {
            int starx = (int) ((getWidth() - lineWidth) / 2);
            canvas.drawLine(starx, startLine, starx + lineWidth, startLine, paint);
            canvas.drawLine(starx, endLine, starx + lineWidth, endLine, paint);
        }
        paint.setColor(Color.BLACK);
    }

    List<String> items;//数据集合

    //回调
    public interface ItemSelectListener {
        //原始数据，和下标
        void onItemSelect(String item, int position);
    }

}

