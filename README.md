# Show Wallet
Show Wallet is an open-source wallet on Solana, craft for smart watches. 

![Show Wallet](https://github.com/Hetaro-Labs/Show-Wallet/blob/main/show-wallet.png?raw=true)


# Table of Contents
1. Toolchain Before Build
2. Build Process
3. Architecture
4. Key Features
5. Roadmap

# Toolchain Before Build

To successfully build the project, ensure you have the following tools installed:

- **Java Development Kit (JDK)**: Version 11 or newer
- **Android Studio**: Latest stable version
- **Gradle**: Version compatible with the project's `gradle-wrapper.properties`
- **Android SDK**: Ensure all required SDK components are installed

# Build Process

This guide will walk you through the process of building your Android project using Gradle.

## Build Configuration
### `app/build.gradle` 
Please update your own keystore information in app/build.gradle

```
signingConfigs {
	//update your keystore info 
	release {
		storeFile file("")
		storePassword ""
		keyAlias ""
		keyPassword ""
	}
}
```

Adn update your own api key in lib_base/build.gradle
```
ext {
apiKey = 'your-helius-rpc-key'
}
```

## Building the Project
### Using Android Studio
Open Android Studio and load your project.
Click on Build in the menu bar.
Select Build Bundle(s) / APK(s) and then Build APK(s).
View the build output in the "Build" window.

### Using Command Line
Open a terminal.
Navigate to your project directory.
Run the following command to build the APK:
./gradlew app:assembleRelease
Or 
./gradlew app:bundleRelease
to build .aab file. 

## Running the App
### Using Android Studio
Connect your Android device via USB or start an emulator.
Click on the green Run button or press Shift + F10.

### Using Command Line
Ensure an emulator is running or a device is connected.
Run the following command:
./gradlew app:installDebug

# Architecture

```
-project
--app
---com.showtime.wallet
---com.showtime.wallet.adapter
---com.showtime.wallet.data
---com.showtime.wallet.net
---com.showtime.wallet.net.bean
---com.showtime.wallet.usecase
---com.showtime.wallet.utils
---com.showtime.wallet.view
---com.showtime.wallet.vm
--common
--sol4k //Solana web3.js in Kotlin
--walletlib //Solana mobile adapter
```


# Key Features

- **Transfer SOL and SPL Tokens**: Effortlessly transfer SOL and SPL tokens to any wallet address with low transaction fees.

- **Receive Tokens**: Generate and share your wallet address or QR code to receive SOL and SPL tokens securely.

- **Send SPL Tokens**: Easily send SPL tokens to other wallets with a user-friendly interface that ensures accurate transactions.

- **Transaction History**: View a detailed history of all your transactions, including timestamps, amounts, and transaction IDs.

- **Swap Tokens**: Instantly swap between different SPL tokens within the wallet using integrated decentralized exchanges.

- **NFT Management**: View, send, and receive NFTs with detailed information and a visual gallery of your NFT collection.

- **QR Code Scanner**: Utilize the built-in QR code scanner for quick and accurate address entry when sending tokens.


# Roadmap

### Q1 2025
- **Launch Beta Version**: Release a beta version for initial testing and feedback.
- **User Feedback Integration**: Gather user feedback and integrate improvements.
- **Security Audit**: Conduct a comprehensive security audit to ensure user funds are safe.

### Q2 2025
- **Full Public Release**: Launch the first stable version of the wallet.
- **Advanced NFT Features**: Introduce NFT trading and detailed analytics.
- **Enhanced Swap Functionality**: Integrate more token swap options and improve UI for better user experience.

### Q3 2025
- **Mobile App Development**: Release mobile applications for iOS and Android.
- **Multilingual Support**: Add support for multiple languages to cater to a global audience.
- **Staking Enhancements**: Provide detailed staking insights and automatic reward reinvestment.

### Q4 2025
- **DApp Browser Integration**: Incorporate a DApp browser for seamless interaction with decentralized applications.
- **Cross-Platform Sync**: Enable synchronization across all devices for consistent user experience.
- **Community Building**: Launch community forums and support channels to foster user engagement.

### Future Plans
- **AI-Powered Insights**: Implement AI-driven analytics for personalized investment insights.
- **Partnerships and Integrations**: Collaborate with other blockchain projects to expand functionalities.
- **Continuous Security Improvements**: Regular updates and audits to maintain top-notch security standards.
