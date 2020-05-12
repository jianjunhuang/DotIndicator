package com.jianjun.dotindicator

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.LinearLayout
import androidx.core.view.get
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import kotlin.math.abs
import kotlin.math.max

class DotIndicator : LinearLayout, View.OnClickListener {

    private val proxyViewPager = ProxyViewPager()

    private val onPagerChangeCallback = PagerChangeCallback()

    private var viewPager2: ViewPager2? = null
        set(value) {
            field = value
            value?.let {
                proxyViewPager.attach(it)
                proxyViewPager.registerPagerChangeListener(onPagerChangeCallback)
                invalidate()
            }
        }

    private var viewPager: ViewPager? = null
        set(value) {
            field = value
            value?.let {
                proxyViewPager.attach(it)
                proxyViewPager.registerPagerChangeListener(onPagerChangeCallback)
                invalidate()
            }
        }

    private var adapter: DotIndicator.AbsIndicatorAdapter? = null
        set(value) {
            field = value
            field?.let {
                removeAllViews()
                addWidget()
                requestLayout()
            }
        }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var isScrolling = false
    private var currentPos = 0
    private var nextPos = 1
    private var dotOffset = 0f

    private val shrinkAnimator = ValueAnimator.ofFloat(1f, 0f)
    private val expendAnimator = ValueAnimator.ofFloat(0f, 1f)
//    var style = Style.AVERAGE

    //    enum class Style {
//        AVERAGE,//平均划分当前layout
//
//        //        WRAP,//item 大小 wrap_content
//        MAX//item 大小取 item 中最大的
//    }
    private var shrinkProgress = 0f

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, -1)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        orientation = HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
        paint.color = Color.WHITE
        paint.strokeJoin = Paint.Join.ROUND
        paint.strokeWidth = 10f
        setWillNotDraw(false)
        shrinkAnimator.duration = 100
        shrinkAnimator.interpolator = LinearInterpolator()
        shrinkAnimator.addUpdateListener {
            shrinkProgress = it.animatedValue as Float
            invalidate()
        }
        expendAnimator.duration = 100
        expendAnimator.interpolator = LinearInterpolator()
        expendAnimator.addUpdateListener {
            dotOffset = it.animatedValue as Float
            invalidate()
        }
        expendAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
            }
        })

    }

    private fun addWidget() {
        for (index in 0 until proxyViewPager.getItemCount()) {
            adapter?.let { adapter ->
                val view = adapter.createItemView(index)
                val params = view.layoutParams ?: LinearLayout.LayoutParams(
                    LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT
                )
                view.tag = index
                view.setOnClickListener(this)
                addViewInLayout(view, index, params, true)
            }
        }
    }

    fun attach(viewPager: ViewPager, adapter: AbsIndicatorAdapter) {
        this.viewPager = viewPager
        this.adapter = adapter
    }

    fun attach(viewPager2: ViewPager2, adapter: AbsIndicatorAdapter) {
        this.viewPager2 = viewPager2
        this.adapter = adapter
    }

//    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
//        val measureWidth = measureWidth(widthMeasureSpec)
//        val measureHeight = measureHeight(heightMeasureSpec)
//        setMeasuredDimension(measureWidth, measureHeight)
//    }

//    private fun measureHeight(heightMeasureSpec: Int): Int {
//        when (style) {
//            Style.AVERAGE -> {
//
//            }
//            Style.MAX -> {
//
//            }
//            Style.WRAP -> {
//                //todo
//            }
//        }
//
//        return MeasureSpec.getSize(heightMeasureSpec)
//    }
//
//    private fun measureWidth(widthMeasureSpec: Int): Int {
//        val mode = MeasureSpec.getMode(widthMeasureSpec)
//        val size = MeasureSpec.getSize(widthMeasureSpec)
//        when (style) {
//            Style.AVERAGE -> {
//                //todo
//                measureC
//            }
//            Style.MAX -> {
//
//            }
//            Style.WRAP -> {
//                //todo
//            }
//        }
//
//        return 0
//    }

    var lineY = 0f
    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        for (index in 0 until childCount) {
            val child = get(index)
            val childBottom = child.bottom
            lineY = max(lineY, childBottom.toFloat())
        }
        lineY += 16f
        if (lineY > height) {
            lineY = height * 3 / 4f
        }
    }

    override fun onDraw(canvas: Canvas?) {
        val itemCount = proxyViewPager.getItemCount()
        //每格长度
        val interval = width / itemCount
        paint.style = Paint.Style.FILL
        drawLine(canvas, interval)
    }

    private fun drawLine(canvas: Canvas?, interval: Int) {
        //line 位置
        val x = currentPos * interval + interval / 2f
        if (isScrolling) {
            paint.style = Paint.Style.STROKE
            //draw line
            if (isClick) {
                if (nextPos < currentPos) {
                    //to left
                    canvas?.drawLine(
                        x,
                        lineY,
                        x - dotOffset * interval * (abs(nextPos - currentPos)),
                        lineY,
                        paint
                    )
                } else {
                    //to right
                    canvas?.drawLine(
                        x,
                        lineY,
                        x + dotOffset * interval * (abs(nextPos - currentPos)),
                        lineY,
                        paint
                    )
                }
                return
            }
            if (nextPos != currentPos) {
                //to left
                canvas?.drawLine(
                    x,
                    lineY,
                    x - (1 - dotOffset) * interval * (abs(nextPos - currentPos)),
                    lineY,
                    paint
                )
            } else {
                //to right
                canvas?.drawLine(
                    x,
                    lineY,
                    x + dotOffset * interval * (abs(nextPos - currentPos) + 1),
                    lineY,
                    paint
                )
            }
        } else {
            //draw dot
            val dotX = (proxyViewPager.currentItem) * interval + interval / 2f
            canvas?.drawCircle(dotX, lineY, 5f, paint)
        }
        if (shrinkAnimator.isRunning) {
            //last pos
            //current pos
            val targetX = proxyViewPager.currentItem * interval + interval / 2f
            val dx = targetX - x
            canvas?.drawLine(targetX, lineY, targetX - dx * shrinkProgress, lineY, paint)
        }
    }


    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
    }


    inner class PagerChangeCallback : ProxyViewPager.OnPageChangeListener {
        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
            if (isClick) {
                return
            }
            shrinkAnimator.cancel()
            dotOffset = positionOffset
            nextPos = position
            if (position <= currentPos) {
                invalidate()
            }
        }

        override fun onPageSelected(position: Int) {
            for (pos in 0 until proxyViewPager.getItemCount()) {
                adapter?.onPagerChanged(pos, get(pos), pos == position)
            }
        }

        override fun onPageScrollStateChanged(state: Int) {
            when (state) {
                ViewPager2.SCROLL_STATE_IDLE -> {
                    isScrolling = false
                    isClick = false
                    expendAnimator.cancel()
                    shrinkAnimator.start()
                    shrinkAnimator.duration = abs(proxyViewPager.currentItem - currentPos) * 100L
                }
                ViewPager2.SCROLL_STATE_DRAGGING -> {
                    isScrolling = true
                    currentPos = proxyViewPager.currentItem
                }
            }
        }
    }

    abstract class AbsIndicatorAdapter {
        abstract fun createItemView(pos: Int): View
        abstract fun onPagerChanged(pos: Int, view: View, isSelected: Boolean)
    }

    private var isClick = false
    override fun onClick(v: View?) {
        v?.let {
            currentPos = proxyViewPager.currentItem
            val pos = v.tag as Int
            if (pos == currentPos) {
                return
            }
            nextPos = pos
            isScrolling = true
            isClick = true
            expendAnimator.duration = abs(nextPos - currentPos) * 100L
            proxyViewPager.setCurrentItem(pos, true)
            expendAnimator.start()
        }
    }
}