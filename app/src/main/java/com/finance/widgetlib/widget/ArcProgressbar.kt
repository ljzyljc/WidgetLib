package com.finance.forward.widget

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.opengl.ETC1.getHeight
import android.opengl.ETC1.getWidth
import android.os.Build
import android.support.annotation.RequiresApi
import com.finance.widgetlib.R


/**
 * 弧形进度条
 * Created by 彭治铭 on 2018/5/9.
 */
class ArcProgressbar(context: Context, attrs: AttributeSet?) : View(context, attrs) {
    var paint: Paint
    var strokeWidth = resources.getDimensionPixelOffset(R.dimen.y20).toFloat()//边框宽度
    var sweepGradient: Shader? = null//扫描渐变

    init {
        paint = Paint()
        paint.isAntiAlias = true
        paint.isDither = true
        paint.strokeCap = Paint.Cap.ROUND
        paint.strokeJoin = Paint.Join.ROUND
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = strokeWidth
        setLayerType(View.LAYER_TYPE_HARDWARE, paint)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        sweepGradient = SweepGradient((width / 2).toFloat(), (height / 2).toFloat(),
                intArrayOf(Color.parseColor("#ffA3C6F0"),
                        Color.parseColor("#ff418ADF"),
                        Color.parseColor("#ff4D91E1"),
                        Color.parseColor("#ffffffff"),
                        Color.parseColor("#ffffffff"),
                        Color.parseColor("#ffA3C6F0")), null)
        paint.setShader(sweepGradient)
    }

    var bias: Float = 70.5F//百分比
        set(value) {
            field = value
            invalidate()
        }

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        canvas?.let {
            var rectF = RectF(strokeWidth / 2, strokeWidth / 2, width.toFloat() - strokeWidth / 2, height.toFloat() - strokeWidth / 2)
            canvas.drawArc(rectF, -90f, 360 * bias/100, false, paint)
        }
    }

}
