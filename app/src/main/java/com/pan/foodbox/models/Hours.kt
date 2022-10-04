package com.pan.foodbox.models

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class Hours(
    @SerializedName("Friday")
    val friday: String,
    @SerializedName("Monday")
    val monday: String,
    @SerializedName("Saturday")
    val saturday: String,
    @SerializedName("Sunday")
    val sunday: String,
    @SerializedName("Thursday")
    val thursday: String,
    @SerializedName("Tuesday")
    val tuesday: String,
    @SerializedName("Wednesday")
    val wednesday: String
) : Parcelable
