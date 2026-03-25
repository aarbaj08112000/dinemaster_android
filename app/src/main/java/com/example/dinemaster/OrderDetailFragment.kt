package com.example.dinemaster

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dinemaster.adapter.FoodItemAdapter
import com.example.dinemaster.helper.LoaderHelper
import com.example.dinemaster.helper.RetrofitClient
import com.example.dinemaster.model.FoodItem
import com.example.dinemaster.model.OrderItemData
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch

class OrderDetailFragment : Fragment() {

    private lateinit var ivEdit: ImageView
    private lateinit var rvFoodItems: RecyclerView

    private lateinit var tvOrderDetailHeader: TextView
    private lateinit var tvTableNo: TextView
    private lateinit var tvStatus: TextView
    private lateinit var tvSubtotal: TextView
    private lateinit var tvGST: TextView
    private lateinit var tvTotal: TextView
    private lateinit var btnAddFoodItem: MaterialButton

    private lateinit var adapter: FoodItemAdapter
    private val foodItems = mutableListOf<OrderItemData>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_order_detail, container, false)

        (activity as? HomeActivity)?.updateHeader("Order Details", true)

        initViews(view)
//        setupRecycler(view)

        val orderId = arguments?.getString("orderId") ?: ""
        if (orderId.isNotEmpty()) {
            loadOrderDetails(orderId)
        }

        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecycler(view)

        parentFragmentManager.setFragmentResultListener(
            "order_updated",
            viewLifecycleOwner
        ) { _, _ ->

            val orderId = arguments?.getString("orderId") ?: ""

            if (orderId.isNotEmpty()) {
                loadOrderDetails(orderId)   // 🔥 REFRESH API CALL
            }
        }

    }

    private fun initViews(view: View) {
        val orderId = arguments?.getString("orderId") ?: ""
        tvOrderDetailHeader = view.findViewById(R.id.tvOrderDetailHeader)
        tvTableNo = view.findViewById(R.id.tvTableNo)
        tvStatus = view.findViewById(R.id.tvStatus)
        tvSubtotal = view.findViewById(R.id.tvSubtotal)
        tvGST = view.findViewById(R.id.tvGST)
        tvTotal = view.findViewById(R.id.tvTotal)

        ivEdit = view.findViewById(R.id.ivEdit)
        btnAddFoodItem = view.findViewById(R.id.btnAddFoodItem)

        ivEdit.setOnClickListener {

            val bottomSheet = UpdateOrderBottomSheet(
                orderId = orderId.toInt(),
                foodItems = foodItems
            )
            bottomSheet.show(parentFragmentManager, "UpdateOrder")
        }

        btnAddFoodItem.setOnClickListener {
            val menuFragment = MenuFragment().apply {
                arguments = bundleOf(
                    MenuFragment.ARG_MODE to MenuFragment.MODE_EDIT,
                    "orderId" to orderId   // ✅ pass orderId
                )
            }

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, menuFragment)
                .addToBackStack(null)
                .commit()
        }
    }

    private fun setupRecycler(view: View) {

        rvFoodItems = view.findViewById(R.id.rvFoodItems)

        rvFoodItems.layoutManager = LinearLayoutManager(requireContext())

        adapter = FoodItemAdapter()
        rvFoodItems.adapter = adapter
    }

    private fun loadOrderDetails(orderId: String) {

        lifecycleScope.launch {
            try {
                LoaderHelper.showLoader(requireContext())

                val response = RetrofitClient.api.getOrderDetails(
                    mapOf("id" to orderId)
                )

                if (response.settings.success) {

                    val order = response.data

                    // 🔹 Header
                    tvOrderDetailHeader.text = "Order #${order.order_id}"
                    tvTableNo.text = "Table: ${order.table_id}"
                    tvStatus.text = order.status
                    setStatusColor(order.status)

                    // 🔥 Handle duplicate items (VERY IMPORTANT)
//                    val uniqueItems = order.items.distinctBy { it.order_item_id }

                    foodItems.clear()
//                    foodItems.addAll(uniqueItems)
                    foodItems.addAll(order.items)

                    adapter.submitList(foodItems.toList())

                    // 🔥 Amounts
                    val subtotal = order.subtotal_amount.toDoubleOrNull() ?: 0.0
                    val gstPercent = order.gst_percentage.toDoubleOrNull() ?: 0.0
                    val gstAmount = order.tax_amount.toDoubleOrNull() ?: 0.0
                    val total = order.total_payable.toDoubleOrNull() ?: 0.0

                    tvSubtotal.text = "Subtotal: ₹${formatAmount(subtotal)}"

                    if (order.gst_applicable == "yes") {
                        tvGST.text = "GST (${gstPercent}%): ₹${formatAmount(gstAmount)}"
                        tvGST.visibility = View.VISIBLE
                    } else {
                        tvGST.visibility = View.GONE
                    }

                    tvTotal.text = "Total: ₹${formatAmount(total)}"
                }

            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(),
                    "Failed to load order details",
                    Toast.LENGTH_SHORT
                ).show()
            } finally {
                LoaderHelper.hideLoader()
            }
        }
    }

    private fun formatAmount(value: Double): String {
        return String.format("%.2f", value)
    }

    private fun setStatusColor(status: String) {
        when (status.uppercase()) {
            "PENDING", "PLACED" -> tvStatus.setTextColor(
                ContextCompat.getColor(requireContext(), android.R.color.holo_orange_dark)
            )
            "PREPARING" -> tvStatus.setTextColor(
                ContextCompat.getColor(requireContext(), android.R.color.holo_blue_dark)
            )
            "SERVED", "COMPLETED" -> tvStatus.setTextColor(
                ContextCompat.getColor(requireContext(), android.R.color.holo_green_dark)
            )
            else -> tvStatus.setTextColor(
                ContextCompat.getColor(requireContext(), android.R.color.darker_gray)
            )
        }
    }
}



