package com.capston2024.capstonapp.extension

import com.capston2024.capstonapp.data.responseDto.ResponseFoodDto

sealed class PictureState {
    data object Loading: PictureState()
    data class Success(var foodList: List<ResponseFoodDto.Food>) : PictureState()
    data object Error: PictureState()
}