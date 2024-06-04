/*
package com.capston2024.capstonapp.presentation.aimode

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.capston2024.capstonapp.R
import com.capston2024.capstonapp.data.Message
import com.capston2024.capstonapp.data.MessageType
import com.capston2024.capstonapp.databinding.ItemAichatBinding
import com.capston2024.capstonapp.presentation.aimode.data.CustomChatMessage

class AIAdapter(private val messages: List<Message>) :
    RecyclerView.Adapter<ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_AI_INPUT = 1
        private const val VIEW_TYPE_AI_CHAT = 2
    }

inner class AIChatViewHolder(private val binding: ItemAichatBinding) :
       ViewHolder(binding.root) {
        fun bind(message: CustomChatMessage) {
            binding.tvAi.text = message.message
        }
    }

    inner class AIInputViewHolder(private val binding:ItemAichatBinding):
    ViewHolder(binding.root){
        fun bind(message: CustomChatMessage){
            binding.tvAi.text=message.message
            binding.tvAi.resources.getColor(R.color.ai_background_input_purple)
        }
    }


    override fun getItemViewType(position: Int): Int {
        return when (messages[position].type) {
            MessageType.AI_CHAT -> VIEW_TYPE_AI_CHAT
            MessageType.USER_INPUT -> VIEW_TYPE_AI_INPUT
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_AI_INPUT -> AIInputViewHolder(ItemAichatBinding.inflate(inflater, parent, false))
            VIEW_TYPE_AI_CHAT -> AIChatViewHolder(ItemAichatBinding.inflate(inflater, parent, false))
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (holder) {
            is AIInputViewHolder -> holder.bind(messages[position])
            is AIChatViewHolder -> holder.bind(messages[position])
        }
    }

    override fun getItemCount(): Int = messages.size
}
*/
