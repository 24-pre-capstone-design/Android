package com.capston2024.capstonapp.presentation.main.foods

import android.util.Log
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
import kotlinx.serialization.decodeFromString
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
                        Log.d("foodsviewmodel","success,${response.message}")
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
            /*try {
                val response = authRepository.getFoodList(id)
                if (response.isSuccess){
                    val data=response.getOrNull()
                    data?.let{
                        _getState.value=FoodState.Success(it)
                    }
                    Log.d("success","Success!!!!")
                }
                else {
                    response.recoverCatching {
                        if (it is HttpException) {
                            val data = it.response()?.errorBody()?.byteString()?.toString()
                            //val errorBody = data?.substringAfter("message")
                            if (data != null) {
                                Log.e("error", "messages${data}")
                            }
                            _getState.value = FoodState.Error
                        }
                    }
                }
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                errorBody?.let {
                    val errorResponse = Json.decodeFromString<ResponseFoodDto>(errorBody ?: "")
                    Log.e("error", "message: ${errorResponse.message}")
                }
                _getState.value = FoodState.Error
            }*/
            /*authRepository.getFoodList(id).onSuccess { response ->
                _getState.value=FoodState.Success(response)
            }.onFailure {
                if(it is HttpException){
                    val data = it.response()?.errorBody()?.byteString()?.toString()
                    val errorBody=data?.substringAfter("message")
                    if(errorBody!=null){
                        Log.e("error", "message${errorBody}")
                    }
                    _getState.value= FoodState.Error
                }else
                    Log.e("error","Error: ${it.message}")
            }*/
        }
    }

    /* fun getData(albumId: Int){
         viewModelScope.launch {
             authRepository.getPhotoList(albumId).onSuccess { response ->
                 _getState.value=UiState.Success(response)
             }.onFailure {
                 _getState.value=UiState.Error
                 Log.e("error", "Error is : ${it.message}")
             }
         }
     }*/
}