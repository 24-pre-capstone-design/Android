package com.capston2024.capstonapp.extension

import com.capston2024.capstonapp.data.responseDto.ResponseMockDto

sealed class UiState {
    data object Loading: UiState()
    data class Success(var mock: List<ResponseMockDto.MockModel>) : UiState()
    data object Error: UiState()
}