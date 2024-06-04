package com.capston2024.capstonapp.presentation.startend

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.capston2024.capstonapp.BuildConfig
import com.capston2024.capstonapp.data.responseDto.ResponseFoodDto

class StartViewModel:ViewModel() {
    private val _foodImages= MutableLiveData<List<String>>()
    val foodImages:LiveData<List<String>> = _foodImages

    fun updateImages(foodImgList:List<String>){
        Log.d("startviewmodel","foodimglist:${foodImgList}")
        _foodImages.value=foodImgList
    }
}