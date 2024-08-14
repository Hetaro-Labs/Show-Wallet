package com.amez.mall.lib_base.ui

import androidx.databinding.ViewDataBinding
import com.amez.mall.lib_base.base.mvvm.v.BaseFrameFragment
import com.amez.mall.lib_base.base.mvvm.vm.BaseViewModel
import com.amez.mall.lib_base.utils.Logger

/**
 * Describe:Fragment base classes related to the project
 * Created by:DK
 * Created time:
 */
abstract class BaseProjFragment<VB : ViewDataBinding, VM : BaseViewModel> : BaseFrameFragment<VB, VM>(){
    protected fun log(msg: String){
        Logger.d(javaClass.simpleName, msg)
    }
}