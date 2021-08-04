package com.mredrock.cyxbs.store.utils.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import com.example.module_store.R
import com.mredrock.cyxbs.common.utils.extensions.dp2px

/**
 *    author : zz
 *    e-mail : 1140143252@qq.com
 *    date   : 2021/8/4 10:22
 *
 *    用圆点来展示图片显示进度
 *    根据View宽度、圆点个数与半径自动均分圆点间隔
 */
class ProgressDotView : View {
    private var mRadius = 3f //圆点半径
    private var mInterval = 0f //两圆点间隔
    private var mDotCount = 3 //圆点个数
    private var mUnselectedColor = R.color.store_progress_dot_unselected //未选中的圆点颜色
    private var mSelectedColor = R.color.store_progress_dot_selected //选中的圆点颜色
    private var mUnselectedDotPaint = Paint() //未选中圆点画笔
    private var mSelectedDotPaint = Paint() //选中圆点画笔
    private var mPosition = 0 //当前选中的位置 从0开始

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        //获取相关属性
        initAttrs(context, attrs)
        //初始化画笔
        initPaint()
    }

    private fun initInterval() {
        mInterval = (measuredWidth.toFloat() - mDotCount * mRadius * 2) / (mDotCount - 1)
    }

    @SuppressLint("ResourceAsColor")
    private fun initPaint() {
        mUnselectedDotPaint.apply {
            color = ContextCompat.getColor(context, mUnselectedColor)
            style = Paint.Style.FILL
            isAntiAlias = true

        }
        mSelectedDotPaint.apply {
            color = ContextCompat.getColor(context, mSelectedColor)
            style = Paint.Style.FILL
            isAntiAlias = true
        }
    }

    private fun initAttrs(context: Context?, attrs: AttributeSet?) {
        if (context != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.EventIndicatorView)
            mRadius = a.getFloat(R.styleable.ProgressDotView_dotRadius, 3f)
            mRadius=context.dp2px(mRadius).toFloat()
            mDotCount = a.getInt(R.styleable.ProgressDotView_dotCount, 3)
            mUnselectedColor = a.getResourceId(R.styleable.ProgressDotView_unSelectedColor, R.color.store_progress_dot_unselected)
            mSelectedColor = a.getResourceId(R.styleable.ProgressDotView_selectedColor, R.color.store_progress_dot_selected)
        }
    }

    override fun onDraw(canvas: Canvas?) {
        //计算间距
        initInterval()
        //绘制点
        drawDot(canvas)
    }

    private fun drawDot(canvas: Canvas?) {
        if (canvas != null) {
            for (i in 0 until mDotCount) {
                //根据位置绘制不同颜色的圆点
                if (i == mPosition) {
                    canvas.drawCircle(mRadius + i * mRadius * 2 + i * mInterval, measuredHeight / 2f, mRadius, mSelectedDotPaint)
                } else {
                    canvas.drawCircle(mRadius + i * mRadius * 2 + i * mInterval, measuredHeight / 2f, mRadius, mUnselectedDotPaint)
                }
            }
        }
    }

     fun updatePosition(position:Int){
        mPosition=position
        invalidate()
    }
}