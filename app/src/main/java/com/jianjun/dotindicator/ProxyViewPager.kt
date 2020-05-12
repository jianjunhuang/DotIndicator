package com.jianjun.dotindicator

import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2

class ProxyViewPager {

    private var viewPager: ViewPager? = null

    private var viewPager2: ViewPager2? = null

    private val pager2ChangeCallback by lazy {
        object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                onPageChangeListener?.onPageScrolled(
                    position,
                    positionOffset,
                    positionOffsetPixels
                )
            }

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                onPageChangeListener?.onPageSelected(position)
            }

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
                onPageChangeListener?.onPageScrollStateChanged(state)
            }
        }
    }

    private val pagerChangeListener by lazy {
        object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
                onPageChangeListener?.onPageScrollStateChanged(state)
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                onPageChangeListener?.onPageScrolled(
                    position,
                    positionOffset,
                    positionOffsetPixels
                )
            }

            override fun onPageSelected(position: Int) {
                onPageChangeListener?.onPageSelected(position)
            }

        }
    }

    fun attach(vp: ViewPager) {
        viewPager2?.let {
            throw IllegalArgumentException("ViewPager2 has been attached")
        }
        viewPager = vp
    }

    fun attach(vp2: ViewPager2) {
        viewPager?.let { throw IllegalArgumentException("ViewPager has been attached") }
        viewPager2 = vp2
    }

    fun getItemCount(): Int {
        viewPager2?.run { return this.adapter?.itemCount ?: 0 }
        viewPager?.run { return this.adapter?.count ?: 0 }
        return 0
    }

    fun setCurrentItem(pos: Int, smooth: Boolean) {
        viewPager?.run { this.setCurrentItem(pos, smooth) }
        viewPager2?.run { this.setCurrentItem(pos, smooth) }
    }

    var currentItem: Int = -1
        set(value) {
            field = value
            setCurrentItem(value, false)
        }
        get() {
            viewPager2?.run { return this.currentItem }
            viewPager?.run { return this.currentItem }
            return -1
        }

    private var onPageChangeListener: OnPageChangeListener? = null

    fun registerPagerChangeListener(listener: OnPageChangeListener) {
        viewPager2?.let {
            it.unregisterOnPageChangeCallback(pager2ChangeCallback)
            it.registerOnPageChangeCallback(pager2ChangeCallback)
        }
        viewPager?.let {
            it.removeOnPageChangeListener(pagerChangeListener)
            it.addOnPageChangeListener(pagerChangeListener)
        }
        onPageChangeListener = listener
    }

    interface OnPageChangeListener {
        fun onPageScrollStateChanged(state: Int)
        fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        )

        fun onPageSelected(position: Int)
    }
}