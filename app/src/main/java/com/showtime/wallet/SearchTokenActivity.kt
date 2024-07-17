package com.showtime.wallet

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import com.amez.mall.lib_base.ui.BaseProjActivity
import com.showtime.wallet.adapter.TokenAccountsByOwnerAdapter
import com.showtime.wallet.databinding.ActivitySearchTokenBinding
import com.showtime.wallet.net.bean.Token
import com.showtime.wallet.utils.AppConstants
import com.showtime.wallet.utils.TokenListCache
import com.showtime.wallet.utils.clickNoRepeat
import com.showtime.wallet.vm.WalletHomeVModel

class SearchTokenActivity : BaseProjActivity<ActivitySearchTokenBinding, WalletHomeVModel>(){

    private lateinit var tokenList: List<Token>
    private var fromSwap=false //Is it coming from swapFragment? The adapter needs to handle click events
    private lateinit var tokenType:String

    override fun getBundleExtras(extras: Bundle?) {
        /**tokenList = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            intent.getParcelableArrayListExtra(AppConstants.KEY,Token::class.java)!!
        else
            intent.getParcelableArrayListExtra(AppConstants.KEY)!!**/
        tokenList=TokenListCache.getList()
        fromSwap=extras?.getBoolean(AppConstants.FROM_SWAP_TAG)?:false
        tokenType=extras?.getString(AppConstants.FROM_SWAP_TOKENTYPE)?:""
    }

    override fun getContentViewLayoutID() = R.layout.activity_search_token

    override fun initLiveDataObserve() {
        mViewModel.getTokensBySearch.observeForever {
            setAdapter(it)
        }
    }

    override fun initRequestData() {
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun ActivitySearchTokenBinding.initView() {
        setAdapter(tokenList)
        ivSearch.clickNoRepeat {
            val serachTxt=tokenSearchInput.text.toString()
            if(serachTxt.isEmpty()) return@clickNoRepeat
            val searchConditionList=tokenList.filter { it.symbol.contains(serachTxt) } //Currently, we use symbol fuzzy search
            if(searchConditionList.isEmpty()){
                mViewModel.getTokensBySearch(serachTxt)
            }else{
                setAdapter(searchConditionList as ArrayList<Token>)
            }
        }
    }

    private fun setAdapter(list: List<Token>){
        val adapter = TokenAccountsByOwnerAdapter(this@SearchTokenActivity, list ,fromSwap,tokenType)
        mBinding.rvTokenList.adapter = adapter
    }

}