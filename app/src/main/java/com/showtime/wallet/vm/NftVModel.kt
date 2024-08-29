package com.showtime.wallet.vm

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.amez.mall.lib_base.base.mvvm.vm.BaseViewModel
import com.amez.mall.lib_base.bean.GetAssetsByOwnerResp
import com.amez.mall.lib_base.net.ApiRequest

class NFTVModel :BaseWalletVModel() {

    private val TAG = NFTVModel::class.simpleName
    private val _getAssetsByOwner = MutableLiveData<GetAssetsByOwnerResp>()
    val getAssetsByOwner: MutableLiveData<GetAssetsByOwnerResp> = _getAssetsByOwner

    fun getAssetsByOwner(ownerAddress:String){
        ApiRequest.getAssetsByOwner(ownerAddress){
            Log.d(TAG,"getAssetsByOwner=${it}")
            _getAssetsByOwner.postValue(it)
        }
    }
}