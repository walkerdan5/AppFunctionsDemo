package com.mantelgroup.appfunctionsdemo

import android.app.Application
import androidx.appfunctions.service.AppFunctionConfiguration
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.mantelgroup.appfunctionsdemo.appfunctions.GroceryFunctions
import com.mantelgroup.appfunctionsdemo.ui.CartRepository

class CounterApplication : Application(), AppFunctionConfiguration.Provider {

    val cartRepository = CartRepository()

    override fun onCreate() {
        super.onCreate()
        FirebaseAppCheck.getInstance().installAppCheckProviderFactory(
            DebugAppCheckProviderFactory.getInstance()
        )
    }

    override val appFunctionConfiguration: AppFunctionConfiguration
        get() = AppFunctionConfiguration.Builder()
            .addEnclosingClassFactory(GroceryFunctions::class.java) {
                GroceryFunctions(cartRepository)
            }
            .build()
}
