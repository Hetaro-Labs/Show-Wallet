/*
 * Copyright (c) 2022 Solana Mobile Inc.
 */

package com.showtime.wallet

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.showtime.wallet.ShowVaultViewModel.WalletServiceRequest
import kotlinx.coroutines.launch

class ShowVaultActivity : AppCompatActivity() {
    private val viewModel: ShowVaultViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mobile_wallet_adapter)
        Log.d(TAG, "MobileWalletAdapterActivity start")

        // listen events
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.walletServiceEvents.collect { request ->
                    if (request is WalletServiceRequest.SessionTerminated) {
                        finish()
                    } else if (request is WalletServiceRequest.LowPowerNoConnection) {
                        // TODO: Alert Dialog
                    }
                }
            }
        }

        val result = viewModel.processLaunch(intent, callingPackage)
        if (!result) {
            Log.w(TAG, "Invalid launch intent; finishing activity")
            finish()
            return
        }
    }

    companion object {
        private val TAG = ShowVaultActivity::class.simpleName
    }
}