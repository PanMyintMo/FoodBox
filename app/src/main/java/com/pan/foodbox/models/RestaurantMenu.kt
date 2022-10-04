package com.pan.foodbox.models

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class RestaurantMenu(
    @SerializedName("name")
    var name: String,
    @SerializedName("price")
    val price: Double,
    @SerializedName("url")
    var url: String,
    @SerializedName("instruction")
    val instruction:String,
    @SerializedName("overview")
    val overview:String,
    @SerializedName("ingredients")
    val ingredients:ArrayList<Ingredient>
    ) : Parcelable