package com.nyxai.app.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nyxai.app.data.model.Message
import com.nyxai.app.data.model.SenderType
import com.nyxai.app.databinding.ItemChatMessageBinding
import java.text.SimpleDateFormat
import java.util.*

/**
 * Adapter para a RecyclerView de mensagens do chat
 */
class ChatAdapter(
    private val messages: List<Message>
) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {
    
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    
    inner class ChatViewHolder(private val binding: ItemChatMessageBinding) 
        : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(message: Message) {
            binding.textViewMessage.text = message.content
            binding.textViewTime.text = timeFormat.format(Date(message.timestamp))
            
            // Configurar layout baseado no remetente
            val isUser = message.sender == SenderType.USER
            
            // Alinhamento e estilo da bolha
            val layoutParams = binding.cardMessage.layoutParams as ViewGroup.MarginLayoutParams
            if (isUser) {
                layoutParams.marginEnd = 16
                layoutParams.marginStart = 80
                binding.cardMessage.setCardBackgroundColor(
                    binding.root.context.getColor(com.nyxai.app.R.color.chat_bubble_user)
                )
                binding.textViewMessage.setTextColor(
                    binding.root.context.getColor(com.nyxai.app.R.color.text_primary)
                )
            } else {
                layoutParams.marginStart = 16
                layoutParams.marginEnd = 80
                binding.cardMessage.setCardBackgroundColor(
                    binding.root.context.getColor(com.nyxai.app.R.color.chat_bubble_ai)
                )
                binding.textViewMessage.setTextColor(
                    binding.root.context.getColor(com.nyxai.app.R.color.text_primary)
                )
            }
            binding.cardMessage.layoutParams = layoutParams
        }
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val binding = ItemChatMessageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ChatViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.bind(messages[position])
    }
    
    override fun getItemCount(): Int = messages.size
}
