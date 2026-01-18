package com.example.dinemaster
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dinemaster.adapter.OrderAdapter
import com.example.dinemaster.model.Order

class OrderListFragment : Fragment() {

    private lateinit var rvOrders: RecyclerView
    private lateinit var adapter: OrderAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_order, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? HomeActivity)?.setActiveTabByTag("Orders")
        (activity as? HomeActivity)?.updateHeader("Order List", false)

        rvOrders = view.findViewById(R.id.rvOrders)

        // Dummy data
        val orders = listOf(
            Order("123", "5", "2x Pizza, 1x Coke", "Pending"),
            Order("124", "2", "1x Burger, 2x Fries", "Served"),
            Order("125", "8", "3x Pasta", "Preparing"),
            Order("126", "4", "2x Sandwich, 1x Coffee", "Pending")
        )

        adapter = OrderAdapter(orders) { order ->
            openOrderDetail(order)
        }

        rvOrders.layoutManager = LinearLayoutManager(requireContext())
        rvOrders.adapter = adapter
    }

    private fun openOrderDetail(order: Order) {
        val fragment = OrderDetailFragment()
        val bundle = Bundle().apply {
            putString("orderId", order.orderId)
            putString("tableNo", order.tableNo)
            putString("details", order.details)
            putString("status", order.status)
        }
        fragment.arguments = bundle
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }
}


