package com.capston2024.capstonapp.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.capston2024.capstonapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinds()
    }
    private fun initBinds() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}