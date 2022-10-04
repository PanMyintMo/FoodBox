package com.example.foodsample.ui

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.MenuItem
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodsample.adapter.PlaceYourOrderAdapter
import com.example.foodsample.databinding.ActivityPlaceYourOrderBinding
import com.example.foodsample.models.RestaurantDataModel

class PlaceYourOrderActivity : AppCompatActivity() {

    private var placeYourOrderAdapter: PlaceYourOrderAdapter? = null
    private lateinit var binding: ActivityPlaceYourOrderBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlaceYourOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val menuModel = intent.getParcelableExtra<RestaurantDataModel>("RestaurantModel")
        val actionBar: ActionBar? = supportActionBar
        actionBar?.title = menuModel?.name
        actionBar?.subtitle = menuModel?.address
        actionBar?.setDisplayHomeAsUpEnabled(true)

        initRecyclerView(menuModel)

        binding.buttonPlaceYourOrder.setOnClickListener {
            menuModel?.let { it1 -> onPlaceOrderButtonClick(it1) }
        }


        binding.switchDelivery.setOnCheckedChangeListener { _, isChecked ->

            if (isChecked) {
                binding.inputAddress.visibility = View.VISIBLE
                binding.inputCity.visibility = View.VISIBLE
                binding.inputState.visibility = View.VISIBLE
                binding.inputZip.visibility = View.VISIBLE
                binding.tvDeliveryCharge.visibility = View.VISIBLE
                binding.tvDeliveryChargeAmount.visibility = View.VISIBLE
            } else {
                binding.inputAddress.visibility = View.GONE
                binding.inputCity.visibility = View.GONE
                binding.inputState.visibility = View.GONE
                binding.inputZip.visibility = View.GONE
                binding.tvDeliveryCharge.visibility = View.GONE
                binding.tvDeliveryChargeAmount.visibility = View.GONE
            }

            calculateTotalAmount(menuModel)
        }

        calculateTotalAmount(menuModel)
    }

    private fun calculateTotalAmount(menuModel: RestaurantDataModel?) {
        var subTotalAmount = 0f

        if (menuModel != null) {
            for (menu in menuModel.menus!!) {
                subTotalAmount += menu?.price?.times(menu.totalInCart!!)!!
            }
        }

        binding.tvSubtotalAmount.text = "$" + String.format("%.2f", subTotalAmount)


        if (binding.switchDelivery.isChecked) {
            subTotalAmount += menuModel?.delivery_charge?.toFloat()!!
            binding.tvDeliveryChargeAmount.text =
                "$" + menuModel.delivery_charge.let { String.format("%.2f", it.toFloat()) }
        } else {
            binding.tvDeliveryChargeAmount.text = "0"
        }

        binding.tvTotalAmount.text = "$" + String.format("%.2f", subTotalAmount)

    }

    private fun initRecyclerView(menuModel: RestaurantDataModel?) {
        binding.cartItemsRecyclerView.layoutManager = LinearLayoutManager(this)
        placeYourOrderAdapter = PlaceYourOrderAdapter(menuModel?.menus)
        binding.cartItemsRecyclerView.adapter = placeYourOrderAdapter
    }

    private fun onPlaceOrderButtonClick(menuModel: RestaurantDataModel) {

        if (TextUtils.isEmpty(binding.inputName.toString())) {
            binding.inputName.error = "Enter Your  Name"
            return
        } else if (binding.switchDelivery.isChecked && TextUtils.isEmpty(binding.inputAddress.toString())) {
            binding.inputAddress.error = "Enter Your Address"
            return
        } else if (binding.switchDelivery.isChecked && TextUtils.isEmpty(binding.inputCity.toString())) {
            binding.inputCity.error = "Enter Your City"
            return
        } else if (binding.switchDelivery.isChecked && TextUtils.isEmpty(binding.inputZip.toString())) {
            binding.inputZip.error = "Enter Your Zip Code"
            return
        } else if (TextUtils.isEmpty(binding.inputCardNumber.text.toString())) {
            binding.inputCardExpiry.error = "Enter your credit card expiry"
            return
        } else if (TextUtils.isEmpty(binding.inputCardPin.text.toString())) {
            binding.inputCardPin.error = "Enter your credit card pin/cvv"
            return
        }
        val intent = Intent(this@PlaceYourOrderActivity, SuccessOrderActivity::class.java)
        intent.putExtra("MenuOrder", menuModel)
        resultLauncher.launch(intent)
    }

    val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                setResult(RESULT_OK)
                finish()
            }
        }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home)
            finish()
        return super.onOptionsItemSelected(item)

    }


}