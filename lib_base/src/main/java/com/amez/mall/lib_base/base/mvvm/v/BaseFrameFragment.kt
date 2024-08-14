package com.amez.mall.lib_base.base.mvvm.v

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.amez.mall.lib_base.base.mvvm.vm.BaseViewModel
import com.amez.mall.lib_base.utils.BindingReflex
import com.amez.mall.lib_base.utils.EventBusRegister
import com.amez.mall.lib_base.utils.EventBusUtils
import com.kongzue.dialog.v2.WaitDialog

/**
 * Describe:Fragment base class is project independent
 * Created by:DK
 * Created time:
 */
abstract class BaseFrameFragment<VB : ViewDataBinding, VM : BaseViewModel> : Fragment(), FrameView<VB> {


    protected lateinit var mBinding: VB

    protected val mViewModel: VM by lazy(mode = LazyThreadSafetyMode.NONE) {
        BindingReflex.reflexViewModel(javaClass, this) as VM
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        arguments?.let { getBundleExtras(it) }
        if (getContentViewLayoutID() != 0) {
            mBinding= DataBindingUtil.inflate(inflater, getContentViewLayoutID(),container,false)
        } else {
            throw IllegalArgumentException("You must return a right contentView layout resource Id")
        }
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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

    protected abstract fun getBundleExtras(extras: Bundle)

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

    /**
     * Register UI events
     */
    private fun registorDefUIChange() {
        mViewModel.defUI.showDialog.observe(viewLifecycleOwner, Observer { t ->
            WaitDialog.show(activity,"Requesting...")
        })
        mViewModel.defUI.dismissDialog.observe(viewLifecycleOwner, Observer { t ->
            WaitDialog.dismiss()
        })
        mViewModel.defUI.toastEvent.observe(viewLifecycleOwner, Observer { t ->
            Toast.makeText(activity,t,Toast.LENGTH_SHORT).show()

        })
        mViewModel.defUI.msgEvent.observe(viewLifecycleOwner, Observer { t ->

        })
    }

}