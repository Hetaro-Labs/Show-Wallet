package com.showtime.wallet

import android.os.Bundle
import androidx.databinding.ViewDataBinding
import com.amez.mall.lib_base.ui.BaseProjNotMVVMFragment
import com.showtime.wallet.utils.AppConstants

abstract class BaseSecondaryNotMVVMFragment<VB : ViewDataBinding> : BaseProjNotMVVMFragment<VB>(){

    protected lateinit var key: String

    override fun getBundleExtras(extras: Bundle) {
        key = extras.getString(AppConstants.SELECTED_PUBLIC_KEY, "")
    }

}