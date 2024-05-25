package com.capston2024.capstonapp.domain.repository

import com.capston2024.capstonapp.data.requestDto.RequestOrderDto
import com.capston2024.capstonapp.data.responseDto.ResponseFoodDto
import com.capston2024.capstonapp.data.responseDto.ResponseMenuDto
import com.capston2024.capstonapp.data.responseDto.ResponseMockDto
import com.capston2024.capstonapp.data.responseDto.ResponseOrderDto
import com.capston2024.capstonapp.data.responseDto.ResponseOrderHistoryDto
import com.capston2024.capstonapp.data.responseDto.ResponsePaymentIdDto
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

    suspend fun getPaymentId():Result<ResponsePaymentIdDto>
    suspend fun makeOrder(
        order:RequestOrderDto
    ):Result<ResponseOrderDto>

    suspend fun getOrderHistory(
        paymentId:Int
    ):Result<ResponseOrderHistoryDto>

    suspend fun getAllFoods():Result<ResponseFoodDto>
}