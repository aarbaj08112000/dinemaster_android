package com.example.dinemaster
import android.app.Activity
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar

fun Activity.showSnackbar(
    message: String,
    isError: Boolean = true,
    anchorView: View? = null
) {
    val snackbar = Snackbar.make(this.findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT)

    val snackbarView = snackbar.view
    snackbarView.setBackgroundColor(
        ContextCompat.getColor(
            this,
            if (isError) R.color.snackbar_error_bg else R.color.snackbar_success_bg
        )
    )
    snackbar.setTextColor(ContextCompat.getColor(this, android.R.color.white))

    // 🔹 Move Snackbar to the TOP
    val params = snackbarView.layoutParams as FrameLayout.LayoutParams
    params.gravity = Gravity.TOP
    snackbarView.layoutParams = params

    // Optional: anchor support
    anchorView?.let { snackbar.anchorView = it }

    snackbar.show()
}


fun Fragment.showSnackbar(
    message: String,
    isError: Boolean = true,
    anchorView: View? = null
) {
    val snackbar = Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT)

    val snackbarView = snackbar.view
    snackbarView.setBackgroundColor(
        ContextCompat.getColor(
            requireContext(),
            if (isError) R.color.snackbar_error_bg else R.color.snackbar_success_bg
        )
    )
    snackbar.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white))

    // 🔹 Move Snackbar to the TOP
    val params = snackbarView.layoutParams as FrameLayout.LayoutParams
    params.gravity = Gravity.TOP
    snackbarView.layoutParams = params

    // Optional: anchor support
    anchorView?.let { snackbar.anchorView = it }

    snackbar.show()
}

