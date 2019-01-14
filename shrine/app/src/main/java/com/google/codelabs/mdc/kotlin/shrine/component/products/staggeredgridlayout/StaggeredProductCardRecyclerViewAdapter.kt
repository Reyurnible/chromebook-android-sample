package com.google.codelabs.mdc.kotlin.shrine.component.products.staggeredgridlayout

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.codelabs.mdc.kotlin.shrine.network.ProductEntry

/**
 * Adapter used to show an asymmetric grid of products, with 2 items in the first column, and 1
 * item in the second column, and so on.
 */
class StaggeredProductCardRecyclerViewAdapter(
    var productList: List<ProductEntry> = emptyList()
) : RecyclerView.Adapter<StaggeredProductCardViewHolder>() {

    override fun getItemViewType(position: Int): Int = position % 3

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StaggeredProductCardViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            0 -> StaggeredProductCardViewHolder.First(inflater, parent)
            1 -> StaggeredProductCardViewHolder.Second(inflater, parent)
            2 -> StaggeredProductCardViewHolder.Third(inflater, parent)
            else -> throw IllegalArgumentException("view type is require 0 to 2")
        }
    }

    override fun onBindViewHolder(holder: StaggeredProductCardViewHolder, position: Int) {
        productList.getOrNull(position)?.let {
            holder.bind(it)
        }
    }

    override fun getItemCount(): Int = productList.size
}
