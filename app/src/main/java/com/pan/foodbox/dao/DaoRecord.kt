package com.pan.foodbox.dao

import androidx.room.*
import com.pan.foodbox.entity.ItemHistory


@Dao
interface DaoRecord {
@Query("SELECT * FROM item_records ORDER BY id DESC")
fun getAll():List<ItemHistory>

@Insert
fun insert(itemHistory: ItemHistory):Long



}