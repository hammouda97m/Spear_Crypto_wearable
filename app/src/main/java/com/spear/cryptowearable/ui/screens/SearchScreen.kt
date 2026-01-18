package com.spear.cryptowearable.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material.*
import com.spear.cryptowearable.viewmodel.WatchlistViewModel

/**
 * Search screen for adding cryptocurrencies to watchlist
 */
@Composable
fun SearchScreen(
    viewModel: WatchlistViewModel = viewModel()
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val listState = rememberScalingLazyListState()
    
    ScalingLazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            ListHeader {
                Text(
                    text = "Add Crypto",
                    textAlign = TextAlign.Center
                )
            }
        }
        
        item {
            Text(
                text = "Search for cryptocurrencies to add to your watchlist",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.caption1,
                modifier = Modifier.padding(16.dp)
            )
        }
        
        // Note: Text input on Wear OS is limited. In a real app, 
        // you'd use voice input or a predefined list
        item {
            Text(
                text = "Popular Cryptos:",
                style = MaterialTheme.typography.title3,
                modifier = Modifier.padding(8.dp)
            )
        }
        
        // Show popular cryptocurrencies as quick add buttons
        val popularCryptos = listOf(
            "Bitcoin" to "bitcoin",
            "Ethereum" to "ethereum",
            "Cardano" to "cardano",
            "Solana" to "solana",
            "Polkadot" to "polkadot",
            "Dogecoin" to "dogecoin",
            "Ripple" to "ripple",
            "Polygon" to "matic-network"
        )
        
        items(popularCryptos.size) { index ->
            val (name, id) = popularCryptos[index]
            Chip(
                label = { Text(name) },
                onClick = {
                    viewModel.addToWatchlist(
                        com.spear.cryptowearable.data.model.CoinSearchItem(
                            id = id,
                            symbol = id,
                            name = name,
                            thumb = null,
                            marketCapRank = index + 1
                        )
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                colors = ChipDefaults.secondaryChipColors()
            )
        }
    }
}
