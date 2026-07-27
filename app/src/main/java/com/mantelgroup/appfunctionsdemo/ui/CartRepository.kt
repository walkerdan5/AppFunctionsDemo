package com.mantelgroup.appfunctionsdemo.ui

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CartRepository {

    private val _cartItems = MutableStateFlow<Map<GroceryItem, Int>>(emptyMap())
    val cartItems: StateFlow<Map<GroceryItem, Int>> = _cartItems.asStateFlow()

    fun addToCart(item: GroceryItem, quantity: Int = 1) {
        val current = _cartItems.value
        _cartItems.value = buildMap {
            put(item, (current[item] ?: 0) + quantity.coerceAtLeast(1))
            current.forEach { (k, v) -> if (k != item) put(k, v) }
        }
    }

    fun removeFromCart(item: GroceryItem, quantity: Int = 1) {
        _cartItems.value = _cartItems.value.toMutableMap().apply {
            val current = this[item] ?: return
            val next = current - quantity.coerceAtLeast(1)
            if (next <= 0) remove(item) else this[item] = next
        }
    }

    fun swapInCart(from: GroceryItem, to: GroceryItem) {
        val current = _cartItems.value
        val fromQty = current[from] ?: return
        val toExisting = current[to] ?: 0
        _cartItems.value = buildMap {
            current.forEach { (k, v) ->
                when (k) {
                    from -> put(to, fromQty + toExisting)
                    to -> {}
                    else -> put(k, v)
                }
            }
        }
    }

    fun clearCart() {
        _cartItems.value = emptyMap()
    }

    val totalItems: Int get() = _cartItems.value.values.sum()
}
