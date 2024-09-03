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
import com.amez.mall.lib_base.utils.Logger
import com.showtime.wallet.DefaultTokenListData
import com.showtime.wallet.R
import com.showtime.wallet.TerminalActivity
import com.showtime.wallet.TransactionDetailFragment
import com.showtime.wallet.net.bean.ConvertedTransaction
import com.showtime.wallet.utils.AppConstants
import com.showtime.wallet.utils.CryptoUtils
import com.showtime.wallet.utils.TokenListCache
import com.showtime.wallet.utils.clickNoRepeat
import com.showtime.wallet.utils.gone
import com.showtime.wallet.utils.visible
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.math.pow

class TransactionHistoryAdapter(
    private var mContext: Context,
    val data: List<ConvertedTransaction>,
    private val key: String
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    //help me:
    //group data by data.item.timestamp
    private val sdf = SimpleDateFormat("yyyy-MM-dd")

    private val mList =
        data.groupBy { sdf.format((it.timestamp + "000").toDouble()) }
            .map { TransactionGroup(it.key, it.value) }

    companion object {
        private const val TYPE_GROUP = 0
        private const val TYPE_CHILD = 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (isGroupHeader(position)) TYPE_GROUP else TYPE_CHILD
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder.itemViewType == TYPE_GROUP) {
            (holder as GroupViewHolder).bind(mList[getGroupPosition(position)])
        } else {
            val (groupPos, childPos) = getChildPosition(position)
            (holder as ChildViewHolder).bind(mList[groupPos].items[childPos])
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_GROUP) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_transaction_date, parent, false)
            GroupViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_transaction, parent, false)
            ChildViewHolder(view)
        }
    }

    override fun getItemCount(): Int {
        // Total count of groups and children
        return mList.sumOf { it.items.size + 1 }
    }

    private fun isGroupHeader(position: Int): Boolean {
        var count = 0
        for (group in mList) {
            if (position == count) return true
            count += group.items.size + 1
        }
        return false
    }

    private fun getGroupPosition(position: Int): Int {
        var count = 0
        for (i in mList.indices) {
            if (position == count) return i
            count += mList[i].items.size + 1
        }
        return -1
    }

    private fun getChildPosition(position: Int): Pair<Int, Int> {
        var count = 0
        for (i in mList.indices) {
            if (position < count + mList[i].items.size + 1) {
                return Pair(i, position - count - 1)
            }
            count += mList[i].items.size + 1
        }
        return Pair(-1, -1)
    }

    inner class GroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val title: TextView = itemView.findViewById(R.id.tv_date)

        fun bind(group: TransactionGroup) {
            title.text = group.date
        }
    }

    inner class ChildViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val iconToken: ImageView = itemView.findViewById(R.id.icon_token)
        val transferIconGroup: View = itemView.findViewById(R.id.transfer_icon_group)
        val swapIconGroup: View = itemView.findViewById(R.id.swap_icon_group)
        val swapIconFrom : ImageView = itemView.findViewById(R.id.icon_swap_from)
        val swapIconTo : ImageView = itemView.findViewById(R.id.icon_swap_to)

        val iconTransactionType: ImageView = itemView.findViewById(R.id.icon_transaction_type)
        val tvTransactionType: TextView = itemView.findViewById(R.id.tv_transaction_type)
        val tvAddress: TextView = itemView.findViewById(R.id.tv_address)
        val tvInAmount: TextView = itemView.findViewById(R.id.tv_in_amount)
        val tvOutAmount: TextView = itemView.findViewById(R.id.tv_out_amount)


        fun bind(bean: ConvertedTransaction) {
            if (bean.type == ConvertedTransaction.TYPE_SEND) {
                transferIconGroup.visible()
                swapIconGroup.gone()

                tvTransactionType.text = mContext.getString(R.string.sent)
                ImageHelper.obtainImage(mContext, bean.icon1!!, iconToken)
            } else if (bean.type == ConvertedTransaction.TYPE_RECEIVE) {
                transferIconGroup.visible()
                swapIconGroup.gone()

                tvTransactionType.text = mContext.getString(R.string.received)

                ImageHelper.obtainImage(mContext, bean.icon2!!, iconToken)
            } else if (bean.type == ConvertedTransaction.TYPE_SWAP) {
                transferIconGroup.gone()
                swapIconGroup.visible()

                tvTransactionType.text = "" //mContext.getString(R.string.swap)

                ImageHelper.obtainImage(mContext, bean.icon1!!, swapIconFrom)
                ImageHelper.obtainImage(mContext, bean.icon2!!, swapIconTo)
            } else if (bean.type == ConvertedTransaction.TYPE_DK) {
                iconToken.setImageResource(R.drawable.ic_error)
                transferIconGroup.visible()
                swapIconGroup.gone()

                tvTransactionType.text = mContext.getString(R.string.unknown_transaction)
            }

            bean.inAmount?.let { tvInAmount.text = it }?:run { tvInAmount.text = "" }
            bean.outAmount?.let { tvOutAmount.text = it }?:run { tvOutAmount.text = "" }
            tvAddress.text = CryptoUtils.getDisplayAddress(bean.handler)

            itemView.clickNoRepeat {
//                TerminalActivity.start(
//                    mContext, TerminalActivity.Companion.FragmentTypeEnum.TRANSACTION_DETAIL,
//                    key,
//                    TransactionDetailFragment.getBundle(bean)
//                )
//                val intent = Intent(mContext, TransactionDetailFragment::class.java)
//                intent.putExtra(AppConstants.KEY, bean)
//                mContext.startActivity(intent)
            }

        }
    }

    data class TransactionGroup(val date: String, val items: List<ConvertedTransaction>)
}