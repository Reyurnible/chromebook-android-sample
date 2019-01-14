package com.google.codelabs.mdc.kotlin.shrine.component.products

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.codelabs.mdc.kotlin.shrine.network.ProductEntry

/**
 * Adapter used to show a simple grid of products.
 */
class ProductCardRecyclerViewAdapter(private val productList: List<ProductEntry>) : RecyclerView.Adapter<ProductCardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductCardViewHolder =
        ProductCardViewHolder(inflater = LayoutInflater.from(parent.context), parent = parent)

    override fun onBindViewHolder(holder: ProductCardViewHolder, position: Int) {
        productList.getOrNull(position)?.let {
            holder.bind(it)
        }
    }

    override fun getItemCount(): Int = productList.size
}
