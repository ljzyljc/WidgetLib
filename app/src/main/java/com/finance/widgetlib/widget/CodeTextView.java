package com.finance.widgetlib.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.finance.commonlib.util.RegexUtils;
import com.finance.commonlib.util.ToastUtils;
import com.finance.widgetlib.listener.baseInterface;

/**
 * 验证码文本框
 * Created by 彭治铭 on 2018/1/30.
 */

@SuppressLint("AppCompatCustomView")
public class CodeTextView extends TextView implements View.OnClickListener {
    public CodeTextView(Context context) {
        super(context);
        init();
    }

    public CodeTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    private void init() {
        setOnClickListener(this);
    }


    String waitText = "重新发送";//发送验证码之后的文本
    boolean isSend = false;//验证码是否已经发送

    int maxSeconds = 60;
    int minSeconds = 0;
    int seconds = maxSeconds;//计时

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            seconds--;
            if (seconds < minSeconds) {
                seconds = maxSeconds;
                isSend = false;
            }
            removeCallbacks(runnable);
            String text = "";//文本
            //等待计时状态
            if (isSend) {
                text = waitText + "(" + seconds + ")";
                postDelayed(runnable, 1000);
            } else {
                //重新发送状态
                text = waitText;
            }
            setText(text);
        }
    };

    EditText edit_tel;//文本编辑框，用来获取手机号
    baseInterface callback;//回调，发生点击事件，并且手机号正确时调用

    /**
     * 设置手机号文本框和回调事件
     *
     * @param edit_tel 文本编辑框，用来获取手机号
     * @param callback 回调，发生点击事件，并且手机号正确时调用
     */
    public void setEditAndCallback(EditText edit_tel, baseInterface callback) {
        this.edit_tel = edit_tel;
        this.callback = callback;
    }

    //验证码点击事件
    @Override
    public void onClick(View v) {
        if (edit_tel == null) {
            return;
        }
        if (edit_tel.getText().toString().trim().length() <= 0) {
            ToastUtils.showToast(getContext(), "手机号不能为空");
            return;
        }
        if (!RegexUtils.getInstance().isMobileNO(edit_tel.getText().toString().trim())) {
            ToastUtils.showToast(getContext(), "请输入正确的手机号");
            return;
        }
        if (callback != null) {
            callback.finish();
        }
    }

    //验证码发送成功时调用[验证码开始计时]
    public void sendSuccess() {
        if (isSend) {
            return;
        }
        if (seconds > 0) {
            isSend = true;
        }
        post(runnable);
    }

}
