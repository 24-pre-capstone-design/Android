package com.capston2024.capstonapp.extension

import com.capston2024.capstonapp.data.responseDto.ResponseOrderDto

sealed class OrderState {
    data object Loading:OrderState()
    data object Success:OrderState()
    data object Error:OrderState()
}