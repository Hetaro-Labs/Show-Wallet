package com.showtime.wallet

import android.os.Bundle
import com.amez.mall.lib_base.base.mvvm.v.BaseFrameNotMVVMActivity
import com.showtime.wallet.data.Ed25519KeyRepositoryNew
import com.showtime.wallet.databinding.ActivitySplashBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SplashActivity : BaseFrameNotMVVMActivity<ActivitySplashBinding>() {

    override fun getBundleExtras(extras: Bundle?) {
    }

    override fun getContentViewLayoutID() = R.layout.activity_splash

    override fun ActivitySplashBinding.initView() {
        /**
         * TODO
         * if Ed25519KeyRepository.getAll()==null
         *  -> WelcomeActivity
         * else
         *  -> WalletActivity
         */
        GlobalScope.launch(Dispatchers.IO) {
            if(Ed25519KeyRepositoryNew.getAll().isNullOrEmpty())
                openActivity(WelcomeActivity::class.java,true)
            else
                openActivity(WalletActivity::class.java,true)
        }
    }
}