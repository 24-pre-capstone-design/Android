package com.capston2024.capstonapp.data.responseDto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseMockDto (
    @SerialName("page")
    val page: Int,
    @SerialName("per_page")
    val perPage: Int,
    @SerialName("total")
    val total: Int,
    @SerialName("total_pages")
    val totalPages: Int,
    @SerialName("data")
    val data: List<MockModel>,
    @SerialName("support")
    val support: Support
) {
    @Serializable
    data class MockModel(
        @SerialName("id")
        var id: Int,
        @SerialName("email")
        val email: String,
        @SerialName("first_name")
        val firstName: String,
        @SerialName("last_name")
        val lastName: String,
        @SerialName("avatar")
        val avatar: String
    ) :  java.io.Serializable // Serializable을 구현합니다.

    @Serializable
    data class Support(
        @SerialName("url")
        val url: String,
        @SerialName("text")
        val text: String
    )

    fun toMockModelList(): List<MockModel> = data.map { friend ->
        MockModel(
            id = friend.id,
            email = friend.email,
            firstName = friend.firstName,
            lastName = friend.lastName,
            avatar = friend.avatar,
        )
    }
}
