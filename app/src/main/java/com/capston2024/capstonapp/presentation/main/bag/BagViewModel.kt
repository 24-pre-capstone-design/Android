package com.capston2024.capstonapp.presentation.main.bag

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capston2024.capstonapp.data.Bag
import com.capston2024.capstonapp.data.requestDto.RequestOrderDto
import com.capston2024.capstonapp.domain.repository.AuthRepository
import com.capston2024.capstonapp.extension.OrderState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class BagViewModel @Inject constructor(
    private val authRepository: AuthRepository
): ViewModel(){

    private val _orderState = MutableStateFlow<OrderState>(OrderState.Loading)
    val orderState:StateFlow<OrderState> = _orderState
    private var bagList: List<RequestOrderDto.OrderRequestDtoList>? = null

    fun makeBagList(bags:List<Bag>){
        bagList = bags.map { bag ->
            RequestOrderDto.OrderRequestDtoList(
                foodId = bag.id,
                quantity = bag.count
            )
        }
    }
    fun makeOrderHistory(paymentId: Int){
        var request = RequestOrderDto(paymentId, bagList!!)
        viewModelScope.launch {
            authRepository.makeOrder(request).onSuccess {
                _orderState.value=OrderState.Success
                Log.d("bagviewmodel","success")
            }.onFailure {
                Log.e("bagviewmodel","makeorderhistory Error:${it.message}")
                if (it is HttpException) {
                    val data = it.response()?.errorBody()?.byteString()?.toString()
                    val errorBody = data?.substringAfter("message")
                    if (errorBody != null) {
                        Log.e("bagviewmodel", "message${errorBody}")
                    }
                }
            }
        }
    }
}