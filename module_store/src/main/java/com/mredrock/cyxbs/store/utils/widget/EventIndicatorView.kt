package com.mredrock.cyxbs.store.utils.widget

import android.annotation.SuppressLint
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
class EventIndicatorView : View {

    //进度条状态 分为两种 一种是左边会跟着进度移动 一种是固定左边 仅右边移动
    enum class IndicatorType() {
        MOVE, FIXED
    }

    private var mCount = 1 //总数
    private var mPosition = 1 //当前所在位置
    private var mProgress = 0f //当前位置移动时的进度 MOVE时需要用到
    private lateinit var mIndicatorUnderPaint: Paint //进度条底部矩形画笔
    private lateinit var mIndicatorTopPaint: Paint //进度条选中矩形画笔
    private var mUnderRect = RectF() //底部矩形
    private var mTopRect = RectF() //顶层矩形
    private var mUnderColor = R.color.store_indicator_under //底部颜色
    private var mTopColor = R.color.store_indicator_top //顶部颜色
    private var mRectRadius = 18f //矩形圆角角度
    private var mType = IndicatorType.FIXED //默认类型

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
        mCount = imageCount
        invalidate()
    }

    //设置类型
    fun setType(type: IndicatorType) {
        mType = type
    }

    @SuppressLint("ResourceAsColor")
    private fun initAttrs(context: Context?, attrs: AttributeSet?) {
        if (context != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.EventIndicatorView)
            mCount = a.getInt(R.styleable.EventIndicatorView_count, 1)
            mRectRadius = a.getFloat(R.styleable.EventIndicatorView_radius, 15f)
            mUnderColor = a.getResourceId(R.styleable.EventIndicatorView_underColor, R.color.store_indicator_under)
            mTopColor = a.getResourceId(R.styleable.EventIndicatorView_topColor, R.color.store_indicator_top)
        }
    }

    private fun initPaints() {
        //进度条底部矩形画笔
        mIndicatorUnderPaint = Paint()
        mIndicatorUnderPaint.color = ContextCompat.getColor(context, mUnderColor)
        mIndicatorUnderPaint.style = Paint.Style.FILL
        mIndicatorUnderPaint.isAntiAlias = true
        //进度条选中矩形画笔
        mIndicatorTopPaint = Paint()
        mIndicatorTopPaint.color = ContextCompat.getColor(context, mTopColor)
        mIndicatorTopPaint.style = Paint.Style.FILL
        mIndicatorTopPaint.isAntiAlias = true
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

        //配置顶部矩形
        when (mType) {
            IndicatorType.MOVE -> {
                mTopRect.top = 0f
                mTopRect.bottom = measuredHeight.toFloat()
                mTopRect.left = (measuredWidth * ((mPosition - 1) / mCount.toFloat())) + (1 / mCount.toFloat()) * measuredWidth * mProgress
                mTopRect.right = (measuredWidth * (mPosition / mCount.toFloat())) + (1 / mCount.toFloat()) * measuredWidth * mProgress
                canvas?.drawRoundRect(mTopRect, mRectRadius, mRectRadius, mIndicatorTopPaint)
            }
            IndicatorType.FIXED -> {
                mTopRect.top = 0f
                mTopRect.bottom = measuredHeight.toFloat()
                mTopRect.left =0f
                mTopRect.right = (measuredWidth * (mPosition / mCount.toFloat())) + (1 / mCount.toFloat()) * measuredWidth * mProgress
                canvas?.drawRoundRect(mTopRect, mRectRadius, mRectRadius, mIndicatorTopPaint)
            }
        }

    }

    //更新进度条
    fun updateIndicator(position: Int, progress: Float) {
        mProgress = progress
        mPosition = position + 1
        invalidate()
    }

    fun updateIndicator(position: Int) {
        mPosition = position
        invalidate()
    }
}