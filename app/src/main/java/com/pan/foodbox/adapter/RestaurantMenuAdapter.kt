package com.pan.foodbox.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.pan.foodbox.R
import com.pan.foodbox.databinding.ItemRestaurantMenuBinding
import com.pan.foodbox.models.RestaurantMenu
import com.pan.foodbox.ui.DetailActivity

class RestaurantMenuAdapter(
    private val context: Context,
    private val restaurantMenuList: ArrayList<RestaurantMenu>,
    private val cartCheckedList: ArrayList<Boolean>,
    private val onCartClickedListener: OnCartClickedListener,

    ) :
    RecyclerView.Adapter<RestaurantMenuAdapter.PlaceHolder>(), Filterable {

    private var userFilter: ArrayList<RestaurantMenu> = arrayListOf()

    init {
        userFilter = restaurantMenuList
    }

    inner class PlaceHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemRestaurantMenuBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceHolder {
        return PlaceHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_restaurant_menu, parent, false)
        )
    }

    override fun onBindViewHolder(holder: PlaceHolder, position: Int) {
        val restaurantMenu = userFilter[position]
        var isCartChecked = cartCheckedList[position]

        holder.binding.apply {
            tvName.text = restaurantMenu.name
            tvPrice.text =
                context.getString(R.string.title_price_unit, restaurantMenu.price)
            btnCart.setIconResource(getCartIcon(isCartChecked))

            Glide.with(ivThumbnail)
                .load(restaurantMenu.url)
                .placeholder(R.drawable.ic_restaurant_placeholder)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(ivThumbnail)

            layoutDetail.setOnClickListener {
                val intent = Intent(context, DetailActivity::class.java)
                intent.putExtra("restaurant", restaurantMenu)
                context.startActivity(intent)
            }
            btnCart.setOnClickListener {
                isCartChecked = !isCartChecked
                btnCart.setIconResource(getCartIcon(isCartChecked))
                onCartClickedListener.onCartClick(
                    holder.adapterPosition,
                    isCartChecked
                )
            }
        }
    }

    @DrawableRes
    private fun getCartIcon(isCartChecked: Boolean): Int {
        return if (isCartChecked) R.drawable.ic_remove_shopping_cart else R.drawable.ic_add_shopping_cart
    }

    override fun getItemCount(): Int {
        return userFilter.size
    }

    interface OnCartClickedListener {
        fun onCartClick(position: Int, isCartChecked: Boolean)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(p0: CharSequence?): FilterResults {
                val charSequence = p0.toString()
                userFilter = if (charSequence.isEmpty()) {
                    restaurantMenuList
                } else {
                    val resultList: ArrayList<RestaurantMenu> = arrayListOf()
                    for (row in restaurantMenuList) {
                        if (row.name.contains(charSequence, true)) {
                            resultList.add(row)
                        }
                    }
                    resultList
                }
                val filterResults = FilterResults()
                filterResults.values = userFilter
                return filterResults
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
                userFilter =
                    p1?.values as ArrayList<RestaurantMenu>/* = java.util.ArrayList<com.example.foodsample.models.RestaurantMenu> */
                notifyDataSetChanged()
            }
        }
    }
    }