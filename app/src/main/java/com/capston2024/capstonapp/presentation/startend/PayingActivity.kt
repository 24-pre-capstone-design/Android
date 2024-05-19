package com.capston2024.capstonapp.presentation.startend


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.capston2024.capstonapp.R
import com.capston2024.capstonapp.databinding.ActivityPayingBinding


class PayingActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_paying)
        Handler(Looper.getMainLooper()).postDelayed({
            var intent = Intent(applicationContext, CompletePaymentActivity::class.java)
            startActivity(intent)
        }, 5000)
    }
}
