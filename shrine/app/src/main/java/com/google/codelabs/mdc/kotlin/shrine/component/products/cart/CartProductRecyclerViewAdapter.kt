package com.google.codelabs.mdc.kotlin.shrine.component.products.cart

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.codelabs.mdc.kotlin.shrine.network.ProductEntry

/**
 * Adapter used to show a simple grid of products.
 */
class CartProductRecyclerViewAdapter(
    var productList: List<ProductEntry>
) : RecyclerView.Adapter<CartProductViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartProductViewHolder =
        CartProductViewHolder(inflater = LayoutInflater.from(parent.context), parent = parent)

    override fun onBindViewHolder(holder: CartProductViewHolder, position: Int) {
        holder.product = productList.getOrNull(position)
    }

    override fun getItemCount(): Int = productList.size
}
