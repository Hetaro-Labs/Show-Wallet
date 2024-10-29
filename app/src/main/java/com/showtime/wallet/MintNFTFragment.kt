package com.showtime.wallet

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import com.showtime.wallet.TerminalActivity.Companion.FragmentTypeEnum
import com.showtime.wallet.databinding.FragmentMintNftBinding
import com.showtime.wallet.utils.CryptoUtils
import com.showtime.wallet.utils.clickNoRepeat
import com.showtime.wallet.utils.gone
import com.showtime.wallet.utils.visible
import com.showtime.wallet.vm.MintNFTVModel

class MintNFTFragment : BaseSecondaryFragment<FragmentMintNftBinding, MintNFTVModel>() {

    companion object {
        fun start(context: Context, key: String, receiver: String) {
            val bundle = Bundle()
            bundle.putString("receiver", receiver)
            TerminalActivity.start(
                context, FragmentTypeEnum.MINT_NFT, key,
                bundle
            )
        }
    }

    private lateinit var receiver: String

    override fun getContentViewLayoutID(): Int {
        return R.layout.fragment_mint_nft
    }

    override fun getBundleExtras(extras: Bundle) {
        super.getBundleExtras(extras)
        receiver = extras.getString("receiver")!!
    }

    override fun initLiveDataObserve() {
        mViewModel.getTransactionHash.observe(viewLifecycleOwner) {
            TransactionStatusFragment.start(
                requireContext(),
                TransactionStatusFragment.TYPE_MINT,
                "mint to ${CryptoUtils.getDisplayAddress(receiver)}",
                it
            )

            requireActivity().finish()
        }

        mViewModel.getTransactionError.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        }
    }

    override fun initRequestData() {
    }

    override fun FragmentMintNftBinding.initView() {
        mBinding.btnMintNft1.clickNoRepeat {
            mViewModel.mint(receiver, 1)

            mBinding.buttons.gone()
            mBinding.loading.visible()
        }

        mBinding.btnMintNft2.clickNoRepeat {
            mViewModel.mint(receiver, 2)

            mBinding.buttons.gone()
            mBinding.loading.visible()
        }

        mBinding.btnMintNft3.clickNoRepeat {
            mViewModel.mint(receiver, 3)

            mBinding.buttons.gone()
            mBinding.loading.visible()
        }

        mBinding.receiverAddress.text = receiver
    }

}