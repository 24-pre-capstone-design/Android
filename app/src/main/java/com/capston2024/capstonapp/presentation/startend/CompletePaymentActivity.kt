package com.capston2024.capstonapp.presentation.startend

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.capston2024.capstonapp.R

class CompletePaymentActivity : Activity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_complete_payment)

        val imageView: ImageView = findViewById(R.id.randomImg)

        Handler(Looper.getMainLooper()).postDelayed({
            var intent = Intent(applicationContext, StartActivity::class.java)
            startActivity(intent)
            finish()
        }, 5000)

        StartActivity.updateImageRunnable = Runnable {
            StartActivity.fetchAndUpdateImage(imageView)
            StartActivity.imageHandler.postDelayed(
                StartActivity.updateImageRunnable,
                StartActivity.updateInterval
            )
        }

        StartActivity.imageHandler.post(StartActivity.updateImageRunnable)

    }

    override fun onDestroy() {
        super.onDestroy()
        StartActivity.imageHandler.removeCallbacks(StartActivity.updateImageRunnable)
    }
}
