package com.capston2024.capstonapp.presentation.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.capston2024.capstonapp.data.Bag
import com.capston2024.capstonapp.data.FragmentType
import com.capston2024.capstonapp.data.responseDto.ResponseFoodDto
import com.capston2024.capstonapp.data.responseDto.ResponseMenuDto
import com.capston2024.capstonapp.domain.repository.AuthRepository
import com.capston2024.capstonapp.extension.FoodState
import com.capston2024.capstonapp.extension.MenuState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject
import kotlin.properties.Delegates

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authRepository: AuthRepository
): ViewModel() {
    private val _orderList = MutableLiveData<MutableList<Bag>>()
    val orderList: LiveData<MutableList<Bag>>
        get() = _orderList

    private val _firstMenu= MutableLiveData<ResponseMenuDto.Menu>()
    val firstMenu: LiveData<ResponseMenuDto.Menu> = _firstMenu

    // MutableSharedFlow에서 MutableStateFlow로 변경
    private val _menuState = MutableStateFlow<MenuState>(MenuState.Loading) // 초기 상태로 Loading 설정
    val menuState: StateFlow<MenuState> = _menuState

    private val _mode = MutableLiveData<FragmentType>()
    var mode:LiveData<FragmentType> = _mode

    private val _menuID=MutableLiveData<Int>()
    var menuID:LiveData<Int> = _menuID

    init {
        _orderList.value = mutableListOf()  // 초기화: 빈 MutableList로 설정
    }

    fun addToOrderList(items: MutableList<Bag>) {
        val currentList = _orderList.value ?: mutableListOf()
        currentList.addAll(items)
        _orderList.value = currentList
    }

    fun clearOrderList() {
        _orderList.value?.clear()
    }

    fun showAIModeButton(type:Int):Boolean{
        if(type==0) return true
        else return false
    }

    fun getMenu(){
        viewModelScope.launch {
            authRepository.getMenuList().onSuccess { response ->
                _menuState.value = MenuState.Success(response.makeMenuList()) // emit 대신 value 사용
                //Log.d("Success","Success~~:mainviewmodel")
                _firstMenu.value=response.makeMenuList()[0]
            }.onFailure {
                Log.e("Error","Error:${it.message}")
                if (it is HttpException) {
                    val data = it.response()?.errorBody()?.byteString()?.toString()
                    val errorBody = data?.substringAfter("message")
                    if (errorBody != null) {
                        Log.e("erorr", "message${errorBody}")
                    }
                    _menuState.value = MenuState.Error // emit 대신 value 사용
                }
            }
        }
    }

    fun changeMode(modeNum:FragmentType){
        _mode.value=modeNum
       // Log.d("mainviewmodel","mainviewmodel-changemode:${modeNum}")
    }

    fun changeMenuID(menuID:Int){
        _menuID.value=menuID
    }
}