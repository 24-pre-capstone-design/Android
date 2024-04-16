package com.capston2024.capstonapp.data.datasourceImpl

import android.util.Log
import com.capston2024.capstonapp.data.datasource.AuthDataSource
import com.capston2024.capstonapp.data.responseDto.ResponseMockDto
import com.capston2024.capstonapp.data.service.AuthService
import org.json.JSONArray
import org.json.JSONException
import retrofit2.Response
import javax.inject.Inject

class AuthDataSourceImpl @Inject constructor(
    private val authService: AuthService,
) : AuthDataSource {
    override suspend fun getPhotos(page: Int): ResponseMockDto = authService.getFoods(page)

}