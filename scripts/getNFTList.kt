import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.gson.*
import kotlinx.coroutines.runBlocking
import com.google.gson.Gson
import com.google.gson.JsonObject

data class TokenAccount(val pubkey: String, val account: AccountInfo)
data class AccountInfo(val data: ParsedData)
data class ParsedData(val parsed: ParsedInfo)
data class ParsedInfo(val info: TokenAmount)
data class TokenAmount(val tokenAmount: TokenAmountDetails)
data class TokenAmountDetails(val amount: String, val decimals: Int, val uiAmount: Double)
data class NFTMetadata(val image: String)

const val SOLANA_RPC_URL = "https://api.mainnet-beta.solana.com"
const val TOKEN_PROGRAM_ID = "TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA"

suspend fun getNFTsForOwner(ownerAddress: String): List<Pair<String, String>> {
    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            gson()
        }
    }

    val requestPayload = JsonObject().apply {
        addProperty("jsonrpc", "2.0")
        addProperty("id", 1)
        addProperty("method", "getTokenAccountsByOwner")
        add("params", Gson().toJsonTree(listOf(ownerAddress, mapOf("programId" to TOKEN_PROGRAM_ID), mapOf("encoding" to "jsonParsed"))))
    }

    val response: HttpResponse = client.post(SOLANA_RPC_URL) {
        contentType(ContentType.Application.Json)
        body = requestPayload
    }

    val responseBody = response.readText()
    val jsonResponse = Gson().fromJson(responseBody, JsonObject::class.java)
    val result = jsonResponse["result"].asJsonObject
    val accounts = result["value"].asJsonArray

    val nftMints = accounts.map { accountElement ->
        val account = Gson().fromJson(accountElement, TokenAccount::class.java)
        val amount = account.account.data.parsed.info.tokenAmount
        if (amount.decimals == 0 && amount.uiAmount == 1.0) {
            account.account.data.parsed.info.mint
        } else {
            null
        }
    }.filterNotNull()

    return nftMints.mapNotNull { mint ->
        val metadataUrl = getMetadataUrlForToken(client, mint)
        val metadata = metadataUrl?.let { fetchMetadata(client, it) }
        metadata?.let { mint to it.image }
    }
}

suspend fun getMetadataUrlForToken(client: HttpClient, tokenMint: String): String? {
    val requestPayload = JsonObject().apply {
        addProperty("jsonrpc", "2.0")
        addProperty("id", 1)
        addProperty("method", "getAccountInfo")
        add("params", Gson().toJsonTree(listOf(tokenMint, mapOf("encoding" to "jsonParsed"))))
    }

    val response: HttpResponse = client.post(SOLANA_RPC_URL) {
        contentType(ContentType.Application.Json)
        body = requestPayload
    }

    val responseBody = response.readText()
    val jsonResponse = Gson().fromJson(responseBody, JsonObject::class.java)
    val result = jsonResponse["result"].asJsonObject
    val accountData = result["value"].asJsonObject["data"].asJsonObject["parsed"].asJsonObject["info"].asJsonObject["uri"].asString

    return accountData
}

suspend fun fetchMetadata(client: HttpClient, metadataUrl: String): NFTMetadata {
    val response: HttpResponse = client.get(metadataUrl)
    val responseBody = response.readText()
    return Gson().fromJson(responseBody, NFTMetadata::class.java)
}

fun main() = runBlocking {
    val ownerAddress = "YourOwnerPublicAddressHere"
    val nfts = getNFTsForOwner(ownerAddress)
    nfts.forEach { (mint, imageUrl) ->
        println("NFT Mint: $mint, Image URL: $imageUrl")
    }
}