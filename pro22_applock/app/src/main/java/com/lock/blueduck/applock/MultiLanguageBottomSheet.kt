package com.lock.blueduck.applock

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lock.blueduck.applock.databinding.FragmentMultiLanguageBottomSheetBinding
import com.lock.blueduck.applock.enum.AppLanguage
import com.lock.blueduck.applock.preferences.getAppLanguage
import com.lock.blueduck.applock.preferences.setAppLanguage
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class MultiLanguageBottomSheet : BottomSheetDialogFragment() {
    lateinit var binding: FragmentMultiLanguageBottomSheetBinding



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentMultiLanguageBottomSheetBinding.inflate(inflater, container, false)
        configureUi()
        return binding.root
    }

    private fun configureUi() {
        when (requireActivity().getAppLanguage()) {
            AppLanguage.HINDI.language -> { binding.rbHindi.isChecked = true }
            AppLanguage.ENGLISH.language -> { binding.rbEnglish.isChecked = true }
            AppLanguage.KOREAN.language -> { binding.rbKorean.isChecked = true }
            AppLanguage.RUSSIAN.language -> { binding.rbRussian.isChecked = true }
            AppLanguage.SPANISH.language -> { binding.rbSpanish.isChecked = true }
            else -> {
                binding.rbEnglish.isChecked = true
            }
        }
        setClickListeners()
    }

    private fun setClickListeners() {
        binding.radioGroup.setOnCheckedChangeListener { group, selectedId ->
            when (selectedId) {
                R.id.rb_hindi -> {
                     //requireActivity().setAppLanguage( AppLanguage.HINDI.language )
                }
                R.id.rb_english -> {
                    requireActivity().setAppLanguage( AppLanguage.ENGLISH.language )
                    reopenApp()
                }
                R.id.rb_korean -> {
                    requireActivity().setAppLanguage( AppLanguage.KOREAN.language )
                    reopenApp()
                }
                R.id.rb_russian -> {
                    //requireActivity().setAppLanguage( AppLanguage.RUSSIAN.language )
                }
                R.id.rb_spanish -> {
                    //requireActivity().setAppLanguage( AppLanguage.SPANISH.language )
                }
            }
        }
        binding.ivClose.setOnClickListener {
            dismiss()
        }
    }

    private fun reopenApp() {
        requireActivity().startActivity(Intent(requireContext(), MainActivity::class.java))
        requireActivity().finishAffinity()
    }


    companion object {
        const val TAG = "MultiLanguageBottomSheet"
    }
}