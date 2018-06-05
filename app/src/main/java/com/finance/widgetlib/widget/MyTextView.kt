package com.finance.forward.widget

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.widget.TextView
import android.graphics.RectF
import android.view.View
import com.finance.widgetlib.R


/**
 * 自定义圆角文本宽
 * Created by 彭治铭 on 2018/5/20.
 */
class MyTextView(context: Context?, attrs: AttributeSet?) : TextView(context, attrs) {
    var left_top: Float = 0f//左上角
    var left_bottom: Float = 0f//左下角
    var right_top = 0f//右上角
    var right_bottom = 0f//右下角

    init {
        setLayerType(View.LAYER_TYPE_HARDWARE, null)
        val typedArray = context?.obtainStyledAttributes(attrs, R.styleable.RoundCornersRect)
        typedArray?.let {
            var default=typedArray?.getDimension(R.styleable.RoundCornersRect_radian_all,0f)
            left_top = typedArray?.getDimension(R.styleable.RoundCornersRect_radian_left_top, default)
            left_bottom = typedArray?.getDimension(R.styleable.RoundCornersRect_radian_left_bottom, default)
            right_top = typedArray?.getDimension(R.styleable.RoundCornersRect_radian_right_top, default)
            right_bottom = typedArray?.getDimension(R.styleable.RoundCornersRect_radian_right_bottom, default)
        }
    }

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        canvas?.let {
            //利用内补丁画圆角。只对负补丁有效(防止和正补丁冲突，所以取负)
            var paint = Paint()
            paint.isDither = true
            paint.isAntiAlias = true
            paint.style = Paint.Style.FILL
            paint.strokeWidth = 0f
            paint.setXfermode(PorterDuffXfermode(PorterDuff.Mode.DST_IN))//取下面的交集

            // 矩形弧度
            val radian = floatArrayOf(left_top!!, left_top!!, right_top, right_top, right_bottom, right_bottom, left_bottom, left_bottom)
            // 矩形
            val rectF = RectF(0f, 0f, width.toFloat(), height.toFloat())
            var path = Path()
            path.addRoundRect(rectF, radian, Path.Direction.CW)
            canvas.drawPath(path, paint)
        }
    }

}