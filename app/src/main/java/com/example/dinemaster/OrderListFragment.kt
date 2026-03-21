package com.example.dinemaster

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dinemaster.adapter.OrderAdapter
import com.example.dinemaster.helper.LoaderHelper
import com.example.dinemaster.helper.RetrofitClient
import com.example.dinemaster.model.OrderData
import kotlinx.coroutines.launch

class OrderListFragment : Fragment() {

    private lateinit var rvOrders: RecyclerView
    private lateinit var adapter: OrderAdapter
    private val orderList = mutableListOf<OrderData>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_order, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? HomeActivity)?.setActiveTabByTag("Orders")
        (activity as? HomeActivity)?.updateHeader("Order List", false)

        rvOrders = view.findViewById(R.id.rvOrders)

        adapter = OrderAdapter(orderList) { order ->
            openOrderDetail(order)
        }

        rvOrders.layoutManager = LinearLayoutManager(requireContext())
        rvOrders.adapter = adapter

        callOrdersApi()
    }

    private fun callOrdersApi() {

        viewLifecycleOwner.lifecycleScope.launch {

            try {

                LoaderHelper.showLoader(requireContext())

                val response =
                    RetrofitClient.api.getOrders(mapOf("restaurant_id" to "1"))

                if (response.settings.success) {

                    orderList.clear()

                    response.data.forEach { apiOrder ->
                        orderList.add(apiOrder)
                    }

                    adapter.notifyDataSetChanged()

                } else {

                    Toast.makeText(
                        requireContext(),
                        response.settings.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } catch (e: Exception) {

                Log.e("OrdersAPI", "Error loading orders", e)

                Toast.makeText(
                    requireContext(),
                    "Failed to load orders",
                    Toast.LENGTH_SHORT
                ).show()

            } finally {

                LoaderHelper.hideLoader()

            }
        }
    }

    private fun openOrderDetail(order: OrderData) {

        val fragment = OrderDetailFragment()

        val bundle = Bundle().apply {
            putString("orderId", order.order_id)
            putString("tableNo", order.table_id)
            putString("status", order.status)
        }

        fragment.arguments = bundle

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }
}