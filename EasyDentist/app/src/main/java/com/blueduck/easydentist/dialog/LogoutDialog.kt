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
import com.blueduck.easydentist.databinding.DialogLogoutBinding
import com.blueduck.easydentist.databinding.DialogRegistrationConfirmationBinding
import com.blueduck.easydentist.model.AppUser
import com.blueduck.easydentist.preferences.getUser


// This code defines a custom dialog called LogoutDialog that extends the Dialog class.
class LogoutDialog(private val activity: Activity) : Dialog(activity, R.style.customDialog) {
    private val binding: DialogLogoutBinding by lazy {
        DialogLogoutBinding.inflate(
            LayoutInflater.from(activity),
            null,
            false
        )
    }

    // The onOkClick variable is a lambda function that is executed when the user clicks on the "Ok" button in the dialog.
    var onOkClick: () -> Unit = {}

    // The appUser variable is an instance of the AppUser class, and it is assigned in the onCreate method.
    lateinit var appUser: AppUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(binding.root)
        appUser = activity.getUser()!!
        window?.setLayout(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window?.setDimAmount(0f)

        binding.tvUserImage.text = appUser.name[0].toString()
        binding.btnOk.setOnClickListener {
            onOkClick()
            dismiss()
        }
    }


}