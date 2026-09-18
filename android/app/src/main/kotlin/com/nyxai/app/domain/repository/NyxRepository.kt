package com.nyxai.app.domain.repository

import com.nyxai.app.data.local.MessageDao
import com.nyxai.app.data.local.entity.MessageEntity
import com.nyxai.app.data.model.Message
import com.nyxai.app.data.model.SenderType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * Repositório principal da Nyx Engine
 * Gerencia comunicação com IA (Groq + Qwen), cache local e fallback offline
 * 
 * SEGURANÇA: Chaves API carregadas via variáveis de ambiente ou BuildConfig
 */
class NyxRepository(
    private val messageDao: MessageDao
) {

    // Lista de chaves Groq para rotatividade automática
    // Configure via BuildConfig.GROQ_API_KEYS ou variáveis de ambiente
    private val groqApiKeys = listOfNotNull(
        System.getenv("GROQ_API_KEY_1"),
        System.getenv("GROQ_API_KEY_2"),
        System.getenv("GROQ_API_KEY_3"),
        System.getenv("GROQ_API_KEY_4"),
        System.getenv("GROQ_API_KEY_5")
    ).filter { it.isNotBlank() }

    private var currentKeyIndex = 0

    // Histórico em memória para contexto da conversa (últimas 20 mensagens)
    private val conversationHistory = mutableListOf<Pair<String, String>>()

    /**
     * Envia mensagem e recebe resposta da Nyx via Groq API
     */
    suspend fun sendMessage(userMessage: String): Result<MessageEntity> = withContext(Dispatchers.IO) {
        try {
            // 1. Salvar mensagem do usuário no banco
            val userMsgEntity = MessageEntity(
                id = 0,
                text = userMessage,
                sender = "user",
                timestamp = System.currentTimeMillis(),
                isLocal = true
            )
            messageDao.insertMessage(userMsgEntity)

            // 2. Chamar API Groq com Qwen
            val response = callGroqAPI(userMessage)

            // 3. Atualizar histórico
            conversationHistory.add(Pair(userMessage, response))
            if (conversationHistory.size > 20) {
                conversationHistory.removeAt(0)
            }

            // 4. Salvar resposta da Nyx no banco
            val nyxMsgEntity = MessageEntity(
                id = 0,
                text = response,
                sender = "nyx",
                timestamp = System.currentTimeMillis(),
                isLocal = true
            )
            val savedMsg = messageDao.insertMessage(nyxMsgEntity)

            Result.success(savedMsg)

        } catch (e: IOException) {
            handleOfflineMode(userMessage)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Chama API Groq com sistema de rotatividade de chaves
     * Modelo: qwen-2.5-32b (gratuito e de alta qualidade)
     */
    private suspend fun callGroqAPI(message: String): String {
        val client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build()

        var lastException: Exception? = null

        // Tenta cada chave até funcionar
        for (attempt in 0 until groqApiKeys.size) {
            try {
                val apiKey = groqApiKeys[(currentKeyIndex + attempt) % groqApiKeys.size]

                // Construir histórico de mensagens
                val messagesJson = buildString {
                    append("[")
                    append("{\"role\": \"system\", \"content\": \"Você é Nyx AI, uma assistente pessoal amigável, inteligente e prestativa. Responda de forma natural, útil e em português.\"}")
                    
                    conversationHistory.forEach { (userMsg, assistantMsg) ->
                        append(",{\"role\": \"user\", \"content\": \"${escapeJson(userMsg)}\"}")
                        append(",{\"role\": \"assistant\", \"content\": \"${escapeJson(assistantMsg)}\"}")
                    }
                    
                    append(",{\"role\": \"user\", \"content\": \"${escapeJson(message)}\"}")
                    append("]")
                }

                val jsonBody = """
                    {
                        "model": "qwen-2.5-32b",
                        "messages": $messagesJson,
                        "max_tokens": 1024,
                        "temperature": 0.7,
                        "top_p": 0.9
                    }
                """.trimIndent()

                val request = Request.Builder()
                    .url("https://api.groq.com/openai/v1/chat/completions")
                    .addHeader("Authorization", "Bearer $apiKey")
                    .addHeader("Content-Type", "application/json")
                    .post(jsonBody.toRequestBody("application/json".toMediaType()))
                    .build()

                val response = client.newCall(request).execute()

                if (response.isSuccessful) {
                    val responseBody = response.body?.string() ?: throw IOException("Resposta vazia")
                    val jsonResponse = JSONObject(responseBody)
                    val content = jsonResponse
                        .getJSONArray("choices")
                        .getJSONObject(0)
                        .getJSONObject("message")
                        .getString("content")

                    currentKeyIndex = (currentKeyIndex + attempt) % groqApiKeys.size
                    return content.trim()
                } else {
                    val errorBody = response.body?.string() ?: "Erro desconhecido"
                    // Se erro for de limite (429), tenta próxima chave
                    if (response.code == 429) {
                        continue
                    }
                    throw IOException("Erro API Groq: ${response.code} - $errorBody")
                }
            } catch (e: Exception) {
                lastException = e
                continue
            }
        }

        throw lastException ?: IOException("Todas as chaves Groq falharam")
    }

    /**
     * Escapa caracteres especiais para JSON
     */
    private fun escapeJson(text: String): String {
        return text
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t")
    }

    /**
     * Modo offline: retorna mensagem amigável
     */
    private suspend fun handleOfflineMode(userMessage: String): Result<MessageEntity> = withContext(Dispatchers.IO) {
        val offlineResponse = "Estou offline no momento, mas já registrei sua mensagem. Assim que conectar, responderei com detalhes. 🌙"

        val offlineMsg = MessageEntity(
            id = 0,
            text = offlineResponse,
            sender = "nyx",
            timestamp = System.currentTimeMillis(),
            isLocal = true
        )

        val savedMsg = messageDao.insertMessage(offlineMsg)
        Result.success(savedMsg)
    }

    /**
     * Retorna todas as mensagens do banco
     */
    suspend fun getAllMessages(): List<Message> {
        return messageDao.getAllMessages().map { entity ->
            Message(
                content = entity.text,
                sender = if (entity.sender == "user") SenderType.USER else SenderType.AI,
                timestamp = entity.timestamp
            )
        }
    }

    /**
     * Limpa histórico da conversa
     */
    fun clearConversation() {
        conversationHistory.clear()
    }
}
