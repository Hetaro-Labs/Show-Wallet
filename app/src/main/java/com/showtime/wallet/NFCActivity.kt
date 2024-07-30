package com.showtime.wallet

import android.content.Intent
import android.net.Uri
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.TagTechnology
import androidx.appcompat.app.AppCompatActivity
import com.amez.mall.lib_base.bean.Hydration
import com.amez.mall.lib_base.bean.TokenInfoReq
import com.amez.mall.lib_base.net.ApiRequest
import com.showtime.wallet.net.bean.Token
import com.showtime.wallet.utils.TokenListCache
import com.showtime.wallet.utils.notNull
import java.io.IOException

class NFCActivity : BaseUriHandleActivity() {

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        if (intent.action == NfcAdapter.ACTION_TAG_DISCOVERED) {
            val tag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
            // Process the tag data here
            val tagTechList = tag?.techList?.let { tagTechList ->
                for (tech in tagTechList) {
//                    try {
//                        val tagTechnology = TagTechnology.get(tag, tech)
//                        tagTechnology.connect()
//                        // Read data from the tag using tagTechnology
//                    } catch (e: IOException) {
//                        // Handle exceptions
//                    } finally {
//                        try {
//                            tagTechnology?.close()
//                        } catch (e: IOException) {
//                            // Handle exceptions
//                        }
//                    }
                }
            }

        }
    }

}