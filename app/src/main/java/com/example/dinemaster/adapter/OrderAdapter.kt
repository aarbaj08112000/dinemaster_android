package com.example.dinemaster.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.dinemaster.R
import com.example.dinemaster.model.Order
import com.example.dinemaster.model.OrderData

class OrderAdapter(
    private val orders: List<OrderData>,
    private val onOrderClick: (OrderData) -> Unit
) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvOrderId: TextView = itemView.findViewById(R.id.tvOrderId)
        val tvTableNo: TextView = itemView.findViewById(R.id.tvTableNo)
        val tvDetails: TextView = itemView.findViewById(R.id.tvOrderDetails)
        val tvStatus: TextView = itemView.findViewById(R.id.tvOrderStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {

        val order = orders[position]

        holder.tvOrderId.text = "Order #${order.order_id}"
        holder.tvTableNo.text = "Table: ${order.table_id}"

        // Convert items list to readable string
        val details = order.items.joinToString(", ") { item ->
            val qty = item.quantity.toDouble().toInt()
            "${qty}x ${item.item_name}"
        }

        holder.tvDetails.text = details

        holder.tvStatus.text = order.status

        val context = holder.itemView.context

        when (order.status.uppercase()) {

            "PLACED" -> holder.tvStatus.setTextColor(
                context.getColor(android.R.color.holo_orange_dark)
            )

            "PREPARING" -> holder.tvStatus.setTextColor(
                context.getColor(android.R.color.holo_blue_dark)
            )

            "READY" -> holder.tvStatus.setTextColor(
                context.getColor(android.R.color.holo_purple)
            )

            "SERVED" -> holder.tvStatus.setTextColor(
                context.getColor(android.R.color.holo_green_dark)
            )

            "PAID" -> holder.tvStatus.setTextColor(
                context.getColor(android.R.color.darker_gray)
            )

            else -> holder.tvStatus.setTextColor(
                context.getColor(android.R.color.black)
            )
        }

        holder.itemView.setOnClickListener {
            onOrderClick(order)
        }
    }

    override fun getItemCount(): Int = orders.size
}