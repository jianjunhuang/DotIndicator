package com.jianjun.dotindicator

import androidx.core.view.get
import androidx.viewpager2.widget.ViewPager2

internal class ViewPager2ChangeCallback : ViewPager2.OnPageChangeCallback() {
    override fun onPageScrolled(
        position: Int,
        positionOffset: Float,
        positionOffsetPixels: Int
    ) {
        super.onPageScrolled(position, positionOffset, positionOffsetPixels)
//        shrinkAnimator.cancel()
//        nextPos = position
//        dotOffset = positionOffset
//        invalidate()
    }

    override fun onPageSelected(position: Int) {
        super.onPageSelected(position)
//        viewPager?.adapter?.let {
//            for (pos in 0 until it.itemCount) {
//                adapter?.onPagerChanged(pos, get(pos), pos == position)
//            }
//        }
    }

    override fun onPageScrollStateChanged(state: Int) {
        super.onPageScrollStateChanged(state)
//        when (state) {
//            ViewPager2.SCROLL_STATE_IDLE -> {
//                isScrolling = false
//                shrinkAnimator.start()
//            }
//            ViewPager2.SCROLL_STATE_DRAGGING -> {
//                isScrolling = true
//                currentPos = viewPager?.currentItem ?: 0
//            }
//        }
    }
}