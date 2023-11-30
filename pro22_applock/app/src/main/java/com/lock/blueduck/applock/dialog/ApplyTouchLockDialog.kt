package com.lock.blueduck.applock.dialog

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Window
import android.widget.RelativeLayout
import com.lock.blueduck.applock.R
import com.lock.blueduck.applock.databinding.DialogApplyTouchLockBinding


class ApplyTouchLockDialog(private val activity: Activity) : Dialog(activity, R.style.customDialog) {
    private val binding: DialogApplyTouchLockBinding by lazy { DialogApplyTouchLockBinding.inflate(LayoutInflater.from(activity), null, false) }

    var onOkayClick: () -> Unit = {}
    var onCancelClick: () -> Unit = {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(binding.root)
        window?.setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window?.setDimAmount(0f)

        configureUi()
    }

    private fun configureUi() {
        setClickListeners()
    }

    private fun setClickListeners() {
        binding.btnCancel.setOnClickListener {
            onCancelClick()
            dismiss()
        }
        binding.btnOkay.setOnClickListener {
            onOkayClick()
            dismiss()
        }
    }

}