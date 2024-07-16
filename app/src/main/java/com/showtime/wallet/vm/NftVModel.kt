package com.showtime.wallet.vm

import android.util.Log
import com.amez.mall.lib_base.base.mvvm.vm.BaseViewModel
import com.solana.core.PublicKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.sol4k.Connection
import org.sol4k.RpcUrl


class NftVModel :BaseViewModel() {

    private val TAG = NftVModel::class.simpleName

    private fun findMetadataAccountAddress(mint: PublicKey): String {
        val metadataProgramId = PublicKey("metaqbxxUerdq28cj1RbAWkYQm3ybzjb6a8bt518x1s")
        val seeds = listOf(
            "metadata".toByteArray(),
            metadataProgramId.toByteArray(),
            mint.toByteArray()
        )

        return PublicKey.findProgramAddress(seeds, metadataProgramId).address.toBase58()
    }

    fun bytesToBinary(bytes: ByteArray): String {
        val binaryBuilder = StringBuilder()
        for (b in bytes) {
            var `val` = b.toInt()
            for (i in 0..7) {
                binaryBuilder.append(if ((`val` and 128) == 0) 0 else 1)
                `val` = `val` shl 1
            }
        }
        return binaryBuilder.toString()
    }

    fun getAccountInfo() {
        GlobalScope.launch(Dispatchers.IO) {
            val response = async {
                val connection = Connection(RpcUrl.MAINNNET)
                connection.getAccountInfo(org.sol4k.PublicKey("metaqbxxUerdq28cj1RbAWkYQm3ybzjb6a8bt518x1s"))
            }
            val result = response.await()
            Log.d(TAG,"accountInfo=${result}")


        }


    }
}