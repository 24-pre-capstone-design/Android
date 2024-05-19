package com.capston2024.capstonapp.data.service

import com.capston2024.capstonapp.data.responseDto.ResponseFoodDto
import com.capston2024.capstonapp.data.responseDto.ResponsePictureDto
import com.capston2024.capstonapp.data.responseDto.ResponseMenuDto
import com.capston2024.capstonapp.data.responseDto.ResponseMockDto
import retrofit2.http.GET
import retrofit2.http.Query

interface AuthService {
    @GET("api/users")
    suspend fun getMock(
       @Query("page") page:Int
    ): ResponseMockDto

    @GET("/foodCategory")
    suspend fun getMenus():ResponseMenuDto

    @GET("/food/category/{foodCategoryId}")
    suspend fun getFoods(
        @Query("foodCategoryId") id:Int
    ):ResponseFoodDto

    @GET("/resources/files/{pictureURL}")
    suspend fun getImageUrl(@Query("pictureURL") pictureURL:String
    ): ResponsePictureDto
}