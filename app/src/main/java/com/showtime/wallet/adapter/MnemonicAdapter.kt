package com.showtime.wallet.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.showtime.wallet.R
/**
 * mnemonic adapter
 */
class MnemonicAdapter(private var mContext: Context,
                      private val mList: List<String>): RecyclerView.Adapter<MnemonicAdapter.MnemonicViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MnemonicViewHolder {
        val itemView: View =
            LayoutInflater.from(mContext).inflate(R.layout.adapter_mnemonic, parent, false)
        val holder = MnemonicViewHolder(itemView)
        return holder
    }

    override fun onBindViewHolder(holder: MnemonicViewHolder, position: Int) {
        val str = mList[position]
        holder.tvMnemonic.text=str
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    // ViewHolder Reuse Component
    class MnemonicViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvMnemonic: TextView = itemView.findViewById(R.id.tv_mnemonic)
    }
}