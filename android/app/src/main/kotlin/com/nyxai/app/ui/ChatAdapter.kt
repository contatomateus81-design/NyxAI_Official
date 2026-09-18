package com.nyxai.app.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nyxai.app.data.model.Message
import com.nyxai.app.data.model.SenderType
import com.nyxai.app.databinding.ItemChatMessageBinding
import java.text.SimpleDateFormat
import java.util.Locale

class ChatAdapter : ListAdapter<Message, ChatAdapter.MessageViewHolder>(MessageDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val binding = ItemChatMessageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MessageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class MessageViewHolder(
        private val binding: ItemChatMessageBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(message: Message) {
            binding.textMessageContent.text = message.content
            binding.textMessageTime.text = formatTimestamp(message.timestamp)

            // Configurar layout baseado no remetente
            if (message.sender == SenderType.USER) {
                // Mensagem do usuário - alinhada à direita
                binding.messageContainer.setBackgroundResource(R.drawable.bg_message_user)
                binding.textMessageContent.setTextColor(
                    itemView.context.getColor(com.nyxai.app.R.color.text_primary)
                )
                binding.messageContainer.layoutParams.apply {
                    this as ViewGroup.MarginLayoutParams
                    marginStart = itemView.resources.getDimensionPixelSize(com.nyxai.app.R.dimen.message_margin_start)
                    marginEnd = 0
                }
            } else {
                // Mensagem da Nyx AI - alinhada à esquerda
                binding.messageContainer.setBackgroundResource(R.drawable.bg_message_ai)
                binding.textMessageContent.setTextColor(
                    itemView.context.getColor(com.nyxai.app.R.color.text_primary)
                )
                binding.messageContainer.layoutParams.apply {
                    this as ViewGroup.MarginLayoutParams
                    marginStart = 0
                    marginEnd = itemView.resources.getDimensionPixelSize(com.nyxai.app.R.dimen.message_margin_end)
                }
            }
        }

        private fun formatTimestamp(timestamp: Long): String {
            val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
            return sdf.format(java.util.Date(timestamp))
        }
    }

    class MessageDiffCallback : DiffUtil.ItemCallback<Message>() {
        override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem == newItem
        }
    }
}
