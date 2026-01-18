package com.example.dinemaster

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dinemaster.adapter.UpdateOrderAdapter
import com.example.dinemaster.model.FoodItem
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class UpdateOrderBottomSheet(
    private val foodItems: MutableList<FoodItem>   // make it mutable
) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.bottomsheet_update_order, container, false)

        val rvOrderItems = view.findViewById<RecyclerView>(R.id.rvOrderItems)
        val btnConfirmOrder = view.findViewById<Button>(R.id.btnConfirmOrder)
        val tvClose = view.findViewById<TextView>(R.id.tvClose)

        rvOrderItems.layoutManager = LinearLayoutManager(requireContext())
        rvOrderItems.adapter = UpdateOrderAdapter(foodItems)

        // Close button
        tvClose.setOnClickListener { dismiss() }

        // Confirm button
        btnConfirmOrder.setOnClickListener {
            Toast.makeText(requireContext(), "Order Updated!", Toast.LENGTH_SHORT).show()
            dismiss()
        }

        return view
    }
}


