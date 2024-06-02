package com.capston2024.capstonapp.presentation.startend

import android.animation.ObjectAnimator
import android.app.Activity
import android.app.Dialog

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper

import android.widget.Button
import android.widget.ProgressBar

import androidx.appcompat.app.AppCompatActivity
import com.capston2024.capstonapp.R
import com.capston2024.capstonapp.databinding.ActivityPayingBinding



class PayingActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_paying)

        val payingBtn: Button = findViewById(R.id.payingBtn)
        payingBtn.setOnClickListener {
            showPopup()
        }
    }

    private fun showPopup() {
        val payingDL = Dialog(this)
        payingDL.setContentView(R.layout.popup_paying)
        payingDL.setCancelable(false)
        payingDL.show()

        val payingPB: ProgressBar = payingDL.findViewById(R.id.payingProgressBar)
        animateProgressBar(payingPB)

        Handler(Looper.getMainLooper()).postDelayed({
            payingDL.dismiss()
            goToNextScreen()
        }, 5000)
    }

    private fun animateProgressBar(progressBar: ProgressBar) {
        val animator = ObjectAnimator.ofInt(progressBar, "progress", 0, 100)
        animator.duration = 4700
        animator.start()
    }

    private fun goToNextScreen() {
        val intent = Intent(this, CompletePaymentActivity::class.java)
        startActivity(intent)
        finish()
    }
}
