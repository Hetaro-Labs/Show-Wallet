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
import org.bouncycastle.crypto.AsymmetricCipherKeyPair
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters


class CreateWalletActivity : BaseProjActivity<ActivityGeneratePhraseBinding, CreateWalletVModel>() {

    override fun getBundleExtras(extras: Bundle?) {
    }

    override fun initRequestData() {
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
        val keypair = AsymmetricCipherKeyPair(privateKeyParams.generatePublicKey(), privateKeyParams)

        btnNext.clickNoRepeat {
            mViewModel.createWallet(keypair)
        }
    }

    override fun initLiveDataObserve() {
        mViewModel.createLiveData.observeForever {
            openActivity(WalletActivity::class.java, true)
        }
    }

}