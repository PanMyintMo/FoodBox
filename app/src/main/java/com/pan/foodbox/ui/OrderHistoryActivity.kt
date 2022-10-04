package com.pan.foodbox.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.pan.foodbox.adapter.OrderHistoryAdapter
import com.pan.foodbox.entity.ItemHistory
import com.pan.foodbox.models.Restaurant
import com.pan.foodbox.util.SpacingItemDecoration
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pan.foodbox.database.AppDatabase
import com.pan.foodbox.databinding.ActivityReorderBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class OrderHistoryActivity : BaseActivity(), OrderHistoryAdapter.OnReorderClickedListener {
    private lateinit var binding: ActivityReorderBinding
    private lateinit var appDatabase: AppDatabase
    private lateinit var sharePreference: SharedPreferences
    private lateinit var adapter: OrderHistoryAdapter
    private val orderHistoryList = arrayListOf<ItemHistory>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReorderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Order History"

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        appDatabase = AppDatabase.getInstance(this)
        initViews()
        loadOrderHistory()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        onBackPressed()
        return super.onOptionsItemSelected(item)
    }

    private fun loadOrderHistory() {
        CoroutineScope(Dispatchers.IO).launch {
            orderHistoryList.addAll(appDatabase.daoRecord().getAll())
            launch(Dispatchers.Main) {
                adapter.notifyItemRangeChanged(0, orderHistoryList.size)
            }
        }
    }

    private fun initViews() {

        val decoration = SpacingItemDecoration(1, toPx(15), true)
        binding.orderRecycler.addItemDecoration(decoration)
        binding.orderRecycler.setHasFixedSize(true)
        binding.orderRecycler.layoutManager = LinearLayoutManager(this)
        adapter = OrderHistoryAdapter(this, orderHistoryList, this)
        binding.orderRecycler.adapter = adapter

    }

    override fun onRecorderClick(orderHistory: ItemHistory) {
        CoroutineScope(Dispatchers.IO).launch {
            orderHistory.cartItem.forEach {
                appDatabase.cartItemDao().insert(it)
              //  appDatabase.cartItemDao().delete(it)
            }

            val restaurant = getRestaurant().firstOrNull() {
                orderHistory.restaurantName == it.name
            }
            launch(Dispatchers.Main) {
                val intent = Intent(this@OrderHistoryActivity, ShoppingCartActivity::class.java)
                intent.putExtra(RestaurantMenuActivity.EXTRA_RESTAURANT, restaurant)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun getRestaurant(): List<Restaurant> {
        val json = assets.open(getFileName())
            .bufferedReader().use {
                it.readText()
            }
        val restaurantListType = object : TypeToken<List<Restaurant>>() {}.type
        return Gson().fromJson(json, restaurantListType) as List<Restaurant>
    }

    private fun getFileName(): String {
        sharePreference = getSharedPreferences("preference", Context.MODE_PRIVATE)
        return if (sharePreference.getString("lan", "en") == "en") "en/restaurants.json"
        else "mm/restaurants.json"
    }

}