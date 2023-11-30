package com.lock.blueduck.applock

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.lock.blueduck.applock.databinding.FragmentTermsServiceBinding


class TermsServiceFragment : Fragment() {
    private lateinit var binding: FragmentTermsServiceBinding


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentTermsServiceBinding.inflate(inflater, container, false)
        configureUi()
        return binding.root
    }

    private fun configureUi() {
        binding.webView.loadUrl("https://spsoftmobile.com/about/privacy_applock.html")

    }

}