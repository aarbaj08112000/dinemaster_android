package com.example.dinemaster
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CreateOrderFragment : Fragment() {

    private lateinit var rvSelectedMenu: RecyclerView
    private lateinit var tvEmptyMessage: TextView
    private lateinit var tvSummary: TextView
    private lateinit var btnSaveOrder: MaterialButton
    private lateinit var btnAddMenu: MaterialButton
    private lateinit var etTableNo: EditText

    private lateinit var menuAdapter: MenuAdapter
    private val selectedItems = mutableListOf<MenuItemApi>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_create_order, container, false)

        (activity as? HomeActivity)?.setActiveTabByTag("Create Order")
        (activity as? HomeActivity)?.updateHeader("Create Order", false)

        initViews(view)
        setupRecycler()
        loadDataFromArguments()
        setupListeners()

        updateUI()

        return view
    }

    // ✅ Initialize Views
    private fun initViews(view: View) {
        rvSelectedMenu = view.findViewById(R.id.rvSelectedMenu)
        tvEmptyMessage = view.findViewById(R.id.tvEmptyMessage)
        tvSummary = view.findViewById(R.id.tvSummary)
        btnSaveOrder = view.findViewById(R.id.btnSaveOrder)
        btnAddMenu = view.findViewById(R.id.btnAddMenu)
        etTableNo = view.findViewById(R.id.etTableNo)
    }

    // ✅ Setup RecyclerView
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
            onQtyChange = {
                updateUI()
            }
        )

        rvSelectedMenu.adapter = menuAdapter
    }

    // ✅ Load Data
    private fun loadDataFromArguments() {
        val items = arguments?.getSerializable("order_items") as? ArrayList<MenuItemApi>
        items?.let { selectedItems.addAll(it) }
    }

    // ✅ Click Listeners
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
            val tableNo = etTableNo.text.toString().trim()

            when {
                tableNo.isEmpty() -> showSnackbar("Enter table number", true)
                !tableNo.all { it.isDigit() } -> showSnackbar("Table number must be numeric", true)
                selectedItems.isEmpty() -> showSnackbar("Add menu items first", true)
                else -> callCreateOrderApi(tableNo)
            }
        }
    }

    // ✅ Update UI
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

    // ✅ API CALL
    private fun callCreateOrderApi(tableNo: String) {
        val restaurantId = PrefManager.getRestaurantId()

        LoaderHelper.showLoader(requireContext())

        lifecycleScope.launch {
            try {

                // 🔥 Convert items
                val itemList = selectedItems.map { item ->
                    Item(
                        item_id = item.item_id.toString(),
                        item_name = item.name,
                        unit_price = item.base_price.toString(),
                        quantity = item.qty.toString(),
                        addons = null // 👉 update if you have addons
                    )
                }

                val request = NewOrderRequest(
                    restaurant_id = restaurantId, // 👉 make dynamic later
                    table_no = tableNo,
                    items = itemList
                )

                val response = RetrofitClient.api.createOrder(request)

                if (response.isSuccessful && response.body()?.settings?.success == true) {

                    val orderId = response.body()?.data?.order_id

                    showSnackbar("Order Created! ID: $orderId", false)

                    // ✅ Clear data
                    selectedItems.clear()
                    updateUI()

                    // ✅ Optional navigation
                    delay(1000)
                    parentFragmentManager.popBackStack()

                } else {
                    showSnackbar(
                        response.body()?.settings?.message ?: "Failed to create order",
                        true
                    )
                }

            } catch (e: Exception) {
                showSnackbar("Error: ${e.message}", true)
            } finally {
                LoaderHelper.hideLoader()
            }
        }
    }


}
