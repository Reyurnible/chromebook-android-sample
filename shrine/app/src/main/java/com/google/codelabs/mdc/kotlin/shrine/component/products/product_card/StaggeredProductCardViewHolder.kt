package com.google.codelabs.mdc.kotlin.shrine.component.products.product_card

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
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
    // Listener
    var addCartButtonClickListener: ((View, ProductEntry) -> Unit)? = null
    var product: ProductEntry? = null
        set(value) {
            field = value
            value?.let(this::bind)
        }
    // Views
    private var productImage: ImageView = itemView.findViewById(R.id.product_image)
    private var addCartButton: ImageButton = itemView.findViewById(R.id.add_cart_button)
    private var productTitle: TextView = itemView.findViewById(R.id.product_title)
    private var productPrice: TextView = itemView.findViewById(R.id.product_price)

    init {
        addCartButton.setOnClickListener {
            val product = this.product ?: return@setOnClickListener
            addCartButtonClickListener?.invoke(it, product)
        }
    }

    private fun bind(product: ProductEntry) {
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
