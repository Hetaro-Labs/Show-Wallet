package com.showtime.wallet.net

import android.util.Log
import com.showtime.wallet.net.bean.AccountInfo
import com.showtime.wallet.net.bean.AppBalance
import com.showtime.wallet.net.bean.AppResponse
import com.showtime.wallet.net.bean.TokenAccountsByOwnerBean
import com.showtime.wallet.net.bean.TransactionStatusResp
import kotlinx.serialization.json.Json
import org.json.JSONArray
import org.json.JSONObject
import org.sol4k.PublicKey
import org.sol4k.RpcUrl
import org.sol4k.api.Commitment
import org.sol4k.api.Commitment.FINALIZED
import java.io.BufferedReader
import java.io.InputStreamReader
import java.math.BigInteger
import java.net.HttpURLConnection
import java.net.URL

class AppConnection @JvmOverloads constructor(
    private val rpcUrl: String,
    private val commitment: Commitment = FINALIZED,
) {
    @JvmOverloads
    constructor(
        rpcUrl: RpcUrl,
        commitment: Commitment = FINALIZED,
    ) : this(rpcUrl.value, commitment)


    private val TAG = AppConnection::class.simpleName

    private val jsonParser = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }


    fun getTransaction(signature: String): TransactionStatusResp? {
        val params = JSONArray()
        params.put(signature)
        val jsonObjectEnc = JSONObject()
        jsonObjectEnc.put("encoding", "json")
        jsonObjectEnc.put("commitment", "finalized")
        params.put(jsonObjectEnc)
        val result: TransactionStatusResp? = rpcCall("getTransaction", params)
        return result
    }


    fun getAccountInfo(metadataPDA: String): AccountInfo? {
        val params = JSONArray()
        params.put(metadataPDA)
        val jsonObjectEnc = JSONObject()
        jsonObjectEnc.put("encoding", "jsonParsed")
        params.put(jsonObjectEnc)
        val result: AccountInfo? = rpcCall("getAccountInfo", params)
        return result
    }


    fun getTokenAccountsByOwner(walletAddress: PublicKey): TokenAccountsByOwnerBean? {
        val params = JSONArray()
        params.put(walletAddress.toBase58())

        val jsonObjectPro = JSONObject()
        jsonObjectPro.put("programId", "TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA")
        params.put(jsonObjectPro)
        val jsonObjectEnc = JSONObject()
        jsonObjectEnc.put("encoding", "jsonParsed")
        params.put(jsonObjectEnc)
        val result: TokenAccountsByOwnerBean? = rpcCall("getTokenAccountsByOwner", params)
        return result
    }

    fun getBalance(walletAddress: PublicKey): BigInteger {
        val params = JSONArray()
        params.put(walletAddress.toBase58())
        val balance: AppBalance? = rpcCall("getBalance", params)
        return balance?.value ?: BigInteger.ZERO
    }

    private inline fun <reified T> rpcCall(method: String, params: JSONArray): T? {
        val bodyJson = JSONObject()
        bodyJson.put("method", method)
        bodyJson.put("jsonrpc", "2.0")
        bodyJson.put("id", System.currentTimeMillis())
        bodyJson.put("params", params)

        try {
            val connection = URL(rpcUrl).openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.doOutput = true
            connection.outputStream.use {
                val body = bodyJson.toString()
                Log.d(TAG, "body========${body}")
                it.write(body.toByteArray())
            }
            val responseBody = connection.inputStream.use {
                BufferedReader(InputStreamReader(it)).use { reader ->
                    reader.readText()
                }
            }
            connection.disconnect()
            Log.d(TAG, "responseBody========${responseBody}")
            val (result) = jsonParser.decodeFromString<AppResponse<T>>(responseBody)
            Log.d(TAG, "result========${result}")
            return result
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }
}
