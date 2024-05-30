package com.capston2024.capstonapp.data.datasource

import com.capston2024.capstonapp.data.requestDto.RequestOrderDto
import com.capston2024.capstonapp.data.responseDto.ResponseFoodDto
import com.capston2024.capstonapp.data.responseDto.ResponsePictureDto
import com.capston2024.capstonapp.data.responseDto.ResponseMenuDto
import com.capston2024.capstonapp.data.responseDto.ResponseMockDto
import com.capston2024.capstonapp.data.responseDto.ResponseOrderDto
import com.capston2024.capstonapp.data.responseDto.ResponseOrderHistoryDto
import com.capston2024.capstonapp.data.responseDto.ResponsePaymentIdDto

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

    suspend fun getPaymentId():ResponsePaymentIdDto

    suspend fun makeOrderHistory(
        orderHistory:RequestOrderDto
    ):ResponseOrderDto

    suspend fun getOrderHistory(
        paymentId:Int
    ):ResponseOrderHistoryDto

    suspend fun getAllFoods():ResponseFoodDto
}