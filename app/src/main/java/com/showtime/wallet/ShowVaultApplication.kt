/*
 * Copyright (c) 2022 Solana Mobile Inc.
 */

package com.showtime.wallet

import android.app.Application
import androidx.multidex.MultiDexApplication
import com.amez.mall.lib_base.utils.MmkvUtils
import com.showtime.wallet.data.Ed25519KeyRepository

class ShowVaultApplication : MultiDexApplication() {

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