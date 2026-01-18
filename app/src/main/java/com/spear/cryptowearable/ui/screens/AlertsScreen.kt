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
import com.spear.cryptowearable.viewmodel.AlertsUiState
import com.spear.cryptowearable.viewmodel.AlertsViewModel

/**
 * Alerts screen showing price alerts
 */
@Composable
fun AlertsScreen(
    onNavigateToAdd: () -> Unit,
    viewModel: AlertsViewModel = viewModel()
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
                    text = "Price Alerts",
                    textAlign = TextAlign.Center
                )
            }
        }
        
        when (val state = uiState) {
            is AlertsUiState.Loading -> {
                item {
                    CircularProgressIndicator()
                }
            }
            
            is AlertsUiState.Empty -> {
                item {
                    Text(
                        text = "No price alerts configured",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                item {
                    Chip(
                        label = { Text("Add Alert") },
                        onClick = onNavigateToAdd,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        colors = ChipDefaults.primaryChipColors()
                    )
                }
            }
            
            is AlertsUiState.Success -> {
                item {
                    Chip(
                        label = { Text("Add Alert") },
                        onClick = onNavigateToAdd,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        colors = ChipDefaults.primaryChipColors()
                    )
                }
                
                items(state.alerts.size) { index ->
                    val alert = state.alerts[index]
                    val direction = if (alert.isAbove) "Above" else "Below"
                    val statusColor = when {
                        !alert.isEnabled -> MaterialTheme.colors.onSurfaceVariant
                        alert.isTriggered -> CryptoColors.Positive
                        else -> MaterialTheme.colors.onSurface
                    }
                    
                    ToggleChip(
                        checked = alert.isEnabled,
                        onCheckedChange = { viewModel.toggleAlert(alert) },
                        label = {
                            Column {
                                Text(
                                    text = "${alert.symbol.uppercase()} $direction",
                                    style = MaterialTheme.typography.title3,
                                    color = statusColor
                                )
                                Text(
                                    text = FormatUtils.formatPrice(alert.targetPrice),
                                    style = MaterialTheme.typography.caption2,
                                    color = statusColor
                                )
                            }
                        },
                        toggleControl = {
                            Icon(
                                imageVector = ToggleChipDefaults.switchIcon(checked = alert.isEnabled),
                                contentDescription = null
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        colors = ToggleChipDefaults.toggleChipColors()
                    )
                }
            }
            
            is AlertsUiState.Error -> {
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
 * Screen for adding price alerts
 * Simplified version - in real app would have input fields
 */
@Composable
fun AddAlertScreen(
    viewModel: AlertsViewModel = viewModel()
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
                    text = "Add Alert",
                    textAlign = TextAlign.Center
                )
            }
        }
        
        item {
            Text(
                text = "Feature coming soon!\n\nUse voice input or companion app to add alerts.",
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}
