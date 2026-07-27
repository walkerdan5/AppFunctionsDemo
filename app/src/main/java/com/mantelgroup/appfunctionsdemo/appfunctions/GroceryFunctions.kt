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
     * If the return value starts with "ITEM_NOT_FOUND" you MUST ask the user to choose from the suggestions — do NOT retry with a guessed name.
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
            ?: return notFound(itemName)
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
            ?: return notFound(itemName)
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
     * If the return value starts with "ITEM_NOT_FOUND" you MUST ask the user to choose from the suggestions — do NOT retry with a guessed name.
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
            ?: return notFound(fromItemName)
        val to = resolveItem(toItemName)
            ?: return notFound(toItemName)
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

    private fun suggest(name: String): String {
        val query = name.trim().lowercase()
        val matches = GroceryItem.entries.filter {
            it.name.lowercase().contains(query) ||
            it.displayName.lowercase().contains(query)
        }
        return if (matches.isEmpty()) "" else
            " Did you mean: ${matches.joinToString(", ") { it.displayName }}?"
    }

    private fun notFound(name: String): String {
        val suggestion = suggest(name)
        if (suggestion.isNotEmpty()) {
            return "ITEM_NOT_FOUND: The item '$name' is not on my list. Please ASK THE USER to pick from: $suggestion — do NOT guess or retry with a different name."
        }
        return "ITEM_NOT_FOUND: The item '$name' is not on my list. Please tell the user it wasn't found."
    }
}
