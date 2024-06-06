package com.capston2024.capstonapp.presentation.main

import com.capston2024.capstonapp.data.FragmentMode

interface ChangeFragmentListener {
    fun replaceFragment(type:FragmentMode, id:Int)
    fun replaceMenuName(menuName:String)
    fun changeMenuID(menuID:Int)
}
