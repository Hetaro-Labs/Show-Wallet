/*
 * Copyright (c) 2022 Solana Mobile Inc.
 */

package com.showtime.wallet

import android.app.Application
import com.showtime.wallet.data.Ed25519KeyRepository

class ShoWalletApplication : Application() {
    val keyRepository: Ed25519KeyRepository by lazy {
        Ed25519KeyRepository(this)
    }
}