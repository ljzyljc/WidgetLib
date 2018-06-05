package com.finance.widgetlib.widget;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.finance.commonlib.util.NumberUtils;
import com.finance.commonlib.util.ProportionUtils;


/**
 * 圆形或圆角矩形图片,默认为圆角矩形图片
 * Created by 彭治铭 on 2017/5/22.
 */

public class CircleRoundView extends View {
    public CircleRoundView(Context context) {
        super(context);
        onCreate();
    }

    public CircleRoundView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        onCreate();
    }

    private void onCreate() {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        setLayerType(LAYER_TYPE_HARDWARE, paint);
    }

    Bitmap bitmap;
    private float mRadus = ProportionUtils.getInstance().adapterInt(30);//矩形圆角半径，或者圆的半径
    private Path path;
    private Paint paint;

    public float getmRadus() {
        return mRadus;
    }

    public void setmRadus(float mRadus) {
        this.mRadus = mRadus;
    }

    String text = "游";

    boolean isShowBg = false;//是否显示背景颜色

    public void setShowBg(boolean showBg) {
        isShowBg = showBg;
    }

    //设置文本【一个字】
    public CircleRoundView setText(String text) {
        this.text = text;
        return this;
    }

    //随机颜色
    int[] clors = new int[]
            {Color.parseColor("#5E97E4"), Color.parseColor("#D61F25"), Color.parseColor("#39B54A"), Color.parseColor("#8080FF"),
                    Color.parseColor("#0000FF"), Color.parseColor("#5ED0E4"), Color.parseColor("#8080FF"), Color.parseColor("#FF00FF"),
                    Color.parseColor("#F1A655")};

    @Override
    public void draw(Canvas canvas) {
        //canvas.drawColor(clors[0]);
        //Log.e("test", "1:\t width:\t" + getWidth() + "\theight:\t" + getHeight());
        if (isShowBg) {
            //后面如果有位图加载，会覆盖这一块
            String s = NumberUtils.getInstance().getRandom(1);
            //Log.e("test","随机值:\t"+s);
            int radom = Integer.valueOf(s);
            if (radom < 0) {
                radom = 0;
            }
            if (radom >= clors.length) {
                radom = clors.length - 1;
            }
            canvas.drawColor(clors[radom]);
            Paint tPaint = new Paint();
            tPaint.setColor(Color.WHITE);
            tPaint.setAntiAlias(true);
            tPaint.setDither(true);
            tPaint.setTextSize(ProportionUtils.getInstance().adapterInt(50));
            tPaint.setTextAlign(Paint.Align.CENTER);
            int h = (int) (tPaint.descent() - tPaint.ascent());
            int off = (getHeight() - h) / 3;
            canvas.drawText(text, getWidth() / 2, h + off, tPaint);
        }
        super.draw(canvas);
        if (bitmap != null && !bitmap.isRecycled()) {
            canvas.drawBitmap(bitmap, 0, 0, null);
        }
        if (path == null) {
            path = new Path();
            if (isCircle) {
                mRadus = getWidth() > getHeight() ? getHeight() : getWidth();//取宽和高较小的一边。
                mRadus = mRadus / 2;
                //raidus = (int) (raidus - mRadus);
                //Log.e("test","x:\t"+getWidth()/2+"\ty:\t"+getHeight()/2+"\t半径:\t"+raidus);
                path.addCircle(getWidth() / 2, getHeight() / 2, mRadus, Path.Direction.CW);//圆形图片
                //Log.e("test", "半径:\t" + mRadus + "\t宽:\t" + getWidth() + "\t高:\t" + getHeight());
            } else {
                path.addRoundRect(new RectF(0 - mRadus / 2, 0 - mRadus / 2, getWidth() + mRadus / 2, getHeight() + mRadus / 2), mRadus, mRadus, Path.Direction.CW);//圆角矩形图片
            }
            paint = new Paint();
            paint.setColor(Color.TRANSPARENT);
            paint.setAntiAlias(true);
            paint.setDither(true);
            if (isCircle) {
                //圆
                paint.setStyle(Paint.Style.FILL);//只画边框。
                paint.setStrokeWidth(mRadus);//设置边框的宽度。
                paint.setColor(Color.RED);
                //矩形
                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));//只显示下面的交集
            } else {
                paint.setStyle(Paint.Style.STROKE);//只画边框。
                paint.setStrokeWidth(mRadus);//设置边框的宽度。
                //矩形
                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));//因为只画边框，所以会清除边框的交集。
            }

        }
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        canvas.drawPath(path, paint);
    }

    public void setBitmap(Bitmap bitmap) {
        if(bitmap!=null&&bitmap.getWidth()>0&&bitmap.getHeight()>0){
            int w=mWidht;
            int h=mHeight;
            if(w<=0){
                w=getWidth();
            }
            if(h<=0){
                h=getHeight();
            }
            this.bitmap = Bitmap.createScaledBitmap(bitmap,w,h,true);//将位图压缩成和自己一样大。
        }else {
            bitmap=null;
        }
        invalidate();
    }

    //直接设置背景图片。效果和setBitmap()一样。都是圆角矩形。
    @Override
    public void setBackground(Drawable background) {
        super.setBackground(background);
    }

    boolean isCircle = false;//true为圆形，false为矩形。默认为圆角矩形图片。

    public void setCircle(boolean circle) {
        isCircle = circle;
    }

    public void recycle() {
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
            bitmap = null;
            setBackground(null);
            System.gc();
        }
    }

    int mWidht=0,mHeight=0;

    public void setWidthAndHeight(int widht,int height){
        this.mWidht=widht;
        this.mHeight=height;
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if(mWidht!=0&&mHeight!=0){
            setMeasuredDimension(mWidht,mHeight);
        }else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }
}
