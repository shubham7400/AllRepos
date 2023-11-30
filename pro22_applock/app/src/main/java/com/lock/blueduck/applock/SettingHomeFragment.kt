package com.lock.blueduck.applock

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.lock.blueduck.applock.databinding.FragmentSettingHomeBinding
import com.lock.blueduck.applock.preferences.getTouchLockEnabledStatus
import com.lock.blueduck.applock.preferences.setTouchLockEnabledStatus


class SettingHomeFragment : Fragment() {
    lateinit var binding: FragmentSettingHomeBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSettingHomeBinding.inflate(inflater, container, false)
        configureUi()
        return  binding.root
    }

    private fun configureUi() {
        binding.sbAllowFingerprintLock.isChecked = requireActivity().getTouchLockEnabledStatus()

        setClickListeners()
    }

    private fun setClickListeners() {
        binding.clAppLanguage.setOnClickListener {
            val modalBottomSheet = MultiLanguageBottomSheet()
            modalBottomSheet.show(requireActivity().supportFragmentManager, MultiLanguageBottomSheet.TAG)
        }
        binding.clResetPass.setOnClickListener {
            val dialogFragment = PatternSetUpDialogFragment()
            dialogFragment.show(requireActivity().supportFragmentManager, "PatternSetUpDialogFragment")
        }
        binding.sbAllowFingerprintLock.setOnCheckedChangeListener { compoundButton, isChecked ->
            requireActivity().setTouchLockEnabledStatus(isChecked)
        }
        binding.ivArrowBack.setOnClickListener { requireActivity().onBackPressed() }
        binding.clTerms.setOnClickListener {
            findNavController().navigate(R.id.action_settingHomeFragment_to_termsServiceFragment)
        }
    }

}