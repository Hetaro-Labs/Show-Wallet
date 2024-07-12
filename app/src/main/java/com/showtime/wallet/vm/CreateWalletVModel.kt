package com.showtime.wallet.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.amez.mall.lib_base.base.mvvm.vm.BaseViewModel

/**
 * CreateWallet Related business VM
 */
class CreateWalletVModel : BaseViewModel() {

    // Private variable LiveData
    private val _createLiveData = MutableLiveData<Boolean>()

    // Externally exposed and immutable LiveData
    val createLiveData: LiveData<Boolean> = _createLiveData


    /**
     * crate Wallet Pseudocode
     */
    fun createWallet(){
        //Pseudocode
        /**
        launchOnlyResult({ "Backend interface getData" }, {
            _createLiveData.postValue(it)
        }, {
            defUI.toastEvent.postValue("${it.code}: ${it.errMsg}")
        } ,{},true)**/
    }
}