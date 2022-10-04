package com.pan.foodbox.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.SearchView
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.pan.foodbox.R
import com.pan.foodbox.SettingActivity
import com.pan.foodbox.adapter.RestaurantAdapter
import com.pan.foodbox.databinding.ActivityMainBinding
import com.pan.foodbox.models.Restaurant
import com.pan.foodbox.util.GlideApp
import com.pan.foodbox.util.SpacingItemDecoration
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class MainActivity : BaseActivity(),
    RestaurantAdapter.OnItemClickedListener {
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var binding: ActivityMainBinding
    private val restaurants = arrayListOf<Restaurant>()
    private lateinit var adapter: RestaurantAdapter
    private lateinit var firebaseAuth: FirebaseAuth
    private var searchView: SearchView? = null

    private lateinit var sharePreference: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val actionBar = supportActionBar
        actionBar?.title = "Restaurant List"
        actionBar?.setDisplayHomeAsUpEnabled(true)
        firebaseAuth = FirebaseAuth.getInstance()

        sharePreference = getSharedPreferences("success", MODE_PRIVATE)
        val editor=sharePreference.edit()
        editor.putBoolean("isRegister",true)
        editor.apply()


        val currentUser: FirebaseUser? = firebaseAuth.currentUser

        val navigationView = findViewById(R.id.nvView) as NavigationView
        val navHeader = navigationView.getHeaderView(0)
        val nvEmail = navHeader.findViewById(R.id.user_mail) as TextView
        val nvName = navHeader.findViewById(R.id.user_name) as TextView
        val nvProfile = navHeader.findViewById(R.id.navProfile) as ImageView

        val db = FirebaseFirestore.getInstance()
        val uid = firebaseAuth.currentUser?.uid


        if (currentUser != null) {
            db.collection("users")
                .document(uid.toString()).get()
                .addOnCompleteListener {
                    nvName.text = it.result.get("name").toString()
                    nvEmail.text = currentUser.email
                    val profilePath = "profiles/$uid/profile.jpg"
                    val profileReference = Firebase.storage.reference.child(profilePath)
                    profileReference.downloadUrl.addOnCompleteListener {
                        GlideApp.with(applicationContext)
                            .load(profileReference)
                            .into(nvProfile)
                    }
                }
        }

        toggle = ActionBarDrawerToggle(this, binding.drawerLayout, R.string.open, R.string.close)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        binding.nvView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_call -> {
                    val dialIntent = Intent(Intent.ACTION_DIAL)
                    dialIntent.data = Uri.parse("tel:" + "09798217582")
                    startActivity(dialIntent)
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                }

                R.id.share -> {
                    val sendIntent = Intent()
                    sendIntent.action = Intent.ACTION_SEND
                    sendIntent.putExtra(Intent.EXTRA_EMAIL, "Hey check out this great App!.")
                    sendIntent.type = "text/plain"
                    startActivity(sendIntent)
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                }
                R.id.rate -> {
                    startActivity(Intent(this@MainActivity, RatingBarActivity::class.java))
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                }
                R.id.reorder -> {
                    val intent = Intent(this@MainActivity, OrderHistoryActivity::class.java)
                    startActivity(intent)
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                }
                R.id.logout -> {
                  //  sharePreference.edit().remove("isRegister").commit()
                    forSingOut()
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                }

                R.id.setting -> {
                    val intent = Intent(this@MainActivity, SettingActivity::class.java)
                    startActivity(intent)
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                }

                else -> {
                    Toast.makeText(this@MainActivity, "Something Wrong!", Toast.LENGTH_SHORT).show()
                }
            }
            true
        }

        initRecyclerView()
        restaurants.addAll(getRestaurants())
        adapter.notifyItemRangeInserted(0, restaurants.size)
    }

    private fun forSingOut() {
        MaterialAlertDialogBuilder(this)
            .setMessage("Do you want to logout?")
            .setPositiveButton("Ok") { _, _ ->
                FirebaseAuth.getInstance().signOut()
                finish()
            }
            .setNegativeButton("Cancel", null)
            .setCancelable(false)
            .show()

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initRecyclerView() {
        val decoration = SpacingItemDecoration(1, toPx(15), true)
        binding.recyclerRestaurant.addItemDecoration(decoration)
        binding.recyclerRestaurant.setHasFixedSize(true)
        binding.recyclerRestaurant.layoutManager = LinearLayoutManager(this)
        adapter = RestaurantAdapter(restaurants, this)
        binding.recyclerRestaurant.adapter = adapter
    }

    private fun getRestaurants(): List<Restaurant> {
        val json = assets.open(getFileName())
            .bufferedReader()
            .use { it.readText() }
        val restaurantListType = object : TypeToken<List<Restaurant>>() {}.type
        return Gson().fromJson(json, restaurantListType) as List<Restaurant>
    }

    private fun getFileName(): String {
        sharePreference = getSharedPreferences("preference", Context.MODE_PRIVATE)
        return if (sharePreference.getString("lan", "en") == "en") "en/restaurants.json"
        else "mm/restaurants.json"
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.restaurant_search, menu)
        val search = menu?.findItem(R.id.rest_search)
        searchView = search?.actionView as? SearchView
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                adapter.filter.filter(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return true
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    override fun onItemClick(restaurant: Restaurant) {

        /* val intent = Intent(this@MainActivity, DetailActivity::class.java)
         intent.putExtra(RestaurantMenuActivity.EXTRA_RESTAURANT, restaurants)
         startActivity(intent)*/

    }

    override fun onStart() {
        super.onStart()
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        if (firebaseUser == null) {
            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
        }
    }
}