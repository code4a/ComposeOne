package com.jiangyt.app.carremote.view

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.TimeInterpolator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.OvershootInterpolator
import com.jiangyt.app.carremote.R
import com.jiangyt.app.carremote.utils.dp
import kotlin.math.*

/**
 * 方向盘控件
 */
class SteeringWheelView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        private const val TAG = "SteeringWheelView"

        /**
         * 当前方向无效，方向盘没有触摸时处于该状态
         */
        const val INVALID = -1

        /**
         * 向右
         */
        const val RIGHT = 0

        /**
         * 向上
         */
        const val UP = 1

        /**
         * 向左
         */
        const val LEFT = 2

        /**
         * 向下
         */
        const val DOWN = 4
        private const val mDefaultWidthDp = 200
        private const val mDefaultHeightDp = 200

        /**
         * 当采用wrap_content测量模式时，默认宽度
         */
        private var mDefaultWidth = 0

        /**
         * 当采用wrap_content测量模式时，默认高度
         */
        private var mDefaultHeight = 0
    }

    /**
     * 外部监听器
     */
    private var mListener: SteeringWheelListener? = null

    /**
     * 画笔对象
     */
    private val mPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    /**
     * 画笔的颜色
     */
    private var mColor = 0

    /**
     * 当前中心X
     */
    private var mCenterX = 0f

    /**
     * 当前中心Y
     */
    private var mCenterY = 0f

    /**
     * 球
     */
    private var mBallDrawable: Drawable? = null

    /**
     * 被按下后，球的图片
     */
    private var mBallPressedDrawable: Drawable? = null

    /**
     * 当前球中心X坐标
     */
    private var mBallCenterX = 0f

    /**
     * 当前球中心Y坐标
     */
    private var mBallCenterY = 0f

    /**
     * 球的半径
     */
    private var mBallRadius = 0f

    /**
     * 当前半径
     */
    private var mRadius = 0f

    /**
     * 当前角度
     */
    private var mAngle = 0.0

    /**
     * 当前偏离中心的百分比，取值为 0 - 100
     */
    private var mPower = 0

    /**
     * 通知的时间最小间隔
     */
    private var mNotifyInterval: Long = 0

    /**
     * 通知者
     */
    private var mNotifyRunnable: Runnable? = null

    /**
     * 上次通知监听者的时间
     */
    private var mLastNotifyTime: Long = 0

    /**
     * 当前方向
     */
    private var mDirection = INVALID

    /**
     * 向右箭头
     */
    private var mArrowRightDrawable: Drawable? = null

    /**
     * 回弹动画
     */
    private var mAnimator: ObjectAnimator? = null

    /**
     * 时间插值器
     */
    private var mInterpolator: TimeInterpolator? = null
    private var mWasTouched = false

    /**
     * 获取球X坐标
     *
     * @return 球X坐标
     */
    fun getBallX(): Float {
        return mBallCenterX
    }

    /**
     * 设置球X坐标。目前该API的执行时机为Choreographer中每帧中的动画阶段,由底层动画框架反射调用
     *
     * @param ballX 球X坐标
     */
    fun setBallX(ballX: Float) {
        if (ballX != mBallCenterX) {
            mBallCenterX = ballX
            updatePower()
            updateDirection()
            invalidate()
            notifyStatusChanged()
        }
    }

    /**
     * 获取球Y坐标
     *
     * @return 球Y坐标
     */
    fun getBallY(): Float {
        return mBallCenterY
    }

    /**
     * 设置球Y坐标。目前该API的执行时机为Choreographer中每帧中的动画阶段,由底层动画框架反射调用
     *
     * @param ballY 球Y坐标
     */
    fun setBallY(ballY: Float) {
        if (mBallCenterY != ballY) {
            mBallCenterY = ballY
            updatePower()
            updateDirection()
            invalidate()
            notifyStatusChanged()
        }
    }

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.SteeringWheelView)
        //读取XML配置
        mColor = a.getColor(R.styleable.SteeringWheelView_ballColor, Color.RED)
        mArrowRightDrawable = a.getDrawable(R.styleable.SteeringWheelView_arrowRight)
        mBallDrawable = a.getDrawable(R.styleable.SteeringWheelView_ballSrc)
        mBallPressedDrawable = a.getDrawable(R.styleable.SteeringWheelView_ballPressedSrc)
        a.recycle()
        mBallRadius = (mBallDrawable!!.intrinsicWidth shr 1).toFloat()
        mDefaultWidth = mDefaultWidthDp.dp.toInt()
        mDefaultHeight = mDefaultHeightDp.dp.toInt()
        mPaint.color = mColor
        mPaint.style = Paint.Style.STROKE
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        Log.d(TAG, "onMeasure: ")
        //handle wrap_content
        val widthSpecMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSpecSize = MeasureSpec.getSize(widthMeasureSpec)
        //解析上层ViewGroup传下来的数据，高两位是模式，低30位是大小
        //主要需要特殊处理wrap_content情形
        val heightSpecMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSpecSize = MeasureSpec.getSize(heightMeasureSpec)
        if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(mDefaultWidth, mDefaultHeight)
        } else if (widthSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(mDefaultWidth, heightSpecSize)
        } else if (heightSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(widthSpecSize, mDefaultHeight)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        Log.d(TAG, "onSizeChanged: w=$w#h=$h#oldw=$oldw#oldh=$oldh")
        //在layout过程中会回调该方法
        //handle padding
        val paddingLeft = paddingLeft
        val paddingRight = paddingRight
        val paddingTop = paddingTop
        val paddingBottom = paddingBottom
        val width = width - paddingLeft - paddingRight
        val height = height - paddingTop - paddingBottom
        mRadius =
            ((width.coerceAtMost(height) shr 1) - mArrowRightDrawable!!.intrinsicWidth / 2).toFloat()
        mCenterX = (paddingLeft + (width shr 1)).toFloat()
        mBallCenterX = mCenterX
        mCenterY = (paddingTop + (height shr 1)).toFloat()
        mBallCenterY = mCenterY

        //calc arrow bounds
        mArrowRightDrawable!!.setBounds(
            (mCenterX + mRadius - mArrowRightDrawable!!.intrinsicWidth / 2).toInt(),
            (mCenterY - mArrowRightDrawable!!.intrinsicHeight / 2).toInt(),
            (mCenterX + mRadius + mArrowRightDrawable!!.intrinsicWidth / 2).toInt(),
            (mCenterY + mArrowRightDrawable!!.intrinsicHeight / 2).toInt()
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //画横线
        canvas.drawLine(mCenterX - mRadius, mCenterY, mCenterX + mRadius, mCenterY, mPaint)
        //画竖线
        canvas.drawLine(mCenterX, mCenterY - mRadius, mCenterX, mCenterY + mRadius, mPaint)
        //画大圆
        canvas.drawCircle(mCenterX, mCenterY, mRadius, mPaint)
        //画球
        drawBall(canvas)
        //画箭头
        drawArrow(canvas)
    }

    private fun drawBall(canvas: Canvas) {
        //val drawable: Drawable?
        //point to the right drawable instance
        val drawable = if (mWasTouched) {
            mBallPressedDrawable
        } else {
            mBallDrawable
        }?.also {
            it.setBounds(
                (mBallCenterX - it.intrinsicWidth / 2).toInt(),
                (mBallCenterY - it.intrinsicHeight / 2).toInt(),
                (mBallCenterX + it.intrinsicWidth / 2).toInt(),
                (mBallCenterY + it.intrinsicHeight / 2).toInt()
            )
            it.draw(canvas)
        }

    }

    /**
     * 画箭头
     *
     * @param canvas 画布对象
     */
    private fun drawArrow(canvas: Canvas) {
        if (!mWasTouched) return
        //canvas.save(Canvas.MATRIX_SAVE_FLAG)
        canvas.save()
        //旋转角度
        canvas.rotate(-mAngle.toFloat(), mCenterX, mCenterY)
        mArrowRightDrawable?.draw(canvas)
        canvas.restore()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        Log.d(TAG, "onTouchEvent: ")
        val x = event.x.toInt()
        val y = event.y.toInt()
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mWasTouched = true
                mAnimator?.let {
                    if (it.isRunning) {
                        //在本次触摸事件序列中，如果上一个复位动画还没执行完毕，则需要取消动画，及时响应用户输入
                        it.cancel()
                    }
                }
            }
            MotionEvent.ACTION_MOVE -> {
                updateBallData(x, y)
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                mWasTouched = false
                resetBall()
            }
            else -> {}
        }
        notifyStatusChanged()
        return true
    }

    /**
     * 指定球回弹动画时间插值器
     *
     * @param value 插值器
     */
    fun interpolator(value: TimeInterpolator?): SteeringWheelView {
        mInterpolator = value ?: OvershootInterpolator()
        return this
    }

    private fun getInterpolator(): TimeInterpolator? {
        if (mInterpolator == null) {
            mInterpolator = OvershootInterpolator()
        }
        return mInterpolator
    }

    /**
     * 弹性滑动
     */
    private fun resetBall() {
        val pvhX = PropertyValuesHolder.ofFloat("BallX", mBallCenterX, mCenterX)
        val pvhY = PropertyValuesHolder.ofFloat("BallY", mBallCenterY, mCenterY)
        mAnimator = ObjectAnimator.ofPropertyValuesHolder(this, pvhX, pvhY).setDuration(150)
        mAnimator?.interpolator = getInterpolator()
        mAnimator?.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}
            override fun onAnimationEnd(animation: Animator) {
                mAngle = 0.0
                mPower = 0
                mDirection = INVALID
                notifyStatusChanged()
            }

            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })
        mAnimator?.start()
    }

    private fun updateBallData(x: Int, y: Int) {
        mBallCenterX = x.toFloat()
        mBallCenterY = y.toFloat()
        val outOfRange = outOfRange(x, y)
        //采用(a, b]开闭区间
        if (x >= mCenterX && y < mCenterY) {
            //第一象限
            mAngle = Math.toDegrees(atan(((mCenterY - y) / (x - mCenterX)).toDouble()))
            if (outOfRange) {
                mBallCenterX =
                    (mCenterX + cos(Math.toRadians(mAngle)) * (mRadius - mBallRadius)).toFloat()
                mBallCenterY =
                    (mCenterY - sin(Math.toRadians(mAngle)) * (mRadius - mBallRadius)).toFloat()
            }
        } else if (x < mCenterX && y <= mCenterY) {
            //第二象限
            mAngle = 180 - Math.toDegrees(atan(((mCenterY - y) / (mCenterX - x)).toDouble()))
            if (outOfRange) {
                mBallCenterX =
                    (mCenterX - cos(Math.toRadians(180 - mAngle)) * (mRadius - mBallRadius)).toFloat()
                mBallCenterY =
                    (mCenterY - sin(Math.toRadians(180 - mAngle)) * (mRadius - mBallRadius)).toFloat()
            }
        } else if (x <= mCenterX && y > mCenterY) {
            //第三象限
            mAngle = 270 - Math.toDegrees(atan(((mCenterX - x) / (y - mCenterY)).toDouble()))
            if (outOfRange) {
                mBallCenterX =
                    (mCenterX - cos(Math.toRadians(mAngle - 180)) * (mRadius - mBallRadius)).toFloat()
                mBallCenterY =
                    (mCenterY + sin(Math.toRadians(mAngle - 180)) * (mRadius - mBallRadius)).toFloat()
            }
        } else if (x > mCenterX && y >= mCenterY) {
            //第四象限
            mAngle = 360 - Math.toDegrees(atan(((y - mCenterY) / (x - mCenterX)).toDouble()))
            if (outOfRange) {
                mBallCenterX =
                    (mCenterX + cos(Math.toRadians(360 - mAngle)) * (mRadius - mBallRadius)).toFloat()
                mBallCenterY =
                    (mCenterY + sin(Math.toRadians(360 - mAngle)) * (mRadius - mBallRadius)).toFloat()
            }
        }
        updatePower()
        updateDirection()
        invalidate()
    }

    private fun updatePower() {
        mPower = (100 * sqrt(
            (mBallCenterX - mCenterX).toDouble().pow(2.0) + (mBallCenterY - mCenterY).toDouble()
                .pow(2.0)
        ) / (mRadius - mBallRadius)).toInt()
        Log.d(TAG, "updatePower: mPower = $mPower")
    }

    private fun outOfRange(newX: Int, newY: Int): Boolean {
        return (newX - mCenterX).toDouble().pow(2.0) + (newY - mCenterY).toDouble()
            .pow(2.0) > (mRadius - mBallRadius).toDouble()
            .pow(2.0)
    }

    /**
     * 采用(a,b]开闭区间
     *
     * @return 方向值
     */
    private fun updateDirection(): Int {
        mDirection =
            if (abs(mCenterX - mBallCenterX) < 0.00000001
                && abs(mCenterY - mBallCenterY) < 0.00000001
            )
                INVALID
            else if (mAngle <= 45 || mAngle > 315)
                RIGHT
            else if (mAngle > 45 && mAngle <= 135)
                UP
            else if (mAngle > 135 && mAngle <= 225)
                LEFT
            else
                DOWN
        return mDirection
    }

    /**
     * 通知监听者方向盘状态改变
     */
    private fun notifyStatusChanged() {
        if (mListener == null) return
        var delay: Long = 0
        if (mNotifyRunnable == null) {
            mNotifyRunnable = createNotifyRunnable()
        } else {
            val now = System.currentTimeMillis()
            if (now - mLastNotifyTime < mNotifyInterval) {
                //移除旧消息
                removeCallbacks(mNotifyRunnable)
                delay = mNotifyInterval - (now - mLastNotifyTime)
            }
        }
        postDelayed(mNotifyRunnable, delay)
    }

    private fun createNotifyRunnable(): Runnable {
        return Runnable {
            Log.d(TAG, "run: ")
            mLastNotifyTime = System.currentTimeMillis()
            //取当前数据，而非过去数据的snapshot
            mListener?.onStatusChanged(this@SteeringWheelView, mAngle.toInt(), mPower, mDirection)
        }
    }

    /**
     * 设置回调时间间隔
     *
     * @param interval 回调时间间隔
     */
    fun notifyInterval(interval: Long): SteeringWheelView {
        if (interval < 0) {
            throw RuntimeException("notifyInterval interval < 0 is not accept")
        }
        mNotifyInterval = interval
        return this
    }

    /**
     * 设置监听器
     *
     * @param listener 监听器对象
     */
    fun listener(listener: SteeringWheelListener?): SteeringWheelView {
        mListener = listener
        return this
    }

    interface SteeringWheelListener {
        /**
         * 方向盘状态改变的回调
         *
         * @param view      方向盘实例对象
         * @param angle     当前角度。范围0-360，其中右0，上90，左180，下270
         * @param power     方向上的力度。范围0-100
         * @param direction 大致方向。取值为 [.RIGHT] [.UP] [.LEFT] [.DOWN]
         */
        fun onStatusChanged(view: SteeringWheelView, angle: Int, power: Int, direction: Int)
    }
}