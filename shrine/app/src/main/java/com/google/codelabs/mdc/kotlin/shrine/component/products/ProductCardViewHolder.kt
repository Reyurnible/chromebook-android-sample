package com.google.codelabs.mdc.kotlin.shrine.component.products

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.codelabs.mdc.kotlin.shrine.R
import com.google.codelabs.mdc.kotlin.shrine.network.ImageRequester
import com.google.codelabs.mdc.kotlin.shrine.network.ProductEntry
import kotlinx.android.synthetic.main.shr_product_card.view.*

class ProductCardViewHolder(
    inflater: LayoutInflater,
    parent: ViewGroup
) : RecyclerView.ViewHolder(
    inflater.inflate(R.layout.shr_product_card, parent, false)
) {
    private var productImage: ImageView = itemView.product_image
    private var productTitle: TextView = itemView.product_title
    private var productPrice: TextView = itemView.product_price

    fun bind(product: ProductEntry) {
        productTitle.text = product.title
        productPrice.text = product.price
        ImageRequester.setImageFromUrl(productImage, product.url)
    }
}
