package com.example.learnmediacodec

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btn_detect_codec_infos).setOnClickListener {
            val intent = Intent(this, DetectCodecInfosActivity::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.btn_decode_to_bitmap).setOnClickListener {
            val intent = Intent(this, DecodeUsingBuffersActivity::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.btn_decode_to_surface).setOnClickListener{
            val intent = Intent(this, DecodeUsingSurfaceActivity::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.btn_encode_bitmap).setOnClickListener{
            val intent = Intent(this, EncodeUsingBuffersActivity::class.java)
            startActivity(intent)
        }
    }
}