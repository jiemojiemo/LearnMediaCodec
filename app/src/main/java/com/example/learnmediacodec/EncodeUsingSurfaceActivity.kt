package com.example.learnmediacodec

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class EncodeUsingSurfaceActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_encode_using_surface)

        // render image to surface view
        val bitmap = BitmapFactory.decodeResource(resources, R.raw.test_img_795x1280)
        val surfaceView = findViewById<android.view.SurfaceView>(R.id.surface_view)
        val holder = surfaceView.holder
        holder.addCallback(object : android.view.SurfaceHolder.Callback {
            override fun surfaceCreated(holder: android.view.SurfaceHolder) {
                val canvas = holder.lockCanvas()
                canvas.drawBitmap(bitmap, 0f, 0f, null)
                holder.unlockCanvasAndPost(canvas)
            }

            override fun surfaceChanged(
                holder: android.view.SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {
            }

            override fun surfaceDestroyed(holder: android.view.SurfaceHolder) {
            }
        })

        val btnStartEncoding = findViewById<android.widget.Button>(R.id.btn_start_encoding_async)
        btnStartEncoding.setOnClickListener {
            Thread {
                encodeUsingSurfaceAsync()
            }.start()
        }
    }

    private fun encodeUsingSurfaceAsync() {

    }
}