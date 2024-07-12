package com.showtime.wallet

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.amez.mall.lib_base.ui.BaseProjFragment
import com.showtime.wallet.adapter.TokenAccountsByOwnerAdapter
import com.showtime.wallet.databinding.FragmentWalletHomeBinding
import com.showtime.wallet.utils.AppConstants
import com.showtime.wallet.vm.WalletHomeVModel
import org.sol4k.PublicKey
import java.util.ArrayList

class WalletHomeFragment : BaseProjFragment<FragmentWalletHomeBinding, WalletHomeVModel>() {

    companion object {
        fun getInstance(publicKey: String): WalletHomeFragment {
            val fragment = WalletHomeFragment()
            val args = Bundle()
            args.putString("publicKey", publicKey)
            fragment.arguments = args
            return fragment
        }
    }

    private var selectedPublicKey: String? = null
    private val TAG = WalletHomeFragment::class.simpleName

    override fun getBundleExtras(extras: Bundle?) {
    }

    override fun getContentViewLayoutID() = R.layout.fragment_wallet_home

    override fun FragmentWalletHomeBinding.initView() {
        selectedPublicKey = arguments?.getString("publicKey")
        Log.e(TAG, "selectedPublicKey:${selectedPublicKey}")

        mViewModel.getBalance(PublicKey(selectedPublicKey ?: ""))

        //mViewModel.getTokenAccountsByOwner(PublicKey(selectedPublicKey ?: ""))
        mViewModel.getTokenAccountsByOwner(PublicKey("EjAX2KePXZEZEaADMVc5UT2SQDvBYfoP1Jyx7frignFX")) //test key


        mBinding.btnReceive.setOnClickListener {
            val mBundle = Bundle()
            mBundle.putString(AppConstants.KEY, selectedPublicKey)
            openActivity(ReceiveActivity::class.java, mBundle)
        }

        mBinding.btnSend.setOnClickListener {
            val intent= Intent(requireActivity(),SendTokenListActivity::class.java)
            intent.putParcelableArrayListExtra(AppConstants.KEY, mViewModel.getTokens.value as ArrayList)
            requireActivity().startActivity(intent)
        }

        mBinding.btnSwap.setOnClickListener {
            //TODO start a default activity that holds SwapFragment
            (requireActivity() as WalletActivity).changeFrag(WalletHomeVModel.FragmentTypeEnum.SWAP)
        }

        mBinding.btnNft.setOnClickListener {
            //TODO start a default activity that holds NFTFragment
            (requireActivity() as WalletActivity).changeFrag(WalletHomeVModel.FragmentTypeEnum.NFT)
        }

        mBinding.btnTransactions.setOnClickListener {
            (requireActivity() as WalletActivity).changeFrag(WalletHomeVModel.FragmentTypeEnum.TRANSACTION)
        }

    }

    @SuppressLint("SetTextI18n")
    override fun initLiveDataObserve() {
        mViewModel.getBlanceLiveData.observeForever { mBinding.tvBalance.text = "balance:${it}" }

        mViewModel.getTokens.observeForever {
            val adapter = TokenAccountsByOwnerAdapter(requireActivity(), it)
            mBinding.rvTokenList.adapter = adapter
            //TODO for each item onClick, call SendTokenDetailActivity.start(content, item)
            //OnClick is bound in TokenAccountsByOwnerAdapter
        }
    }

    override fun initRequestData() {
    }

}