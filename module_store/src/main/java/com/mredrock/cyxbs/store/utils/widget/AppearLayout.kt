package com.mredrock.cyxbs.store.utils.widget

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.widget.FrameLayout
import com.mredrock.cyxbs.store.R

/**
 * 用于邮票中心界面右上角的邮票显示
 *
 * 里面给了一个方法用于设置切割线和透明度
 *
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @data 2021/8/10
 */
class AppearLayout(
    context: Context,
    attrs: AttributeSet?
) : FrameLayout(context, attrs) {

    /**
     * 设置移动多少, 会移动切割线和透明度
     * @param multiple
     */
    fun setMultiple(multiple: Float) {
        when {
            multiple < 0F -> rect.set(0, 0, width, height)
            multiple > 1F -> rect.set(width, 0, width, height)
            else -> rect.set((multiple * width).toInt(), 0, width, height)
        }
        alpha = multiple
        invalidate()
    }

    private val bgColor: Int

    init {
        val ty = context.obtainStyledAttributes(attrs, R.styleable.AppearLayout)
        bgColor = ty.getColor(R.styleable.AppearLayout_bgColor, 0xFFF8F2F2.toInt())
        ty.recycle()
    }
    private val rect by lazy { Rect(0, 0, width, height) }
    private val paint by lazy {
        val paint = Paint()
        paint.color = bgColor
        paint
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        canvas.drawRect(rect, paint)
    }
}