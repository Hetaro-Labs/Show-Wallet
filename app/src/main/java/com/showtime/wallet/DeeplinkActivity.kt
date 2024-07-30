package com.showtime.wallet

import android.content.Intent

class DeeplinkActivity: BaseUriHandleActivity() {
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        intent.data?.let { data ->
            // Extract data from the intent and navigate to the desired screen
            val path = data.path

        }
    }
}