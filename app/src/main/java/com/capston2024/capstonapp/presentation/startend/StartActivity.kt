package com.capston2024.capstonapp.presentation.startend

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.Toast
import com.capston2024.capstonapp.databinding.ActivityStartBinding
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import coil.load
import com.capston2024.capstonapp.BuildConfig
import com.capston2024.capstonapp.R
import com.capston2024.capstonapp.extension.FoodState
import com.capston2024.capstonapp.presentation.aimode.AIViewModel
import com.capston2024.capstonapp.presentation.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Locale

@AndroidEntryPoint
class StartActivity : AppCompatActivity() {
    private lateinit var binding:ActivityStartBinding
    private val aiViewModel: AIViewModel by viewModels()
    private val startViewModel:StartViewModel by viewModels()
   // private var handler = Handler(Looper.getMainLooper())
    private var imageUpdateRunnable: Runnable? = null

    private lateinit var handler: Handler
    private lateinit var runnable: Runnable
    private var textToSpeech: TextToSpeech? = null
    private var isTTSReady = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        setting()
        setImages()
    }

    private fun setting(){
        // 상태 바와 네비게이션 바 숨기기
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)

        requestPermission()
        resetTTS()

        binding = ActivityStartBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.startScreen.setOnClickListener{
            var intent = Intent(applicationContext, MainActivity :: class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        getImages()
    }

    private fun getImages(){
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
                    }
                    is FoodState.Loading -> {}
                    is FoodState.Error -> {}
                }
            }
        }
    }

    //
    private fun setImages() {
        startViewModel.foodImages.observe(this) { images ->
            if (images.isNotEmpty()) {
                Log.d("startactivity", "images success")
                startImageUpdateCycle(images)
            }
        }
    }

    private var activeImageView: ImageView? = null


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

    private fun switchImage(currentImageView: ImageView, nextImage: String) {
        // Coil 라이브러리를 사용하여 이미지 로드
        currentImageView.load(nextImage) {
            crossfade(true) // 이미지 로딩 시 크로스페이드 효과 적용
            crossfade(100) // 크로스페이드 지속 시간 설정 (옵션)
        }
    }

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

    private fun requestPermission() {
        // 버전 체크, 권한 허용했는지 체크
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO), 0
            )
        }
    }

    private fun resetTTS() {
        // TTS 객체 초기화
        textToSpeech = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = textToSpeech?.setLanguage(Locale.KOREAN)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    // 언어 데이터가 없거나 지원하지 않는 언어일 때 처리
                    Log.e("aifragment", "Language is not supported")
                    Toast.makeText(this, "TTS 언어 데이터가 필요합니다. Google TTS 앱에서 데이터를 설치해주세요.", Toast.LENGTH_LONG).show()
                    // 사용자를 Google TTS 앱 또는 설정 페이지로 안내할 수 있는 인텐트 실행
                    val installIntent = Intent()
                    installIntent.action = TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA
                    startActivity(installIntent)
                } else {
                    isTTSReady = true // TTS가 준비되었음을 표시
                    textToSpeech?.setSpeechRate(1.7f) // TTS 속도 설정
                    startRepeatingTask() // 초기 메시지 음성 출력 시작
                }
            } else {
                // TTS 초기화 실패 처리
                Log.e("aifragment", "Initialization failed")
                Toast.makeText(this, "TTS 초기화에 실패하였습니다. 앱 설정에서 TTS 엔진을 확인해주세요.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun startRepeatingTask() {
        handler = Handler(Looper.getMainLooper())
        runnable = object : Runnable {
            override fun run() {
                speakInitialMessage()
                handler.postDelayed(this, 10000) // 5초 후에 다시 실행
            }
        }
        handler.post(runnable)
    }

    private fun stopRepeatingTask() {
        handler.removeCallbacks(runnable)
    }

    private fun speakInitialMessage() {
        if (isTTSReady) { // TTS가 준비되었고, 액티비티가 보일 때만 실행
            // 설명 메시지를 TTS로 말하기
            textToSpeech?.speak(
                getString(R.string.ai_explain),
                TextToSpeech.QUEUE_FLUSH,
                null,
                ""
            )
            Log.d("aifragment", "tts is ready")
        }
    }


    override fun onDestroy() {
        imageUpdateRunnable?.let { handler.removeCallbacks(it) }
        super.onDestroy()
        stopRepeatingTask()
        textToSpeech?.stop()
        textToSpeech?.shutdown()
    }
}