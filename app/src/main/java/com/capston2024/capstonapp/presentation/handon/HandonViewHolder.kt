package com.capston2024.capstonapp.presentation.handon

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.capston2024.capstonapp.R
import com.capston2024.capstonapp.data.responseDto.ResponseMockDto
import com.capston2024.capstonapp.databinding.ItemFoodBinding

class HandonViewHolder(
    private val binding: ItemFoodBinding,
    private val listener: HandonViewHolder.OnItemClickListener,
) : RecyclerView.ViewHolder(binding.root) {
    interface OnItemClickListener {
        fun onFoodItemClick(foodItem: ResponseMockDto.MockModel)
    }

    fun onBind(foodData: ResponseMockDto.MockModel) {

        with(binding) {
            ivImage.load(foodData.avatar)
            tvFoodName.text = foodData.lastName
            tvFoodPrice.text = itemView.context.getString(R.string.bag_price,foodData.id)
        }

        // 아이템 클릭 리스너 설정
        itemView.setOnClickListener {
            listener.onFoodItemClick(foodData)
        }

    }
}