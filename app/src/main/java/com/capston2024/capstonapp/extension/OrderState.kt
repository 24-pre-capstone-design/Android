package com.capston2024.capstonapp.extension


sealed class OrderState {
    data object Loading:OrderState()
    data object Success:OrderState()
    data object Error:OrderState()
}