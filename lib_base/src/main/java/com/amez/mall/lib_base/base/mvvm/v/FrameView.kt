package com.amez.mall.lib_base.base.mvvm.v

import androidx.databinding.ViewDataBinding

/**
 * Describe:View layer base class abstraction
 */
interface FrameView <VB : ViewDataBinding> {
    /**
     * Initialize View
     */
    fun VB.initView()

    /**
     * Initialize the subscription relationship for LiveData
     */
    fun initLiveDataObserve()

    /**
     * Data requests during initialization interface creation
     */
    fun initRequestData()
}