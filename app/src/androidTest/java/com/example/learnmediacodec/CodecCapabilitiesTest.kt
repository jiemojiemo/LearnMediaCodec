package com.example.learnmediacodec

import android.media.MediaCodecInfo
import android.media.MediaCodecList
import android.media.MediaFormat
import android.os.Environment
import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.gson.Gson
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.lang.reflect.Field


@RunWith(AndroidJUnit4::class)
class CodecCapabilitiesTest {
    private val TAG = "CodecCapabilitiesTest"
    private val mRegularCodes = MediaCodecList(MediaCodecList.REGULAR_CODECS)
    private val mCodecInfos = mRegularCodes.codecInfos

    private val mAudioMime = "audio/mp4a-latm"
    private val mAudioFormat = MediaFormat.createAudioFormat(mAudioMime, 44100, 2)
    private val mAudioDecoderCodecName = mRegularCodes.findDecoderForFormat(mAudioFormat)
    private val mAudioEncoderCodecName = mRegularCodes.findEncoderForFormat(mAudioFormat)
    private val mAudioDecoderInfo = mCodecInfos.find { it.name == mAudioDecoderCodecName }
    private val mAudioEncoderInfo = mCodecInfos.find { it.name == mAudioEncoderCodecName }

    private val mAVCMime = "video/avc"
    private val mAVCFormat = MediaFormat.createVideoFormat(mAVCMime, 1280, 720)
    private val mAVCDecoderCodecName = mRegularCodes.findDecoderForFormat(mAVCFormat)
    private val mAVCEncoderCodecName = mRegularCodes.findEncoderForFormat(mAVCFormat)

    private val mHEVCFormat = MediaFormat.createVideoFormat("video/hevc", 1280, 720)
    private val mHEVCDecoderCodecName = mRegularCodes.findDecoderForFormat(mHEVCFormat)
    private val mHEVCEncoderCodecName = mRegularCodes.findEncoderForFormat(mHEVCFormat)

    private val mAVCDecoderInfo = mCodecInfos.find { it.name == mAVCDecoderCodecName }
    private val mAVCEncoderInfo = mCodecInfos.find { it.name == mAVCEncoderCodecName }

    private val mHEVCDecoderInfo = mCodecInfos.find { it.name == mHEVCDecoderCodecName }
    private val mHEVCEncoderInfo = mCodecInfos.find { it.name == mHEVCEncoderCodecName }

    private val codecProfileLevelFields: Array<Field> =
        MediaCodecInfo.CodecProfileLevel::class.java.fields
    private val capabilitiesFields: Array<Field> =
        MediaCodecInfo.CodecCapabilities::class.java.fields


    @Test
    fun getCapabilitiesFromCodecInfo() {
        val decoderCal = mAVCDecoderInfo?.getCapabilitiesForType(mAVCMime)
        val encoderCal = mAVCEncoderInfo?.getCapabilitiesForType(mAVCMime)

        assertNotNull(decoderCal)
        assertNotNull(encoderCal)
    }

    @Test
    fun canGetMimeType() {
        val decoderCal = mAVCDecoderInfo?.getCapabilitiesForType(mAVCMime)

        assertEquals(mAVCMime, decoderCal?.mimeType)
    }

    @Test
    fun canGetColorFormats() {
        val decoderCal = mAVCDecoderInfo?.getCapabilitiesForType(mAVCMime)
        val encoderCal = mAVCEncoderInfo?.getCapabilitiesForType(mAVCMime)

        val decoderColorFormats = decoderCal?.colorFormats
        val encoderColorFormats = encoderCal?.colorFormats

        assertNotNull(decoderColorFormats)
        assertNotNull(encoderColorFormats)
    }

    @Test
    fun canCreateFromProfileAndLevel(){
        val profile = MediaCodecInfo.CodecProfileLevel.AVCProfileBaseline
        val level = MediaCodecInfo.CodecProfileLevel.AVCLevel31
        val cap = MediaCodecInfo.CodecCapabilities.createFromProfileLevel("video/avc", profile, level)

        assertNotNull(cap)
    }

    @Test
    fun createFailedIfProfileAndLevelNotUnderstoodByFramework(){
        val profile = 100
        val level = MediaCodecInfo.CodecProfileLevel.H263Level70
        val cap = MediaCodecInfo.CodecCapabilities.createFromProfileLevel("video/avc", profile, level)

        assertNull(cap)
    }

    @Test
    fun canCheckIsFeatureSupportedOrNot(){
        val decoderCal = mAVCDecoderInfo?.getCapabilitiesForType(mAVCMime)

        assertTrue(decoderCal?.isFeatureSupported(MediaCodecInfo.CodecCapabilities.FEATURE_AdaptivePlayback)!!)
        assertFalse(decoderCal.isFeatureSupported(MediaCodecInfo.CodecCapabilities.FEATURE_SecurePlayback))
    }

    @Test
    fun canGetFeaturesFromDefaultFormat(){
        val decoderCal = mAVCDecoderInfo?.getCapabilitiesForType(mAVCMime)
        val defaultFormat = decoderCal?.defaultFormat

        for(f in defaultFormat?.features!!){
            assertTrue(decoderCal.isFeatureSupported(f))
        }
    }

    @Test
    fun canGetVideoCapabilities(){
        val decoderCal = mAVCDecoderInfo?.getCapabilitiesForType(mAVCMime)

        assertNotNull(decoderCal?.videoCapabilities)
    }

    @Test
    fun getVideoCapabilitiesFailedIfNotVideoCodec(){
        val decoderCal = mAudioDecoderInfo?.getCapabilitiesForType(mAudioMime)

        assertNull(decoderCal?.videoCapabilities)
    }

    @Test
    fun canGetAudioCapabilities(){
        val decoderCal = mAudioDecoderInfo?.getCapabilitiesForType(mAudioMime)

        assertNotNull(decoderCal?.audioCapabilities)
    }

    @Test
    fun getAudioCapabilitiesFailedIfNotAudioCodec(){
        val decoderCal = mAVCDecoderInfo?.getCapabilitiesForType(mAVCMime)

        assertNull(decoderCal?.audioCapabilities)
    }

    @Test
    fun canGetEncoderCapabilities(){
        val encoderCal = mAVCEncoderInfo?.getCapabilitiesForType(mAVCMime)

        assertNotNull(encoderCal?.encoderCapabilities)
    }

    @Test
    fun getEncoderCapabilitiesFailedIfNotEncoder(){
        val decoderCal = mAVCDecoderInfo?.getCapabilitiesForType(mAVCMime)

        assertNull(decoderCal?.encoderCapabilities)
    }

    @Test
    fun canCheckIsFormatSupportedOrNot(){
        val decoderCal = mAVCDecoderInfo?.getCapabilitiesForType(mAVCMime)
        val encoderCal = mAVCEncoderInfo?.getCapabilitiesForType(mAVCMime)

        assertTrue(decoderCal?.isFormatSupported(mAVCFormat)!!)
        assertTrue(encoderCal?.isFormatSupported(mAVCFormat)!!)
    }

    private fun buildProfileLevelsString(profileLevels: Array<MediaCodecInfo.CodecProfileLevel>): String {
        val sb = StringBuilder()
        sb.append("supported profile levels:\n")
        for (profileLevel in profileLevels) {
            val profileHex = String.format("0x%X", profileLevel.profile)
            val levelHex = String.format("0x%X", profileLevel.level)

            sb.append("profile: ${profileHex}, level: ${levelHex}\n")
        }

        return sb.toString()
    }

    private fun buildColorFormatsString(colorFormats: IntArray): String {
        val sb = StringBuilder()
        sb.append("supported color formats:")
        for (colorFormat in colorFormats) {
            for (field in capabilitiesFields) {
                if (field.type == Int::class.java && field.getInt(null) == colorFormat) {
                    sb.append("${field.name},")
                    break
                }
            }
        }

        sb.append("\n")
        return sb.toString()
    }

    private fun buildVideoCapabilitiesString(vc: MediaCodecInfo.VideoCapabilities?): String {
        val sb = StringBuilder()
        sb.append("video capabilities:\n")
        if(vc == null){
            return sb.toString()
        }

        sb.append("supported width range: ${vc.supportedWidths}\n")
        sb.append("supported height range: ${vc.supportedHeights}\n")
        sb.append("supported frame rate range: ${vc.supportedFrameRates}\n")
        sb.append("supported bit rate range: ${vc.bitrateRange}\n")
        return sb.toString()
    }

    private fun buildAudioCapabilitiesString(ac: MediaCodecInfo.AudioCapabilities?): String {
        val sb = StringBuilder()
        sb.append("audio capabilities:\n")
        if(ac == null){
            return sb.toString()
        }

        sb.append("supported sample rates: ${ac.supportedSampleRates}\n")
        sb.append("supported channel rate ranges: ${ac.supportedSampleRateRanges}\n")
        sb.append("supported input channel ranges: ${ac.inputChannelCountRanges}\n")
        sb.append("supported min input channel: ${ac.minInputChannelCount}\n")
        sb.append("supported max input channel: ${ac.maxInputChannelCount}\n")
        sb.append("supported bit rate range: ${ac.bitrateRange}\n")
        return sb.toString()
    }

    private fun buildEncoderCapabilitiesString(ec: MediaCodecInfo.EncoderCapabilities?): String {
        val sb = StringBuilder()
        sb.append("encoder capabilities:\n")
        if(ec == null){
            return sb.toString()
        }

        sb.append("supported complexity range: ${ec.complexityRange}\n")
        sb.append("supported quality range: ${ec.qualityRange}\n")
        return sb.toString()
    }

    private fun buildCapabilitiesString(cal: MediaCodecInfo.CodecCapabilities): String {
        return MediaClassJsonUtils.toJson(cal).toString()
    }




    @Test
    fun printAllCapabilities() {
        val str = MediaClassJsonUtils.toJsonArray(mCodecInfos).toString()
        // save str to cache file
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val cacheDir = context.cacheDir
        val cacheFile = File(cacheDir, "capabilities.json")
        Log.d(TAG, "printAllCapabilities: ${cacheFile.absolutePath}")
        cacheFile.writeText(str)
    }
}