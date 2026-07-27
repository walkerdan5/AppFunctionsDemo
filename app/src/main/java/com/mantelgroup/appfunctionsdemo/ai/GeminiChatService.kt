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

class GeminiChatService(
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

        while (response.functionCalls.isNotEmpty()) {
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
            "Add a grocery item to the cart with an optional quantity.",
            mapOf(
                "itemName" to Schema.string("The name of the grocery item, e.g. 'apples' or 'milk'."),
                "quantity" to Schema.integer("How many to add. Defaults to 1 if omitted."),
            ),
            listOf("quantity"),
        ),
        FunctionDeclaration(
            "removeGroceryItem",
            "Remove a grocery item from the cart with an optional quantity. Removes the item entirely if the quantity meets or exceeds the current amount.",
            mapOf(
                "itemName" to Schema.string("The name of the grocery item, e.g. 'apples' or 'milk'."),
                "quantity" to Schema.integer("How many to remove. Defaults to 1 if omitted."),
            ),
            listOf("quantity"),
        ),
        FunctionDeclaration(
            "swapGroceryItem",
            "Swap one item in the cart for another, keeping the same quantity. Merges quantities if the replacement is already in the cart.",
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
