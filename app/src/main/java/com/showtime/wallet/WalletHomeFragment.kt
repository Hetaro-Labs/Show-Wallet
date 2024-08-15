package com.showtime.wallet

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.amez.mall.lib_base.bus.FlowEventBus
import com.amez.mall.lib_base.ui.BaseProjFragment
import com.amez.mall.lib_base.utils.MmkvUtils
import com.showtime.wallet.adapter.HomeButtonAdapter
import com.showtime.wallet.adapter.TokenAccountsByOwnerAdapter
import com.showtime.wallet.data.Ed25519KeyRepositoryNew
import com.showtime.wallet.databinding.FragmentWalletHomeBinding
import com.showtime.wallet.net.QuickNodeUrl
import com.showtime.wallet.utils.AppConstants
import com.showtime.wallet.utils.EventConstants
import com.showtime.wallet.vm.WalletHomeVModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.sol4k.Connection
import org.sol4k.Keypair
import org.sol4k.PublicKey
import org.sol4k.Transaction
import org.sol4k.transaction.EncodedTransaction
import org.sol4k.transaction.VersionedTransaction
import java.util.Base64

class WalletHomeFragment : BaseProjFragment<FragmentWalletHomeBinding, WalletHomeVModel>() {

    companion object {
        fun getInstance(publicKey: String): WalletHomeFragment {
            val fragment = WalletHomeFragment()
            val args = Bundle()
            args.putString("publicKey", publicKey)
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var selectedPublicKey: String
    private val TAG = WalletHomeFragment::class.simpleName

    override fun getBundleExtras(extras: Bundle) {
    }

    override fun getContentViewLayoutID() = R.layout.fragment_wallet_home

    override fun FragmentWalletHomeBinding.initView() {
        selectedPublicKey = arguments?.getString("publicKey") ?: ""
        Log.e(TAG, "selectedPublicKey:${selectedPublicKey}")

        mViewModel.getTokenAccountsByOwner(PublicKey(selectedPublicKey ?: ""))

//        mBinding.btnTestDeep.setOnClickListener {
//            val sol4kTx =
//                "AQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABAAoVQxhqYIKKYYeMpA6TRO9mmZnz7F7ey0Arxzusf/Es0tJwccDT11PCgZnvGncl43WttfK2QUfCBVUqNg8vpBi7S3yqkxCBRoNKvUQM6+vM7hdUBgKi+akZpbvaCpd1sVYfl6fiMQT0LnAXBDu2lQOARhtYi5QbgO4L6/gDqyD/dS+fPs/q96K8ow96krYAokWVzZaNzbWKSIcxNgQQzBKEgwkKzcQCjJktPFDq/uMmm1vR0JPHfzTSU/YmDHMVPYs3qLLQ4QY0S20HU2ioqmnunsWIpHYgUUVifOcbOi5XS4HL5/Tq7ETeVhqOTtChh/pFHz+eEhBUyQfl0VMvc6/zgMjqi5tCwzl46rfpfq7Ar6aeSwFEFdHMOzjAsCPJTqq91ipydsU+eIhTH/m/TKngg0D0n/6oHyWCREj1ntWq4ZfgINIPkc3KG4eh6BHDA91d4BVdrP+dBe5F+DHttZ3bCQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAALzjEbkBDAlaM77NkXMPfqXNLSveCkWI7UEgNs31WEWCMlyWPTiSJ8bs9ECkUjg2DC1oTmdr/EIQEjnvY2+n4WaXVyp4Ez121kLcUui/jLLFZEz/BwZK3Ilf9B9OcsEAeAwZGb+UhFzL/7K26csOb57yM5bvF9xJrLEObOkAAAAC0P/on9df2SnTAmx8pWHneSwmrNt/J3VFLMhqns4zl6Mb6evO+2606PWXzaqvJdDGxu+TC0vbg5HymAgNFL11hBHnVW/IxwG7udMVuzmgVB/2xst6j9I5RArHNola8E48Gm4hX/quBhPtof2NGGMA12sQ53BrrO1WYoPAAAAAAAQbd9uHXZaGT2cvhRs7reawctIXtX1s3kTqM9YV+/wCpfwvmlw/gXULgLIhT912jP0NhVJRdx73Gp6B8AFCvBgsIDwAFAvPQAgAPAAkDjA4AAAAAAAANBgAFABMLFAEBCwIABQwCAAAAgJaYAAAAAAAUAQUBEQ0GAAkAEQsUAQESGBQABQkSERIQEg4ADAgFCQoCBBQBAwYHEiPlF8uXeuOtKgEAAAAaZAABgJaYAAAAAAA2mRQAAAAAADIAABQDBQAAAQk="
//            val testTx =
//                "AQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABAAIED52oQcnEB1ydK0URH+Jf+cV387tI6ix7cZQ1EKK7y73qDVdqX3aXwhcyGNdGyzWOjb1OSuRVkQ/Y2BzIqLpVugAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABUpTWpkpIQZNJOhxYNo4fHw1td28kruB5B+oQEEFRI0FjqdprFIQv5Gs2y/RSpmX16gQ5U/pkxF8e/tyjcrEKQIDAJwCZDk2M2MwNWUzMzkzZmNhZWRjYjA1ODk2ZGVjY2E0M2ZhYWFmZXlKcGRHVnRJam9pVTJodmQxUnBiV1VpTENKeGRIa2lPakVzSW5WdWFYUmZjSEpwWTJVaU9qQXVNREF3TXl3aVkzVnljbVZ1WTNraU9pSlRUMHdpTENKd1lYbGxjbDkzWVd4c1pYUWlPaUl5TTNoWmIyTjFPREoyWW1VelVrUmxia0ZXUzJKdGRVdEJhSEpGTTI5RlJXNVJZMHRNWW1WMVdWcFFRU0lzSW1Kc2IyTnJhR0Z6YUNJNklrNW9RMnB0ZVdocllVMTZaazB5VmxneWEwSjBWM1ZUYVhWSGNYZHlWV2xVVUhWbVdHcFlOVWRVT0V3aWZRPT0CAgABDAIAAADgkwQAAAAAAA=="
//            val swapTx =
//                "AQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACAAQAHDw+dqEHJxAdcnStFER/iX/nFd/O7SOose3GUNRCiu8u9C3UgzyHnGpR6VJ/vU2WjD5DjwsvxD8xvVd650NGXdFMPP/lHAYEuGWoSOE8V5JKqdJcnKouC7evvFOlsDweRhmZ/y1OFUjaxGSYPYSYxL38DI5jSwryQ6RMhtMx+2NckcTN9kd8r51/xwc6IwfXwKTzn7kVi9u4+BF+gf6rTEd13VyZ6Wnh32LYA+VVO8G66zAXGlsOCMGCxQ8dRN2u79qBJsQp2lhgkvNf2vxwe1z/ee1YB27kjbMe9pNI5TogC2I0IiUoKk/MSbO5YVZJbku39z+hXx5fKXsGiq0tyq4gAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAMGRm/lIRcy/+ytunLDm+e8jOW7xfcSayxDmzpAAAAABHnVW/IxwG7udMVuzmgVB/2xst6j9I5RArHNola8E48G3fbh12Whk9nL4UbO63msHLSF7V9bN5E6jPWFfv8AqYyXJY9OJInxuz0QKRSODYMLWhOZ2v8QhASOe9jb6fhZmoAL/0yHNoiWwg/BQHPr8ctao3X+gf5NvcgrpN+3Xni0P/on9df2SnTAmx8pWHneSwmrNt/J3VFLMhqns4zl6CkkdEVX5Afu+rs01ol+cisIqz57wyMevzZXRo+xw14nBQkABQLAXBUACQAJA2QAAAAAAAAADAYAAgAhCAsBAQpGCw0ABgMEAiUhCgoOCiAdIB4cAwUkJRsgDQsLIiAHGh8KIBAgEhMFASQjDyANCwsiIBQRCiAZIBcVAQQhIxYgDQsLIiAYCizBIJszQdacgQIDAAAAJmQAASZkAQImZAIDQA0DAAAAAABiJhUAAAAAAAEAAAsDAgAAAQkDHXGaq1IbQtL0hcAKGVfl/Jlulv4v+RpE78Jq9Ln/mT4GYFpbKixcBlBhSiteTMOvz+ujj58uZtkNbPgHM3DTp2g1sQQ3yRRKTaeQU/qaBTkwNDIxAMgtRSovtAzeEt+2SiOgoSbZUbuEwRtHtBM2BKmvfCPXBjc0OTE4MwA="
//
//            log("======= using VersionedTransaction ========")
//            val transaction = VersionedTransaction.deserialize(swapTx)
//            val serialize = transaction.serialize()
//            val encoded = Base64.getEncoder().encodeToString(serialize)
//
//            log("serialize.size: ${serialize.size}")
//            log("encoded: $encoded")
//
//            GlobalScope.launch(Dispatchers.IO) {
//                Ed25519KeyRepositoryNew.getByPublicKey(
//                    selectedPublicKey
//                )?.let {
//                    log("======= using EncodedTransaction ========")
//                    val transaction = EncodedTransaction.deserialize(swapTx)
//                    val serialize = transaction.serialize()
//                    val encoded = Base64.getEncoder().encodeToString(serialize)
//
//                    log("serialize.size: ${serialize.size}")
//                    log("encoded: $encoded")
//
//                    transaction.sign(it)
//
//                    try {
////                        val connection = Connection(QuickNodeUrl.MAINNNET)
////                        val signature = connection.sendTransaction(transaction)
//
//                        log("======= after EncodedTransaction ========")
//                        val serialize = transaction.serialize()
//                        log("a4sign serialize.size: ${serialize.size}")
//                        val encodedTransaction = Base64.getEncoder().encodeToString(serialize)
//                        log("encodedTransaction: $encodedTransaction")
//
//                        //js  -> AX0/rg2a/9s+X49aZ+f0R8aFHbNTUABx7BO6JicRzTX/8WLkGyfq97CaqgyOt98Rguxa/kzySJHi0MvonCNKFQKAAQAHDw+dqEHJxAdcnStFER/iX/nFd/O7SOose3GUNRCiu8u9C3UgzyHnGpR6VJ/vU2WjD5DjwsvxD8xvVd650NGXdFMPP/lHAYEuGWoSOE8V5JKqdJcnKouC7evvFOlsDweRhmZ/y1OFUjaxGSYPYSYxL38DI5jSwryQ6RMhtMx+2NckcTN9kd8r51/xwc6IwfXwKTzn7kVi9u4+BF+gf6rTEd13VyZ6Wnh32LYA+VVO8G66zAXGlsOCMGCxQ8dRN2u79qBJsQp2lhgkvNf2vxwe1z/ee1YB27kjbMe9pNI5TogC2I0IiUoKk/MSbO5YVZJbku39z+hXx5fKXsGiq0tyq4gAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAMGRm/lIRcy/+ytunLDm+e8jOW7xfcSayxDmzpAAAAABHnVW/IxwG7udMVuzmgVB/2xst6j9I5RArHNola8E48G3fbh12Whk9nL4UbO63msHLSF7V9bN5E6jPWFfv8AqYyXJY9OJInxuz0QKRSODYMLWhOZ2v8QhASOe9jb6fhZmoAL/0yHNoiWwg/BQHPr8ctao3X+gf5NvcgrpN+3Xni0P/on9df2SnTAmx8pWHneSwmrNt/J3VFLMhqns4zl6CkkdEVX5Afu+rs01ol+cisIqz57wyMevzZXRo+xw14nBQkABQLAXBUACQAJA2QAAAAAAAAADAYAAgAhCAsBAQpGCw0ABgMEAiUhCgoOCiAdIB4cAwUkJRsgDQsLIiAHGh8KIBAgEhMFASQjDyANCwsiIBQRCiAZIBcVAQQhIxYgDQsLIiAYCizBIJszQdacgQIDAAAAJmQAASZkAQImZAIDQA0DAAAAAABiJhUAAAAAAAEAAAsDAgAAAQkDHXGaq1IbQtL0hcAKGVfl/Jlulv4v+RpE78Jq9Ln/mT4GYFpbKixcBlBhSiteTMOvz+ujj58uZtkNbPgHM3DTp2g1sQQ3yRRKTaeQU/qaBTkwNDIxAMgtRSovtAzeEt+2SiOgoSbZUbuEwRtHtBM2BKmvfCPXBjc0OTE4MwA=
//                        //got -> AgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACoFyJ6dkvjZSNZe33nIM6HQR/HKs8xY+pkmWGUDXkts76ZeOhvkQ9iAc2kXgf9euBrs+/K5x5ACkbbx+10KMAAAQAHDw+dqEHJxAdcnStFER/iX/nFd/O7SOose3GUNRCiu8u9C3UgzyHnGpR6VJ/vU2WjD5DjwsvxD8xvVd650NGXdFMPP/lHAYEuGWoSOE8V5JKqdJcnKouC7evvFOlsDweRhmZ/y1OFUjaxGSYPYSYxL38DI5jSwryQ6RMhtMx+2NckcTN9kd8r51/xwc6IwfXwKTzn7kVi9u4+BF+gf6rTEd13VyZ6Wnh32LYA+VVO8G66zAXGlsOCMGCxQ8dRN2u79qBJsQp2lhgkvNf2vxwe1z/ee1YB27kjbMe9pNI5TogC2I0IiUoKk/MSbO5YVZJbku39z+hXx5fKXsGiq0tyq4gAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAMGRm/lIRcy/+ytunLDm+e8jOW7xfcSayxDmzpAAAAABHnVW/IxwG7udMVuzmgVB/2xst6j9I5RArHNola8E48G3fbh12Whk9nL4UbO63msHLSF7V9bN5E6jPWFfv8AqYyXJY9OJInxuz0QKRSODYMLWhOZ2v8QhASOe9jb6fhZmoAL/0yHNoiWwg/BQHPr8ctao3X+gf5NvcgrpN+3Xni0P/on9df2SnTAmx8pWHneSwmrNt/J3VFLMhqns4zl6CkkdEVX5Afu+rs01ol+cisIqz57wyMevzZXRo+xw14nBQkABQLAXBUACQAJA2QAAAAAAAAADAYAAgAhCAsBAQpGCw0ABgMEAiUhCgoOCiAdIB4cAwUkJRsgDQsLIiAHGh8KIBAgEhMFASQjDyANCwsiIBQRCiAZIBcVAQQhIxYgDQsLIiAYCizBIJszQdacgQIDAAAAJmQAASZkAQImZAIDQA0DAAAAAABiJhUAAAAAAAEAAAsDAgAAAQkDHXGaq1IbQtL0hcAKGVfl/Jlulv4v+RpE78Jq9Ln/mT4GYFpbKixcBlBhSiteTMOvz+ujj58uZtkNbPgHM3DTp2g1sQQ3yRRKTaeQU/qaBTkwNDIxAMgtRSovtAzeEt+2SiOgoSbZUbuEwRtHtBM2BKmvfCPXBjc0OTE4MwA=
//                    } catch (e: Exception) {
//                        e.printStackTrace()
//                    }
//                }
//            }
//
////            val intent = Intent(Intent.ACTION_VIEW)
////            intent.data = Uri.parse("showallet://swap?mint=EPjFWdd5AufqSSqeM2qN1xzybapC8G4wEGGkZwyTDt1v&amount=0.001")
////            requireContext().startActivity(intent)
//        }

        mBinding.homeButtonsRv.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        mBinding.homeButtonsRv.adapter = HomeButtonAdapter(
            requireContext(),
            listOf(
                HomeButtonAdapter.HomeButton(R.drawable.ic_receive, R.string.wallet_receive) {
                    TerminalActivity.start(
                        requireContext(),
                        TerminalActivity.Companion.FragmentTypeEnum.RECEIVE,
                        selectedPublicKey
                    )
                },
                HomeButtonAdapter.HomeButton(R.drawable.ic_send, R.string.wallet_send) {
                    TerminalActivity.start(
                        requireContext(),
                        TerminalActivity.Companion.FragmentTypeEnum.SEARCH,
                        selectedPublicKey
                    )
                },
                HomeButtonAdapter.HomeButton(R.drawable.ic_swap, R.string.wallet_swap) {
                    TerminalActivity.start(
                        requireContext(),
                        TerminalActivity.Companion.FragmentTypeEnum.SWAP,
                        selectedPublicKey
                    )
                },
                HomeButtonAdapter.HomeButton(R.drawable.ic_nft, R.string.wallet_nfts) {

                    TerminalActivity.start(
                        requireContext(),
                        TerminalActivity.Companion.FragmentTypeEnum.NFT,
                        selectedPublicKey
                    )
                },
                HomeButtonAdapter.HomeButton(R.drawable.ic_nft, R.string.wallet_transactions) {
                    TerminalActivity.start(
                        requireContext(),
                        TerminalActivity.Companion.FragmentTypeEnum.TRANSACTION,
                        selectedPublicKey
                    )
                },
            )
        )

        mBinding.swipeRefresh.setOnRefreshListener {
            mViewModel.getTokenAccountsByOwner(PublicKey(selectedPublicKey ?: ""))
        }
        mBinding.swipeRefresh.isRefreshing = true
    }

    @SuppressLint("SetTextI18n")
    override fun initLiveDataObserve() {
        mViewModel.getBlanceLiveData.observeForever { mBinding.tvBalance.text = "$${it} USD" }

        mViewModel.getTokens.observeForever {
            mBinding.swipeRefresh.isRefreshing = false

            val adapter =
                TokenAccountsByOwnerAdapter(requireActivity(), it, false, "", selectedPublicKey)
            mBinding.rvTokenList.adapter = adapter

            mViewModel.getBalance(it)
        }
        mViewModel.getBlanceTotal.observeForever {
            mBinding.tvBalance.text = it
        }

        FlowEventBus.with<Boolean>(EventConstants.EVENT_REFRESH_BALANCE)
            .register(requireActivity()) {
                mViewModel.getTokenAccountsByOwner(PublicKey(selectedPublicKey ?: ""))
            }
    }

    override fun initRequestData() {
    }

}