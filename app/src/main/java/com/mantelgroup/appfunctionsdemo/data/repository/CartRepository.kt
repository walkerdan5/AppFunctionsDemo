package com.mantelgroup.appfunctionsdemo.data.repository

import com.mantelgroup.appfunctionsdemo.data.model.GroceryItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

interface CartRepository {
    val cartItems: StateFlow<Map<GroceryItem, Int>>
    fun addToCart(item: GroceryItem, quantity: Int = 1)
    fun removeFromCart(item: GroceryItem, quantity: Int = 1)
    fun removeItem(item: GroceryItem)
    fun swapInCart(from: GroceryItem, to: GroceryItem)
    fun clearCart()
    val totalItems: Int
}

@Singleton
class DefaultCartRepository @Inject constructor() : CartRepository {

    private val _cartItems = MutableStateFlow<Map<GroceryItem, Int>>(emptyMap())
    override val cartItems: StateFlow<Map<GroceryItem, Int>> = _cartItems.asStateFlow()

    override fun addToCart(item: GroceryItem, quantity: Int) {
        _cartItems.update { current ->
            buildMap {
                put(item, (current[item] ?: 0) + quantity.coerceAtLeast(1))
                current.forEach { (k, v) -> if (k != item) put(k, v) }
            }
        }
    }

    override fun removeFromCart(item: GroceryItem, quantity: Int) {
        _cartItems.update { current ->
            val next = current[item]?.let { it - quantity.coerceAtLeast(1) }
            if (next == null || next <= 0) current - item
            else current + (item to next)
        }
    }

    override fun removeItem(item: GroceryItem) {
        _cartItems.update { current -> current - item }
    }

    override fun swapInCart(from: GroceryItem, to: GroceryItem) {
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

    override fun clearCart() {
        _cartItems.value = emptyMap()
    }

    override val totalItems: Int get() = _cartItems.value.values.sum()
}
