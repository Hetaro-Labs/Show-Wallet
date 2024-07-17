package com.showtime.wallet.vm

import androidx.lifecycle.MutableLiveData
import com.amez.mall.lib_base.base.mvvm.vm.BaseViewModel
import com.amez.mall.lib_base.bean.TokenPairUpdatedResp
import com.amez.mall.lib_base.net.ApiRequest

class SwapVModel :BaseViewModel() {

    private val _getTokenPairUpdated = MutableLiveData<TokenPairUpdatedResp>()
    val getTokenPairUpdated: MutableLiveData<TokenPairUpdatedResp> = _getTokenPairUpdated

    fun getTokenPairUpdated(parameter1: String,parameter2: String,parameter3: Int){
        ApiRequest.getTokenPairUpdated(parameter1,parameter2,parameter3){
            _getTokenPairUpdated.postValue(it)
        }
    }

    enum class TokenTypeEnum(val value: String){
        TOKEN1("token1"),
        TOKEN2("token2")
    }
}