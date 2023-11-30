package com.blueduck.easydentist.ui.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.blueduck.easydentist.databinding.ActivityAuthBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {
    private val binding: ActivityAuthBinding by lazy { ActivityAuthBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        configureUi()
    }

    private fun configureUi() {

    }
}