package com.pan.foodbox.database

import androidx.room.TypeConverter
import com.pan.foodbox.entity.CartItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    @TypeConverter
    fun stringToCart(str:String) : ArrayList<CartItem>{

        val listType=object : TypeToken<ArrayList<CartItem>>() {}.type
        return Gson().fromJson(str,listType)

    }

    @TypeConverter
    fun cartsToString(list:ArrayList<CartItem?>?):String{
        return Gson().toJson(list)

    }
}