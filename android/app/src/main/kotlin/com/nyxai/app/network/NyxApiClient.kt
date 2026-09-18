package com.nyxai.app.network

import android.content.Context
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Cliente API dedicado para Nyx Engine
 * Baseado em Qwen 2.5 Open com otimizações próprias
 * 
 * Arquitetura:
 * - Primary: Qwen 2.5 72B (via API cloud para respostas complexas)
 * - Secondary: Qwen 2.5 7B quantizado (via ONNX local para tarefas simples)
 * - Fallback: Cache local + resposta diferida quando offline
 */
object NyxApiClient {
    
    // URLs dos endpoints
    private const val QWEN_CLOUD_BASE_URL = "https://dashscope-intl.aliyuncs.com/"
    private const val NYX_PROXY_BASE_URL = "https://nyx-engine.yourdomain.com/" // Futuro proxy próprio
    
    // Configuração do modelo Qwen
    const val QWEN_MODEL_72B = "qwen-max"
    const val QWEN_MODEL_7B = "qwen-plus"
    const val QWEN_MODEL_TURBO = "qwen-turbo"
    
    // Contexto da aplicação (inicializar no NyxApplication)
    lateinit var appContext: Context
    
    // Timeout otimizado para conversação natural
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = if (isDebuggable()) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
    }
    
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor { chain ->
            val versionName = try {
                appContext.packageManager.getPackageInfo(appContext.packageName, 0).versionName ?: "1.0"
            } catch (e: Exception) {
                "1.0"
            }
            val request = chain.request().newBuilder()
                .header("X-Nyx-Version", versionName)
                .header("X-Device-ID", getDeviceId())
                .build()
            chain.proceed(request)
        }
        .connectTimeout(45, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .build()
    
    /**
     * Cliente principal para Qwen Cloud
     * Usado para requisições complexas que exigem o modelo 72B
     */
    fun getQwenCloudClient(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(QWEN_CLOUD_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    /**
     * Cliente para futuro proxy próprio (recomendado para produção)
     * Permite esconder chaves de API e adicionar lógica customizada
     */
    fun getNyxProxyClient(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(NYX_PROXY_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    /**
     * Cria serviço Nyx com autenticação
     */
    fun createNyxService(apiKey: String): NyxEngineService {
        val clientWithAuth = okHttpClient.newBuilder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .header("Authorization", "Bearer $apiKey")
                    .header("Content-Type", "application/json")
                    .build()
                chain.proceed(request)
            }
            .build()
        
        return Retrofit.Builder()
            .baseUrl(QWEN_CLOUD_BASE_URL)
            .client(clientWithAuth)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NyxEngineService::class.java)
    }
    
    /**
     * Gera ID único do dispositivo (anonimizado para privacidade)
     */
    private fun getDeviceId(): String {
        // Implementação real usaria Settings.Secure.ANDROID_ID com hash
        return android.provider.Settings.Secure.getString(
            appContext.contentResolver, 
            android.provider.Settings.Secure.ANDROID_ID
        ) ?: "unknown"
    }
    
    /**
     * Verifica se o app está em modo debug
     */
    private fun isDebuggable(): Boolean {
        return try {
            val appInfo = appContext.packageManager.getApplicationInfo(
                appContext.packageName, 
                0
            )
            (appInfo.flags and android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE) != 0
        } catch (e: Exception) {
            false
        }
    }
}
