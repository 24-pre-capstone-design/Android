package com.capston2024.capstonapp.presentation.main.foods

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capston2024.capstonapp.data.responseDto.ResponseFoodDto
import com.capston2024.capstonapp.domain.repository.AuthRepository
import com.capston2024.capstonapp.extension.PictureState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PictureViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel(){
    private val _pictureState= MutableStateFlow<PictureState>(PictureState.Loading)
    var pictureState:StateFlow<PictureState> = _pictureState.asStateFlow()

    /*fun getData(foodList: List<ResponseFoodDto.Food>) {
        viewModelScope.launch {
            foodList.forEach { food ->
                Log.d("food","food:${food.name},url:${food.pictureURL}")
                authRepository.getPicture(food.pictureURL).onSuccess { response ->
                    // 여기서는 response 객체가 갱신된 pictureURL을 포함하고 있다고 가정합니다.
                    food.pictureURL = response.pictureURL // food 객체의 pictureURL을 업데이트합니다.
                    _pictureState.value = PictureState.Success(foodList) // 업데이트된 foodList를 포함하여 상태를 업데이트합니다.
                }.onFailure { throwable ->
                    _pictureState.value = PictureState.Error
                    Log.e("Error", "Error:${throwable.message}")
                }
            }
        }
    }*/


    /*fun getData(foodList:List<ResponseFoodDto.Food>){

        viewModelScope.launch {
            authRepository.getPicture(pictureURL).onSuccess { response ->
                _pictureState.value=PictureState.Success(response.pictureURL)
            }.onFailure { throwable ->
                _pictureState.value=PictureState.Error
                Log.e("Error","Error:${throwable.message}")
            }
        }
    }*/
}