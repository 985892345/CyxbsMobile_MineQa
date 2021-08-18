package com.mredrock.cyxbs.store.utils.widget

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import androidx.core.animation.addListener
import androidx.core.view.*
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

/**
 * **WARNING:** 只有第一层下的第一个 View(ViewGroup) 才能改变大小, 且那个 View 的**高度必须为确定值**,
 * 第二个 View(ViewGroup) 不受限制
 *
 * **原理:** 使用了嵌套滑动, 具体实现思路可以查看 [onNestedPreScroll]、[onNestedScroll]
 *
 * **NOTE:** 想修改能够滑动的距离请看 [mCanMoveHeight]
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @data 2021/8/6
 */
class SlideUpLayout(
    context: Context,
    attrs: AttributeSet
) : ViewGroup(context, attrs), NestedScrollingParent2 {

    /**
     * 设置移动监听
     *
     * 完全展开时 multiple >= 1, 收拢时 multiple <= 0
     *
     * @param l 其中 multiple 可能为 `[-0.5, 1.5]` 的值, 因为有过弹动画, 所以**存在负值**
     */
    fun setMoveListener(l: (multiple: Float) -> Unit) {
        mMoveListener = l
    }

    /**
     * 是否是展开状态
     */
    fun isUnfold(): Boolean {
        return mSecondChild.top >= mOriginalFirstChildRect.bottom
    }

    /**
     * 设置第一个 View 完全展开时的回调
     */
    fun setUnfoldCallBack(call: () -> Unit) {
        if (mSecondChild.top == mOriginalFirstChildRect.bottom) {
            call.invoke()
        }
        mUnfoldCallBack = call
    }

    /**
     * 去掉第一个 View 完全展开时的回调
     */
    fun removeUnfoldCallBack() {
        mUnfoldCallBack = null
    }

    private var mUnfoldCallBack: (() -> Unit)? = null
    private var mMoveListener: ((multiple: Float) -> Unit)? = null
    private val mFirstChild by lazy { getChildAt(0) }
    private val mSecondChild by lazy { getChildAt(1) }
    private val mOriginalFirstChildRect by lazy { // 第一个子 View 原始的 Rect, 不会改变
        val rect = Rect(mFirstChild.left, mFirstChild.top, mFirstChild.right, mFirstChild.bottom)
        rect
    }
    private val mCanMoveHeight by lazy { // 能够滑动的距离
        val lp = getChildAt(0).layoutParams
        lp.height // 返回了第一个 View 的高度
    }
    // 能够移动的上限值
    private val mUpperHeight by lazy { mOriginalFirstChildRect.bottom - mCanMoveHeight }
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        var wMS = widthMeasureSpec
        var hMS = heightMeasureSpec
        if (widthMode == MeasureSpec.AT_MOST || widthMode == MeasureSpec.UNSPECIFIED) {
            wMS = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY)
        }
        if (heightMode == MeasureSpec.AT_MOST || heightMode == MeasureSpec.UNSPECIFIED) {
            hMS = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
        }
        setMeasuredDimension(wMS, hMS)
        val child1 = getChildAt(0)
        val child2 = getChildAt(1)
        val lp1 = child1.layoutParams
        val childWidthMS1 = getChildMeasureSpec(wMS, 0, lp1.width)
        val childHeightMS1 = MeasureSpec.makeMeasureSpec(lp1.height, MeasureSpec.EXACTLY)
        child1.measure(childWidthMS1, childHeightMS1)

        val lp2 = child2.layoutParams
        val childWidthMS2 = getChildMeasureSpec(wMS, 0, lp2.width)
        val childHeightMS2: Int = if (lp2.height >= 0) {
            MeasureSpec.makeMeasureSpec(lp2.height, MeasureSpec.EXACTLY)
        }else {
            MeasureSpec.makeMeasureSpec(height - lp1.height + mCanMoveHeight, MeasureSpec.EXACTLY)
        }
        child2.measure(childWidthMS2, childHeightMS2)
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
                if (y > mLastMoveY + 20) { // 在加载上滑动画的时候手指向下滑动
                    if (mIsInterceptFastSlideUp) {
                        mSlowlyMoveAnimate?.cancel()
                        mIsAllowNonTouch = true // 结束
                    }
                }else if (mLastMoveY > y + 20) { // 在加载下滑动画的时候手指向上滑动
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

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var child = mFirstChild
        child.layout(0, 0, child.measuredWidth, child.measuredHeight)
        // 如果第二个 child 的高为 0, 说明是第一次加载
        var height = if (mSecondChild.height == 0) child.measuredHeight else mSecondChild.top
        for (i in 1 until childCount) {
            child = getChildAt(i)
            child.layout(0, height, child.measuredWidth, height + child.measuredHeight)
            height += child.measuredHeight
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        if (childCount != 2) {
            throw RuntimeException("SlideUpLayout: " +
                    "只能拥有两个子 View(ViewGroup), 多的请使用 ViewGroup 来包裹")
        }
    }

    private var mIsOpenNonTouch = false // 是否开启了惯性滑动
    private var mIsFirstSlide = false // 是否是第一次滑动
    private var mIsExistPreScroll = false // 是否存在手指移动
    private val mParentHelper by lazy { NestedScrollingParentHelper(this) }
    override fun onStartNestedScroll(child: View, target: View, axes: Int, type: Int): Boolean {
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL // 只跟垂直滑动进行交互
    }
    override fun onNestedScrollAccepted(child: View, target: View, axes: Int, type: Int) {
        mParentHelper.onNestedScrollAccepted(child, target, axes, type)
        if (type == ViewCompat.TYPE_TOUCH) {
            mIsExistPreScroll = false // 还原
            mIsFirstSlide = true // 开始
            mIsOpenNonTouch = false // 还原
        }else {
            // 惯性滑动的也会调用 onNestedScrollAccepted()，且在非惯性调用 onStopNestedScroll() 前调用
            mIsOpenNonTouch = true
        }
    }
    override fun onStopNestedScroll(target: View, type: Int) {
        mParentHelper.onStopNestedScroll(target, type)
        if (type == ViewCompat.TYPE_TOUCH && !mIsOpenNonTouch) {
            slideOver() // 手指抬起屏幕且没有惯性滑动
        }else if (type == ViewCompat.TYPE_NON_TOUCH){
            slideOver() // 惯性滑动结束
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
            // nothing
        }else if (dyUnconsumed < 0) { // 向下滑, 此时一定处于 RecyclerView 顶部
            unconsumedSlideDown(target, dyUnconsumed, type)
        }
    }

    private fun unconsumedSlideDown(target: View, dyUnconsumed: Int, type: Int): Int {
        val newSecondTop = mSecondChild.top - dyUnconsumed // 将要移到的位置
        when (type) {
            ViewCompat.TYPE_TOUCH -> {
                // 拦截快速向下滑动, 直接加载动画, 并消耗接下来的所有滑动距离
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
                        return mOriginalFirstChildRect.bottom - mSecondChild.top
                    }else {
                        moveTo(newSecondTop)
                    }
                }
            }
            ViewCompat.TYPE_NON_TOUCH -> {
                // 拦截离手时的惯性滑动, 直接加载动画, 并消耗接下来的所有滑动距离
                if (mSecondChild.top != mOriginalFirstChildRect.bottom) {
                    slowlyAnimate(mSecondChild.top, mOriginalFirstChildRect.bottom,
                        onEnd = { mIsInterceptFastSlideDown = false/*结束*/ },
                        onCancel = { mIsInterceptFastSlideDown = false/*结束*/ },
                        onMove = { moveTo(it) }
                    )
                    mIsInterceptFastSlideDown = true // 开始
                    mIsAllowNonTouch = false // 开始
                }
                // 如果不在下边界处,就先滑到下边界
                if (mSecondChild.top <= mOriginalFirstChildRect.bottom) {
                    // 将要滑到的位置超过了滑动范围
                    if (newSecondTop > mOriginalFirstChildRect.bottom) {
                        moveTo(mOriginalFirstChildRect.bottom)
                        return mOriginalFirstChildRect.bottom - mSecondChild.top
                    }else {
                        moveTo(newSecondTop)
                    }
                }
            }
        }
        return dyUnconsumed
    }

    private var mUpMaxDy = 0 // 向上滑最大的速度
    private var mDownMaxDy = 0 // 向下滑第一次最大的速度
    private var mIsInterceptFastSlideUp = false // 是否拦截快速向上滑动，在 RecyclerView 滑到顶时，如果速度过大，就拦截惯性滑动
    private var mIsInterceptFastSlideDown = false // 是否拦截快速向下滑动
    private var mIsAllowNonTouch = true // 是否允许惯性滑动
    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
        if (mIsFirstSlide) {
            mIsFirstSlide = false // 结束
            mIsAllowNonTouch = true // 还原
            mIsExistPreScroll = true // 还原
            mUpMaxDy = 0 // 还原
            mDownMaxDy = 0 // 还原
        }
        mUpMaxDy = max(dy, mUpMaxDy)
        mDownMaxDy = min(dy, mDownMaxDy)
        if (dy > 0) mDownMaxDy = 0 else mUpMaxDy = 0
        consume(dy, type, target, consumed)
    }
    private fun consume(dy: Int, type: Int, target: View, consumed: IntArray) {
        if (type == ViewCompat.TYPE_TOUCH) {
            // 如果正处于拦截快速滑动后的动画状态, 消耗所有滑动距离, 禁止用户滑动(这个状态不会维持很久)
            // 向反方向滑动时应取消动画, 但因 target view 坐标系改变原因, 写在了 dispatchTouchEvent() 中
            if (mIsInterceptFastSlideUp || mIsInterceptFastSlideDown) { consumed[1] = dy; return }
        }else if (type == ViewCompat.TYPE_NON_TOUCH) {
            if (!mIsAllowNonTouch || mIsInterceptFastSlideUp || mIsInterceptFastSlideDown) {
                /*
                * 此时处于拦截快速滑动后的动画状态, 可以取消惯性滑动,
                * 如果不取消, 则会因为 RecyclerView 正处于惯性滑动
                * 而拦截掉 VP2 的左右滑动
                * 可以查看 RecyclerView 源码的第 3199 行, 因处于 SCROLL_STATE_SETTLING
                * 而直接调用了 requestDisallowInterceptTouchEvent(true), 调用后 VP2
                * 再也不会调用 onInterceptTouch() 来拦截事件
                * */
                if (target is RecyclerView) { target.stopScroll() }
                consumed[1] = dy
                return
            }
        }
        // 下面的是允许你滑动的时候
        if (dy > 0) slideUp(target, dy, type, consumed) // 向上滑
        else if (dy < 0) slideDown(target, dy, type, consumed) // 向下滑

    }
    private fun slideUp(target: View, dy: Int, type: Int, consumed: IntArray) {
        val newSecondTop = mSecondChild.top - dy // 将要移到的位置
        when (type) {
            ViewCompat.TYPE_TOUCH -> {
                // 拦截快速向上滑动, 直接加载动画, 并消耗接下来的所有滑动距离
                if (mUpMaxDy > 80 && mSecondChild.top != mUpperHeight) {
                    consumed[1] = dy
                    slowlyAnimate(mSecondChild.top, mUpperHeight,
                        onEnd = { mIsInterceptFastSlideUp = false/*结束*/ },
                        onCancel = { mIsInterceptFastSlideUp = false/*结束*/ },
                        onMove = { moveTo(it) }
                    )
                    mIsInterceptFastSlideUp = true // 开始
                    mIsAllowNonTouch = false // 开始
                }
                // 如果不在上边界处,就先滑到上边界
                else if (mSecondChild.top >= (mUpperHeight+1)) {
                    // 将要滑到的位置超过了滑动范围
                    if (newSecondTop <= mUpperHeight+1) { // 留个1用于继续滑动
                        moveTo(mUpperHeight)
                        consumed[1] = dy
                    }else {
                        moveTo(newSecondTop)
                        consumed[1] = dy
                    }
                }
            }
            ViewCompat.TYPE_NON_TOUCH -> {
                // 如果不在上边界处,就先滑到上边界
                if (mSecondChild.top >= (mUpperHeight+1)) {
                    consumed[1] = dy
                    if (newSecondTop <= mUpperHeight+1) { // 留个1用于继续滑动
                        moveTo(mUpperHeight)
                    }else {
                        moveTo(newSecondTop)
                    }
                }else {
                    consumed[1] = (dy * 0.4).toInt() // 降低惯性滑动, 防止直接滑到底
                }
            }
        }
    }
    private fun slideDown(target: View, dy: Int, type: Int, consumed: IntArray) {
        when (type) {
            ViewCompat.TYPE_TOUCH -> {
                // nothing
            }
            ViewCompat.TYPE_NON_TOUCH -> {
                // nothing
            }
        }
    }

    private fun moveTo(newSecondTop: Int) {
        if (newSecondTop == mSecondChild.top) return
        changeFirstChild(newSecondTop)
        changeOtherChild(newSecondTop)
        moveToOver(newSecondTop)
        mMoveListener?.invoke((newSecondTop - mUpperHeight) / mCanMoveHeight.toFloat())
    }
    // 改变第一个 child
    private fun changeFirstChild(newSecondTop: Int) {
        val half = mOriginalFirstChildRect.height()/2F
        val x = ((newSecondTop - half) / half)
        val k = 0.9F // 调整上下滑动时与第一个 View 的缩放倍速
        val b = 1 - k
        val multiple = k * x + b
        when {
            multiple in 0F..1F -> {
                mFirstChild.alpha = multiple
                mFirstChild.scaleX = multiple
                mFirstChild.scaleY = multiple
            }
            multiple > 1F -> {
                // 得到 multiple 的小数
                val decimals = multiple - multiple.toInt()
                mFirstChild.alpha = 1F
                // 降低因过弹插值器引起的过于放大的影响
                mFirstChild.scaleX = multiple.toInt() + decimals * 0.35F
                mFirstChild.scaleY = multiple.toInt() + decimals * 0.35F
            }
            multiple < 0F -> {
                mFirstChild.alpha = 0F
                mFirstChild.scaleX = 0F
                mFirstChild.scaleY = 0F
            }
        }
        if (multiple == 1F) {
            mUnfoldCallBack?.invoke()
        }
    }
    // 移动其他 child
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

    // 每次 moveTo() 结束
    private fun moveToOver(newSecondTop: Int) {
    }

    // 手指抬起屏幕且没有惯性滑动和惯性滑动时调用
    private fun slideOver() {
        if (!mIsExistPreScroll) { return } // 这个必须放到最前面, 此值用于手指没有移动就结束
        mIsAllowNonTouch = true // 结束
        if (mIsInterceptFastSlideUp || mIsInterceptFastSlideDown) { return } // 此时开启了动画自己在移动

        val halfY = mOriginalFirstChildRect.bottom - mCanMoveHeight/2
        if (mSecondChild.top < halfY ) {
            if (mSecondChild.top != mUpperHeight) {
                slowlyAnimate(mSecondChild.top, mUpperHeight) { moveTo(it) }
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
            interpolator = OvershootInterpolator()
            duration = (abs(newY - oldY).toDouble().pow(0.9) + 280).toLong()
            start()
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mSlowlyMoveAnimate?.let { if (it.isRunning) it.end() }
    }
}