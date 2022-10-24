package com.pan.foodbox.apiInterface

import com.pan.foodbox.modDirection.MapDatas
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface DirectionApiInterface {
  /*  @GET("/maps/api/directions/json?destination=16.82521999076325,%2096.1258090411045&origin=16.796644,%2096.130649&key=AIzaSyDlfh4WuZJz51yTzzIiopDiWIA1CmntLC0")
    fun getDirection(): Call<MapDatas>*/

    @GET("/maps/api/directions/json?")
    fun getDirection(@Query("origin") origin: String,
                     @Query("destination") destination: String,
                     @Query("key") apiKey: String): Call<MapDatas>
}

