package com.showtime.wallet

import android.net.Uri
import android.os.Bundle
import com.amez.mall.lib_base.bean.Hydration
import com.amez.mall.lib_base.bean.TokenInfoReq
import com.amez.mall.lib_base.net.ApiRequest
import com.amez.mall.lib_base.ui.BaseProjNotMVVMActivity
import com.showtime.wallet.databinding.ActivityHandleUriBinding
import com.showtime.wallet.net.bean.Token
import com.showtime.wallet.utils.TokenListCache
import com.showtime.wallet.utils.notNull

/**
 * showallet://send?to=walletAddress
 * showallet://send.spl?mint=mintAddress&to=walletAddress
 * showallet://swap?mint=mintAddress&amount=amountInSol
 */
open class BaseUriHandleActivity : BaseProjNotMVVMActivity<ActivityHandleUriBinding>() {

    companion object {
        val URI_SCHEME = "showallet://"
        val URI_SEND_SOL = "send"
        val URI_SEND_TOKEN = "send.spl"
        val URI_SWAP = "swap"
        val PARAM_TO_ADDRESS = "to"
        val PARAM_SPL_ADDRESS = "mint"
        val PARAM_AMOUNT = "amount"
    }

    protected fun parcelData(data: Uri) {
        val key = ""
        when (data.host) {
            URI_SEND_SOL -> {
                val to = data.getQueryParameter(PARAM_TO_ADDRESS) ?: return

                SendTokenDetailFragment.start(this@BaseUriHandleActivity, key, DefaultTokenListData.SOL, to)
                finish()
            }

            URI_SEND_TOKEN -> {
                val mint = data.getQueryParameter(PARAM_SPL_ADDRESS) ?: return
                val to = data.getQueryParameter(PARAM_TO_ADDRESS) ?: return

                val token = TokenListCache.getList().find {
                    it.mint == mint
                }
                if (token != null) {
                    SendTokenDetailFragment.start(this@BaseUriHandleActivity, key, token, to)
                    finish()
                } else {
                    getToken(mint) {
                        it?.notNull({ _ ->
                            SendTokenDetailFragment.start(this@BaseUriHandleActivity, key, it, to)
                        }, {
                            onTokenNotFound()
                        })
                    }
                }
            }

            URI_SWAP -> {
                val mint = data.getQueryParameter(PARAM_SPL_ADDRESS) ?: return
                val amount = data.getQueryParameter(PARAM_AMOUNT)?.toDouble() ?: return

                val token = TokenListCache.getList().find {
                    it.mint == mint
                }
                if (token != null) {
                    SwapFragment.start(this@BaseUriHandleActivity, key, DefaultTokenListData.SOL, token, amount)
                    finish()
                } else {
                    getToken(mint) {
                        it?.notNull({ _ ->
                            SwapFragment.start(this@BaseUriHandleActivity, key, DefaultTokenListData.SOL, it!!, amount)
                        }, {
                            onTokenNotFound()
                        })
                    }
                }
            }
        }
    }

    private fun onTokenNotFound(){

    }

    private fun getToken(mint: String, callback: (Token?) -> Unit) {
        ApiRequest.getTokens(TokenInfoReq(Hydration(true), listOf(mint))) { data ->
            if (data.result?.isNotEmpty() == true) {
                val it = data.result!![0]
                callback(
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
            } else {
                callback(null)
            }
        }
    }

    override fun getBundleExtras(extras: Bundle?) {
    }

    override fun getContentViewLayoutID(): Int {
        return R.layout.activity_handle_uri
    }

    override fun ActivityHandleUriBinding.initView() {
    }

}