package com.amez.mall.lib_base.utils

import android.content.Context
import android.widget.ImageView
import com.aminography.redirectglide.GlideApp
import com.aminography.redirectglide.RedirectGlideUrl

object ImageHelper {

    fun obtainImage(content: Context, url: String, iv: ImageView) {
        if (url.startsWith("drawable://")){
            val id = url.replace("drawable://", "").toInt()
            iv.setImageResource(id)
        }else{
            GlideApp.with(content)
                .load(RedirectGlideUrl(url, 3))
                .into(iv)
        }
    }
}