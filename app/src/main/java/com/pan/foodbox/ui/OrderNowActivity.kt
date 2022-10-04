package com.pan.foodbox.ui


import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.annotation.StringRes
import androidx.core.widget.addTextChangedListener
import com.pan.foodbox.R
import com.pan.foodbox.adapter.CartItemAdapter
import com.pan.foodbox.databinding.ActivityOrderNowBinding
import com.pan.foodbox.entity.CartItem
import com.pan.foodbox.entity.ItemHistory
import com.pan.foodbox.models.Restaurant
import com.pan.foodbox.models.RestaurantMenu
import com.pan.foodbox.util.SpacingItemDecoration
import com.google.android.material.textfield.TextInputLayout
import com.pan.foodbox.database.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class OrderNowActivity : BaseActivity() {

    private lateinit var binding: ActivityOrderNowBinding
    private lateinit var restaurant: Restaurant
    private lateinit var restaurantMenu: ArrayList<RestaurantMenu>
    private lateinit var appDatabase: AppDatabase

    private lateinit var cartItems: ArrayList<CartItem>
    private lateinit var adapter: CartItemAdapter


    private var subtotalPrice = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderNowBinding.inflate(layoutInflater)
        setContentView(binding.root)


        supportActionBar?.title ="Order Now"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        appDatabase = AppDatabase.getInstance(this)

        intent.getParcelableExtra<Restaurant>(RestaurantMenuActivity.EXTRA_RESTAURANT)?.let {
            restaurant = it
            restaurantMenu = restaurant.menus

            loadCartItems()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        onBackPressed()
        return super.onOptionsItemSelected(item)
    }

    private fun loadCartItems() {
        CoroutineScope(Dispatchers.IO).launch {
            cartItems = appDatabase.cartItemDao()
                .getAllFromRestaurant(restaurant.name) as ArrayList<CartItem>
            launch(Dispatchers.Main) { initViews() }
        }
    }

    private fun initViews() {
        val decoration = SpacingItemDecoration(1, toPx(15), true)
        binding.recycler.addItemDecoration(decoration)
        binding.recycler.setHasFixedSize(true)
        adapter = CartItemAdapter(this, cartItems, null)
        binding.recycler.adapter = adapter
        binding.edtName.addTextChangedListener { binding.edtNameLayout.error = null }
        binding.edtAddress.addTextChangedListener { binding.edtAddressLayout.error = null }
        binding.edtCity.addTextChangedListener { binding.edtCityLayout.error = null }
        binding.edtState.addTextChangedListener { binding.edtNameLayout.error = null }
        binding.edtZip.addTextChangedListener { binding.edtZipLayout.error = null }
        binding.edtCardNumber.addTextChangedListener { binding.edtCardNumberLayout.error = null }
        binding.edtCardExpiry.addTextChangedListener { binding.edtCardExpiryLayout.error = null }
        binding.edtCardPin.addTextChangedListener { binding.edtCardPinLayout.error = null }

        for (cartItem in cartItems) {
            subtotalPrice += cartItem.price * cartItem.count
        }

        binding.tvSubtotal.text = getString(R.string.title_price_unit, subtotalPrice)
        updateTotalPrice(false)

        binding.switchDelivery.setOnCheckedChangeListener { _, isChecked ->
            binding.deliveryLayout.visibility = if (isChecked) View.VISIBLE else View.GONE
            if (isChecked) {
                binding.deliveryCharge.visibility = View.VISIBLE
                binding.tvDeliveryCharge.visibility = View.VISIBLE
                binding.tvDeliveryCharge.text =
                    getString(R.string.title_price_unit, restaurant.deliveryCharge)
                updateTotalPrice(isChecked)
            } else {
                binding.deliveryCharge.visibility = View.GONE
                binding.tvDeliveryCharge.visibility = View.GONE
                updateTotalPrice(false)
            }
        }
        binding.btnOrderNow.setOnClickListener { checkForm() }
    }

    private fun updateTotalPrice(isDeliveryPicked: Boolean) {
        val totalPrice =
            if (isDeliveryPicked) subtotalPrice + restaurant.deliveryCharge else subtotalPrice + 0
        binding.tvTotal.text = getString(R.string.title_price_unit, totalPrice)
    }

    private fun checkForm() {
        val name = binding.edtName.text.toString()
        val isDeliveryPicked = binding.switchDelivery.isChecked
        val address = binding.edtAddress.text.toString()
        val city = binding.edtCity.text.toString()
        val state = binding.edtState.text.toString()
        val zipCode = binding.edtZip.text.toString()
        val cardNumber = binding.edtCardNumber.text.toString()
        val cardExpiry = binding.edtCardExpiry.text.toString()
        val cardPin = binding.edtCardPin.text.toString()


        when {
            name.isBlank() -> showError(binding.edtNameLayout, R.string.hint_name)

            isDeliveryPicked && address.isBlank() -> showError(
                binding.edtAddressLayout,
                R.string.hint_address
            )

            isDeliveryPicked && city.isBlank() -> showError(
                binding.edtCityLayout,
                R.string.hint_city
            )

            isDeliveryPicked && state.isBlank() -> showError(
                binding.edtStateLayout,
                R.string.hint_state
            )

            isDeliveryPicked && zipCode.isBlank() -> showError(
                binding.edtZipLayout,
                R.string.hint_zip_code
            )

            cardNumber.isBlank() -> showError(
                binding.edtCardNumberLayout,
                R.string.hint_card_number
            )

            cardExpiry.isBlank() -> showError(
                binding.edtCardExpiryLayout,
                R.string.hint_card_expiry
            )

            cardPin.isBlank() -> showError(binding.edtCardPinLayout, R.string.hint_card_pin)

            else -> orderNow(name)
        }
    }

    private fun showError(textInputLayout: TextInputLayout, @StringRes hintName: Int) {
        textInputLayout.error =
            getString(R.string.err_empty, getString(hintName))
        binding.scrollView.smoothScrollTo(0, 0)
    }

    private fun orderNow(customerName: String) {
        // order items to your server here
        CoroutineScope(Dispatchers.IO).launch {
            //save History
            appDatabase.daoRecord().insert(
                ItemHistory(
                    cartItem = cartItems,
                    restaurantName = restaurant.name,
                    customerName = customerName
                )
            )

            // delete all menu from cart
            appDatabase.cartItemDao().deleteAllFromRestaurant(restaurant.name)
            launchOrderSuccessActivity()

        }
    }

    private fun launchOrderSuccessActivity() {
        startActivity(Intent(this, OrderSuccessActivity::class.java))
    }
}