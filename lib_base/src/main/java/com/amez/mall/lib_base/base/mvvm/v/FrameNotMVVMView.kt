package com.amez.mall.lib_base.base.mvvm.v

import androidx.databinding.ViewDataBinding

/**
 * Describe:View layer base class abstraction
 * Created by:DK
 * Created time:
 */
interface FrameNotMVVMView<VB : ViewDataBinding> {
    /**
     * Initialize View
     */
    fun VB.initView()
}