package com.capston2024.capstonapp.presentation.handon

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.capston2024.capstonapp.data.responseDto.ResponseMockDto
import com.capston2024.capstonapp.databinding.ItemFoodBinding
import com.capston2024.capstonapp.extension.Diff

class HandonAdapter : ListAdapter<ResponseMockDto.MockModel, HandonViewHolder>(
    userDiffCallback
) {
    private lateinit var listener: HandonViewHolder.OnItemClickListener

    // 클릭 리스너 설정 메서드
    fun setOnItemClickListener(listener: HandonViewHolder.OnItemClickListener) {
        this.listener = listener
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HandonViewHolder {
        val binding= ItemFoodBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return HandonViewHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: HandonViewHolder, position: Int) {
        val foodItem = getItem(position) // 현재 위치의 아이템 가져오기
        holder.onBind(foodItem) // 뷰홀더에 데이터 바인딩
    }

    override fun getItemCount(): Int = currentList.size


    companion object {
        private val userDiffCallback =
            Diff<ResponseMockDto.MockModel>(
                onItemsTheSame = { old, new -> old.id == new.id },
                onContentsTheSame = { old, new -> old == new }
            )
    }
}