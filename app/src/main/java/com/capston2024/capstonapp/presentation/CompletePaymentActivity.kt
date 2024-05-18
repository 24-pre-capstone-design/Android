package com.capston2024.capstonapp.presentation

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.capston2024.capstonapp.R

class CompletePaymentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_complete_payment)
        Handler(Looper.getMainLooper()).postDelayed({
            var intent = Intent(applicationContext, StartActivity::class.java)
            startActivity(intent)
        }, 5000)
    }
}
