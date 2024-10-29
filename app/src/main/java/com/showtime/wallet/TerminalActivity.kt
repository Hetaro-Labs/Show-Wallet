package com.showtime.wallet

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.amez.mall.lib_base.base.mvvm.v.BaseFrameNotMVVMActivity
import com.showtime.wallet.databinding.ActivityTerminalBinding
import com.showtime.wallet.demo.AssistanceFragment
import com.showtime.wallet.demo.DemoFragment
import com.showtime.wallet.demo.RunningAppFragment
import com.showtime.wallet.demo.SignTransactionFragment
import com.showtime.wallet.demo.TokenPriceChangeFragment
import com.showtime.wallet.utils.AppConstants
import com.showtime.wallet.utils.clickNoRepeat

class TerminalActivity : BaseFrameNotMVVMActivity<ActivityTerminalBinding>() {

    companion object {
        enum class FragmentTypeEnum(val value: String) {
            SCAN("SCAN QR CODE"),
            SEND_DETAIL("SEND"),
            SEND_CONFIRMATION("CONFIRMATION"),
            SWAP_CONFIRMATION("SWAP CONFIRM"),
            SEARCH("SEARCH"),
            RECEIVE("RECEIVE"),
            SWAP("SWAP"),
            IMPORT_KEY("IMPORT"),
            NFT("NFT"),
            MINT_NFT("MINT NFT"),
            NFT_DETAIL("NFT DETAIL"),
            TRANSACTION("HISTORY"),
            TRANSACTION_DETAIL("TRANSACTION"),
            STATUS("TRANSACTION STATUS"),
            ASSISTANCE("SHOW AI"),
            TOKEN_PRICE_ALERT("PRICE ALERT"),
            DEMO_RUNNING_APP("RUNNING MATE"),
            DEMO_SIGN_TRANSACTION("SIGN TRANSACTION"),
            DEMO("DEMOS"),
        }

        fun start(
            context: Context,
            fragment: FragmentTypeEnum,
            key: String,
            bundle: Bundle? = null
        ) {
            val intent = Intent(context, TerminalActivity::class.java)
            intent.putExtra(AppConstants.FRAGMENT, fragment.value)
            intent.putExtra(AppConstants.SELECTED_PUBLIC_KEY, key)
            bundle?.let {
                intent.putExtras(it)
            }
            context.startActivity(intent)
        }
    }

    override fun getBundleExtras(extras: Bundle?) {
    }

    override fun getContentViewLayoutID(): Int {
        return R.layout.activity_terminal
    }

    fun setTitle(title: String){
        mBinding.tvTitle.text = title
    }

    override fun ActivityTerminalBinding.initView() {
        layoutTitleBar.clickNoRepeat {
            finish()
        }

        val fragmentType = intent.getStringExtra("fragment")
        tvTitle.text = fragmentType

        val fragment = when (fragmentType) {
            FragmentTypeEnum.SWAP_CONFIRMATION.value -> SwapConfirmFragment()
            FragmentTypeEnum.SEARCH.value -> SearchTokenFragment()
            FragmentTypeEnum.SEND_DETAIL.value -> SendTokenDetailFragment()
            FragmentTypeEnum.SEND_CONFIRMATION.value -> SendTokenConfirmationFragment()
            FragmentTypeEnum.RECEIVE.value -> ReceiveFragment()
            FragmentTypeEnum.SWAP.value -> SwapFragment()
            FragmentTypeEnum.NFT.value -> NFTFragment()
            FragmentTypeEnum.NFT_DETAIL.value -> NFTDetailFragment()
            FragmentTypeEnum.TRANSACTION.value -> TransactionHistoryFragment()
            FragmentTypeEnum.TRANSACTION_DETAIL.value -> TransactionDetailFragment()
            FragmentTypeEnum.STATUS.value -> TransactionStatusFragment()
            FragmentTypeEnum.SCAN.value -> ScanQRFragment()
            FragmentTypeEnum.IMPORT_KEY.value -> ImportPrivateKeyFragment()
            FragmentTypeEnum.MINT_NFT.value -> MintNFTFragment()
            FragmentTypeEnum.ASSISTANCE.value -> AssistanceFragment()
            FragmentTypeEnum.TOKEN_PRICE_ALERT.value -> TokenPriceChangeFragment()
            FragmentTypeEnum.DEMO_RUNNING_APP.value -> RunningAppFragment()
            FragmentTypeEnum.DEMO_SIGN_TRANSACTION.value -> SignTransactionFragment()
            FragmentTypeEnum.DEMO.value -> DemoFragment()
            else -> throw IllegalArgumentException("Invalid fragment type")
        }

        fragment.arguments = intent.extras

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_placeholder, fragment) // Replace with your fragment container ID
            .commit()
    }

}