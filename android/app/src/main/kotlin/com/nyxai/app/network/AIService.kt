package com.nyxai.app.network

import retrofit2.http.*

/**
 * API Service para integração com modelos de IA Gratuitos (Qwen via Groq/OpenRouter)
 * Foco em custo zero e alta performance
 */
interface AIService {
    
    /**
     * Endpoint para Chat Completion (Compatível com OpenAI API)
     * Usado por Groq e OpenRouter
     */
    @POST("chat/completions")
    @Headers("Content-Type: application/json")
    suspend fun getChatCompletion(
        @Header("Authorization") apiKey: String,
        @Body request: ChatRequest
    ): ChatResponse
    
    /**
     * Endpoint específico para Groq (mais rápido)
     */
    @POST("chat/completions")
    suspend fun getGroqResponse(
        @Body request: ChatRequest
    ): ChatResponse
    
    /**
     * Endpoint específico para OpenRouter
     */
    @POST("chat/completions")
    @Headers(
        "HTTP-Referer: https://github.com/contatomateus81-design/NyxAI_Official",
        "X-Title: Nyx AI"
    )
    suspend fun getOpenRouterResponse(
        @Body request: ChatRequest
    ): ChatResponse
}

// Request/Response models compatíveis com OpenAI API (Groq e OpenRouter usam este formato)
data class ChatRequest(
    val model: String,
    val messages: List<Message>,
    val max_tokens: Int = 2048,
    val temperature: Double = 0.7,
    val top_p: Double = 1.0,
    val stream: Boolean = false,
    val stop: List<String>? = null,
    val user: String? = null // ID do usuário para tracking opcional
)

data class Message(
    val role: String, // "system", "user", "assistant"
    val content: String
)

data class ChatResponse(
    val id: String?,
    val `object`: String?,
    val created: Long?,
    val model: String?,
    val choices: List<Choice>?,
    val usage: Usage?,
    val error: ApiError? = null // Para tratamento de erros da API
)

data class Choice(
    val index: Int?,
    val message: Message,
    val finish_reason: String?,
    val delta: Delta? = null // Para streaming
)

data class Delta(
    val role: String?,
    val content: String?
)

data class Usage(
    val prompt_tokens: Int,
    val completion_tokens: Int,
    val total_tokens: Int
)

data class ApiError(
    val message: String,
    val type: String,
    val code: String?
)

/**
 * Companion object com prompts pré-configurados para a personalidade da Nyx
 */
object NyxPrompts {
    
    /**
     * System Prompt Principal - Personalidade da Nyx AI
     * Define o comportamento, tom e capacidades da assistente
     */
    val SYSTEM_PROMPT = """
        Você é Nyx AI, uma assistente pessoal digital inteligente, amigável e proativa.
        
        SUAS CARACTERÍSTICAS:
        - Nome: Nyx (Deusa grega da noite, simbolizando sabedoria e mistério)
        - Personalidade: Amigável, prestativa, organizada, empática e ligeiramente divertida
        - Tom de voz: Natural, conversacional, como uma amiga próxima
        - Especialidades: Organização pessoal, produtividade, companhia virtual, suporte emocional leve
        
        SUAS CAPACIDADES:
        1. Conversação natural em português brasileiro
        2. Ajuda com organização de tarefas e agenda
        3. Suporte para microempreendedores (gestão financeira básica)
        4. Automação de tarefas do dispositivo (quando integrado)
        5. Busca inteligente de informações
        
        REGRAS DE COMPORTAMENTO:
        - Seja concisa mas completa nas respostas
        - Use emojis moderadamente para tornar a conversa mais calorosa 🌙✨
        - Se não souber algo, admita honestamente e sugira alternativas
        - Mantenha privacidade: nunca peça dados sensíveis
        - Para MEIs: ofereça ajuda com cálculos de lucro, despesas e margens
        - Em momentos apropriados, mostre preocupação genuína com o bem-estar do usuário
        
        FORMATO DAS RESPOSTAS:
        - Use formatação markdown quando útil (listas, negrito)
        - Para código, use blocos formatados
        - Para matemática/finanças, mostre o cálculo passo a passo
        
        EXEMPLO DE SAUDAÇÃO:
        "Olá! Sou a Nyx 🌙 Sua assistente pessoal. Como posso te ajudar hoje?"
    """.trimIndent()
    
    /**
     * System Prompt especializado para modo Financeiro/MEI
     */
    val FINANCE_SYSTEM_PROMPT = """
        Você é Nyx AI, especializada em gestão financeira para Microempreendedores Individuais (MEI).
        
        ESPECIALIDADES FINANCEIRAS:
        - Cálculo de lucro líquido e bruto
        - Controle de despesas fixas e variáveis
        - Margem de contribuição
        - Preço de venda ideal
        - Fluxo de caixa básico
        - Impostos do MEI (DAS)
        
        AO RESPONDER QUESTÕES FINANCEIRAS:
        1. Sempre mostre os cálculos passo a passo
        2. Use exemplos práticos quando possível
        3. Alertas sobre gastos excessivos
        4. Sugestões de otimização de custos
        5. Lembrete sobre obrigações do MEI
        
        TOM: Profissional mas acessível, como um contador amigo
    """.trimIndent()
    
    /**
     * System Prompt para modo Criativo
     */
    val CREATIVE_SYSTEM_PROMPT = """
        Você é Nyx AI em modo criativo.
        
        CAPACIDADES CRIATIVAS:
        - Brainstorming de ideias
        - Escrita criativa (histórias, poemas, roteiros)
        - Planejamento de projetos artísticos
        - Sugestões de design e estética
        
        TOM: Inspirador, imaginativo, encorajador
        USE: Metáforas, linguagem vívida, entusiasmo criativo
    """.trimIndent()
}

/**
 * Extensões utilitárias para construção de requests
 */
fun chatRequest(
    model: String,
    systemPrompt: String = NyxPrompts.SYSTEM_PROMPT,
    userMessage: String,
    history: List<Message> = emptyList(),
    temperature: Double = 0.7,
    maxTokens: Int = 2048
): ChatRequest {
    val messages = mutableListOf(
        Message(role = "system", content = systemPrompt)
    )
    
    // Adiciona histórico se existir
    messages.addAll(history)
    
    // Adiciona mensagem atual do usuário
    messages.add(Message(role = "user", content = userMessage))
    
    return ChatRequest(
        model = model,
        messages = messages,
        temperature = temperature,
        max_tokens = maxTokens
    )
}

/**
 * Extrai a resposta de texto do ChatResponse de forma segura
 */
fun ChatResponse.extractText(): String {
    return this.error?.message 
        ?: this.choices?.firstOrNull()?.message?.content 
        ?: "Desculpe, não consegui processar sua solicitação. Tente novamente."
}
