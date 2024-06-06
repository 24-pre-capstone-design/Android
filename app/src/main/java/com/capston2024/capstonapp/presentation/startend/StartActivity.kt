package com.capston2024.capstonapp.presentation.startend

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import coil.load
import com.capston2024.capstonapp.BuildConfig
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.capston2024.capstonapp.R
import com.capston2024.capstonapp.databinding.ActivityStartBinding
import com.capston2024.capstonapp.presentation.main.MainActivity
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.Locale
import kotlin.concurrent.thread
import kotlin.random.Random

class StartActivity : Activity() {

    private lateinit var binding: ActivityStartBinding
    private var textToSpeech: TextToSpeech? = null
    var isTTSReady = false // TTS 준비 상태 플래그
    private var handler: Handler? = null
    private var runnable: Runnable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        val imageView: ImageView = findViewById(R.id.randomImg)

        updateImageRunnable = Runnable {
            fetchAndUpdateImage(imageView)
            imageHandler.postDelayed(updateImageRunnable, updateInterval)
        }

        var newActivity = findViewById<LinearLayout>(R.id.startScreen)
        imageHandler.post(updateImageRunnable)

        newActivity.setOnClickListener() {
            var intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)

            fun setting() {
                /*binding = ActivityStartBinding.inflate(layoutInflater)
                setContentView(binding.root)
                binding.startScreen.setOnClickListener {
                    var intent = Intent(applicationContext, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }*/

                //권한 설정
                requestPermission()
                //tts 객체 초기화
                resetTTS()
            }


        }


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
                    Toast.makeText(
                        this,
                        "TTS 언어 데이터가 필요합니다. Google TTS 앱에서 데이터를 설치해주세요.",
                        Toast.LENGTH_LONG
                    ).show()
                    // 사용자를 Google TTS 앱 또는 설정 페이지로 안내할 수 있는 인텐트 실행
                    val installIntent = Intent()
                    installIntent.action = TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA
                    this?.startActivity(installIntent)
                } else {
                    isTTSReady = true // TTS가 준비되었음을 표시
                    textToSpeech?.setSpeechRate(5.0f) // TTS 속도 설정
                    speakInitialMessage() // 초기 메시지 음성 출력
                }
            } else {
                // TTS 초기화 실패 처리
                Log.e("aifragment", "Initialization failed")
                Toast.makeText(
                    this,
                    "TTS 초기화에 실패하였습니다. 앱 설정에서 TTS 엔진을 확인해주세요.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
    private fun startRepeatingTask() {
        handler = Handler(Looper.getMainLooper())
        runnable = object : Runnable {
            override fun run() {
                speakInitialMessage()
                handler?.postDelayed(this, 10000) // 10초 후에 다시 실행
            }
        }
        runnable?.let { handler?.post(it) }
    }

    private fun stopRepeatingTask() {
        handler?.removeCallbacks(runnable!!)
    }

    override fun onResume() {
        super.onResume()
        startRepeatingTask() // 액티비티/프래그먼트가 사용자에게 보일 때 TTS 시작
    }

    override fun onPause() {
        super.onPause()
        stopRepeatingTask() // 액티비티/프래그먼트가 사용자에게 보이지 않을 때 TTS 중단
    }

    private fun speakInitialMessage() {
        if (isTTSReady) {
            // 예제 메시지를 TTS로 말하기
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
        super.onDestroy()
        imageHandler.removeCallbacks(updateImageRunnable)
 }

    companion object {

        val updateInterval: Long = 2500 // 이미지 전환 간격
        val imageHandler = Handler(Looper.getMainLooper())
        lateinit var updateImageRunnable: Runnable

        fun fetchAndUpdateImage(imageView: ImageView) {
            fetchRandomImage { imageUrl ->
                loadImageFromUrl(imageUrl) { bitmap ->
                    imageView.post {
                        if (bitmap != null) {
                            imageView.setImageBitmap(bitmap)
                        } else {
                            Log.e("ImageUtils", "Failed to load image from URL: $imageUrl")
                        }
                    }
                }
            }
        }

        fun fetchRandomImage(callback: (String) -> Unit) {
            val client = OkHttpClient()

            val request = Request.Builder()
                .url(BuildConfig.BASE_URL + "/food")
                .build()

            thread {
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")

                    val responseData =
                        response.body?.string() ?: throw IOException("Response body is null")

                    val json = JSONObject(responseData)
                    val dataArray = json.getJSONArray("data")
                    val randomData = dataArray.getJSONObject(Random.nextInt(dataArray.length()))
                    val imageUrl = BuildConfig.BASE_URL + randomData.getString("pictureURL")
                    callback(imageUrl)
                }
            }
        }

        fun loadImageFromUrl(imageUrl: String, callback: (Bitmap?) -> Unit) {
            thread {
                try {
                    val url = URL(imageUrl)
                    val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
                    connection.doInput = true
                    connection.connect()
                    val input: InputStream = connection.inputStream
                    val bitmap = BitmapFactory.decodeStream(input)
                    callback(bitmap)
                } catch (e: Exception) {
                    e.printStackTrace()
                    callback(null)
                }
            }
        }
    }

}