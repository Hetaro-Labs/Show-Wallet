package com.showtime.wallet

import android.os.Bundle
import android.util.Log
import com.amez.mall.lib_base.base.mvvm.v.BaseFrameNotMVVMActivity
import com.amez.mall.lib_base.utils.Logger
import com.amez.mall.lib_base.utils.MmkvUtils
import com.showtime.wallet.data.Ed25519KeyRepositoryNew
import com.showtime.wallet.databinding.ActivitySplashBinding
import com.showtime.wallet.utils.AppConstants
import com.showtime.wallet.utils.CryptoUtils
import com.showtime.wallet.utils.TokenListCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.bitcoinj.core.Base58

class SplashActivity : BaseFrameNotMVVMActivity<ActivitySplashBinding>() {

    override fun getBundleExtras(extras: Bundle?) {
    }

    override fun getContentViewLayoutID() = R.layout.activity_splash

    override fun ActivitySplashBinding.initView() {
        GlobalScope.launch(Dispatchers.IO) {
            val accounts = Ed25519KeyRepositoryNew.getAll()

            if(accounts.isNullOrEmpty()){
                openActivity(WelcomeActivity::class.java,true)
            } else{
                val keypair = CryptoUtils.convertKeypair(accounts[0])
                val base58PrivateKey = Base58.encode(keypair.secret)
                Logger.d("Keypair", "public key: ${keypair.publicKey.toBase58()}", )
                Logger.d("Keypair", "private key: $base58PrivateKey")

                openActivity(WalletActivity::class.java,true)
            }
        }
    }
}