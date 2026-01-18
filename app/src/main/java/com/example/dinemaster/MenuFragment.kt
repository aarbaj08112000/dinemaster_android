package com.example.dinemaster

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dinemaster.adapter.CategoryAdapter
import com.example.dinemaster.adapter.MenuAdapter
import com.example.dinemaster.model.Category
import com.example.dinemaster.model.CategoryRequest
import com.example.dinemaster.model.MenuItemApi
import com.example.dinemaster.model.MenuRequest
import kotlinx.coroutines.launch
import retrofit2.HttpException

class MenuFragment : Fragment(R.layout.fragment_menu) {

    private lateinit var rvCategories: RecyclerView
    private lateinit var rvMenu: RecyclerView
    private lateinit var etSearch: EditText
    private lateinit var btnOrderSave: Button
    private lateinit var chkVeg: CheckBox
    private lateinit var chkNonVeg: CheckBox

    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var menuAdapter: MenuAdapter

    private val categoryList = mutableListOf<Category>()
    private val menuItems = mutableListOf<MenuItemApi>()

    companion object {
        const val ARG_MODE = "type"
        const val MODE_VIEW = "ViewMode"
        const val MODE_EDIT = "EditMode"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? HomeActivity)?.setActiveTabByTag("Menu")
        (activity as? HomeActivity)?.updateHeader("Menu", true)

        rvCategories = view.findViewById(R.id.rvCategories)
        rvMenu = view.findViewById(R.id.rvMenu)
        etSearch = view.findViewById(R.id.etSearch)
        btnOrderSave = view.findViewById(R.id.btnOrderSave)
        chkVeg = view.findViewById(R.id.chkVeg)
        chkNonVeg = view.findViewById(R.id.chkNonVeg)

        // --- Setup Category RecyclerView ---
        rvCategories.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        categoryAdapter = CategoryAdapter(emptyList()) { category ->
            loadMenuForCategory(category.category_id)
        }
        rvCategories.adapter = categoryAdapter

        // --- Setup Menu RecyclerView ---
        val mode = arguments?.getString(ARG_MODE)?.ifBlank { MODE_VIEW } ?: MODE_VIEW
        rvMenu.layoutManager = GridLayoutManager(requireContext(), 2)
        menuAdapter = MenuAdapter(mutableListOf(), mode,
            onItemClick = { selectedItem ->
                val bundle = Bundle().apply { putString("name", selectedItem.name) }
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, MenuDetailFragment::class.java, bundle)
                    .addToBackStack(null)
                    .commit()
            },
            onQtyChange = { /* handle cart update if needed */ }
        )
        rvMenu.adapter = menuAdapter

        // --- Search filter ---
        etSearch.addTextChangedListener { query ->
            categoryAdapter.filter(query.toString())
        }

        // --- Save Order Button ---
        btnOrderSave.visibility = if (mode.equals(MODE_EDIT, ignoreCase = true)) View.VISIBLE else View.GONE
        btnOrderSave.setOnClickListener { saveOrder(mode) }

        // --- Load Categories from API ---
        loadCategoriesFromApi()
    }

    private fun saveOrder(mode: String) {
        val selectedItems = menuItems.filter { it.qty > 0 }
        if (selectedItems.isEmpty()) {
            Toast.makeText(requireContext(), "No items selected!", Toast.LENGTH_SHORT).show()
            return
        }

        val bundle = Bundle()
        bundle.putSerializable("order_items", ArrayList(selectedItems))
        bundle.putString(ARG_MODE, mode)
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, CreateOrderFragment::class.java, bundle)
            .addToBackStack(null)
            .commit()
    }

    // --- Load Categories from API ---
    private fun loadCategoriesFromApi() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                Log.d("API_REQUEST", "Calling categories with id=1")

                val response = RetrofitClient.api.getCategories(1)

                categoryList.clear()
                categoryList.addAll(response.data)
                categoryAdapter.updateList(categoryList)

            } catch (e: HttpException) {
                Log.e("API_ERROR", "HTTP ${e.code()} | ${e.response()?.errorBody()?.string()}")
            } catch (e: Exception) {
                Log.e("API_ERROR", e.toString())
            }
        }
    }



    // --- Load Menu Items for a Category ---
    private fun loadMenuForCategory(categoryId: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val request = MenuRequest(
                    restaurant_id = "1",
                    category_id = categoryId
                )
                val response = RetrofitClient.api.getMenuItems(request)

                menuItems.clear()
                menuItems.addAll(response.data)
                menuAdapter.updateItems(menuItems)

            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Failed to load menu", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        }
    }

}
