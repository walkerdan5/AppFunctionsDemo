package com.mantelgroup.appfunctionsdemo.ai

import com.google.firebase.Firebase
import com.google.firebase.ai.Chat
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.FunctionDeclaration
import com.google.firebase.ai.type.FunctionResponsePart
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.Schema
import com.google.firebase.ai.type.Tool
import com.google.firebase.ai.type.content
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import javax.inject.Inject

class GeminiChatService @Inject constructor(
    private val appFunctionRunner: AppFunctionRunner,
) {
    private val chat: Chat by lazy {
        Firebase.ai(backend = GenerativeBackend.googleAI())
            .generativeModel(
                modelName = MODEL_NAME,
                tools = listOf(Tool.functionDeclarations(functionDeclarations())),
            )
            .startChat()
    }

    suspend fun sendMessage(userMessage: String): String {
        var response = chat.sendMessage(userMessage)

        var iterations = 0
        while (response.functionCalls.isNotEmpty() && iterations < 3) {
            iterations++
            val resultParts = response.functionCalls.map { call ->
                val result = appFunctionRunner.execute(call.name, call.args)
                FunctionResponsePart(call.name, buildJsonObject { put("result", result) })
            }
            response = chat.sendMessage(
                content(role = "function") { resultParts.forEach { part(it) } },
            )
        }

        return response.text ?: "(The assistant did not return any text.)"
    }

    private fun functionDeclarations(): List<FunctionDeclaration> = listOf(
        FunctionDeclaration(
            "addGroceryItem",
            "Call this function exactly ONCE per item the user wants to add. Adds the given quantity of a grocery item to the cart (default 1). If the return value starts with 'ITEM_NOT_FOUND', ask the user to pick from the listed options — do NOT retry with a guessed name.",
            mapOf(
                "itemName" to Schema.string("The name of the grocery item, e.g. 'apples' or 'milk'."),
                "quantity" to Schema.integer("How many to add. Defaults to 1 if omitted. Do not guess — only use a number the user explicitly said."),
            ),
            listOf("quantity"),
        ),
        FunctionDeclaration(
            "removeGroceryItem",
            "Call this function exactly ONCE per item the user wants to remove. Removes the given quantity of a grocery item from the cart (default 1). Removes the item entirely if the quantity meets or exceeds the current amount. If the return value starts with 'ITEM_NOT_FOUND', ask the user to pick from the listed options — do NOT retry with a guessed name.",
            mapOf(
                "itemName" to Schema.string("The name of the grocery item, e.g. 'apples' or 'milk'."),
                "quantity" to Schema.integer("How many to remove. Defaults to 1 if omitted. Do not guess — only use a number the user explicitly said."),
            ),
            listOf("quantity"),
        ),
        FunctionDeclaration(
            "swapGroceryItem",
            "Swap one item in the cart for another, keeping the same quantity. Merges quantities if the replacement is already in the cart. If the return value starts with 'ITEM_NOT_FOUND', ask the user to pick from the listed options — do NOT retry with a guessed name.",
            mapOf(
                "fromItemName" to Schema.string("The name of the item to replace, e.g. 'salmon'."),
                "toItemName" to Schema.string("The name of the item to replace it with, e.g. 'shrimp'."),
            ),
        ),
        FunctionDeclaration(
            "clearCart",
            "Remove every item from the cart.",
            emptyMap(),
        ),
        FunctionDeclaration(
            "getCart",
            "Get the current cart contents, quantities, and total price.",
            emptyMap(),
        ),
    )

    companion object {
        const val MODEL_NAME = "gemini-3.1-flash-lite"
    }
}
