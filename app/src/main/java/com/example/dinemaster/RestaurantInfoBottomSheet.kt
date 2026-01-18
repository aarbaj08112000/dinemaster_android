package com.example.dinemaster

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.core.os.bundleOf
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import android.widget.FrameLayout

class RestaurantInfoBottomSheet : BottomSheetDialogFragment() {

    override fun getTheme(): Int =
        com.google.android.material.R.style.Theme_Material3_Light_BottomSheetDialog


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.bottomsheet_restaurant_info, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvName = view.findViewById<TextView>(R.id.tvRestaurantName)
        val tvInfo = view.findViewById<TextView>(R.id.tvRestaurantInfo)
        val tvContact = view.findViewById<TextView>(R.id.tvContact)
        val ivLogo = view.findViewById<ImageView>(R.id.ivRestaurantLogo)

        // Static data (no arguments)
        tvName.text = "Dine Master"
        tvInfo.text = "Best food in town with fresh ingredients and cozy vibes."
        tvContact.text = "📍 Ahmedabad, Gujarat\n📞 +91 9876543210\n🕒 9 AM – 11 PM"
        ivLogo.setImageResource(R.drawable.dine_master_logo)

        val tvClose = view.findViewById<TextView>(R.id.tvClose)
        tvClose.setOnClickListener {
            dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
        (dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as? FrameLayout)?.let { sheet ->
            sheet.setBackgroundResource(android.R.color.transparent)
            val behavior = BottomSheetBehavior.from(sheet)
            behavior.isFitToContents = true
            behavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }
}


