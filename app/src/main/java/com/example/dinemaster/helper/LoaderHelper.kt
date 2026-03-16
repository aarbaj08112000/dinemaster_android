package com.example.dinemaster.helper

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.animation.AnimationUtils
import android.graphics.Color
import com.example.dinemaster.R


object LoaderHelper {

    private var dialog: Dialog? = null

    fun showLoader(context: Context) {
        if (dialog == null) {
            dialog = Dialog(context)
            dialog?.setContentView(R.layout.layout_loader)
            dialog?.setCancelable(false)
            dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            val dots = listOf(
                dialog!!.findViewById<View>(R.id.dot1),
                dialog!!.findViewById<View>(R.id.dot2),
                dialog!!.findViewById<View>(R.id.dot3),
                dialog!!.findViewById<View>(R.id.dot4)
            )

            dots.forEachIndexed { index, dot ->
                val anim = AnimationUtils.loadAnimation(context, R.anim.anim_dot_scale)
                anim.startOffset = index * 150L
                dot.startAnimation(anim)
            }
        }
        dialog?.show()
    }

    fun hideLoader() {
        dialog?.dismiss()
        dialog = null
    }
}



