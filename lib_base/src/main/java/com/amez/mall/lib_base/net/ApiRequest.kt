package com.amez.mall.lib_base.net

import com.amez.mall.lib_base.bean.GetAssetsByOwnerReq
import com.amez.mall.lib_base.bean.GetAssetsByOwnerReqParams
import com.amez.mall.lib_base.bean.GetAssetsByOwnerResp
import com.amez.mall.lib_base.bean.SwapReq
import com.amez.mall.lib_base.bean.SwapResp
import com.amez.mall.lib_base.bean.TokenInfoReq
import com.amez.mall.lib_base.bean.TokenInfoResult
import com.amez.mall.lib_base.bean.TokenPairUpdatedResp
import com.amez.mall.lib_base.bean.TransactionsResp
import com.amez.mall.lib_base.utils.Logger
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.math.pow

object ApiRequest {

    private val TAG = ApiRequest::class.simpleName

    private const val SOLAN_BASE_URL = "https://api.solana.fm/"

    fun getTokens(req: TokenInfoReq): Response<HashMap<String, TokenInfoResult>> {
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
        val call: Call<HashMap<String, TokenInfoResult>> = apiService.getTokens(req)
        return call.execute()
    }

    fun getTokens(req: TokenInfoReq, callback: (HashMap<String, TokenInfoResult>) -> Unit) {
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
        val call: Call<HashMap<String, TokenInfoResult>> = apiService.getTokens(req)
        call.enqueue(object : Callback<HashMap<String, TokenInfoResult>> {
            override fun onFailure(call: Call<HashMap<String, TokenInfoResult>>, t: Throwable) {
            }

            override fun onResponse(call: Call<HashMap<String, TokenInfoResult>>, response: Response<HashMap<String, TokenInfoResult>>) {
                if (response.isSuccessful || response.code() == 400) callback.invoke(
                    response.body() ?: HashMap()
                )
            }
        })
    }

    fun getTransactions(publicKeyString: String, callback: (TransactionsResp) -> Unit) {
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

            override fun onResponse(
                call: Call<TransactionsResp>,
                response: Response<TransactionsResp>
            ) {
                if (response.isSuccessful) callback.invoke(
                    response.body() ?: TransactionsResp(
                        null,
                        null,
                        null,
                        null
                    )
                )
            }
        })
    }

    fun getAmountInUSD(
        uiAmount: Double,
        decimals: Int,
        mint: String,
        callback: (Double) -> Unit
    ) {
        //special cases: USDT/USDC
        if (listOf(
                "Es9vMFrzaCERmJfrF4H2FYD4KCoNkY11McCe8BenwNYB",
                "EPjFWdd5AufqSSqeM2qN1xzybapC8G4wEGGkZwyTDt1v"
            ).contains(mint)
        ) {
            callback(uiAmount)
        } else {
            val amount = java.math.BigInteger.valueOf((uiAmount * 10.0.pow(decimals)).toLong())
            getTokenPairUpdated(
                mint,
                "EPjFWdd5AufqSSqeM2qN1xzybapC8G4wEGGkZwyTDt1v",
                amount
            ) {
                val outUiAmount = it?.outAmount?.toDouble()?.let { it/ 1000000 } ?: 0.0
                callback(outUiAmount)
            }
        }
    }

    fun getTokenPairUpdated(
        mint1: String,
        mint2: String,
        amount1: java.math.BigInteger,
        callback: (TokenPairUpdatedResp?) -> Unit
    ) {
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
        val call: Call<TokenPairUpdatedResp> =
            apiService.getTokenPairUpdated(mint1, mint2, amount1, 1)
        call.enqueue(object : Callback<TokenPairUpdatedResp> {
            override fun onFailure(call: Call<TokenPairUpdatedResp>, t: Throwable) {
                Logger.d("WalletHomeVModel", "get price failed: $t")
            }

            override fun onResponse(
                call: Call<TokenPairUpdatedResp>,
                response: Response<TokenPairUpdatedResp>
            ) {
                val body = response.body() ?: TokenPairUpdatedResp()
                Logger.d("WalletHomeVModel", "get price response: $body")
                if (response.isSuccessful) callback.invoke(body)
                else callback.invoke(null)
            }
        })
    }

    fun getAssetsByOwner(ownerAddress: String, callback: (GetAssetsByOwnerResp) -> Unit) {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY) //Set Log Level
        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor(logging)  //Adding interceptors to OkHttp

        val retrofit = Retrofit.Builder()
            .baseUrl("https://mainnet.helius-rpc.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient.build())
            .build()

        val apiService = retrofit.create(ApiService::class.java)
        val req = GetAssetsByOwnerReq(GetAssetsByOwnerReqParams(ownerAddress))
        val call: Call<GetAssetsByOwnerResp> = apiService.getAssetsByOwner(req)
        call.enqueue(object : Callback<GetAssetsByOwnerResp> {
            override fun onFailure(call: Call<GetAssetsByOwnerResp>, t: Throwable) {
            }

            override fun onResponse(
                call: Call<GetAssetsByOwnerResp>,
                response: Response<GetAssetsByOwnerResp>
            ) {
                if (response.isSuccessful) callback.invoke(response.body()!!)
            }
        })

    }

    fun swap(swapReq: SwapReq, callback: (SwapResp) -> Unit) {
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
        val call: Call<SwapResp> = apiService.swap(swapReq)
        call.enqueue(object : Callback<SwapResp> {
            override fun onFailure(call: Call<SwapResp>, t: Throwable) {
            }

            override fun onResponse(call: Call<SwapResp>, response: Response<SwapResp>) {
                if (response.isSuccessful) callback.invoke(response.body()!!)
            }
        })

    }
}