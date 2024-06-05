package com.capston2024.capstonapp.presentation.main.foods

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
class FoodsViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _foodState = MutableStateFlow<FoodState>(FoodState.Loading)
    var foodState: StateFlow<FoodState> = _foodState.asStateFlow()

    fun getData(id: Int) {
        viewModelScope.launch {
            authRepository.getFoodList(id).onSuccess { response ->
                when (response.success) {
                    false -> {
                        _foodState.value = FoodState.Error("요청한 정보를 찾을 수 없습니다.")
                    }

                    true -> {
                        _foodState.value = FoodState.Success(response.makeFoodList())
                    }
                }
            }.onFailure { throwable ->
                if (throwable is HttpException) {
                    val errorBody = throwable.response()?.errorBody()?.string()
                    val errorResponse = Json.decodeFromString<ResponseFoodDto>(errorBody ?: "")
                    _foodState.value = FoodState.Error(errorResponse.message)
                } else {
                    _foodState.value = throwable?.message?.let { FoodState.Error(it) }!!
                }
            }
        }
    }
}