package com.blueduck.easydentist.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.blueduck.easydentist.databinding.BottomSheetAddImageBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

// bottom sheet dialog that allows the user to select either the camera or the gallery option for adding an image.
class AddImageBottomSheet : BottomSheetDialogFragment() {
    lateinit var binding: BottomSheetAddImageBinding

    var onGalleryClick: () -> Unit = {}
    var onCameraClick: () -> Unit = {}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetAddImageBinding.inflate(inflater, container, false)
        configureUi()
        return binding.root
    }

    private fun configureUi() {
        setClickListeners()
    }

    private fun setClickListeners() {
        binding.ivClose.setOnClickListener {
            dismiss()
        }
        binding.llGallery.setOnClickListener {
            onGalleryClick()
            dismiss()
        }
        binding.llCamera.setOnClickListener {
            onCameraClick()
            dismiss()
        }
    }

}