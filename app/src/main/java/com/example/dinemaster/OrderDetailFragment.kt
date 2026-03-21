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

    private lateinit var adapter: FoodItemAdapter
    private val foodItems = mutableListOf<OrderItemData>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_order_detail, container, false)

        (activity as? HomeActivity)?.updateHeader("Order Details", true)

        tvOrderDetailHeader = view.findViewById(R.id.tvOrderDetailHeader)
        tvTableNo = view.findViewById(R.id.tvTableNo)
        tvStatus = view.findViewById(R.id.tvStatus)
        tvSubtotal = view.findViewById(R.id.tvSubtotal)
        tvGST = view.findViewById(R.id.tvGST)
        tvTotal = view.findViewById(R.id.tvTotal)

        ivEdit = view.findViewById(R.id.ivEdit)

        rvFoodItems = view.findViewById(R.id.rvFoodItems)
        rvFoodItems.layoutManager = LinearLayoutManager(requireContext())

        adapter = FoodItemAdapter(foodItems)
        rvFoodItems.adapter = adapter

        val orderId = arguments?.getString("orderId") ?: ""

        if (orderId.isNotEmpty()) {
            loadOrderDetails(orderId)
        }

        return view
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

                    tvOrderDetailHeader.text = "Order #${order.order_id}"
                    tvTableNo.text = "Table: ${order.table_id}"
                    tvStatus.text = order.status

                    setStatusColor(order.status)

                    foodItems.clear()
                    foodItems.addAll(order.items)
                    adapter.notifyDataSetChanged()

//                    tvSubtotal.text = "Subtotal: ₹${order.total_payable}"
//                    tvGST.text = "GST: ₹${order.tax_amount}"
                    tvTotal.text = "Total: ₹${order.total_payable}"

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

    private fun setStatusColor(status: String) {

        when (status.uppercase()) {

            "PLACED" -> tvStatus.setTextColor(
                ContextCompat.getColor(requireContext(), android.R.color.holo_orange_dark)

            )

            "PREPARING" -> tvStatus.setTextColor(
                ContextCompat.getColor(requireContext(), android.R.color.holo_blue_dark)
            )

            "SERVED" -> tvStatus.setTextColor(
                ContextCompat.getColor(requireContext(), android.R.color.holo_green_dark)
            )

            else -> tvStatus.setTextColor(
                ContextCompat.getColor(requireContext(), android.R.color.darker_gray)
            )
        }
    }
}



