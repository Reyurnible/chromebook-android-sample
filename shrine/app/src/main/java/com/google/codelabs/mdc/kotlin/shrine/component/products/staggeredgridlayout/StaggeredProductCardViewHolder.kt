package com.google.codelabs.mdc.kotlin.shrine.component.products.staggeredgridlayout

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.google.codelabs.mdc.kotlin.shrine.R
import com.google.codelabs.mdc.kotlin.shrine.network.ImageRequester
import com.google.codelabs.mdc.kotlin.shrine.network.ProductEntry

sealed class StaggeredProductCardViewHolder(
    inflater: LayoutInflater,
    parent: ViewGroup,
    @LayoutRes layoutResId: Int
) : RecyclerView.ViewHolder(
    inflater.inflate(layoutResId, parent, false)
) {
    private var productImage: ImageView = itemView.findViewById(R.id.product_image)
    private var productTitle: TextView = itemView.findViewById(R.id.product_title)
    private var productPrice: TextView = itemView.findViewById(R.id.product_price)

    fun bind(product: ProductEntry) {
        productTitle.text = product.title
        productPrice.text = product.price
        ImageRequester.setImageFromUrl(productImage, product.url)
    }

    class First(
        inflater: LayoutInflater,
        parent: ViewGroup
    ) : StaggeredProductCardViewHolder(inflater, parent, R.layout.shr_staggered_product_card_first)

    class Second(
        inflater: LayoutInflater,
        parent: ViewGroup
    ) : StaggeredProductCardViewHolder(inflater, parent, R.layout.shr_staggered_product_card_second)

    class Third(
        inflater: LayoutInflater,
        parent: ViewGroup
    ) : StaggeredProductCardViewHolder(inflater, parent, R.layout.shr_staggered_product_card_third)
}
