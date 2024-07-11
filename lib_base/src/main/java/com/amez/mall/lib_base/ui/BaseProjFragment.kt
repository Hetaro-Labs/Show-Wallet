package com.amez.mall.lib_base.ui

import androidx.databinding.ViewDataBinding
import com.amez.mall.lib_base.base.mvvm.v.BaseFrameFragment
import com.amez.mall.lib_base.base.mvvm.vm.BaseViewModel

/**
 * Describe:Fragment base classes related to the project
 * Created by:Sky
 * Created time:
 */
abstract class BaseProjFragment<VB : ViewDataBinding, VM : BaseViewModel> : BaseFrameFragment<VB, VM>()