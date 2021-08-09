package com.mredrock.cyxbs.store.utils.widget

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.OvershootInterpolator
import android.widget.LinearLayout
import androidx.core.animation.addListener
import androidx.core.view.NestedScrollingParent2
import androidx.core.view.NestedScrollingParentHelper
import androidx.core.view.ViewCompat
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

/**
 * 继承于 LinearLayout, 可以使用 LinearLayout 全部属性
 *
 * **原理:** 使用了嵌套滑动, 具体实现思路可以查看 [onNestedPreScroll]、[onNestedScroll]
 *
 * **NOTE:** 如果无法布局, 请检查是否设置 orientation="vertical" 属性
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @data 2021/8/6
 */
class SlideUpLayout(
    context: Context,
    attrs: AttributeSet
) : LinearLayout(context, attrs), NestedScrollingParent2 {
    init { orientation = VERTICAL }
    private val mFirstChild by lazy { getChildAt(0) }
    private val mSecondChild by lazy { getChildAt(1) }
    private val mCurrentFirstChildRect = Rect()
    private val mOriginalFirstChildRect by lazy {
        val rect = Rect(mFirstChild.left, mFirstChild.top, mFirstChild.right, mFirstChild.bottom)
        mCurrentFirstChildRect.set(rect)
        rect
    }
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val height = MeasureSpec.getSize(heightMeasureSpec)
        var newHeightMS = heightMeasureSpec
        when (MeasureSpec.getMode(heightMeasureSpec)) {
            MeasureSpec.AT_MOST, MeasureSpec.UNSPECIFIED -> {
                newHeightMS = MeasureSpec.makeMeasureSpec(0, MeasureSpec.EXACTLY)
            }
            MeasureSpec.EXACTLY -> {
                // 为什么直接使用 getChildAt(0).measuredHeight/2？
                // 因为我打 log 后发现整个 View 树会测量三次，所以该 View 第一次测量 getChildAt(0).measuredHeight/2
                // 值为 0，而第二次测量就能得到具体值了
                newHeightMS = MeasureSpec.makeMeasureSpec(height + getChildAt(0).measuredHeight/2, MeasureSpec.EXACTLY)
            }
        }
        super.onMeasure(widthMeasureSpec, newHeightMS)
    }

    private var mLastMoveY = 0
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val y = ev.y.toInt()
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                mLastMoveY = y
            }
            MotionEvent.ACTION_MOVE -> {
                // 下面两个判断为什么放在这里，不放在嵌套处理中?
                // 因为如果放在嵌套处理中,会因为内部嵌套的 View 的坐标系移动,而出现手指移动的错误判断
                if (y > mLastMoveY + 4) { // 在加载上滑动画的时候手指向下滑动
                    if (mIsInterceptFastSlideUp) {
                        mSlowlyMoveAnimate?.cancel()
                        mIsAllowNonTouch = true // 结束
                    }
                }else if (mLastMoveY > y + 4) { // 在加载下滑动画的时候手指向上滑动
                    if (mIsInterceptFastSlideDown) {
                        mSlowlyMoveAnimate?.cancel()
                        mIsAllowNonTouch = true // 结束
                    }
                }
                mLastMoveY = y
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    private var mIsOpenNonTouch = false // 是否开启了惯性滑动
    private var mIsFirstSlide = false // 是否是第一次滑动
    private var mIsExistPreScroll = false
    private val mParentHelper by lazy { NestedScrollingParentHelper(this) }
    override fun onStartNestedScroll(child: View, target: View, axes: Int, type: Int): Boolean {
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL
    }
    override fun onNestedScrollAccepted(child: View, target: View, axes: Int, type: Int) {
        mParentHelper.onNestedScrollAccepted(child, target, axes, type)
        if (type == ViewCompat.TYPE_TOUCH) {
            mIsExistPreScroll = false // 还原
            mIsFirstSlide = true // 开始
            mIsOpenNonTouch = false // 还原
        }else {
            mIsOpenNonTouch = true // 惯性滑动的也会调用 Accepted，且在非惯性调用 Stop 前调用
        }
    }
    override fun onStopNestedScroll(target: View, type: Int) {
        mParentHelper.onStopNestedScroll(target, type)
        if (type == ViewCompat.TYPE_TOUCH && !mIsOpenNonTouch) {
            slideOver()
        }else if (type == ViewCompat.TYPE_NON_TOUCH){
            slideOver()
        }
    }
    override fun onNestedScroll(
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int
    ) {
        if (dyUnconsumed > 0) { // 向上滑, 此时一定处于 RecyclerView 底部
            unconsumedSlideUp(dyUnconsumed, type)
        }else if (dyUnconsumed < 0) { // 向下滑, 此时一定处于 RecyclerView 顶部
            unconsumedSlideDown(dyUnconsumed, type)
        }
    }

    private fun unconsumedSlideUp(dyUnconsumed: Int, type: Int) {
        when (type) {
            ViewCompat.TYPE_TOUCH -> {
            }
            ViewCompat.TYPE_NON_TOUCH -> {
            }
        }
    }
    private fun unconsumedSlideDown(dyUnconsumed: Int, type: Int) {
        val newSecondTop = mSecondChild.top - dyUnconsumed // 将要移到的位置
        when (type) {
            ViewCompat.TYPE_TOUCH -> {
                // 拦截快速向上滑动, 直接加载动画, 并消耗接下来的所有滑动距离
                if (-mDownMaxDy > 80 && mSecondChild.top != mOriginalFirstChildRect.bottom) {
                    mIsInterceptFastSlideDown = true // 开始
                    mIsAllowNonTouch = false // 开始
                    slowlyAnimate(mSecondChild.top, mOriginalFirstChildRect.bottom,
                        onEnd = { mIsInterceptFastSlideDown = false/*结束*/ },
                        onCancel = { mIsInterceptFastSlideDown = false/*结束*/ },
                        onMove = { moveTo(it) }
                    )
                }
                // 如果不在下边界处,就先滑到下边界
                else if (mSecondChild.top <= mOriginalFirstChildRect.bottom) {
                    // 将要滑到的位置超过了滑动范围
                    if (newSecondTop > mOriginalFirstChildRect.bottom) {
                        moveTo(mOriginalFirstChildRect.bottom)
                    }else {
                        moveTo(newSecondTop)
                    }
                }
            }
            ViewCompat.TYPE_NON_TOUCH -> {
                // 如果不在下边界处,就先滑到下边界
                if (mSecondChild.top <= mOriginalFirstChildRect.bottom) {
                    // 将要滑到的位置超过了滑动范围
                    if (newSecondTop > mOriginalFirstChildRect.bottom) {
                        moveTo(mOriginalFirstChildRect.bottom)
                    }else {
                        moveTo(newSecondTop)
                    }
                }
            }
        }
    }

    private var mUpMaxDy = 0 // 向上滑最大的速度
    private var mDownMaxDy = 0 // 向下滑第一次最大的速度
    private var mIsInterceptFastSlideUp = false // 是否拦截快速向上滑动，在 RecyclerView 滑到顶时，如果速度过大，就拦截惯性滑动
    private var mIsInterceptFastSlideDown = false // 是否拦截快速向下滑动
    private var mIsAllowNonTouch = true // 是否允许惯性滑动
    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
        if (mIsFirstSlide) {
            mIsFirstSlide = false // 结束
            mUpMaxDy = 0 // 还原
            mIsInterceptFastSlideUp = false // 还原
            mIsInterceptFastSlideDown = false // 还原
            mIsAllowNonTouch = true // 还原
            mUpMaxDy = dy
            mIsExistPreScroll = true // 还原
            mUpMaxDy = 0
            mDownMaxDy = 0
        }
        mUpMaxDy = max(dy, mUpMaxDy)
        mDownMaxDy = min(dy, mDownMaxDy)
        if (dy > 0) mDownMaxDy = 0 else mUpMaxDy = 0
        consume(dy, type, target, consumed)
    }
    private fun consume(dy: Int, type: Int, target: View, consumed: IntArray) {
        if (dy > 0) { // 向上滑
            slideUp(dy, type, consumed)
        }else if (dy < 0) { // 向下滑
            slideDown(dy, type, consumed)
        }
    }
    private fun slideUp(dy: Int, type: Int, consumed: IntArray) {
        val newSecondTop = mSecondChild.top - dy // 将要移到的位置
        when (type) {
            ViewCompat.TYPE_TOUCH -> {
                // 如果正处于拦截快速滑动状态, 消耗所有滑动距离
                if (mIsInterceptFastSlideUp || mIsInterceptFastSlideDown) { consumed[1] = dy; return }
                // 拦截快速向上滑动, 直接加载动画, 并消耗接下来的所有滑动距离
                if (mUpMaxDy > 80 && mSecondChild.top != mOriginalFirstChildRect.centerY()) {
                    mIsInterceptFastSlideUp = true // 开始
                    mIsAllowNonTouch = false // 开始
                    consumed[1] = dy
                    slowlyAnimate(mSecondChild.top, mOriginalFirstChildRect.centerY(),
                        onEnd = { mIsInterceptFastSlideUp = false/*结束*/ },
                        onCancel = { mIsInterceptFastSlideUp = false/*结束*/ },
                        onMove = { moveTo(it) }
                    )
                }
                // 如果不在上边界处,就先滑到上边界
                else if (mSecondChild.top >= (mOriginalFirstChildRect.centerY()+1)) {
                    // 将要滑到的位置超过了滑动范围
                    if (newSecondTop <= mOriginalFirstChildRect.centerY()+1) { // 留个1用于继续滑动
                        moveTo(mOriginalFirstChildRect.centerY())
                        consumed[1] = dy
                    }else {
                        moveTo(newSecondTop)
                        consumed[1] = dy
                    }
                }
            }
            ViewCompat.TYPE_NON_TOUCH -> {
                // 如果正处于拦截快速滑动状态, 消耗所有滑动距离
                if (!mIsAllowNonTouch || mIsInterceptFastSlideUp || mIsInterceptFastSlideDown) {
                    consumed[1] = dy
                    return
                }
                // 如果不在上边界处,就先滑到上边界
                if (mSecondChild.top >= (mOriginalFirstChildRect.centerY()+1)) {
                    consumed[1] = dy
                    if (newSecondTop <= mOriginalFirstChildRect.centerY()+1) { // 留个1用于继续滑动
                        moveTo(mOriginalFirstChildRect.centerY())
                    }else {
                        moveTo(newSecondTop)
                    }
                }else {
                    consumed[1] = (dy * 0.6).toInt() // 减速
                }
            }
        }
    }
    private fun slideDown(dy: Int, type: Int, consumed: IntArray) {
        when (type) {
            ViewCompat.TYPE_TOUCH -> {
                if (mIsInterceptFastSlideDown || mIsInterceptFastSlideUp) { consumed[1] = dy; return }
            }
            ViewCompat.TYPE_NON_TOUCH -> {
                if (!mIsAllowNonTouch || mIsInterceptFastSlideUp || mIsInterceptFastSlideDown) {
                    consumed[1] = dy
                    return
                }
            }
        }
    }

    private fun moveTo(newSecondTop: Int) {
        if (newSecondTop == mSecondChild.top) return
        changeFirstChild(newSecondTop)
        changeOtherChild(newSecondTop)
        moveOver(newSecondTop)
    }
    private fun changeFirstChild(newSecondTop: Int) {
        val dy = mCurrentFirstChildRect.bottom - newSecondTop
        val multiple = (mCurrentFirstChildRect.height() - 2 * dy)/mOriginalFirstChildRect.height().toFloat()
        if (multiple in 0F..1F) {
            mFirstChild.alpha = multiple
            mFirstChild.scaleX = multiple
            mFirstChild.scaleY = multiple
        }else if (multiple > 1F) {
            mFirstChild.alpha = 1F
            mFirstChild.scaleX = multiple
            mFirstChild.scaleY = multiple
        }else if (multiple < 0F) {
            mFirstChild.alpha = 0F
            mFirstChild.scaleX = 0F
            mFirstChild.scaleY = 0F
        }
    }
    private fun changeOtherChild(newSecondTop: Int) {
        var height = newSecondTop
        for (it in 1 until childCount) {
            val child = getChildAt(it)
            val l = child.left
            val t = child.top
            val r = child.right
            val b = child.bottom
            child.layout(l, height , r, height + b - t)
            height += b - t
        }
    }
    private fun moveOver(newSecondTop: Int) {
        val dy = mCurrentFirstChildRect.bottom - newSecondTop
        mCurrentFirstChildRect.top += dy
        mCurrentFirstChildRect.bottom -= dy
        val radio = abs(mOriginalFirstChildRect.width() / mOriginalFirstChildRect.height().toFloat())
        mCurrentFirstChildRect.left += (radio * dy).toInt()
        mCurrentFirstChildRect.right -= (radio * dy).toInt()
    }
    private fun slideOver() {
        if (!mIsExistPreScroll) { return } // 这个必须放到最前面
        mIsAllowNonTouch = true // 结束
        if (mIsInterceptFastSlideUp) { return }
        if (mIsInterceptFastSlideDown) { return }
        Log.d("123","(SlideUpLayout5.kt:263)-->> Over")

        val halfY = mOriginalFirstChildRect.centerY() + mOriginalFirstChildRect.height()/4
        if (mSecondChild.top < halfY ) {
            if (mSecondChild.top != mOriginalFirstChildRect.centerY()) {
                slowlyAnimate(mSecondChild.top, mOriginalFirstChildRect.centerY()) { moveTo(it) }
            }
        }else if (mSecondChild.top >= halfY) { // 此时回到展开状态
            if (mSecondChild.top != mOriginalFirstChildRect.bottom) {
                slowlyAnimate(mSecondChild.top, mOriginalFirstChildRect.bottom) { moveTo(it) }
            }
        }
    }

    private var mSlowlyMoveAnimate: ValueAnimator? = null
    private fun slowlyAnimate(
        oldY: Int,
        newY: Int,
        onEnd: (() -> Unit)? = null,
        onCancel: (() -> Unit)? = null,
        onMove: (nowY: Int) -> Unit
    ) {
        mSlowlyMoveAnimate?.let { if (it.isRunning) it.cancel() }
        mSlowlyMoveAnimate = ValueAnimator.ofInt(oldY, newY)
        mSlowlyMoveAnimate?.apply {
            addUpdateListener {
                val nowY = animatedValue as Int
                onMove.invoke(nowY)
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
            interpolator = OvershootInterpolator(2.5F)
            duration = (abs(newY - oldY).toDouble().pow(0.9) + 300).toLong()
            start()
        }
    }
}