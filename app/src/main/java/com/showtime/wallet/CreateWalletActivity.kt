package com.showtime.wallet

import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
import com.amez.mall.lib_base.ui.BaseProjActivity
import com.amez.mall.lib_base.utils.MmkvUtils
import com.showtime.wallet.adapter.MnemonicAdapter
import com.showtime.wallet.data.Ed25519KeyRepositoryNew
import com.showtime.wallet.databinding.ActivityGeneratePhraseBinding
import com.showtime.wallet.utils.AppConstants
import com.showtime.wallet.utils.CryptoUtils
import com.showtime.wallet.utils.clickNoRepeat
import com.showtime.wallet.vm.CreateWalletVModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.bouncycastle.crypto.AsymmetricCipherKeyPair
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters


class CreateWalletActivity : BaseProjActivity<ActivityGeneratePhraseBinding, CreateWalletVModel>() {

    override fun getBundleExtras(extras: Bundle?) {
    }

    override fun getContentViewLayoutID() = R.layout.activity_generate_phrase

    override fun ActivityGeneratePhraseBinding.initView() {
        val mnemonic = CryptoUtils.generateMnemonic()
        //1. display mnemonic in recyclerview
        val adapter = MnemonicAdapter(this@CreateWalletActivity, mnemonic)
        mBinding.recyclerView.layoutManager = GridLayoutManager(this@CreateWalletActivity, 2)
        mBinding.recyclerView.adapter = adapter

        val privateKey = CryptoUtils.mnemonicToPrivateKeyBytes(mnemonic)
        val privateKeyParams = Ed25519PrivateKeyParameters(privateKey, 0)
        val keypair =
            AsymmetricCipherKeyPair(privateKeyParams.generatePublicKey(), privateKeyParams)

        btnNext.clickNoRepeat {
            //3. go to WalletActivity and finish page
            GlobalScope.launch(Dispatchers.IO) {

                val publicKey = CryptoUtils.keypairToPublicKey(keypair)
                Log.e("CreateWalletActivity", publicKey)

                if (Ed25519KeyRepositoryNew.getAll().isNullOrEmpty()) {
                    MmkvUtils.put(AppConstants.SELECTED_PUBLIC_KEY, publicKey)
                }
                //2. insert to db, see @Ed25519KeyRepository
                Ed25519KeyRepositoryNew.insertOne(keypair)

                openActivity(WalletActivity::class.java, true)
            }
        }
    }

    override fun initLiveDataObserve() {
        mViewModel.createLiveData.observeForever {
            //callback result,UI related operations
        }

    }

    override fun initRequestData() {
        //initialization get Request Data
        mViewModel.createWallet()
    }

}