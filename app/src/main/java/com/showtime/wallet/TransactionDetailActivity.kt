package com.showtime.wallet

import android.os.Build
import android.os.Bundle
import com.amez.mall.lib_base.bean.TransactionsData
import com.amez.mall.lib_base.ui.BaseProjNotMVVMActivity
import com.amez.mall.lib_base.utils.DateUtils
import com.amez.mall.lib_base.utils.ImageHelper
import com.showtime.wallet.databinding.ActivityTransactionDetailBinding
import com.showtime.wallet.utils.AppConstants
import com.showtime.wallet.utils.clickNoRepeat


class TransactionDetailActivity : BaseProjNotMVVMActivity<ActivityTransactionDetailBinding>(){

    private lateinit var transactionsData: TransactionsData

    override fun getBundleExtras(extras: Bundle?) {
        transactionsData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            intent.getParcelableExtra(AppConstants.KEY, TransactionsData::class.java)!!
        else
            intent.getParcelableExtra(AppConstants.KEY)!!
    }

    override fun getContentViewLayoutID() = R.layout.activity_transaction_detail

    override fun ActivityTransactionDetailBinding.initView() {
        mBinding.let {
            closeButton.clickNoRepeat { finish() }
            titleText.text=transactionsData.showTransactionType
            if(transactionsData.showUrl.isNullOrEmpty())
                mainIcon.setImageResource(R.drawable.ic_solana)
            else
                ImageHelper.obtainImage(this@TransactionDetailActivity,transactionsData.showUrl?:"",mainIcon)
            amountText.text=transactionsData.showAmount
            tvTime.text= DateUtils.timestampToAmericanDate(transactionsData.timestamp?.toLong()?:System.currentTimeMillis())
            tvStatus.text=transactionsData.status
            tvFrom.text=transactionsData.source
            //TODO NetWork、view_on_solscan I don't know which field to take (logic)
        }
    }
}