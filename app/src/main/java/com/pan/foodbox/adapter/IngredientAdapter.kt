package com.pan.foodbox.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pan.foodbox.models.Ingredient
import com.pan.foodbox.R
import com.pan.foodbox.databinding.IngredientRowBinding
import java.util.ArrayList

class IngredientAdapter(

    private val ingredientImageList: ArrayList<Ingredient>,
    private val url: String
) : RecyclerView.Adapter<IngredientAdapter.MyViewHolder>() {
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = IngredientRowBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.ingredient_row, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = ingredientImageList[position]
        holder.binding.apply {
            ingredientName.text=item.name
            ingredientAmount.text=item.amount
        }

        Glide.with(holder.binding.ingredientImage)
            .load(item.img)
            .into(holder.binding.ingredientImage)

    }

    override fun getItemCount(): Int {
        return ingredientImageList.size
    }
}