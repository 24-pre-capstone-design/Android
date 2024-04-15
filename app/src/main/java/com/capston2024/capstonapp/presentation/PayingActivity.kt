package com.cookandroid.myapplication

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

class PayingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_paying)

        Handler(Looper.getMainLooper()).postDelayed({
            var intent = Intent(applicationContext, CompletePayment::class.java)
            startActivity(intent)
        }, 5000)
    }
}
