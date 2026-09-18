package com.nyxai.app.network

import retrofit2.http.*
import okhttp3.RequestBody
import okhttp3.ResponseBody

/**
 * API Service para integração com múltiplos modelos de IA
 * Suporte para diferentes provedores de LLM
 */
interface AIService {
    
    // OpenAI GPT
    @POST("v1/chat/completions")
    @Headers("Content-Type: application/json")
    suspend fun getGPTResponse(
        @Header("Authorization") apiKey: String,
        @Body request: ChatRequest
    ): ChatResponse
    
    // Google Gemini
    @POST("v1beta/models/{model}:generateContent")
    @Headers("Content-Type: application/json")
    suspend fun getGeminiResponse(
        @Path("model") model: String,
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse
    
    // Anthropic Claude
    @POST("v1/messages")
    @Headers("Content-Type: application/json")
    suspend fun getClaudeResponse(
        @Header("x-api-key") apiKey: String,
        @Body request: ClaudeRequest
    ): ClaudeResponse
    
    // Grok (xAI)
    @POST("v1/chat/completions")
    @Headers("Content-Type: application/json")
    suspend fun getGrokResponse(
        @Header("Authorization") apiKey: String,
        @Body request: ChatRequest
    ): ChatResponse
}

// Request/Response models genéricos
data class ChatRequest(
    val model: String,
    val messages: List<Message>,
    val max_tokens: Int = 1024,
    val temperature: Double = 0.7
)

data class Message(
    val role: String,
    val content: String
)

data class ChatResponse(
    val id: String?,
    val choices: List<Choice>?,
    val usage: Usage?
)

data class Choice(
    val message: Message,
    val finish_reason: String?
)

data class Usage(
    val prompt_tokens: Int,
    val completion_tokens: Int,
    val total_tokens: Int
)

// Gemini specific
data class GeminiRequest(
    val contents: List<GeminiContent>,
    val generationConfig: GenerationConfig? = null
)

data class GeminiContent(
    val parts: List<Part>
)

data class Part(
    val text: String
)

data class GenerationConfig(
    val temperature: Double = 0.7,
    val maxOutputTokens: Int = 1024
)

data class GeminiResponse(
    val candidates: List<Candidate>?
)

data class Candidate(
    val content: GeminiContent,
    val finishReason: String?
)

// Claude specific
data class ClaudeRequest(
    val model: String,
    val messages: List<Message>,
    val max_tokens: Int = 1024
)

data class ClaudeResponse(
    val id: String?,
    val content: List<Content>?,
    val usage: Usage?
)

data class Content(
    val type: String,
    val text: String
)
