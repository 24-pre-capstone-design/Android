package com.capston2024.capstonapp.presentation.startend

import android.content.Intent
import android.os.Bundle
import com.capston2024.capstonapp.databinding.ActivityStartBinding
import androidx.appcompat.app.AppCompatActivity
import com.capston2024.capstonapp.R
import com.capston2024.capstonapp.presentation.main.MainActivity

class StartActivity : AppCompatActivity() {
    private lateinit var binding:ActivityStartBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        binding= ActivityStartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.startScreen.setOnClickListener() {
            var intent = Intent(applicationContext, MainActivity :: class.java)
            startActivity(intent)
        }
    }
}