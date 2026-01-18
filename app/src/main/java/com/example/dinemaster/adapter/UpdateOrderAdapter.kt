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


class UpdateOrderAdapter(
    private val items: MutableList<FoodItem>
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
        holder.tvFoodName.text = item.name
        holder.tvQuantity.text = item.qty.toString()

        // Minus button logic
        holder.btnMinus.setOnClickListener {
            var currentQty = holder.tvQuantity.text.toString().toInt()
            if (currentQty > 1) {
                currentQty--
                items[position] = item.copy(qty = currentQty)
                holder.tvQuantity.text = currentQty.toString()
            } else {
                Toast.makeText(holder.itemView.context, "Qty cannot be less than 1", Toast.LENGTH_SHORT).show()
            }
        }

        // Plus button logic
        holder.btnPlus.setOnClickListener {
            var currentQty = holder.tvQuantity.text.toString().toInt()
            currentQty++
            items[position] = item.copy(qty = currentQty)
            holder.tvQuantity.text = currentQty.toString()
        }

        // Delete button logic
//        holder.btnDelete.setOnClickListener {
//            AlertDialog.Builder(holder.itemView.context)
//                .setTitle("Delete Item")
//                .setMessage("Are you sure you want to delete ${item.name}?")
//                .setPositiveButton("Yes") { _, _ ->
//                    items.removeAt(position)
//                    notifyItemRemoved(position)
//                    notifyItemRangeChanged(position, items.size)
//                    Toast.makeText(holder.itemView.context, "${item.name} removed", Toast.LENGTH_SHORT).show()
//                }
//                .setNegativeButton("No", null)
//                .show()
//        }
        // Delete button logic
        holder.btnDelete.setOnClickListener {
            val pos = holder.adapterPosition
            if (pos != RecyclerView.NO_POSITION) {
                val dialog = AlertDialog.Builder(holder.itemView.context)
                    .setTitle("Delete Item")
                    .setMessage("Are you sure you want to delete ${items[pos].name}?")
                    .setPositiveButton("Delete") { _, _ ->
                        val removedItem = items[pos]
                        items.removeAt(pos)
                        notifyItemRemoved(pos)
                        Toast.makeText(holder.itemView.context, "${removedItem.name} removed", Toast.LENGTH_SHORT).show()
                    }
                    .setNegativeButton("Cancel", null)
                    .create()

                dialog.show()

                // Change button colors
                dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    .setTextColor(ContextCompat.getColor(holder.itemView.context, android.R.color.holo_red_dark))
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                    .setTextColor(ContextCompat.getColor(holder.itemView.context, android.R.color.black))
            }
        }


    }

    override fun getItemCount() = items.size
}

