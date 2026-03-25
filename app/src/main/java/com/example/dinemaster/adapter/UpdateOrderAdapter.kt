package com.example.dinemaster.adapter

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.dinemaster.R
import com.example.dinemaster.model.FoodItem
import com.example.dinemaster.model.OrderItemData


class UpdateOrderAdapter(
    private val items: MutableList<OrderItemData>,
    private val onDeleteClick: (OrderItemData, Int) -> Unit
) : RecyclerView.Adapter<UpdateOrderAdapter.FoodViewHolder>() {

    inner class FoodViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvFoodName: TextView = view.findViewById(R.id.tvFoodName)
        val tvQuantity: TextView = view.findViewById(R.id.tvQuantity)
        val btnMinus: ImageButton = view.findViewById(R.id.btnMinus)
        val btnPlus: ImageButton = view.findViewById(R.id.btnPlus)
        val btnDelete: ImageButton = view.findViewById(R.id.btnDelete) // 👈 add this in XML
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order_food, parent, false)
        return FoodViewHolder(view)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        val item = items[position]
        holder.tvFoodName.text = item.item_name
        holder.tvQuantity.text = item.quantity.toString()

        // Minus button logic
        // Minus button
        holder.btnMinus.setOnClickListener {

            val currentQty = holder.tvQuantity.text.toString()
                .toDoubleOrNull()?.toInt() ?: 0

            if (currentQty > 1) {

                val newQty = currentQty - 1

                items[position] = item.copy(quantity = newQty.toString())
                holder.tvQuantity.text = newQty.toString()

            } else {
                Toast.makeText(
                    holder.itemView.context,
                    "Qty cannot be less than 1",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        // Plus button
        holder.btnPlus.setOnClickListener {

            val currentQty = holder.tvQuantity.text.toString()
                .toDoubleOrNull()?.toInt() ?: 0

            val newQty = currentQty + 1

            items[position] = item.copy(quantity = newQty.toString())
            holder.tvQuantity.text = newQty.toString()
        }

        holder.btnDelete.setOnClickListener {

            val pos = holder.adapterPosition
            if (pos != RecyclerView.NO_POSITION) {

                val dialog = AlertDialog.Builder(holder.itemView.context)
                    .setTitle("Delete Item")
                    .setMessage("Remove ${items[pos].item_name}?")
                    .setPositiveButton("Delete") { _, _ ->

                        // 🔥 Instead of removing → set qty = 0
                        val item = items[pos]
                        items[pos] = item.copy(quantity = "0")

                        notifyItemChanged(pos)

                        Toast.makeText(
                            holder.itemView.context,
                            "${item.item_name} marked for removal",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    .setNegativeButton("Cancel", null)
                    .create()

                dialog.show()
                dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    .setTextColor(ContextCompat.getColor(holder.itemView.context, android.R.color.holo_red_dark))
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                    .setTextColor(ContextCompat.getColor(holder.itemView.context, android.R.color.black))
            }
        }


    }

    override fun getItemCount() = items.size
}

