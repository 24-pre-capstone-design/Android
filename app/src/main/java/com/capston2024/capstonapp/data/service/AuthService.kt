package com.capston2024.capstonapp.data.service

import com.capston2024.capstonapp.data.responseDto.ResponseMockDto
import retrofit2.http.GET
import retrofit2.http.Query

interface AuthService {
    @GET("api/users")
    suspend fun getFoods(
       @Query("page") page:Int
    ): ResponseMockDto
}