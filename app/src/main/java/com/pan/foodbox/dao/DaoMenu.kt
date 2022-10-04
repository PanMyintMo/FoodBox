package com.pan.foodbox.dao


import androidx.room.*
import com.pan.foodbox.entity.CartItem
@Dao
interface CartItemDao {
    @Query("SELECT * FROM cart_items ORDER BY id DESC")
    fun getAll(): List<CartItem>

    @Query("SELECT * FROM cart_items WHERE restaurant_name = :restaurantName")
    fun getAllFromRestaurant(restaurantName: String): List<CartItem>

   @Query("DELETE FROM cart_items WHERE restaurant_name = :restaurantName")
    fun deleteAllFromRestaurant(restaurantName: String)

    @Query("SELECT * FROM cart_items WHERE name = :name LIMIT 1")
    fun get(name: String): CartItem?

    @Insert
    fun insert(cartItem: CartItem): Long

    @Update
    fun update(cartItem: CartItem)


    @Delete
    fun delete(cartItem: CartItem)

    @Query("DELETE FROM cart_items WHERE name = :menuName")
    fun delete(menuName: String)
}



