package com.blueduck.easydentist.ui.home

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.blueduck.easydentist.databinding.FragmentProfileBinding
import com.blueduck.easydentist.dialog.LogoutDialog
import com.blueduck.easydentist.preferences.setUserLoginStatus
import com.blueduck.easydentist.ui.SplashActivity
import com.blueduck.easydentist.util.getSimpleProgressDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    lateinit var binding: FragmentProfileBinding

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    lateinit var db: FirebaseFirestore


    private lateinit var progressDialog: ProgressDialog

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        configureUi()
        return binding.root
    }

    private fun configureUi() {
        db = FirebaseFirestore.getInstance()
        progressDialog = getSimpleProgressDialog(requireContext())
        setClickListeners()
    }

    private fun setClickListeners() {
        binding.ivBackBtn.setOnClickListener { requireActivity().onBackPressed() }
        binding.llLogout.setOnClickListener {
            val dialog = LogoutDialog(requireActivity())
            dialog.onOkClick = {
                logoutAndClearCache()
            }
            dialog.show()
        }
    }

    private fun logoutAndClearCache() {
        firebaseAuth.signOut()
        requireContext().setUserLoginStatus(false)
        startActivity(Intent(requireContext(), SplashActivity::class.java))
        requireActivity().finishAffinity()

    }


}