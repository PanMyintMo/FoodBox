package com.example.foodsample.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodsample.R
import com.example.foodsample.adapter.RestaurantListAdapter
import com.example.foodsample.databinding.ActivityMainBinding
import com.example.foodsample.models.RestaurantDataModel
import com.google.gson.Gson
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.StringWriter
import java.io.Writer

class MainActivity : AppCompatActivity() ,RestaurantListAdapter.RestaurantClickListener{

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val actionBar = supportActionBar
        actionBar?.title="Restaurant List"

        val restaurantModel = getRestaurantData()
        initRecyclerView(restaurantModel)

    }


    private fun initRecyclerView(restaurantDataModel: List<RestaurantDataModel?>?) {
        binding.recyclerRestaurant.layoutManager = LinearLayoutManager(this)
        val adapter = RestaurantListAdapter(restaurantDataModel as List<RestaurantDataModel>?,this)
        binding.recyclerRestaurant.adapter = adapter

    }

    private fun getRestaurantData(): List<RestaurantDataModel?> {

        val inputStream = resources.openRawResource(R.raw.restaurant)
        val writer: Writer = StringWriter()
        val buffer = CharArray(1024)

        try {
            val reader = BufferedReader(InputStreamReader(inputStream, "UTF-8"))
            var n: Int
            while (reader.read(buffer).also { n = it } != -1) {
                writer.write(buffer, 0, n)
            }

        } catch (e: Exception) {
        }

        val jsonStr: String = writer.toString()
        val gson = Gson()
        val restaurantdataModel = gson.fromJson<Array<RestaurantDataModel>>(
            jsonStr,
            Array<RestaurantDataModel>
            ::class.java
        ).toList()

        return restaurantdataModel
    }
    override fun onItemClick(restaurantData: RestaurantDataModel?) {
        val intent=Intent(this, RestaurantMenuActivity::class.java)
        intent.putExtra("Restaurant",restaurantData)
        startActivity(intent)    }
}