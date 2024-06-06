package com.capston2024.capstonapp.data.service

import com.capston2024.capstonapp.data.requestDto.RequestOrderDto
import com.capston2024.capstonapp.data.responseDto.ResponseFoodDto
import com.capston2024.capstonapp.data.responseDto.ResponsePictureDto
import com.capston2024.capstonapp.data.responseDto.ResponseMenuDto
import com.capston2024.capstonapp.data.responseDto.ResponseOrderDto
import com.capston2024.capstonapp.data.responseDto.ResponseOrderHistoryDto
import com.capston2024.capstonapp.data.responseDto.ResponsePaymentIdDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface AuthService {

    @GET("/foodCategory")
    suspend fun getMenus():ResponseMenuDto

    @GET("/food/category/{foodCategoryId}")
    suspend fun getFoods(
        @Query("foodCategoryId") id:Int
    ):ResponseFoodDto

    @GET("/resources/files/{pictureURL}")
    suspend fun getImageUrl(@Query("pictureURL") pictureURL:String
    ): ResponsePictureDto

    //paymentId
    @POST("/payment")
    suspend fun getPaymentId(): ResponsePaymentIdDto

    @POST("/orderHistory")
    suspend fun makeOrderHistory(
        @Body requestOrderListDto: RequestOrderDto
    ): ResponseOrderDto

    @GET("/orderHistory/payment/{paymentId}")
    suspend fun getOrderHistory(
        @Path("paymentId") paymentId:Int
    ): ResponseOrderHistoryDto

    @GET("/food")
    suspend fun getAllFoods():ResponseFoodDto
}