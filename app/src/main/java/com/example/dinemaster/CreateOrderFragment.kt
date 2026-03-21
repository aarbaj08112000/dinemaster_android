package com.example.dinemaster
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dinemaster.adapter.MenuAdapter
import com.example.dinemaster.helper.LoaderHelper
import com.example.dinemaster.helper.PrefManager
import com.example.dinemaster.helper.RetrofitClient
import com.example.dinemaster.helper.showSnackbar
import com.example.dinemaster.model.Item
import com.example.dinemaster.model.MenuItem
import com.example.dinemaster.model.MenuItemApi
import com.example.dinemaster.model.NewOrderRequest
import com.example.dinemaster.model.TableData
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CreateOrderFragment : Fragment() {

    private lateinit var rvSelectedMenu: RecyclerView
    private lateinit var tvEmptyMessage: TextView
    private lateinit var tvSummary: TextView
    private lateinit var btnSaveOrder: MaterialButton
    private lateinit var btnAddMenu: MaterialButton
    private lateinit var actTable: AutoCompleteTextView

    private lateinit var menuAdapter: MenuAdapter
    private val selectedItems = mutableListOf<MenuItemApi>()

    // ✅ Table Data
    private var tableList: List<TableData> = listOf()
    private var selectedTableId: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_create_order, container, false)

        (activity as? HomeActivity)?.setActiveTabByTag("Create Order")
        (activity as? HomeActivity)?.updateHeader("Create Order", false)

        initViews(view)
        setupRecycler()
        loadDataFromArguments()
        setupListeners()

        callTableListApi() // 🔥 load tables

        updateUI()

        return view
    }

    private fun initViews(view: View) {
        rvSelectedMenu = view.findViewById(R.id.rvSelectedMenu)
        tvEmptyMessage = view.findViewById(R.id.tvEmptyMessage)
        tvSummary = view.findViewById(R.id.tvSummary)
        btnSaveOrder = view.findViewById(R.id.btnSaveOrder)
        btnAddMenu = view.findViewById(R.id.btnAddMenu)
        actTable = view.findViewById(R.id.actTable)
    }

    private fun setupRecycler() {
        rvSelectedMenu.layoutManager = GridLayoutManager(requireContext(), 2)

        val mode = arguments?.getString(MenuFragment.ARG_MODE)
            ?.ifBlank { MenuFragment.MODE_EDIT }
            ?: MenuFragment.MODE_EDIT

        menuAdapter = MenuAdapter(
            selectedItems,
            mode,
            onItemClick = { item ->
                Toast.makeText(requireContext(), "Clicked: ${item.name}", Toast.LENGTH_SHORT).show()
            },
            onQtyChange = { updateUI() }
        )

        rvSelectedMenu.adapter = menuAdapter
    }

    private fun loadDataFromArguments() {
        val items = arguments?.getSerializable("order_items") as? ArrayList<MenuItemApi>
        items?.let { selectedItems.addAll(it) }
    }

    private fun setupListeners() {

        btnAddMenu.setOnClickListener {
            val menuFragment = MenuFragment().apply {
                arguments = bundleOf(
                    MenuFragment.ARG_MODE to MenuFragment.MODE_EDIT,
                    "existing_items" to ArrayList(selectedItems)
                )
            }

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, menuFragment)
                .addToBackStack(null)
                .commit()
        }

        btnSaveOrder.setOnClickListener {

            when {
                selectedTableId.isEmpty() -> showSnackbar("Select table", true)
                selectedItems.isEmpty() -> showSnackbar("Add menu items first", true)
                else -> callCreateOrderApi(selectedTableId)
            }
        }
    }

    // ✅ TABLE API
    private fun callTableListApi() {

        val restaurantId = PrefManager.getRestaurantId()

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.getTables(
                    mapOf("restaurant_id" to restaurantId)
                )

                if (response.settings.success) {

                    tableList = response.data

                    // 🔥 Set dropdown values
                    val tableNames = tableList.map { it.table_name }

                    val adapter = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_dropdown_item_1line,
                        tableNames
                    )

                    actTable.setAdapter(adapter)

                    // 🔥 Handle selection
                    actTable.setOnItemClickListener { _, _, position, _ ->
                        val selectedTable = tableList[position]
                        selectedTableId = selectedTable.table_id
                    }

                } else {
                    showSnackbar(response.settings.message, true)
                }

            } catch (e: Exception) {
                showSnackbar("Error loading tables", true)
            }
        }
    }

    private fun updateUI() {
        if (selectedItems.isEmpty()) {
            rvSelectedMenu.visibility = View.GONE
            tvEmptyMessage.visibility = View.VISIBLE
            tvSummary.text = "0 items | ₹0.00"
        } else {
            rvSelectedMenu.visibility = View.VISIBLE
            tvEmptyMessage.visibility = View.GONE

            val totalItems = selectedItems.sumOf { it.qty }
            val totalPrice = selectedItems.sumOf {
                it.qty.toDouble() * it.base_price.toDouble()
            }

            tvSummary.text = "$totalItems items | ₹%.2f".format(totalPrice)

            menuAdapter.notifyDataSetChanged()
        }
    }

    private fun callCreateOrderApi(tableId: String) {

        val restaurantId = PrefManager.getRestaurantId()

        LoaderHelper.showLoader(requireContext())

        lifecycleScope.launch {
            try {

                val itemList = selectedItems.map { item ->
                    Item(
                        item_id = item.item_id.toString(),
                        item_name = item.name,
                        unit_price = item.base_price.toString(),
                        quantity = item.qty.toString(),
                        addons = null
                    )
                }

                val request = NewOrderRequest(
                    restaurant_id = restaurantId,
                    table_no = tableId, // ✅ now from dropdown
                    items = itemList
                )

                val response = RetrofitClient.api.createOrder(request)

                if (response.isSuccessful && response.body()?.settings?.success == true) {

                    val orderId = response.body()?.data?.order_id
                    showSnackbar("Order Created! ID: $orderId", false)

                    selectedItems.clear()
                    updateUI()

                    delay(1000)
                    parentFragmentManager.popBackStack()

                } else {
                    showSnackbar("Failed to create order", true)
                }

            } catch (e: Exception) {
                showSnackbar("Error: ${e.message}", true)
            } finally {
                LoaderHelper.hideLoader()
            }
        }
    }


}