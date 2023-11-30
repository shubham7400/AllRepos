package com.lock.blueduck.applock

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lock.blueduck.applock.databinding.FragmentWrongPatternLimitBottomSheetBinding
import com.lock.blueduck.applock.preferences.getWrongPatternLimit
import com.lock.blueduck.applock.preferences.setWrongPatternLimit
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.*

class WrongPatternLimitBottomSheet : BottomSheetDialogFragment() {
    lateinit var binding: FragmentWrongPatternLimitBottomSheetBinding



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentWrongPatternLimitBottomSheetBinding.inflate(inflater, container, false)
        configureUi()
        return binding.root
    }

    private fun configureUi() {
        when (requireActivity().getWrongPatternLimit()) {
            1 -> { binding.rbLimit1.isChecked = true }
            2 -> { binding.rbLimit2.isChecked = true }
            3 -> { binding.rbLimit3.isChecked = true }
            4 -> { binding.rbLimit4.isChecked = true }
            5 -> { binding.rbLimit5.isChecked = true }
            else -> {
                binding.rbLimit3.isChecked = true
            }
        }
        setClickListeners()
    }

    private fun setClickListeners() {
        binding.radioGroup.setOnCheckedChangeListener { group, selectedId ->
            when (selectedId) {
                R.id.rb_limit_1 -> {
                    setLimit(1)
                }
                R.id.rb_limit_2 -> {
                    setLimit(2)
                }
                R.id.rb_limit_3 -> {
                    setLimit(3)
                }
                R.id.rb_limit_4 -> {
                    setLimit(4)
                }
                R.id.rb_limit_5 -> {
                    setLimit(5)
                }
            }
        }

    }

    private fun setLimit(limit: Int) {
        requireActivity().setWrongPatternLimit(limit)
        CoroutineScope(Dispatchers.Default).launch {
            delay(500)
            dismiss()
        }
    }


    companion object {
        const val TAG = "WrongPatternLimitBottomSheet"
    }
}