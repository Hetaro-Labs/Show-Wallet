package com.amez.mall.lib_base.base.mvvm.v

import androidx.databinding.ViewDataBinding

/**
 * Describe:View layer base class abstraction
 */
interface FrameNotMVVMView<VB : ViewDataBinding> {
    /**
     * Initialize View
     */
    fun VB.initView()
}