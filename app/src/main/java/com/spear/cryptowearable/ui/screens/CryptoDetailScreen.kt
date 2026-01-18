package com.spear.cryptowearable.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material.*
import com.spear.cryptowearable.ui.theme.CryptoColors
import com.spear.cryptowearable.utils.FormatUtils
import com.spear.cryptowearable.viewmodel.CryptoDetailUiState
import com.spear.cryptowearable.viewmodel.CryptoDetailViewModel

/**
 * Cryptocurrency detail screen with chart
 */
@Composable
fun CryptoDetailScreen(
    cryptoId: String,
    viewModel: CryptoDetailViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val chartData by viewModel.chartData.collectAsState()
    val selectedDays by viewModel.selectedDays.collectAsState()
    val listState = rememberScalingLazyListState()
    
    LaunchedEffect(cryptoId) {
        viewModel.loadCryptoDetail(cryptoId)
    }
    
    ScalingLazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (val state = uiState) {
            is CryptoDetailUiState.Loading -> {
                item {
                    CircularProgressIndicator()
                }
            }
            
            is CryptoDetailUiState.Success -> {
                val crypto = state.crypto
                val changeColor = if ((crypto.priceChangePercentage24h ?: 0.0) >= 0) 
                    CryptoColors.Positive else CryptoColors.Negative
                
                item {
                    ListHeader {
                        Text(
                            text = crypto.symbol.uppercase(),
                            textAlign = TextAlign.Center
                        )
                    }
                }
                
                item {
                    Text(
                        text = crypto.name,
                        style = MaterialTheme.typography.body2,
                        textAlign = TextAlign.Center
                    )
                }
                
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                item {
                    Text(
                        text = FormatUtils.formatPrice(crypto.currentPrice),
                        style = MaterialTheme.typography.title1,
                        textAlign = TextAlign.Center
                    )
                }
                
                item {
                    crypto.priceChangePercentage24h?.let { change ->
                        val changeSign = if (change >= 0) "+" else ""
                        Text(
                            text = "$changeSign${FormatUtils.formatPercentage(change)}",
                            style = MaterialTheme.typography.body1,
                            color = changeColor,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
                
                // Chart period selector
                item {
                    Text(
                        text = "Price Chart",
                        style = MaterialTheme.typography.title3,
                        modifier = Modifier.padding(8.dp)
                    )
                }
                
                item {
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                    ) {
                        ChipPeriodButton("1D", 1, selectedDays) {
                            viewModel.loadChartData(cryptoId, 1)
                        }
                        ChipPeriodButton("7D", 7, selectedDays) {
                            viewModel.loadChartData(cryptoId, 7)
                        }
                        ChipPeriodButton("30D", 30, selectedDays) {
                            viewModel.loadChartData(cryptoId, 30)
                        }
                    }
                }
                
                item {
                    if (chartData.isNotEmpty()) {
                        Text(
                            text = "${chartData.size} data points",
                            style = MaterialTheme.typography.caption2,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
                
                // Additional stats
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                crypto.high24h?.let { high ->
                    item {
                        Text(
                            text = "24h High: ${FormatUtils.formatPrice(high)}",
                            style = MaterialTheme.typography.caption1,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                
                crypto.low24h?.let { low ->
                    item {
                        Text(
                            text = "24h Low: ${FormatUtils.formatPrice(low)}",
                            style = MaterialTheme.typography.caption1,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                
                crypto.marketCap?.let { marketCap ->
                    item {
                        Text(
                            text = "Market Cap: ${FormatUtils.formatLargeNumber(marketCap)}",
                            style = MaterialTheme.typography.caption1,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            
            is CryptoDetailUiState.Error -> {
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
fun ChipPeriodButton(
    label: String,
    days: Int,
    selectedDays: Int,
    onClick: () -> Unit
) {
    val isSelected = days == selectedDays
    CompactChip(
        label = { Text(label) },
        onClick = onClick,
        colors = if (isSelected) {
            ChipDefaults.primaryChipColors()
        } else {
            ChipDefaults.secondaryChipColors()
        }
    )
}
