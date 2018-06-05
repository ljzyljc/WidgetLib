package com.finance.widgetlib.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ListView;

/**
 * Created by Jackie on 2018/5/22.
 * 自定义listview，可以获取点击的X,Y坐标
 */
public class CustomXYListview extends ListView{
    private static final String TAG = "jackie";


    public CustomXYListview(Context context) {
        super(context);
    }

    public CustomXYListview(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomXYListview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    public int xDownPosition = 0;
    public int yDownPosition = 0;

    public int getxDownPosition() {
        return xDownPosition;
    }

    public int getyDownPosition() {
        return yDownPosition;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                xDownPosition = (int) ev.getRawX();
                yDownPosition = (int) ev.getRawY();
                break;

        }
        return super.dispatchTouchEvent(ev);
    }

}
