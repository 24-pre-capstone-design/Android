package com.capston2024.capstonapp.extension

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.ImageView
import com.capston2024.capstonapp.BuildConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.URL
import java.util.concurrent.Executors

object ImageLoader {
    fun ImageView.loadImage(url: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val image = BitmapFactory.decodeStream(URL(url).openStream())
                CoroutineScope(Dispatchers.Main).launch {
                    this@loadImage.setImageBitmap(image)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}