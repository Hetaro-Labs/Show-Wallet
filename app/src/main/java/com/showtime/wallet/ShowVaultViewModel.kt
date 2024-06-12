/*
 * Copyright (c) 2022 Solana Mobile Inc.
 */

package com.showtime.wallet

import android.app.Application
import android.content.Intent
import android.net.Uri
import android.util.Base64
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.funkatronics.encoders.Base58
import com.showtime.wallet.usecase.ClientTrustUseCase
import com.showtime.wallet.usecase.SendTransactionsUseCase
import com.showtime.wallet.usecase.SolanaSigningUseCase
import com.solana.digitalassetlinks.BuildConfig
import com.solana.mobilewalletadapter.common.ProtocolContract
import com.solana.mobilewalletadapter.common.signin.SignInWithSolana
import com.solana.mobilewalletadapter.walletlib.association.AssociationUri
import com.solana.mobilewalletadapter.walletlib.association.LocalAssociationUri
import com.solana.mobilewalletadapter.walletlib.authorization.AuthIssuerConfig
import com.solana.mobilewalletadapter.walletlib.protocol.MobileWalletAdapterConfig
import com.solana.mobilewalletadapter.walletlib.scenario.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters
import java.nio.charset.StandardCharsets

class ShowVaultViewModel(application: Application) : AndroidViewModel(application) {
    private val _walletServiceEvents =
        MutableStateFlow<WalletServiceRequest>(WalletServiceRequest.None)
    val walletServiceEvents = _walletServiceEvents.asSharedFlow() // expose as event stream, rather than a stateful object

    private var clientTrustUseCase: ClientTrustUseCase? = null
    private var scenario: LocalScenario? = null

    fun processLaunch(intent: Intent?, callingPackage: String?): Boolean {
        if (intent == null) {
            Log.e(TAG, "No Intent available")
            return false
        } else if (intent.data == null) {
            Log.e(TAG, "Intent has no data URI")
            return false
        }

        val associationUri = intent.data?.let { uri -> AssociationUri.parse(uri) }
        if (associationUri == null) {
            Log.e(TAG, "Unsupported association URI '${intent.data}'")
            return false
        } else if (associationUri !is LocalAssociationUri) {
            Log.w(TAG, "Current implementation of fakewallet does not support remote clients")
            return false
        }
        Log.d(TAG, "processLaunch... ${intent.data}")

        clientTrustUseCase = ClientTrustUseCase(
            viewModelScope,
            getApplication<Application>().packageManager,
            callingPackage,
            associationUri
        )

        scenario =
            if (BuildConfig.DEBUG) {
//            if (BuildConfig.PROTOCOL_VERSION == SessionProperties.ProtocolVersion.LEGACY) {
            // manually create the scenario here so we can override the association protocol version
            // this forces ProtocolVersion.LEGACY to simulate a wallet using walletlib 1.x (for testing)
            LocalWebSocketServerScenario(
                getApplication<ShowVaultApplication>().applicationContext,
                MobileWalletAdapterConfig(
                    true,
                    10,
                    10,
                    arrayOf(MobileWalletAdapterConfig.LEGACY_TRANSACTION_VERSION, 0),
                    LOW_POWER_NO_CONNECTION_TIMEOUT_MS
                ),
                AuthIssuerConfig("showallet"),
                MobileWalletAdapterScenarioCallbacks(),
                associationUri.associationPublicKey,
                listOf(),
                associationUri.port,
            )
        } else {
            associationUri.createScenario(
                getApplication<ShowVaultApplication>().applicationContext,
                MobileWalletAdapterConfig(
                    10,
                    10,
                    arrayOf(MobileWalletAdapterConfig.LEGACY_TRANSACTION_VERSION, 0),
                    LOW_POWER_NO_CONNECTION_TIMEOUT_MS,
                    arrayOf(
                        ProtocolContract.FEATURE_ID_SIGN_TRANSACTIONS,
                        ProtocolContract.FEATURE_ID_SIGN_IN_WITH_SOLANA
                    )
                ),
                AuthIssuerConfig("showallet"),
                MobileWalletAdapterScenarioCallbacks()
            )
        }.also { it.start() }

        return true
    }

    override fun onCleared() {
        scenario?.close()
        scenario = null
    }


    private suspend fun getAccounts(): List<AuthorizedAccount> {
        val keypair = getApplication<ShowVaultApplication>().keyRepository.getOne()
        if (keypair == null){
            Log.w(TAG, "get no wallet")
            return emptyList()
        }

        val publicKey = keypair.public as Ed25519PublicKeyParameters
        Log.d(TAG, "get public key(pub=${publicKey.encoded.contentToString()}) for authorize request")
        val accounts = listOf(buildAccount(publicKey.encoded, "showallet account"))

        return accounts
    }


    fun authorizeDapp(
        request: WalletServiceRequest.AuthorizationRequest,
        authorized: Boolean,
        numAccounts: Int = 1
    ) {
        if (rejectStaleRequest(request)) {
            return
        }

        viewModelScope.launch {
            if (authorized) {
                Log.d(TAG, "authorizing...")
                val accounts = getAccounts()

//                val accounts = (0 until numAccounts).map {
//                    val keypair = getApplication<FakeWalletApplication>().keyRepository.generateKeypair()
//                    val publicKey = keypair.public as Ed25519PublicKeyParameters
//                    Log.d(TAG, "Generated a new keypair (pub=${publicKey.encoded.contentToString()}) for authorize request")
//                    buildAccount(publicKey.encoded, "fakewallet account $it")
//                }

                request.request.completeWithAuthorize(accounts.toTypedArray(), null,
                    request.sourceVerificationState.authorizationScope.encodeToByteArray(), null)
            } else {
                request.request.completeWithDecline()
            }
        }
    }

    fun authorizeDappSimulateClusterNotSupported(
        request: WalletServiceRequest.AuthorizeDapp
    ) {
        if (rejectStaleRequest(request)) {
            return
        }

        request.request.completeWithClusterNotSupported()
    }

    fun authorizationSimulateInternalError(
        request: WalletServiceRequest.AuthorizationRequest
    ) {
        if (rejectStaleRequest(request)) {
            return
        }

        request.request.completeWithInternalError(RuntimeException("Internal error during authorize: -1234"))
    }

    fun signIn(
        request: WalletServiceRequest.SignIn,
        authorizeSignIn: Boolean
    ) {
        if (rejectStaleRequest(request)) {
            return
        }

        viewModelScope.launch {
            if (authorizeSignIn) {
//                val keypair = getApplication<FakeWalletApplication>().keyRepository.generateKeypair()
                val keypair = getApplication<ShowVaultApplication>().keyRepository.getOne()
                if (keypair == null){
                    Log.w(TAG, "get no wallet")
                    return@launch
                }

                val publicKey = keypair.public as Ed25519PublicKeyParameters
                Log.d(TAG, "get keypair (pub=${publicKey.encoded.contentToString()}) for authorize request")

                val address = Base64.encodeToString(publicKey.encoded, Base64.NO_WRAP)
                val siwsMessage = request.signInPayload.prepareMessage(address)
                val signResult = try {
                    val messageBytes = siwsMessage.encodeToByteArray()
                    SolanaSigningUseCase.signMessage(messageBytes, listOf(keypair))
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "failed to sign SIWS payload", e)
                    request.request.completeWithInternalError(e)
                    return@launch
                }

                val signInResult = SignInResult(publicKey.encoded,
                    siwsMessage.encodeToByteArray(), signResult.signature, "ed25519")

                val account = buildAccount(publicKey.encoded, "fakewallet")
                request.request.completeWithAuthorize(account, null,
                    request.sourceVerificationState.authorizationScope.encodeToByteArray(), signInResult)
            } else {
                request.request.completeWithDecline()
            }
        }
    }

    fun signInSimulateSignInNotSupported(
        request: WalletServiceRequest.SignIn
    ) {
        authorizeDapp(request, true)
    }

    fun signPayloadsSimulateSign(request: WalletServiceRequest.SignPayloads) {
        if (rejectStaleRequest(request)) {
            return
        }

        viewModelScope.launch {

            val valid = BooleanArray(request.request.payloads.size) { true }
            val signedPayloads = when (request) {
                is WalletServiceRequest.SignTransactions -> {
                    Array(request.request.payloads.size) { i ->
                        val tx = request.request.payloads[i]
                        val keypairs = SolanaSigningUseCase.getSignersForTransaction(tx).mapNotNull {
                            getApplication<ShowVaultApplication>().keyRepository.getKeypair(it)
                        }
                        Log.d(TAG, "Simulating transaction signing with ${keypairs.joinToString {
                            Base58.encodeToString((it.public as Ed25519PublicKeyParameters).encoded)
                        }}")
                        try {
                            SolanaSigningUseCase.signTransaction(tx, keypairs).signedPayload
                        } catch (e: IllegalArgumentException) {
                            Log.w(TAG, "Transaction [$i] is not a valid Solana transaction", e)
                            valid[i] = false
                            byteArrayOf()
                        }
                    }
                }
                is WalletServiceRequest.SignMessages -> {
                    val keypairs = request.request.addresses.map {
                        val keypair = getApplication<ShowVaultApplication>().keyRepository.getKeypair(it)
                        check(keypair != null) { "Unknown public key for signing request" }
                        keypair
                    }
                    Log.d(TAG, "Simulating message signing with ${keypairs.joinToString {
                        Base58.encodeToString((it.public as Ed25519PublicKeyParameters).encoded)
                    }}")
                    Array(request.request.payloads.size) { i ->
                        // TODO: wallet should check that the payload is NOT a transaction
                        //  to ensure the user is not being tricked into signing a transaction
                        SolanaSigningUseCase.signMessage(request.request.payloads[i], keypairs).signedPayload
                    }
                }
            }

            if (valid.all { it }) {
                request.request.completeWithSignedPayloads(signedPayloads)
            } else {
                Log.e(TAG, "One or more transactions not valid")
                request.request.completeWithInvalidPayloads(valid)
            }
        }
    }

    fun signPayloadsDeclined(request: WalletServiceRequest.SignPayloads) {
        if (rejectStaleRequest(request)) {
            return
        }
        request.request.completeWithDecline()
    }

    fun signPayloadsSimulateAuthTokenInvalid(request: WalletServiceRequest.SignPayloads) {
        if (rejectStaleRequest(request)) {
            return
        }
        request.request.completeWithAuthorizationNotValid()
    }

    fun signPayloadsSimulateInvalidPayloads(request: WalletServiceRequest.SignPayloads) {
        if (rejectStaleRequest(request)) {
            return
        }
        val valid = BooleanArray(request.request.payloads.size) { i -> i != 0 }
        request.request.completeWithInvalidPayloads(valid)
    }

    fun signPayloadsSimulateTooManyPayloads(request: WalletServiceRequest.SignPayloads) {
        if (rejectStaleRequest(request)) {
            return
        }
        request.request.completeWithTooManyPayloads()
    }

    fun signPayloadsSimulateInternalError(request: WalletServiceRequest.SignPayloads) {
        if (rejectStaleRequest(request)) {
            return
        }
        request.request.completeWithInternalError(RuntimeException("Internal error during signing: -1234"))
    }

    fun signAndSendTransactionsSimulateSign(request: WalletServiceRequest.SignAndSendTransactions) {
        viewModelScope.launch {
            val signingResults = request.request.payloads.map { tx ->
                val keypairs = SolanaSigningUseCase.getSignersForTransaction(tx).mapNotNull {
                    getApplication<ShowVaultApplication>().keyRepository.getKeypair(it)
                }
                Log.d(TAG, "Simulating transaction signing with ${keypairs.joinToString {
                    Base58.encodeToString((it.public as Ed25519PublicKeyParameters).encoded)
                }}")
                try {
                    SolanaSigningUseCase.signTransaction(tx, keypairs)
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "not a valid Solana transaction", e)
                    SolanaSigningUseCase.Result(byteArrayOf(), byteArrayOf())
                }
            }

            val valid = signingResults.map { result -> result.signature.isNotEmpty() }
            if (valid.all { it }) {
                val signatures = signingResults.map { result -> result.signature }
                val signedTransactions = signingResults.map { result -> result.signedPayload }
                val requestWithSignatures = request.copy(
                    signatures = signatures.toTypedArray(),
                    signedTransactions = signedTransactions.toTypedArray()
                )
                if (!updateExistingRequest(request, requestWithSignatures)) {
                    return@launch
                }
            } else {
                Log.e(TAG, "One or more transactions not valid")
                if (rejectStaleRequest(request)) {
                    return@launch
                }
                request.request.completeWithInvalidSignatures(valid.toBooleanArray())
            }
        }
    }

    fun signAndSendTransactionsDeclined(request: WalletServiceRequest.SignAndSendTransactions) {
        if (rejectStaleRequest(request)) {
            return
        }
        request.request.completeWithDecline()
    }

    fun signAndSendTransactionsSimulateAuthTokenInvalid(request: WalletServiceRequest.SignAndSendTransactions) {
        if (rejectStaleRequest(request)) {
            return
        }
        request.request.completeWithAuthorizationNotValid()
    }

    fun signAndSendTransactionsSimulateInvalidPayloads(request: WalletServiceRequest.SignAndSendTransactions) {
        if (rejectStaleRequest(request)) {
            return
        }
        val valid = BooleanArray(request.request.payloads.size) { i -> i != 0 }
        request.request.completeWithInvalidSignatures(valid)
    }

    fun signAndSendTransactionsSubmitted(request: WalletServiceRequest.SignAndSendTransactions) {
        if (rejectStaleRequest(request)) {
            return
        }

        Log.d(TAG, "Simulating transactions submitted on cluster=${request.request.chain}")

        request.request.completeWithSignatures(request.signatures!!)
    }

    fun signAndSendTransactionsNotSubmitted(request: WalletServiceRequest.SignAndSendTransactions) {
        if (rejectStaleRequest(request)) {
            return
        }

        Log.d(TAG, "Simulating transactions NOT submitted on cluster=${request.request.chain}")

        val signatures = request.signatures!!
        val notSubmittedSignatures = Array(signatures.size) { i ->
            if (i != 0) signatures[i] else null
        }
        request.request.completeWithNotSubmitted(notSubmittedSignatures)
    }

    fun signAndSendTransactionsSend(request: WalletServiceRequest.SignAndSendTransactions) {
        if (rejectStaleRequest(request)) {
            return
        }

        Log.d(TAG, "Sending transactions to ${request.endpointUri}")

        viewModelScope.launch(Dispatchers.IO) {
            request.signedTransactions!!
            request.signatures!!

            try {
                SendTransactionsUseCase(
                    request.endpointUri,
                    request.signedTransactions,
                    request.request.minContextSlot,
                    request.request.commitment,
                    request.request.skipPreflight,
                    request.request.maxRetries
                )
                Log.d(TAG, "All transactions submitted via RPC")
                request.request.completeWithSignatures(request.signatures)
            } catch (e: SendTransactionsUseCase.InvalidTransactionsException) {
                Log.e(TAG, "Failed submitting transactions via RPC", e)
                request.request.completeWithInvalidSignatures(e.valid)
            }
        }
    }

    fun signAndSendTransactionsSimulateTooManyPayloads(request: WalletServiceRequest.SignAndSendTransactions) {
        if (rejectStaleRequest(request)) {
            return
        }
        request.request.completeWithTooManyPayloads()
    }

    fun signAndSendTransactionsSimulateInternalError(request: WalletServiceRequest.SignAndSendTransactions) {
        if (rejectStaleRequest(request)) {
            return
        }
        request.request.completeWithInternalError(RuntimeException("Internal error during sign_and_send_transactions: -1234"))
    }

    private fun rejectStaleRequest(request: WalletServiceRequest): Boolean {
        if (!_walletServiceEvents.compareAndSet(
                request,
                WalletServiceRequest.None
            )
        ) {
            Log.w(TAG, "Discarding stale request")
            if (request is WalletServiceRequest.MobileWalletAdapterRemoteRequest) {
                request.request.cancel()
            }
            return true
        }
        return false
    }

    private fun <T : WalletServiceRequest.MobileWalletAdapterRemoteRequest> updateExistingRequest(
        request: T,
        updated: T
    ): Boolean {
        require(request.request === updated.request) { "When updating a request, the same underlying ScenarioRequest is expected" }
        if (!_walletServiceEvents.compareAndSet(request, updated)
        ) {
            Log.w(TAG, "Discarding stale request")
            request.request.cancel()
            return false
        }
        return true
    }

    private fun cancelAndReplaceRequest(request: WalletServiceRequest) {
        val oldRequest = _walletServiceEvents.getAndUpdate { request }
        if (oldRequest is WalletServiceRequest.MobileWalletAdapterRemoteRequest) {
            oldRequest.request.cancel()
        }
    }

    private fun buildAccount(publicKey: ByteArray, label: String, icon: Uri? = null,
                             chains: Array<String>? = null, features: Array<String>? = null ) =
        AuthorizedAccount(
            publicKey, Base58.encodeToString(publicKey), "base58",
            label, icon, chains, features
        )

    private fun chainOrClusterToRpcUri(chainOrCluster: String?): Uri {
        return when (chainOrCluster) {
            ProtocolContract.CHAIN_SOLANA_MAINNET,
            ProtocolContract.CLUSTER_MAINNET_BETA ->
                Uri.parse("https://api.mainnet-beta.solana.com")
            ProtocolContract.CHAIN_SOLANA_DEVNET,
            ProtocolContract.CLUSTER_DEVNET ->
                Uri.parse("https://api.devnet.solana.com")
            ProtocolContract.CHAIN_SOLANA_TESTNET,
            ProtocolContract.CLUSTER_TESTNET ->
                Uri.parse("https://api.testnet.solana.com")
            else -> throw IllegalArgumentException("Unsupported chain/cluster: $chainOrCluster")
        }
    }

    private inner class MobileWalletAdapterScenarioCallbacks : LocalScenario.Callbacks {
        override fun onScenarioReady() = Unit
        override fun onScenarioServingClients() = Unit
        override fun onScenarioServingComplete() {
            viewModelScope.launch(Dispatchers.Main) {
                scenario?.close()
                cancelAndReplaceRequest(WalletServiceRequest.None)
            }
        }
        override fun onScenarioComplete() = Unit
        override fun onScenarioError() = Unit
        override fun onScenarioTeardownComplete() {
            Log.d(TAG, "onScenarioTeardownComplete")

            viewModelScope.launch {
                // No need to cancel any outstanding request; the scenario is torn down, and so
                // cancelling a request that originated from it isn't actionable
                _walletServiceEvents.emit(WalletServiceRequest.SessionTerminated)
            }
        }

        override fun onAuthorizeRequest(request: AuthorizeRequest) {
            Log.d(TAG, "onAuthorizeRequest")

            val clientTrustUseCase = clientTrustUseCase!! // should never be null if we get here

            val authorizationRequest = request.signInPayload?.let { signInPayload ->
                WalletServiceRequest.SignIn(
                    request, signInPayload,
                    clientTrustUseCase.verificationInProgress
                )
            } ?: WalletServiceRequest.AuthorizeDapp(
                request,
                clientTrustUseCase.verificationInProgress
            )
            cancelAndReplaceRequest(authorizationRequest)

            val verify = clientTrustUseCase.verifyAuthorizationSourceAsync(request.identityUri)
            viewModelScope.launch {
                val verificationState = withTimeoutOrNull(SOURCE_VERIFICATION_TIMEOUT_MS) {
                    verify.await()
                } ?: clientTrustUseCase.verificationTimedOut

                if (!updateExistingRequest(
                        authorizationRequest,
                        when (authorizationRequest) {
                            is WalletServiceRequest.AuthorizeDapp ->
                                authorizationRequest.copy(sourceVerificationState = verificationState)
                            is WalletServiceRequest.SignIn ->
                                authorizationRequest.copy(sourceVerificationState = verificationState)
                        }
                    )
                ) {
                    return@launch
                }
            }
        }

        override fun onReauthorizeRequest(request: ReauthorizeRequest) {
            Log.d(TAG, "onReauthorizeRequest")

            val reverify = clientTrustUseCase!!.verifyReauthorizationSourceAsync(
                String(request.authorizationScope, StandardCharsets.UTF_8),
                request.identityUri
            )
            viewModelScope.launch {
                val verificationState = withTimeoutOrNull(SOURCE_VERIFICATION_TIMEOUT_MS) {
                    reverify.await()
                }
                when (verificationState) {
                    is ClientTrustUseCase.VerificationInProgress -> throw IllegalStateException()
                    is ClientTrustUseCase.VerificationSucceeded -> {
                        Log.i(TAG, "Reauthorization source verification succeeded")
                        request.completeWithReauthorize()
                    }
                    is ClientTrustUseCase.NotVerifiable -> {
                        Log.i(TAG, "Reauthorization source not verifiable; approving")
                        request.completeWithReauthorize()
                    }
                    is ClientTrustUseCase.VerificationFailed -> {
                        Log.w(TAG, "Reauthorization source verification failed")
                        request.completeWithDecline()
                    }
                    null -> {
                        Log.w(TAG, "Timed out waiting for reauthorization source verification")
                        request.completeWithDecline()
                    }
                }
            }
        }

        override fun onSignTransactionsRequest(request: SignTransactionsRequest) {
            Log.d(TAG, "onSignTransactionsRequest: ")

            if (verifyPrivilegedMethodSource(request)) {
                cancelAndReplaceRequest(WalletServiceRequest.SignTransactions(request))
            } else {
                request.completeWithDecline()
            }
        }
        override fun onSignMessagesRequest(request: SignMessagesRequest) {
            Log.d(TAG, "onSignMessagesRequest")

            if (verifyPrivilegedMethodSource(request)) {
                viewModelScope.launch {
                    val accounts = getAccounts()
                    if (accounts.isEmpty()) {
                        request.completeWithAuthorizationNotValid()
                    } else {
                        cancelAndReplaceRequest(
                            WalletServiceRequest.SignMessages(
                                request
                            )
                        )
                    }
                }
            } else {
                request.completeWithDecline()
            }
        }

        override fun onSignAndSendTransactionsRequest(request: SignAndSendTransactionsRequest) {
            Log.d(TAG, "onSignAndSendTransactionsRequest: " + request.payloads)

            if (verifyPrivilegedMethodSource(request)) {
                val endpointUri = chainOrClusterToRpcUri(request.chain)
                cancelAndReplaceRequest(
                    WalletServiceRequest.SignAndSendTransactions(
                        request,
                        endpointUri
                    )
                )
            } else {
                request.completeWithDecline()
            }
        }

        private fun verifyPrivilegedMethodSource(request: VerifiableIdentityRequest): Boolean {
            return clientTrustUseCase!!.verifyPrivilegedMethodSource(
                String(request.authorizationScope, StandardCharsets.UTF_8),
                request.identityUri
            )
        }

        override fun onDeauthorizedEvent(event: DeauthorizedEvent) {
            Log.d(TAG, "'${event.identityName}' deauthorized")
            event.complete()
        }

        override fun onLowPowerAndNoConnection() {
            Log.w(TAG, "Device is in power save mode and no connection was made. The connection was likely suppressed by power save mode.")
            viewModelScope.launch {
                _walletServiceEvents.emit(WalletServiceRequest.LowPowerNoConnection)
            }
        }
    }

    sealed interface WalletServiceRequest {
        object None : WalletServiceRequest
        object SessionTerminated : WalletServiceRequest
        object LowPowerNoConnection : WalletServiceRequest

        sealed class MobileWalletAdapterRemoteRequest(open val request: ScenarioRequest) :
            WalletServiceRequest
        sealed class AuthorizationRequest(
            override val request: AuthorizeRequest,
            open val sourceVerificationState: ClientTrustUseCase.VerificationState
        ) : MobileWalletAdapterRemoteRequest(request)
        data class AuthorizeDapp(
            override val request: AuthorizeRequest,
            override val sourceVerificationState: ClientTrustUseCase.VerificationState
        ) : AuthorizationRequest(request, sourceVerificationState)
        data class SignIn(
            override val request: AuthorizeRequest,
            val signInPayload: SignInWithSolana.Payload,
            override val sourceVerificationState: ClientTrustUseCase.VerificationState
        ) : AuthorizationRequest(request, sourceVerificationState)
        sealed class SignPayloads(
            override val request: SignPayloadsRequest
        ) : MobileWalletAdapterRemoteRequest(request)
        data class SignTransactions(
            override val request: SignTransactionsRequest
        ) : SignPayloads(request)
        data class SignMessages(
            override val request: SignMessagesRequest,
//            val accounts: List<AuthorizedAccount>
        ) : SignPayloads(request)
        data class SignAndSendTransactions(
            override val request: SignAndSendTransactionsRequest,
            val endpointUri: Uri,
            val signedTransactions: Array<ByteArray>? = null,
            val signatures: Array<ByteArray>? = null
        ) : MobileWalletAdapterRemoteRequest(request)
    }

    companion object {
        private val TAG = ShowVaultViewModel::class.simpleName
        private const val SOURCE_VERIFICATION_TIMEOUT_MS = 3000L
        private const val LOW_POWER_NO_CONNECTION_TIMEOUT_MS = 3000L
    }
}