package com.example.learnmediacodec

import android.graphics.*
import android.media.*
import android.media.MediaCodec.BUFFER_FLAG_END_OF_STREAM
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer


class DecodeUsingBuffersActivity : AppCompatActivity() {
    private val TAG = "DecodeUsingBuffersActivity"
    private lateinit var imageView : ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_decode_using_buffers)

        imageView = findViewById(R.id.imageview_decode_to_bitmap)
        val button = findViewById<android.widget.Button>(R.id.btn_start_decoding)
        button.setOnClickListener {
            Thread{
              decodeToBitmap()
            }.start()
        }
    }

    private fun decodeToBitmap(){
        // create and configure media extractor
        val mediaExtractor = MediaExtractor()
        resources.openRawResourceFd(R.raw.h264_720p).use {
            mediaExtractor.setDataSource(it)
        }
        val videoTrackIndex = 0
        mediaExtractor.selectTrack(videoTrackIndex)
        val videoFormat = mediaExtractor.getTrackFormat(videoTrackIndex)

        // create and configure media codec
        val codecList = MediaCodecList(MediaCodecList.REGULAR_CODECS)
        val codecName = codecList.findDecoderForFormat(videoFormat)
        val codec = MediaCodec.createByCodecName(codecName)
        // configure with null surface so that we can get decoded bitmap easily
        codec.configure(videoFormat, null, null, 0)

        // start decoding

        val maxInputSize = videoFormat.getInteger(MediaFormat.KEY_MAX_INPUT_SIZE)
        val inputBuffer = ByteBuffer.allocate(maxInputSize)
        val bufferInfo = MediaCodec.BufferInfo()
        val timeoutUs = 10000L // 10ms
        var inputDone = false
        var outputDone = false
        codec.start()

        while (!outputDone){
            val isInputBufferEnd = getInputBufferFromExtractor(mediaExtractor, inputBuffer, bufferInfo)
            if (isInputBufferEnd) {
                inputDone = true
            }

            // get codec input buffer and fill it with data from extractor
            val inputBufferId = codec.dequeueInputBuffer(-1L)
            if(inputBufferId >= 0){
                if(inputDone){
                    codec.queueInputBuffer(inputBufferId, 0, 0, 0, BUFFER_FLAG_END_OF_STREAM)
                }else{
                    val codecInputBuffer = codec.getInputBuffer(inputBufferId)
                    codecInputBuffer!!.put(inputBuffer)
                    codec.queueInputBuffer(inputBufferId, 0, bufferInfo.size, bufferInfo.presentationTimeUs, 0)
                }
            }

            val outputBufferId = codec.dequeueOutputBuffer(bufferInfo, timeoutUs)
            if(outputBufferId >= 0){
                if (bufferInfo.flags and BUFFER_FLAG_END_OF_STREAM != 0) {
                    outputDone = true
                }
                if(bufferInfo.size > 0){
                    val outputImage = codec.getOutputImage(outputBufferId)
                    val bitmap = yuvImage2Bitmap(outputImage!!)
                    imageView.post{
                        imageView.setImageBitmap(bitmap)
                    }
                    codec.releaseOutputBuffer(outputBufferId, false)
                    // sleep 30ms to simulate 30fps
                    Thread.sleep(30)
                }
            }

            mediaExtractor.advance()
        }


        mediaExtractor.release()
        codec.stop()
        codec.release()
    }

    private fun yuvImage2Bitmap(image: Image): Bitmap{
        val yBuffer = image.planes[0].buffer
        val uBuffer = image.planes[1].buffer
        val vBuffer = image.planes[2].buffer

        val ySize = yBuffer.remaining()
        val uSize = uBuffer.remaining()
        val vSize = vBuffer.remaining()

        val nv21 = ByteArray(ySize + uSize + vSize)
        yBuffer.get(nv21, 0, ySize)
        vBuffer.get(nv21, ySize, vSize)
        uBuffer.get(nv21, ySize + vSize, uSize)

        val yuvImage = YuvImage(nv21, ImageFormat.NV21, image.width, image.height, null)
        val out = ByteArrayOutputStream()
        yuvImage.compressToJpeg(Rect(0, 0, yuvImage.width, yuvImage.height), 100, out)
        val imageBytes = out.toByteArray()
        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        return bitmap
    }

    private fun imageToBitmap(image: Image): Bitmap {
        val buffer = image.planes[0].buffer
        val bytes = ByteArray(buffer.capacity())
        buffer.get(bytes)
        val yuvImage = YuvImage(bytes, image.format, image.width, image.height, null)

        val outputStream = ByteArrayOutputStream()
        yuvImage.compressToJpeg(Rect(0, 0, image.width, image.height), 100, outputStream)
        val jpegData = outputStream.toByteArray()
        val bitmap = BitmapFactory.decodeByteArray(jpegData, 0, jpegData.size)
        return bitmap
    }

    private fun getInputBufferFromExtractor(
        mediaExtractor: MediaExtractor,
        inputBuffer: ByteBuffer,
        bufferInfo: MediaCodec.BufferInfo
    ): Boolean {
        val sampleSize = mediaExtractor.readSampleData(inputBuffer, 0)
        if (sampleSize < 0) {
            return true
        }

        bufferInfo.size = sampleSize
        bufferInfo.presentationTimeUs = mediaExtractor.sampleTime
        bufferInfo.offset = 0
        bufferInfo.flags = mediaExtractor.sampleFlags

        return false
    }
}