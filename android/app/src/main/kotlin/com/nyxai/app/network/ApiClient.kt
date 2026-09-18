package com.nyxai.app.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Configuração do Cliente HTTP para APIs de IA Gratuitas/Open-Source.
 * 
 * ESTRATÉGIA DE CUSTO ZERO:
 * Utilizamos agregadores que oferecem tiers gratuitos robustos para modelos open-source.
 * 
 * Opções Suportadas:
 * 1. Groq Cloud (Recomendado): Extremamente rápido, free tier generoso para Llama/Qwen/Mixtral.
 *    - Site: https://console.groq.com (Chave grátis disponível)
 * 2. OpenRouter: Agregador com muitos modelos free/teste.
 *    - Site: https://openrouter.ai
 * 
 * Modelos Disponíveis Gratuitamente:
 * - Qwen 2.5 72B (Nosso modelo principal - equivalente a GPT-4)
 * - Llama 3.1 70B
 * - Mixtral 8x7B
 */
object ApiClient {
    
    // BASE URLs dos provedores gratuitos
    const val GROQ_BASE_URL = "https://api.groq.com/openai/v1/"
    const val OPENROUTER_BASE_URL = "https://openrouter.ai/api/v1/"
    
    // Modelos Qwen disponíveis gratuitamente
    const val QWEN_GROQ_MODEL = "qwen-2.5-72b-versatile"  // Groq
    const val QWEN_OPENROUTER_MODEL = "qwen/qwen-2.5-72b-instruct"  // OpenRouter
    
    // Timeout ajustado para respostas de IA
    private const val TIMEOUT_SECONDS = 30L

    /**
     * Cria um OkHttpClient com logging e autenticação dinâmica
     */
    fun provideOkHttpClient(apiKey: String): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor { chain ->
                val original = chain.request()
                
                // Adiciona headers de autenticação dinamicamente dependendo da URL
                val requestBuilder = original.newBuilder()
                    .header("Content-Type", "application/json")
                
                when {
                    original.url.toString().contains("groq.com") -> {
                        requestBuilder.header("Authorization", "Bearer $apiKey")
                    }
                    original.url.toString().contains("openrouter.ai") -> {
                        requestBuilder
                            .header("Authorization", "Bearer $apiKey")
                            .header("HTTP-Referer", "https://github.com/contatomateus81-design/NyxAI_Official")
                            .header("X-Title", "Nyx AI")
                    }
                }

                chain.proceed(requestBuilder.build())
            }
            .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .build()
    }

    /**
     * Cliente Retrofit para Groq (Recomendado - Mais rápido e estável)
     * Use este para produção com chave grátis da Groq
     */
    fun getGroqClient(apiKey: String): Retrofit {
        val client = provideOkHttpClient(apiKey)
        
        return Retrofit.Builder()
            .baseUrl(GROQ_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    /**
     * Cliente Retrofit para OpenRouter (Alternativa com mais modelos)
     */
    fun getOpenRouterClient(apiKey: String): Retrofit {
        val client = provideOkHttpClient(apiKey)
        
        return Retrofit.Builder()
            .baseUrl(OPENROUTER_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    /**
     * Método utilitário para obter o endpoint correto baseado na escolha do usuário
     */
    fun getClient(provider: String, apiKey: String): Retrofit {
        return when (provider.lowercase()) {
            "groq" -> getGroqClient(apiKey)
            "openrouter" -> getOpenRouterClient(apiKey)
            else -> getGroqClient(apiKey) // Default para Groq
        }
    }
}
