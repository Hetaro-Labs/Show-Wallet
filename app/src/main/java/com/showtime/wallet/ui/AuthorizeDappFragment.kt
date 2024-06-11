package com.showtime.wallet.ui

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.showtime.wallet.ShoWalletViewModel
import com.showtime.wallet.R
import com.showtime.wallet.databinding.FragmentAuthorizeDappBinding
import com.showtime.wallet.usecase.ClientTrustUseCase
import kotlinx.coroutines.launch

class AuthorizeDappFragment : Fragment() {
    private val activityViewModel: ShoWalletViewModel by activityViewModels()
    private lateinit var viewBinding: FragmentAuthorizeDappBinding

    private var request: ShoWalletViewModel.WalletServiceRequest.AuthorizeDapp? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentAuthorizeDappBinding.inflate(layoutInflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                activityViewModel.walletServiceEvents.collect { request ->
                    when (request) {
                        is ShoWalletViewModel.WalletServiceRequest.AuthorizeDapp -> {
                            this@AuthorizeDappFragment.request = request

                            if (request.request.identityUri?.isAbsolute == true &&
                                request.request.iconRelativeUri?.isHierarchical == true
                            ) {
                                val uri = Uri.withAppendedPath(
                                    request.request.identityUri!!,
                                    request.request.iconRelativeUri!!.encodedPath
                                )
                                viewBinding.imageIcon.setImageURI(uri)
                            }
                            viewBinding.textName.text = request.request.identityName ?: "<no name>"
                            viewBinding.textUri.text =
                                request.request.identityUri?.toString() ?: "<no URI>"
                            viewBinding.textCluster.text = request.request.cluster
                            viewBinding.textVerificationState.setText(
                                when (request.sourceVerificationState) {
                                    is ClientTrustUseCase.VerificationInProgress -> R.string.str_verification_in_progress
                                    is ClientTrustUseCase.NotVerifiable -> R.string.str_verification_not_verifiable
                                    is ClientTrustUseCase.VerificationFailed -> R.string.str_verification_failed
                                    is ClientTrustUseCase.VerificationSucceeded -> R.string.str_verification_succeeded
                                }
                            )
                            viewBinding.textVerificationScope.text =
                                request.sourceVerificationState.authorizationScope
                        }
                        else -> {
                            this@AuthorizeDappFragment.request = null
                            // If several events are emitted back-to-back (e.g. during session
                            // teardown), this fragment may not have had a chance to transition
                            // lifecycle states. Only navigate if we believe we are still here.
                            findNavController().let { nc ->
                                if (nc.currentDestination?.id == R.id.fragment_authorize_dapp) {
                                    nc.navigate(AuthorizeDappFragmentDirections.actionAuthorizeDappComplete())
                                }
                            }
                        }
                    }
                }
            }
        }

        viewBinding.btnAuthorize.setOnClickListener {
            request?.let {
                Log.i(TAG, "Authorizing dapp")
                activityViewModel.authorizeDapp(it, true)
            }
        }

        viewBinding.btnAuthorizeX3.setOnClickListener {
            request?.let {
                Log.i(TAG, "Authorizing dapp, with 3 accounts")
                activityViewModel.authorizeDapp(it, true, 3)
            }
        }

        viewBinding.btnDecline.setOnClickListener {
            request?.let {
                Log.w(TAG, "Not authorizing dapp")
                activityViewModel.authorizeDapp(it, false)
            }
        }

        viewBinding.btnSimulateClusterNotSupported.setOnClickListener {
            request?.let {
                Log.w(TAG, "Simulating cluster not supported")
                activityViewModel.authorizeDappSimulateClusterNotSupported(it)
            }
        }

        viewBinding.btnSimulateInternalError.setOnClickListener {
            request?.let {
                Log.w(TAG, "Simulating internal error")
                activityViewModel.authorizationSimulateInternalError(it)
            }
        }
    }

    companion object {
        private val TAG = AuthorizeDappFragment::class.simpleName
    }
}