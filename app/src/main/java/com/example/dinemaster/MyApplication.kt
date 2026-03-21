package com.example.dinemaster

import android.app.Application
import com.example.dinemaster.helper.PrefManager

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        instance = this

        // ⭐ initialize PrefManager here
        PrefManager.init(this)
    }

    companion object {
        lateinit var instance: MyApplication
            private set
    }
}