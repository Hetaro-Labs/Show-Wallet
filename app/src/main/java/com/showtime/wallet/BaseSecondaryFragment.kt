package com.showtime.wallet

import android.os.Bundle
import androidx.databinding.ViewDataBinding
import com.amez.mall.lib_base.base.mvvm.v.BaseFrameFragment
import com.amez.mall.lib_base.base.mvvm.vm.BaseViewModel
import com.amez.mall.lib_base.ui.BaseProjFragment
import com.showtime.wallet.utils.AppConstants
import com.showtime.wallet.vm.BaseWalletVModel

abstract class BaseSecondaryFragment<VB : ViewDataBinding, VM : BaseWalletVModel> : BaseProjFragment<VB, VM>(){

    protected lateinit var key: String

    override fun getBundleExtras(extras: Bundle) {
        key = extras.getString(AppConstants.SELECTED_PUBLIC_KEY, "")

        mViewModel.initAccount(key)
    }

}