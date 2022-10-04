package com.pan.foodbox.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.pan.foodbox.R
import com.pan.foodbox.databinding.ItemCartBinding
import com.pan.foodbox.entity.CartItem
import com.pan.foodbox.ui.ShoppingCartActivity

class CartItemAdapter(
    private val context: Context,
    private val cartItems: ArrayList<CartItem>,
    private val onCartUpdateListener: OnCartUpdateListener?
) :
    RecyclerView.Adapter<CartItemAdapter.PlaceHolder>() {

    inner class PlaceHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemCartBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceHolder {
        return PlaceHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_cart, parent, false)
        )
    }

    override fun onBindViewHolder(holder: PlaceHolder, position: Int) {
        val cartItem = cartItems[position]

        holder.binding.apply {
            tvName.text = cartItem.name
            tvCount.text = cartItem.count.toString()
            tvPrice.text =
                context.getString(R.string.title_price_unit, cartItem.price)

            Glide.with(ivThumbnail)
                .load(cartItem.url)
                .placeholder(R.drawable.ic_restaurant_placeholder)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(ivThumbnail)

            val buttonsVisibility = if (onCartUpdateListener == null) View.GONE else View.VISIBLE
            btnAdd.visibility = buttonsVisibility
            btnRemove.visibility = buttonsVisibility

            val countRightPadding = if (onCartUpdateListener == null) 80 else 0
            tvCount.setPadding(0, 0, countRightPadding, 0)

            btnAdd.setOnClickListener {
                if (cartItem.count < ShoppingCartActivity.MAX_ITEM_COUNT) {
                    cartItem.count++
                } else {
                    btnAdd.isEnabled = false
                }

                tvCount.text = cartItem.count.toString()
                onCartUpdateListener?.onCartUpdated(holder.adapterPosition, cartItem)
            }

            btnRemove.setOnClickListener {
                if (cartItem.count <= 1) {
                    onCartUpdateListener?.onCartRemoved(holder.adapterPosition, cartItem)
                } else {
                    cartItem.count--
                    btnAdd.isEnabled = true
                    onCartUpdateListener?.onCartUpdated(holder.adapterPosition, cartItem)
                }

                tvCount.text = cartItem.count.toString()
            }
        }
    }

    override fun getItemCount(): Int {
        return cartItems.size
    }

    interface OnCartUpdateListener {
        fun onCartUpdated(position: Int, cartItem: CartItem)
        fun onCartRemoved(position: Int, cartItem: CartItem)
    }
}