/*
 * Copyright (c) 2022 Solana Mobile Inc.
 */

package com.showtime.wallet.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.showtime.wallet.R
import com.showtime.wallet.databinding.FragmentAssociateBinding
import com.showtime.wallet.ShoWalletViewModel
import com.showtime.wallet.ShoWalletViewModel.WalletServiceRequest
import kotlinx.coroutines.launch

class AssociateFragment : Fragment() {
    private val activityViewModel: ShoWalletViewModel by activityViewModels()
    private lateinit var viewBinding: FragmentAssociateBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentAssociateBinding.inflate(layoutInflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                activityViewModel.walletServiceEvents.collect { request ->
                    val navController = findNavController()
                    // If several events are emitted back-to-back (e.g. during session
                    // teardown), this fragment may not have had a chance to transition
                    // lifecycle states. Only navigate if we believe we are still here.
                    if (navController.currentDestination?.id != R.id.fragment_associate) {
                        return@collect
                    }

                    when (request) {
                        is WalletServiceRequest.None ->
                            Unit
                        is WalletServiceRequest.AuthorizeDapp ->
                            navController.navigate(AssociateFragmentDirections.actionAuthorizeDapp())
                        is WalletServiceRequest.SignIn ->
                            navController.navigate(AssociateFragmentDirections.actionSignIn())
                        is WalletServiceRequest.SignPayloads,
                        is WalletServiceRequest.SignAndSendTransactions ->
                            navController.navigate(AssociateFragmentDirections.actionSignPayload())
                        is WalletServiceRequest.SessionTerminated ->
                            Unit
                        is WalletServiceRequest.LowPowerNoConnection ->
                            Unit
                    }
                }
            }
        }
    }
}