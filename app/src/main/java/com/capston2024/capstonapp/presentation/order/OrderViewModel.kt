package com.capston2024.capstonapp.presentation.order

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capston2024.capstonapp.domain.repository.AuthRepository
import com.capston2024.capstonapp.extension.OrderHistoryState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val authRepository:AuthRepository
): ViewModel() {
    private val _orderHistoryState = MutableStateFlow<OrderHistoryState>(OrderHistoryState.Loading)
    val orderHistoryState:StateFlow<OrderHistoryState> = _orderHistoryState.asStateFlow()

    private val _orderTotalPrice = MutableLiveData<Int>(0)
    var orderTotalPrice:LiveData<Int> = _orderTotalPrice
    fun getOrderHistory(paymentId:Int){
        viewModelScope.launch {
            authRepository.getOrderHistory(paymentId).onSuccess { response ->
                when(response.success){
                    true -> {
                        Log.d("orderviewmodel","getorderhistory success:${response.data.paymentId}")
                        Log.d("orderviewmodel","getorderhistory success: ${response.toOrderHistoryList()}")
                        _orderHistoryState.value=OrderHistoryState.Success(response.toOrderHistoryList())
                        _orderTotalPrice.value = response.data.sumOfPaymentCost
                    }
                    false -> {
                        _orderHistoryState.value= OrderHistoryState.Error
                    }
                }

            }.onFailure {
                Log.e("orderviewmodel","Error:${it.message}")
                Log.e("orderviewmodel", Log.getStackTraceString(it))
                if (it is HttpException) {
                    try {
                        val errorBody: ResponseBody? = it.response()?.errorBody()
                        val errorBodyString = errorBody?.string() ?: ""

                        // JSONObject를 사용하여 메시지 추출
                        val jsonObject = JSONObject(errorBodyString)
                        val errorMessage = jsonObject.optString("message", "Unknown error")

                        // 추출된 에러 메시지 로깅
                        Log.e("mainviewmodel", "Error message: $errorMessage")
                    } catch (e: Exception) {
                        // JSON 파싱 실패 시 로깅
                        Log.e("mainviewmodel", "Error parsing error body", e)
                    }
                }
            }
        }
    }
}