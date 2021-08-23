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
 * 用于邮票中心界面上下滑控件
 *
 * **WARNING:** 只有第一层下的第一个 View(ViewGroup) 才能改变大小,
 * 第二个 View(ViewGroup) 在设置为 match_parent 的情况下才会有隐藏于屏幕底外的高度
 *
 * **原理:** 使用了嵌套滑动, 具体实现思路可以查看 [onNestedPreScroll]、[onNestedScroll]
 *
 * **NOTE:** 想修改能够滑动的距离请看 [mCanMoveHeight]
 *
 * ```
 * 做一些记录:
 * 背景: 当初我们是 2020 级学员, 做这个积分商城就成了我们的暑假考核. 那时我们还有 11 人, 分成了 5 组
 * 5 个组对于积分商城的板块上下移动有 5 种不同的解决方案,
 * 分别有:
 *   1、自定义 ViewGroup(由我设计)
 *   2、自定义 View(王兮设计, 使用了多层嵌套)
 *   3、自定义 Behavior(邱天设计)
 *   4、AppBarLayout(王中泰设计)
 *   5、MotionLayout(叶圣豪设计)
 * 最后因为我们组的更符合产品效果就选上了
 * 当初想的是自己自定义 View 更强, 就使用自定义 View 来做了
 * 原谅我可能会在以后需求变更时有学弟抱怨这什么垃圾学长写的什么jb玩意
 * 但其实我的自定义 View 算是最简单, 耦合度最低的解决方案
 * ```
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
     * @param l 其中 multiple 可能为 `[-0.5, 1.5]` 的值, 因为有过弹动画, 所以***存在负值***
     */
    fun setMoveListener(l: (multiple: Float) -> Unit) {
        mMoveListener = l
    }

    /**
     * 是否是完全展开状态
     */
    fun isUnfold(): Boolean {
        return mSecondChild.y.toInt() >= mOriginalFirstChildRect.bottom
    }

    /**
     * 设置第一个 View 完全展开时的回调
     */
    fun setUnfoldCallBack(call: () -> Unit) {
        if (mSecondChild.y.toInt() == mOriginalFirstChildRect.bottom) {
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




    /*
    * ==============================================================================================
    * 全局变量区
    * */

    private var mUnfoldCallBack: (() -> Unit)? = null
    private var mMoveListener: ((multiple: Float) -> Unit)? = null
    private val mFirstChild by lazy(LazyThreadSafetyMode.NONE) { getChildAt(0) }
    private val mSecondChild by lazy(LazyThreadSafetyMode.NONE) { getChildAt(1) }
    private val mOriginalFirstChildRect by lazy(LazyThreadSafetyMode.NONE) { // 第一个子 View 原始的 Rect, 不会改变
        Rect(mFirstChild.left, mFirstChild.top, mFirstChild.right, mFirstChild.bottom)
    }
    private val mCanMoveHeight by lazy(LazyThreadSafetyMode.NONE) { // 能够滑动的距离
        getChildAt(0).layoutParams.height // 返回了第一个 View 的高度
    }
    // 能够移动的上限值
    private val mUpperHeight by lazy(LazyThreadSafetyMode.NONE) { mOriginalFirstChildRect.bottom - mCanMoveHeight }




    /*
    * ==============================================================================================
    * 实现板块上下滑动和图片缩放的核心代码
    * */

    /**
     * 如果以后有什么动画的变更, 可以在这里进行修改
     *
     * 该方法就是用于修改子 View 的
     *
     * @param newSecondTop 表示第二个 View 的顶部(或第一个 View 的底部)将要移到的高度
     */
    private fun moveTo(newSecondTop: Int) {
        if (newSecondTop == mSecondChild.y.toInt()) return
        changeFirstChild(newSecondTop)
        changeOtherChild(newSecondTop)
        mMoveListener?.invoke((newSecondTop - mUpperHeight) / mCanMoveHeight.toFloat())
    }
    // 改变第一个 child
    private fun changeFirstChild(newSecondTop: Int) {
        val half = mOriginalFirstChildRect.height()/2F
        val x = ((newSecondTop - half) / half)
        val k = 0.9F // 调整第二个 View 上下滑动时与第一个 View 的缩放倍速
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
        mSecondChild.y = newSecondTop.toFloat()
    }
    // 滑动彻底结束时调用(滑动彻底结束并不一定指手指抬起, 因为可能存在惯性滑动)
    private fun slideOver() {
        if (!mIsExistPreScroll) { return } // 这个必须放到最前面, 此值用于手指没有移动就结束
        mIsAllowNonTouch = true // 结束
        if (mIsInterceptFastSlideUp || mIsInterceptFastSlideDown) { return } // 此时开启了动画自己在移动

        val y = mSecondChild.y.toInt()
        val halfY = mOriginalFirstChildRect.bottom - mCanMoveHeight/2
        if (y < halfY ) { // 小于以半就自动压缩
            /*
            * 下面这个判断很重要
            * 其实是嵌套滑动的坑, 在手指触摸时会调用 accept -> stop -> accept -> ...
            * 就是手指刚触摸时(是刚触摸, 也就是 Down 事件发生时)立马调用两次 accept, 并且中间附带一次 stop
            * 可以打 log 验证, 暂不知原因
            * (其实不用写这个判断也行, 因为我后面使用了 mIsExistPreScroll 变量来进一步防止这种情况发生)
            * */
            if (y != mUpperHeight) {
                slowlyAnimate(y, mUpperHeight) { moveTo(it) }
            }
        }else if (y >= halfY) { // 大于一半就回到展开状态
            if (y != mOriginalFirstChildRect.bottom) { // 这里的判断与上方原因相同
                slowlyAnimate(y, mOriginalFirstChildRect.bottom) { moveTo(it) }
            }
        }
    }




    /*
    * ==============================================================================================
    * 下面是嵌套滑动的具体逻辑代码, 查看前请先了解嵌套滑动基础知识
    * */

    private var mIsOpenNonTouch = false // 是否开启了惯性滑动
    private var mIsExistPreScroll = false // 是否存在手指移动
    private val mParentHelper by lazy { NestedScrollingParentHelper(this) }
    override fun onStartNestedScroll(child: View, target: View, axes: Int, type: Int): Boolean {
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL // 只跟垂直滑动进行交互
    }
    override fun onNestedScrollAccepted(child: View, target: View, axes: Int, type: Int) {
        /*
        * 在 RecyclerView 外套了一层官方的刷新控件 SwipeRefreshLayout 后, 手指触摸时回调该方法的
        * target 是 SwipeRefreshLayout, 而在 RecyclerView 惯性滑动开始时回调此方法的 target 是
        * RecyclerView, 我感觉很奇怪, 去翻了源码后才知道原来 SwipeRefreshLayout 使用的是
        * NestedScrollingParent 接口, 而该接口只会接受 type == TYPE_TOUCH 事件, 具体原因在于
        * NestedScrollingChildHelper 的 153 行调用了 ViewParentCompat 类中 205 行的
        * onStartNestedScroll(), 又因 SwipeRefreshLayout 使用的是 NestedScrollingParent 接口,
        * 执行了 211 行 "if (type == ViewCompat.TYPE_TOUCH)", 只有在 TYPE_TOUCH
        * 情况下才会调用开始嵌套滑动, 所以 RecyclerView 会绕过 SwipeRefreshLayout 去找到
        * 同意惯性滑动的父布局, 因此才会出现 type 不同, target 不同的情况
        * (在次做一个记录 2021-8-20,13:17 by Guo Xingrui)
        * */
        mParentHelper.onNestedScrollAccepted(child, target, axes, type)
        if (type == ViewCompat.TYPE_TOUCH) {
            mIsExistPreScroll = false // 还原
            mIsOpenNonTouch = false // 还原
            mIsAllowNonTouch = true // 还原
            mUpMaxDy = 0 // 还原
            mDownMaxDy = 0 // 还原
        }else {
            // 惯性滑动的也会调用 onNestedScrollAccepted()，但会在非惯性调用 onStopNestedScroll() 前调用
            // 所以可以使用这种方式去掉手指抬起后还有惯性滑动时, 对于 onStopNestedScroll() 的回调
            // 意思就是整个事件只会有一次开始和一次彻底的结束
            mIsOpenNonTouch = true
        }
    }

    private var mUpMaxDy = 0 // 向上滑最大的速度
    private var mDownMaxDy = 0 // 向下滑第一次最大的速度
    private var mIsInterceptFastSlideUp = false // 是否拦截快速向上滑动，在 RecyclerView 滑到顶时，如果速度过大，就拦截惯性滑动
    private var mIsInterceptFastSlideDown = false // 是否拦截快速向下滑动
    private var mIsAllowNonTouch = true // 是否允许惯性滑动
    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
        mIsExistPreScroll = true // 开始
        mUpMaxDy = max(dy, mUpMaxDy)
        mDownMaxDy = min(dy, mDownMaxDy)
        if (dy > 0) mDownMaxDy = 0 else mUpMaxDy = 0
        consumePreScroll(dy, type, target, consumed)
    }
    private fun consumePreScroll(dy: Int, type: Int, target: View, consumed: IntArray) {
        if (type == ViewCompat.TYPE_TOUCH) {
            // 如果正处于拦截快速滑动后的动画状态, 消耗所有滑动距离, 禁止用户滑动(这个状态不会维持很久)
            // 向反方向滑动时应取消动画, 但因 target view 坐标系改变原因, 写在了 dispatchTouchEvent() 中
            if (mIsInterceptFastSlideUp || mIsInterceptFastSlideDown) { consumed[1] = dy; return }
        }else if (type == ViewCompat.TYPE_NON_TOUCH) {
            if (!mIsAllowNonTouch || mIsInterceptFastSlideUp || mIsInterceptFastSlideDown) {
                /*
                * 此时处于拦截快速滑动后的动画状态, 可以取消惯性滑动,
                * 如果不取消, 则会因为 RecyclerView 正处于惯性滑动
                * 而拦截掉 VP2 的左右滑动.
                * 直接原因可以查看 RecyclerView 源码的第 3199 行, 因处于 SCROLL_STATE_SETTLING
                * 而直接调用了 requestDisallowInterceptTouchEvent(true), 调用后 VP2
                * 再也不会调用 onInterceptTouch() 来拦截事件
                * (该问题只出现在给 RecyclerView 外层套用了嵌套滑动布局, 根本原因不明,
                * 但线索应该在 RecyclerView 的 5397 行没有被调用, 我尽力了找不到:(
                * )
                * */
                if (target is RecyclerView) { target.stopScroll() }
                consumed[1] = dy
                return
            }
            if (!target.canScrollVertically(1)) { // 与上面原因相同, 但这是在 RecyclerView 滑到底部时取消惯性滑动
                if (target is RecyclerView) { target.stopScroll() }
                consumed[1] = dy
                return
            }
        }
        // 下面的是允许你滑动的时候
        if (dy > 0) slideUp(target, dy, type, consumed) // 向上滑
        else if (dy < 0) { // 向下滑
            /*
            * 向下滑.
            * 由于当前需求不存在与父布局的嵌套滑动, 所以默认不对向下滑做任何处理.
            * 但, 也有可能之后的需求存在上述情况, 那么, 学弟你可以在 **充分** 理解了嵌套滑动的知识后
            * 在这里写上向下滑时与父布局的联动(提示: 应该要使用 NestedScrollingChild2 接口)
            * */
        }
    }
    private fun slideUp(target: View, dy: Int, type: Int, consumed: IntArray) {
        val y = mSecondChild.y.toInt()
        val newSecondTop = y - dy // 将要移到的位置
        when (type) {
            ViewCompat.TYPE_TOUCH -> {
                // 拦截快速向上滑动, 直接加载动画, 并消耗接下来的所有滑动距离
                if (mUpMaxDy > 80 && y != mUpperHeight) {
                    consumed[1] = dy
                    slowlyAnimate(y, mUpperHeight,
                        onEnd = { mIsInterceptFastSlideUp = false/*结束*/ },
                        onCancel = { mIsInterceptFastSlideUp = false/*结束*/ },
                        onMove = { moveTo(it) }
                    )
                    mIsInterceptFastSlideUp = true // 开始
                    mIsAllowNonTouch = false // 开始
                }
                // 如果不在上边界处,就先滑到上边界
                else if (y >= (mUpperHeight+1)) {
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
                if (y >= (mUpperHeight+1)) {
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
        val y = mSecondChild.y.toInt() // 当前位置
        val newSecondTop = y - dyUnconsumed // 将要移到的位置
        when (type) {
            ViewCompat.TYPE_TOUCH -> {
                // 拦截快速向下滑动, 直接加载动画, 并消耗接下来的所有滑动距离
                // 这里其实是一种取巧的操作, 反正用户滑得很快, 就直接加载动画了, 不再跟随用户的手指滑动
                // 即使因为滑得快然后又马上反向滑动我也做了处理, 处理逻辑写在 dispatchTouchEvent()
                if (-mDownMaxDy > 80 && y != mOriginalFirstChildRect.bottom) {
                    mIsInterceptFastSlideDown = true // 开始
                    mIsAllowNonTouch = false // 开始
                    slowlyAnimate(y, mOriginalFirstChildRect.bottom,
                        onEnd = { mIsInterceptFastSlideDown = false/*结束*/ },
                        onCancel = { mIsInterceptFastSlideDown = false/*结束*/ },
                        onMove = { moveTo(it) }
                    )
                }
                // 如果不在下边界处,就先滑到下边界
                else if (y <= mOriginalFirstChildRect.bottom) {
                    // 将要滑到的位置超过了滑动范围
                    if (newSecondTop > mOriginalFirstChildRect.bottom) {
                        moveTo(mOriginalFirstChildRect.bottom)
                        return mOriginalFirstChildRect.bottom - y
                    }else {
                        moveTo(newSecondTop)
                    }
                }
            }
            ViewCompat.TYPE_NON_TOUCH -> {
                // 拦截离手时的惯性滑动, 直接加载动画, 并消耗接下来的所有滑动距离
                // 这里其实是一种取巧的操作, 反正用户滑得很快, 就直接加载动画了, 不再跟随用户的手指滑动
                // 即使因为滑得快然后又马上反向滑动我也做了处理, 处理逻辑写在 dispatchTouchEvent()
                if (y != mOriginalFirstChildRect.bottom) { // 这里只有 RecyclerView 滑到顶时才触发
                    slowlyAnimate(y, mOriginalFirstChildRect.bottom,
                        onEnd = { mIsInterceptFastSlideDown = false/*结束*/ },
                        onCancel = { mIsInterceptFastSlideDown = false/*结束*/ },
                        onMove = { moveTo(it) }
                    )
                    mIsInterceptFastSlideDown = true // 开始
                    mIsAllowNonTouch = false // 开始
                }
                // 如果不在下边界处,就先滑到下边界
                else if (y <= mOriginalFirstChildRect.bottom) {
                    // 将要滑到的位置超过了滑动范围
                    if (newSecondTop > mOriginalFirstChildRect.bottom) {
                        moveTo(mOriginalFirstChildRect.bottom)
                        return mOriginalFirstChildRect.bottom - y
                    }else {
                        moveTo(newSecondTop)
                    }
                }
            }
        }
        return dyUnconsumed // 返回值是消耗量
    }

    override fun onStopNestedScroll(target: View, type: Int) {
        mParentHelper.onStopNestedScroll(target, type)
        // 用了 mIsOpenNonTouch 做判断可以使整个事件只会有一次开始和一次彻底的结束
        if (type == ViewCompat.TYPE_TOUCH && !mIsOpenNonTouch) {
            slideOver() // 手指抬起屏幕且没有惯性滑动
        }else if (type == ViewCompat.TYPE_NON_TOUCH){
            slideOver() // 惯性滑动结束
        }
    }

    private var mLastMoveY = 0
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val y = ev.y.toInt()
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                mLastMoveY = y
            }
            MotionEvent.ACTION_MOVE -> {
                // 下面的判断用于在滑动过程中触发了快速滑动的动画, 但手指立马又反向滑动, 就停止动画
                // 你说反向滑动的判断为什么放在这里，不放在嵌套处理中?
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




    /*
    * ==============================================================================================
    * 下面的东西涉及到一些基本布局等一些不用修改的内容
    * */

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
        // 因为只允许有两个子 View, 原因在于测量时不用关心子 View 的高度, 就跟 ScrollView 只允许只有一个子 View 一样
        val child1 = getChildAt(0)
        val child2 = getChildAt(1)
        val lp1 = child1.layoutParams
        val childWidthMS1 = getChildMeasureSpec(wMS, 0, lp1.width)
        val childHeightMS1 = getChildMeasureSpec(hMS, 0, lp1.height)
        child1.measure(childWidthMS1, childHeightMS1)

        val lp2 = child2.layoutParams
        val childWidthMS2 = getChildMeasureSpec(wMS, 0, lp2.width)
        val childHeightMS2: Int = if (lp2.height >= 0) {
            MeasureSpec.makeMeasureSpec(lp2.height, MeasureSpec.EXACTLY)
        }else {
            // 如果为 match_parent 或 wrap_content 就设置多的高度给第二个 View, 让它超出屏幕, 向上滑动时就能显示
            // 该方案比动态改变第一个 View 的 LayoutParams 性能更好
            MeasureSpec.makeMeasureSpec(height - lp1.height + mCanMoveHeight, MeasureSpec.EXACTLY)
        }
        child2.measure(childWidthMS2, childHeightMS2)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        // 因为只有两个 View, 所以测量就很简单
        mFirstChild.layout(0, 0, mFirstChild.measuredWidth, mFirstChild.measuredHeight)
        val height = mFirstChild.measuredHeight
        mSecondChild.layout(0, height, mSecondChild.measuredWidth, height + mSecondChild.measuredHeight)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        if (childCount != 2) { // 如果子 View 不为 2 就报错
            throw RuntimeException("SlideUpLayout: " +
                    "只能拥有两个子 View(ViewGroup), 多的请使用 ViewGroup 来包裹")
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
        // 防止控件离开屏幕后造成内存泄漏
        mSlowlyMoveAnimate?.let { if (it.isRunning) it.end() }
    }
}