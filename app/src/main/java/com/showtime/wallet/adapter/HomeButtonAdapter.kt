package com.showtime.wallet.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.showtime.wallet.R
import com.showtime.wallet.utils.clickNoRepeat

class HomeButtonAdapter(
    private var mContext: Context,
    private val mList: List<HomeButton>,
) : RecyclerView.Adapter<HomeButtonAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val itemView: View =
            LayoutInflater.from(mContext).inflate(R.layout.layout_home_button, parent, false)
        val holder = ViewHolder(itemView)
        return holder
    }

    @SuppressLint("IntentWithNullActionLaunch")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val bean = mList[position]
        holder.ivButtonIcon.setImageResource(bean.icon)
        holder.tvButtonLabel.setText(bean.label)

        holder.itemView.clickNoRepeat {
            bean.onClick()
        }

    }

    override fun getItemCount(): Int {
        return mList.size
    }

    data class HomeButton(val icon: Int, val label: Int, val onClick: () -> Unit)

    // ViewHolder Reuse Component
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivButtonIcon: ImageView = itemView.findViewById(R.id.button_icon)
        val tvButtonLabel: TextView = itemView.findViewById(R.id.button_label)
    }

}