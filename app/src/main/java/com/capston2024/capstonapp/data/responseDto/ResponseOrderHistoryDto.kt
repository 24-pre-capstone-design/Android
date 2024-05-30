package com.capston2024.capstonapp.data.responseDto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseOrderHistoryDto (
    @SerialName("success")
    val success:Boolean,
    @SerialName("status")
    val status:String,
    @SerialName("message")
    val message:String,
    @SerialName("data")
    val data:Data
){
    @Serializable
    data class Data(
        @SerialName("paymentId")
        val paymentId:Int,
        @SerialName("orderHistoryResponseDtoList")
        val orderHistoryResponseDtoList:List<OrderHistoryResponseDtoList>,
        @SerialName("sumOfPaymentCost")
        val sumOfPaymentCost:Int,
    )
    @Serializable
    data class OrderHistoryResponseDtoList(
        @SerialName("paymentId")
        val paymentId:Int,
        @SerialName("orderResponseDtoList")
        val orderResponseDtoList:List<OrderResponseDtoList>,
        @SerialName("orderHistoryStatus")
        val orderHistoryStatus:String,
        @SerialName("sumOfOrderHistoryCost")
        val sumOfOrderHistoryCost:Int,
        @SerialName("orderedAt")
        val orderedAt: String,
    )
    @Serializable
    data class OrderResponseDtoList(
        @SerialName("orderHistoryId")
        val orderHistoryId:Int,
        @SerialName("foodName")
        val foodName:String,
        @SerialName("quantity")
        val quantity:Int,@SerialName("sumOfOrderCost")
        val sumOfOrderCost:Int,
    )

    fun toOrderHistoryList(): List<OrderHistoryResponseDtoList> {
        return data.orderHistoryResponseDtoList.map { orderHistory ->
            OrderHistoryResponseDtoList(
                paymentId = orderHistory.paymentId,
                orderResponseDtoList = orderHistory.orderResponseDtoList,
                orderHistoryStatus = orderHistory.orderHistoryStatus,
                sumOfOrderHistoryCost = orderHistory.sumOfOrderHistoryCost,
                orderedAt = orderHistory.orderedAt
            )
        }
    }

    fun toOrderResponseList(): List<OrderResponseDtoList> {
        return data.orderHistoryResponseDtoList.flatMap { orderHistory ->
            orderHistory.orderResponseDtoList.map { orderResponse ->
                OrderResponseDtoList(
                    orderHistoryId = orderResponse.orderHistoryId,
                    foodName = orderResponse.foodName,
                    quantity = orderResponse.quantity,
                    sumOfOrderCost = orderResponse.sumOfOrderCost
                )
            }
        }
    }



}