package com.example.dinemaster.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dinemaster.R
import com.example.dinemaster.model.Category

class CategoryAdapter(
    private var categories: List<Category>,
    private val onCategoryClick: (Category) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    private var filteredList = categories.toMutableList()
    private var selectedPos =
        if (categories.isNotEmpty()) 0 else RecyclerView.NO_POSITION

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val text: TextView = itemView.findViewById(R.id.tvCategory)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun getItemCount() = filteredList.size

    override fun onBindViewHolder(
        holder: CategoryViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
        val category = filteredList[position]

        holder.text.text = category.name   // 👈 API value

        // Highlight selected tab
        holder.text.setBackgroundResource(
            if (position == selectedPos)
                R.drawable.category_tab_bg_selected
            else
                R.drawable.category_tab_bg_unselected
        )

        holder.itemView.setOnClickListener {
            val prevPos = selectedPos
            selectedPos = position
            notifyItemChanged(prevPos)
            notifyItemChanged(selectedPos)

            onCategoryClick(category)
        }

        // Auto select first category
        if (position == 0 && selectedPos == 0) {
            holder.text.post {
                onCategoryClick(category)
            }
        }
    }

    // 🔄 Update list after API call
    fun updateList(newList: List<Category>) {
        categories = newList
        filteredList = newList.toMutableList()
        selectedPos = if (newList.isNotEmpty()) 0 else RecyclerView.NO_POSITION
        notifyDataSetChanged()
    }

    // 🔍 Search filter
    fun filter(query: String) {
        filteredList = if (query.isEmpty()) {
            categories.toMutableList()
        } else {
            categories.filter {
                it.name.contains(query, ignoreCase = true)
            }.toMutableList()
        }

        selectedPos = if (filteredList.isNotEmpty()) 0 else RecyclerView.NO_POSITION
        notifyDataSetChanged()
    }
}
