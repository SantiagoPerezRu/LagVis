package com.example.lagvis_v1.ui.xml

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class GridSpacingDecoration(
    private val spacingPx: Int,
    private val headerPositions: Set<Int> = setOf(0) // el header está en pos 0
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        out: Rect,
        v: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val lm = parent.layoutManager as? GridLayoutManager ?: return
        val pos = parent.getChildAdapterPosition(v)
        if (pos == RecyclerView.NO_POSITION) return

        if (headerPositions.contains(pos)) {
            // Header con padding vertical propio
            out.set(0, spacingPx, 0, spacingPx)
            return
        }

        val spanCount = lm.spanCount
        val column = (v.layoutParams as GridLayoutManager.LayoutParams).spanIndex

        // Márgenes verticales
        out.top = spacingPx
        out.bottom = spacingPx

        // Reparto horizontal equitativo
        val totalSpacing = spacingPx * (spanCount - 1)
        val spacePerItem = totalSpacing / spanCount.toFloat()

        out.left = (column * (spacePerItem / (spanCount - 1))).toInt()
        out.right = (spacePerItem - out.left).toInt()
    }
}
