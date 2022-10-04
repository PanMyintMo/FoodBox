package com.pan.foodbox.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pan.foodbox.R
import com.pan.foodbox.databinding.ItemOrderHistoryBinding
import com.pan.foodbox.entity.ItemHistory
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class OrderHistoryAdapter(
    private val context: Context,
    private val orderHistoryList: ArrayList<ItemHistory>,
    private val onReorderClickedListener: OnReorderClickedListener
) : RecyclerView.Adapter<OrderHistoryAdapter.PlaceHolder>() {
    private val simpleDateFormat = SimpleDateFormat("h:mm a, dd /mm /yy", Locale.getDefault())

    inner class PlaceHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val binding = ItemOrderHistoryBinding.bind(itemView)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceHolder {
        return PlaceHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_order_history, parent, false)
        )
    }

    override fun onBindViewHolder(holder: PlaceHolder, position: Int) {
        val orderHistory = orderHistoryList[position]
        holder.binding.apply {

            tvCreatedAt.text = simpleDateFormat.format(Date(orderHistory.createdAt))
            tvRestaurantName.text = orderHistory.restaurantName
            tvMenuList.text = orderHistory.cartItem.joinToString(separator = " , ") { it.name }
            btnReorder.setOnClickListener {
                onReorderClickedListener.onRecorderClick(orderHistory)
            }

        }
    }

    override fun getItemCount(): Int {
        return orderHistoryList.size
    }

    interface OnReorderClickedListener {
        fun onRecorderClick(orderHistory: ItemHistory)
    }
}