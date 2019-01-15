package com.google.codelabs.mdc.kotlin.shrine.network

import android.content.Context
import com.google.codelabs.mdc.kotlin.shrine.R
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.io.BufferedReader
import java.util.ArrayList
import java.util.concurrent.TimeUnit

class ProductRepository(
    context: Context
) {
    private val resources = context.resources
    private val gson: Gson = GsonBuilder().create()

    fun fetchList(): Single<List<ProductEntry>> =
        Single.timer(3L, TimeUnit.SECONDS)
            .flatMap {
                val inputStream = resources.openRawResource(R.raw.products)
                val jsonProductsString = inputStream.bufferedReader().use(BufferedReader::readText)
                val productListType = object : TypeToken<ArrayList<ProductEntry>>() {}.type
                Single.just(gson.fromJson<List<ProductEntry>>(jsonProductsString, productListType))
            }
            .subscribeOn(Schedulers.io())
}
