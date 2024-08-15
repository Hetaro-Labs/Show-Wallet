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
import org.bouncycastle.crypto.AsymmetricCipherKeyPair
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters

class SplashActivity : BaseFrameNotMVVMActivity<ActivitySplashBinding>() {

    override fun getBundleExtras(extras: Bundle?) {
    }

    override fun getContentViewLayoutID() = R.layout.activity_splash

    override fun ActivitySplashBinding.initView() {
        GlobalScope.launch(Dispatchers.IO) {
            //test code
            val privateKey =
                "5meW8MJeTQYt9c1gZUCP3ks7uxQvziU2kXthrqfacpxaQ92DAocuFicbqsuj9gWvoQYHPHy5BeWSMhLnzWop53iU"
            val decodedBytes = org.sol4k.Base58.decode(privateKey)
            val privateKeyParams = Ed25519PrivateKeyParameters(decodedBytes, 0)
            val keypair =
                AsymmetricCipherKeyPair(privateKeyParams.generatePublicKey(), privateKeyParams)
            val publicKey = CryptoUtils.convertKeypair(keypair).publicKey.toBase58()
            Ed25519KeyRepositoryNew.insertOne(keypair)
            MmkvUtils.put(AppConstants.SELECTED_PUBLIC_KEY, publicKey)
            log("pubkey=" + publicKey)

            val accounts = Ed25519KeyRepositoryNew.getAll()

            if (accounts.isNullOrEmpty()) {
                openActivity(WelcomeActivity::class.java, true)
            } else {
                openActivity(WalletActivity::class.java, true)
            }
        }
    }
}