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
import com.spear.cryptowearable.viewmodel.PortfolioUiState
import com.spear.cryptowearable.viewmodel.PortfolioViewModel

/**
 * Portfolio screen showing user's holdings
 */
@Composable
fun PortfolioScreen(
    onNavigateToAdd: () -> Unit,
    viewModel: PortfolioViewModel = viewModel()
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
                    text = "Portfolio",
                    textAlign = TextAlign.Center
                )
            }
        }
        
        when (val state = uiState) {
            is PortfolioUiState.Loading -> {
                item {
                    CircularProgressIndicator()
                }
            }
            
            is PortfolioUiState.Empty -> {
                item {
                    Text(
                        text = "No holdings in portfolio",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                item {
                    Chip(
                        label = { Text("Add Holding") },
                        onClick = onNavigateToAdd,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        colors = ChipDefaults.primaryChipColors()
                    )
                }
            }
            
            is PortfolioUiState.Success -> {
                // Total value card
                item {
                    Card(
                        onClick = { },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Total Value",
                                style = MaterialTheme.typography.caption1
                            )
                            Text(
                                text = FormatUtils.formatPrice(state.totalValue),
                                style = MaterialTheme.typography.title1
                            )
                            
                            val profitLossColor = if (state.totalProfitLoss >= 0) 
                                CryptoColors.Positive else CryptoColors.Negative
                            val sign = if (state.totalProfitLoss >= 0) "+" else ""
                            
                            Text(
                                text = "$sign${FormatUtils.formatPrice(state.totalProfitLoss)}",
                                style = MaterialTheme.typography.body2,
                                color = profitLossColor
                            )
                        }
                    }
                }
                
                item {
                    Chip(
                        label = { Text("Add More") },
                        onClick = onNavigateToAdd,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        colors = ChipDefaults.primaryChipColors()
                    )
                }
                
                // Holdings list
                items(state.holdings.size) { index ->
                    val holding = state.holdings[index]
                    val profitLossColor = if (holding.profitLoss >= 0) 
                        CryptoColors.Positive else CryptoColors.Negative
                    val sign = if (holding.profitLoss >= 0) "+" else ""
                    
                    Chip(
                        onClick = { },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        label = {
                            Column {
                                Text(
                                    text = "${holding.item.symbol.uppercase()} (${holding.item.amount})",
                                    style = MaterialTheme.typography.title3
                                )
                                Text(
                                    text = FormatUtils.formatPrice(holding.currentValue),
                                    style = MaterialTheme.typography.caption2
                                )
                            }
                        },
                        secondaryLabel = {
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "$sign${FormatUtils.formatPrice(holding.profitLoss)}",
                                    style = MaterialTheme.typography.body2,
                                    color = profitLossColor
                                )
                                Text(
                                    text = "${if (holding.profitLossPercentage >= 0) "+" else ""}${String.format("%.2f", holding.profitLossPercentage)}%",
                                    style = MaterialTheme.typography.caption2,
                                    color = profitLossColor
                                )
                            }
                        },
                        colors = ChipDefaults.secondaryChipColors()
                    )
                }
            }
            
            is PortfolioUiState.Error -> {
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

/**
 * Screen for adding holdings to portfolio
 * Simplified version - in real app would have input fields
 */
@Composable
fun AddPortfolioScreen(
    viewModel: PortfolioViewModel = viewModel()
) {
    val listState = rememberScalingLazyListState()
    
    ScalingLazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            ListHeader {
                Text(
                    text = "Add Holding",
                    textAlign = TextAlign.Center
                )
            }
        }
        
        item {
            Text(
                text = "Feature coming soon!\n\nUse voice input or companion app to add holdings.",
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}
