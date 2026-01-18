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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dinemaster.adapter.MenuAdapter
import com.example.dinemaster.model.MenuItem
import com.google.android.material.button.MaterialButton

class CreateOrderFragment : Fragment() {

    private lateinit var rvSelectedMenu: RecyclerView
    private lateinit var tvEmptyMessage: TextView
    private lateinit var tvSummary: TextView
    private lateinit var btnSaveOrder: MaterialButton
    private lateinit var btnAddMenu: MaterialButton
    private lateinit var etTableNo: EditText
    private lateinit var menuAdapter: MenuAdapter

    private val selectedItems = mutableListOf<MenuItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_create_order, container, false)
        (activity as? HomeActivity)?.setActiveTabByTag("Create Order")
        (activity as? HomeActivity)?.updateHeader("Create Order", false)

        rvSelectedMenu = view.findViewById(R.id.rvSelectedMenu)
        tvEmptyMessage = view.findViewById(R.id.tvEmptyMessage)
        tvSummary = view.findViewById(R.id.tvSummary)
        btnSaveOrder = view.findViewById(R.id.btnSaveOrder)
        btnAddMenu = view.findViewById(R.id.btnAddMenu)
        etTableNo = view.findViewById(R.id.etTableNo)

        val items = arguments?.getSerializable("order_items") as? ArrayList<MenuItem>
        items?.let { selectedItems.addAll(it) }

        rvSelectedMenu.layoutManager = GridLayoutManager(requireContext(), 2)
        val mode = arguments?.getString(MenuFragment.ARG_MODE)?.ifBlank { MenuFragment.MODE_EDIT }
            ?: MenuFragment.MODE_EDIT

//        menuAdapter = MenuAdapter(selectedItems, mode,
//            onItemClick = { item ->
//                Toast.makeText(requireContext(), "Clicked: ${item.name}", Toast.LENGTH_SHORT).show()
//            },
//            onQtyChange = { updateUI() }
//        )
        rvSelectedMenu.adapter = menuAdapter

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
                else -> {
                    showSnackbar("Order Saved!", false)
                }
            }
        }

        updateUI()
        return view
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
            val totalPrice = selectedItems.sumOf { it.qty * it.price }
            tvSummary.text = "$totalItems items | ₹%.2f".format(totalPrice)

            menuAdapter.notifyDataSetChanged()
        }
    }


}





