package com.showtime.wallet.utils

import com.amez.mall.lib_base.utils.MmkvUtils.getString
import com.amez.mall.lib_base.utils.MmkvUtils.put
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.showtime.wallet.net.bean.Token

object TokenListCache {

    fun saveList(list:List<Token>){
        val gson = Gson()
        val str: String = gson.toJson(list)
        put("tokenList",str)
    }

    fun getList():List<Token>{
        val gson = Gson()
        val type = object : TypeToken<List<Token?>?>() {}.type
        val list: List<Token> = gson.fromJson(getString("tokenList"), type)
        return list
    }
}