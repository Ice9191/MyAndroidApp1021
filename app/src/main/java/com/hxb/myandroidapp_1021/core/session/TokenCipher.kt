package com.hxb.myandroidapp_1021.core.session

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.inject.Inject

private const val KEYSTORE = "AndroidKeyStore"
private const val KEY_ALIAS = "pda_session_token"
private const val TRANSFORMATION = "AES/GCM/NoPadding"
private const val IV_SIZE_BYTES = 12
private const val TAG_SIZE_BITS = 128

internal class TokenCipher @Inject constructor() {
    fun encrypt(token: String, sessionMetadata: ByteArray): String {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, getOrCreateKey())
        cipher.updateAAD(sessionMetadata)
        val ciphertext = cipher.doFinal(token.encodeToByteArray())
        return Base64.encodeToString(cipher.iv + ciphertext, Base64.NO_WRAP)
    }

    fun decrypt(encryptedToken: String, sessionMetadata: ByteArray): String? {
        val payload = try {
            Base64.decode(encryptedToken, Base64.NO_WRAP)
        } catch (_: IllegalArgumentException) {
            return null
        }
        if (payload.size <= IV_SIZE_BYTES + TAG_SIZE_BITS / Byte.SIZE_BITS) return null

        val key = loadKey() ?: return null
        val parameters = GCMParameterSpec(TAG_SIZE_BITS, payload, 0, IV_SIZE_BYTES)
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.DECRYPT_MODE, key, parameters)
        cipher.updateAAD(sessionMetadata)
        return try {
            cipher.doFinal(payload, IV_SIZE_BYTES, payload.size - IV_SIZE_BYTES).decodeToString()
        } catch (_: BadPaddingException) {
            null
        }
    }

    private fun loadKey(): SecretKey? {
        val keyStore = KeyStore.getInstance(KEYSTORE).apply { load(null) }
        return keyStore.getKey(KEY_ALIAS, null) as? SecretKey
    }

    private fun getOrCreateKey(): SecretKey {
        loadKey()?.let { return it }
        val generator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, KEYSTORE)
        val parameters = KeyGenParameterSpec.Builder(
            KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setKeySize(256)
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .build()
        generator.init(parameters)
        return generator.generateKey()
    }
}
