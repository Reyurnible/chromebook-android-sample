package com.google.codelabs.mdc.kotlin.shrine.component.products

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.codelabs.mdc.kotlin.shrine.R

class ProductCardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var productImage: ImageView = itemView.findViewById(R.id.product_image)
    var productTitle: TextView = itemView.findViewById(R.id.product_title)
    var productPrice: TextView = itemView.findViewById(R.id.product_price)
}