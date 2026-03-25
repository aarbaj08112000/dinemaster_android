package com.example.dinemaster

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dinemaster.adapter.UpdateOrderAdapter
import com.example.dinemaster.helper.LoaderHelper
import com.example.dinemaster.helper.RetrofitClient
import com.example.dinemaster.helper.showSnackbar
import com.example.dinemaster.model.DeleteItemRequest
import com.example.dinemaster.model.FoodItem
import com.example.dinemaster.model.OrderItemData
import com.example.dinemaster.model.UpdateItem
import com.example.dinemaster.model.UpdateOrderRequest
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch


class UpdateOrderBottomSheet(
    private val orderId: Int,
    private val foodItems: MutableList<OrderItemData>
) : BottomSheetDialogFragment() {

    private lateinit var rvOrderItems: RecyclerView
    private lateinit var adapter: UpdateOrderAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.bottomsheet_update_order, container, false)

        rvOrderItems = view.findViewById(R.id.rvOrderItems)
        val btnConfirmOrder = view.findViewById<Button>(R.id.btnConfirmOrder)
        val tvClose = view.findViewById<TextView>(R.id.tvClose)

        rvOrderItems.layoutManager = LinearLayoutManager(requireContext())

        // ✅ PASS DELETE CALLBACK
        adapter = UpdateOrderAdapter(foodItems) { item, position ->
            callDeleteItemAPI(item, position)
        }

        rvOrderItems.adapter = adapter

        tvClose.setOnClickListener { dismiss() }

        btnConfirmOrder.setOnClickListener {
            callUpdateOrderAPI()
        }

        return view
    }

    // ---------------------------
    // ✅ UPDATE ORDER API
    // ---------------------------
    private fun callUpdateOrderAPI() {

        val requestItems = mutableListOf<UpdateItem>()

        for (item in foodItems) {

            val qty = item.quantity.toDoubleOrNull()?.toInt() ?: 0
            val price = item.unit_price.toDoubleOrNull() ?: 0.0

            if (qty > 0) {
                requestItems.add(
                    UpdateItem(
                        order_item_id = item.order_item_id.toIntOrNull(),
                        quantity = qty,
                        price = price
                    )
                )
            } else {
                requestItems.add(
                    UpdateItem(
                        menu_id = item.item_id.toIntOrNull(),
                        quantity = 0
                    )
                )
            }
        }

        val request = UpdateOrderRequest(
            order_id = orderId,
            items = requestItems
        )

        lifecycleScope.launch {
            try {

                LoaderHelper.showLoader(requireContext())

                val response = RetrofitClient.api.updateOrderItems(request)

                if (!isAdded) return@launch

                if (response.settings.success) {

                    Toast.makeText(requireContext(), response.settings.message, Toast.LENGTH_SHORT).show()
//                    parentFragmentManager.popBackStack()
                    parentFragmentManager.setFragmentResult("order_updated", Bundle())

                    view?.post { dismiss() }
                }

            } catch (e: Exception) {

                Log.e("UPDATE_ERROR", "Error: ", e)

                Toast.makeText(requireContext(), "Update failed", Toast.LENGTH_SHORT).show()

            } finally {
                LoaderHelper.hideLoader()
            }
        }
    }

    // ---------------------------
    // ✅ DELETE ITEM API
    // ---------------------------
    private fun callDeleteItemAPI(item: OrderItemData, position: Int) {

        val request = DeleteItemRequest(
            order_id = orderId.toString(),
            item_id = item.order_item_id.toString()   // ⚠️ confirm field
        )

        lifecycleScope.launch {
            try {

                LoaderHelper.showLoader(requireContext())

                val response = RetrofitClient.api.deleteOrderItem(request)

                if (!isAdded) return@launch

                if (response.settings.success) {

                    Toast.makeText(requireContext(), response.settings.message, Toast.LENGTH_SHORT).show()

                    // ✅ REMOVE FROM LIST
                    foodItems.removeAt(position)
                    adapter.notifyItemRemoved(position)

                    // ✅ notify parent fragment
//                    parentFragmentManager.popBackStack()
                    parentFragmentManager.setFragmentResult("order_updated", Bundle())
                }

            } catch (e: Exception) {

                Log.e("DELETE_ERROR", "Error: ", e)

                Toast.makeText(requireContext(), "Delete failed", Toast.LENGTH_SHORT).show()

            } finally {
                LoaderHelper.hideLoader()
            }
        }
    }
}
