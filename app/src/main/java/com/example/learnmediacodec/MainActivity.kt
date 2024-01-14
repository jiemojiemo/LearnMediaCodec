package com.example.learnmediacodec

import android.content.Intent
import android.media.MediaCodecList
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import java.io.File

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btn = findViewById<Button>(R.id.detect_codec_infos)
        btn.setOnClickListener {
            val intent = Intent(this, DetectCodecInfosActivity::class.java)
            startActivity(intent)
        }
    }
}