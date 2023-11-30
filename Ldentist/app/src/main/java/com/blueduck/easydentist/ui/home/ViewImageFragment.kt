package com.blueduck.easydentist.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.blueduck.easydentist.R
import com.blueduck.easydentist.databinding.FragmentViewImageBinding
import com.blueduck.easydentist.model.AppUser
import com.blueduck.easydentist.preferences.getUser
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import dagger.hilt.android.AndroidEntryPoint


// this fragment to show post image on full screen and to zoom image
@AndroidEntryPoint
class ViewImageFragment : Fragment() {
    lateinit var binding: FragmentViewImageBinding

    private lateinit var appUser: AppUser

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentViewImageBinding.inflate(inflater, container, false)
        configureUi()
        return binding.root
    }

    private fun configureUi() {
        appUser = requireContext().getUser()!!
        val url = requireArguments().getString("image_url")
        val creatorName = requireArguments().getString("creator_name")
        val date = requireArguments().getString("date")
        binding.tvUserImage.text = appUser.name[0].toString()
        binding.tvUserName.text = creatorName
        binding.tvDate.text = date
        Glide.with(binding.myZoomageView.context)
            .load(url)
            .into(binding.myZoomageView)
        binding.ivBackBtn.setOnClickListener {
            requireActivity().onBackPressed()
        }
        binding.cvProfile.setOnClickListener {
            findNavController().navigate(R.id.action_viewImageFragment_to_profileFragment)
        }
    }

}