package com.blueduck.easydentist.dialog

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Window
import android.widget.RelativeLayout
import com.blueduck.easydentist.R
import com.blueduck.easydentist.databinding.DialogRegistrationConfirmationBinding

class RegistrationConfirmationDialog(private val activity: Activity) :
    Dialog(activity, R.style.customDialog) {
    private val binding: DialogRegistrationConfirmationBinding by lazy {
        DialogRegistrationConfirmationBinding.inflate(
            LayoutInflater.from(activity),
            null,
            false
        )
    }
    var onOkClick: () -> Unit = {}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(binding.root)
        window?.setLayout(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window?.setDimAmount(0f)

        binding.btnOk.setOnClickListener {
            onOkClick()
            dismiss()
        }
    }


}