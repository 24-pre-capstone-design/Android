package com.capston2024.capstonapp.data.datasourceImpl

import android.util.Log
import com.capston2024.capstonapp.data.datasource.AuthDataSource
import com.capston2024.capstonapp.data.requestDto.RequestOrderDto
import com.capston2024.capstonapp.data.responseDto.ResponseFoodDto
import com.capston2024.capstonapp.data.responseDto.ResponseMenuDto
import com.capston2024.capstonapp.data.responseDto.ResponseMockDto
import com.capston2024.capstonapp.data.responseDto.ResponseOrderDto
import com.capston2024.capstonapp.data.responseDto.ResponseOrderHistoryDto
import com.capston2024.capstonapp.data.responseDto.ResponsePaymentIdDto
import com.capston2024.capstonapp.data.responseDto.ResponsePictureDto
import com.capston2024.capstonapp.data.service.AuthService
import org.json.JSONArray
import org.json.JSONException
import retrofit2.Response
import javax.inject.Inject

class AuthDataSourceImpl @Inject constructor(
    private val authService: AuthService,
) : AuthDataSource {
    override suspend fun getPhotos(page: Int): ResponseMockDto = authService.getMock(page)
    override suspend fun getMenus(): ResponseMenuDto = authService.getMenus()
    override suspend fun getFoods(id: Int): ResponseFoodDto = authService.getFoods(id)
    override suspend fun getPicture(pictureURL:String): ResponsePictureDto = authService.getImageUrl(pictureURL)
    override suspend fun getPaymentId(): ResponsePaymentIdDto = authService.getPaymentId()
    override suspend fun makeOrderHistory(orderHistory: RequestOrderDto): ResponseOrderDto = authService.makeOrderHistory(orderHistory)
    override suspend fun getOrderHistory(paymentId: Int): ResponseOrderHistoryDto = authService.getOrderHistory(paymentId)

    override suspend fun getAllFoods(): ResponseFoodDto = authService.getAllFoods()
}