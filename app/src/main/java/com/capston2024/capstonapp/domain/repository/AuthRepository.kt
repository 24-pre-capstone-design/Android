package com.capston2024.capstonapp.domain.repository

import com.capston2024.capstonapp.data.responseDto.ResponseMockDto

interface AuthRepository {
    suspend fun getPhotoList(
        albumId:Int,
    ):Result<List<ResponseMockDto.MockModel>>
}