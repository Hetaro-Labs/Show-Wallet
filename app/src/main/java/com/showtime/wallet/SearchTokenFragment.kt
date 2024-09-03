package com.showtime.wallet

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import com.amez.mall.lib_base.ui.BaseProjActivity
import com.showtime.wallet.adapter.TokenAccountsByOwnerAdapter
import com.showtime.wallet.databinding.FragmentSearchTokenBinding
import com.showtime.wallet.net.bean.Token
import com.showtime.wallet.utils.AppConstants
import com.showtime.wallet.utils.TokenListCache
import com.showtime.wallet.utils.clickNoRepeat
import com.showtime.wallet.utils.gone
import com.showtime.wallet.utils.visible
import com.showtime.wallet.vm.WalletHomeVModel

class SearchTokenFragment : BaseSecondaryFragment<FragmentSearchTokenBinding, WalletHomeVModel>() {

    companion object {
        fun start(context: Context, selectedPublicKey: String, to: String = "") {
            val bundle = Bundle()
            bundle.putString("to", to)

            TerminalActivity.start(
                context,
                TerminalActivity.Companion.FragmentTypeEnum.SEARCH,
                selectedPublicKey,
                bundle
            )
        }
    }

    private lateinit var tokenList: List<Token>
    private var fromSwap =
        false //Is it coming from swapFragment? The adapter needs to handle click events
    private lateinit var tokenType: String
    private var to: String = ""

    override fun getBundleExtras(extras: Bundle) {
        super.getBundleExtras(extras)
        tokenList = TokenListCache.getList()
        fromSwap = extras.getBoolean(AppConstants.FROM_SWAP_TAG)
        tokenType = extras.getString(AppConstants.FROM_SWAP_TOKENTYPE, "")
        to = extras.getString("to", "")
    }

    override fun getContentViewLayoutID() = R.layout.fragment_search_token

    override fun initLiveDataObserve() {
        mViewModel.getTokensBySearch.observeForever {
            setAdapter(it)
        }
    }

    override fun initRequestData() {
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun FragmentSearchTokenBinding.initView() {
        setAdapter(tokenList)

        tokenSearchInput.setOnKeyListener { view, i, keyEvent ->
            if (keyEvent.action == KeyEvent.ACTION_UP && keyEvent.keyCode == KeyEvent.KEYCODE_ENTER) {
                setAdapter(ArrayList())
                mBinding.viewLoading.visible()

                val serachTxt = tokenSearchInput.text.toString()
                if (serachTxt.isNotEmpty()) {
                    val searchConditionList =
                        tokenList.filter { it.tokenName.contains(serachTxt) } //Currently, we use symbol fuzzy search
                    if (searchConditionList.isEmpty()) {
                        mViewModel.getTokensBySearch(serachTxt)
                    } else {
                        setAdapter(searchConditionList as ArrayList<Token>)
                    }
                }
            }

            true
        }

    }

    private fun setAdapter(list: List<Token>) {
        mBinding.viewLoading.gone()

        val adapter =
            TokenAccountsByOwnerAdapter(
                requireContext(),
                list.toMutableList(),
                fromSwap,
                tokenType,
                key,
                to
            )
        mBinding.rvTokenList.adapter = adapter
    }

}