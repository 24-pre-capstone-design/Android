package com.capston2024.capstonapp.presentation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.capston2024.capstonapp.databinding.ActivityStartBinding
import com.capston2024.capstonapp.presentation.main.MainActivity

class StartActivity : AppCompatActivity() {
    private lateinit var binding:ActivityStartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinds()
        clickButton()
    }

    private fun initBinds(){
        binding= ActivityStartBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun clickButton(){
        binding.btnAiMode.setOnClickListener {
            var intent = Intent(applicationContext, OrderingActivityTemp::class.java)
            startActivity(intent)
        }
        binding.btnBasic.setOnClickListener {
            var intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
        }
    }

}
