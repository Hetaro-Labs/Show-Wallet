/*
 * Copyright (c) 2022 Solana Mobile Inc.
 */

package com.showtime.wallet.demo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.showtime.wallet.R
import com.showtime.wallet.demo.ShowVaultViewModel.MobileWalletAdapterServiceRequest
import kotlinx.coroutines.launch

class ShowVaultActivity : AppCompatActivity() {
    private val viewModel: ShowVaultViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "MobileWalletAdapterActivity onCreate")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vault)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.mobileWalletAdapterServiceEvents.collect { request ->
                    if (request is MobileWalletAdapterServiceRequest.SessionTerminated) {
                        finish()
                    } else if (request is MobileWalletAdapterServiceRequest.LowPowerNoConnection) {
                        // should use dialog fragment, etc. but this is a quick demo
//                        AlertDialog.Builder(this@MobileWalletAdapterActivity)
//                            .setTitle(R.string.low_power_mode_warning_title)
//                            .setMessage(R.string.str_low_power_mode_warning_dsc)
//                            .setPositiveButton(android.R.string.ok) { _, _ ->
//                                Log.w(TAG, "Connection failed due to device low power mode, returning to dapp.")
//                                finish()
//                            }
//                            .show()
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