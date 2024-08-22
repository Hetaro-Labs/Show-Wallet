package com.showtime.wallet

import android.content.Context
import android.os.Bundle
import com.amez.mall.lib_base.base.mvvm.v.BaseFrameNotMVVMFragment
import com.amez.mall.lib_base.utils.MmkvUtils
import com.showtime.wallet.data.Ed25519KeyRepositoryNew
import com.showtime.wallet.databinding.FragmentImportPrivateKeyBinding
import com.showtime.wallet.utils.AppConstants
import com.showtime.wallet.utils.CryptoUtils
import com.showtime.wallet.utils.clickNoRepeat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.bouncycastle.crypto.AsymmetricCipherKeyPair
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters

class ImportPrivateKeyFragment: BaseFrameNotMVVMFragment<FragmentImportPrivateKeyBinding>() {

    companion object{
        fun start(context: Context){
            TerminalActivity.start(
                context,
                TerminalActivity.Companion.FragmentTypeEnum.IMPORT_KEY,
                ""
            )
        }
    }

    override fun getBundleExtras(extras: Bundle) {

    }

    override fun getContentViewLayoutID(): Int {
        return R.layout.fragment_import_private_key
    }

    override fun FragmentImportPrivateKeyBinding.initView() {
        btnImport.clickNoRepeat{
            val privateKey = privateKeyInput.text.toString()

            val decodedBytes = org.sol4k.Base58.decode(privateKey)
            val privateKeyParams = Ed25519PrivateKeyParameters(decodedBytes, 0)
            val keypair =
                AsymmetricCipherKeyPair(privateKeyParams.generatePublicKey(), privateKeyParams)
            val publicKey = CryptoUtils.convertKeypair(keypair).publicKey.toBase58()

            GlobalScope.launch(Dispatchers.IO) {
                Ed25519KeyRepositoryNew.insertOne(keypair)
                MmkvUtils.put(AppConstants.SELECTED_PUBLIC_KEY, publicKey)

                openActivity(WalletActivity::class.java, true)
            }
        }
    }

}