package com.finance.widgetlib.widget;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import java.util.ArrayList;
import java.util.List;

public class WaveHelper {
    private WaveView mWaveView;

    private AnimatorSet mAnimatorSet;

    public WaveHelper(WaveView waveView) {
        mWaveView = waveView;
    }


    //属性设置完成之后，要执行该方法才能刷新生效。
    public WaveHelper exe() {
        List<Animator> animators = new ArrayList<>();

        // horizontal animation. 水平的动画
        // wave waves infinitely. 海波浪无限
        ObjectAnimator waveShiftAnim = ObjectAnimator.ofFloat(
                mWaveView, "waveShiftRatio", startWaveShiftRatio, endWaveShiftRatio);//相位变化，产生水平移动效果。
        waveShiftAnim.setRepeatCount(ValueAnimator.INFINITE);//次数无限循环
        waveShiftAnim.setDuration(1500);//变化时间
        waveShiftAnim.setInterpolator(new LinearInterpolator());
        animators.add(waveShiftAnim);

        // vertical animation. 垂直的动画【水位上升】
        // water level increases from 0 to center of WaveView 水位从0上升到波幅中心。
        ObjectAnimator waterLevelAnim = ObjectAnimator.ofFloat(
                mWaveView, "waterLevelRatio", startWaterLevelRatio, endWaterLevelRatio); //水位
        waterLevelAnim.setRepeatCount(0);//次数一次。
        waterLevelAnim.setDuration(10000);
        waterLevelAnim.setInterpolator(new DecelerateInterpolator());
        animators.add(waterLevelAnim);

        // amplitude animation. 垂直(振幅)动画【波高】
        // wave grows big then grows small, repeatedly
        ObjectAnimator amplitudeAnim = ObjectAnimator.ofFloat(
                mWaveView, "amplitudeRatio", startAmplitudeRatio, endAmplitudeRatio);//振幅,不可以为0，但可以尽可能接近0。如：0.0001f 。因为为0的话，会有闪屏bug。
        amplitudeAnim.setRepeatCount(ValueAnimator.INFINITE);
        amplitudeAnim.setRepeatMode(ValueAnimator.REVERSE);//动画效果反转
        amplitudeAnim.setDuration(3000);
        amplitudeAnim.setInterpolator(new LinearInterpolator());
        animators.add(amplitudeAnim);

        mAnimatorSet = new AnimatorSet();
        mAnimatorSet.playTogether(animators);
        return this;
    }

    float startWaveShiftRatio = 0F;//开始相位
    float endWaveShiftRatio = 0.1F;//结束相位

    float startWaterLevelRatio = 0f;//开始水位
    float endWaterLevelRatio = 0.5f;//结束水位

    float startAmplitudeRatio = 0.0001f;//开始振幅
    float endAmplitudeRatio = 0.1f;//结束振幅

    /**
     * 设置开始相位范围（0~1）
     *
     * @param startWaveShiftRatio
     */
    public WaveHelper setStartWaveShiftRatio(float startWaveShiftRatio) {
        this.startWaveShiftRatio = startWaveShiftRatio;
        return this;
    }

    /**
     * 设置结束相位范围（0~1）
     *
     * @param endWaveShiftRatio
     */
    public WaveHelper setEndWaveShiftRatio(float endWaveShiftRatio) {
        this.endWaveShiftRatio = endWaveShiftRatio;
        return this;
    }

    /**
     * 开始水位（0~1）
     *
     * @param startWaterLevelRatio
     */
    public WaveHelper setStartWaterLevelRatio(float startWaterLevelRatio) {
        this.startWaterLevelRatio = startWaterLevelRatio;
        return this;
    }

    /**
     * 结束水位（0~1）
     *
     * @param endWaterLevelRatio
     */
    public WaveHelper setEndWaterLevelRatio(float endWaterLevelRatio) {
        this.endWaterLevelRatio = endWaterLevelRatio;
        return this;
    }

    /**
     * 开始振幅 范围（0~1），不可以为0。会有闪屏Bug
     *
     * @param startAmplitudeRatio
     */
    public WaveHelper setStartAmplitudeRatio(float startAmplitudeRatio) {
        this.startAmplitudeRatio = startAmplitudeRatio;
        return this;
    }

    /**
     * 结束振幅 范围（0~1），不可以为0。会有闪屏Bug
     *
     * @param endAmplitudeRatio
     */
    public WaveHelper setEndAmplitudeRatio(float endAmplitudeRatio) {
        this.endAmplitudeRatio = endAmplitudeRatio;
        return this;
    }

    //开始属性动画
    public void start() {
        mWaveView.setShowWave(true);
        if (mAnimatorSet != null) {
            mAnimatorSet.start();
        }
    }

    //取消属性动画
    public void cancel() {
        if (mAnimatorSet != null) {
            //mAnimatorSet.cancel();
            mAnimatorSet.end();
        }
    }
}
