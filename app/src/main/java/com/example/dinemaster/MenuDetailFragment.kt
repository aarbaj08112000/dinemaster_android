package com.example.dinemaster

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import coil.load
import com.example.dinemaster.helper.LoaderHelper
import com.example.dinemaster.helper.RetrofitClient
import com.example.dinemaster.model.ItemDetailsRequest
import kotlinx.coroutines.launch

class MenuDetailFragment : Fragment(R.layout.fragment_menu_detail) {

    private lateinit var ivFoodImage: ImageView
    private lateinit var tvFoodName: TextView
    private lateinit var tvSubtitle: TextView
    private lateinit var tvDescription: TextView
    private lateinit var tvPrice: TextView

    private var itemId: String = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ivFoodImage = view.findViewById(R.id.ivFoodImage)
        tvFoodName = view.findViewById(R.id.tvFoodName)
        tvSubtitle = view.findViewById(R.id.tvSubtitle)
        tvDescription = view.findViewById(R.id.tvDescription)
        tvPrice = view.findViewById(R.id.tvPrice)

        itemId = arguments?.getString("item_id") ?: ""

        if (itemId.isNotEmpty()) {
            loadItemDetails(itemId)
        }
    }

    private fun loadItemDetails(itemId: String) {

        viewLifecycleOwner.lifecycleScope.launch {

            LoaderHelper.showLoader(requireContext())

            try {

                val request = ItemDetailsRequest(itemId)

                val response = RetrofitClient.api.getItemDetails(request)

                if (response.settings.success) {

                    val item = response.data

                    tvFoodName.text = item.name
                    tvSubtitle.text = item.code
                    tvDescription.text = item.description
                    tvPrice.text = "₹${item.base_price}"

                    ivFoodImage.load(item.image_url) {
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

            } catch (e: Exception) {

                Toast.makeText(
                    requireContext(),
                    "Failed to load item details",
                    Toast.LENGTH_SHORT
                ).show()

                e.printStackTrace()

            } finally {

                LoaderHelper.hideLoader()

            }
        }
    }
}
