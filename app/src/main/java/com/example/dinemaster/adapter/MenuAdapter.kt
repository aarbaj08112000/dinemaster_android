package com.example.dinemaster.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.dinemaster.MenuFragment
import com.example.dinemaster.R
import com.example.dinemaster.model.MenuItemApi

class MenuAdapter(
    private val items: MutableList<MenuItemApi>,
    private val mode: String,
    private val onItemClick: (MenuItemApi) -> Unit,
    private val onQtyChange: (MenuItemApi) -> Unit
) : RecyclerView.Adapter<MenuAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val ivFood: ImageView = itemView.findViewById(R.id.imgFoodItem)
        val imgVegType: ImageView = itemView.findViewById(R.id.imgVegType)
        val tvName: TextView = itemView.findViewById(R.id.tvMenuName)
        val tvPrice: TextView = itemView.findViewById(R.id.tvPrice)
        val tvQty: TextView = itemView.findViewById(R.id.tvQuantity)
        val btnPlus: ImageButton = itemView.findViewById(R.id.btnPlus)
        val btnMinus: ImageButton = itemView.findViewById(R.id.btnMinus)
        val llQuantityControl: LinearLayout = itemView.findViewById(R.id.llQuantityControl)

        fun bind(item: MenuItemApi) {

            ivFood.load(item.image_url) {
                placeholder(R.drawable.food_placeholder)
                error(R.drawable.food_placeholder)
            }

            tvName.text = item.name
            tvPrice.text = "₹%.2f".format(item.base_price.toDoubleOrNull() ?: 0.0)
            tvQty.text = item.qty.toString()

            imgVegType.setImageResource(
                if (item.veg_type.equals("VEG", true))
                    R.drawable.ic_veg
                else
                    R.drawable.ic_nonveg
            )

            // ✅ FIXED VISIBILITY (EDIT + ADD show, VIEW hide)
            val isViewMode = mode.equals(MenuFragment.MODE_VIEW, true)
            llQuantityControl.visibility = if (isViewMode) View.GONE else View.VISIBLE

            itemView.setOnClickListener {
                onItemClick(item)
            }

            btnPlus.setOnClickListener {
                val pos = bindingAdapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    val updatedItem = items[pos]
                    updatedItem.qty++

                    notifyItemChanged(pos)
                    onQtyChange(updatedItem)
                }
            }

            btnMinus.setOnClickListener {
                val pos = bindingAdapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    val updatedItem = items[pos]

                    if (updatedItem.qty > 0) {
                        updatedItem.qty--

                        notifyItemChanged(pos)
                        onQtyChange(updatedItem)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_menu, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun updateItems(newItems: List<MenuItemApi>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    fun getItems(): List<MenuItemApi> = items
}