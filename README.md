# Spear Crypto Wearable - Cryptocurrency Tracker for Wear OS

A comprehensive Wear OS application for tracking cryptocurrencies on Samsung Galaxy Watch 5 Pro and other Wear OS 3.0+ devices.

## Features

### 📊 Live Price Tracking
- Real-time cryptocurrency prices from CoinGecko API
- Auto-refresh with smart caching (1-minute cache)
- 24-hour price change indicators
- Color-coded price movements (green for up, red for down)

### 📱 Custom Watchlist
- Add/remove cryptocurrencies easily
- Quick access to popular cryptos (BTC, ETH, ADA, SOL, etc.)
- Persistent storage with Room database
- Swipe navigation between screens

### 🔔 Price Alerts
- Set custom price thresholds
- Above/below target price notifications
- Vibration feedback on watch
- Background monitoring with WorkManager
- Enable/disable alerts on the fly

### 💰 Portfolio Tracking
- Track your cryptocurrency holdings
- Real-time portfolio value calculation
- Profit/loss tracking
- Purchase price vs current price comparison
- Percentage gain/loss indicators

### 📈 Price Charts
- Historical price data visualization
- Multiple time periods (1D, 7D, 30D)
- Watch-optimized charts
- 24h high/low indicators
- Market cap information

### ⌚ Wear OS Features
- **Tiles**: Quick glance widget showing crypto prices
- **Complications**: Watch face integration for price display
- **Rotary Input**: Full support for rotating bezel/crown
- **Dark Theme**: AMOLED-friendly pure black theme
- **Smooth Navigation**: Optimized for circular displays

## Technical Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose for Wear OS
- **Architecture**: MVVM (Model-View-ViewModel)
- **API**: CoinGecko Free API v3
- **Database**: Room (SQLite)
- **Networking**: Retrofit + OkHttp
- **Async**: Kotlin Coroutines + Flow
- **Background Work**: WorkManager
- **Image Loading**: Coil
- **Charts**: Vico (Compose-based charts)

## Project Structure

```
app/
├── src/main/
│   ├── java/com/spear/cryptowearable/
│   │   ├── ui/
│   │   │   ├── MainActivity.kt                 # Main entry point
│   │   │   ├── screens/                        # Compose screens
│   │   │   │   ├── MainNavigationScreen.kt     # Home navigation
│   │   │   │   ├── WatchlistScreen.kt          # Crypto list
│   │   │   │   ├── SearchScreen.kt             # Add cryptos
│   │   │   │   ├── CryptoDetailScreen.kt       # Detail & chart
│   │   │   │   ├── PortfolioScreen.kt          # Holdings
│   │   │   │   └── AlertsScreen.kt             # Price alerts
│   │   │   └── theme/                          # UI theme
│   │   ├── data/
│   │   │   ├── api/                            # Retrofit API
│   │   │   │   ├── CoinGeckoService.kt         # API endpoints
│   │   │   │   └── ApiClient.kt                # Retrofit setup
│   │   │   ├── database/                       # Room database
│   │   │   │   ├── CryptoDatabase.kt
│   │   │   │   └── CryptoDao.kt
│   │   │   ├── model/                          # Data models
│   │   │   │   └── Models.kt
│   │   │   └── repository/                     # Data layer
│   │   │       └── CryptoRepository.kt
│   │   ├── viewmodel/                          # ViewModels
│   │   │   ├── WatchlistViewModel.kt
│   │   │   ├── PortfolioViewModel.kt
│   │   │   ├── AlertsViewModel.kt
│   │   │   └── CryptoDetailViewModel.kt
│   │   ├── worker/                             # Background work
│   │   │   └── PriceUpdateWorker.kt
│   │   ├── tile/                               # Tile service
│   │   │   └── CryptoTileService.kt
│   │   ├── complication/                       # Watch complications
│   │   │   └── CryptoComplicationService.kt
│   │   ├── notification/                       # Notifications
│   │   │   └── NotificationHelper.kt
│   │   └── utils/                              # Utilities
│   │       └── FormatUtils.kt
│   ├── res/                                    # Resources
│   │   ├── drawable/                           # Icons
│   │   ├── values/                             # Strings, colors
│   │   └── mipmap/                             # App icons
│   └── AndroidManifest.xml                     # App manifest
├── build.gradle.kts                            # App build config
└── proguard-rules.pro                          # ProGuard rules
```

## Setup Instructions

### Prerequisites

1. **Android Studio**: Flamingo (2022.2.1) or later
2. **Java Development Kit**: JDK 17 or later
3. **Android SDK**: API Level 34
4. **Wear OS Emulator** or physical Wear OS device (Samsung Galaxy Watch 5 Pro recommended)

### Installation Steps

1. **Clone the repository**
   ```bash
   git clone https://github.com/hammouda97m/Spear_Crypto_wearable.git
   cd Spear_Crypto_wearable
   ```

2. **Open in Android Studio**
   - Launch Android Studio
   - Select "Open an Existing Project"
   - Navigate to the cloned directory
   - Click "OK"

3. **Sync Gradle**
   - Android Studio will automatically sync Gradle
   - Wait for dependencies to download
   - If sync fails, click "File > Sync Project with Gradle Files"

4. **Setup Wear OS Emulator (Optional)**
   - Go to "Tools > Device Manager"
   - Click "Create Device"
   - Select "Wear OS" category
   - Choose "Wear OS Large Round" or "Wear OS Small Round"
   - Select system image (API 30 or higher)
   - Finish setup

## Building the APK

### Debug Build

Build a debug APK for testing:

```bash
# Command line
./gradlew assembleDebug

# APK location
app/build/outputs/apk/debug/app-debug.apk
```

### Release Build

Build an optimized release APK:

```bash
# Command line
./gradlew assembleRelease

# APK location
app/build/outputs/apk/release/app-release.apk
```

**Note**: For release builds, you need to configure signing. Create a keystore:

```bash
keytool -genkey -v -keystore crypto-wearable.jks -keyalg RSA -keysize 2048 -validity 10000 -alias crypto-wearable
```

Add to `app/build.gradle.kts`:

```kotlin
signingConfigs {
    create("release") {
        storeFile = file("../crypto-wearable.jks")
        storePassword = "your_password"
        keyAlias = "crypto-wearable"
        keyPassword = "your_password"
    }
}

buildTypes {
    release {
        signingConfig = signingConfigs.getByName("release")
        // ... rest of config
    }
}
```

### Build from Android Studio

1. Select "Build > Build Bundle(s) / APK(s) > Build APK(s)"
2. Wait for build to complete
3. Click "locate" in the notification to find the APK

## Installing on Samsung Galaxy Watch 5 Pro

### Method 1: ADB (Android Debug Bridge)

1. **Enable Developer Options on Watch**
   - Go to Settings > About watch
   - Tap "Software version" 7 times
   - Go back to Settings > Developer options
   - Enable "ADB debugging"
   - Enable "Debug over Wi-Fi"

2. **Connect via ADB**
   ```bash
   # Find watch IP address (shown in Developer options)
   adb connect <WATCH_IP_ADDRESS>:5555
   
   # Install APK
   adb install app/build/outputs/apk/debug/app-debug.apk
   ```

3. **Launch App**
   - App will appear in watch app drawer
   - Or use: `adb shell am start -n com.spear.cryptowearable/.ui.MainActivity`

### Method 2: Wear OS Companion App

1. Install "Wear OS" app on your phone
2. Pair your Galaxy Watch 5 Pro
3. Use a file manager app to transfer APK to phone
4. Use a Wear OS APK installer app (like "Wear Installer")

### Method 3: Google Play Console (Production)

1. Create a Google Play Developer account
2. Create a new Wear OS app
3. Upload the signed APK/AAB
4. Complete store listing
5. Publish to production or internal testing
6. Install from Play Store on watch

## API Configuration

This app uses the **CoinGecko Free API** which requires no API key for basic usage.

### API Limits (Free Tier)
- 10-30 calls/minute
- No API key required
- Public data only

### Endpoints Used
- `/coins/markets` - Market data for cryptocurrencies
- `/coins/{id}` - Detailed coin information
- `/coins/{id}/market_chart` - Historical price data
- `/search` - Search cryptocurrencies

### Rate Limiting
The app implements smart caching (1-minute cache) and periodic updates (15-minute intervals) to stay within rate limits.

## Usage Guide

### First Launch

1. **Main Screen**: Shows three navigation options
   - Watchlist: Track crypto prices
   - Portfolio: Manage holdings
   - Alerts: Set price notifications

2. **Add Cryptocurrencies**
   - Navigate to Watchlist
   - Tap "Add More" or "Add Crypto"
   - Select from popular cryptos list
   - Tap to add to watchlist

3. **View Details**
   - Tap any cryptocurrency in watchlist
   - View current price and 24h change
   - Switch between 1D, 7D, and 30D charts
   - See 24h high/low and market cap

4. **Set Price Alerts**
   - Navigate to Alerts
   - Tap "Add Alert"
   - Select crypto and target price
   - Choose above/below threshold
   - Get notifications when triggered

5. **Track Portfolio** (Coming Soon)
   - Navigate to Portfolio
   - Add holdings with purchase price
   - View total value and P&L

### Watch Face Integration

**Add Tile**:
1. Long press on watch face
2. Swipe left to tiles
3. Tap "+" to add tile
4. Find "Crypto Price"
5. Swipe right to view crypto prices

**Add Complication**:
1. Long press on watch face
2. Tap "Customize"
3. Select a complication slot
4. Scroll to find "Crypto Price"
5. Select display format (Short Text, Long Text, or Ranged Value)

## Testing

### Run on Emulator

```bash
# Start emulator
emulator -avd Wear_OS_Large_Round_API_30

# Install and run
./gradlew installDebug
adb shell am start -n com.spear.cryptowearable/.ui.MainActivity
```

### Test Features

- ✅ Add/remove cryptocurrencies to watchlist
- ✅ View real-time prices and changes
- ✅ Navigate to detail screens
- ✅ Set and toggle price alerts
- ✅ View portfolio holdings
- ✅ Test rotary input (use mouse wheel in emulator)
- ✅ Verify notifications
- ✅ Check background updates

## Troubleshooting

### Build Issues

**Problem**: Gradle sync fails
```
Solution: 
- Check internet connection
- File > Invalidate Caches / Restart
- Update Android Studio to latest version
```

**Problem**: Compilation errors
```
Solution:
- Ensure JDK 17 is configured
- Check build.gradle.kts versions match
- Clean project: Build > Clean Project
```

### Runtime Issues

**Problem**: App crashes on launch
```
Solution:
- Check LogCat for errors
- Verify minSdk is 30 or higher
- Clear app data and reinstall
```

**Problem**: No data showing
```
Solution:
- Check internet connection
- Verify CoinGecko API is accessible
- Add cryptocurrencies to watchlist first
```

**Problem**: Notifications not working
```
Solution:
- Grant notification permission
- Enable "Debug over Wi-Fi" might disable notifications
- Check notification settings on watch
```

## Battery Optimization

- Background updates: Every 15 minutes (configurable)
- Smart caching: Reduces API calls
- AMOLED dark theme: Saves battery on OLED displays
- WorkManager: Efficient background processing
- No unnecessary wake locks

## Privacy & Permissions

### Required Permissions
- `INTERNET`: Fetch cryptocurrency data
- `WAKE_LOCK`: Background updates
- `VIBRATE`: Alert notifications
- `POST_NOTIFICATIONS`: Show price alerts

### Data Collection
- No personal data collected
- No analytics or tracking
- All data stored locally on device
- API calls to CoinGecko only

## Known Limitations

- Text input on Wear OS is limited (use predefined crypto list)
- Charts are simplified for watch screen size
- Free CoinGecko API has rate limits
- Background updates limited by Android battery optimization
- Some features require companion app (portfolio/alert input)

## Future Enhancements

- [ ] Voice input for adding cryptos and alerts
- [ ] Companion phone app for easier data entry
- [ ] More chart customization options
- [ ] Support for multiple currencies (EUR, GBP, etc.)
- [ ] Price trend notifications
- [ ] Portfolio statistics and charts
- [ ] Export portfolio data
- [ ] Dark/light theme toggle
- [ ] Custom refresh intervals
- [ ] Multiple watchlist groups

## Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## Dependencies

See `app/build.gradle.kts` for complete list:

- **Wear OS**: androidx.wear:wear:1.3.0
- **Wear Compose**: androidx.wear.compose:compose-material:1.3.0
- **Room**: androidx.room:room-runtime:2.6.1
- **Retrofit**: com.squareup.retrofit2:retrofit:2.9.0
- **Coroutines**: org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3
- **WorkManager**: androidx.work:work-runtime-ktx:2.9.0
- **Vico Charts**: com.patrykandpatrick.vico:compose:1.13.1

## License

This project is open source and available under the MIT License.

## Support

For issues, questions, or suggestions:
- Open an issue on GitHub
- Contact: [GitHub Issues](https://github.com/hammouda97m/Spear_Crypto_wearable/issues)

## Acknowledgments

- **CoinGecko**: Cryptocurrency data API
- **Google**: Wear OS platform and libraries
- **Samsung**: Galaxy Watch 5 Pro
- **Android Community**: Libraries and tools

---

**Built with ❤️ for Wear OS** | **Powered by CoinGecko API**
