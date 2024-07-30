package com.amez.mall.lib_base.ui

import androidx.databinding.ViewDataBinding
import com.amez.mall.lib_base.base.mvvm.v.BaseFrameNotMVVMFragment
import com.amez.mall.lib_base.utils.Logger

/**
 * Describe:Not a base class for MVVM pattern
 */
abstract class BaseProjNotMVVMFragment<VB : ViewDataBinding> : BaseFrameNotMVVMFragment<VB>(){
    protected fun log(msg: String){
        Logger.d(javaClass.simpleName, msg)
    }
}