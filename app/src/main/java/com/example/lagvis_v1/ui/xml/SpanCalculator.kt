package com.example.lagvis_v1.ui.xml

import android.content.Context
import android.util.TypedValue
import kotlin.math.floor
import kotlin.math.max

object SpanCalculator {
    fun computeSpanCount(context: Context, rvWidthPx: Int, minCardWidthDp: Float): Int {
        if (rvWidthPx <= 0) return 2
        val density = context.resources.displayMetrics.density
        val minCardPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, minCardWidthDp, context.resources.displayMetrics)
        val paddingPx = context.resources.getDimensionPixelSize(com.example.lagvis_v1.R.dimen.grid_padding)
        val spacingPx = context.resources.getDimensionPixelSize(com.example.lagvis_v1.R.dimen.grid_spacing)

        val available = rvWidthPx - (paddingPx * 2)
        // Para N columnas hay (N-1) espacios
        var n = floor((available + spacingPx) / (minCardPx + spacingPx)).toInt()
        if (n < 2) n = 2
        return n
    }
}
