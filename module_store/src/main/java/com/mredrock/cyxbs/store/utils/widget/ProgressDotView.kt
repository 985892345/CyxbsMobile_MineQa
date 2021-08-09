package com.mredrock.cyxbs.store.utils.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.mredrock.cyxbs.common.utils.extensions.dp2px
import com.mredrock.cyxbs.store.R

/**
 *    author : zz
 *    e-mail : 1140143252@qq.com
 *    date   : 2021/8/4 10:22
 *
 *    用圆点来展示图片显示进度
 *    根据View宽度、圆点个数与半径自动均分圆点间隔
 */
class ProgressDotView : View {
    private var mRadius = 3f //圆点半径 dp
    private var mInterval = 0f //两圆间隔
    private var mCircleCenterInterval = 0f //两相邻圆圆心间隔
    private var mDotCount = 3 //圆点个数
    private var mUnselectedColor = R.color.store_progress_dot_unselected //未选中的圆点颜色
    private var mSelectedColor = R.color.store_progress_dot_selected //选中的圆点颜色
    private var mUnselectedDotPaint = Paint() //未选中圆点画笔
    private var mSelectedDotPaint = Paint() //选中圆点画笔
    private var mPathPaint = Paint() //移动路径画笔
    private var mPath = Path() //移动路径
    private var mPosition = 0 //当前选中的位置 从0开始
    private var mProgress = 0f //页面由一个到另一个的进度

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        //获取相关属性
        initAttrs(context, attrs)
        //初始化画笔
        initPaint()
    }

    private fun initInterval() {
        mInterval = (measuredWidth.toFloat() - mDotCount * mRadius * 2) / (mDotCount - 1)
        mCircleCenterInterval = mInterval + 2 * mRadius
    }

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

        mPathPaint.apply {
            color = ContextCompat.getColor(context, mSelectedColor)
            style = Paint.Style.FILL_AND_STROKE
            isAntiAlias = true
        }
    }

    private fun initAttrs(context: Context, attrs: AttributeSet?) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.ProgressDotView)
        mRadius = a.getFloat(R.styleable.ProgressDotView_dotRadius, 3f)
        mRadius = context.dp2px(mRadius).toFloat()
        mDotCount = a.getInt(R.styleable.ProgressDotView_dotCount, 3)
        mUnselectedColor = a.getResourceId(R.styleable.ProgressDotView_unSelectedColor, R.color.store_progress_dot_unselected)
        mSelectedColor = a.getResourceId(R.styleable.ProgressDotView_selectedColor, R.color.store_progress_dot_selected)
        mZoom = a.getFloat(R.styleable.ProgressDotView_circleZoom, 0.4f)
        mLeftCircleCanMovePosition = a.getFloat(R.styleable.ProgressDotView_leftCircleCanMovePosition, 0.5f)
        a.recycle()
    }

    override fun onDraw(canvas: Canvas?) {
        //计算间距
        initInterval()
        //绘制点
        drawDot(canvas)
        //在外面设置了mProgress才绘制路径
        if (mProgress != 0f) {
            drawPath(canvas)
        }
    }

    private var mStartX = 0f //移动前起始圆点圆心X坐标
    private var mEndX = 0f //移动后最终点圆心X坐标
    private var mLeftX = 0f //移动时左圆的X坐标值
    private var mRightX = 0f //移动时右圆的X坐标值
    private var mY = 0f //圆心Y轴固定
    private var mLeftCircleRadius = 0f //左圆的实时半径
    private var mRightCircleRadius = 0f //右圆的实时半径
    private var mZoom = 0.4f //左圆缩放mZoom后开始移动 右圆放大到(1-mZoom)后刚好移动到mEnd
    private var mLeftCircleCanMovePosition = 0.5f //当mProgress大于该值时 左圆缩放mZoom完毕 开始移动
    private var mIsEnd = false //是否达到最后一页

    private fun drawDot(canvas: Canvas?) {
        mStartX = mRadius + mPosition * mRadius * 2 + mPosition * mInterval
        mEndX = mStartX + mCircleCenterInterval
        mLeftX = mStartX
        mRightX = mStartX
        mY = measuredHeight / 2f

        if (canvas != null) {
            //初始化Dot 此时未滑动
            initDot(canvas)

            //在外面设置了mProgress才绘制移动过程的圆
            if (mProgress != 0f) {
                drawMoveCircle(canvas)
            }

        }
    }

    private fun drawMoveCircle(canvas: Canvas) {
        //若外部通过 page：2 0 1 2 0 来实现 0 1 2 界面的循环滑动  则在这里确定是否已经滑到最后一页
        mIsEnd = mPosition == mDotCount - 1

        //若最后/第一页 设置相关参数 使得如果继续滑动 会产生从最后/第一个圆点倒退/前进到第一个/最后一个圆点的动画
        if (mIsEnd) {
            mCircleCenterInterval *= (mDotCount - 1)
            mStartX = mRadius
            mProgress = 1 - mProgress
        }

        if (mProgress < mLeftCircleCanMovePosition) {
            //滑动一半之前 左圆点缩小mZoom (默认百分之40) 不移动
            mLeftCircleRadius = changeCircle(0f, 0f, -mZoom / mLeftCircleCanMovePosition * mProgress, canvas)
            //记录此时左圆的X
            mLeftX = mStartX + 0f
        } else {
            //滑动mLeftCircleCanMovePosition之后 左圆点开始平移向右至右圆点圆心 且缩小至不可见
            mLeftCircleRadius = changeCircle(mCircleCenterInterval / (1 - mLeftCircleCanMovePosition) * (mProgress - mLeftCircleCanMovePosition), 0f, -mZoom + (mZoom - 1) / (1 - mLeftCircleCanMovePosition) * (mProgress - mLeftCircleCanMovePosition), canvas)
            //记录此时左圆的X
            mLeftX = mStartX + mCircleCenterInterval / (1 - mLeftCircleCanMovePosition) * (mProgress - mLeftCircleCanMovePosition)

        }

        if (mProgress < mLeftCircleCanMovePosition) {
            //滑动mLeftCircleCanMovePosition之前 右圆点从左圆点圆心处出发 从0放大到正常大小的(1-mZoom)(默认百分之60) 同时移动到右侧圆心处
            mRightCircleRadius = changeCircle(mCircleCenterInterval / mLeftCircleCanMovePosition * mProgress, 0f, -1 + (1 - mZoom) / mLeftCircleCanMovePosition * mProgress, canvas)
            //记录此时右圆的X
            mRightX = mStartX + mCircleCenterInterval / mLeftCircleCanMovePosition * mProgress
        } else {
            //滑动mLeftCircleCanMovePosition之后 右圆点从正常大小的(1-mZoom)(默认百分之60)放大到正常大小
            mRightCircleRadius = changeCircle(mCircleCenterInterval, 0f, -mZoom + mZoom / (1 - mLeftCircleCanMovePosition) * (mProgress - mLeftCircleCanMovePosition), canvas)
            //记录此时右圆的X
            mRightX = mStartX + mCircleCenterInterval
        }
    }

    private fun initDot(canvas: Canvas) {
        for (i in 0 until mDotCount) {
            //根据位置绘制不同颜色的圆点
            if ((i == mPosition && mProgress == 0f) || mPosition == i + 3) {//注意 i+3=3 若外部通过 page：2 0 1 2 0 来实现 0 1 2 界面的循环滑动 当从0往前滑到2时 因为设定了跳转 所以会出现3的情况
                canvas.drawCircle(mRadius + i * mRadius * 2 + i * mInterval, mY, mRadius, mSelectedDotPaint)
            } else {
                canvas.drawCircle(mRadius + i * mRadius * 2 + i * mInterval, mY, mRadius, mUnselectedDotPaint)
            }
        }
    }

    private fun drawPath(canvas: Canvas?) {
        if (canvas != null) {
            mPath.reset()
            mPath.moveTo(mLeftX, mY + mLeftCircleRadius)
            mPath.quadTo(mLeftX + (mRightX - mLeftX) / 2, mY, mRightX, mY + mRightCircleRadius)
            mPath.lineTo(mRightX, mY - mRightCircleRadius)
            mPath.quadTo(mLeftX + (mRightX - mLeftX) / 2, mY, mLeftX, mY - mLeftCircleRadius)
            mPath.close()
            canvas.drawPath(mPath, mPathPaint)
        }
    }

    /**
     * @param changeValueX 与起点圆心X轴坐标相比改变的X值大小
     * @param changeValueY 与起点圆心X轴坐标相比改变的Y值大小
     * @param changeProportionRadius 与初始圆的半径相比 改变的半径比例 初始为 0 若缩小百分之40 即为-0.4
     */
    private fun changeCircle(changeValueX: Float, changeValueY: Float, changeProportionRadius: Float, canvas: Canvas): Float {

        canvas.drawCircle(mStartX + changeValueX, mY + changeValueY, mRadius * (1 + changeProportionRadius), mSelectedDotPaint)

        //返回此时圆的半径
        return mRadius * (1 + changeProportionRadius)
    }

    fun updatePosition(position: Int) {
        mPosition = position
        invalidate()
    }

    fun updatePosition(position: Int, progress: Float) {
        mPosition = position
        mProgress = progress
        invalidate()
    }
}