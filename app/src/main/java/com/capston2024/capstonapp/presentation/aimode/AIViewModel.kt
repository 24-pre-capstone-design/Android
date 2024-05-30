package com.capston2024.capstonapp.presentation.aimode

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capston2024.capstonapp.data.responseDto.ResponseFoodDto
import com.capston2024.capstonapp.domain.repository.AuthRepository
import com.capston2024.capstonapp.extension.FoodState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class AIViewModel @Inject constructor(
    private val authRepository: AuthRepository
):ViewModel(){
    private val _allFoodState = MutableStateFlow<FoodState>(FoodState.Loading)
    var allFoodState: StateFlow<FoodState> = _allFoodState.asStateFlow()

    private val _foodsList = MutableLiveData<List<ResponseFoodDto.Food>>()
    val foodsList:LiveData<List<ResponseFoodDto.Food>>
        get()=_foodsList
    fun getData(){
        viewModelScope.launch {
            authRepository.getAllFoods().onSuccess { response ->
                when (response.success) {
                    false -> {
                        _allFoodState.value = FoodState.Error("요청한 정보를 찾을 수 없습니다.")
                    }
                    true -> {
                        _allFoodState.value = FoodState.Success(response.makeFoodList())
                        Log.d("foodsviewmodel","success,${response.message}")
                    }
                }
            }.onFailure { throwable ->
                if (throwable is HttpException) {
                    val errorBody = throwable.response()?.errorBody()?.string()
                    val errorResponse = Json.decodeFromString<ResponseFoodDto>(errorBody ?: "")
                    _allFoodState.value = FoodState.Error(errorResponse.message)
                } else {
                    _allFoodState.value = throwable?.message?.let { FoodState.Error(it) }!!
                }
            }
        }
    }

    fun updateFoodsList(newList:List<ResponseFoodDto.Food>){
        _foodsList.value=newList
    }

    fun getFoodByName(foodName:String):  ResponseFoodDto.Food? {
        val foodList = foodsList.value
        return foodList?.firstOrNull { it.name.equals(foodName, ignoreCase = true) }
    }
}