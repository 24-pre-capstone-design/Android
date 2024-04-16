package com.capston2024.capstonapp.data.repositoryImpl

import android.util.Log
import com.capston2024.capstonapp.data.datasource.AuthDataSource
import com.capston2024.capstonapp.data.responseDto.ResponseMockDto
import com.capston2024.capstonapp.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authDataSource: AuthDataSource
) : AuthRepository{
    override suspend fun getPhotoList(albumId: Int): Result<List<ResponseMockDto.MockModel>> {
        return runCatching {
            authDataSource.getPhotos(albumId).toMockModelList()
        }
    }
}