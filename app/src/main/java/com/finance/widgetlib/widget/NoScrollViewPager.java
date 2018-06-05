package com.finance.widgetlib.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by 彭治铭 on 2018/2/6.
 */

public class NoScrollViewPager extends VerticalViewPager {//也可以继承ViewPager

    public NoScrollViewPager(Context context) {
        super(context);
    }

    public NoScrollViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    Boolean isScroll = false;//true 能滑动，false不能滑动

    public Boolean getisScroll() {
        return isScroll;
    }

    public void setisScroll(Boolean scroll) {
        isScroll = scroll;
    }

    //不可以滑动，但是可以setCurrentItem的ViewPager。

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // TODO Auto-generated method stub
        if(!isScroll){//false不能滑动
            if (ev.getAction() == MotionEvent.ACTION_MOVE) {//只屏蔽滑动事件。这样就不会影响子控件【点击，触摸都不会影响】。
                return true; //禁止滑动
            }
        }
        return super.dispatchTouchEvent(ev);
    }

}
