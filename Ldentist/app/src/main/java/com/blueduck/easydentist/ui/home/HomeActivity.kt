package com.blueduck.easydentist.ui.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.blueduck.easydentist.R
import com.blueduck.easydentist.databinding.ActivityHomeBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {
    private val binding: ActivityHomeBinding by lazy { ActivityHomeBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
}