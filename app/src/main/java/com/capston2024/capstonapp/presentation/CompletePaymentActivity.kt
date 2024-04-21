package com.capston2024.capstonapp.presentation

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.capston2024.capstonapp.databinding.ActivityCompletePaymentBinding

class CompletePaymentActivity : AppCompatActivity() {
    private lateinit var binding:ActivityCompletePaymentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinds()
        Handler(Looper.getMainLooper()).postDelayed({
            var intent = Intent(applicationContext, StartActivity::class.java)
            startActivity(intent)
        }, 5000)
    }

    private fun initBinds(){
        binding= ActivityCompletePaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
