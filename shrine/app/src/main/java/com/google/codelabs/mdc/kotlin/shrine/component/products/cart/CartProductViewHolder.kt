package com.google.codelabs.mdc.kotlin.shrine.component.products.cart

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.google.codelabs.mdc.kotlin.shrine.R
import com.google.codelabs.mdc.kotlin.shrine.network.ImageRequester
import com.google.codelabs.mdc.kotlin.shrine.network.ProductEntry
import kotlinx.android.synthetic.main.shr_cart_product.view.*

class CartProductViewHolder(
    inflater: LayoutInflater,
    parent: ViewGroup
) : RecyclerView.ViewHolder(
    inflater.inflate(R.layout.shr_cart_product, parent, false)
) {
    var product: ProductEntry? = null
        set(value) {
            field = value
            value?.let(this::bind)
        }

    private var productImage: ImageView = itemView.product_image

    private fun bind(product: ProductEntry) {
        ImageRequester.setImageFromUrl(productImage, product.url)
    }
}
