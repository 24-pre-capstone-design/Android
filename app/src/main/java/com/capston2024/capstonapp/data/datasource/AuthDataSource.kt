package com.capston2024.capstonapp.data.datasource

import com.capston2024.capstonapp.data.responseDto.ResponseFoodDto
import com.capston2024.capstonapp.data.responseDto.ResponsePictureDto
import com.capston2024.capstonapp.data.responseDto.ResponseMenuDto
import com.capston2024.capstonapp.data.responseDto.ResponseMockDto

interface AuthDataSource {
    suspend fun getPhotos(
        page:Int
    ): ResponseMockDto

    suspend fun getMenus(
    ): ResponseMenuDto

    suspend fun getFoods(
        id:Int
    ):ResponseFoodDto

    suspend fun getPicture(
        pictureURL:String
    ):ResponsePictureDto
}