package com.example.dinemaster
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.dinemaster.helper.LoaderHelper
import com.example.dinemaster.helper.PrefManager
import com.example.dinemaster.helper.RetrofitClient
import kotlinx.coroutines.launch

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    // ✅ User Info
    private lateinit var tvUsername: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvPhone: TextView
    private lateinit var tvUserRole: TextView
    private lateinit var tvStatus: TextView

    // ✅ Stats
    private lateinit var tvTodayOrders: TextView
    private lateinit var tvLast7DaysOrder: TextView
    private lateinit var tvTotalOrders: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? HomeActivity)?.setActiveTabByTag("Profile")
        (activity as? HomeActivity)?.updateHeader("Profile", false)

        initViews(view)
        callUserDetailsApi()

        // ✅ Logout
        view.findViewById<Button>(R.id.btnLogout).setOnClickListener {
            showLogoutDialog()
        }
    }

    // ✅ Initialize Views
    private fun initViews(view: View) {
        tvUsername = view.findViewById(R.id.tvUsername)
        tvEmail = view.findViewById(R.id.tvEmail)
        tvPhone = view.findViewById(R.id.tvPhone)
        tvUserRole = view.findViewById(R.id.tvUserRole)
        tvStatus = view.findViewById(R.id.tvStatus)

        tvTodayOrders = view.findViewById(R.id.tvTodayOrders)
        tvLast7DaysOrder = view.findViewById(R.id.tvLast7DaysOrder)
        tvTotalOrders = view.findViewById(R.id.tvTotalOrders)
    }

    // ✅ API Call
    private fun callUserDetailsApi() {

        val userId = PrefManager.getUserId()

        viewLifecycleOwner.lifecycleScope.launch {

            try {
                LoaderHelper.showLoader(requireContext())

                val response = RetrofitClient.api.getUserDetails(mapOf("id" to userId))

                if (response.settings.success) {

                    val data = response.data

                    // ✅ Set User Info
                    tvUsername.text = data.user_name
                    tvEmail.text = data.user_email
                    tvPhone.text = data.phone
                    tvUserRole.text = data.role_name
                    tvStatus.text = data.status

                    // ✅ Set Stats
                    tvTodayOrders.text = data.today_orders.toString()
                    tvLast7DaysOrder.text = data.last_7_days_orders.toString()
                    tvTotalOrders.text = data.total_orders.toString()

                } else {
                    Toast.makeText(
                        requireContext(),
                        response.settings.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } catch (e: Exception) {

                Log.e("ProfileAPI", "Error", e)

                Toast.makeText(
                    requireContext(),
                    "Failed to load profile",
                    Toast.LENGTH_SHORT
                ).show()

            } finally {
                LoaderHelper.hideLoader()
            }
        }
    }

    // ✅ Logout Dialog
    private fun showLogoutDialog() {

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Logout") { d, _ ->

                PrefManager.clear()

                val intent = Intent(requireContext(), LoginActivity::class.java)
                intent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

                startActivity(intent)
                d.dismiss()
            }
            .setNegativeButton("Cancel") { d, _ -> d.dismiss() }
            .create()

        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            .setTextColor(ContextCompat.getColor(requireContext(), R.color.color_primary))

        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            .setTextColor(ContextCompat.getColor(requireContext(), R.color.dark_gray))
    }
}