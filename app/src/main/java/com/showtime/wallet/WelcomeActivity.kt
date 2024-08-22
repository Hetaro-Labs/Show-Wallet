package com.showtime.wallet

import android.os.Bundle
import com.amez.mall.lib_base.ui.BaseProjNotMVVMActivity
import com.showtime.wallet.databinding.ActivityWelcomeBinding
import com.showtime.wallet.utils.clickNoRepeat

class WelcomeActivity : BaseProjNotMVVMActivity<ActivityWelcomeBinding>() {

    override fun getBundleExtras(extras: Bundle?) {
    }

    override fun getContentViewLayoutID() = R.layout.activity_welcome

    override fun ActivityWelcomeBinding.initView() {
        mBinding.apply {
            btnCreateWallet.clickNoRepeat {
                openActivity(CreateWalletActivity::class.java,true)
            }

            btnImport.clickNoRepeat {
                ImportPrivateKeyFragment.start(this@WelcomeActivity)
            }
        }
    }

}