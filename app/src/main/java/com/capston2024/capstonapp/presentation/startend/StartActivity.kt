package com.capston2024.capstonapp.presentation.startend

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
<<<<<<< HEAD
<<<<<<< HEAD:app/src/main/java/com/capston2024/capstonapp/presentation/StartActivity.kt
import android.widget.ImageView
=======
import android.widget.ImageView
import com.capston2024.capstonapp.databinding.ActivityStartBinding
>>>>>>> d57da330fedace9faa94eb07aeeba6496cf631f6
import android.widget.LinearLayout
=======
import com.capston2024.capstonapp.databinding.ActivityStartBinding
import androidx.appcompat.app.AppCompatActivity
>>>>>>> b0fc477b21bf69f7158df35c3b1e4542384594bd:app/src/main/java/com/capston2024/capstonapp/presentation/startend/StartActivity.kt
import com.capston2024.capstonapp.R
import com.capston2024.capstonapp.presentation.main.MainActivity
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread
import kotlin.random.Random

<<<<<<< HEAD
class StartActivity : AppCompatActivity() {
    private lateinit var binding:ActivityStartBinding
=======
class StartActivity : Activity() {
    private lateinit var binding:ActivityStartBinding

>>>>>>> d57da330fedace9faa94eb07aeeba6496cf631f6
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        setting()
    }

<<<<<<< HEAD
        binding= ActivityStartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.startScreen.setOnClickListener() {
            var intent = Intent(applicationContext, MainActivity :: class.java)
            startActivity(intent)
=======
    private fun setting(){
        binding = ActivityStartBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.startScreen.setOnClickListener{
            var intent = Intent(applicationContext, MainActivity :: class.java)
            startActivity(intent)
            finish()
        }

        val imageView: ImageView = findViewById(R.id.randomImg)
        fetchRandomImage { imageUrl ->
            loadImageFromUrl(imageUrl) { bitmap ->
                runOnUiThread {
                    imageView.setImageBitmap(bitmap)
                }
            }
>>>>>>> d57da330fedace9faa94eb07aeeba6496cf631f6
        }

        val imageView: ImageView = findViewById(R.id.randomImg)

        val url = "http://15.164.96.227:3000"
        /*
        fetchRandomImage { imageUrl ->
            loadImageFromUrl(imageUrl) { bitmap ->
                runOnUiThread {
                    imageView.setImageBitmap(bitmap)
                }
            }
        }*/
    }

    companion object {
<<<<<<< HEAD
        /*
        fun fetchRandomImage(callback: (String) -> Unit) {
            val client = OkHttpClient()
=======
        fun fetchRandomImage(callback: (String) -> Unit) {
            val client = OkHttpClient()

            val request = Request.Builder()
                .url("https://reqres.in/api/users")
                .build()

            thread {
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")

                    val responseData = response.body?.string() ?: throw IOException("Response body is null")

                    val json = JSONObject(responseData)
                    val dataArray = json.getJSONArray("data")
                    val randomData = dataArray.getJSONObject(Random.nextInt(dataArray.length()))
                    val imageUrl = randomData.getString("avatar")

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
>>>>>>> d57da330fedace9faa94eb07aeeba6496cf631f6

            val request = Request.Builder()
                //.url("https://reqres.in/api/users")
                .build()

            thread {
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")

                    val responseData = response.body?.string() ?: throw IOException("Response body is null")

                    val json = JSONObject(responseData)
                    val dataArray = json.getJSONArray("data")
                    val randomData = dataArray.getJSONObject(Random.nextInt(dataArray.length()))
                    val imageUrl = randomData.getString("avatar")

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
        }*/
    }
}