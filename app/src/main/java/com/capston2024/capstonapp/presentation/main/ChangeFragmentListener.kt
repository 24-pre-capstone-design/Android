package com.capston2024.capstonapp.presentation.main

import androidx.fragment.app.Fragment
import com.capston2024.capstonapp.data.FragmentType

interface ChangeFragmentListener {
    fun replaceFragment(fragment: Fragment, type:FragmentType)
    fun replaceMenuName(menuName:String)
    fun changeMenuID(menuID:Int)
}
