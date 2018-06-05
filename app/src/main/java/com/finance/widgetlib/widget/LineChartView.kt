package com.finance.forward.widget

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.finance.commonlib.util.NumberUtils
import com.finance.widgetlib.R

/**
 * 折线图表
 * Created by 彭治铭 on 2018/5/11.
 */
class LineChartView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    var strokePaint: Paint//画边框
    var strokeWidth = resources.getDimension(R.dimen.x2)
    var textPaint: Paint

    var w = resources.getDimensionPixelOffset(R.dimen.x567)//x轴的宽度
    var h = resources.getDimensionPixelOffset(R.dimen.x284)//y轴的高度
    var offsetX = resources.getDimensionPixelOffset(R.dimen.x69)//x左边的偏移量
    var offsetY = resources.getDimensionPixelOffset(R.dimen.x10)//y上面的偏移量
    var offset = resources.getDimensionPixelOffset(R.dimen.x12)//原点标记长度
    var arrow = resources.getDimensionPixelOffset(R.dimen.x8)//箭头的长度
    var countX = 12//x轴分割个数
    var childWidth = w / (countX + 1)//x轴和y轴每段单位长度[x和y单位长度统一]
    var countY = 5//y軸分割个数
    var childHeight = offset / 2//分隔断的高度
    //坐标原点
    var originPoint: Point = Point(offsetX, h + offsetY)
    //坐标X轴终点
    var endxPoint: Point = Point((originPoint.x + w), originPoint.y)
    //坐标Y轴终点
    var endyPoint: Point = Point(originPoint.x, offsetY)
    //X轴的分段点数组
    var arryX = FloatArray(12)
    //Y轴的分段点数组
    var arryY = FloatArray(5)

    var dataUnitY = ArrayList<String>()//Y轴单位数据
    var dataUnitX = ArrayList<String>()//X轴单位数据
    var unitY: Float = (h / countY.toFloat() / 1000f).toFloat()//y轴，每个点，代表的单位。即两个点之间最小单位。

    var dataCanMoney = ArrayList<Float>()//可用资金
    var linePath = Path()//线条路径
    var linePaint: Paint//线条画笔


    var dataBalance = ArrayList<Float>()//上日结存
    var linePath2 = Path()//线条路径
    var linePaint2: Paint//线条画笔

    var segPath = Path()//部分路径
    var curValue=0f
    set(value) {
        field=value
        invalidate()
    }

    fun start(){
        var propertyValuesHolder = PropertyValuesHolder.ofFloat("curValue", 0f, 1f)
        var objectAnimator = ObjectAnimator.ofPropertyValuesHolder(this, propertyValuesHolder)
        objectAnimator?.duration = 3500
        objectAnimator?.start()
    }

    init {
        strokePaint = getPaint()
        strokePaint.style = Paint.Style.STROKE
        strokePaint.strokeWidth = strokeWidth
        textPaint = getPaint()
        linePaint = getPaint()
        linePaint2 = getPaint()

        //可用资金样式
        linePaint.style=Paint.Style.STROKE
        linePaint.strokeWidth=resources.getDimension(R.dimen.y4)
        linePaint.textAlign=Paint.Align.LEFT
        linePaint.textSize=resources.getDimension(R.dimen.x20)
        linePaint.strokeCap=Paint.Cap.ROUND

        //上日结存
        linePaint2.style=Paint.Style.STROKE
        linePaint2.strokeWidth=resources.getDimension(R.dimen.y4)
        linePaint2.textAlign=Paint.Align.LEFT
        linePaint2.textSize=resources.getDimension(R.dimen.x20)
        linePaint2.strokeCap=Paint.Cap.ROUND

        var cornerPathEffect=CornerPathEffect(resources.getDimension(R.dimen.y36))
        linePaint.setPathEffect(cornerPathEffect)
        linePaint2.setPathEffect(cornerPathEffect)

        setLayerType(View.LAYER_TYPE_HARDWARE, null)


        //x轴
        for (i in 1..countX) {
            dataUnitX.add(i.toString() + "日")
        }

        //y轴
        for (i in 1..countY) {
            dataUnitY.add(i.toString() + "千")
        }

        var unit=1000//每个分割段代表的数据。
        unitY = (childWidth / unit.toFloat()).toFloat()//两个点之间最小单位。

        for(i in 1..9){
            //模拟可用资金
            var f= NumberUtils.getInstance().getRandom(1).toFloat()//随机数
            f=f% countY
            var data=f* unit//原始数据
            dataCanMoney.add(data*unitY)//将数据转换为对应的点
        }

        for(i in 1..9){
            //模拟上日
            var f=NumberUtils.getInstance().getRandom(1).toFloat()//随机数
            f=f% countY
            var data=f* unit//原始数据
            dataBalance.add(data*unitY)//将数据转换为对应的点
        }
    }


    fun getPaint(): Paint {
        var paint = Paint()
        paint.isAntiAlias = true
        paint.isDither = true
        return paint
    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        for (i in 1..countX) {
            arryX[i - 1] = originPoint.x.toFloat() + i * childWidth
        }
        for (i in 1..countY) {
            arryY[i - 1] = originPoint.y.toFloat() - i * childWidth
        }
    }

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        canvas?.let {
            //Log.e("test","宽度:\t"+width+"\t高度:\t"+height+"\tx:\t"+originPoint.x+"\ty:\t"+originPoint.y)
            strokePaint.color = Color.parseColor("#BABABA")
            textPaint.color = Color.parseColor("#000000")
            textPaint.textSize = resources.getDimension(R.dimen.x20)

            //画X轴
            canvas.drawLine(originPoint.x.toFloat(), originPoint.y.toFloat(), endxPoint.x.toFloat(), endxPoint.y.toFloat(), strokePaint)
            //画X轴箭头
            canvas.drawLine(endxPoint.x.toFloat() - arrow, endxPoint.y.toFloat() - arrow, endxPoint.x.toFloat(), endxPoint.y.toFloat() + strokeWidth / 4, strokePaint)
            canvas.drawLine(endxPoint.x.toFloat() - arrow, endxPoint.y.toFloat() + arrow, endxPoint.x.toFloat(), endxPoint.y.toFloat() - strokeWidth / 4, strokePaint)
            //画X轴上分割段
            textPaint.textAlign = Paint.Align.CENTER
            for (i in 0..countX - 1) {
                canvas.drawLine(arryX[i], originPoint.y.toFloat() - childHeight, arryX[i], endxPoint.y.toFloat(), strokePaint)
                //画X轴上的单位
                canvas.drawText(dataUnitX[i], arryX[i], originPoint.y.toFloat() + childHeight + textPaint.textSize + arrow, textPaint)
            }

            //画Y轴
            canvas.drawLine(originPoint.x.toFloat(), originPoint.y.toFloat(), endyPoint.x.toFloat(), endyPoint.y.toFloat(), strokePaint)
            //画Y轴箭头
            canvas.drawLine(endyPoint.x.toFloat() - arrow, endyPoint.y.toFloat() + arrow, endyPoint.x.toFloat() + strokeWidth / 4, endyPoint.y.toFloat(), strokePaint)
            canvas.drawLine(endyPoint.x.toFloat() + arrow, endyPoint.y.toFloat() + arrow, endyPoint.x.toFloat() - strokeWidth / 4, endyPoint.y.toFloat(), strokePaint)
            //画Y轴上分割段
            textPaint.textAlign = Paint.Align.RIGHT
            for (i in 0..countY - 1) {
                canvas.drawLine(originPoint.x.toFloat(), arryY[i], endyPoint.x.toFloat() + childHeight, arryY[i], strokePaint)
                //画Y轴上的单位
                canvas.drawText(dataUnitY[i], endyPoint.x.toFloat() - childHeight - arrow, arryY[i] + textPaint.textSize / 3, textPaint)
            }


            //画文本，单位
            textPaint.textAlign = Paint.Align.LEFT
            textPaint.color = Color.parseColor("#cc000000")
            textPaint.textSize = resources.getDimension(R.dimen.x20)
            canvas.drawText("单位/RMB", endyPoint.x.toFloat() + textPaint.textSize, endyPoint.y.toFloat() + textPaint.textSize / 2, textPaint)
            canvas.drawText("单位/日", endxPoint.x.toFloat() - textPaint.textSize * 1.1f, endxPoint.y.toFloat() - textPaint.textSize * 1.2f, textPaint)

            //画原点标记
            strokePaint.color = Color.parseColor("#7C7C7C")
            canvas.drawLine(originPoint.x.toFloat(), originPoint.y.toFloat(), (originPoint.x + offset).toFloat(), originPoint.y.toFloat(), strokePaint)
            canvas.drawLine(originPoint.x.toFloat(), originPoint.y.toFloat() + strokeWidth / 2, originPoint.x.toFloat(), originPoint.y.toFloat() - offset, strokePaint)

            //fixme ================================================================================以下是数据线条

            //画可用资金
            linePath.reset()
            for(i in 0..dataCanMoney.size-1){
                if(i==0){
                    linePath.moveTo(arryX[i],originPoint.y.toFloat() -dataCanMoney[i])
                }else{
                    linePath.lineTo(arryX[i],originPoint.y.toFloat() -dataCanMoney[i])
                }
                if(i==(dataCanMoney.size-1)&&curValue>=1){
                    linePaint.style=Paint.Style.FILL
                    linePaint.color=Color.parseColor("#3ecc8d")
                    canvas.drawCircle(arryX[i],originPoint.y.toFloat() -dataCanMoney[i],resources.getDimension(R.dimen.y6),linePaint)
                    linePaint.color=Color.parseColor("#3ecc8d")
                    canvas.drawText((dataCanMoney[i]/unitY).toInt().toString(),arryX[i]+linePaint.textSize/2,originPoint.y.toFloat() -dataCanMoney[i]+linePaint.textSize/3,linePaint)
                }
            }
            linePaint.color=Color.parseColor("#3ecc8d")
            linePaint.style=Paint.Style.STROKE
            var pathMeasure=PathMeasure()
            segPath.reset()
            pathMeasure.setPath(linePath,false)
            pathMeasure.getSegment(0f,pathMeasure.length*curValue,segPath,true)
            canvas.drawPath(segPath,linePaint)


            //画上日结存
            linePath2.reset()
            for(i in 0..dataBalance.size-1){
                if(i==0){
                    linePath2.moveTo(arryX[i],originPoint.y.toFloat() -dataBalance[i])
                }else{
                    linePath2.lineTo(arryX[i],originPoint.y.toFloat() -dataBalance[i])
                }
                if(i==(dataBalance.size-1)&&curValue>=1){
                    linePaint2.style=Paint.Style.FILL
                    linePaint2.color=Color.parseColor("#2fc1e1")
                    canvas.drawCircle(arryX[i],originPoint.y.toFloat() -dataBalance[i],resources.getDimension(R.dimen.y6),linePaint2)
                    linePaint2.color=Color.parseColor("#2fc1e1")
                    canvas.drawText((dataBalance[i]/unitY).toInt().toString(),arryX[i]+linePaint2.textSize/2,originPoint.y.toFloat() -dataBalance[i]+linePaint2.textSize/3,linePaint2)
                }
            }
            linePaint2.color=Color.parseColor("#2fc1e1")
            linePaint2.style=Paint.Style.STROKE
            //canvas.drawPath(linePath2,linePaint2)
            segPath.reset()
            pathMeasure.setPath(linePath2,false)
            pathMeasure.getSegment(0f,pathMeasure.length*curValue,segPath,true)
            canvas.drawPath(segPath,linePaint2)

        }
    }

}