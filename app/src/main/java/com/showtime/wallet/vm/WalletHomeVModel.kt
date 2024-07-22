package com.showtime.wallet.vm

import android.app.Activity
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListPopupWindow
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.amez.mall.lib_base.base.mvvm.vm.BaseViewModel
import com.amez.mall.lib_base.bean.Hydration
import com.amez.mall.lib_base.bean.TokenInfoReq
import com.amez.mall.lib_base.net.ApiRequest
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.showtime.wallet.DefaultTokenListData
import com.showtime.wallet.R
import com.showtime.wallet.data.Ed25519KeyRepositoryNew
import com.showtime.wallet.net.AppConnection
import com.showtime.wallet.net.bean.Token
import com.showtime.wallet.utils.CryptoUtils
import com.showtime.wallet.utils.TokenListCache
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.sol4k.PublicKey
import org.sol4k.RpcUrl


class WalletHomeVModel : BaseViewModel() {

    private var listPopupWindow: ListPopupWindow? = null

    // Private variable LiveData
    private val _getBlanceLiveData = MutableLiveData<String>()

    // Externally exposed and immutable LiveData
    val getBlanceLiveData: LiveData<String> = _getBlanceLiveData

    // Private variable LiveData
    private val _getTokens = MutableLiveData<List<Token>>()
    // Externally exposed and immutable LiveData
    val getTokens: MutableLiveData<List<Token>> = _getTokens

    private val _getTokensBySearch = MutableLiveData<List<Token>>()
    val getTokensBySearch: MutableLiveData<List<Token>> = _getTokensBySearch

    //Display Switch User PopWindows
    fun showAccountPopWindow(act: Activity, view: View, callback: (String) -> Unit) {
        if (null == listPopupWindow) {
            listPopupWindow = ListPopupWindow(act)
            val list = mutableListOf<String>()
            GlobalScope.launch(Dispatchers.IO) {
                Ed25519KeyRepositoryNew.getAll()?.forEach {
                    list.add(CryptoUtils.keypairToPublicKey(it))
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

    @OptIn(DelicateCoroutinesApi::class)
    fun getBalance(key: PublicKey) {
        GlobalScope.launch(Dispatchers.IO) {
            val response = async {
                val connection = AppConnection(RpcUrl.DEVNET)
                connection.getBalance(key).toString()
            }
            val result = response.await()
            _getBlanceLiveData.postValue(result)
        }
    }

    @OptIn(DelicateCoroutinesApi::class)

    fun getTokenAccountsByOwner(key:PublicKey){
        GlobalScope.launch(Dispatchers.IO) {

            //1.request getTokenAccountsByOwner
            val response = async {
                val connection = AppConnection(RpcUrl.MAINNNET)
                connection.getTokenAccountsByOwner(key)
            }
            val result= response.await()

            //2.assemble all mints
            val mintsList= arrayListOf<String>()
            //resultOne.value.forEach { mintsList.add(it.account.data.parsed.info.mint) }
            //https://api.solana.fm/v0/tokens The interface limits the length of the mints input parameter set
            result.value.forEachIndexed { index, it -> if(index<50) mintsList.add(it.account.data.parsed.info.mint) }

            //3.request getTokens,Data after successful callback request
            ApiRequest.getTokens(TokenInfoReq(Hydration(true),mintsList)){ data->
                //4.assemble all tokens
                val tokensList= mutableListOf<Token>()
                data.result?.forEachIndexed { index, it ->
                    val amount=result.value.get(index).account.data.parsed.info.tokenAmount
                    //TODO Should the uiAmount here traverse the mint of the getTokenAccountsByOwner set as a condition to match
                    tokensList.add(
                        Token(
                            mint = it.data.mint,
                            tokenName = it.data.tokenName,
                            symbol = it.data.symbol,
                            decimals = it.data.decimals,
                            logo = it.data.logo,
                            uiAmount = result.value.get(index).account.data.parsed.info.tokenAmount.uiAmount,
                            isNFT = (amount.decimals == 0 && amount.uiAmount == 1.0),
                            tokenType = ""
                        )
                    )
                }
                //5.if tokenList has the data, do not add,else add DefaultTokenListData
                val gson = Gson()
                val type = object : TypeToken<List<DefaultTokenListData.DefaultToken?>?>() {}.type
                val defaultTokenList: List<DefaultTokenListData.DefaultToken> = gson.fromJson(DefaultTokenListData.JSON, type)
                defaultTokenList.forEach { defaultToken->
                    if(tokensList.filter { it.mint==defaultToken.mint }.isEmpty())
                        tokensList.add(Token(
                            mint = defaultToken.mint,
                            symbol = defaultToken.symbol,
                            logo = defaultToken.icon,
                            tokenName = defaultToken.token_id,
                            decimals = defaultToken.decimals,
                            uiAmount = 0.0,
                            isNFT = false,
                            tokenType = "")
                        )
                }
                //6.post data to fill adapter
                //cache tokenList
                TokenListCache.saveList(tokensList)
                _getTokens.postValue(tokensList.filter { !it.isNFT })
            }
        }
    }

    fun getTokensBySearch(address:String){
        val mintsList= arrayListOf<String>()
        mintsList.add(address)
        ApiRequest.getTokens(TokenInfoReq(Hydration(true),mintsList)){ data->
            val tokensList= mutableListOf<Token>()
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
                        tokenType = ""
                    )
                )
            }
            _getTokensBySearch.postValue(tokensList)
        }
    }

    enum class FragmentTypeEnum(val value: String){
        SWAP("SWAP"),
        NFT("NFT"),
        TRANSACTION("TRANSACTION")
    }

}