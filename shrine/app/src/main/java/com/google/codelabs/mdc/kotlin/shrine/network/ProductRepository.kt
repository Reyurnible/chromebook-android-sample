package com.google.codelabs.mdc.kotlin.shrine.network

import android.content.Context
import com.google.codelabs.mdc.kotlin.shrine.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.BufferedReader
import java.util.ArrayList

class ProductRepository(
    context: Context
) {
    private val resources = context.resources

    fun fetchList(): List<ProductEntry> {
        val inputStream = resources.openRawResource(R.raw.products)
        val jsonProductsString = inputStream.bufferedReader().use(BufferedReader::readText)
        val gson = Gson()
        val productListType = object : TypeToken<ArrayList<ProductEntry>>() {}.type
        return gson.fromJson<List<ProductEntry>>(jsonProductsString, productListType)
    }
}
