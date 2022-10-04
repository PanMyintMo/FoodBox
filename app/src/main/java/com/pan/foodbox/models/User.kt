package com.pan.foodbox.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class User (
    val uid:String="",
    val name:String="",
    val imageProfilePath:String="",
    val email:String="") : Parcelable
