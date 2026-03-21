package com.example.dinemaster

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import android.widget.FrameLayout
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import coil.load
import com.example.dinemaster.helper.LoaderHelper
import com.example.dinemaster.helper.PrefManager
import com.example.dinemaster.helper.RetrofitClient
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class RestaurantInfoBottomSheet : BottomSheetDialogFragment() {

    private lateinit var tvName: TextView
    private lateinit var tvInfo: TextView
    private lateinit var tvContact: TextView
    private lateinit var ivLogo: ImageView
    private lateinit var tvClose: TextView

    override fun getTheme(): Int =
        com.google.android.material.R.style.Theme_Material3_Light_BottomSheetDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottomsheet_restaurant_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvName = view.findViewById(R.id.tvRestaurantName)
        tvInfo = view.findViewById(R.id.tvRestaurantInfo)
        tvContact = view.findViewById(R.id.tvContact)
        ivLogo = view.findViewById(R.id.ivRestaurantLogo)
        tvClose = view.findViewById(R.id.tvClose)

        tvClose.setOnClickListener { dismiss() }

        callRestaurantApi()
    }

    private fun callRestaurantApi() {

        LoaderHelper.showLoader(requireContext())

        viewLifecycleOwner.lifecycleScope.launch {

            try {

                // 🔹 Debug token from PrefManager
                val token = PrefManager.getToken()
                Log.d("RestaurantAPI", "Saved Token: $token")

                val response = RetrofitClient.api.getRestaurantDetails(
                    mapOf("id" to "1")
                )

                Log.d("RestaurantAPI", "Response: $response")

                if (response.settings.success) {

                    val data = response.data

                    tvName.text = data.name

                    tvInfo.text =
                        "${data.legal_name}\nGSTIN: ${data.gstin}"

                    tvContact.text =
                        "📍 ${data.address_line1}, ${data.city}, ${data.state}\n" +
                                "📞 ${data.contact_phone}\n" +
                                "✉ ${data.contact_email}"

                    ivLogo.load(data.logo_url) {
                        placeholder(R.drawable.food_placeholder)
                        error(R.drawable.food_placeholder)
                    }

                } else {

                    Toast.makeText(
                        requireContext(),
                        response.settings.message,
                        Toast.LENGTH_SHORT
                    ).show()

                }

            }
            catch (e: HttpException) {

                Log.e("RestaurantAPI", "HTTP Error Code: ${e.code()}")

                val errorBody = e.response()?.errorBody()?.string()
                Log.e("RestaurantAPI", "Server Error: $errorBody")

                Toast.makeText(
                    requireContext(),
                    "Server Error: ${e.code()}",
                    Toast.LENGTH_SHORT
                ).show()

            }
            catch (e: IOException) {

                Log.e("RestaurantAPI", "Network Error", e)

                Toast.makeText(
                    requireContext(),
                    "Network error. Please check internet.",
                    Toast.LENGTH_SHORT
                ).show()

            }
            catch (e: Exception) {

                Log.e("RestaurantAPI", "Unexpected Error", e)

                Toast.makeText(
                    requireContext(),
                    "Something went wrong: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()

            }
            finally {

                LoaderHelper.hideLoader()

            }
        }
    }
    override fun onStart() {
        super.onStart()

        (dialog?.findViewById<View>(
            com.google.android.material.R.id.design_bottom_sheet
        ) as? FrameLayout)?.let { sheet ->

            sheet.setBackgroundResource(android.R.color.transparent)

            val behavior = BottomSheetBehavior.from(sheet)
            behavior.isFitToContents = true
            behavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }
}


