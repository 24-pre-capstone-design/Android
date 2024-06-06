package com.capston2024.capstonapp.extension

import com.capston2024.capstonapp.data.responseDto.ResponseFoodDto

sealed class FoodState{
    data object Loading: FoodState()
    data class Success(var foodList: List<ResponseFoodDto.Food>) : FoodState()
    data class Error(val message:String): FoodState()
}
