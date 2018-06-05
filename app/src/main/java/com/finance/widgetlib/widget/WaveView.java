/*
 *  Copyright (C) 2015, gelitenight(gelitenight@gmail.com).
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.finance.widgetlib.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposeShader;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class WaveView extends View {
    /**
     * +------------------------+
     * |<--wave length 波长->       |______
     * |   /\          |   /\   |  |
     * |  /  \         |  /  \  | amplitude 振幅
     * | /    \        | /    \ |  |
     * |/      \       |/      \|__|____
     * |        \      /        |  |
     * |         \    /         |  |
     * |          \  /          |  |
     * |           \/           | water level 水位
     * |                        |  |
     * |                        |  |
     * +------------------------+__|____
     */
    private static final float DEFAULT_AMPLITUDE_RATIO = 0.05f;
    private static final float DEFAULT_WATER_LEVEL_RATIO = 0.5f;
    private static final float DEFAULT_WAVE_LENGTH_RATIO = 1.0f;
    private static final float DEFAULT_WAVE_SHIFT_RATIO = 0.0f;

    public enum ShapeType {
        CIRCLE,//圆
        SQUARE//矩形
    }

    // if true, the shader will display the wave
    private boolean mShowWave;

    // shader containing repeated waves
    private BitmapShader mWaveShader;
    // shader matrix
    private Matrix mShaderMatrix;
    // paint to draw wave
    private Paint mViewPaint;
    // paint to draw border
    private Paint mBorderPaint;

    private float mDefaultAmplitude;
    private float mDefaultWaterLevel;
    private float mDefaultWaveLength;
    private double mDefaultAngularFrequency;

    //振幅  范围（0~1）,0.5就是控件的一半高，1就是整个控件的高度。
    //不可以为0，但可以尽可能接近0。如：0.0001f 。因为为0的话，会有闪屏bug。
    private float mAmplitudeRatio = DEFAULT_AMPLITUDE_RATIO;
    //波长 范围（0~1），0.5表示一个波长等于控件的一半。1就是波长和控件的长度相等。即：乘以控件的宽度
    private float mWaveLengthRatio = DEFAULT_WAVE_LENGTH_RATIO;
    private float mWaterLevelRatio = DEFAULT_WATER_LEVEL_RATIO;//水位 范围（0~1）,0.5就是半瓶，1就是整个控件的高度
    private float mWaveShiftRatio = DEFAULT_WAVE_SHIFT_RATIO;//相位，产生水平波浪动画效果。范围（0~1）

    private int brontWaveColor = Color.parseColor("#3CFFFFFF");//后置波浪颜色
    private int frontWaveColor = Color.parseColor("#28FFFFFF");//前置波浪颜色
    private ShapeType mShapeType = ShapeType.CIRCLE;//默认画圆。

    boolean isTRANSPARENT = true;//颜色是否透明渐变

    /**
     * 属性设置完成之后，要执行该方法才能刷新生效。
     *
     * @return
     */
    public WaveView exe() {
        mWaveShader = null;
        composeShader = null;
        createShader();
        mShowWave=true;
        invalidate();
        return this;
    }

    /**
     * 波浪线颜色，透明色 Color.TRANSPARENT 不会绘制。
     *
     * @param frontWaveColor 前置波浪颜色
     * @param brontWaveColor 后缀波浪颜色
     */
    public WaveView setWaveColor(int frontWaveColor, int brontWaveColor) {
        this.frontWaveColor = frontWaveColor;
        this.brontWaveColor = brontWaveColor;
        return this;
    }

    /**
     * 设置后置颜色
     *
     * @param brontWaveColor
     * @return
     */
    public WaveView setBrontWaveColor(int brontWaveColor) {
        this.brontWaveColor = brontWaveColor;
        return this;
    }

    /**
     * 设置前置颜色
     *
     * @param frontWaveColor
     * @return
     */
    public WaveView setFrontWaveColor(int frontWaveColor) {
        this.frontWaveColor = frontWaveColor;
        return this;
    }

    /**
     * 设置振幅（波的高度）
     * <p>
     * 范围（0~1）,0.5就是控件的一半高，1就是整个控件的高度。
     * 不可以为0，但可以尽可能接近0。如：0.0001f 。因为为0的话，会有闪屏bug。
     */
    public WaveView setAmplitudeRatio(float amplitudeRatio) {
        if (mAmplitudeRatio != amplitudeRatio) {
            mAmplitudeRatio = amplitudeRatio;
            invalidate();//属性动画需要set()方法里手动刷新
        }
        return this;
    }

    /**
     * 设置水位
     * <p>
     * 水位 范围（0~1）,0.5就是半瓶，1就是整个控件的高度
     */
    public WaveView setWaterLevelRatio(float waterLevelRatio) {
        if (mWaterLevelRatio != waterLevelRatio) {
            mWaterLevelRatio = waterLevelRatio;
            invalidate();//属性动画需要set()方法里手动刷新
        }
        return this;
    }

    /**
     * 设置相位
     * 相位，产生水平波浪动画效果。范围（0~1）
     */
    public WaveView setWaveShiftRatio(float waveShiftRatio) {
        if (mWaveShiftRatio != waveShiftRatio) {
            mWaveShiftRatio = waveShiftRatio;
            invalidate();//属性动画需要set()方法里手动刷新
        }
        return this;
    }

    /**
     * 设置波的长度
     * 范围（0~1），0.5表示一个波长等于控件的一半。1就是波长和控件的长度相等。即：乘以控件的宽度
     */
    public WaveView setWaveLengthRatio(float waveLengthRatio) {
        mWaveLengthRatio = waveLengthRatio;
        return this;
    }

    /**
     * 设置边框
     *
     * @param width 边框宽度
     * @param color 边框颜色
     */
    public WaveView setBorder(int width, int color) {
        if (mBorderPaint == null) {
            mBorderPaint = new Paint();
            mBorderPaint.setAntiAlias(true);
            mBorderPaint.setStyle(Style.STROKE);
        }
        mBorderPaint.setColor(color);
        mBorderPaint.setStrokeWidth(width);
        return this;
    }

    /**
     * WaveView.ShapeType.CIRCLE 圆形
     * WaveView.ShapeType.SQUARE 方形
     *
     * @param shapeType
     */
    public WaveView setShapeType(ShapeType shapeType) {
        mShapeType = shapeType;
        return this;
    }

    /**
     * 波浪是否渐变透明。 true 渐变透明。false 不渐变
     *
     * @param TRANSPARENT
     * @return
     */
    public WaveView setTRANSPARENT(boolean TRANSPARENT) {
        isTRANSPARENT = TRANSPARENT;
        return this;
    }

    public WaveView(Context context) {
        super(context);
        init();
    }

    public WaveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WaveView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mShaderMatrix = new Matrix();
        mViewPaint = new Paint();
        mViewPaint.setAntiAlias(true);
        mViewPaint.setDither(true);
        setLayerType(View.LAYER_TYPE_HARDWARE, mViewPaint);
    }

    public float getWaveShiftRatio() {
        return mWaveShiftRatio;
    }


    public float getWaterLevelRatio() {
        return mWaterLevelRatio;
    }


    public float getAmplitudeRatio() {
        return mAmplitudeRatio;
    }


    public float getWaveLengthRatio() {
        return mWaveLengthRatio;
    }


    public boolean isShowWave() {
        return mShowWave;
    }

    public void setShowWave(boolean showWave) {
        mShowWave = showWave;
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        createShader();
    }

    /**
     * Create the shader with default waves which repeat horizontally, and clamp vertically
     */
    private void createShader() {
        int width = getLayoutParams().width >= getWidth() ? getLayoutParams().width : getWidth();
        int height = getLayoutParams().height >= getHeight() ? getLayoutParams().height : getHeight();
        if (width <= 0 || height <= 0) {
            return;
        }
        mDefaultAngularFrequency = 2.0f * Math.PI / DEFAULT_WAVE_LENGTH_RATIO /width;
        mDefaultAmplitude = height * DEFAULT_AMPLITUDE_RATIO;
        mDefaultWaterLevel = height * DEFAULT_WATER_LEVEL_RATIO;
        mDefaultWaveLength = width;

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Paint wavePaint = new Paint();
        wavePaint.setStrokeWidth(2);
        wavePaint.setAntiAlias(true);

        // Draw default waves into the bitmap
        // y=Asin(ωx+φ)+h
        final int endX = width + 1;
        final int endY = height + 1;

        float[] waveY = new float[endX];

        //fixme ====================================================================================第一波(显示在最低层,后置)
        if (brontWaveColor != Color.TRANSPARENT) {
            wavePaint.setColor(brontWaveColor);
            for (int beginX = 0; beginX < endX; beginX++) {
                double wx = beginX * mDefaultAngularFrequency;
                float beginY = (float) (mDefaultWaterLevel + mDefaultAmplitude * Math.sin(wx));
                canvas.drawLine(beginX, beginY, beginX, endY, wavePaint);

                waveY[beginX] = beginY;//这个是波浪数据。后面的第二波就是根据这个数据来画的。
            }
        }

        //fixme ====================================================================================第二波(显示在前面，前置)
        if (frontWaveColor != Color.TRANSPARENT) {
            int wave2Shift = (int) (mDefaultWaveLength / 4);//相位差。波与波之间的间距。
            wavePaint.setColor(frontWaveColor);
            for (int beginX = 0; beginX < endX; beginX++) {
                canvas.drawLine(beginX, waveY[(beginX + wave2Shift) % endX], beginX, endY, wavePaint);
            }
        }


        // use the bitamp to create the shader
        mWaveShader = new BitmapShader(bitmap, Shader.TileMode.REPEAT, Shader.TileMode.CLAMP);

        //fixme 实现波浪颜色渐变[逐渐变透明]
        linearGradient = new LinearGradient(0, 0, 0, endY, Color.WHITE, Color.TRANSPARENT, Shader.TileMode.REPEAT);
        composeShader = new ComposeShader(mWaveShader, linearGradient, PorterDuff.Mode.DST_IN);

        //是否透明渐变
        if (isTRANSPARENT) {
            mViewPaint.setShader(composeShader);
        } else {
            mViewPaint.setShader(mWaveShader);
        }

    }

    Shader linearGradient;
    Shader composeShader;

    @Override
    protected void onDraw(Canvas canvas) {
        // modify paint shader according to mShowWave state
        if (mShowWave && mWaveShader != null && composeShader != null) {

            // sacle shader according to mWaveLengthRatio and mAmplitudeRatio
            // this decides the size(mWaveLengthRatio for width, mAmplitudeRatio for height) of waves
            mShaderMatrix.setScale(
                    mWaveLengthRatio / DEFAULT_WAVE_LENGTH_RATIO,
                    mAmplitudeRatio / DEFAULT_AMPLITUDE_RATIO,
                    0,
                    mDefaultWaterLevel);
            // translate shader according to mWaveShiftRatio and mWaterLevelRatio
            // this decides the start position(mWaveShiftRatio for x, mWaterLevelRatio for y) of waves
            mShaderMatrix.postTranslate(
                    mWaveShiftRatio * getWidth(),
                    (DEFAULT_WATER_LEVEL_RATIO - mWaterLevelRatio) * getHeight());

            // assign matrix to invalidate the shader
            mWaveShader.setLocalMatrix(mShaderMatrix);

            //是否透明渐变
            if (isTRANSPARENT) {
                composeShader = null;
                composeShader = new ComposeShader(mWaveShader, linearGradient, PorterDuff.Mode.DST_IN);
                mViewPaint.setShader(composeShader);
            } else {
                mViewPaint.setShader(mWaveShader);
            }


            float borderWidth = mBorderPaint == null ? 0f : mBorderPaint.getStrokeWidth();
            switch (mShapeType) {
                case CIRCLE:
                    //画边框
                    if (borderWidth > 0) {
                        canvas.drawCircle(getWidth() / 2f, getHeight() / 2f,
                                (getWidth() - borderWidth) / 2f - 1f, mBorderPaint);
                    }
                    float radius = getWidth() / 2f - borderWidth;
                    //画圆
                    canvas.drawCircle(getWidth() / 2f, getHeight() / 2f, radius, mViewPaint);
                    break;
                case SQUARE:
                    if (borderWidth > 0) {
                        canvas.drawRect(
                                borderWidth / 2f,
                                borderWidth / 2f,
                                getWidth() - borderWidth / 2f - 0.5f,
                                getHeight() - borderWidth / 2f - 0.5f,
                                mBorderPaint);
                    }
                    //画矩形
                    canvas.drawRect(borderWidth, borderWidth, getWidth() - borderWidth,
                            getHeight() - borderWidth, mViewPaint);
                    break;
            }
        } else {
            mViewPaint.setShader(null);
        }
    }
}
