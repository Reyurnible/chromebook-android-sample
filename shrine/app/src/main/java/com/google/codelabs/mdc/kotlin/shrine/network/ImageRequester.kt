package com.google.codelabs.mdc.kotlin.shrine.network

import android.widget.ImageView
import com.bumptech.glide.Glide

/**
 * Class that handles image requests using Glide.
 */
object ImageRequester {

    fun setImageFromUrl(imageView: ImageView, url: String) {
        Glide.with(imageView).load(url)
            .into(imageView)
    }
}
