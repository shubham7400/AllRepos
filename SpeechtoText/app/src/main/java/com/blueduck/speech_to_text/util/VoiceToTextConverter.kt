package com.blueduck.speech_to_text.util

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object VoiceToTextConverter {
    // Declare a constant for the RECORD_AUDIO permission request code
    private const val RECORD_AUDIO_REQUEST_CODE = 1

    // Initialize the SpeechRecognizer
    private var speechRecognizer: SpeechRecognizer? = null

    fun convertVoiceToText(activity: AppCompatActivity) {
        // Check if the RECORD_AUDIO permission is granted
        val permission = android.Manifest.permission.RECORD_AUDIO
        val permissionGranted = PackageManager.PERMISSION_GRANTED
        val hasPermission =
            ContextCompat.checkSelfPermission(activity, permission) == permissionGranted

        if (!hasPermission) {
            // Request the RECORD_AUDIO permission if not granted
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(permission),
                RECORD_AUDIO_REQUEST_CODE
            )
            return
        }

        // Initialize the SpeechRecognizer if not already initialized
        if (speechRecognizer == null) {
            // Create a SpeechRecognizer instance
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(activity)

            // Set the RecognitionListener to receive speech recognition events
            val listener = object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {
                    // Called when the speech recognition service is ready to receive audio
                }

                override fun onBeginningOfSpeech() {
                    // Called when the user starts speaking
                }

                override fun onRmsChanged(rmsdB: Float) {
                    // Called when the RMS (root mean square) dB value of the incoming audio changes
                }

                override fun onBufferReceived(p0: ByteArray?) {

                }

                override fun onPartialResults(partialResults: Bundle?) {
                    // Called when partial recognition results are available
                }

                override fun onResults(results: Bundle?) {
                    // Called when final recognition results are available
                    val textResults =
                        results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    if (!textResults.isNullOrEmpty()) {
                        val convertedText = textResults[0] // Get the first recognized text
                        // Do something with the converted text
                        Toast.makeText(
                            activity,
                            "Converted text: $convertedText",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onEndOfSpeech() {
                    // Called when the user stops speaking
                }

                override fun onError(error: Int) {
                    // Called when an error occurs during speech recognition
                    Toast.makeText(
                        activity,
                        "Speech recognition error",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onEvent(eventType: Int, params: Bundle?) {
                    // Called when events related to the recognition service occur
                }
            }

            // Set the RecognitionListener to the SpeechRecognizer
            speechRecognizer?.setRecognitionListener(listener)

        }

        // Create an intent for speech recognition
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, KOREAN_LANGUAGE)

        // Start listening for speech
        speechRecognizer?.startListening(intent)
    }


    fun releaseResources() {
        speechRecognizer?.stopListening()
        speechRecognizer?.destroy()
        speechRecognizer = null
    }

}
