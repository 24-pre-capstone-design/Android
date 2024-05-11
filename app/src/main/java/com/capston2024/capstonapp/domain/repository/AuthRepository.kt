package com.capston2024.capstonapp.domain.repository

import com.capston2024.capstonapp.data.responseDto.ResponseFoodDto
import com.capston2024.capstonapp.data.responseDto.ResponseMenuDto
import com.capston2024.capstonapp.data.responseDto.ResponseMockDto
import com.capston2024.capstonapp.data.responseDto.ResponsePictureDto

interface AuthRepository {
    suspend fun getPhotoList(
        albumId:Int,
    ):Result<List<ResponseMockDto.MockModel>>

    suspend fun getMenuList():Result<ResponseMenuDto>

    suspend fun getFoodList(
        id:Int,
    ):Result<ResponseFoodDto>

    suspend fun getPicture(pictureURL:String):Result<ResponsePictureDto>
}