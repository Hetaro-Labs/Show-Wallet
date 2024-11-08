package com.amez.mall.lib_base.net

import com.amez.mall.lib_base.BuildConfig
import com.amez.mall.lib_base.bean.GetAssetsByOwnerReq
import com.amez.mall.lib_base.bean.GetAssetsByOwnerResp
import com.amez.mall.lib_base.bean.SwapReq
import com.amez.mall.lib_base.bean.SwapResp
import com.amez.mall.lib_base.bean.TokenInfoReq
import com.amez.mall.lib_base.bean.TokenInfoResult
import com.amez.mall.lib_base.bean.TokenPairUpdatedResp
import com.amez.mall.lib_base.bean.TransactionsResp
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import java.math.BigInteger

interface ApiService {

    @POST("v1/tokens")
    fun getTokens(@Body reqBean: TokenInfoReq): Call<HashMap<String, TokenInfoResult>>

    @GET("v0/accounts/{publicKeyString}/transfers?utcFrom=0&utcTo=12698883199&page=1")
    fun getTransactions(@Path("publicKeyString") publicKeyString: String?): Call<TransactionsResp>

    @GET("v6/quote")
    fun getTokenPairUpdated(@Query("inputMint") parameterOne:String?,
                            @Query("outputMint") parameterTwo: String?,
                            @Query("amount") parameterThree: BigInteger?,
                            @Query("slippageBps") slippageBps:Int): Call<TokenPairUpdatedResp>

    @POST("?api-key=${BuildConfig.API_KEY}") //update your own key, for instance,
    fun getAssetsByOwner(@Body reqBean: GetAssetsByOwnerReq): Call<GetAssetsByOwnerResp>

    @POST("v6/swap")
    fun swap(@Body reqBean: SwapReq): Call<SwapResp>

}