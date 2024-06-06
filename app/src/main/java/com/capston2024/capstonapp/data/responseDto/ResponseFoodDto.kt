package com.capston2024.capstonapp.data.responseDto

import android.util.Log
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseFoodDto (
    @SerialName("success")
    val success:Boolean,
    @SerialName("status")
    val status:String,
    @SerialName("message")
    val message:String,
    @SerialName("data")
    val data:List<Food>
){
    @Serializable
    data class Food(
        @SerialName("id")
        val id:Int,
        @SerialName("foodCategory")
        val foodCategory:ResponseMenuDto.Menu,
        @SerialName("name")
        val name:String,
        @SerialName("price")
        val price:Int,
        @SerialName("pictureURL")
        var pictureURL:String,
        @SerialName("status")
        val status:String,
        @SerialName("createdAt")
        val createdAt:String,
    )

    fun makeFoodList(): List<Food> = data.map { menu ->
        Food(
            id=menu.id,
            foodCategory = menu.foodCategory,
            name=menu.name,
            price=menu.price,
            pictureURL = menu.pictureURL,
            status=menu.status,
            createdAt = menu.createdAt,
        )
    }
}


