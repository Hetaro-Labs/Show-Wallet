package com.amez.mall.lib_base.utils

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide

object ImageHelper {

    fun obtainImage(content: Context, url: String, iv: ImageView) {
        if (url.startsWith("drawable://")){
            val id = url.replace("drawable://", "").toInt()
            iv.setImageResource(id)
        }else{
            Glide.with(content).load(url).into(iv);
        }
    }
}