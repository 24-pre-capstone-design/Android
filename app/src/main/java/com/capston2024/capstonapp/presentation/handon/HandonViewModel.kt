package com.capston2024.capstonapp.presentation.handon

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capston2024.capstonapp.domain.repository.AuthRepository
import com.capston2024.capstonapp.extension.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HandonViewModel @Inject constructor(
    private val authRepository: AuthRepository
): ViewModel(){
    private val _getState = MutableStateFlow<UiState>(UiState.Loading)
    var getState: StateFlow<UiState> = _getState.asStateFlow()

    fun getData(albumId: Int){
        viewModelScope.launch {
            authRepository.getPhotoList(albumId).onSuccess { response ->
                _getState.value= UiState.Success(response)
            }.onFailure {
                _getState.value= UiState.Error
                Log.e("error", "Error is : ${it.message}")
            }
        }
    }
}