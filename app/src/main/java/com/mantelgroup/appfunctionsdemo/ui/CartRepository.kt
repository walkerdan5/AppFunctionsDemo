package com.mantelgroup.appfunctionsdemo.ui

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CartRepository {

    private val _cartItems = MutableStateFlow<Map<GroceryItem, Int>>(emptyMap())
    val cartItems: StateFlow<Map<GroceryItem, Int>> = _cartItems.asStateFlow()

    fun addToCart(item: GroceryItem, quantity: Int = 1) {
        _cartItems.update { current ->
            buildMap {
                put(item, (current[item] ?: 0) + quantity.coerceAtLeast(1))
                current.forEach { (k, v) -> if (k != item) put(k, v) }
            }
        }
    }

    fun removeFromCart(item: GroceryItem, quantity: Int = 1) {
        _cartItems.update { current ->
            val next = current[item]?.let { it - quantity.coerceAtLeast(1) }
            if (next == null || next <= 0) current - item
            else current + (item to next)
        }
    }

    fun swapInCart(from: GroceryItem, to: GroceryItem) {
        _cartItems.update { current ->
            val fromQty = current[from] ?: return@update current
            val toExisting = current[to] ?: 0
            buildMap {
                current.forEach { (k, v) ->
                    when (k) {
                        from -> put(to, fromQty + toExisting)
                        to -> {}
                        else -> put(k, v)
                    }
                }
            }
        }
    }

    fun clearCart() {
        _cartItems.value = emptyMap()
    }

    val totalItems: Int get() = _cartItems.value.values.sum()
}
