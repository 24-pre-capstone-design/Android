package com.capston2024.capstonapp.data.datasource

import com.capston2024.capstonapp.data.responseDto.ResponseMockDto

interface AuthDataSource {
    suspend fun getPhotos(
        pate:Int
    ): ResponseMockDto
}