package com.nyxai.app.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.nyxai.app.databinding.ActivityChatBinding
import com.nyxai.app.data.model.Message
import com.nyxai.app.data.model.SenderType

/**
 * Tela principal de Chat - Conversação com IA por texto e voz
 */
class ChatActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityChatBinding
    private lateinit var chatAdapter: ChatAdapter
    private val messages = mutableListOf<Message>()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupRecyclerView()
        setupInputListeners()
        loadMessages()
    }
    
    private fun setupRecyclerView() {
        chatAdapter = ChatAdapter(messages)
        binding.recyclerViewChat.apply {
            layoutManager = LinearLayoutManager(this@ChatActivity).apply {
                stackFromEnd = true
            }
            adapter = chatAdapter
        }
    }
    
    private fun setupInputListeners() {
        binding.buttonSend.setOnClickListener {
            sendMessage()
        }
        
        binding.buttonVoice.setOnClickListener {
            startVoiceInput()
        }
    }
    
    private fun sendMessage() {
        val text = binding.editTextMessage.text.toString().trim()
        if (text.isEmpty()) return
        
        // Adicionar mensagem do usuário
        val userMessage = Message(
            content = text,
            sender = SenderType.USER
        )
        messages.add(userMessage)
        chatAdapter.notifyItemInserted(messages.size - 1)
        binding.recyclerViewChat.scrollToPosition(messages.size - 1)
        
        binding.editTextMessage.text?.clear()
        
        // Enviar para IA e receber resposta
        sendToAI(text)
    }
    
    private fun sendToAI(message: String) {
        // TODO: Implementar chamada para API de IA
        // Simular resposta temporária
        simulateAIResponse(message)
    }
    
    private fun simulateAIResponse(userMessage: String) {
        // Simulação de delay de rede
        binding.progressBar.visibility = android.view.View.VISIBLE
        
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            val aiMessage = Message(
                content = "Esta é uma resposta simulada da Nyx AI. Em breve, integrarei com modelos reais como GPT, Gemini, Grok e Qwen!",
                sender = SenderType.AI
            )
            messages.add(aiMessage)
            chatAdapter.notifyItemInserted(messages.size - 1)
            binding.recyclerViewChat.scrollToPosition(messages.size - 1)
            binding.progressBar.visibility = android.view.View.GONE
        }, 1500)
    }
    
    private fun startVoiceInput() {
        // TODO: Implementar reconhecimento de voz
        Snackbar.make(binding.root, "Recurso de voz em desenvolvimento", Snackbar.LENGTH_SHORT).show()
    }
    
    private fun loadMessages() {
        // TODO: Carregar mensagens do banco de dados local (Room)
        // Mensagem de boas-vindas inicial
        val welcomeMessage = Message(
            content = getString(com.nyxai.app.R.string.chat_welcome),
            sender = SenderType.AI
        )
        messages.add(welcomeMessage)
        chatAdapter.notifyDataSetChanged()
    }
}
