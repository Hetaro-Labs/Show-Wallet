package com.amez.mall.lib_base.base.mvvm.v

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.amez.mall.lib_base.utils.EventBusRegister
import com.amez.mall.lib_base.utils.EventBusUtils
import com.amez.mall.lib_base.utils.Logger

/**
 * Describe:Activity base class that does not use MVVM
 * Created by:DK
 * Created time:
 */
abstract class BaseFrameNotMVVMActivity<VB : ViewDataBinding> : AppCompatActivity(),
    FrameNotMVVMView<VB> {

    protected lateinit var mBinding: VB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var extras = intent.extras
        extras?.let { getBundleExtras(it) }
        if (getContentViewLayoutID() != 0) {
            mBinding = DataBindingUtil.setContentView(this, getContentViewLayoutID())
        } else {
            throw IllegalArgumentException("You must return a right contentView layout resource Id")
        }
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
        val intent = Intent(this, pClass)
        if (pBundle != null) {
            intent.putExtras(pBundle)
        }
        startActivity(intent)
        if (isFinish) {
            finish()
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

    fun log(msg: String){
        Logger.d(javaClass.simpleName, msg)
    }

}