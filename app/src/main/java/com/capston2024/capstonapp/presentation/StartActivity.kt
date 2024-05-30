package com.capston2024.capstonapp.presentation

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
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

class StartActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_start)

        var newActivity = findViewById<LinearLayout>(R.id.startScreen)
        newActivity.setOnClickListener() {
            var intent = Intent(applicationContext, MainActivity :: class.java)
            startActivity(intent)
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
        /*
        fun fetchRandomImage(callback: (String) -> Unit) {
            val client = OkHttpClient()

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