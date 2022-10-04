package com.pan.foodbox.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "item_records")
@kotlinx.parcelize.Parcelize
data class ItemHistory(
    @PrimaryKey(autoGenerate = true)
    val id :Long =0,
    @ColumnInfo(name = "cart_items")
    val cartItem: ArrayList<CartItem> = arrayListOf(),
    @ColumnInfo(name = "restaurant_name")
    val restaurantName: String,
    @ColumnInfo(name = "customerName")
    val customerName: String,
    @ColumnInfo(name="created_at")
    val createdAt:Long=System.currentTimeMillis()
) : Parcelable
