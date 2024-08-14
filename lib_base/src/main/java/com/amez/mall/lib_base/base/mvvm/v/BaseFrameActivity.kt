package com.amez.mall.lib_base.base.mvvm.v

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import com.amez.mall.lib_base.base.mvvm.vm.BaseViewModel
import com.amez.mall.lib_base.utils.BindingReflex
import com.amez.mall.lib_base.utils.EventBusRegister
import com.amez.mall.lib_base.utils.EventBusUtils
import com.amez.mall.lib_base.utils.Logger
import com.kongzue.dialog.v2.WaitDialog

/**
 * Describe:Activity base class is not related to the project
 * Created by:DK
 * Created time:
 */
abstract class BaseFrameActivity<VB : ViewDataBinding, VM : BaseViewModel> :
    AppCompatActivity(), FrameView<VB> {

    protected lateinit var mBinding: VB

    protected val mViewModel: VM by lazy(mode = LazyThreadSafetyMode.NONE) {
        BindingReflex.reflexViewModel(javaClass, this) as VM
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var extras = intent.extras
        extras?.let { getBundleExtras(it) }
        if (getContentViewLayoutID() != 0) {
            mBinding= DataBindingUtil.setContentView(this,getContentViewLayoutID())
        } else {
            throw IllegalArgumentException("You must return a right contentView layout resource Id")
        }
        lifecycle.addObserver(mViewModel)
        //Register UI events
        registorDefUIChange()
        // Register EventBus
        if (javaClass.isAnnotationPresent(EventBusRegister::class.java)) EventBusUtils.register(this)
        mBinding.initView()
        initLiveDataObserve()
        initRequestData()
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

    /**
     * Register UI events
     */
    private fun registorDefUIChange() {
        mViewModel.defUI.showDialog.observe(this, Observer { t ->
            WaitDialog.show(this,"Requesting...")
        })
        mViewModel.defUI.dismissDialog.observe(this, Observer { t ->
            WaitDialog.dismiss()
        })
        mViewModel.defUI.toastEvent.observe(this, Observer { t ->
            Toast.makeText(this,"BaseAct abnormal:"+t, Toast.LENGTH_LONG).show()
        })
        mViewModel.defUI.msgEvent.observe(this, Observer { t ->

        })
    }

    fun log(msg: String){
        Logger.d(javaClass.simpleName, msg)
    }
}