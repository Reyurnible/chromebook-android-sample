package com.google.codelabs.mdc.kotlin.shrine.component.products

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.TextView

import com.android.volley.toolbox.NetworkImageView
import com.google.codelabs.mdc.kotlin.shrine.R

class ProductCardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var productImage: NetworkImageView = itemView.findViewById(R.id.product_image)
    var productTitle: TextView = itemView.findViewById(R.id.product_title)
    var productPrice: TextView = itemView.findViewById(R.id.product_price)
}
