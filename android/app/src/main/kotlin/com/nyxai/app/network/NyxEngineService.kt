package com.nyxai.app.network

import retrofit2.http.*

/**
 * Serviço principal da Nyx Engine
 * Interface para comunicação com Qwen 2.5 otimizado
 */
interface NyxEngineService {
    
    /**
     * Endpoint principal de chat com contexto estendido
     * Suporta até 128K tokens de contexto (Qwen-Max)
     */
    @POST("compatible-mode/v1/chat/completions")
    @Headers("Content-Type: application/json")
    suspend fun chatCompletion(
        @Body request: NyxChatRequest
    ): NyxChatResponse
    
    /**
     * Endpoint para respostas rápidas (modo turbo)
     * Ideal para saudações e comandos simples
     */
    @POST("compatible-mode/v1/chat/completions")
    @Headers("Content-Type: application/json")
    suspend fun chatTurbo(
        @Body request: NyxChatRequest
    ): NyxChatResponse
    
    /**
     * Endpoint para análise de imagens (visão computacional)
     * Usado para busca inteligente no Google Fotos
     */
    @POST("compatible-mode/v1/chat/completions")
    @Headers("Content-Type: application/json")
    suspend fun chatWithVision(
        @Body request: NyxVisionRequest
    ): NyxChatResponse
}

// ==================== Request Models ====================

/**
 * Estrutura de requisição de chat otimizada para Nyx
 */
data class NyxChatRequest(
    val model: String,
    val messages: List<NyxMessage>,
    val max_tokens: Int = 2048,
    val temperature: Double = 0.7,
    val top_p: Double = 0.9,
    val stream: Boolean = false,
    val presence_penalty: Double = 0.0,
    val frequency_penalty: Double = 0.0,
    val user: String? = null // ID anonimizado do usuário
)

/**
 * Mensagem no formato Qwen-compatible
 */
data class NyxMessage(
    val role: String, // "system", "user", "assistant"
    val content: String,
    val name: String? = null // Opcional para identificar funções
)

/**
 * Requisição com suporte a visão computacional
 */
data class NyxVisionRequest(
    val model: String,
    val messages: List<NyxVisionMessage>,
    val max_tokens: Int = 1024
)

data class NyxVisionMessage(
    val role: String,
    val content: List<VisionContentPart>
)

sealed class VisionContentPart {
    data class TextPart(val text: String) : VisionContentPart()
    data class ImagePart(val image_url: ImageUrl) : VisionContentPart()
}

data class ImageUrl(
    val url: String,
    val detail: String = "auto" // "low", "high", "auto"
)

// ==================== Response Models ====================

/**
 * Resposta padrão do chat Nyx
 */
data class NyxChatResponse(
    val id: String?,
    val choices: List<NyxChoice>?,
    val usage: NyxUsage?,
    val created: Long?,
    val model: String?,
    val system_fingerprint: String?
)

data class NyxChoice(
    val index: Int,
    val message: NyxMessage,
    val finish_reason: String?,
    val logprobs: Any? = null
)

data class NyxUsage(
    val prompt_tokens: Int,
    val completion_tokens: Int,
    val total_tokens: Int,
    val prompt_cache_hit_tokens: Int = 0,
    val prompt_cache_miss_tokens: Int = 0
)

// ==================== System Prompts ====================

/**
 * Prompt de sistema que define a personalidade da Nyx
 * Este é o "cérebro" que diferencia Nyx de outros assistentes
 */
object NyxSystemPrompts {
    
    const val DEFAULT_PERSONALITY = """
        Você é Nyx, uma assistente pessoal digital inteligente, empática e proativa.
        
        SUA PERSONALIDADE:
        - Amigável e acolhedora, como uma amiga próxima
        - Organizada e eficiente, mas nunca robótica
        - Proativa: antecipe necessidades do usuário
        - Discreta: nunca julgue, sempre apoie
        - Humor leve quando apropriado, mas respeitosa
        
        SUAS CAPACIDADES:
        - Gerenciar agenda e compromissos
        - Controlar automações do dispositivo
        - Buscar informações precisas e relevantes
        - Manter conversas naturais com memória de contexto
        - Auxiliar microempreendedores com gestão financeira básica
        
        REGRAS IMPORTANTES:
        - Sempre priorize a privacidade do usuário
        - Se não souber algo, admita honestamente
        - Mantenha respostas concisas, mas completas
        - Use português brasileiro natural (gírias leves ok)
        - Nunca invente informações (alucinações)
        
        CONTEXTO ATUAL:
        - Usuário pode ser microempreendedor ou profissional comum
        - App funciona em modo escuro, mencione isso se relevante
        - Dados são locais-first, sincronização apenas opcional
    """.trimIndent()
    
    const val FINANCIAL_MODE = """
        Você está no modo de assistência financeira para microempreendedores.
        
        FOCO:
        - Calcular margens de lucro
        - Organizar despesas e receitas
        - Sugerir precificação
        - Alertar sobre gastos excessivos
        - Explicar conceitos financeiros de forma simples
        
        LINGUAGEM:
        - Técnica mas acessível
        - Use exemplos práticos
        - Sempre valide cálculos
    """.trimIndent()
    
    const val AUTOMATION_MODE = """
        Você está no modo de criação de automações.
        
        FOCO:
        - Criar gatilhos baseados em localização, tempo ou eventos
        - Sugerir ações automáticas úteis
        - Explicar permissões necessárias
        - Validar lógica das automações
        
        EXEMPLOS:
        - "Quando chegar em casa, ligue Wi-Fi"
        - "Às 22h, ative modo não perturbe"
        - "Se bateria < 20%, economize energia"
    """.trimIndent()
}
