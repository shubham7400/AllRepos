package com.blueduck.speech_to_text

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.SpeechRecognizer
import android.widget.Toast
import com.blueduck.speech_to_text.databinding.ActivityMainBinding
import com.blueduck.speech_to_text.util.PdfCreator
import com.blueduck.speech_to_text.util.VoiceToTextConverter


// Declare a constant for the RECORD_AUDIO permission request code
private const val RECORD_AUDIO_REQUEST_CODE = 1


class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.btnSpeechToText.setOnClickListener {
            VoiceToTextConverter.convertVoiceToText(this)
        }
        binding.btnConvertToPdf.setOnClickListener {
            val pdfCreator = PdfCreator(this)
            pdfCreator.createPdf()
        }
    }


    override fun onStop() {
        super.onStop()
        VoiceToTextConverter.releaseResources()
    }



    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            RECORD_AUDIO_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, call the voice-to-text conversion function
                    VoiceToTextConverter.convertVoiceToText(this)
                } else {
                    // Permission denied, show a message or handle it accordingly
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}