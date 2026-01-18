package com.spear.cryptowearable.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.wear.compose.material.*
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.spear.cryptowearable.ui.screens.*
import com.spear.cryptowearable.ui.theme.CryptoWearTheme
import com.spear.cryptowearable.worker.PriceUpdateWorker

/**
 * Main activity for the Wear OS app
 */
class MainActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        
        // Schedule background work for price updates
        PriceUpdateWorker.schedule(this)
        
        setContent {
            CryptoWearApp()
        }
    }
}

@Composable
fun CryptoWearApp() {
    CryptoWearTheme {
        val navController = rememberSwipeDismissableNavController()
        val listState = rememberScalingLazyListState()
        
        Scaffold(
            timeText = {
                TimeText()
            },
            vignette = {
                Vignette(vignettePosition = VignettePosition.TopAndBottom)
            },
            positionIndicator = {
                PositionIndicator(scalingLazyListState = listState)
            }
        ) {
            SwipeDismissableNavHost(
                navController = navController,
                startDestination = "main"
            ) {
                composable("main") {
                    MainNavigationScreen(
                        onNavigateToWatchlist = { navController.navigate("watchlist") },
                        onNavigateToPortfolio = { navController.navigate("portfolio") },
                        onNavigateToAlerts = { navController.navigate("alerts") }
                    )
                }
                
                composable("watchlist") {
                    WatchlistScreen(
                        onNavigateToDetail = { cryptoId ->
                            navController.navigate("detail/$cryptoId")
                        },
                        onNavigateToSearch = {
                            navController.navigate("search")
                        }
                    )
                }
                
                composable("search") {
                    SearchScreen()
                }
                
                composable("detail/{cryptoId}") { backStackEntry ->
                    val cryptoId = backStackEntry.arguments?.getString("cryptoId") ?: return@composable
                    CryptoDetailScreen(cryptoId = cryptoId)
                }
                
                composable("portfolio") {
                    PortfolioScreen(
                        onNavigateToAdd = {
                            navController.navigate("portfolio/add")
                        }
                    )
                }
                
                composable("portfolio/add") {
                    AddPortfolioScreen()
                }
                
                composable("alerts") {
                    AlertsScreen(
                        onNavigateToAdd = {
                            navController.navigate("alerts/add")
                        }
                    )
                }
                
                composable("alerts/add") {
                    AddAlertScreen()
                }
            }
        }
    }
}
