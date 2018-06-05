package com.finance.widgetlib.widget;

import android.content.Context;
import android.graphics.Rect;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.TranslateAnimation;

/**
 * 弹性ScrollView
 * Created by 彭治铭 on 2018/4/25.
 */
public class BounceScrollView extends NestedScrollView {

    private View inner;// 孩子View

    private float y;// 点击时y坐标

    private Rect normal = new Rect();// 矩形(这里只是个形式，只是用于判断是否需要动画.)

    private boolean isCount = false;// 是否开始计算
    private float lastX = 0;
    private float lastY = 0;
    private float currentX = 0;
    private float currentY = 0;
    private float distanceX = 0;
    private float distanceY = 0;
    private boolean upDownSlide = false; //判断上下滑动的flag

    public BounceScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /***
     * 根据 XML 生成视图工作完成.该函数在生成视图的最后调用，在所有子视图添加完之后. 即使子类覆盖了 onFinishInflate
     * 方法，也应该调用父类的方法，使该方法得以执行.
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() > 0) {
            inner = getChildAt(0);
        }
    }

    public boolean isSlidingDown = false;//是否下滑。true 下滑，false上滑
    public boolean isDownAnime = true;//下滑弹性动画开启
    public boolean isUpAnime = true;//上滑弹性动画开启。


    //解决嵌套滑动冲突。
    @Override
    public boolean startNestedScroll(int axes) {
        //子View滑动时，禁止弹性动画。
        isDownAnime = false;
        isUpAnime = false;
        return super.startNestedScroll(axes);
    }


    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int[] offsetInWindow) {
        //Log.e("test", "dyConsumed:\t" + dyConsumed + "\tdyUnconsumed:\t" + dyUnconsumed);
        if (dyConsumed == 0) {
            isDownAnime = true;
            isUpAnime = true;
        } else {
            isDownAnime = false;
            isUpAnime = false;
        }
        if (dyConsumed == 0 && dyUnconsumed == 0) {
            isDownAnime = false;
            isUpAnime = false;
        }
        return super.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow);
    }

    @Override
    public void stopNestedScroll() {
        super.stopNestedScroll();
        //结束时，开启弹性滑动。
        isDownAnime = true;
        isUpAnime = true;
    }

    boolean isHorizon = false;//是否属于横屏滑动(水平滑动，不具备弹性效果)
    boolean isfirst = true;//是否为第一次滑动。

    int inerTop = 0;//记录原始的顶部高度。

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // TODO Auto-generated method stub

        currentX = ev.getX();
        currentY = ev.getY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //Log.e("test", "頂部Y2:\t" + inner.getTop());
                inerTop = inner.getTop();//原始顶部，不一定都是0，所以要记录一下。
                isfirst = true;
                break;
            case MotionEvent.ACTION_MOVE:
                distanceX = currentX - lastX;
                distanceY = currentY - lastY;
                //Log.e("test", "x滑动:\t" + distanceX + "\ty滑动:\t" + distanceY);
                if (distanceY > 0) {
                    isSlidingDown = true;//下滑大于0
                } else {
                    isSlidingDown = false;//上滑小于0
                }
                if (Math.abs(distanceX) < Math.abs(distanceY) && Math.abs(distanceY) > 12) {
                    upDownSlide = true;//表示上下滑动
                }

                if (isfirst) {
                    isHorizon = !upDownSlide;//横屏滑动(水平滑动，不具备弹性效果)
                    isfirst = false;
                }

                if (isSlidingDown && isDownAnime && !isHorizon) {
                    if (upDownSlide && inner != null) commOnTouchEvent(ev);//开启下拉弹性
                } else if (!isSlidingDown && isUpAnime && !isHorizon) {
                    if (upDownSlide && inner != null) commOnTouchEvent(ev);//开启上拉弹性
                }

                break;
            case MotionEvent.ACTION_UP:
                //以防万一，恢复原始状态
                isDownAnime = true;
                isUpAnime = true;
                if (upDownSlide && inner != null) commOnTouchEvent(ev);
                break;
            default:
                break;
        }
        lastX = currentX;
        lastY = currentY;

        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        //Log.e("test", "顶部Y:\t" + inner.getTop());
        if (inner.getTop() != inerTop) {
            return true;//子View在移动的时候，拦截对子View的事件处理。
        }
        return super.onInterceptTouchEvent(e);
    }

    /***
     * 监听touch
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return super.onTouchEvent(ev);
    }


    /***
     * 触摸事件
     *
     * @param ev
     */
    public void commOnTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_UP:
                // 手指松开.
                if (isNeedAnimation()) {
                    animation();
                    isCount = false;
                }
                clear0();
                break;
            case MotionEvent.ACTION_MOVE:
                final float preY = y;// 按下时的y坐标
                float nowY = ev.getY();// 时时y坐标
                int deltaY = (int) (preY - nowY);// 滑动距离
                if (!isCount) {
                    deltaY = 0; // 在这里要归0.
                }

                y = nowY;
                // 当滚动到最上或者最下时就不会再滚动，这时移动布局
                if (isNeedMove()) {
                    // 初始化头部矩形
                    if (normal.isEmpty()) {
                        // 保存正常的布局位置
                        normal.set(inner.getLeft(), inner.getTop(),
                                inner.getRight(), inner.getBottom());
                    }
                    // 移动布局
                    inner.layout(inner.getLeft(), inner.getTop() - deltaY / 2,
                            inner.getRight(), inner.getBottom() - deltaY / 2);
                }
                isCount = true;
                break;

            default:
                break;
        }
    }

    /***
     * 回缩动画
     */
    public void animation() {
        // 开启移动动画
        TranslateAnimation ta = new TranslateAnimation(0, 0, inner.getTop(),
                normal.top);
        ta.setDuration(200);
        inner.startAnimation(ta);
        // 设置回到正常的布局位置
        inner.layout(normal.left, normal.top, normal.right, normal.bottom);

        normal.setEmpty();

    }

    // 是否需要开启动画
    public boolean isNeedAnimation() {
        return !normal.isEmpty();
    }

    /***
     * 是否需要移动布局 inner.getMeasuredHeight():获取的是控件的总高度
     *
     * getHeight()：获取的是屏幕的高度
     *
     * @return
     */
    public boolean isNeedMove() {
        int offset = inner.getMeasuredHeight() - getHeight();
        int scrollY = getScrollY();
        // 0是顶部，后面那个是底部
        if (scrollY == 0 || scrollY == offset) {
            return true;
        }
        return false;
    }

    private void clear0() {
        lastX = 0;
        lastY = 0;
        distanceX = 0;
        distanceY = 0;
        upDownSlide = false;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (scrollViewForTabListener != null) {
            scrollViewForTabListener.onScrollChanged(this, l, t, oldl, oldt);
        }

    }

    private ScrollViewForTabListener scrollViewForTabListener;

    public void setScrollViewForTabListener(ScrollViewForTabListener scrollViewForTabListener) {
        this.scrollViewForTabListener = scrollViewForTabListener;
    }

    public interface ScrollViewForTabListener {
        void onScrollChanged(BounceScrollView bounceScrollView, int x, int y, int oldx, int oldy);
    }
}
