package com.lock.blueduck.applock.services

import android.app.*
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.*
import android.content.pm.PackageManager
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import android.hardware.biometrics.BiometricPrompt
import android.os.*
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.NotificationCompat
import com.andrognito.patternlockview.PatternLockView
import com.andrognito.patternlockview.listener.PatternLockViewListener
import com.andrognito.patternlockview.utils.PatternLockUtils
import com.andrognito.pinlockview.IndicatorDots
import com.andrognito.pinlockview.PinLockListener
import com.andrognito.pinlockview.PinLockView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.lock.blueduck.applock.AppListActivity
import com.lock.blueduck.applock.R
import com.lock.blueduck.applock.preferences.*
import dagger.hilt.android.AndroidEntryPoint
import java.util.*


@AndroidEntryPoint
class AppLockWindowService : Service()  {

    var lastOpenedAppPackageName = ""

    private lateinit var mWindowManager: WindowManager
    private lateinit var lockView: View
    lateinit var patternLockView: PatternLockView
    lateinit var pinLockView: PinLockView
    private lateinit var indicatorDots: IndicatorDots
    private lateinit var passcodeView: ConstraintLayout
    private lateinit var fingerprintView: ConstraintLayout
    lateinit var appName: TextView
    lateinit var appIcon: ImageView

    private  var biometricPrompt: BiometricPrompt? = null

    // create a CancellationSignal variable and assign a value null to it
    private var cancellationSignal: CancellationSignal? = null

    private var packages = arrayListOf<String>()

    private val layoutParams = WindowManager.LayoutParams(
        WindowManager.LayoutParams.MATCH_PARENT,
        WindowManager.LayoutParams.MATCH_PARENT,
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        else
            WindowManager.LayoutParams.TYPE_PHONE,
        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
        PixelFormat.TRANSLUCENT
    )

    override fun onBind(intent: Intent?): IBinder? {
        // This service is not designed to be bound, so return null
        return null
    }

       @RequiresApi(Build.VERSION_CODES.P)
       override fun onCreate() {
        super.onCreate()




        startForegroundService()

        // Start the loop
        handler.postDelayed(runnable, delay)
    }


    private fun getCancellationSignal(): CancellationSignal {
        cancellationSignal = CancellationSignal()
        cancellationSignal?.setOnCancelListener {

        }
        return cancellationSignal as CancellationSignal
    }

    val handler = Handler()
    val delay: Long = 1000 // 2 seconds in milliseconds

    private val runnable = object : Runnable {
          @RequiresApi(Build.VERSION_CODES.Q)
          override fun run() {
              getAppUsageStatistics()
            handler.postDelayed(this, delay)
        }
    }



       @RequiresApi(Build.VERSION_CODES.P)
       private fun getAppUsageStatistics() {

        packages = getLockedAppsPackages()

        val usageStatsManager = getSystemService(USAGE_STATS_SERVICE) as UsageStatsManager

        // Get the start and end time of the query
        val endTime = System.currentTimeMillis()
        val startTime = endTime - 3000  // 1 minute

        // Query the usage stats for the given app package name
        val usageEvents = usageStatsManager.queryEvents(startTime, endTime)
           val event = UsageEvents.Event()
        while (usageEvents.hasNextEvent()) {
            usageEvents.getNextEvent(event)
            if (packages.contains(event.packageName)) {
                if (event.eventType == UsageEvents.Event.ACTIVITY_RESUMED) {
                    val lastOpenTime = event.timeStamp
                    // Do something with the last open time
                    if (getOpenedAppPackage() != event.packageName){
                        if (!lockView.isShown){
                            if ((System.currentTimeMillis() - lastOpenTime) < 2000){
                                lastOpenedAppPackageName = event.packageName
                                val nameAndIcon = getAppNameAndIcon(event.packageName)
                                appName.text = nameAndIcon.first
                                appIcon.setImageDrawable(nameAndIcon.second)
                                mWindowManager.addView(lockView, layoutParams)
                                if (getTouchLockEnabledStatus()){
                                    initializeBiometricCallback()
                                }
                            }
                        }
                    }
                 } else if (event.eventType == UsageEvents.Event.ACTIVITY_PAUSED) {
                    val lastCloseTime = event.timeStamp
                    // Do something with the last close time
                    if ((System.currentTimeMillis() - lastCloseTime) < 2000){
                        try {
                            mWindowManager.removeView(lockView)
                        }catch (e: java.lang.Exception){
                            println("errorrr ${e.message}")
                        }
                    }
                }
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.P)
    private fun initializeBiometricCallback() {
        patternLockView.visibility = View.GONE
        passcodeView.visibility = View.GONE
        fingerprintView.visibility = View.VISIBLE

        cancellationSignal?.cancel()
        cancellationSignal = null
        biometricPrompt = null
        // This creates a dialog of biometric auth and
        // it requires title , subtitle ,
        // and description
        // In our case there is a cancel button by
        // clicking it, it will cancel the process of
        // fingerprint authentication
        biometricPrompt = BiometricPrompt.Builder(this)
            .setTitle("Unlock with biometric")
            .setNegativeButton("Cancel", this.mainExecutor) { dialog, which ->
                showOtherOption()
            }.build()
        biometricPrompt?.authenticate(getCancellationSignal(), mainExecutor, authenticationCallback)
    }


    private fun startForegroundService() {
        val notificationIntent = Intent(this, AppListActivity::class.java)
        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_MUTABLE)
        } else {
            PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_ONE_SHOT)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("channelId", "My Background Service", NotificationManager.IMPORTANCE_LOW)

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }


        val notification = NotificationCompat.Builder(this, "channelId")
            .setContentTitle("My Background Service")
            .setContentText("Running...")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentIntent(pendingIntent)
            .build()

        startForeground(1, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val filter = IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
        registerReceiver(homeButtonReceiver, filter)


        mWindowManager = (getSystemService(Context.WINDOW_SERVICE) as WindowManager?)!!
        lockView = View.inflate(this, R.layout.layout_lock_screen, null)
        patternLockView = lockView.findViewById(R.id.pattern_lock_view)
        pinLockView = lockView.findViewById(R.id.pin_lock_view)
        indicatorDots = lockView.findViewById(R.id.indicator_dots)
        passcodeView = lockView.findViewById(R.id.cl_passcode)
        fingerprintView = lockView.findViewById(R.id.cl_fingerprint_view)
        appName = lockView.findViewById(R.id.tv_app_name)
        appIcon = lockView.findViewById(R.id.iv_app_icon)

        val adView = lockView.findViewById<AdView>(R.id.adView)

        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)

        if (getTouchLockEnabledStatus()){
            patternLockView.visibility = View.GONE
            passcodeView.visibility = View.GONE
        }else{
            showOtherOption()
        }

        return START_STICKY
    }

    private fun showOtherOption() {
        fingerprintView.visibility = View.GONE
        if (isAuthTypePattern()) {
            patternLockView.visibility = View.VISIBLE
            patternLockView.addPatternLockListener(mPatternLockViewListener)
        } else {
            passcodeView.visibility = View.VISIBLE
            pinLockView.attachIndicatorDots(indicatorDots)
            pinLockView.setPinLockListener(mPinLockListener)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        // Stop your background work here
        // ...

        Toast.makeText(this, "Service has stopped...", Toast.LENGTH_SHORT).show()
        mWindowManager.removeView(lockView)
        setOpenedAppPackage("")

    }



    private val mPatternLockViewListener: PatternLockViewListener = object : PatternLockViewListener {
        override fun onStarted() {
            Log.d(javaClass.name, "Pattern drawing started")
        }

        override fun onProgress(progressPattern: List<PatternLockView.Dot>) {
            Log.d(javaClass.name, "Pattern progress: " + PatternLockUtils.patternToString(patternLockView, progressPattern))
        }

        override fun onComplete(pattern: List<PatternLockView.Dot>) {
            Log.d(javaClass.name, "Pattern complete: " + PatternLockUtils.patternToString(patternLockView, pattern))
            val patternPath = PatternLockUtils.patternToString(patternLockView, pattern)
            // Check if pattern matches the correct pattern
            if (patternPath == getPatternLockPath()){
                try {
                    // Pattern is correct, do something here
                    patternLockView.clearPattern()
                    setOpenedAppPackage(lastOpenedAppPackageName)
                    mWindowManager.removeView(lockView)
                }catch (e: java.lang.Exception){
                    println("errorrr ${e.message}")
                }
            }else if (pattern.isNotEmpty()){
                // Pattern is incorrect, throw wrong pattern error after 500ms
                patternLockView.setViewMode(PatternLockView.PatternViewMode.WRONG)
                Handler().postDelayed({
                    patternLockView.clearPattern()
                    patternLockView.setViewMode(PatternLockView.PatternViewMode.CORRECT)
                }, 1000)
             }else{
                patternLockView.clearPattern()
            }
        }

        override fun onCleared() {
            Log.d(javaClass.name, "Pattern has been cleared")
        }
    }



    private val authenticationCallback = @RequiresApi(Build.VERSION_CODES.P) object : BiometricPrompt.AuthenticationCallback() {
            // here we need to implement two methods
            // onAuthenticationError and onAuthenticationSucceeded
            // If the fingerprint is not recognized by the app it will call
            // onAuthenticationError and show a toast
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
                super.onAuthenticationError(errorCode, errString)
                println("dfjdlsjfs $errString")
                showOtherOption()
            }

        override fun onAuthenticationFailed() {
            super.onAuthenticationFailed()
            cancellationSignal?.cancel()
            showOtherOption()
        }

            // If the fingerprint is recognized by the app then it will call
            // onAuthenticationSucceeded and show a toast that Authentication has Succeed
            // Here you can also start a new activity after that
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult?) {
                super.onAuthenticationSucceeded(result)
                println("dfjdlsjgfdsfdfs")
            }
    }


    private val mPinLockListener: PinLockListener = object : PinLockListener {
        override fun onComplete(pin: String) {
            if (pin == getPatternLockPath()){
                try {
                    pinLockView.resetPinLockView()
                    mWindowManager.removeView(lockView)
                    setOpenedAppPackage(lastOpenedAppPackageName)
                }catch (e: java.lang.Exception){
                    println("errorrr ${e.message}")
                }
            }else{
                Handler().postDelayed({
                    pinLockView.resetPinLockView()
                }, 500)
             }
        }

        override fun onEmpty() {

        }

        override fun onPinChange(pinLength: Int, intermediatePin: String) {

        }



    }


    private fun getAppNameAndIcon(packageName: String): Pair<String, Drawable?> {
        var name = ""
        var appIcon: Drawable? = null

        try {
            val appInfo = packageManager.getApplicationInfo(packageName, 0)
            name = packageManager.getApplicationLabel(appInfo).toString()
            println("fsjdfls $name $packageName")
            appIcon = packageManager.getApplicationIcon(appInfo)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        return Pair(name, appIcon)
    }


    private val homeButtonReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == Intent.ACTION_CLOSE_SYSTEM_DIALOGS) {
               setOpenedAppPackage("")
                try {
                    mWindowManager.removeView(lockView)
                }catch (e: java.lang.Exception){
                    println("errorrr ${e.message}")
                }
            }
        }
    }

}


