package com.mredrock.cyxbs.store.utils

import android.content.res.Resources

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @data 2021/8/27
 */
internal fun Int.dp2px(): Int {
    return (this.dp2pxF() + 0.5f).toInt()
}

internal fun Int.dp2pxF(): Float {
    return Resources.getSystem().displayMetrics.density * this
}

internal fun Float.dp2px(): Int {
    return (this.dp2pxF() + 0.5f).toInt()
}

internal fun Float.dp2pxF(): Float {
    return Resources.getSystem().displayMetrics.density * this
}