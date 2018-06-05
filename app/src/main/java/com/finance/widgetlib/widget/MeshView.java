package com.finance.widgetlib.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * Created by 彭治铭 on 2018/4/26.
 */
public class MeshView extends View {
    public MeshView(Context context) {
        super(context);
        initView();
    }

    public MeshView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private int width = 0;
    private int height = 0;
    private int baseLine = 0;// 基线，用于控制水位上涨的，这里是写死了没动，你可以不断的设置改变。
    private Paint mPaint;
    private int waveHeight = 100;// 波浪的最高度
    private int waveWidth;//波长
    private float offset = 0f;//偏移量

    public int duration = 3000;//動畫時間

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    /**
     * 不断的更新偏移量，并且循环。
     */
    private void updateXControl() {
        //设置一个波长的偏移
        ValueAnimator mAnimator = ValueAnimator.ofFloat(0, waveWidth);
        mAnimator.setInterpolator(new LinearInterpolator());
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animatorValue = (float) animation.getAnimatedValue();
                offset = animatorValue;//不断的设置偏移量，并重画
                postInvalidate();
            }
        });
        mAnimator.setDuration(duration);
        mAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mAnimator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(getPath(), mPaint);
    }

    //初始化paint，没什么可说的。
    private void initView() {
        mPaint = new Paint();

        Drawable background = getBackground();
        //background包括color和Drawable,这里分开取值
        if (background instanceof ColorDrawable) {
            ColorDrawable colordDrawable = (ColorDrawable) background;
            int color = colordDrawable.getColor();
            mPaint.setColor(color);
            setBackgroundDrawable(null);  //fixme:设置颜色
        } else {
            mPaint.setColor(Color.BLUE);
        }
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.FILL);

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        width = getMeasuredWidth();//获取屏幕宽度
        height = getMeasuredHeight();//获取屏幕高度
        waveWidth = width;
        baseLine = height / 2;
        updateXControl();
    }

    /**
     * 核心代码，计算path
     *
     * @return
     */
    private Path getPath() {
        int itemWidth = waveWidth / 2;//半个波长
//        int itemWidth = waveWidth;
        Path mPath = new Path();
        mPath.moveTo(-itemWidth * 3, baseLine);//起始坐标
        //核心的代码就是这里
        for (int i = -3; i < 2; i++) {
            int startX = i * itemWidth;
            mPath.quadTo(
                    startX + itemWidth / 2 + offset,//控制点的X,（起始点X + itemWidth/2 + offset)
                    getWaveHeigh(i),//控制点的Y
                    startX + itemWidth + offset,//结束点的X
                    baseLine//结束点的Y
            );//只需要处理完半个波长，剩下的有for循环自已就添加了。
        }
        //下面这三句话很重要，它是形成了一封闭区间，让曲线以下的面积填充一种颜色，大家可以把这3句话注释了看看效果。
        mPath.lineTo(width, height);
        mPath.lineTo(0, height);
        mPath.close();
        return mPath;
    }

    //奇数峰值是正的，偶数峰值是负数
    private int getWaveHeigh(int num) {
        if (num % 2 == 0) {
            return baseLine + waveHeight / 2;//fixme:决定几个波
        }
        return baseLine - waveHeight / 2;//决定波的高度。数字越小，越高。
    }

}
