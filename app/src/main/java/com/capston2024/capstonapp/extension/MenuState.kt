package com.capston2024.capstonapp.extension

import com.capston2024.capstonapp.data.responseDto.ResponseMenuDto

sealed class MenuState {
    data object Loading: MenuState()
    data class Success(var menuList: List<ResponseMenuDto.Menu>) : MenuState()
    data object Error: MenuState()
}