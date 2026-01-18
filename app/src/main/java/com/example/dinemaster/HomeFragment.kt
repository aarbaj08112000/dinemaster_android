package com.example.dinemaster
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
class HomeFragment : Fragment(R.layout.fragment_home) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Update header in activity
        (activity as? HomeActivity)?.setActiveTabByTag("Home")
        (activity as? HomeActivity)?.updateHeader("Home", false)

        // Quick Actions Clicks

        // Menu
        val llMenu = view.findViewById<LinearLayout>(R.id.llMenu)
        llMenu.setOnClickListener {
            val menuFragment = MenuFragment().apply {
                arguments = bundleOf(MenuFragment.ARG_MODE to MenuFragment.MODE_VIEW)
            }
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, menuFragment)
                .addToBackStack(null)
                .commit()
        }

        // New Order
        val llNewOrder = view.findViewById<LinearLayout>(R.id.llNewOrder)
        llNewOrder.setOnClickListener {
            val newOrderFragment = CreateOrderFragment() // replace with your fragment
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, newOrderFragment)
                .addToBackStack(null)
                .commit()
        }

        // Scan QR
        val llScanQR = view.findViewById<LinearLayout>(R.id.llScanQR)
        llScanQR.setOnClickListener {
            QrRatingBottomSheet().show(parentFragmentManager, "ScanQRCode")
        }

        // Orders
        val llOrdersMenu = view.findViewById<LinearLayout>(R.id.llOrdersMenu)
        llOrdersMenu.setOnClickListener {
            val orderListFragment = OrderListFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, orderListFragment)
                .addToBackStack(null)
                .commit()
        }

        // Profile
        val llProfileAction = view.findViewById<LinearLayout>(R.id.llProfileAction)
        llProfileAction.setOnClickListener {
            val profileFragment = ProfileFragment() // replace with your fragment
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, profileFragment)
                .addToBackStack(null)
                .commit()
        }

        // Hotel Info
        val llHotelInfo = view.findViewById<LinearLayout>(R.id.llHotelInfo)
        llHotelInfo.setOnClickListener {
            RestaurantInfoBottomSheet().show(parentFragmentManager, "RestaurantInfo")
        }


    }
}




