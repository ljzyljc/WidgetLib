package com.finance.widgetlib.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

import com.finance.commonlib.util.ProportionUtils;


/**
 * 菜单滑动条
 *
 * @author 彭治铭
 */
public class TabLayoutBar extends android.support.v7.widget.AppCompatImageView implements ViewPager.OnPageChangeListener {

    public TabLayoutBar(Context context) {
        super(context);
        init();
    }

    public TabLayoutBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setColor(color);
        Drawable src = getDrawable();
        if (src == null) {
            return;
        } else if (src instanceof ColorDrawable) {
            ColorDrawable colordDrawable = (ColorDrawable) src;
            color = colordDrawable.getColor();
            paint.setColor(color);
        } else {
            tab = ((BitmapDrawable) src).getBitmap();
            tab = ProportionUtils.getInstance().adapterBitmap(tab);
        }
        setImageBitmap(null);//src颜色和位图都会清空
    }

    Bitmap tab;//位图
    int color = Color.parseColor("#3388FF");//滑动条颜色，默认为蓝色
    Paint paint;
    int count = 0;//页面个数
    int w = 0;//单个tab的宽度
    int x = 0;
    int y = 0;
    int offset = 0;//x的偏移量，用于图片居中

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);//画子View，以下方法必须放在下面。不然就被遮挡了。
        if (this.tab != null && !tab.isRecycled()) {
            canvas.drawBitmap(tab, x, y, paint);
        } else {
            RectF rectF = new RectF(x, 0, x + w, getHeight());
            canvas.drawRect(rectF, paint);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.w = w / count;
        if (tab != null && !tab.isRecycled()) {
            offset = (this.w - tab.getWidth()) / 2;
            y = (h - tab.getHeight()) / 2;
            x = 0 * w + offset;
        }
    }

    public void setViewPager(ViewPager viewPager) {
        if (viewPager != null && viewPager.getAdapter() != null) {
            viewPager.addOnPageChangeListener(this);//addOnPageChangeListener滑动事件监听，可以添加多个监听。不会冲突。
            count = viewPager.getAdapter().getCount();
            requestLayout();
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (positionOffset >= 0 && positionOffset <= 1) {
            x = positionOffsetPixels / count + (position * w) + offset;
            invalidate();
        }
    }

    @Override
    public void onPageSelected(int position) {
        x = position * w + offset;
        invalidate();
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
