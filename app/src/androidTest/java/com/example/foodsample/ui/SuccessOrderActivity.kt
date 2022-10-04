package com.example.foodsample.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.foodsample.databinding.ActivitySuccessOrderBinding
import com.example.foodsample.models.RestaurantDataModel

class SuccessOrderActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySuccessOrderBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivitySuccessOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val restaurantDataModel = intent.getParcelableExtra<RestaurantDataModel>("MenuOrder")
        val actionBar = supportActionBar
        actionBar?.title = restaurantDataModel?.name
        actionBar?.subtitle = restaurantDataModel?.address
        actionBar?.setDisplayHomeAsUpEnabled(false)

        binding.buttonDone.setOnClickListener {

            setResult(RESULT_OK )
            finish()
        }
    }


}