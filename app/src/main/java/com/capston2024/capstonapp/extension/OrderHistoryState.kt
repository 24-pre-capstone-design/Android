package com.capston2024.capstonapp.extension

import com.capston2024.capstonapp.data.responseDto.ResponseOrderHistoryDto

sealed class OrderHistoryState {
    data object Loading: OrderHistoryState()
    data class Success(var orderHistoryList: List<ResponseOrderHistoryDto.OrderHistoryResponseDtoList>): OrderHistoryState()
    data object Error: OrderHistoryState()
}