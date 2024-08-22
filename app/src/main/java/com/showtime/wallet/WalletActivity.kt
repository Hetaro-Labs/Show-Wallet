package com.showtime.wallet

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import com.amez.mall.lib_base.ui.BaseProjActivity
import com.amez.mall.lib_base.utils.MmkvUtils
import com.showtime.wallet.databinding.ActivityWalletBinding
import com.showtime.wallet.utils.AppConstants
import com.showtime.wallet.utils.CryptoUtils
import com.showtime.wallet.utils.clickNoRepeat
import com.showtime.wallet.vm.WalletHomeVModel

class WalletActivity : BaseProjActivity<ActivityWalletBinding, WalletHomeVModel>() {

    private val TAG = "WalletActivity"
    private var selectedPublicKey: String = ""
    private var fragmentTag = 0

    override fun getBundleExtras(extras: Bundle?) {
    }

    override fun getContentViewLayoutID() = R.layout.activity_wallet

    override fun initLiveDataObserve() {
    }

    override fun initRequestData() {
    }

    override fun ActivityWalletBinding.initView() {
        initWalletHomeFragment()
        layoutAccountSelection.clickNoRepeat {
            mViewModel.showAccountPopWindow(this@WalletActivity, layoutAccountSelection) {
                tvAccountName.text = CryptoUtils.getDisplayAddress(it)
                updateSelectedAccount(it)
            }
        }
    }

    private fun initWalletHomeFragment() {
        val key = MmkvUtils.getString(AppConstants.SELECTED_PUBLIC_KEY)
        Log.e(TAG, "initView: $key")

        key?.let {
            selectedPublicKey = it
            mBinding.tvAccountName.text = CryptoUtils.getDisplayAddress(it)
            updateSelectedAccount(it)
        }
    }

    private fun updateSelectedAccount(publicKey: String) {
        val mFragmentTransaction =
            supportFragmentManager.beginTransaction().setCustomAnimations(0, 0, 0, 0)
        mFragmentTransaction?.addToBackStack(null)
        mFragmentTransaction?.replace(
            R.id.fragment_placeholder,
            WalletHomeFragment.getInstance(publicKey)
        )
            ?.commit()
        fragmentTag = 0
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (KeyEvent.KEYCODE_BACK == keyCode) {
            if (0 == fragmentTag)
                finish()
            else
                initWalletHomeFragment()
        }
        return true
    }
}