package com.showtime.wallet.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.amez.mall.lib_base.bus.FlowEventBus
import com.amez.mall.lib_base.utils.ImageHelper
import com.showtime.wallet.R
import com.showtime.wallet.SendTokenDetailActivity
import com.showtime.wallet.net.bean.Token
import com.showtime.wallet.utils.AppConstants
import com.showtime.wallet.utils.EventConstants
import com.showtime.wallet.utils.clickNoRepeat

class TokenAccountsByOwnerAdapter(private var mContext: Context,
                                  private val mList: List<Token>,
                                  private val fromSwap:Boolean=false,
                                  private val tokenType:String=""): RecyclerView.Adapter<TokenAccountsByOwnerAdapter.TokenAccountsByOwnerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TokenAccountsByOwnerViewHolder {
        val itemView: View =
            LayoutInflater.from(mContext).inflate(R.layout.layout_token, parent, false)
        val holder = TokenAccountsByOwnerViewHolder(itemView)
        return holder
    }

    @SuppressLint("IntentWithNullActionLaunch")
    override fun onBindViewHolder(holder: TokenAccountsByOwnerViewHolder, position: Int) {
        val bean = mList[position]
        ImageHelper.obtainImage(mContext,bean.logo,holder.ivTokenIcon)
        holder.tokenName.text=bean.symbol
        holder.tokenAmount.text=bean.uiAmount.toString()
        holder.itemView.clickNoRepeat {
            if(fromSwap){
                bean.tokenType=tokenType
                FlowEventBus.with<Token>(EventConstants.EVENT_SEL_TOKEN).post(bean)
                (mContext as Activity).finish()

            }else{
                val intent= Intent(mContext,SendTokenDetailActivity::class.java)
                intent.putExtra(AppConstants.KEY,bean)
                mContext.startActivity(intent)
            }
        }

    }

    override fun getItemCount(): Int {
        return mList.size
    }

    // ViewHolder Reuse Component
    class TokenAccountsByOwnerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivTokenIcon: ImageView = itemView.findViewById(R.id.token_icon)
        val tokenName: TextView = itemView.findViewById(R.id.token_name)
        val tokenAmount: TextView = itemView.findViewById(R.id.token_amount)
    }
}