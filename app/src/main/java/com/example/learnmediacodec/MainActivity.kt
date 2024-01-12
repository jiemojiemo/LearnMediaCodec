package com.example.learnmediacodec

import android.media.MediaCodecList
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import java.io.File

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btn = findViewById<Button>(R.id.btn)
        btn.setOnClickListener {
            val RegularCodes = MediaCodecList(MediaCodecList.REGULAR_CODECS)
            val CodecInfos = RegularCodes.codecInfos
            val codecJsonStr = MediaClassJsonUtils.toJsonArray(CodecInfos).toString()
            val cacheFile = File(externalCacheDir, "codec_infos.json")
            Log.d(TAG, "codec info json file: ${cacheFile.absolutePath}")
            cacheFile.writeText(codecJsonStr)
        }
    }
}