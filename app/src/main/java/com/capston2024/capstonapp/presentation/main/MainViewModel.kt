package com.capston2024.capstonapp.presentation.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.capston2024.capstonapp.data.Bag

class MainViewModel : ViewModel() {

    private val _orderList = MutableLiveData<MutableList<Bag>>() // 추후 MutableLiveData로 변경
    val orderList: LiveData<MutableList<Bag>>
        get() = _orderList

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
}

