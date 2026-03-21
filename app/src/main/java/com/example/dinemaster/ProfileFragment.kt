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
import com.example.dinemaster.helper.PrefManager
import com.example.dinemaster.helper.RetrofitClient
import kotlinx.coroutines.launch

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private lateinit var tvName: TextView
    private lateinit var tvRole: TextView
    private lateinit var tvLocation: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? HomeActivity)?.setActiveTabByTag("Profile")
        (activity as? HomeActivity)?.updateHeader("Profile", false)

        tvName = view.findViewById(R.id.tvName)
        tvRole = view.findViewById(R.id.tvRole)
        tvLocation = view.findViewById(R.id.tvLocation)

        callUserDetailsApi()

        // Logout button
        view.findViewById<Button>(R.id.btnLogout).setOnClickListener {
            showLogoutDialog()
        }
    }

    private fun callUserDetailsApi() {

        val userId = PrefManager.getUserId()

        viewLifecycleOwner.lifecycleScope.launch {

            try {

                val response = RetrofitClient.api.getUserDetails(mapOf("id" to userId))

                if (response.settings.success) {

                    val data = response.data

                    tvName.text = data.user_name
                    tvRole.text = "Role ID: ${data.user_role}"
                    tvLocation.text = "📞 ${data.phone}"

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
            }
        }
    }

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
