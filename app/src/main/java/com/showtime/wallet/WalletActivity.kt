package com.showtime.wallet

import android.os.Bundle
import androidx.fragment.app.FragmentTransaction
import com.amez.mall.lib_base.ui.BaseProjActivity
import com.amez.mall.lib_base.utils.MmkvUtils
import com.showtime.wallet.databinding.ActivityWalletBinding
import com.showtime.wallet.utils.AppConstants
import com.showtime.wallet.utils.clickNoRepeat
import com.showtime.wallet.vm.WalletHomeVModel

class WalletActivity : BaseProjActivity<ActivityWalletBinding, WalletHomeVModel>() {

    private var selectedPublicKey: String = ""
    private var mFragmentTransaction: FragmentTransaction? = null

    override fun getBundleExtras(extras: Bundle?) {
    }

    override fun getContentViewLayoutID() = R.layout.activity_wallet

    override fun initLiveDataObserve() {
    }

    override fun initRequestData() {
    }

    override fun ActivityWalletBinding.initView() {
        selectedPublicKey = "3W1bVgaXPnhbbU8pWuSjBt7EZCcZ49XY9rzERt8U4piH"//MmkvUtils.getString(AppConstants.SELECTED_PUBLIC_KEY)
        selectedPublicKey?.let {
            if (it.length > 5) tvAccountName.text = it.substring(6)
            updateSelectedAccount(it)
        }

        layoutAccountSelection.clickNoRepeat {
            mViewModel.showAccountPopWindow(this@WalletActivity, layoutAccountSelection) {
                tvAccountName.text = it.substring(6)
                updateSelectedAccount(it)
            }
        }
    }

    private fun updateSelectedAccount(publicKey: String) {
        if (null == mFragmentTransaction)
            mFragmentTransaction =
                supportFragmentManager.beginTransaction().setCustomAnimations(0, 0, 0, 0)
        mFragmentTransaction?.replace(
            R.id.fragment_placeholder,
            WalletHomeFragment.getInstance(publicKey)
        )
            ?.commit()
    }

    fun changeFrag(enum:WalletHomeVModel.FragmentTypeEnum){
        val mFragmentTransaction = supportFragmentManager.beginTransaction().setCustomAnimations(0, 0, 0, 0)
        when(enum.value){
            WalletHomeVModel.FragmentTypeEnum.SWAP.value->{
                mFragmentTransaction.replace(R.id.fragment_placeholder, SwapFragment(selectedPublicKey)).commit()
            }
            WalletHomeVModel.FragmentTypeEnum.NFT.value->{
                mFragmentTransaction.replace(R.id.fragment_placeholder, NFTFragment(selectedPublicKey)).commit()
            }
            WalletHomeVModel.FragmentTypeEnum.TRANSACTION.value->{
                mFragmentTransaction.replace(R.id.fragment_placeholder, TransactionHistoryFragment(selectedPublicKey)).commit()
            }
        }
    }


}