package com.pan.foodbox.ui

import android.os.Bundle
import android.view.MenuItem
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.pan.foodbox.R
import com.pan.foodbox.adapter.PagerAdapter
import com.pan.foodbox.databinding.ActivityDetailBinding
import com.pan.foodbox.fragment.IngredientFragment
import com.pan.foodbox.fragment.InstructionFragment
import com.pan.foodbox.fragment.OverviewFragment
import com.pan.foodbox.models.RestaurantMenu

import com.google.android.material.tabs.TabLayoutMediator

class DetailActivity : BaseActivity() {
    private lateinit var restaurantMenu: RestaurantMenu
    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white))
        restaurantMenu = intent.getParcelableExtra("restaurant")!!

        val fragment = ArrayList<Fragment>()
        fragment.add(OverviewFragment())
        fragment.add(InstructionFragment())
        val ingredientFragment = IngredientFragment()

        val bundle = Bundle()

        bundle.putParcelable("list", restaurantMenu)
        ingredientFragment.arguments = bundle


        fragment.add(IngredientFragment())
        val title = ArrayList<String>()
        title.add("Overview")
        title.add("Instruction")
        title.add("Ingredient")


        val pagerAdapter = PagerAdapter(bundle, fragment, this)

        binding.viewPager.isUserInputEnabled = false

        binding.viewPager.apply {
            adapter = pagerAdapter
        }

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = title[position]
        }
            .attach()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }
}