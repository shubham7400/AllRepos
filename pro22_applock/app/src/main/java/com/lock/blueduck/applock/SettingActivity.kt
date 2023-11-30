package com.lock.blueduck.applock

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.lock.blueduck.applock.databinding.ActivitySettingBinding

class SettingActivity : AppCompatActivity() {
    val binding: ActivitySettingBinding by lazy { ActivitySettingBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        configureUi()
    }

    private fun configureUi() {

    }
}