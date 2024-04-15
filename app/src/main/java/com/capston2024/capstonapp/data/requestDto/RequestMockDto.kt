package com.capston2024.capstonapp.data.requestDto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RequestMockDto (
    @SerialName("albumId")
    val albumId:Int,
)