package com.example.dinemaster

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate


class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Force light mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        // Initialize wave after setContentView
//        val wave = findViewById<ImageView>(R.id.wave)
//
//        wave.animate()
//            .translationYBy(-30f)
//            .setDuration(1500)
//            .setInterpolator(android.view.animation.AccelerateDecelerateInterpolator())
//            .setListener(null) // optional reset
//            .withEndAction {
//                // loop manually if needed
//                wave.animate()
//                    .translationYBy(30f)
//                    .setDuration(1500)
//                    .setInterpolator(android.view.animation.AccelerateDecelerateInterpolator())
//                    .withEndAction { wave.animate() } // endless loop
//            }

        // Move to next screen after 2s
        Handler(Looper.getMainLooper()).postDelayed({
            val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
            val isLoggedIn = sharedPref.getBoolean("isLoggedIn", false)

            if (isLoggedIn) {
                startActivity(Intent(this, HomeActivity::class.java))
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
            }
            finish()
        }, 2000)
    }
}
