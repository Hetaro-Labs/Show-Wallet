package com.amez.mall.lib_base.net

import com.amez.mall.lib_base.bean.GetAssetsByOwnerReq
import com.amez.mall.lib_base.bean.GetAssetsByOwnerReqParams
import com.amez.mall.lib_base.bean.GetAssetsByOwnerResp
import com.amez.mall.lib_base.bean.SwapReq
import com.amez.mall.lib_base.bean.SwapResp
import com.amez.mall.lib_base.bean.TokenInfoReq
import com.amez.mall.lib_base.bean.TokenInfoResp
import com.amez.mall.lib_base.bean.TokenPairUpdatedResp
import com.amez.mall.lib_base.bean.TransactionsResp
import com.amez.mall.lib_base.utils.Logger
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.bouncycastle.util.test.FixedSecureRandom.BigInteger
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.math.pow

object ApiRequest {

    private val TAG = ApiRequest::class.simpleName

    private const val SOLAN_BASE_URL = "https://api.solana.fm/v0/"


    /**
     * {"hydration":{"accountHash":true},"tokenHashes":["57n1Z8g7XHKAj7eeHeZ3SaYYbeDEmTGUjYsv9Hk7TxMx","EPjFWdd5AufqSSqeM2qN1xzybapC8G4wEGGkZwyTDt1v"]}
     * {"status":"Success","message":"Retrieved tokens' info","result":[{"tokenHash":"EPjFWdd5AufqSSqeM2qN1xzybapC8G4wEGGkZwyTDt1v","data":{"mint":"EPjFWdd5AufqSSqeM2qN1xzybapC8G4wEGGkZwyTDt1v","tokenName":"USD Coin","symbol":"USDC","decimals":6,"description":"","logo":"https://s3.coinmarketcap.com/static-gravity/image/5a8229787b5e4c809b5914eef709b59a.png","tags":["stablecoin","saber-mkt-usd"],"verified":"true","network":["mainnet"],"metadataToken":""}}]}
     */
    fun getTokens(req: TokenInfoReq, callback: (TokenInfoResp) -> Unit) {
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
                if (response.isSuccessful || response.code() == 400) callback.invoke(
                    response.body() ?: TokenInfoResp(null, null, null)
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

    fun getPriceInUSD(
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
        ){
            callback(uiAmount)
        }else{
            val amount = java.math.BigInteger.valueOf((uiAmount * 10.0.pow(decimals)).toLong())
            getTokenPairUpdated(
                mint,
                "EPjFWdd5AufqSSqeM2qN1xzybapC8G4wEGGkZwyTDt1v",
                amount
            ) {
                val uiAmount = it.outAmount!!.toDouble() / 1000000
                callback(uiAmount)
            }
        }

    }

    fun getTokenPairUpdated(
        parameter1: String,
        parameter2: String,
        parameter3: java.math.BigInteger,
        callback: (TokenPairUpdatedResp) -> Unit
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
            apiService.getTokenPairUpdated(parameter1, parameter2, parameter3, 1)
        call.enqueue(object : Callback<TokenPairUpdatedResp> {
            override fun onFailure(call: Call<TokenPairUpdatedResp>, t: Throwable) {
                Logger.d("WalletHomeVModel", "get price failed: $t")
            }

            override fun onResponse(
                call: Call<TokenPairUpdatedResp>,
                response: Response<TokenPairUpdatedResp>
            ) {
                if (response.isSuccessful) callback.invoke(
                    response.body() ?: TokenPairUpdatedResp()
                )
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