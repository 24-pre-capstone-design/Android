package com.capston2024.capstonapp.extension


sealed class PaymentIdState {
    data object Loading: PaymentIdState()
    data class Success(val paymentId: Int) : PaymentIdState()
    data object Error: PaymentIdState()
}