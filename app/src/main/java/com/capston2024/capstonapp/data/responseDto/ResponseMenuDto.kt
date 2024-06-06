package com.capston2024.capstonapp.data.responseDto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseMenuDto(
    @SerialName("success")
    val success:Boolean,
    @SerialName("status")
    val status:String,
    @SerialName("message")
    val message:String,
    @SerialName("data")
    val data:List<Menu>
){
    @Serializable
    data class Menu(
        @SerialName("id")
        val id:Int,
        @SerialName("name")
        val name:String
    )

    fun makeMenuList(): List<Menu> = data.map { menu ->
        Menu(
            id = menu.id,
            name=menu.name
        )
    }
}
