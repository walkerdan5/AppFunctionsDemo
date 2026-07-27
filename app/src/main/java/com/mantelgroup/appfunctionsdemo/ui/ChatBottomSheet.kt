package com.mantelgroup.appfunctionsdemo.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mantelgroup.appfunctionsdemo.ui.theme.AppFunctionsDemoTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatBottomSheet(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ChatViewModel = viewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var input by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(state.messages.size, state.isSending) {
        val target = state.messages.size
        if (target > 0) listState.animateScrollToItem(target)
    }

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState) {
        ChatSheetContent(
            state = state,
            input = input,
            onInputChange = { input = it },
            onSend = { viewModel.sendMessage(input); input = "" },
            listState = listState,
            modifier = modifier,
        )
    }
}

@Composable
private fun ChatSheetContent(
    state: ChatUiState,
    input: String,
    onInputChange: (String) -> Unit,
    onSend: () -> Unit,
    listState: LazyListState,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxHeight(0.5f)
            .padding(horizontal = 16.dp)
            .padding(bottom = 16.dp),
    ) {
        Text(
            text = "Shopping assistant",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 8.dp),
        )

        if (state.messages.isEmpty()) {
            Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                Text(
                    text = "Try: \"Add 3 apples\", \"Remove milk\", \"What's in my cart?\", or \"Clear the cart\".",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxWidth().weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(state.messages) { message -> MessageBubble(message) }
                if (state.isSending) {
                    item {
                        Text(
                            text = "Assistant is typing…",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(4.dp),
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            OutlinedTextField(
                value = input,
                onValueChange = onInputChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text("Talk to the assistant…") },
                maxLines = 4,
                shape = RoundedCornerShape(12.dp),
            )
            Button(
                onClick = onSend,
                enabled = input.isNotBlank() && !state.isSending,
            ) {
                Text("Send")
            }
        }
    }
}

@Composable
private fun MessageBubble(message: ChatMessage) {
    val alignment = if (message.isUser) Alignment.CenterEnd else Alignment.CenterStart
    val bubbleColor = if (message.isUser) {
        MaterialTheme.colorScheme.secondaryContainer
    } else {
        MaterialTheme.colorScheme.primary
    }
    val textColor = if (message.isUser) {
        MaterialTheme.colorScheme.onSurfaceVariant
    } else {
        MaterialTheme.colorScheme.onPrimary
    }

    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = alignment) {
        Surface(
            color = bubbleColor,
            shape = RoundedCornerShape(16.dp),
        ) {
            Text(
                text = message.text,
                color = textColor,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ChatSheetEmptyPreview() {
    AppFunctionsDemoTheme {
        ChatSheetContent(
            state = ChatUiState(),
            input = "",
            onInputChange = {},
            onSend = {},
            listState = rememberLazyListState(),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ChatSheetWithMessagesPreview() {
    AppFunctionsDemoTheme {
        ChatSheetContent(
            state = ChatUiState(
                messages = listOf(
                    ChatMessage("Add 3 apples to my cart", isUser = true),
                    ChatMessage("Done! I added 3 Apples ($1.99 each) to your cart.", isUser = false),
                    ChatMessage("What's my total?", isUser = true),
                ),
                isSending = true,
            ),
            input = "Remove milk",
            onInputChange = {},
            onSend = {},
            listState = rememberLazyListState(),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MessageBubblePreview() {
    AppFunctionsDemoTheme {
        Surface(modifier = Modifier.padding(16.dp)) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                MessageBubble(ChatMessage("Add 3 apples to my cart", isUser = true))
                MessageBubble(ChatMessage("Done! I added 3 Apples to your cart.", isUser = false))
            }
        }
    }
}
