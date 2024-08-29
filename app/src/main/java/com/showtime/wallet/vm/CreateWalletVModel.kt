package com.showtime.wallet.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.amez.mall.lib_base.base.mvvm.vm.BaseViewModel
import com.amez.mall.lib_base.utils.MmkvUtils
import com.showtime.wallet.data.Ed25519KeyRepositoryNew
import com.showtime.wallet.utils.AppConstants
import com.showtime.wallet.utils.CryptoUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.bouncycastle.crypto.AsymmetricCipherKeyPair
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters

/**
 * CreateWallet Related business VM
 */
class CreateWalletVModel : BaseViewModel() {

    // Private variable LiveData
    private val _createLiveData = MutableLiveData<Boolean>()

    // Externally exposed and immutable LiveData
    val createLiveData: LiveData<Boolean> = _createLiveData

    fun importWallet(privateKey: String) {
        viewModelScope.launch {
            val decodedBytes = org.sol4k.Base58.decode(privateKey)
            val privateKeyParams = Ed25519PrivateKeyParameters(decodedBytes, 0)
            val keypair =
                AsymmetricCipherKeyPair(privateKeyParams.generatePublicKey(), privateKeyParams)
            val publicKey = CryptoUtils.convertKeypair(keypair).publicKey.toBase58()

            withContext(Dispatchers.IO) {
                Ed25519KeyRepositoryNew.insertOne(keypair)
                MmkvUtils.put(AppConstants.SELECTED_PUBLIC_KEY, publicKey)

                _createLiveData.postValue(true)
            }
        }

    }

    fun createWallet(keypair: AsymmetricCipherKeyPair) {
        viewModelScope.launch {
            val publicKey = CryptoUtils.keypairToPublicKey(keypair)
            log(publicKey)

            withContext(Dispatchers.IO) {
                if (Ed25519KeyRepositoryNew.getAll().isNullOrEmpty()) {
                    MmkvUtils.put(AppConstants.SELECTED_PUBLIC_KEY, publicKey)
                }

                Ed25519KeyRepositoryNew.insertOne(keypair)
                _createLiveData.postValue(true)
            }
        }
    }

}