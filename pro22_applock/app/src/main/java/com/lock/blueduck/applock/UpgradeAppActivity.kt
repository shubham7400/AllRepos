package com.lock.blueduck.applock

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.lock.blueduck.applock.databinding.ActivityUpgradeAppBinding


class UpgradeAppActivity : AppCompatActivity() {
    private val binding: ActivityUpgradeAppBinding by lazy { ActivityUpgradeAppBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)

        configureUi()
    }

    private fun configureUi() {
        setClickListeners()
    }

    private fun setClickListeners() {
        binding.ivArrowBack.setOnClickListener { onBackPressed() }
    }
}