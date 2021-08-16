package com.mredrock.cyxbs.store.utils.widget

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.animation.addListener
import com.mredrock.cyxbs.store.R
import java.util.*

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @data 2021/8/15
 */
class TextRollView(
    context: Context,
    attrs: AttributeSet
) : View(context, attrs) {

    fun setText(text: String, isAttachToWindowStart: Boolean) {
        if (text == mLastText) return
        if (text.matches(Regex("^\\d+$"))) { // 输入的文字匹配非负整数才有效
            mChange = {
                mLastText = text
                mNewTextList.clear()
                mNewTextList.addAll(text.chunked(1))
                mDiffZero = mNewTextList.size - mOldTextList.size
                repeat(mNewTextList.size - mOldTextList.size) { mOldTextList.addFirst("0") }
                repeat(mOldTextList.size - mNewTextList.size) { mNewTextList.addFirst("0") }
                if (text.length > mMaxNumber) {
                    mMaxNumber += text.length - mMaxNumber + 1
                    requestLayout()
                }
                slowlyAnimate(
                    0F, 1F,
                    onEnd = {
                        mNewTextList.clear()
                        mNewTextList.addAll(mLastText.chunked(1))
                        mOldTextList.clear()
                        mOldTextList.addAll(mNewTextList)
                        mDiffZero = 0
                    },
                    onCancel = {
                        mNewTextList.clear()
                        mNewTextList.addAll(mLastText.chunked(1))
                        mOldTextList.clear()
                        mOldTextList.addAll(mNewTextList)
                        mDiffZero = 0
                    },
                    onChange = {
                        mRadio = it
//                        alpha = 0.7F * it + 0.3F
                        invalidate()
                    }
                )
            }
            if (!isAttachToWindowStart) {
                post {
                    mChange?.invoke()
                    mChange = null
                }
            }else {
                if (isAttachedToWindow) {
                    mChange?.invoke()
                }
            }
        }
    }

    fun setTextNoAnimate(text: String) {
        mNewTextList.clear()
        mOldTextList.clear()
        mNewTextList.addAll(text.chunked(1))
        mOldTextList.addAll(mNewTextList)
        invalidate()
    }

    fun setTextOnlyAlpha(text: String) {
        mNewTextList.clear()
        mOldTextList.clear()
        mNewTextList.addAll(text.chunked(1))
        mOldTextList.addAll(mNewTextList)
        invalidate()
        slowlyAnimate(0F, 1F,
            onCancel = { alpha = 1F },
            onChange = {
                alpha = it
            }
        )
    }

    private var mLastText = ""
    private var mRadio = 1F
    private val mTextPaint = Paint()
    private val mOneTextWidth: Float
    private val mTextHeight: Float
    private val mTextDrawHeight: Float
    private var mMaxNumber = 8

    init {
        val ty = context.obtainStyledAttributes(attrs, R.styleable.TextRollView)
        val size = ty.getDimension(R.styleable.TextRollView_view_textSize, 60F)
        val font = Typeface.createFromAsset(context.assets, "font/store_my_stamp_number.ttf")
        val color = ty.getColor(R.styleable.TextRollView_view_textColor, 0xFF000000.toInt())
        ty.recycle()
        mTextPaint.apply {
            isAntiAlias = true
            textSize = size
            this.color = color
            typeface = font
        }
        mOneTextWidth = mTextPaint.measureText("0")
        val fm = mTextPaint.fontMetrics
        mTextHeight = fm.descent - fm.ascent
        mTextDrawHeight = (fm.bottom - fm.top) / 2F - fm.bottom
    }

    private var mDiffZero = 0
    private val mOldTextList = LinkedList<String>()
    private val mNewTextList = LinkedList<String>()
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        repeat(mOldTextList.size) {
            val start = mOldTextList[it].toInt()
            val end = mNewTextList[it].toInt()
            if (start < end) {
                repeat(end - start + 1) { i ->
                    canvas.drawText(
                        (start + i).toString(),
                        getTextX(it),
                        getTextY(start, end, start + i),
                        mTextPaint
                    )
                }
            }else {
                repeat(start - end + 1) { i ->
                    canvas.drawText(
                        (start - i).toString(),
                        getTextX(it),
                        getTextY(start, end, start - i),
                        mTextPaint
                    )
                }
            }
        }
        if (mSlowlyMoveAnimate == null) {
            repeat(mNewTextList.size) {
                canvas.drawText(
                    mNewTextList[it],
                    getTextX(it),
                    getTextY(0, 0, 0),
                    mTextPaint
                )
            }
        }
    }

    private fun getTextX(position: Int): Float {
        return if (mDiffZero > 0) {
            (position - mDiffZero * (1 - mRadio)) * mOneTextWidth
        }else if (mDiffZero < 0){
            (position + mDiffZero * mRadio) * mOneTextWidth
        }else {
            position * mOneTextWidth
        }
    }

    private fun getTextY(start: Int, end: Int, now: Int): Float {
        val moveH = (end - start) * mTextHeight * mRadio
        val diffH = (now - start) * mTextHeight
        return height / 2F + moveH - diffH + mTextDrawHeight
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var wMS = widthMeasureSpec
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        if (widthMode == MeasureSpec.AT_MOST || widthMode == MeasureSpec.UNSPECIFIED) {
            wMS = MeasureSpec.makeMeasureSpec(
                (mMaxNumber * mOneTextWidth).toInt(),
                MeasureSpec.EXACTLY
            )
        }
        var hMS = heightMeasureSpec
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        if (heightMode == MeasureSpec.AT_MOST || heightMode == MeasureSpec.UNSPECIFIED) {
            hMS = MeasureSpec.makeMeasureSpec(mTextHeight.toInt(), MeasureSpec.EXACTLY)
        }
        super.onMeasure(wMS, hMS)
    }

    private var mSlowlyMoveAnimate: ValueAnimator? = null
    private fun slowlyAnimate(
        old: Float,
        new: Float,
        onEnd: (() -> Unit)? = null,
        onCancel: (() -> Unit)? = null,
        onChange: (now: Float) -> Unit
    ) {
        mSlowlyMoveAnimate?.let { if (it.isRunning) it.cancel() }
        mSlowlyMoveAnimate = ValueAnimator.ofFloat(old, new)
        mSlowlyMoveAnimate?.apply {
            addUpdateListener {
                val now = animatedValue as Float
                onChange.invoke(now)
            }
            addListener(
                onEnd = {
                    onEnd?.invoke()
                    mSlowlyMoveAnimate = null
                },
                onCancel = {
                    onCancel?.invoke()
                    mSlowlyMoveAnimate = null
                }
            )
            interpolator = AccelerateDecelerateInterpolator()
            duration = 400L
            start()
        }
    }

    private var mChange: (() -> Unit)? = null
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        post {
            mChange?.invoke()
            mChange = null
        }
    }
}