package com.showtime.wallet.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.amez.mall.lib_base.base.mvvm.vm.BaseViewModel
import com.showtime.wallet.data.Ed25519KeyRepositoryNew
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.sol4k.Keypair

open class BaseWalletVModel: BaseViewModel(){

    protected val _getTransactionHash = MutableLiveData<String>()
    val getTransactionHash: MutableLiveData<String> = _getTransactionHash

    protected val _getTransactionError = MutableLiveData<String>()
    val getTransactionError: MutableLiveData<String> = _getTransactionError

    protected lateinit var myAccount: Keypair

    fun initAccount(publicKey: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                Ed25519KeyRepositoryNew.getByPublicKey(publicKey)
            }?.let {
                myAccount = it
            }
        }
    }

}