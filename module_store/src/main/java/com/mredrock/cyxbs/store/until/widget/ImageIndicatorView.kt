package com.mredrock.cyxbs.store.until.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.example.module_store.R

/**
 *    author : zz
 *    e-mail : 1140143252@qq.com
 *    date   : 2021/8/2 11:07
 */
class ImageIndicatorView : View {
    private var mImageCount = 1 //图片总数
    private var mPosition = 1 //当前界面的图片所在位置 如 2/3 即总图片数为3 当前图片是第二张
    private var mProgress = 0f //当前页面滑动时的进度
    private lateinit var mIndicatorUnderPaint: Paint //进度条底部矩形画笔
    private lateinit var mIndicatorSelectPaint: Paint //进度条选中矩形画笔
    private var mUnderRect = RectF() //底部矩形
    private var mSelectRect = RectF() //选中矩形
    private var mRectRadius=15f //矩形圆角角度

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
            context,
            attrs,
            defStyleAttr
    ) {
        //获取相关属性
        initAttrs(context, attrs)
        //创建画笔
        initPaints()
    }

    //设置图片总数
    fun setImageCount(imageCount: Int) {
        mImageCount = imageCount
        invalidate()
    }

    private fun initAttrs(context: Context?, attrs: AttributeSet?) {
        if (context != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.ImageIndicatorView)
            mImageCount = a.getInt(R.styleable.ImageIndicatorView_imageCount, 1)
            mRectRadius=a.getFloat(R.styleable.ImageIndicatorView_radius,15f)
        }
    }

    private fun initPaints() {
        //进度条底部矩形画笔
        mIndicatorUnderPaint = Paint()
        mIndicatorUnderPaint.color = ContextCompat.getColor(context, R.color.store_image_indicator_under)
        mIndicatorUnderPaint.style = Paint.Style.FILL
        mIndicatorUnderPaint.isAntiAlias = true
        //进度条选中矩形画笔
        mIndicatorSelectPaint = Paint()
        mIndicatorSelectPaint.color = ContextCompat.getColor(context, R.color.store_image_indicator_select)
        mIndicatorSelectPaint.style = Paint.Style.FILL
        mIndicatorSelectPaint.isAntiAlias = true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        setMeasuredDimension(widthSize, heightSize)
    }

    override fun onDraw(canvas: Canvas?) {
        //绘制矩形
        drawRoundRect(canvas)
    }

    private fun drawRoundRect(canvas: Canvas?) {
        //配置底部矩形
        mUnderRect.top = 0f
        mUnderRect.bottom = measuredHeight.toFloat()
        mUnderRect.left = 0f
        mUnderRect.right = measuredWidth.toFloat()
        canvas?.drawRoundRect(mUnderRect, mRectRadius, mRectRadius, mIndicatorUnderPaint)

        //配置选中矩形
        mSelectRect.top = 0f
        mSelectRect.bottom = measuredHeight.toFloat()
        mSelectRect.left = (measuredWidth * ((mPosition - 1) / mImageCount.toFloat()))+(1/mImageCount.toFloat())*measuredWidth*mProgress
        mSelectRect.right = (measuredWidth * (mPosition / mImageCount.toFloat()))+(1/mImageCount.toFloat())*measuredWidth*mProgress
        canvas?.drawRoundRect(mSelectRect, mRectRadius, mRectRadius, mIndicatorSelectPaint)
    }

    //更新进度条
    fun updateIndicator(position:Int,progress: Float) {
        mProgress = progress
        mPosition=position+1
        invalidate()
    }
}