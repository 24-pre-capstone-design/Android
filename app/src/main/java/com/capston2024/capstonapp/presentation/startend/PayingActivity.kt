package com.capston2024.capstonapp.presentation.startend

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.capston2024.capstonapp.databinding.ActivityPayingBinding

class PayingActivity : AppCompatActivity() {
    private lateinit var binding:ActivityPayingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinds()

        Handler(Looper.getMainLooper()).postDelayed({
            var intent = Intent(applicationContext, CompletePaymentActivity::class.java)
            startActivity(intent)
        }, 5000)
    }

    private fun initBinds(){
        binding=ActivityPayingBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
