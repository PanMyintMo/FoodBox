package com.pan.foodbox.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.pan.foodbox.R
import com.pan.foodbox.databinding.ItemRestaurantBinding
import com.pan.foodbox.models.Hours
import com.pan.foodbox.models.Restaurant
import com.pan.foodbox.ui.RestaurantMenuActivity
import java.util.*
import kotlin.collections.ArrayList

class RestaurantAdapter(
    private val restaurants: ArrayList<Restaurant>,
    private val onItemClickedListener: OnItemClickedListener
) :
    RecyclerView.Adapter<RestaurantAdapter.PlaceHolder>(),Filterable {

    private var userFilter:ArrayList<Restaurant> = arrayListOf()

    init {
        userFilter = restaurants
    }

    private val calendar = java.util.Calendar.getInstance()
    private val todayName =
        calendar.getDisplayName(java.util.Calendar.DAY_OF_WEEK, java.util.Calendar.LONG, Locale.ENGLISH) ?: "Sunday"

    inner class PlaceHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemRestaurantBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceHolder {
        return PlaceHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_restaurant, parent, false)
        )
    }

    override fun onBindViewHolder(holder: PlaceHolder, position: Int) {
        val restaurant = userFilter[position]

        holder.binding.apply {
            tvName.text = restaurant.name
            tvAddress.text = restaurant.address
            tvOpenHours.text = getTodayOpenHours(restaurant.hours)

            Glide.with(ivThumbnail)
                .load(restaurant.image)
                .placeholder(R.drawable.ic_restaurant_placeholder)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(ivThumbnail)
            root.setOnClickListener {

                onItemClickedListener.onItemClick(restaurant)
                val intent = Intent(holder.itemView.context, RestaurantMenuActivity::class.java)
            intent.putExtra(RestaurantMenuActivity.EXTRA_RESTAURANT, restaurant)
            holder.itemView.context.startActivity(intent)
        }
        }
    }

    private fun getTodayOpenHours(hours: Hours): String {
        return when (todayName) {
            "Sunday" -> hours.sunday
            "Monday" -> hours.monday
            "Tuesday" -> hours.tuesday
            "Wednesday" -> hours.wednesday
            "Thursday" -> hours.thursday
            "Friday" -> hours.friday
            "Saturday" -> hours.saturday
            else -> hours.sunday
        }
    }

    override fun getItemCount(): Int {
        return userFilter.size
    }

    interface OnItemClickedListener {
        fun onItemClick(restaurant: Restaurant)
    }

    override fun getFilter(): Filter {
       return object :Filter(){
           override fun performFiltering(p0: CharSequence?): FilterResults {
               val charSequence=p0.toString()
              userFilter= if (charSequence.isEmpty()){
                  restaurants
              }
               else {

                   val resultList:ArrayList<Restaurant> = arrayListOf()
                   for (row in restaurants){
                       if (row.name.contains(charSequence)){
                           resultList.add(row)
                       }
                   }
                  resultList
              }

               val filterResults=FilterResults()
               filterResults.values=userFilter
               return filterResults
           }

           override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
              userFilter = p1?.values as ArrayList<Restaurant>
               notifyDataSetChanged()
           }

       }
    }
}