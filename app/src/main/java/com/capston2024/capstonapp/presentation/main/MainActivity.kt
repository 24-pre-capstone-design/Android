package com.capston2024.capstonapp.presentation.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.capston2024.capstonapp.R
import com.capston2024.capstonapp.databinding.ActivityMainBinding
import com.capston2024.capstonapp.presentation.course.CourseFragment
import com.capston2024.capstonapp.presentation.handon.HandonFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity: AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val viewModel: MainViewModel by viewModels()
    var bagIsShow=false
    var bagFragment = BagFragment()
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)

        initBinds()
        settingFragments(R.id.fcv_menu, MenuFragment(), "menuFragment")
        settingFragments(R.id.fcv_main, CourseFragment(), "courseFragment")
    }

    private fun initBinds(){
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    fun settingFragments(fcv: Int, fragment: Fragment, name:String) {
        val currentFragment = supportFragmentManager.findFragmentById(fcv)
        if(currentFragment==null){
            supportFragmentManager.beginTransaction()
                .replace(fcv,fragment)
                .commit()
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    fun clickMenuBar(selectedBtn: Button, currentBtn: Button){
        when(selectedBtn.text){
            "코스요리"->{replaceFragment(CourseFragment())}
            "한돈"->{replaceFragment(HandonFragment())}
        }
        selectedBtn.background = applicationContext.resources.getDrawable(R.drawable.ic_rectangle_beige)
        currentBtn.background=applicationContext.resources.getDrawable(R.drawable.ic_rectangle_white)
    }

    fun replaceFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction()
            .replace(R.id.fcv_main, fragment)
            .commit()
    }
}