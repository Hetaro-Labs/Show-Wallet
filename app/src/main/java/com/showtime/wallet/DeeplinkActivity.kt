package com.showtime.wallet

import android.content.Intent
import android.os.Bundle

class DeeplinkActivity: BaseUriHandleActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        log("WTF")

        log("data: ${intent.data}")
        intent.data?.let { data ->
            parcelData(data)
            // Extract data from the intent and navigate to the desired screen
            val host = data.host
            log("host: $host")

            val params = data.queryParameterNames
            for (p in params){
                log("p: $p -> ${data.getQueryParameter(p)}")
            }
        }
    }

}