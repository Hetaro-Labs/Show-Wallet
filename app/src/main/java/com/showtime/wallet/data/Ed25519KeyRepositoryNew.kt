package com.showtime.wallet.data

import android.util.Base64
import android.util.Log
import androidx.room.Room
import com.amez.mall.lib_base.utils.Logger
import com.showtime.wallet.ShowVaultApplication
import com.showtime.wallet.utils.CryptoUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bouncycastle.crypto.AsymmetricCipherKeyPair
import org.bouncycastle.crypto.generators.Ed25519KeyPairGenerator
import org.bouncycastle.crypto.params.Ed25519KeyGenerationParameters
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters
import org.sol4k.Keypair
import java.security.SecureRandom

object Ed25519KeyRepositoryNew {

    private val TAG = Ed25519KeyRepositoryNew::class.simpleName

    private val db by lazy {
        Room.databaseBuilder(
            ShowVaultApplication.instance(),
            Ed25519KeyDatabase::class.java,
            "keys"
        ).build()
    }

    suspend fun generateKeypair(): AsymmetricCipherKeyPair {
        val kp = withContext(Dispatchers.IO) {
            val kpg = Ed25519KeyPairGenerator()
            kpg.init(Ed25519KeyGenerationParameters(SecureRandom()))
            val keypair = kpg.generateKeyPair()
            val publicKey = keypair.public as Ed25519PublicKeyParameters
            val privateKey = keypair.private as Ed25519PrivateKeyParameters
            val publicKeyBase64 =
                Base64.encodeToString(publicKey.encoded, Base64.NO_PADDING or Base64.NO_WRAP)
            val id = db.keysDao().insert(
                Ed25519KeyPair(publicKeyBase64 = publicKeyBase64, privateKey = privateKey.encoded)
            )
            Log.d(TAG, "Inserted key entry with id=$id for $publicKeyBase64")
            keypair
        }
        return kp
    }

    suspend fun getKeypair(publicKeyRaw: ByteArray): AsymmetricCipherKeyPair? {
        val publicKeyBase64 =
            Base64.encodeToString(publicKeyRaw, Base64.NO_PADDING or Base64.NO_WRAP)
        return withContext(Dispatchers.IO) {
            db.keysDao().get(publicKeyBase64)?.let { keypair ->
                val privateKeyParams = Ed25519PrivateKeyParameters(keypair.privateKey, 0)
                AsymmetricCipherKeyPair(privateKeyParams.generatePublicKey(), privateKeyParams)
            }
        }
    }

    suspend fun getOne(): AsymmetricCipherKeyPair? {
        return withContext(Dispatchers.IO) {
            db.keysDao().getAll()?.let { keypairs ->
                if (keypairs.isNotEmpty()) {
                    val privateKeyParams = Ed25519PrivateKeyParameters(keypairs[0].privateKey, 0)
                    AsymmetricCipherKeyPair(privateKeyParams.generatePublicKey(), privateKeyParams)
                } else {
                    null
                }
            }
        }
    }

    suspend fun getAll(): List<AsymmetricCipherKeyPair>? {
        return withContext(Dispatchers.IO) {
            db.keysDao().getAll()?.map { keypair ->
                val privateKeyParams = Ed25519PrivateKeyParameters(keypair.privateKey, 0)
                AsymmetricCipherKeyPair(privateKeyParams.generatePublicKey(), privateKeyParams)
            }
        }
    }

    suspend fun getByPublicKey(key: String): Keypair? {
        return withContext(Dispatchers.IO) {
            db.keysDao().getAll()?.map{ keypair ->
                val privateKeyParams = Ed25519PrivateKeyParameters(keypair.privateKey, 0)
                val asymmetric = AsymmetricCipherKeyPair(privateKeyParams.generatePublicKey(), privateKeyParams)
                CryptoUtils.convertKeypair(asymmetric)
            }?.find { keypair ->
                Logger.d("getByPublicKey", keypair.publicKey.toBase58())
                Logger.d("getByPublicKey", "->" + key)
                keypair.publicKey.toBase58() == key
            }
            
//            if (keypair != null){
//                val privateKeyParams = Ed25519PrivateKeyParameters(keypair.privateKey, 0)
//                AsymmetricCipherKeyPair(privateKeyParams.generatePublicKey(), privateKeyParams)
//            }else{
//                null
//            }
        }
    }

    /**
     * insert one Ed25519KeyPair
     */
    suspend fun insertOne(keyPair: AsymmetricCipherKeyPair) {
        val kp = withContext(Dispatchers.IO) {
            val publicKey = keyPair.public as Ed25519PublicKeyParameters
            val privateKey = keyPair.private as Ed25519PrivateKeyParameters
            val publicKeyBase64 =
                Base64.encodeToString(publicKey.encoded, Base64.NO_PADDING or Base64.NO_WRAP)
            val id = db.keysDao().insert(
                Ed25519KeyPair(publicKeyBase64 = publicKeyBase64, privateKey = privateKey.encoded)
            )
            Log.d(TAG, "Inserted key entry with id=$id for $publicKeyBase64")
        }
    }
}