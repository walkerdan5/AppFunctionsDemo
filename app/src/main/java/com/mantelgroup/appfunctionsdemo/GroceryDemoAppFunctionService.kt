package com.mantelgroup.appfunctionsdemo

import androidx.appfunctions.AppFunctionService
import androidx.annotation.RequiresApi
import androidx.appfunctions.AppFunction
import androidx.appfunctions.AppFunctionServiceEntryPoint
import com.mantelgroup.appfunctionsdemo.appfunctions.GroceryFunctions
import com.mantelgroup.appfunctionsdemo.data.repository.CartRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

@RequiresApi(37)
@AppFunctionServiceEntryPoint(
    serviceName = "GroceryDemoAppFunctionService",
    appFunctionXmlFileName = "grocery_demo_app_functions",
)
abstract class GroceryDemoAppFunctionServiceBase : AppFunctionService() {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface CartRepositoryEntryPoint {
        fun cartRepository(): CartRepository
    }

    private val cartRepository: CartRepository by lazy {
        EntryPointAccessors.fromApplication(
            applicationContext,
            CartRepositoryEntryPoint::class.java
        ).cartRepository()
    }

    private val groceryFunctions by lazy { GroceryFunctions(cartRepository) }

    @AppFunction(isDescribedByKDoc = true)
    fun addGroceryItem(itemName: String, quantity: Int = 1): String =
        groceryFunctions.addGroceryItem(itemName, quantity)

    @AppFunction(isDescribedByKDoc = true)
    fun removeGroceryItem(itemName: String, quantity: Int = 1): String =
        groceryFunctions.removeGroceryItem(itemName, quantity)

    @AppFunction(isDescribedByKDoc = true)
    fun clearCart(): String = groceryFunctions.clearCart()

    @AppFunction(isDescribedByKDoc = true)
    fun getCart(): String = groceryFunctions.getCart()

    @AppFunction(isDescribedByKDoc = true)
    fun swapGroceryItem(fromItemName: String, toItemName: String): String =
        groceryFunctions.swapGroceryItem(fromItemName, toItemName)
}
