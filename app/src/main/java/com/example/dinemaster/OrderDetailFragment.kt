package com.example.dinemaster

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dinemaster.adapter.FoodItemAdapter
import com.example.dinemaster.model.FoodItem
import com.google.android.material.button.MaterialButton

class OrderDetailFragment : Fragment() {

    private lateinit var ivEdit: ImageView
    private lateinit var foodItems: List<FoodItem>

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_order_detail, container, false)
        (activity as? HomeActivity)?.updateHeader("Order Details", true)

        // Initialize views
        val tvOrderDetailHeader: TextView = view.findViewById(R.id.tvOrderDetailHeader)
        val tvTableNo: TextView = view.findViewById(R.id.tvTableNo)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
        val tvSubtotal: TextView = view.findViewById(R.id.tvSubtotal)
        val tvGST: TextView = view.findViewById(R.id.tvGST)
        val tvTotal: TextView = view.findViewById(R.id.tvTotal)
        val btnDownloadInvoice: Button = view.findViewById(R.id.btnDownloadInvoice)
        val rvFoodItems: RecyclerView = view.findViewById(R.id.rvFoodItems)
        val btnAddFoodItem: MaterialButton = view.findViewById(R.id.btnAddFoodItem)
        ivEdit = view.findViewById(R.id.ivEdit)

        // Setup RecyclerView
        rvFoodItems.layoutManager = LinearLayoutManager(requireContext())

        // Dummy data
        foodItems = listOf(
            FoodItem("Pizza", 2, 200.0),
            FoodItem("Burger", 1, 120.0),
            FoodItem("Pasta", 3, 180.0)
        )

        rvFoodItems.adapter = FoodItemAdapter(foodItems)

        val orderId = arguments?.getString("orderId") ?: ""
        val tableNo = arguments?.getString("tableNo") ?: ""
        val status = arguments?.getString("status") ?: ""

        // Set data to UI
        tvOrderDetailHeader.text = "Order ID: #$orderId"
        tvTableNo.text = "Table: $tableNo"
        tvStatus.text = status

        // Status color
        when (status.lowercase()) {
            "pending" -> tvStatus.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_orange_dark))
            "preparing" -> tvStatus.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_blue_dark))
            "completed" -> tvStatus.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_green_dark))
            else -> tvStatus.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.darker_gray))
        }

        // Calculate totals
        val subtotal = foodItems.sumOf { it.price * it.qty }
        val gst = subtotal * 0.05
        val total = subtotal + gst

        tvSubtotal.text = "Subtotal: ₹$subtotal"
        tvGST.text = "GST (5%): ₹$gst"
        tvTotal.text = "Total: ₹$total"

        // Show Invoice button only if Served
        if (tvStatus.text.toString().contains("Served")) {
            btnDownloadInvoice.visibility = View.VISIBLE
        }

        // Edit button click -> Show BottomSheet
        ivEdit.setOnClickListener {
            val bottomSheet = UpdateOrderBottomSheet(foodItems.toMutableList())
            bottomSheet.show(parentFragmentManager, "UpdateOrderBottomSheet")
        }
        btnAddFoodItem.setOnClickListener {
            val menuFragment = MenuFragment().apply {
                arguments = bundleOf(MenuFragment.ARG_MODE to MenuFragment.MODE_EDIT)
            }
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, menuFragment)
                .addToBackStack(null)
                .commit()
        }


        return view
    }
}




