package com.showtime.wallet

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.solana.core.DerivationPath
import com.solana.core.HotAccount
import com.solana.vendor.bip39.Mnemonic
import com.solana.vendor.bip39.WordCount


class CreateWalletActivity: AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generate_phrase)

        val phrase24 = Mnemonic(WordCount.COUNT_24).phrase

        val account = HotAccount.fromMnemonic(phrase24, "", DerivationPath.BIP44_M_44H_501H_0H)
        account.publicKey.toString() // G75kGJiizyFNdnvvHxkrBrcwLomGJT2CigdXnsYzrFHv


    }

}