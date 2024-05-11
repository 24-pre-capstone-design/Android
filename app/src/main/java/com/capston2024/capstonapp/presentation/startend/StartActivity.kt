package com.capston2024.capstonapp.presentation.startend

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.capston2024.capstonapp.data.FragmentType
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
            var intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("mode", FragmentType.AI_MODE.name) // ai mode일 때. string으로 변환하여 넘겨줌
            startActivity(intent)
            finish()
        }
        binding.btnBasic.setOnClickListener {
            var intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("mode", FragmentType.BASIC_MODE.name) // basic 일 때
            startActivity(intent)
            finish()
        }
    }

}
