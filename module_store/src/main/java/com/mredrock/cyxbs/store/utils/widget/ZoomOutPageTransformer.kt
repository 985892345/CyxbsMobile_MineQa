package com.mredrock.cyxbs.store.utils.widget

import android.view.View
import androidx.viewpager2.widget.ViewPager2
import kotlin.math.abs

/**
 *    author : zz
 *    e-mail : 1140143252@qq.com
 *    date   : 2021/8/11 15:16
 */
class ZoomOutPageTransformer : ViewPager2.PageTransformer {
    private val MIN_SCALE = 0.85f
    private val MIN_ALPHA = 0.5f

    override fun transformPage(view: View, position: Float) {
        val pageWidth = view.width
        val pageHeight = view.height
        when {
            position < -1 -> {
                view.alpha = 0f
            }
            position <= 1 -> {

                val scaleFactor = MIN_SCALE.coerceAtLeast(1 - abs(position))
                val verticalMargin = pageHeight * (1 - scaleFactor) / 2
                val horizontalMargin = pageWidth * (1 - scaleFactor) / 2
                if (position < 0) {
                    view.translationX = horizontalMargin - verticalMargin / 2
                } else {
                    view.translationX = -horizontalMargin + verticalMargin / 2
                }

                view.scaleX = scaleFactor
                view.scaleY = scaleFactor

                view.alpha = MIN_ALPHA +
                        (scaleFactor - MIN_SCALE) /
                        (1 - MIN_SCALE) * (1 - MIN_ALPHA)

            }
            else -> {
                view.alpha = 0f
            }
        }
    }
}