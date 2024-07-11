package com.showtime.wallet.data

import android.util.Base64
import android.util.Log
import androidx.room.Room
import com.showtime.wallet.ShowVaultApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bouncycastle.crypto.AsymmetricCipherKeyPair
import org.bouncycastle.crypto.generators.Ed25519KeyPairGenerator
import org.bouncycastle.crypto.params.Ed25519KeyGenerationParameters
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters
import java.security.SecureRandom

object Ed25519KeyRepositoryNew {

    private val TAG = Ed25519KeyRepositoryNew::class.simpleName

    private val db by lazy {
        Room.databaseBuilder(ShowVaultApplication.instance(), Ed25519KeyDatabase::class.java, "keys").build()
    }

    suspend fun generateKeypair(): AsymmetricCipherKeyPair {
        val kp = withContext(Dispatchers.IO) {
            val kpg = Ed25519KeyPairGenerator()
            kpg.init(Ed25519KeyGenerationParameters(SecureRandom()))
            val keypair = kpg.generateKeyPair()
            val publicKey = keypair.public as Ed25519PublicKeyParameters
            val privateKey = keypair.private as Ed25519PrivateKeyParameters
            val publicKeyBase64 = Base64.encodeToString(publicKey.encoded, Base64.NO_PADDING or Base64.NO_WRAP)
            val id = db.keysDao().insert(
                Ed25519KeyPair(publicKeyBase64 = publicKeyBase64, privateKey = privateKey.encoded)
            )
            Log.d(TAG, "Inserted key entry with id=$id for $publicKeyBase64")
            keypair
        }
        return kp
    }

    suspend fun getKeypair(publicKeyRaw: ByteArray): AsymmetricCipherKeyPair? {
        val publicKeyBase64 = Base64.encodeToString(publicKeyRaw, Base64.NO_PADDING or Base64.NO_WRAP)
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
                if (keypairs.isNotEmpty()){
                    val privateKeyParams = Ed25519PrivateKeyParameters(keypairs[0].privateKey, 0)
                    AsymmetricCipherKeyPair(privateKeyParams.generatePublicKey(), privateKeyParams)
                }else{
                    null
                }
            }
        }
    }

    suspend fun getAll(): List<AsymmetricCipherKeyPair>? {
        return withContext(Dispatchers.IO) {
            db.keysDao().getAll()?.map{ keypair ->
                val privateKeyParams = Ed25519PrivateKeyParameters(keypair.privateKey, 0)
                AsymmetricCipherKeyPair(privateKeyParams.generatePublicKey(), privateKeyParams)
            }
        }
    }

    /**
     * insert one Ed25519KeyPair
     */
    suspend fun insertOne(keyPair:AsymmetricCipherKeyPair) {
        val kp = withContext(Dispatchers.IO) {
            val publicKey = keyPair.public as Ed25519PublicKeyParameters
            val privateKey = keyPair.private as Ed25519PrivateKeyParameters
            val publicKeyBase64 = Base64.encodeToString(publicKey.encoded, Base64.NO_PADDING or Base64.NO_WRAP)
            val id = db.keysDao().insert(
                Ed25519KeyPair(publicKeyBase64 = publicKeyBase64, privateKey = privateKey.encoded)
            )
            Log.d(TAG, "Inserted key entry with id=$id for $publicKeyBase64")
        }
    }
}