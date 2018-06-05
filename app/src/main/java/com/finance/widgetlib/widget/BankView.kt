package com.finance.forward.widget

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.finance.commonlib.util.AssetsUtils
import com.finance.commonlib.util.ProportionUtils
import com.finance.widgetlib.R

/**
 * 银行卡
 * Created by 彭治铭 on 2018/5/17.
 */
class BankView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    var rightBitmap: Bitmap//右边图标，有点击事件时是返回键，没有时，是银联图标

    var bankName = "中国银行"//银行卡名称
    var bankLogo: Bitmap? = null//银行卡Logo图标
    var bankNo: String = "6229001234567893323"//银行卡号
    var timiInfo = "此卡为默认提现银行卡"
        set(value) {
            field = value
            invalidate()
        }

    init {
        setBackgroundResource(R.mipmap.bank_bg)
        rightBitmap = AssetsUtils.getInstance().getBitmapFromAssets(null, R.mipmap.bank_yinglian, true)
        rightBitmap = ProportionUtils.getInstance().adapterBitmap(rightBitmap)
        updateLogo()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        //super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(resources.getDimensionPixelOffset(R.dimen.x704), resources.getDimensionPixelOffset(R.dimen.x366))
    }

    //添加点击事件
    fun onClick(call: () -> Unit): BankView {
        setOnClickListener {
            call()
        }
        rightBitmap = AssetsUtils.getInstance().getBitmapFromAssets(null, R.drawable.top_back_white_rc, true)
        rightBitmap = ProportionUtils.getInstance().adapterBitmap(rightBitmap)
        invalidate()
        return this
    }


    //获取银行卡前五位
    fun getBankNoHead(): String {
        return bankNo.substring(0, 5)
    }


    //获取银行卡中间的星星们
    fun getBankNoCenter(): String {
        val s1 = bankNo.substring(0, 5)
        val s2 = bankNo.substring(bankNo.length - 4, bankNo.length)
        val num = bankNo.length - s1.length - s2.length - 1
        var s3 = ""
        for (i in 0 until num) {
            if (i == 4) {
                s3 += "\t*"
            } else if (i == 8) {
                s3 += "\t*"
            } else {
                s3 += "*"
            }
        }
        s3 = "\t" + s3 + "\t"
        return s3
    }

    //获取银行卡后四位
    fun getBankNoTail(): String {
        return bankNo.substring(bankNo.length - 4, bankNo.length)
    }

    //更新Logo图标
    fun updateLogo() {
        bankLogo = getBankLogo(bankName)
        //var f= 185f/124f
        var f= 155f/124f
        bankLogo = Bitmap.createScaledBitmap(bankLogo, (bankLogo!!.getWidth() *f).toInt(), (bankLogo!!.getHeight() * f).toInt(), true)
    }

    companion object {
        //根据银行名称，获取对应图片
        fun getBankLogo(bankName: String): Bitmap {
            var resid = R.mipmap.bank_china
            if (bankName.contains("中国银行")) {
                resid = R.mipmap.bank_china
            } else if (bankName.contains("中国工商银行")) {
                resid = R.mipmap.bank_china_gongshang
            } else if (bankName.contains("中信银行")) {
                resid = R.mipmap.bank_zhongxinyinghang
            } else if (bankName.contains("招商银行")) {
                resid = R.mipmap.bank_zhaoshang
            } else if (bankName.contains("中国民生银行")) {
                resid = R.mipmap.bank_mignsheng
            } else if (bankName.contains("兴业银行")) {
                resid = R.mipmap.bank_xingye
            } else if (bankName.contains("广发银行")) {
                resid = R.mipmap.bank_guangfa
            } else if (bankName.contains("浦发银行")) {
                resid = R.mipmap.bank_pufa
            } else if (bankName.contains("中国邮政储蓄银行")) {
                resid = R.mipmap.bank_youzheng
            } else if (bankName.contains("交通银行")) {
                resid = R.mipmap.bank_jiantong
            } else if (bankName.contains("平安银行")) {
                resid = R.mipmap.bank_pingan
            } else if (bankName.contains("北京银行")) {
                resid = R.mipmap.bank_beijing
            } else if (bankName.contains("北京农商银行")) {
                resid = R.mipmap.bank_nongshang
            } else if (bankName.contains("中国建设银行")) {
                resid = R.mipmap.bank_jianhang
            } else if (bankName.contains("中国农业银行")) {
                resid = R.mipmap.bank_nongye
            } else if (bankName.contains("浙商银行")) {
                resid = R.mipmap.bank_zheshang
            } else if (bankName.contains("华夏银行")) {
                resid = R.mipmap.bank_huaxia
            } else if (bankName.contains("中国光大银行")) {
                resid = R.mipmap.bank_guangda
            } else if (bankName.contains("南京银行")) {
                resid = R.mipmap.bank_nanjing
            } else if (bankName.contains("渤海银行")) {
                resid = R.mipmap.bank_bohai
            } else if (bankName.contains("浙江泰隆商业银行")) {
                resid = R.mipmap.bank_zhejiangtailong
            } else if (bankName.contains("上海银行")) {
                resid = R.mipmap.bank_shanghai
            } else if (bankName.contains("宁波银行")) {
                resid = R.mipmap.bank_ningbo
            } else if (bankName.contains("东亚银行")) {
                resid = R.mipmap.bank_dongya
            } else if (bankName.contains("杭州银行")) {
                resid = R.mipmap.bank_hangzhou
            }
            var bankLogo = AssetsUtils.getInstance().getBitmapFromAssets(null, resid, true)
            return ProportionUtils.getInstance().adapterBitmap(bankLogo)
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let {
            var paint = Paint()
            paint.isAntiAlias = true
            paint.isDither = true
            paint.color = Color.WHITE
            paint.textAlign = Paint.Align.LEFT
            if (bankNo.trim().length > 0) {
                //卡号
                paint.textSize = resources.getDimension(R.dimen.x46)
                paint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD))
                val h = (paint.descent() - paint.ascent()).toInt()
                var x = resources.getDimension(R.dimen.x92)
                var y = resources.getDimension(R.dimen.x203)
                canvas.drawText(getBankNoHead(), x.toFloat(), y.toFloat(), paint)//前五位
                var w = paint.measureText(getBankNoHead(), 0, getBankNoHead().length).toInt()
                x = x + w + resources.getDimension(R.dimen.x26)
                canvas.drawText(getBankNoCenter(), x.toFloat(), (y + h / 8).toFloat(), paint)//中间的星星们
                w = paint.measureText(getBankNoCenter(), 0, getBankNoCenter().length).toInt()
                x = x + w + resources.getDimension(R.dimen.x15)
                canvas.drawText(getBankNoTail(), x.toFloat(), y.toFloat(), paint)//后四位
            }
            paint.textSize = resources.getDimension(R.dimen.x20)
            paint.textAlign=Paint.Align.RIGHT
            canvas.drawText(timiInfo, resources.getDimension(R.dimen.x681), resources.getDimension(R.dimen.x333), paint)

            var y = resources.getDimensionPixelOffset(R.dimen.x49)//左边图标，中心坐标

            //左边银行Logo
            bankLogo?.let {
                canvas.drawBitmap(bankLogo, resources.getDimension(R.dimen.x24), (y - bankLogo!!.height / 2).toFloat(), paint)
            }
            //右边图标
            canvas.drawBitmap(rightBitmap, width - rightBitmap.width - resources.getDimension(R.dimen.x24), (y - rightBitmap!!.height / 2).toFloat(), paint)
        }


    }

}