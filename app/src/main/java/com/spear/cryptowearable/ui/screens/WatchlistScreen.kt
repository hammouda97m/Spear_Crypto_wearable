package com.spear.cryptowearable.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material.*
import com.spear.cryptowearable.ui.theme.CryptoColors
import com.spear.cryptowearable.utils.FormatUtils
import com.spear.cryptowearable.viewmodel.WatchlistUiState
import com.spear.cryptowearable.viewmodel.WatchlistViewModel

/**
 * Watchlist screen showing cryptocurrency prices
 */
@Composable
fun WatchlistScreen(
    onNavigateToDetail: (String) -> Unit,
    onNavigateToSearch: () -> Unit,
    viewModel: WatchlistViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberScalingLazyListState()
    
    ScalingLazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            ListHeader {
                Text(
                    text = "Watchlist",
                    textAlign = TextAlign.Center
                )
            }
        }
        
        when (val state = uiState) {
            is WatchlistUiState.Loading -> {
                item {
                    CircularProgressIndicator()
                }
            }
            
            is WatchlistUiState.Empty -> {
                item {
                    Text(
                        text = "No cryptocurrencies in watchlist",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                item {
                    Chip(
                        label = { Text("Add Crypto") },
                        onClick = onNavigateToSearch,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        colors = ChipDefaults.primaryChipColors()
                    )
                }
            }
            
            is WatchlistUiState.Success -> {
                item {
                    Chip(
                        label = { Text("Add More") },
                        onClick = onNavigateToSearch,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        colors = ChipDefaults.primaryChipColors()
                    )
                }
                
                items(state.items.size) { index ->
                    val item = state.items[index]
                    CryptoListItem(
                        symbol = item.crypto.symbol.uppercase(),
                        name = item.crypto.name,
                        price = FormatUtils.formatPrice(item.crypto.currentPrice),
                        priceChange = item.crypto.priceChangePercentage24h ?: 0.0,
                        onClick = { onNavigateToDetail(item.crypto.id) }
                    )
                }
            }
            
            is WatchlistUiState.Error -> {
                item {
                    Text(
                        text = "Error: ${state.message}",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colors.error
                    )
                }
            }
        }
    }
}

@Composable
fun CryptoListItem(
    symbol: String,
    name: String,
    price: String,
    priceChange: Double,
    onClick: () -> Unit
) {
    val changeColor = if (priceChange >= 0) CryptoColors.Positive else CryptoColors.Negative
    val changeSign = if (priceChange >= 0) "+" else ""
    
    Chip(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        label = {
            Column {
                Text(
                    text = symbol,
                    style = MaterialTheme.typography.title3,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = name,
                    style = MaterialTheme.typography.caption2,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        },
        secondaryLabel = {
            Column(horizontalAlignment = Alignment.End) {
                Text(text = price, style = MaterialTheme.typography.body2)
                Text(
                    text = "$changeSign${FormatUtils.formatPercentage(priceChange)}",
                    style = MaterialTheme.typography.caption2,
                    color = changeColor
                )
            }
        },
        colors = ChipDefaults.secondaryChipColors()
    )
}
