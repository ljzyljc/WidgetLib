package com.finance.forward.widget

import android.content.Context
import android.text.method.HideReturnsTransformationMethod
import android.util.AttributeSet
import android.view.View
import android.widget.EditText
import com.finance.widgetlib.R
import com.finance.widgetlib.widget.AsteriskPasswordTransformationMethod

/**
 * 密码可见与不可见
 * Created by 彭治铭 on 2018/5/17.
 */
class EyesView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    var isShow = false//密码是否显示
    var mima: EditText? = null//密码框

    fun onClick(show: () -> Unit, hide: () -> Unit) {
        this.setOnClickListener {
            isShow = !isShow
            if (isShow) {
                //显示
                this.layoutParams.width = resources.getDimension(R.dimen.x47).toInt()
                this.layoutParams.height = resources.getDimension(R.dimen.x28).toInt()
                this.setBackgroundResource(R.drawable.p_yanjing_open)//显示图标
                mima?.setTransformationMethod(HideReturnsTransformationMethod.getInstance()) //显示密码
                show()//显示回调
            } else {
                //不显示
                this?.layoutParams?.width = resources.getDimension(R.dimen.x46).toInt()
                this?.layoutParams?.height = resources.getDimension(R.dimen.x20).toInt()
                this.setBackgroundResource(R.drawable.p_yanjing_biyan)//隐藏图标
                //mima?.setTransformationMethod(PasswordTransformationMethod.getInstance()) //隐藏密码 小圆点
                mima?.transformationMethod = AsteriskPasswordTransformationMethod()//隐藏密码 自定义符合 星号（*）
                hide()//隐藏回调
            }
        }
    }


}