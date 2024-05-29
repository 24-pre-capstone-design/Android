package com.capston2024.capstonapp.presentation.aimode.data

import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.aallam.openai.api.chat.ChatRole
import com.capston2024.capstonapp.R
import com.capston2024.capstonapp.databinding.ItemAichatBinding

class ChatAdapter(private val messages: MutableList<CustomChatMessage>) :
    RecyclerView.Adapter<ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_AI_INPUT = 1
        private const val VIEW_TYPE_AI_CHAT = 2
    }

    override fun getItemViewType(position: Int): Int {
        return when(messages[position].role){
            ChatRole.User -> VIEW_TYPE_AI_INPUT
            ChatRole.Assistant -> VIEW_TYPE_AI_CHAT
            else ->throw IllegalArgumentException("Unknown role: ${messages[position].role}")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
       /* val binding = ItemMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MessageViewHolder(binding)*/
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_AI_CHAT -> AIChatViewHolder(ItemAichatBinding.inflate(inflater, parent, false))
            VIEW_TYPE_AI_INPUT -> AIInputViewHolder(ItemAichatBinding.inflate(inflater, parent, false))
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       /* val message = messages[position]
        holder.binding.messageTextView.text = message.userContent*/
        when (holder) {
            is AIInputViewHolder -> holder.bind(messages[position])
            is AIChatViewHolder -> holder.bind(messages[position])
        }
    }

    override fun getItemCount() = messages.size

    //class MessageViewHolder(val binding: ItemMessageBinding) : RecyclerView.ViewHolder(binding.root)
    inner class AIChatViewHolder(private val binding: ItemAichatBinding) :
        ViewHolder(binding.root) {
        fun bind(message: CustomChatMessage) {
            binding.tvAi.text = message.userContent
        }
    }
    inner class AIInputViewHolder(private val binding:ItemAichatBinding):
        ViewHolder(binding.root){
        fun bind(message: CustomChatMessage){
            with(binding){
                binding.tvAi.text=message.userContent
                tvAi.gravity=Gravity.END        //우측정렬
                binding.root.setBackgroundColor(
                    ContextCompat.getColor(binding.root.context, R.color.ai_background_input_pink)
                )
            }


        }
    }
}
