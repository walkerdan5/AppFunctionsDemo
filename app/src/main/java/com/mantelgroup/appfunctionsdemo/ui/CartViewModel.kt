package com.mantelgroup.appfunctionsdemo.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mantelgroup.appfunctionsdemo.CounterApplication
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CartViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = (application as CounterApplication).cartRepository

    val cartItems: StateFlow<Map<GroceryItem, Int>> = repo.cartItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    fun addToCart(item: GroceryItem) {
        viewModelScope.launch { repo.addToCart(item) }
    }

    fun removeFromCart(item: GroceryItem) {
        viewModelScope.launch { repo.removeFromCart(item) }
    }

    fun clearCart() {
        repo.clearCart()
    }

    val totalItems: Int get() = repo.totalItems
}
