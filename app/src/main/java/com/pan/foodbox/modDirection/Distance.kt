package com.pan.foodbox.modDirection


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import androidx.annotation.Keep

@Keep
@Parcelize
data class Distance(
    @SerializedName("text")
    val text: String,
    @SerializedName("value")
    val value: Int
) : Parcelable