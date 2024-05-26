package com.capston2024.capstonapp.data.responseDto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponsePaymentIdDto (
    @SerialName("success")
    val success:Boolean,
    @SerialName("status")
    val status:String,
    @SerialName("message")
    val message:String,
    @SerialName("data")
    val data:Int
)