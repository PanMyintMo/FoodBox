package com.pan.foodbox.models


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import androidx.annotation.Keep

@Keep
@Parcelize
data class Ingredient(
    @SerializedName("amount")
    val amount: String?="",
    @SerializedName("name")
    val name: String,
    @SerializedName("unit")
    val unit: String?="",
    @SerializedName("img")
    val img:String?=""
) : Parcelable