/*
 * Copyright (c) 2022 Solana Mobile Inc.
 */

package com.showtime.wallet

import android.app.Application
import com.showtime.wallet.data.Ed25519KeyRepository
import com.showtime.wallet.utils.MmkvUtils

class ShowVaultApplication : Application() {

    companion object {
        private var instance: Application? = null
        fun instance() = instance ?: throw Throwable("instance Not yet initialized")
    }

    val keyRepository: Ed25519KeyRepository by lazy {
        Ed25519KeyRepository(this)
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        // Initialize mmkv
        MmkvUtils.initMmkv(this)
    }
}