package com.showtime.wallet.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.amez.mall.lib_base.bean.TransactionsData
import com.amez.mall.lib_base.utils.ImageHelper
import com.showtime.wallet.R
import com.showtime.wallet.TransactionDetailActivity
import com.showtime.wallet.utils.AppConstants
import com.showtime.wallet.utils.TokenListCache
import com.showtime.wallet.utils.clickNoRepeat
import kotlin.math.pow

class TransactionHistoryAdapter(
    private var mContext: Context,
    private val mList: List<TransactionsData>,
    private val myAccount: String
) : RecyclerView.Adapter<TransactionHistoryAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView: View =
            LayoutInflater.from(mContext).inflate(R.layout.layout_transaction, parent, false)
        val holder = MyViewHolder(itemView)
        return holder
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val bean = mList[position]

        holder.tvTransactionType.text = if (bean.source == myAccount)
            "sent"
        else
            if (bean.destination == myAccount) "receive" else ""
        bean.showTransactionType = holder.tvTransactionType.text.toString()
        if (bean.token.isNullOrEmpty()) {
            holder.iconToken.setImageResource(R.drawable.ic_solana)
            holder.tvAmount.text = "${bean.amount / (10.toDouble().pow(8))} SOL"
            bean.showUrl = ""
        } else {
            val token = TokenListCache.getList().findLast { it.mint == bean.token }
            token?.let {
                ImageHelper.obtainImage(mContext, it.logo, holder.iconToken)
                holder.tvAmount.text = "" + it.uiAmount + " " + it.symbol
                bean.showUrl = it.logo
            }
        }
        bean.showAmount = holder.tvAmount.text.toString()
        holder.tvAddress.text = bean.source
        holder.itemView.clickNoRepeat {
            val intent = Intent(mContext, TransactionDetailActivity::class.java)
            intent.putExtra(AppConstants.KEY, bean)
            mContext.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return mList.size
    }

    // ViewHolder Reuse Component
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val iconToken: ImageView = itemView.findViewById(R.id.icon_token)
        val iconTransactionType: ImageView = itemView.findViewById(R.id.icon_transaction_type)
        val tvTransactionType: TextView = itemView.findViewById(R.id.tv_transaction_type)
        val tvAddress: TextView = itemView.findViewById(R.id.tv_address)
        val tvAmount: TextView = itemView.findViewById(R.id.tv_amount)

    }
}