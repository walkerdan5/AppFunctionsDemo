package com.mantelgroup.appfunctionsdemo.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mantelgroup.appfunctionsdemo.R
import com.mantelgroup.appfunctionsdemo.data.model.GroceryCategory
import com.mantelgroup.appfunctionsdemo.data.model.GroceryItem
import com.mantelgroup.appfunctionsdemo.ui.theme.AppFunctionsDemoTheme

private val CARD_WIDTH = 140.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToCart: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CartViewModel = hiltViewModel(),
) {
    val cartItems by viewModel.cartItems.collectAsStateWithLifecycle()
    var showChat by remember { mutableStateOf(false) }
    val totalPrice = cartItems.entries.sumOf { (item, qty) -> item.price * qty }

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Groceries",
                        style = MaterialTheme.typography.headlineMedium,
                    )
                },
                actions = {
                    Surface(
                        onClick = { showChat = true },
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.size(36.dp),
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Filled.SmartToy,
                                contentDescription = "Shopping assistant",
                                modifier = Modifier.size(18.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Surface(
                        onClick = onNavigateToCart,
                        shape = MaterialTheme.shapes.extraLarge,
                        color = MaterialTheme.colorScheme.primaryContainer,
                    ) {
                        Row(
                            modifier = Modifier.padding(start = 12.dp, end = 14.dp, top = 8.dp, bottom = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ShoppingCart,
                                contentDescription = "View cart",
                                modifier = Modifier.size(18.dp),
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = stringResource(R.string.price_format, totalPrice),
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                ),
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 80.dp),
        ) {
            GroceryCategory.entries.forEach { category ->
                val categoryItems = GroceryItem.entries.filter { it.category == category }
                if (categoryItems.isNotEmpty()) {
                    item(key = "header_${category}") {
                        CategoryHeader(category = category)
                    }
                    item(key = "row_${category}") {
                        CategoryRow(
                            items = categoryItems,
                            cartItems = cartItems,
                            onAdd = { viewModel.addToCart(it) },
                        )
                    }
                }
            }
        }
    }

    if (showChat) {
        ChatBottomSheet(onDismiss = { showChat = false })
    }
}

@Composable
private fun CategoryHeader(
    category: GroceryCategory,
    modifier: Modifier = Modifier,
) {
    Text(
        text = category.displayName,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        modifier = modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp),
        color = MaterialTheme.colorScheme.onBackground,
    )
}

@Composable
private fun CategoryRow(
    items: List<GroceryItem>,
    cartItems: Map<GroceryItem, Int>,
    onAdd: (GroceryItem) -> Unit,
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(items, key = { it }) { item ->
            GroceryGridCard(
                item = item,
                quantity = cartItems[item] ?: 0,
                onAdd = { onAdd(item) },
            )
        }
    }
}

@Composable
private fun GroceryGridCard(
    item: GroceryItem,
    quantity: Int,
    onAdd: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val inCart = quantity > 0

    Card(
        modifier = modifier.width(CARD_WIDTH),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = item.emoji,
                    fontSize = 48.sp,
                    textAlign = TextAlign.Center,
                )

                if (inCart) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp),
                        shape = MaterialTheme.shapes.small,
                        color = MaterialTheme.colorScheme.primaryContainer,
                    ) {
                        Text(
                            text = "$quantity",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .padding(bottom = 12.dp),
            ) {
                Text(
                    text = item.displayName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                )

                Spacer(modifier = Modifier.height(2.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = stringResource(R.string.price_format, item.price),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                    )

                    Surface(
                        onClick = onAdd,
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(32.dp),
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "+",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun GroceryGridCardEmptyPreview() {
    AppFunctionsDemoTheme {
        Surface(
            modifier = Modifier.padding(12.dp),
            shape = MaterialTheme.shapes.large,
        ) {
            GroceryGridCard(
                item = GroceryItem.APPLES,
                quantity = 0,
                onAdd = {},
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun GroceryGridCardInCartPreview() {
    AppFunctionsDemoTheme {
        Surface(
            modifier = Modifier.padding(12.dp),
            shape = MaterialTheme.shapes.large,
        ) {
            GroceryGridCard(
                item = GroceryItem.SALMON,
                quantity = 3,
                onAdd = {},
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
private fun HomeScreenFullPreview() {
    val sampleCart = mapOf(
        GroceryItem.APPLES to 2,
        GroceryItem.MILK to 1,
        GroceryItem.BANANAS to 3,
    )
    val sampleTotal = sampleCart.entries.sumOf { (item, qty) -> item.price * qty }

    AppFunctionsDemoTheme {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Groceries",
                            style = MaterialTheme.typography.headlineMedium,
                        )
                    },
                    actions = {
                        Surface(
                            onClick = {},
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.size(36.dp),
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Filled.SmartToy,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Surface(
                            onClick = {},
                            shape = MaterialTheme.shapes.extraLarge,
                            color = MaterialTheme.colorScheme.primaryContainer,
                        ) {
                            Row(
                                modifier = Modifier.padding(start = 12.dp, end = 14.dp, top = 8.dp, bottom = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.ShoppingCart,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp),
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = stringResource(R.string.price_format, sampleTotal),
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(16.dp))
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = MaterialTheme.colorScheme.onBackground,
                    ),
                )
            },
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(bottom = 80.dp),
            ) {
                GroceryCategory.entries.forEach { category ->
                    val categoryItems = GroceryItem.entries.filter { it.category == category }
                    if (categoryItems.isNotEmpty()) {
                        item(key = "header_${category}") {
                            CategoryHeader(category = category)
                        }
                        item(key = "row_${category}") {
                            CategoryRow(
                                items = categoryItems,
                                cartItems = sampleCart,
                                onAdd = {},
                            )
                        }
                    }
                }
            }
        }
    }
}
