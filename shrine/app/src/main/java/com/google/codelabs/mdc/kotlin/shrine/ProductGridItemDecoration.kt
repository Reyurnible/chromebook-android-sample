package com.google.codelabs.mdc.kotlin.shrine

import android.graphics.Rect
import androidx.recyclerview.widget.RecyclerView
import android.view.View

/**
 * Custom item decoration for a vertical [ProductGridFragment] [RecyclerView]. Adds a
 * small amount of padding to the left of grid items, and a large amount of padding to the right.
 */
class ProductGridItemDecoration(private val largePadding: Int, private val smallPadding: Int) : androidx.recyclerview.widget.RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View,
                                parent: androidx.recyclerview.widget.RecyclerView, state: androidx.recyclerview.widget.RecyclerView.State) {
        outRect.left = smallPadding
        outRect.right = largePadding
    }
}
