package com.pan.foodbox.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SearchView
import com.pan.foodbox.R
import com.pan.foodbox.adapter.RestaurantMenuAdapter
import com.pan.foodbox.databinding.ActivityRestaurantMenuBinding
import com.pan.foodbox.entity.CartItem
import com.pan.foodbox.models.Restaurant
import com.pan.foodbox.models.RestaurantMenu
import com.pan.foodbox.util.SpacingItemDecoration
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils
import com.pan.foodbox.database.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RestaurantMenuActivity : BaseActivity(), RestaurantMenuAdapter.OnCartClickedListener {

    companion object {
        const val EXTRA_RESTAURANT = "EXTRA_RESTAURANT"
    }

    private lateinit var binding: ActivityRestaurantMenuBinding
    private lateinit var restaurant: Restaurant
    private lateinit var appDatabase: AppDatabase
    private var restaurantMenuList: ArrayList<RestaurantMenu> = arrayListOf()
    private val cartCheckedList: ArrayList<Boolean> = arrayListOf()
    private lateinit var adapter: RestaurantMenuAdapter
    private var badgeDrawable: BadgeDrawable? = null
    private var totalInCart = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRestaurantMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)


        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        appDatabase = AppDatabase.getInstance(this)
        intent.getParcelableExtra<Restaurant>(EXTRA_RESTAURANT)?.let {
            restaurant = it
            restaurantMenuList = restaurant.menus
            restaurantMenuList.forEach { _ ->
                cartCheckedList.add(false)
            }
            initViews()
            loadCartItems()
            binding.search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    adapter.filter.filter(query)
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    adapter.filter.filter(newText)
                    return true
                }
            })
        }
    }
    private fun loadCartItems() {
        CoroutineScope(Dispatchers.IO).launch {
            val cartItems = appDatabase.cartItemDao().getAllFromRestaurant(restaurant.name)
            totalInCart = cartItems.size

            restaurantMenuList.forEachIndexed { index, restaurantMenu ->
                val cardItem = cartItems.firstOrNull { it.name == restaurantMenu.name }
                cartCheckedList[index] = cardItem != null
            }

            launch(Dispatchers.Main) {
                adapter.notifyItemRangeChanged(0, restaurantMenuList.size)
                updateBadgeWithCheckout()
            }
        }
    }



    private fun initViews() {
        supportActionBar?.title = restaurant.name
        supportActionBar?.subtitle = restaurant.address
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val decoration = SpacingItemDecoration(1, toPx(15), true)
        binding.recycler.addItemDecoration(decoration)
        binding.recycler.setHasFixedSize(true)

        adapter = RestaurantMenuAdapter(this, restaurantMenuList, cartCheckedList, this)
        binding.recycler.adapter = adapter

        binding.btnCheckout.setOnClickListener { launchShoppingCartActivity() }
    }


    private fun addMenuToDatabase(restaurantMenu: RestaurantMenu) {
        CoroutineScope(Dispatchers.IO).launch {
            appDatabase.cartItemDao().insert(
                CartItem(
                    restaurantName = restaurant.name,
                    name = restaurantMenu.name,
                    price = restaurantMenu.price,
                    url = restaurantMenu.url
                )
            )
        }
    }

    private fun removeMenuFromDatabase(restaurantMenu: RestaurantMenu) {
        CoroutineScope(Dispatchers.IO).launch {
            appDatabase.cartItemDao().delete(restaurantMenu.name)
        }
    }

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                loadCartItems()
            }
        }

    private fun launchShoppingCartActivity() {
        val intent = Intent(this, ShoppingCartActivity::class.java)
        intent.putExtra(EXTRA_RESTAURANT, restaurant)
        resultLauncher.launch(intent)
    }


    override fun onCartClick(position: Int, isCartChecked: Boolean) {
        cartCheckedList[position] = isCartChecked
        if (isCartChecked) {
            totalInCart++
            addMenuToDatabase(restaurantMenuList[position])
        } else {
            totalInCart--
            removeMenuFromDatabase(restaurantMenuList[position])
        }
        updateBadgeWithCheckout()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.restaurant_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            android.R.id.home ->onBackPressed()
            R.id.menu_shopping_cart -> launchShoppingCartActivity()
        }
        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun updateBadgeWithCheckout() {
        binding.btnCheckout.isEnabled = totalInCart != 0
        if (badgeDrawable == null) badgeDrawable = BadgeDrawable.create(this)
        badgeDrawable!!.number = totalInCart

        if (totalInCart == 0) {
            binding.btnCheckout.setText(R.string.btn_checkout)
            BadgeUtils.detachBadgeDrawable(badgeDrawable, binding.toolbar, R.id.menu_shopping_cart)

        } else {
            binding.btnCheckout.text = getString(R.string.btn_checkout_items, totalInCart)
            BadgeUtils.attachBadgeDrawable(
                badgeDrawable!!,
                binding.toolbar,
                R.id.menu_shopping_cart
            )
        }
    }
}