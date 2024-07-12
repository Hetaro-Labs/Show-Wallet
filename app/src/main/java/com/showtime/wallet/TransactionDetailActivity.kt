package com.showtime.wallet

import android.os.Bundle
import com.amez.mall.lib_base.ui.BaseProjNotMVVMActivity
import com.showtime.wallet.databinding.ActivityTransactionDetailBinding

class TransactionDetailActivity : BaseProjNotMVVMActivity<ActivityTransactionDetailBinding>(){

    override fun getBundleExtras(extras: Bundle?) {
    }

    override fun getContentViewLayoutID() = R.layout.activity_transaction_detail

    override fun ActivityTransactionDetailBinding.initView() {
    }
}