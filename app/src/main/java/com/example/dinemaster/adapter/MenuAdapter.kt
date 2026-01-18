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
    private val mode: String = MenuFragment.MODE_EDIT,
    private val onItemClick: (MenuItemApi) -> Unit,
    private val onQtyChange: (() -> Unit)? = null
) : RecyclerView.Adapter<MenuAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivFood: ImageView = itemView.findViewById(R.id.imgFoodItem)
        val tvName: TextView = itemView.findViewById(R.id.tvMenuName)
        val tvPrice: TextView = itemView.findViewById(R.id.tvPrice)
        val tvQty: TextView = itemView.findViewById(R.id.tvQuantity)
        val btnPlus: ImageButton = itemView.findViewById(R.id.btnPlus)
        val btnMinus: ImageButton = itemView.findViewById(R.id.btnMinus)
        val llQuantityControl: LinearLayout = itemView.findViewById(R.id.llQuantityControl)

        fun bind(item: MenuItemApi) {
            ivFood.load(item.image_url) {
                placeholder(R.drawable.food)
                error(R.drawable.food)
            }

            tvName.text = item.name
            tvPrice.text = "₹%.2f".format(item.base_price.toDouble())
            tvQty.text = item.qty.toString()

            llQuantityControl.visibility =
                if (mode.equals(MenuFragment.MODE_EDIT, true)) View.VISIBLE else View.GONE

            itemView.setOnClickListener {
                onItemClick(item)
            }

            btnPlus.setOnClickListener {
                val pos = bindingAdapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    items[pos].qty++
                    notifyItemChanged(pos)
                    onQtyChange?.invoke()
                }
            }

            btnMinus.setOnClickListener {
                val pos = bindingAdapterPosition
                if (pos != RecyclerView.NO_POSITION && items[pos].qty > 0) {
                    items[pos].qty--
                    notifyItemChanged(pos)
                    onQtyChange?.invoke()
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
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = items.size

    fun updateItems(newItems: List<MenuItemApi>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    fun getItems(): List<MenuItemApi> = items
}
