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
import com.example.dinemaster.helper.LoaderHelper
import com.example.dinemaster.helper.RetrofitClient
import com.example.dinemaster.model.Category
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

    var restaurant_id: Int = 1

    private val categoryList = mutableListOf<Category>()
    private val menuItems = mutableListOf<MenuItemApi>()

    private var vegType: String = ""
    private var selectedCategoryId: String = ""

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

        // ---------------------------
        // Category RecyclerView
        // ---------------------------
        rvCategories.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        categoryAdapter = CategoryAdapter(emptyList()) { category ->
            selectedCategoryId = category.category_id
            loadMenuForCategory(category.category_id)
        }

        rvCategories.adapter = categoryAdapter

        // ---------------------------
        // Menu RecyclerView
        // ---------------------------
        val mode = arguments?.getString(ARG_MODE)?.ifBlank { MODE_VIEW } ?: MODE_VIEW

        rvMenu.layoutManager = GridLayoutManager(requireContext(), 2)

        menuAdapter = MenuAdapter(
            mutableListOf(),
            mode,
            onItemClick = { selectedItem ->

//                val bundle = Bundle().apply {
//                    putString("name", selectedItem.name)
//
//                }
//
//                parentFragmentManager.beginTransaction()
//                    .replace(R.id.fragment_container, MenuDetailFragment::class.java, bundle)
//                    .addToBackStack(null)
//                    .commit()
                val bundle = Bundle().apply {
                    putString("item_id", selectedItem.item_id)
                }

                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, MenuDetailFragment::class.java, bundle)
                    .addToBackStack(null)
                    .commit()
            },
            onQtyChange = {
                // cart update logic if needed
            }
        )

        rvMenu.adapter = menuAdapter

        // ---------------------------
        // Search Category Filter
        // ---------------------------
        etSearch.addTextChangedListener { query ->
            categoryAdapter.filter(query.toString())
        }

        // ---------------------------
        // Save Order Button
        // ---------------------------
        btnOrderSave.visibility =
            if (mode.equals(MODE_EDIT, ignoreCase = true)) View.VISIBLE else View.GONE

        btnOrderSave.setOnClickListener {
            saveOrder(mode)
        }

        // ---------------------------
        // Veg / Non Veg Filter
        // ---------------------------

        chkVeg.setOnCheckedChangeListener { _, isChecked ->

            if (isChecked) {
                chkNonVeg.isChecked = false
                vegType = "VEG"
            } else {
                vegType = ""
            }

            if (selectedCategoryId.isNotEmpty()) {
                loadMenuForCategory(selectedCategoryId)
            }
        }

        chkNonVeg.setOnCheckedChangeListener { _, isChecked ->

            if (isChecked) {
                chkVeg.isChecked = false
                vegType = "NON_VEG"
            } else {
                vegType = ""
            }

            if (selectedCategoryId.isNotEmpty()) {
                loadMenuForCategory(selectedCategoryId)
            }
        }

        // ---------------------------
        // Load Categories
        // ---------------------------
        loadCategoriesFromApi()
    }

    // ---------------------------
    // Save Order
    // ---------------------------

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

    // ---------------------------
    // Load Categories API
    // ---------------------------

    private fun loadCategoriesFromApi() {

        viewLifecycleOwner.lifecycleScope.launch {

            LoaderHelper.showLoader(requireContext())

            try {

                Log.d("API_REQUEST", "Calling categories with id=$restaurant_id")

                val response = RetrofitClient.api.getCategories(restaurant_id)

                if (response.data != null) {

                    categoryList.clear()
                    categoryList.addAll(response.data)

                    categoryAdapter.updateList(categoryList)

                } else {

                    Log.e("API_ERROR", "Category data null")

                }

            } catch (e: HttpException) {

                val errorBody = e.response()?.errorBody()?.string()
                Log.e("API_ERROR", "HTTP ${e.code()} | $errorBody")

            } catch (e: Exception) {

                Log.e("API_ERROR", e.message ?: "Unknown Error")

            } finally {

                LoaderHelper.hideLoader()

            }
        }
    }

    // ---------------------------
    // Load Menu API
    // ---------------------------

    private fun loadMenuForCategory(categoryId: String) {

        selectedCategoryId = categoryId

        viewLifecycleOwner.lifecycleScope.launch {

            LoaderHelper.showLoader(requireContext())

            try {

                val request = MenuRequest(
                    restaurant_id = restaurant_id.toString(),
                    category_id = categoryId,
                    veg_type = vegType
                )

                val response = RetrofitClient.api.getMenuItems(request)
                if (response.settings.success) {
                    menuItems.clear()
                    menuItems.addAll(response.data)
                    menuAdapter.updateItems(menuItems)

                } else {
                    Toast.makeText(
                        requireContext(),
                        response.settings.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } catch (e: Exception) {

                Toast.makeText(
                    requireContext(),
                    "Failed to load menu",
                    Toast.LENGTH_SHORT
                ).show()

                Log.e("MENU_API_ERROR", e.message ?: "Unknown error")

            } finally {

                LoaderHelper.hideLoader()

            }
        }
    }
}


