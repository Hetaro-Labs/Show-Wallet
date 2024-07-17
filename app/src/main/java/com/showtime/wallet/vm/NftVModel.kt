package com.showtime.wallet.vm

import android.util.Log
import com.amez.mall.lib_base.base.mvvm.vm.BaseViewModel
import com.showtime.wallet.net.AppConnection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.sol4k.RpcUrl


class NftVModel :BaseViewModel() {

    private val TAG = NftVModel::class.simpleName


    fun getAccountInfo() {
        GlobalScope.launch(Dispatchers.IO) {
            val response = async {
                val connection = AppConnection(RpcUrl.MAINNNET)
                connection.getAccountInfo("metaqbxxUerdq28cj1RbAWkYQm3ybzjb6a8bt518x1s")
            }
            val result = response.await()
            Log.d(TAG,"accountInfo=${result}")
        }


    }
}