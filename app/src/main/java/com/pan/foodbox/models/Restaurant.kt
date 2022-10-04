package com.pan.foodbox.models

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class Restaurant(
    @SerializedName("address")
    val address: String,
    @SerializedName("delivery_charge")
    val deliveryCharge: Double,
    @SerializedName("hours")
    val hours: Hours,
    @SerializedName("image")
    val image: String,
    @SerializedName("menus")
    val menus: ArrayList<RestaurantMenu>,
    @SerializedName("name")
    val name: String
) : Parcelable