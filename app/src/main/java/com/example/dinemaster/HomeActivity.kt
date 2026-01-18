package com.example.dinemaster
import android.content.res.ColorStateList
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.dinemaster.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private var doubleBackToExitPressedOnce = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Load default fragment
        setActiveTab(binding.tabHome)
        loadFragment(HomeFragment(), "Home")

        binding.tabHome.setOnClickListener {
            setActiveTab(binding.tabHome)
            loadFragment(HomeFragment(), "Home")
        }

        binding.tabCreateOrder.setOnClickListener {
            setActiveTab(binding.tabCreateOrder)
            loadFragment(CreateOrderFragment(), "Create Order")
        }

        binding.tabOrderList.setOnClickListener {
            setActiveTab(binding.tabOrderList)
            loadFragment(OrderListFragment(), "Orders")
        }

        binding.tabProfile.setOnClickListener {
            setActiveTab(binding.tabProfile)
            loadFragment(ProfileFragment(), "Profile")
        }

        binding.tabMenu.setOnClickListener {
            setActiveTab(binding.tabMenu)
            loadFragment(MenuFragment(), "Menu")
        }

        // Back press handling with new dispatcher
        onBackPressedDispatcher.addCallback(this) {
            if (doubleBackToExitPressedOnce) {
                finishAffinity()
            } else {
                doubleBackToExitPressedOnce = true
                Toast.makeText(this@HomeActivity, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()

                Handler(Looper.getMainLooper()).postDelayed({
                    doubleBackToExitPressedOnce = false
                }, 2000)
            }
        }
    }

    private fun loadFragment(fragment: Fragment, title: String) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
        updateHeader(title, false)
    }

     fun setActiveTab(activeView: View) {
        val tabs = listOf(
            binding.tabHome,
            binding.tabMenu,
            binding.tabOrderList,
            binding.tabProfile
        )

        // Reset all tabs first
        tabs.forEach { tab ->
            tab.background = null
            (tab as TextView).apply {
                // inactive state → text white, icon white
                setTextColor(ContextCompat.getColor(this@HomeActivity, R.color.white))
                compoundDrawablesRelative.forEach { drawable ->
                    drawable?.setTint(ContextCompat.getColor(this@HomeActivity, R.color.white))
                }
            }
        }

        // Special case: Create Order FAB
        binding.tabCreateOrder.backgroundTintList =
            ColorStateList.valueOf(ContextCompat.getColor(this, android.R.color.white))
        binding.tabCreateOrder.imageTintList =
            ColorStateList.valueOf(ContextCompat.getColor(this, R.color.dark_gray))

        // Highlight active tab
        if (activeView is TextView) {
            activeView.background =
                ContextCompat.getDrawable(this, R.drawable.bottom_nav_item_bg)
            activeView.setTextColor(ContextCompat.getColor(this, R.color.color_primary))
            activeView.compoundDrawablesRelative.forEach { drawable ->
                drawable?.setTint(ContextCompat.getColor(this, R.color.color_primary))
            }
        } else if (activeView == binding.tabCreateOrder) {
            // Active Create Order
            binding.tabCreateOrder.imageTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.color_primary))
        }
    }

    fun setActiveTabByTag(tag: String) {
        when (tag) {
            "Home" -> setActiveTab(binding.tabHome)
            "Menu" -> setActiveTab(binding.tabMenu)
            "Orders" -> setActiveTab(binding.tabOrderList)
            "Profile" -> setActiveTab(binding.tabProfile)
            "Create Order" -> setActiveTab(binding.tabCreateOrder)
        }
    }

fun updateHeader(title: String, showBack: Boolean, onBackClick: (() -> Unit)? = null) {
    findViewById<TextView>(R.id.tvTitle).text = title
    val back = findViewById<TextView>(R.id.tvBack)
    back.visibility = if (showBack) View.VISIBLE else View.GONE

    back.setOnClickListener {
        if (onBackClick != null) {
            onBackClick.invoke()   // custom redirect
        } else {
            supportFragmentManager.popBackStack() // default back behavior
        }
    }
}

}

