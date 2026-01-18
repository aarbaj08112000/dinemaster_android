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

class OrderAdapter(
    private val orders: List<Order>,
    private val onOrderClick: (Order) -> Unit
) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvOrderId: TextView = itemView.findViewById(R.id.tvOrderId)
        val tvTableNo: TextView = itemView.findViewById(R.id.tvTableNo)
        val tvDetails: TextView = itemView.findViewById(R.id.tvOrderDetails)
        val tvStatus: TextView = itemView.findViewById(R.id.tvOrderStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_order, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]

        holder.tvOrderId.text = "Order #${order.orderId}"
        holder.tvTableNo.text = "Table: ${order.tableNo}"
        holder.tvDetails.text = order.details
        holder.tvStatus.text = order.status

        when (order.status.lowercase()) {
            "pending" -> holder.tvStatus.setTextColor(
                holder.itemView.context.getColor(android.R.color.holo_orange_dark)
            )
            "preparing" -> holder.tvStatus.setTextColor(
                holder.itemView.context.getColor(android.R.color.holo_blue_dark)
            )
            "served" -> holder.tvStatus.setTextColor(
                holder.itemView.context.getColor(android.R.color.holo_green_dark)
            )
            else -> holder.tvStatus.setTextColor(
                holder.itemView.context.getColor(android.R.color.darker_gray)
            )
        }

        holder.itemView.setOnClickListener {
            onOrderClick(order)   // callback
        }
    }

    override fun getItemCount(): Int = orders.size
}
