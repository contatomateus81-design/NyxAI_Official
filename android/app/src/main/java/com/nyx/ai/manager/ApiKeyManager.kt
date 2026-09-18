package com.nyx.ai.manager

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import org.json.JSONArray

/**
 * Gerenciador de Chaves de API com segurança e rotação.
 * Armazena as chaves de forma criptografada usando AndroidX Security.
 */
class ApiKeyManager private constructor(context: Context) {

    private val sharedPreferences: SharedPreferences

    init {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        sharedPreferences = EncryptedSharedPreferences.create(
            context,
            "nyx_secure_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    companion object {
        private const val PREFS_NAME = "nyx_secure_prefs"
        private const val KEY_API_LIST = "groq_api_keys"
        private const val MIN_KEYS = 10
        private const val MAX_KEYS = 1000

        @Volatile
        private var instance: ApiKeyManager? = null

        fun getInstance(context: Context): ApiKeyManager {
            return instance ?: synchronized(this) {
                instance ?: ApiKeyManager(context.applicationContext).also { instance = it }
            }
        }
    }

    /**
     * Salva a lista de chaves.
     * @return true se salvo com sucesso, false se a lista for inválida.
     */
    fun saveKeys(keys: List<String>): Boolean {
        if (keys.size < MIN_KEYS) return false
        if (keys.size > MAX_KEYS) return false

        // Filtra chaves vazias ou nulas
        val validKeys = keys.filter { it.isNotBlank() && it.startsWith("gsk_") }

        if (validKeys.size < MIN_KEYS) return false

        val jsonArray = JSONArray()
        validKeys.forEach { jsonArray.put(it.trim()) }

        return sharedPreferences.edit()
            .putString(KEY_API_LIST, jsonArray.toString())
            .apply()
            .let { true }
    }

    /**
     * Recupera a lista de chaves salvas.
     */
    fun getKeys(): List<String> {
        val json = sharedPreferences.getString(KEY_API_LIST, null) ?: return emptyList()
        val jsonArray = JSONArray(json)
        val keys = mutableListOf<String>()
        for (i in 0 until jsonArray.length()) {
            keys.add(jsonArray.getString(i))
        }
        return keys
    }

    /**
     * Verifica se o usuário já configurou as chaves mínimas.
     */
    fun hasValidConfiguration(): Boolean {
        return getKeys().size >= MIN_KEYS
    }

    /**
     * Retorna a quantidade atual de chaves.
     */
    fun getKeyCount(): Int {
        return getKeys().size
    }

    fun getMinKeysRequired(): Int = MIN_KEYS
    fun getMaxKeysAllowed(): Int = MAX_KEYS
}
