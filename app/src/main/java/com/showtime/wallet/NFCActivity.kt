package com.showtime.wallet

import android.content.Intent
import android.net.Uri
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.TagTechnology
import android.os.Bundle
import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity
import com.amez.mall.lib_base.bean.Hydration
import com.amez.mall.lib_base.bean.TokenInfoReq
import com.amez.mall.lib_base.net.ApiRequest
import com.showtime.wallet.net.bean.Token
import com.showtime.wallet.utils.TokenListCache
import com.showtime.wallet.utils.notNull
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset
import kotlin.experimental.and

class NFCActivity : BaseUriHandleActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        log("onCreate: " + intent.action)

        readFromIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        log("onNewIntent")
    }

    private fun readFromIntent(intent: Intent) {
        val action = intent.action
        if (NfcAdapter.ACTION_TAG_DISCOVERED == action || NfcAdapter.ACTION_TECH_DISCOVERED == action || NfcAdapter.ACTION_NDEF_DISCOVERED == action) {
            val myTag = intent.getParcelableExtra<Parcelable>(NfcAdapter.EXTRA_TAG) as Tag?
            val rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
            var msgs = mutableListOf<NdefMessage>()
            if (rawMsgs != null) {
                for (i in rawMsgs.indices) {
                    msgs.add(i, rawMsgs[i] as NdefMessage)
                }
                buildTagViews(msgs.toTypedArray())
            }
        }
    }


    private fun buildTagViews(msgs: Array<NdefMessage>) {
        if (msgs == null || msgs.isEmpty()) return

        var data = ""
        val payload = msgs[0].records[0].payload
        val textEncoding: Charset = if ((payload[0] and 128.toByte()).toInt() == 0) Charsets.UTF_8 else Charsets.UTF_16 // Get the Text Encoding
        val languageCodeLength: Int = (payload[0] and 51).toInt() // Get the Language Code, e.g. "en"
        try {
            // Get the Text
            data = String(
                payload,
                languageCodeLength + 1,
                payload.size - languageCodeLength - 1,
                textEncoding
            )
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }

        try {
            val uri = Uri.parse(data)
            parcelData(uri)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}