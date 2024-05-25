package com.capston2024.capstonapp.data.requestDto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RequestOrderDto (
    @SerialName("paymentId")
    val paymentId:Int,
    @SerialName("orderRequestDtoList")
    val orderRequestDtoList:List<OrderRequestDtoList>
){
    @Serializable
    data class OrderRequestDtoList(
        @SerialName("foodId")
        val foodId:Int,
        @SerialName("quantity")
        val quantity:Int
    )
}

