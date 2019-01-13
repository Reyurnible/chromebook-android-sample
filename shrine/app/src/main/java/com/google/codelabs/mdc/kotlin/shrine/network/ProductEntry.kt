package com.google.codelabs.mdc.kotlin.shrine.network

import android.net.Uri

/**
 * A product entry in the list of products.
 */
class ProductEntry(
    val title: String,
    dynamicUrl: String,
    val url: String,
    val price: String,
    val description: String
) {
    val dynamicUrl: Uri = Uri.parse(dynamicUrl)
}
