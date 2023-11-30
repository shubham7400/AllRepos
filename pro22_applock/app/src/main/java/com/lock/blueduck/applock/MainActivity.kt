package com.lock.blueduck.applock

import android.content.Intent
import android.content.res.Configuration
import android.hardware.biometrics.BiometricPrompt
import android.os.Build
import android.os.Bundle
import android.os.CancellationSignal
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.andrognito.patternlockview.PatternLockView
import com.andrognito.patternlockview.listener.PatternLockViewListener
import com.andrognito.patternlockview.utils.PatternLockUtils
import com.andrognito.pinlockview.PinLockListener
import com.lock.blueduck.applock.databinding.ActivityMainBinding
import com.lock.blueduck.applock.enum.AppLanguage
import com.lock.blueduck.applock.preferences.*
import java.util.*

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private lateinit var biometricPrompt: BiometricPrompt
    private var cancellationSignal: CancellationSignal? = null

    var fingerprintAuthAttemptCount = 0

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        changeAppLanguage()

        if (getPatternLockPath().isEmpty()){
            binding.clSetupView.visibility = View.VISIBLE
            binding.clAfterView.visibility = View.GONE

            binding.btnGetStarted.setOnClickListener { startActivity(Intent(this, AppListActivity::class.java)) }
        }else{
            binding.clSetupView.visibility = View.GONE
            binding.clAfterView.visibility = View.VISIBLE
            if (getTouchLockEnabledStatus()){
                setUpFingerprintAuthentication()
            }else{
                setUpPatternAuthentication()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun setUpFingerprintAuthentication() {
        biometricPrompt = BiometricPrompt.Builder(this)
            .setTitle(getString(R.string.unlock_with_biometric))
            .setNegativeButton(getString(R.string.cancel), this.mainExecutor) { dialog, which ->
                setUpPatternAuthentication()
            }.build()
        biometricPrompt.authenticate(getCancellationSignal(), mainExecutor, authenticationCallback)
    }

    private fun getCancellationSignal(): CancellationSignal {
        cancellationSignal = CancellationSignal()
        cancellationSignal?.setOnCancelListener {

        }
        return cancellationSignal as CancellationSignal
    }


    private fun changeAppLanguage() {
        val languageCode = getAppLanguage()
        val locale = if (languageCode.isNotEmpty()){
            Locale(languageCode)
        }else{
            Locale(AppLanguage.ENGLISH.language)
        }

        Locale.setDefault(locale)

        val config = Configuration()
        config.locale = locale

        baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)

    }

    private val authenticationCallback = @RequiresApi(Build.VERSION_CODES.P) object : BiometricPrompt.AuthenticationCallback() {
        override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
            super.onAuthenticationError(errorCode, errString)
            setUpPatternAuthentication()
        }

        override fun onAuthenticationFailed() {
            super.onAuthenticationFailed()
            if (fingerprintAuthAttemptCount > 3){
                setUpPatternAuthentication()
            }else{
                fingerprintAuthAttemptCount++
            }
        }

        override fun onAuthenticationHelp(helpCode: Int, helpString: CharSequence?) {
            super.onAuthenticationHelp(helpCode, helpString)
        }

        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult?) {
            super.onAuthenticationSucceeded(result)
            startActivity(Intent(this@MainActivity, AppListActivity::class.java))
        }
    }

    private fun setUpPatternAuthentication() {
        binding.clFingerprintView.visibility = View.GONE
        binding.clPatternLockView.visibility = View.VISIBLE
        if (isAuthTypePattern()){
            binding.tvTitle.text = getString(R.string.draw_your_pattern_to_unlock)
            binding.patternLockView.visibility = View.VISIBLE
            binding.clPasscode.visibility = View.GONE
            binding.patternLockView.addPatternLockListener(mPatternLockViewListener)
        }else{
            binding.tvTitle.text = getString(R.string.enter_pin_to_unlock)
            binding.patternLockView.visibility = View.GONE
            binding.clPasscode.visibility = View.VISIBLE
            binding.pinLockView.attachIndicatorDots(binding.indicatorDots)
            binding.pinLockView.setPinLockListener(mPinLockListener)
        }
    }


    private val mPatternLockViewListener: PatternLockViewListener = object : PatternLockViewListener {
        override fun onStarted() {
            Log.d(javaClass.name, "Pattern drawing started")
            binding.tvPatternValidateText.visibility = View.GONE
        }

        override fun onProgress(progressPattern: List<PatternLockView.Dot>) {
            Log.d(javaClass.name, "Pattern progress: " + PatternLockUtils.patternToString(binding.patternLockView, progressPattern))
        }

        override fun onComplete(pattern: List<PatternLockView.Dot>) {
            Log.d(javaClass.name, "Pattern complete: " + PatternLockUtils.patternToString(binding.patternLockView, pattern))
            val patternPath = PatternLockUtils.patternToString(binding.patternLockView, pattern)
            if (patternPath == getPatternLockPath()){
                startActivity(Intent(this@MainActivity, AppListActivity::class.java))
                finishAffinity()
            }else{
                binding.tvPatternValidateText.visibility = View.VISIBLE
            }
            binding.patternLockView.clearPattern()
        }

        override fun onCleared() {
            Log.d(javaClass.name, "Pattern has been cleared")
        }
    }

    private val mPinLockListener: PinLockListener = object : PinLockListener {
        override fun onComplete(pin: String) {
            if (pin == getPatternLockPath()){
                startActivity(Intent(this@MainActivity, AppListActivity::class.java))
                finishAffinity()
            }else{
                binding.tvPatternValidateText.visibility = View.VISIBLE
            }
            binding.pinLockView.resetPinLockView()
        }

        override fun onEmpty() {

        }

        override fun onPinChange(pinLength: Int, intermediatePin: String) {
            binding.tvPatternValidateText.visibility = View.GONE
        }
    }


}