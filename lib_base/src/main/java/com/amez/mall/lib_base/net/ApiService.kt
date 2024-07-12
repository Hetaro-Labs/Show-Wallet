package com.amez.mall.lib_base.net

import com.amez.mall.lib_base.bean.TokenInfoReq
import com.amez.mall.lib_base.bean.TokenInfoResp
import com.amez.mall.lib_base.bean.TransactionsResp
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    @POST("tokens")
    fun getTokens(@Body reqBean: TokenInfoReq): Call<TokenInfoResp>

    @GET("accounts/{publicKeyString}/transfers?utcFrom=0&utcTo=12698883199&page=1")
    fun getTransactions(@Path("publicKeyString") publicKeyString: String?): Call<TransactionsResp>

}