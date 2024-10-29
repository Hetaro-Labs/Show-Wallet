![Show Wallet](https://github.com/Hetaro-Labs/Show-Wallet/blob/main/show-wallet.png?raw=true)

Show Wallet is an open-source wallet on Solana, craft for smart watches. 

# Build Guide

This guide will walk you through the process of building your Android project using Gradle.

## Prerequisites

1. **Android Studio**: Ensure you have the latest version installed.
2. **JDK**: Java Development Kit 8 or higher is required.
3. **Android SDK**: Properly configured in your system.

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
