package com.showtime.wallet

import android.content.Context
import android.os.Bundle
import com.amez.mall.lib_base.base.mvvm.v.BaseFrameFragment
import com.amez.mall.lib_base.base.mvvm.v.BaseFrameNotMVVMFragment
import com.amez.mall.lib_base.utils.MmkvUtils
import com.showtime.wallet.data.Ed25519KeyRepositoryNew
import com.showtime.wallet.databinding.FragmentImportPrivateKeyBinding
import com.showtime.wallet.utils.AppConstants
import com.showtime.wallet.utils.CryptoUtils
import com.showtime.wallet.utils.clickNoRepeat
import com.showtime.wallet.vm.CreateWalletVModel


class ImportPrivateKeyFragment: BaseFrameFragment<FragmentImportPrivateKeyBinding, CreateWalletVModel>() {

    companion object{
        fun start(context: Context){
            TerminalActivity.start(
                context,
                TerminalActivity.Companion.FragmentTypeEnum.IMPORT_KEY,
                ""
            )
        }
    }

    override fun getBundleExtras(extras: Bundle) {
    }

    override fun getContentViewLayoutID(): Int {
        return R.layout.fragment_import_private_key
    }

    override fun initLiveDataObserve() {
        mViewModel.createLiveData.observe(viewLifecycleOwner){
            openActivity(WalletActivity::class.java, true)
        }
    }

    override fun initRequestData() {
    }

    override fun FragmentImportPrivateKeyBinding.initView() {
        btnImport.clickNoRepeat{
            val privateKey = privateKeyInput.text.toString()
            mViewModel.importWallet(privateKey)
        }
    }

}