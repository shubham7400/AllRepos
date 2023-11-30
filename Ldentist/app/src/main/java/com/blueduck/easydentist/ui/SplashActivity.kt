package com.blueduck.easydentist.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.core.content.ContextCompat
import com.blueduck.easydentist.R
import com.blueduck.easydentist.databinding.ActivitySplashBinding
import com.blueduck.easydentist.preferences.getUserLoginStatus
import com.blueduck.easydentist.ui.auth.AuthActivity
import com.blueduck.easydentist.ui.home.HomeActivity

class SplashActivity : AppCompatActivity() {
    val binding: ActivitySplashBinding by lazy { ActivitySplashBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Set the status bar color
        window.statusBarColor = ContextCompat.getColor(this, R.color.white)

        // Start the next activity after a delay
        Handler().postDelayed({
            if (getUserLoginStatus()){
                startActivity(Intent(this, HomeActivity::class.java))
            }else{
                startActivity(Intent(this, AuthActivity::class.java))
            }
            finishAffinity()
        }, 2000) // 2 seconds delay

    }
}