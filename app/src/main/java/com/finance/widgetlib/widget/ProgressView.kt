package com.finance.forward.widget

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View

/**
 * Created by 彭治铭 on 2018/5/2.
 */
class ProgressView : View {

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {}


    var degress: Float = 0f
    override fun draw(canvas: Canvas?) {
        canvas?.rotate(degress, (measuredWidth / 2).toFloat(), (measuredHeight / 2).toFloat())
        degress += 3
        super.draw(canvas)
        invalidate()
    }

}