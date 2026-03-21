package com.example.dinemaster.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dinemaster.R
import com.example.dinemaster.model.FoodItem
import com.example.dinemaster.model.OrderItemData

class FoodItemAdapter(private val items: List<OrderItemData>) :
    RecyclerView.Adapter<FoodItemAdapter.FoodViewHolder>() {

    inner class FoodViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvFoodName: TextView = itemView.findViewById(R.id.tvFoodName)
        val tvQty: TextView = itemView.findViewById(R.id.tvQty)
        val tvPrice: TextView = itemView.findViewById(R.id.tvPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_food, parent, false)
        return FoodViewHolder(view)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {

        val item = items[position]

        val qty = item.quantity.toDouble().toInt()
        val price = item.unit_price.toDouble()

        holder.tvFoodName.text = item.item_name
        holder.tvQty.text = "Qty: $qty"

        val total = qty * price
        holder.tvPrice.text = "₹$total"
    }

    override fun getItemCount(): Int = items.size
}
