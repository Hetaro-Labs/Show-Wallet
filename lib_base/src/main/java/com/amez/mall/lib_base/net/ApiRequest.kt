package com.amez.mall.lib_base.net

import com.amez.mall.lib_base.bean.TokenInfoReq
import com.amez.mall.lib_base.bean.TokenInfoResp
import com.amez.mall.lib_base.bean.TokenPairUpdatedResp
import com.amez.mall.lib_base.bean.TransactionsResp
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiRequest {

    private val TAG = ApiRequest::class.simpleName

    private const val SOLAN_BASE_URL="https://api.solana.fm/v0/"


    fun getTokens(req:TokenInfoReq,callback:(TokenInfoResp)->Unit){

        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY) //Set Log Level
        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor(logging)  //Adding interceptors to OkHttp

        val retrofit = Retrofit.Builder()
            .baseUrl(SOLAN_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient.build())
            .build()

        val apiService = retrofit.create(ApiService::class.java)
        val call: Call<TokenInfoResp> = apiService.getTokens(req)
        call.enqueue(object : Callback<TokenInfoResp> {
            override fun onFailure(call: Call<TokenInfoResp>, t: Throwable) {
            }

            override fun onResponse(call: Call<TokenInfoResp>, response: Response<TokenInfoResp>) {
                if(response.isSuccessful || response.code()==400) callback.invoke(response.body()?:TokenInfoResp(null,null,null))
            }
        })
    }

    fun getTransactions(publicKeyString:String,callback:(TransactionsResp)->Unit){

        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY) //Set Log Level
        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor(logging)  //Adding interceptors to OkHttp

        val retrofit = Retrofit.Builder()
            .baseUrl(SOLAN_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient.build())
            .build()

        val apiService = retrofit.create(ApiService::class.java)
        val call: Call<TransactionsResp> = apiService.getTransactions(publicKeyString)
        call.enqueue(object : Callback<TransactionsResp> {
            override fun onFailure(call: Call<TransactionsResp>, t: Throwable) {
            }

            override fun onResponse(call: Call<TransactionsResp>, response: Response<TransactionsResp>) {
                if(response.isSuccessful) callback.invoke(response.body()?:TransactionsResp(null,null,null,null))
            }
        })

    }

    fun getTokenPairUpdated(parameter1: String,parameter2: String,parameter3: Int,callback:(TokenPairUpdatedResp)->Unit){
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY) //Set Log Level
        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor(logging)  //Adding interceptors to OkHttp

        val retrofit = Retrofit.Builder()
            .baseUrl("https://quote-api.jup.ag/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient.build())
            .build()

        val apiService = retrofit.create(ApiService::class.java)
        val call: Call<TokenPairUpdatedResp> = apiService.getTokenPairUpdated(parameter1,parameter2,parameter3,1)
        call.enqueue(object : Callback<TokenPairUpdatedResp> {
            override fun onFailure(call: Call<TokenPairUpdatedResp>, t: Throwable) {
            }

            override fun onResponse(call: Call<TokenPairUpdatedResp>, response: Response<TokenPairUpdatedResp>) {
                if(response.isSuccessful) callback.invoke(response.body()?:TokenPairUpdatedResp())
            }
        })

    }
}