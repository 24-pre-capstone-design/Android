package com.capston2024.capstonapp.presentation.startend

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import coil.load
import com.capston2024.capstonapp.BuildConfig
import com.capston2024.capstonapp.R
import com.capston2024.capstonapp.databinding.ActivityCompletePaymentBinding
import com.capston2024.capstonapp.extension.FoodState
import com.capston2024.capstonapp.presentation.aimode.AIViewModel
import kotlinx.coroutines.launch

class CompletePaymentActivity : AppCompatActivity() {
    private lateinit var binding:ActivityCompletePaymentBinding
    private val handler = Handler(Looper.getMainLooper())
    private var imageUpdateRunnable: Runnable? = null
    private val aiViewModel: AIViewModel by viewModels()
    private val startViewModel:StartViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_complete_payment)
        setting()
    }

    private fun setting(){
        binding=ActivityCompletePaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // 상태 바와 네비게이션 바 숨기기
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)

        setTime()
        getImage()
    }


    private fun setTime(){
        val timerTextView = binding.tvTimer
        var secondsRemaining=5

        val handler = Handler(Looper.getMainLooper())
        val runnable = object : Runnable {
            override fun run() {
                if (secondsRemaining > 0) {
                    timerTextView.text = secondsRemaining.toString()
                    secondsRemaining--
                    handler.postDelayed(this, 1000)
                } else {
                    val intent = Intent(applicationContext, StartActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                }
            }
        }
        handler.post(runnable)
    }

    private fun getImage(){
        aiViewModel.getData()
        lifecycleScope.launch {
            aiViewModel.allFoodState.collect{ foodState ->
                when(foodState){
                    is FoodState.Success ->{
                        var list:MutableList<String> = mutableListOf()
                        for(foodImg in foodState.foodList){
                            list.add(BuildConfig.BASE_URL+foodImg.pictureURL)
                        }
                        startViewModel.updateImages(list)
                        setImages()
                    }
                    is FoodState.Loading -> {}
                    is FoodState.Error -> {}
                }
            }
        }
    }

    private fun setImages() {
        startViewModel.foodImages.observe(this) { images ->
            if (images.isNotEmpty()) {
                Log.d("startactivity", "images success")
                startImageUpdateCycle(images)
            }
        }
    }

    private var activeImageView: ImageView? = null

    private fun applyAnimation(view: View, animationResource: Int, visibilityAfter: Int) {
        val animation = AnimationUtils.loadAnimation(this, animationResource)
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                view.visibility = visibilityAfter
            }

            override fun onAnimationRepeat(animation: Animation?) {}
        })
        view.startAnimation(animation)
    }

    private fun switchImage(currentImageView: ImageView, nextImage: String) {
        // Coil 라이브러리를 사용하여 이미지 로드
        currentImageView.load(nextImage) {
            crossfade(true) // 이미지 로딩 시 크로스페이드 효과 적용
            crossfade(500) // 크로스페이드 지속 시간 설정 (옵션)
        }
    }


    private fun startImageUpdateCycle(images: List<String>) {
        val handler = Handler(Looper.getMainLooper())
        val updateRunnable = object : Runnable {
            override fun run() {
                val randomIndex = (images.indices).random()
                val nextImage = images[randomIndex]
                val currentImageView = if (activeImageView == binding.ivRandom1) binding.ivRandom2 else binding.ivRandom1
                val outgoingImageView = if (activeImageView == binding.ivRandom1) binding.ivRandom1 else binding.ivRandom2

                // 현재 이미지 전환
                switchImage(currentImageView, nextImage)

                // 애니메이션 적용
                applyAnimation(currentImageView, R.anim.slide_in, View.VISIBLE)
                applyAnimation(outgoingImageView, R.anim.slide_out, View.GONE)

                activeImageView = currentImageView
                handler.postDelayed(this, 3000) // 3초마다 실행
            }
        }

        // 초기 활성 이미지 뷰 설정
        activeImageView = findViewById(R.id.iv_random1)
        handler.post(updateRunnable)
    }

    override fun onDestroy() {
        imageUpdateRunnable?.let { handler.removeCallbacks(it) } // 액티비티가 파괴될 때 콜백 제거
        super.onDestroy()
    }
}

