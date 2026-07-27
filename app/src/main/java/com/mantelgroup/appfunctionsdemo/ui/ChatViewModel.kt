package com.mantelgroup.appfunctionsdemo.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mantelgroup.appfunctionsdemo.ai.AppFunctionRunner
import com.mantelgroup.appfunctionsdemo.ai.GeminiChatService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ChatMessage(
    val text: String,
    val isUser: Boolean,
)

data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val isSending: Boolean = false,
)

class ChatViewModel(application: Application) : AndroidViewModel(application) {

    private val chatService = GeminiChatService(AppFunctionRunner(application))

    private val _state = MutableStateFlow(ChatUiState())
    val state: StateFlow<ChatUiState> = _state.asStateFlow()

    fun sendMessage(text: String) {
        val trimmed = text.trim()
        if (trimmed.isEmpty() || _state.value.isSending) return

        _state.update {
            it.copy(
                messages = it.messages + ChatMessage(trimmed, isUser = true),
                isSending = true,
            )
        }

        viewModelScope.launch {
            val reply = try {
                chatService.sendMessage(trimmed)
            } catch (e: Exception) {
                e.message ?: "Something went wrong."
            }
            _state.update {
                it.copy(
                    messages = it.messages + ChatMessage(reply, isUser = false),
                    isSending = false,
                )
            }
        }
    }
}
