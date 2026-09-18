package com.nyxai.app.domain.repository

import com.nyxai.app.data.local.MessageDao
import com.nyxai.app.data.model.Message
import com.nyxai.app.data.model.MessageType
import com.nyxai.app.network.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

/**
 * Repositório principal da Nyx Engine
 * Gerencia comunicação com IA, cache local e fallback offline
 * 
 * Arquitetura:
 * 1. Tenta resposta via Qwen Cloud (modelo 72B para complexas, 7B para simples)
 * 2. Salva no banco local (Room) imediatamente
 * 3. Em caso de erro/falha, retorna cache ou mensagem de offline
 */
class NyxRepository(
    private val messageDao: MessageDao,
    private val apiKey: String
) {
    
    private val nyxService: NyxEngineService by lazy {
        NyxApiClient.createNyxService(apiKey)
    }
    
    // Histórico em memória para contexto da conversa
    private val conversationHistory = mutableListOf<NyxMessage>()
    
    /**
     * Envia mensagem e recebe resposta da Nyx
     * @param userMessage Texto do usuário
     * @param mode Modo de operação (DEFAULT, FINANCIAL, AUTOMATION)
     * @return Mensagem de resposta da Nyx
     */
    suspend fun sendMessage(
        userMessage: String,
        mode: NyxMode = NyxMode.DEFAULT
    ): Result<Message> = withContext(Dispatchers.IO) {
        try {
            // 1. Salvar mensagem do usuário no banco
            val userMsgEntity = Message(
                id = null,
                content = userMessage,
                sender = "user",
                type = MessageType.TEXT,
                timestamp = System.currentTimeMillis(),
                isRead = true
            )
            val savedUserMsg = messageDao.insertMessage(userMsgEntity)
            
            // 2. Preparar histórico com contexto
            val systemPrompt = when (mode) {
                NyxMode.FINANCIAL -> NyxSystemPrompts.FINANCIAL_MODE
                NyxMode.AUTOMATION -> NyxSystemPrompts.AUTOMATION_MODE
                NyxMode.DEFAULT -> NyxSystemPrompts.DEFAULT_PERSONALITY
            }
            
            val messages = buildMessagesList(systemPrompt, userMessage)
            
            // 3. Selecionar modelo baseado na complexidade
            val model = selectModel(userMessage)
            
            // 4. Chamar API
            val request = NyxChatRequest(
                model = model,
                messages = messages,
                max_tokens = 2048,
                temperature = 0.7,
                top_p = 0.9,
                stream = false,
                user = NyxApiClient::class.java.getDeclaredMethod("getDeviceId")
                    .let { it.isAccessible = true; it.invoke(NyxApiClient) } as? String
            )
            
            val response = nyxService.chatCompletion(request)
            
            // 5. Processar resposta
            val nyxResponseText = response.choices?.firstOrNull()?.message?.content
                ?: throw IOException("Resposta vazia da API")
            
            // Atualizar histórico
            conversationHistory.add(NyxMessage("user", userMessage))
            conversationHistory.add(NyxMessage("assistant", nyxResponseText))
            
            // Manter histórico limitado (últimas 20 mensagens para contexto)
            if (conversationHistory.size > 20) {
                conversationHistory.removeAt(0)
                conversationHistory.removeAt(0)
            }
            
            // 6. Salvar resposta da Nyx no banco
            val nyxMsgEntity = Message(
                id = null,
                content = nyxResponseText,
                sender = "nyx",
                type = MessageType.TEXT,
                timestamp = System.currentTimeMillis(),
                isRead = true
            )
            val savedNyxMsg = messageDao.insertMessage(nyxMsgEntity)
            
            Result.success(savedNyxMsg)
            
        } catch (e: IOException) {
            // Erro de rede - tentar cache
            handleOfflineMode(userMessage, mode)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Constrói lista de mensagens com system prompt + histórico
     */
    private fun buildMessagesList(systemPrompt: String, newUserMessage: String): List<NyxMessage> {
        val messages = mutableListOf<NyxMessage>()
        
        // System prompt sempre primeiro
        messages.add(NyxMessage("system", systemPrompt))
        
        // Adicionar histórico da conversa
        messages.addAll(conversationHistory)
        
        // Adicionar nova mensagem do usuário
        messages.add(NyxMessage("user", newUserMessage))
        
        return messages
    }
    
    /**
     * Seleciona modelo baseado na complexidade da mensagem
     * Estratégia para otimizar custo e velocidade
     */
    private fun selectModel(message: String): String {
        val wordCount = message.split(" ").size
        
        return when {
            // Mensagens curtas/simples -> Turbo
            wordCount < 5 && message.any { it in listOf('?', '!', '.') } -> 
                NyxApiClient.QWEN_MODEL_TURBO
            
            // Mensagens médias -> Plus (7B)
            wordCount < 20 -> 
                NyxApiClient.QWEN_MODEL_7B
            
            // Mensagens complexas/longas -> Max (72B)
            else -> 
                NyxApiClient.QWEN_MODEL_72B
        }
    }
    
    /**
     * Modo offline: retorna dados em cache ou mensagem amigável
     */
    private suspend fun handleOfflineMode(
        userMessage: String,
        mode: NyxMode
    ): Result<Message> = withContext(Dispatchers.IO) {
        // Tentar buscar últimas mensagens como contexto
        val lastMessages = messageDao.getLastMessages(5)
        
        val offlineResponse = when {
            // Se tiver cache, usar última resposta similar
            lastMessages.isNotEmpty() -> {
                "Estou offline no momento, mas já registrei sua mensagem: \"$userMessage\". " +
                "Assim que conectar, responderei com detalhes. 🌙"
            }
            else -> {
                "Sem conexão no momento. Verifique sua internet e tente novamente. " +
                "Seus dados estão salvos aqui no dispositivo. 🔒"
            }
        }
        
        val offlineMsg = Message(
            id = null,
            content = offlineResponse,
            sender = "nyx",
            type = MessageType.TEXT,
            timestamp = System.currentTimeMillis(),
            isRead = true
        )
        
        val savedMsg = messageDao.insertMessage(offlineMsg)
        Result.success(savedMsg)
    }
    
    /**
     * Limpa histórico da conversa atual
     */
    fun clearConversation() {
        conversationHistory.clear()
    }
    
    /**
     * Flow para observar mensagens salvas
     */
    fun getMessagesFlow(): Flow<List<Message>> {
        return messageDao.getAllMessages()
    }
}

/**
 * Modos de operação da Nyx
 */
enum class NyxMode {
    DEFAULT,      // Personalidade padrão
    FINANCIAL,    // Modo financeiro para MEI
    AUTOMATION    // Modo criação de automações
}
