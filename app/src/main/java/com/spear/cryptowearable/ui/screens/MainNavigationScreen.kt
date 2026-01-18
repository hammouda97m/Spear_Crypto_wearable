package com.spear.cryptowearable.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.*

/**
 * Main navigation screen with options to navigate to different sections
 */
@Composable
fun MainNavigationScreen(
    onNavigateToWatchlist: () -> Unit,
    onNavigateToPortfolio: () -> Unit,
    onNavigateToAlerts: () -> Unit
) {
    val listState = rememberScalingLazyListState()
    
    ScalingLazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        item {
            ListHeader {
                Text(
                    text = "Crypto Tracker",
                    textAlign = TextAlign.Center
                )
            }
        }
        
        item {
            Chip(
                label = { Text("Watchlist") },
                onClick = onNavigateToWatchlist,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                colors = ChipDefaults.primaryChipColors()
            )
        }
        
        item {
            Chip(
                label = { Text("Portfolio") },
                onClick = onNavigateToPortfolio,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                colors = ChipDefaults.secondaryChipColors()
            )
        }
        
        item {
            Chip(
                label = { Text("Alerts") },
                onClick = onNavigateToAlerts,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                colors = ChipDefaults.secondaryChipColors()
            )
        }
    }
}
