package com.cookandroid.myapplication

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

class CompletePayment : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_complete_payment)

        Handler(Looper.getMainLooper()).postDelayed({
            var intent = Intent(applicationContext, StartingActivity::class.java)
            startActivity(intent)
        }, 5000)
    }
}
