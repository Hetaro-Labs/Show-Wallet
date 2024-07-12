package com.amez.mall.lib_base.base.mvvm.v

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.amez.mall.lib_base.utils.EventBusRegister
import com.amez.mall.lib_base.utils.EventBusUtils

/**
 * Describe:Fragment base class that does not use MVVM
 */
abstract class BaseFrameNotMVVMFragment<VB : ViewDataBinding> : Fragment(), FrameNotMVVMView<VB> {

    protected lateinit var mBinding: VB

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var extras = arguments
        extras?.let { getBundleExtras(it) }
        if (getContentViewLayoutID() != 0) {
            mBinding= DataBindingUtil.inflate(inflater, getContentViewLayoutID(),container,false)
        } else {
            throw IllegalArgumentException("You must return a right contentView layout resource Id")
        }
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Register EventBus
        if (javaClass.isAnnotationPresent(EventBusRegister::class.java)) EventBusUtils.register(this)
        mBinding.initView()
    }

    override fun onDestroy() {
        if (javaClass.isAnnotationPresent(EventBusRegister::class.java)) EventBusUtils.unRegister(
            this
        )
        super.onDestroy()
    }

    protected abstract fun getBundleExtras(extras: Bundle?)

    protected abstract fun getContentViewLayoutID(): Int

    open fun openActivity(pClass: Class<*>?) {
        openActivity(pClass, null, false)
    }

    open fun openActivity(pClass: Class<*>?, isFinish: Boolean) {
        openActivity(pClass, null, isFinish)
    }

    open fun openActivity(pClass: Class<*>?, pBundle: Bundle?) {
        openActivity(pClass, pBundle, false)
    }

    open fun openActivity(
        pClass: Class<*>?,
        pBundle: Bundle?,
        isFinish: Boolean
    ) {
        val intent = Intent(context, pClass)
        if (pBundle != null) {
            intent.putExtras(pBundle)
        }
        startActivity(intent)
        if (isFinish) {
            activity?.finish()
        }
    }


    open fun openActivity(pAction: String?) {
        openActivity(pAction, null)
    }

    open fun openActivity(pAction: String?, pBundle: Bundle?) {
        val intent = Intent(pAction)
        if (pBundle != null) {
            intent.putExtras(pBundle)
        }
        startActivity(intent)
    }
}