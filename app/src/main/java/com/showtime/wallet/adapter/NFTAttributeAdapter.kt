package com.showtime.wallet.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.amez.mall.lib_base.bean.GetAssetsByOwnerResultItemContentMetaDataAttributes
import com.showtime.wallet.R

class NFTAttributeAdapter(private var mContext: Context,
                          private val mList: List<GetAssetsByOwnerResultItemContentMetaDataAttributes>
): RecyclerView.Adapter<NFTAttributeAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView: View =
            LayoutInflater.from(mContext).inflate(R.layout.layout_attribute, parent, false)
        val holder = MyViewHolder(itemView)
        return holder
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val bean = mList[position]
        holder.attributeType.text=bean.trait_type
        holder.attributeValue.text=bean.value
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    // ViewHolder Reuse Component
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val attributeType: TextView = itemView.findViewById(R.id.attribute_type)
        val attributeValue: TextView = itemView.findViewById(R.id.attribute_value)
    }
}