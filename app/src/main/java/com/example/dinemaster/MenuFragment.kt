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
import com.example.dinemaster.helper.PrefManager
import com.example.dinemaster.helper.RetrofitClient
import com.example.dinemaster.helper.showSnackbar
import com.example.dinemaster.model.*
import kotlinx.coroutines.launch

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

    private var vegType: String = ""
    private var selectedCategoryId: String = ""

    private var orderId: String = ""
    private var mode: String = MODE_VIEW   // ✅ FIXED (IMPORTANT)

    companion object {
        const val ARG_MODE = "type"
        const val MODE_VIEW = "ViewMode"
        const val MODE_EDIT = "EditMode"
        const val MODE_ADD = "AddMode"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? HomeActivity)?.setActiveTabByTag("Menu")
        (activity as? HomeActivity)?.updateHeader("Menu", true)

        initViews(view)
        handleArguments()
        setupRecycler()
        setupListeners()

        loadCategoriesFromApi()
    }

    private fun initViews(view: View) {
        rvCategories = view.findViewById(R.id.rvCategories)
        rvMenu = view.findViewById(R.id.rvMenu)
        etSearch = view.findViewById(R.id.etSearch)
        btnOrderSave = view.findViewById(R.id.btnOrderSave)
        chkVeg = view.findViewById(R.id.chkVeg)
        chkNonVeg = view.findViewById(R.id.chkNonVeg)
    }

    private fun handleArguments() {
        arguments?.let {
            mode = it.getString(ARG_MODE)?.ifBlank { MODE_VIEW } ?: MODE_VIEW
            orderId = it.getString("orderId") ?: ""
        }

        btnOrderSave.visibility =
            if (mode.equals(MODE_VIEW, true)) View.GONE else View.VISIBLE
    }

    private fun setupRecycler() {

        // Category
        rvCategories.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        categoryAdapter = CategoryAdapter(emptyList()) { category ->
            selectedCategoryId = category.category_id
            loadMenuForCategory(category.category_id)
        }

        rvCategories.adapter = categoryAdapter

        // Menu
        rvMenu.layoutManager = GridLayoutManager(requireContext(), 2)

        menuAdapter = MenuAdapter(
            items = mutableListOf(),
            mode = mode,   // ✅ PASS MODE
            onItemClick = { selectedItem ->
                val bundle = Bundle().apply {
                    putString("item_id", selectedItem.item_id)
                }

                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, MenuDetailFragment::class.java, bundle)
                    .addToBackStack(null)
                    .commit()
            },
            onQtyChange = { updatedItem ->
                val index = menuItems.indexOfFirst { it.item_id == updatedItem.item_id }
                if (index != -1) {
                    menuItems[index] = updatedItem
                }
            }
        )

        rvMenu.adapter = menuAdapter
    }

    private fun setupListeners() {

        etSearch.addTextChangedListener {
            categoryAdapter.filter(it.toString())
        }

        btnOrderSave.setOnClickListener {
            if (mode.equals(MODE_EDIT, true)) {
                addItemsToOrder()
            } else if (mode.equals(MODE_ADD, true)){
                saveOrder(mode)
            }
        }

        chkVeg.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                chkNonVeg.isChecked = false
                vegType = "VEG"
            } else vegType = ""
            reloadMenu()
        }

        chkNonVeg.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                chkVeg.isChecked = false
                vegType = "NON_VEG"
            } else vegType = ""
            reloadMenu()
        }
    }

    private fun reloadMenu() {
        if (selectedCategoryId.isNotEmpty()) {
            loadMenuForCategory(selectedCategoryId)
        }
    }

    // ✅ SAVE ORDER
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

    // ✅ ADD ITEMS API
    private fun addItemsToOrder() {

        val selectedItems = menuItems.filter { it.qty > 0 }

        if (selectedItems.isEmpty()) {
            Toast.makeText(requireContext(), "No items selected!", Toast.LENGTH_SHORT).show()
            return
        }

        val requestItems = selectedItems.map {
            AddItem(
                menu_id = it.item_id.toIntOrNull() ?: 0,
                quantity = it.qty,
                price = it.base_price.toDoubleOrNull() ?: 0.0
            )
        }

        val safeOrderId = orderId.toIntOrNull()
        if (safeOrderId == null) {
            Toast.makeText(requireContext(), "Invalid Order ID", Toast.LENGTH_SHORT).show()
            return
        }

        val request = AddOrderItemsRequest(
            order_id = safeOrderId,
            items = requestItems
        )

        lifecycleScope.launch {
            try {
                LoaderHelper.showLoader(requireContext())

                val response = RetrofitClient.api.addOrderItems(request)

                if (response.settings.success) {
                    showSnackbar(response.settings.message, false)

                    parentFragmentManager.popBackStack()
                    parentFragmentManager.setFragmentResult("order_updated", Bundle())
                }

            } catch (e: Exception) {
                Log.e("ADD_ITEM_ERROR", "Error", e)
                Toast.makeText(requireContext(), "Failed to add items", Toast.LENGTH_SHORT).show()
            } finally {
                LoaderHelper.hideLoader()
            }
        }
    }

    // ✅ APIs
    private fun loadCategoriesFromApi() {
        val restaurantId = PrefManager.getRestaurantId()

        lifecycleScope.launch {
            try {
                LoaderHelper.showLoader(requireContext())

                val response = RetrofitClient.api.getCategories(restaurantId.toInt())

                response.data?.let {
                    categoryList.clear()
                    categoryList.addAll(it)
                    categoryAdapter.updateList(categoryList)
                }

            } catch (e: Exception) {
                Log.e("CATEGORY_ERROR", e.message ?: "Error")
            } finally {
                LoaderHelper.hideLoader()
            }
        }
    }

    private fun loadMenuForCategory(categoryId: String) {
        val restaurantId = PrefManager.getRestaurantId()

        lifecycleScope.launch {
            try {
                LoaderHelper.showLoader(requireContext())

                val response = RetrofitClient.api.getMenuItems(
                    MenuRequest(
                        restaurant_id = restaurantId,
                        category_id = categoryId,
                        veg_type = vegType
                    )
                )

                if (response.settings.success) {
                    menuItems.clear()
                    menuItems.addAll(response.data)
                    menuAdapter.updateItems(menuItems)
                }

            } catch (e: Exception) {
                Log.e("MENU_ERROR", e.message ?: "Error")
            } finally {
                LoaderHelper.hideLoader()
            }
        }
    }
}