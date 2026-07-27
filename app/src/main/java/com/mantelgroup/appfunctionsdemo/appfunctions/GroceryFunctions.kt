package com.mantelgroup.appfunctionsdemo.appfunctions

import androidx.appfunctions.AppFunctionContext
import androidx.appfunctions.service.AppFunction
import com.mantelgroup.appfunctionsdemo.ui.CartRepository
import com.mantelgroup.appfunctionsdemo.ui.GroceryItem

class GroceryFunctions(
    val cartRepository: CartRepository,
) {

    /**
     * Adds a grocery item to the cart.
     *
     * Use this when the user wants to add, put, throw in, or place a grocery item into the cart.
     *
     * @param itemName The name of the grocery item to add (e.g. "apples", "milk", "salmon").
     * @param quantity How many of the item to add. Defaults to 1 when the user does not give a number.
     * @return A short confirmation or an error if the item name is not recognised.
     */
    @AppFunction(isDescribedByKDoc = true)
    fun addGroceryItem(
        appFunctionContext: AppFunctionContext,
        itemName: String,
        quantity: Int = 1,
    ): String {
        val item = resolveItem(itemName)
            ?: return "Sorry, I don't recognise '$itemName'."
        cartRepository.addToCart(item, quantity)
        return "Added $quantity × ${item.displayName} to the cart."
    }

    /**
     * Removes a grocery item from the cart.
     *
     * Use this when the user wants to remove, take out, delete, or drop a grocery item from the cart.
     * If the quantity to remove equals or exceeds the current quantity the item is removed entirely.
     *
     * @param itemName The name of the grocery item to remove (e.g. "apples", "milk", "salmon").
     * @param quantity How many of the item to remove. Defaults to 1 when the user does not give a number.
     * @return A short confirmation or an error if the item name is not recognised.
     */
    @AppFunction(isDescribedByKDoc = true)
    fun removeGroceryItem(
        appFunctionContext: AppFunctionContext,
        itemName: String,
        quantity: Int = 1,
    ): String {
        val item = resolveItem(itemName)
            ?: return "Sorry, I don't recognise '$itemName'"
        val cartItems = cartRepository.cartItems.value
        if (!cartItems.containsKey(item)) {
            return "${item.displayName} is not in the cart."
        }
        cartRepository.removeFromCart(item, quantity)
        return "Removed $quantity × ${item.displayName} from the cart."
    }

    /**
     * Removes every item from the cart.
     *
     * Use this when the user wants to clear, empty, wipe, or reset the whole cart.
     *
     * @return A short confirmation that the cart is now empty.
     */
    @AppFunction(isDescribedByKDoc = true)
    fun clearCart(appFunctionContext: AppFunctionContext): String {
        cartRepository.clearCart()
        return "Cart cleared."
    }

    /**
     * Returns the current contents of the cart with quantities and a total price.
     *
     * Use this when the user asks what is in the cart, the cart contents, the total, or the bill.
     *
     * @return A human-readable summary of cart items and the total price.
     */
    @AppFunction(isDescribedByKDoc = true)
    fun getCart(appFunctionContext: AppFunctionContext): String {
        val cartItems = cartRepository.cartItems.value
        if (cartItems.isEmpty()) return "The cart is empty."
        val lines = cartItems.entries.joinToString("\n") { (item, qty) ->
            "  • ${item.displayName} × $qty  (${"$%.2f".format(item.price * qty)})"
        }
        val total = cartItems.entries.sumOf { (item, qty) -> item.price * qty }
        return "Cart:\n$lines\nTotal: ${"$%.2f".format(total)}"
    }

    /**
     * Swaps one grocery item in the cart for another, preserving the quantity.
     *
     * Use this when the user wants to swap, replace, exchange, or substitute one item for another.
     * If the replacement item is already in the cart the quantities are merged.
     *
     * @param fromItemName The name of the item currently in the cart to be replaced.
     * @param toItemName The name of the item to replace it with.
     * @return A short confirmation or an error if either item name is not recognised.
     */
    @AppFunction(isDescribedByKDoc = true)
    fun swapGroceryItem(
        appFunctionContext: AppFunctionContext,
        fromItemName: String,
        toItemName: String,
    ): String {
        val from = resolveItem(fromItemName)
            ?: return "Sorry, I don't recognise '$fromItemName'."
        val to = resolveItem(toItemName)
            ?: return "Sorry, I don't recognise '$toItemName'."
        if (from == to) return "${from.displayName} is already in the cart."
        val cartItems = cartRepository.cartItems.value
        if (!cartItems.containsKey(from)) {
            return "${from.displayName} is not in the cart."
        }
        cartRepository.swapInCart(from, to)
        return "Swapped ${from.displayName} for ${to.displayName}."
    }

    private fun resolveItem(name: String): GroceryItem? {
        val normalised = name.trim().uppercase().replace(' ', '_').replace('-', '_')
        return GroceryItem.entries.firstOrNull { it.name == normalised }
            ?: GroceryItem.entries.firstOrNull {
                it.displayName.equals(name.trim(), ignoreCase = true)
            }
    }

    private fun availableNames(): String =
        GroceryItem.entries.joinToString(", ") { it.displayName }
}
