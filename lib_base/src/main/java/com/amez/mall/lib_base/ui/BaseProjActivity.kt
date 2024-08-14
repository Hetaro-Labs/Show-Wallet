package com.amez.mall.lib_base.ui

import androidx.databinding.ViewDataBinding
import com.amez.mall.lib_base.base.mvvm.v.BaseFrameActivity
import com.amez.mall.lib_base.base.mvvm.vm.BaseViewModel

/**
 * Describe:Project related activity base classes
 * Created by:DK
 * Created time:
 */
abstract class BaseProjActivity<VB : ViewDataBinding, VM : BaseViewModel> : BaseFrameActivity<VB, VM>()