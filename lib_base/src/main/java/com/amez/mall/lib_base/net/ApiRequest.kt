package com.amez.mall.lib_base.net

import com.amez.mall.lib_base.bean.TokenInfoReq
import com.amez.mall.lib_base.bean.TokenInfoResp
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiRequest {

    private val TAG = ApiRequest::class.simpleName


    fun getTokens(req:TokenInfoReq,callback:(TokenInfoResp)->Unit){

        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY) //Set Log Level
        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor(logging)  //Adding interceptors to OkHttp

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.solana.fm/v0/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient.build())
            .build()

        val apiService = retrofit.create(ApiService::class.java)
        val call: Call<TokenInfoResp> = apiService.getTokens(req)
        call.enqueue(object : Callback<TokenInfoResp> {
            override fun onFailure(call: Call<TokenInfoResp>, t: Throwable) {
            }

            override fun onResponse(call: Call<TokenInfoResp>, response: Response<TokenInfoResp>) {
                if(response.isSuccessful) callback.invoke(response.body()?:TokenInfoResp(null,null,null))
            }
        })
    }
}