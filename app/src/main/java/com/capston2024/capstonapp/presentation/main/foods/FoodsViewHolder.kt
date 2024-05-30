package com.capston2024.capstonapp.presentation.main.foods

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.capston2024.capstonapp.BuildConfig
import com.capston2024.capstonapp.R
import com.capston2024.capstonapp.data.responseDto.ResponseFoodDto
import com.capston2024.capstonapp.databinding.ItemFoodBinding
import java.text.NumberFormat
import java.util.Locale

class FoodsViewHolder (
    private val binding:ItemFoodBinding,
    private val listener: OnItemClickListener
):
RecyclerView.ViewHolder(binding.root){
    interface OnItemClickListener {
        fun onFoodItemClick(foodItem: ResponseFoodDto.Food)
    }
    fun onBind(foodData: ResponseFoodDto.Food) {

        with(binding){
            var url=BuildConfig.BASE_URL+foodData.pictureURL
            val formatter= NumberFormat.getNumberInstance(Locale.KOREA)
            ivImage.load(url)
            tvFoodName.text=foodData.name
            tvFoodPrice.text=itemView.context.getString(R.string.bag_price, formatter.format(foodData.price))
        }

        // 아이템 클릭 리스너 설정
        itemView.setOnClickListener {
            listener.onFoodItemClick(foodData)
        }

    }
}