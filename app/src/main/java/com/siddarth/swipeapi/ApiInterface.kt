package com.siddarth.swipeapi

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiInterface {
    @GET( value = "public/get")
    fun getData(): Call<List<Product>>

    @POST("public/add")
    fun createProduct(@Body product: Product): Call<Product>
}