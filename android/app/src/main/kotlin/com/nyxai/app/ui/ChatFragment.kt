package com.nyxai.app.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.nyxai.app.data.model.Message
import com.nyxai.app.data.model.SenderType
import com.nyxai.app.databinding.FragmentChatBinding

/**
 * Fragment da tela de Chat
 */
class ChatFragment : Fragment() {
    
    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!
    private lateinit var chatAdapter: ChatAdapter
    private val messages = mutableListOf<Message>()
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        setupInputListeners()
        loadWelcomeMessage()
    }
    
    private fun setupRecyclerView() {
        chatAdapter = ChatAdapter(messages)
        binding.recyclerViewChat.apply {
            layoutManager = LinearLayoutManager(requireContext()).apply {
                stackFromEnd = true
            }
            adapter = chatAdapter
        }
    }
    
    private fun setupInputListeners() {
        binding.buttonSend.setOnClickListener {
            sendMessage()
        }
    }
    
    private fun sendMessage() {
        val text = binding.editTextMessage.text.toString().trim()
        if (text.isEmpty()) return
        
        val userMessage = Message(content = text, sender = SenderType.USER)
        messages.add(userMessage)
        chatAdapter.notifyItemInserted(messages.size - 1)
        binding.recyclerViewChat.scrollToPosition(messages.size - 1)
        binding.editTextMessage.text?.clear()
    }
    
    private fun loadWelcomeMessage() {
        val welcomeMessage = Message(
            content = "Olá! Eu sou Nyx, sua assistente pessoal. Como posso ajudar?",
            sender = SenderType.AI
        )
        messages.add(welcomeMessage)
        chatAdapter.notifyDataSetChanged()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
