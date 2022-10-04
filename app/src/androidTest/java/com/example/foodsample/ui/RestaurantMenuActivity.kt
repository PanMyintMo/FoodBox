package com.example.foodsample.ui

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.foodsample.R
import com.example.foodsample.adapter.MenuListAdapter
import com.example.foodsample.databinding.ActivityRestaurantMenuBinding
import com.example.foodsample.entity.MenuEntity
import com.example.foodsample.models.Menu
import com.example.foodsample.models.RestaurantDataModel
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils
import com.google.android.material.snackbar.Snackbar
import database.MenuDatabase
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class RestaurantMenuActivity : AppCompatActivity(), MenuListAdapter.MenuListClickListener {

    private lateinit var binding: ActivityRestaurantMenuBinding
    private val dateFormat = SimpleDateFormat("MMM d,yyyy h:mm, a", Locale.getDefault())
    private val currentDateAndTime = dateFormat.format(Date())
    private var badge: BadgeDrawable? = null

    private val menuDatabase by lazy {
        MenuDatabase.getDatabase(this).DaoMenu()
    }

    private lateinit var restaurantModel: RestaurantDataModel
    private var menuList: List<Menu>? = null
    private var menuListAdapter: MenuListAdapter? = null
    private var itemsInTheCartList: MutableList<Menu> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRestaurantMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolBar)

        restaurantModel = intent?.getParcelableExtra("Restaurant")!!

        val actionBar = supportActionBar
        actionBar?.title = restaurantModel.name
        actionBar?.subtitle = restaurantModel.address
        actionBar?.setDisplayHomeAsUpEnabled(true)
        menuList = restaurantModel.menus as List<Menu>?
        initRecyclerView(menuList!!)

        binding.checkoutButton.setOnClickListener {
            if (itemsInTheCartList.isEmpty()) {
                Snackbar.make(binding.root, "Please add some items in cart", Snackbar.LENGTH_SHORT)
                    .show()
            } else {
                restaurantModel.menus = itemsInTheCartList
                val intent = Intent(this@RestaurantMenuActivity, PlaceYourOrderActivity::class.java)
                intent.putExtra("RestaurantModel", restaurantModel)
                resultLauncher.launch(intent)
            }
        }
    }

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                finish()
            }
        }

    private fun initRecyclerView(menu: List<Menu>) {
        binding.menuRecyclerView.layoutManager = GridLayoutManager(this, 2)
        menuListAdapter = MenuListAdapter(menu, this)
        binding.menuRecyclerView.adapter = menuListAdapter
    }

    override fun addToCartClickListener(menu: Menu) {
        if (!itemsInTheCartList.contains(menu)){
            itemsInTheCartList.add(menu)
        }

        updateCarts()
        binding.checkoutButton.text = "Checkout (${itemsInTheCartList.size}) Items"
    }

    override fun removeFromCartClickListener(menu: Menu) {
        if (menu.totalInCart == 0) {
            itemsInTheCartList.remove(menu)
        }

        updateCarts()
        binding.checkoutButton.text = "Checkout (${itemsInTheCartList.size}) Items"
    }

    override fun onCreateOptionsMenu(menus: android.view.Menu): Boolean {
        menuInflater.inflate(R.menu.shopping_cart_menu, menus)
        updateCarts()
        return super.onCreateOptionsMenu(menus)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish() // action bar back button
            }
            R.id.shopping_cart -> {
                saveToDatabase()
                restaurantModel.menus = itemsInTheCartList
                val intent =
                    Intent(this@RestaurantMenuActivity, ShoppingCartActivity::class.java)
                intent.putExtra("menu_data_added", restaurantModel)
                startActivity(intent)


            }
        }
        return super.onOptionsItemSelected(item)
    }


    private fun saveToDatabase() {
        lifecycleScope.launch {
            for (m in itemsInTheCartList.indices) {
                menuDatabase.addMenu(
                    MenuEntity(
                        id = 0,
                        name = itemsInTheCartList[m].name.toString(),
                        itemsInTheCartList[m].price.toDouble(),
                        currentDateAndTime,
                        paid = true
                    )
                )
            }
        }
    }

    private fun updateCarts() {
        if (badge == null) {
            badge = BadgeDrawable.create(this@RestaurantMenuActivity)
        }

        if (itemsInTheCartList.isEmpty()) {
            BadgeUtils.detachBadgeDrawable(badge, binding.toolBar, R.id.shopping_cart)
        } else {
            // badge.badgeTextColor = ContextCompat.getColor(this, R.color.purple_200)
            badge?.number = itemsInTheCartList.size
            badge?.let { BadgeUtils.attachBadgeDrawable(it, binding.toolBar, R.id.shopping_cart) }
        }
    }
}


