package com.example.foodsample.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodsample.adapter.MenuRecordAdapter
import com.example.foodsample.databinding.ActionBarNotificationIconBinding
import com.example.foodsample.models.Menu
import com.example.foodsample.models.RestaurantDataModel



class ShoppingCartActivity : AppCompatActivity(), MenuRecordAdapter.OnItemClick {


    private lateinit var newMenuItem: MutableList<Menu>

    private var menuRecordAdapter: MenuRecordAdapter? = null

    private var restaurantModel: RestaurantDataModel? = null

    private lateinit var binding: ActionBarNotificationIconBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActionBarNotificationIconBinding.inflate(layoutInflater)
        setContentView(binding.root)

        restaurantModel = intent?.getParcelableExtra("menu_data_added")
        newMenuItem = restaurantModel?.menus as MutableList<Menu>
        val actionBar = supportActionBar
        actionBar?.title = "Basket List"
        actionBar?.subtitle = "Address " + restaurantModel?.address
        actionBar?.setDisplayHomeAsUpEnabled(true)

        initRecyclerView(restaurantModel)

    }

    private fun initRecyclerView(restaurantModel: RestaurantDataModel?) {
        binding.recyclerRecord.layoutManager = LinearLayoutManager(this)
        menuRecordAdapter = MenuRecordAdapter(newMenuItem, this)
        binding.recyclerRecord.adapter = menuRecordAdapter

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun deleteCartListItem(menu: Menu, position: Int) {
        Toast.makeText(this, "Delete reach", Toast.LENGTH_SHORT).show()
        newMenuItem.removeAt(position)
        menuRecordAdapter?.notifyItemRemoved(position)
    }


}





