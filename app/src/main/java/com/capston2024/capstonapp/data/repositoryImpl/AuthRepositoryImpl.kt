package com.capston2024.capstonapp.data.repositoryImpl

import com.capston2024.capstonapp.data.datasource.AuthDataSource
import com.capston2024.capstonapp.data.requestDto.RequestOrderDto
import com.capston2024.capstonapp.data.responseDto.ResponseFoodDto
import com.capston2024.capstonapp.data.responseDto.ResponseMenuDto
import com.capston2024.capstonapp.data.responseDto.ResponseOrderDto
import com.capston2024.capstonapp.data.responseDto.ResponseOrderHistoryDto
import com.capston2024.capstonapp.data.responseDto.ResponsePaymentIdDto
import com.capston2024.capstonapp.data.responseDto.ResponsePictureDto
import com.capston2024.capstonapp.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authDataSource: AuthDataSource
) : AuthRepository{

    override suspend fun getMenuList(): Result<ResponseMenuDto> {
        return runCatching {
            authDataSource.getMenus()
        }
    }

    override suspend fun getFoodList(id: Int): Result<ResponseFoodDto> {
        return runCatching {
            authDataSource.getFoods(id)
        }
    }

    override suspend fun getPicture(pictureURL:String): Result<ResponsePictureDto> {
        return kotlin.runCatching {
            authDataSource.getPicture(pictureURL)
        }
    }

    override suspend fun getPaymentId(): Result<ResponsePaymentIdDto> {
        return kotlin.runCatching {
            authDataSource.getPaymentId()
        }
    }

    override suspend fun makeOrder(order: RequestOrderDto): Result<ResponseOrderDto> {
        return kotlin.runCatching {
            authDataSource.makeOrderHistory(order)
        }
    }

    override suspend fun getOrderHistory(paymentId: Int): Result<ResponseOrderHistoryDto> {
        return kotlin.runCatching {
            authDataSource.getOrderHistory(paymentId)
        }
    }

    override suspend fun getAllFoods(): Result<ResponseFoodDto> {
        return kotlin.runCatching {
            authDataSource.getAllFoods()
        }
    }
}