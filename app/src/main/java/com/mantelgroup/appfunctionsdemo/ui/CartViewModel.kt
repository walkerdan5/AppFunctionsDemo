package com.mantelgroup.appfunctionsdemo.ui

import androidx.lifecycle.ViewModel
import com.mantelgroup.appfunctionsdemo.data.model.GroceryItem
import com.mantelgroup.appfunctionsdemo.data.repository.CartRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.StateFlow

@HiltViewModel
class CartViewModel @Inject constructor(
    private val repo: CartRepository,
) : ViewModel() {

    val cartItems: StateFlow<Map<GroceryItem, Int>> = repo.cartItems

    fun addToCart(item: GroceryItem) = repo.addToCart(item)

    fun removeItem(item: GroceryItem) = repo.removeItem(item)

    fun clearCart() = repo.clearCart()

}
