package com.showtime.wallet.vm

import android.app.Activity
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListPopupWindow
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.amez.mall.lib_base.bean.Hydration
import com.amez.mall.lib_base.bean.TokenInfoReq
import com.amez.mall.lib_base.net.ApiRequest
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.showtime.wallet.DefaultTokenListData
import com.showtime.wallet.R
import com.showtime.wallet.data.Ed25519KeyRepositoryNew
import com.showtime.wallet.net.AppConnection
import com.showtime.wallet.net.QuickNodeUrl
import com.showtime.wallet.net.bean.Token
import com.showtime.wallet.utils.CryptoUtils
import com.showtime.wallet.utils.TokenListCache
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.sol4k.PublicKey
import kotlin.math.pow


class WalletHomeVModel : BaseWalletVModel() {

    private var listPopupWindow: ListPopupWindow? = null

    private val _getBlanceLiveData = MutableLiveData<String>()
    val getBlanceLiveData: LiveData<String> = _getBlanceLiveData

    private val _getTokens = MutableLiveData<List<Token>>()
    val getTokens: MutableLiveData<List<Token>> = _getTokens

    private val _getTokenPrice = MutableLiveData<Token>()
    val getTokensPrice: MutableLiveData<Token> = _getTokenPrice

    private val _getTokensBySearch = MutableLiveData<List<Token>>()
    val getTokensBySearch: MutableLiveData<List<Token>> = _getTokensBySearch

    private val _getBlanceTotal = MutableLiveData<String>()
    val getBlanceTotal: LiveData<String> = _getBlanceTotal

    //Display Switch User PopWindows
    @OptIn(DelicateCoroutinesApi::class)
    fun showAccountPopWindow(act: Activity, view: View, callback: (String) -> Unit) {
        if (null == listPopupWindow) {
            listPopupWindow = ListPopupWindow(act)
            val list = mutableListOf<String>()

            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    Ed25519KeyRepositoryNew.getAll()?.forEach {
                        list.add(CryptoUtils.keypairToPublicKey(it))
                    }
                }
            }

            listPopupWindow?.setAdapter(
                ArrayAdapter(
                    act,
                    R.layout.item_account,
                    list
                )
            ) //Set up adapter
            listPopupWindow?.anchorView = view //Set which control is adjacent to the listpopwindow
            listPopupWindow?.isModal =
                true //Specify whether listpopwindow prevents content from being entered into other windows when displayed
            listPopupWindow?.setOnItemClickListener { parent, view, position, id ->
                callback.invoke(list.get(position))
                listPopupWindow?.dismiss() //If selected, hide listpopwindow
            }
            listPopupWindow?.show()
        } else {
            listPopupWindow?.show()
        }
    }

//    fun getBalance(tokenList: List<Token>) {
//        getPriceInUsd(tokenList.filter { it.uiAmount > 0 })
//    }

    fun getTokenPrices(tokenList: List<Token>) {
        getTokenPriceInUsd(tokenList.filter { it.uiAmount > 0 })
    }

    private fun getTokenPriceInUsd(tList: List<Token>, i: Int = 0, totalBalance: Double = 0.0) {
        if (i == tList.size) {
            _getBlanceTotal.postValue("$${String.format("%.2f", totalBalance)}")
            TokenListCache.saveList(tList)
        } else {
            val token = tList[i]
            log("getPriceInUSD[${token.tokenName}]=" + token.mint)

            ApiRequest.getAmountInUSD(
                token.uiAmount,
                token.decimals,
                token.mint
            ) { balance ->
                //called 3 times
                log("getPriceInUSD[${token.tokenName}]->" + token + ": " + balance)
                token.amountInUsd = balance

                val price = balance / token.uiAmount
                TokenListCache.savePrice(token.mint, price)

                _getTokenPrice.postValue(token)

                getTokenPriceInUsd(tList, i + 1, totalBalance + balance)
            }
        }
    }

//    private fun getPriceInUsd(tList: List<Token>, i: Int = 0, totalBalance: Double = 0.0) {
//        if (i == tList.size) {
//            _getBlanceTotal.postValue("$${String.format("%.2f", totalBalance)}")
//        } else {
//            val token = tList[i]
//            log("getPriceInUSD[${token.tokenName}]=" + token.mint)
//            ApiRequest.getPriceInUSD(
//                token.uiAmount,
//                token.decimals,
//                token.mint
//            ) { balance ->
//                log("getPriceInUSD[${token.tokenName}] -> " + token.uiAmount + ": " + balance)
//                getPriceInUsd(tList, i + 1, totalBalance + balance)
//            }
//        }
//    }

    fun getTokenAccountsByOwner(key: PublicKey) {
        log("getTokenAccountsByOwner")

        viewModelScope.launch {
            log("get balanceOfSOl")
            val tokensList = mutableListOf<Token>()

            val connection = AppConnection(QuickNodeUrl.MAINNNET)

            val balanceOfSol = withContext(Dispatchers.IO) {
                connection.getBalance(key)
                    .toDouble() / 10.0.pow(DefaultTokenListData.SOL.decimals.toDouble())
            }

            val solana = DefaultTokenListData.SOL
            solana.uiAmount = balanceOfSol
            tokensList.add(solana)

            log("balanceOfSOl -> $balanceOfSol")
            //1.request getTokenAccountsByOwner
            withContext(Dispatchers.IO) {
                connection.getTokenAccountsByOwner(key)
            }?.let { result ->
                //2.assemble all mints
                log("get all mints")
                val mintsList = arrayListOf<String>()
                //resultOne.value.forEach { mintsList.add(it.account.data.parsed.info.mint) }
                //https://api.solana.fm/v0/tokens The interface limits the length of the mints input parameter set
                result.value.forEach {
                    log("mint: " + it.account.data.parsed.info.mint)

                    if (mintsList.size < 50) mintsList.add(it.account.data.parsed.info.mint)
                }

                withContext(Dispatchers.IO) {
                    //3.request getTokens,Data after successful callback request
                    val response = ApiRequest.getTokens(TokenInfoReq(Hydration(true), mintsList))
                    if (response.isSuccessful || response.code() == 400) {
                        val data = response.body()

                        //4.assemble all tokens
                        data?.result?.forEach {
                            log("get token: " + it.data.tokenName)
                            val item = result.value.find { candidate ->
                                candidate.account.data.parsed.info.mint == it.data.mint
                            }!!

                            val amount = item.account.data.parsed.info.tokenAmount
                            val tokenAccount = item.pubkey

                            tokensList.add(
                                Token(
                                    mint = it.data.mint,
                                    tokenName = it.data.tokenName,
                                    symbol = it.data.symbol,
                                    decimals = it.data.decimals,
                                    logo = it.data.logo,
                                    uiAmount = item.account.data.parsed.info.tokenAmount.uiAmount,
                                    isNFT = (amount.decimals == 0 && amount.uiAmount == 1.0),
                                    tokenType = "",
                                    tokenAccount = tokenAccount
                                )
                            )
                        }

                        for (token in tokensList) {
                            log("token: " + token.symbol + "->" + token.mint)
                        }

                        //5.if tokenList has the data, do not add,else add DefaultTokenListData
                        val gson = Gson()
                        val type =
                            object : TypeToken<List<DefaultTokenListData.DefaultToken?>?>() {}.type
                        val defaultTokenList: List<DefaultTokenListData.DefaultToken> =
                            gson.fromJson(DefaultTokenListData.JSON, type)
                        defaultTokenList.forEach { defaultToken ->
                            if (tokensList.find { it.mint == defaultToken.mint } == null)
                                tokensList.add(
                                    Token(
                                        mint = defaultToken.mint,
                                        symbol = defaultToken.symbol,
                                        logo = defaultToken.icon,
                                        tokenName = defaultToken.token_id,
                                        decimals = defaultToken.decimals,
                                        uiAmount = 0.0,
                                        isNFT = false,
                                        tokenType = "",
                                        tokenAccount = ""
                                    )
                                )
                        }

                        for (token in tokensList) {
                            val price = TokenListCache.getPrice(token.mint)
                            token.amountInUsd = token.uiAmount * price
                        }

                        //6.post data to fill adapter
                        TokenListCache.saveList(tokensList)
                        _getTokens.postValue(tokensList.filter { !it.isNFT })
                    }
                }

            }
        }
    }

    fun getTokensBySearch(address: String) {
        val mintsList = arrayListOf<String>()
        mintsList.add(address)
        ApiRequest.getTokens(TokenInfoReq(Hydration(true), mintsList)) { data ->
            val tokensList = mutableListOf<Token>()
            data.result?.forEach {
                tokensList.add(
                    Token(
                        mint = it.data.mint,
                        tokenName = it.data.tokenName,
                        symbol = it.data.symbol,
                        decimals = it.data.decimals,
                        logo = it.data.logo,
                        uiAmount = 0.0,
                        isNFT = false,
                        tokenType = "",
                        tokenAccount = ""
                    )
                )
            }
            _getTokensBySearch.postValue(tokensList)
        }
    }

    enum class FragmentTypeEnum(val value: String) {
        SWAP("SWAP"),
        NFT("NFT"),
        TRANSACTION("TRANSACTION")
    }

}