package com.showtime.wallet.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.amez.mall.lib_base.bean.GetAssetsByOwnerResultItem
import com.amez.mall.lib_base.utils.ImageHelper
import com.showtime.wallet.NFTDetailFragment
import com.showtime.wallet.R
import com.showtime.wallet.TerminalActivity
import com.showtime.wallet.utils.AppConstants
import com.showtime.wallet.utils.clickNoRepeat

class NFTAdapter(
    private var mContext: Context,
    private val assetsByOwnerResultItem: List<GetAssetsByOwnerResultItem>,
    private val key: String,
    private val to: String
) : RecyclerView.Adapter<NFTAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView: View =
            LayoutInflater.from(mContext).inflate(R.layout.layout_nft, parent, false)
        val holder = MyViewHolder(itemView)
        return holder
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val bean = assetsByOwnerResultItem[position]
        holder.nftTitle.text = bean.content.metadata.name
        ImageHelper.obtainImage(mContext, bean.content.files[0].cdn_uri, holder.nftImage)
        holder.itemView.clickNoRepeat {
            NFTDetailFragment.start(mContext, bean, key, to)
        }
    }

    override fun getItemCount(): Int {
        return assetsByOwnerResultItem.size
    }

    // ViewHolder Reuse Component
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nftImage: ImageView = itemView.findViewById(R.id.nft_image)
        val nftTitle: TextView = itemView.findViewById(R.id.nft_name)
    }
}