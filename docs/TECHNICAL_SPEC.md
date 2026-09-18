# Nyx AI - Documento de Especificação Técnica

## 1. Visão Geral do Sistema

### 1.1 Arquitetura Geral
```
┌─────────────────────────────────────────────────────────────┐
│                    Camada de Apresentação                    │
│  (Activities, Fragments, Views, ViewModels)                 │
├─────────────────────────────────────────────────────────────┤
│                    Camada de Domínio                         │
│  (Use Cases, Business Logic, Rules)                         │
├─────────────────────────────────────────────────────────────┤
│                    Camada de Dados                           │
│  (Repositories, Local DB, Remote API, Preferences)          │
└─────────────────────────────────────────────────────────────┘
```

### 1.2 Princípios de Design
- **MVVM Pattern**: Separação clara entre UI e lógica de negócio
- **Repository Pattern**: Abstração de fontes de dados
- **Local-First**: Dados primários armazenados localmente
- **Offline-First**: Funcionalidade básica sem conexão

## 2. Stack Tecnológico Detalhado

### 2.1 Linguagem e Frameworks
| Componente | Tecnologia | Versão Mínima |
|------------|------------|---------------|
| Linguagem | Kotlin | 1.9+ |
| SDK Mínimo | Android API | 26 (Android 8.0) |
| SDK Alvo | Android API | 34 (Android 14) |
| Jetpack Compose | UI Moderna | Opcional (v1.5+) |
| View System | XML + Material 3 | Padrão |

### 2.2 Dependências Principais

#### Core Android
```gradle
implementation 'androidx.core:core-ktx:1.12.0'
implementation 'androidx.appcompat:appcompat:1.6.1'
implementation 'com.google.android.material:material:1.11.0'
implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
```

#### Arquitetura
```gradle
implementation 'androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0'
implementation 'androidx.lifecycle:lifecycle-livedata-ktx:2.7.0'
implementation 'androidx.activity:activity-ktx:1.8.2'
implementation 'androidx.fragment:fragment-ktx:1.6.2'
```

#### Banco de Dados Local
```gradle
implementation 'androidx.room:room-runtime:2.6.1'
kapt 'androidx.room:room-compiler:2.6.1'
implementation 'androidx.room:room-ktx:2.6.1'
```

#### Network & API
```gradle
implementation 'com.squareup.retrofit2:retrofit:2.9.0'
implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
implementation 'com.squareup.okhttp3:okhttp:4.12.0'
implementation 'com.squareup.okhttp3:logging-interceptor:4.12.0'
```

#### Coroutines
```gradle
implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3'
implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3'
```

#### Autenticação Google
```gradle
implementation 'com.google.android.gms:play-services-auth:20.7.0'
implementation 'com.google.firebase:firebase-auth-ktx:22.3.0'
```

#### Processamento de Imagem
```gradle
implementation 'io.coil-kt:coil:2.5.0'
implementation 'io.coil-kt:coil-gif:2.5.0'
```

#### Utilitários
```gradle
implementation 'com.google.code.gson:gson:2.10.1'
implementation 'org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2'
```

## 3. Estrutura de Pacotes

```
com.nyxai.app/
├── data/
│   ├── local/
│   │   ├── dao/
│   │   │   ├── MessageDao.kt
│   │   │   ├── AutomationDao.kt
│   │   │   └── UserDao.kt
│   │   ├── entity/
│   │   │   ├── MessageEntity.kt
│   │   │   ├── AutomationEntity.kt
│   │   │   └── UserEntity.kt
│   │   └── NyxDatabase.kt
│   ├── remote/
│   │   ├── api/
│   │   │   ├── OpenAIApi.kt
│   │   │   ├── GeminiApi.kt
│   │   │   ├── GrokApi.kt
│   │   │   └── QwenApi.kt
│   │   ├── model/
│   │   │   ├── ChatRequest.kt
│   │   │   ├── ChatResponse.kt
│   │   │   └── SearchQuery.kt
│   │   └── RetrofitClient.kt
│   ├── repository/
│   │   ├── MessageRepository.kt
│   │   ├── AutomationRepository.kt
│   │   ├── UserRepository.kt
│   │   └── AIRepository.kt
│   └── preferences/
│       └── AppPreferences.kt
├── domain/
│   ├── model/
│   │   ├── Message.kt
│   │   ├── Automation.kt
│   │   ├── User.kt
│   │   └── AIModel.kt
│   ├── usecase/
│   │   ├── SendMessageUseCase.kt
│   │   ├── GetChatHistoryUseCase.kt
│   │   ├── CreateAutomationUseCase.kt
│   │   └── SyncDataUseCase.kt
│   └── repository/
│       ├── MessageRepository.kt
│       ├── AutomationRepository.kt
│       └── AIRepository.kt
├── presentation/
│   ├── splash/
│   │   ├── SplashActivity.kt
│   │   └── SplashViewModel.kt
│   ├── onboarding/
│   │   ├── OnboardingActivity.kt
│   │   ├── OnboardingFragment.kt
│   │   └── OnboardingViewModel.kt
│   ├── chat/
│   │   ├── ChatActivity.kt
│   │   ├── ChatAdapter.kt
│   │   ├── ChatViewModel.kt
│   │   └── viewholder/
│   │       ├── UserMessageViewHolder.kt
│   │       └── AIMessageViewHolder.kt
│   ├── automation/
│   │   ├── AutomationActivity.kt
│   │   ├── AutomationAdapter.kt
│   │   └── AutomationViewModel.kt
│   ├── settings/
│   │   ├── SettingsActivity.kt
│   │   └── SettingsViewModel.kt
│   └── base/
│       ├── BaseActivity.kt
│       └── BaseViewModel.kt
├── di/
│   └── AppModule.kt
├── util/
│   ├── Constants.kt
│   ├── Extensions.kt
│   └── NetworkUtils.kt
└── NyxApp.kt
```

## 4. Modelos de Dados

### 4.1 Message Entity (Local)
```kotlin
@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val content: String,
    val sender: String, // "user" ou "ai"
    val timestamp: Long,
    val aiModel: String?,
    val isSynced: Boolean = false,
    val conversationId: String
)
```

### 4.2 Automation Entity (Local)
```kotlin
@Entity(tableName = "automations")
data class AutomationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val trigger: String,
    val conditions: String, // JSON
    val actions: String, // JSON
    val isEnabled: Boolean = true,
    val createdAt: Long,
    val lastTriggered: Long?
)
```

### 4.3 User Entity (Local)
```kotlin
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val email: String,
    val displayName: String?,
    val photoUrl: String?,
    val createdAt: Long,
    val lastSync: Long?,
    val preferences: String // JSON
)
```

## 5. APIs Externas

### 5.1 OpenAI API
```kotlin
interface OpenAIApi {
    @POST("v1/chat/completions")
    suspend fun chat(@Body request: ChatRequest): ChatResponse
    
    @POST("v1/audio/speech")
    suspend fun textToSpeech(@Body request: TTSRequest): ResponseBody
}
```

### 5.2 Google Gemini API
```kotlin
interface GeminiApi {
    @POST("v1beta/models/gemini-pro:generateContent")
    suspend fun generateContent(@Body request: GeminiRequest): GeminiResponse
}
```

### 5.3 Estrutura de Request/Response
```kotlin
data class ChatRequest(
    val model: String,
    val messages: List<Message>,
    val temperature: Double = 0.7,
    val maxTokens: Int = 2048
)

data class ChatResponse(
    val id: String,
    val choices: List<Choice>,
    val usage: Usage
)
```

## 6. Fluxos Principais

### 6.1 Fluxo de Onboarding
```
1. SplashActivity (2s)
   ↓
2. OnboardingActivity
   - Página 1: Apresentação Nyx AI
   - Página 2: Recursos Principais
   - Página 3: Privacidade Local-First
   - Página 4: Login Google (opcional)
   ↓
3. ChatActivity (Tela Principal)
```

### 6.2 Fluxo de Chat
```
1. Usuário digita mensagem
   ↓
2. ViewModel valida e salva no Room
   ↓
3. Repository envia para API selecionada
   ↓
4. Recebe resposta da IA
   ↓
5. Atualiza UI com nova mensagem
   ↓
6. Sync em background (se online)
```

### 6.3 Fluxo de Automação
```
1. Usuário cria automação
   ↓
2. Define gatilho (tempo, localização, evento)
   ↓
3. Configura condições (opcionais)
   ↓
4. Define ações a executar
   ↓
5. Salva no Room
   ↓
6. WorkManager monitora gatilhos
```

## 7. Segurança e Privacidade

### 7.1 Armazenamento Seguro
- **SharedPreferences Encrypted**: Dados sensíveis
- **Room SQLCipher**: Banco de dados criptografado
- **Android Keystore**: Chaves de API

### 7.2 Permissões Requeridas
```xml
<!-- Necessárias -->
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />

<!-- Opcionais (solicitadas quando necessário) -->
<uses-permission android:name="android.permission.RECORD_AUDIO" />
<uses-permission android:name="android.permission.READ_CALENDAR" />
<uses-permission android:name="android.permission.WRITE_CALENDAR" />
<uses-permission android:name="android.permission.READ_CONTACTS" />
<uses-permission android:name="android.permission.SCHEDULE_EXACT_ALARM" />
```

### 7.3 Proteção de Dados
- Nenhum dado enviado para analytics
- Logs desabilitados em produção
- API keys ofuscadas no BuildConfig
- Comunicação HTTPS obrigatória

## 8. Estratégia de Offline

### 8.1 Cache Local
- Todas as mensagens salvas no Room
- Histórico completo disponível offline
- Fila de sincronização para envio pendente

### 8.2 Sincronização
```kotlin
// WorkManager para sync em background
class SyncWorker : CoroutineWorker() {
    override suspend fun doWork(): Result {
        // 1. Buscar mensagens não sincronizadas
        // 2. Enviar para servidor (se autenticado)
        // 3. Atualizar status de sync
        // 4. Baixar atualizações remotas
        return Result.success()
    }
}
```

## 9. Testes

### 9.1 Testes Unitários
```gradle
testImplementation 'junit:junit:4.13.2'
testImplementation 'org.mockito:mockito-core:5.8.0'
testImplementation 'org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3'
```

### 9.2 Testes de Instrumentação
```gradle
androidTestImplementation 'androidx.test.ext:junit:1.1.5'
androidTestImplementation 'androidx.test.espresso:espresso-core:3.5.1'
androidTestImplementation 'androidx.arch.core:core-testing:2.2.0'
```

## 10. CI/CD (Futuro)

### 10.1 GitHub Actions
```yaml
name: Android CI
on: [push, pull_request]
jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - name: Set up JDK 17
        uses: actions/setup-java@v3
        with:
          java-version: '17'
          distribution: 'temurin'
      - name: Build with Gradle
        run: ./gradlew build
      - name: Run Tests
        run: ./gradlew test
```

## 11. Métricas de Qualidade

### 11.1 KPIs Técnicos
- **Build Time**: < 3 minutos
- **App Size**: < 50 MB (APK)
- **Cold Start**: < 2 segundos
- **Memory Usage**: < 100 MB (média)
- **Crash Rate**: < 1%

### 11.2 KPIs de UX
- **Time to First Message**: < 5 segundos
- **Message Response Time**: < 3 segundos (API)
- **Offline Availability**: 100% do histórico

---

*Documento Técnico Nyx AI v1.0 - Projeto Pessoal*
