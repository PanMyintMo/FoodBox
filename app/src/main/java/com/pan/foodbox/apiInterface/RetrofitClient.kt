package com.pan.foodbox.apiInterface

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    fun getDirectionApi() : DirectionApiInterface {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://maps.googleapis.com/")
            .build()
        return retrofit.create(DirectionApiInterface::class.java)



    }


}