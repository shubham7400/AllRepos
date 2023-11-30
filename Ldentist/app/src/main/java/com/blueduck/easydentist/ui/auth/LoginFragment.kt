package com.blueduck.easydentist.ui.auth

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.blueduck.easydentist.R
import com.blueduck.easydentist.databinding.FragmentLoginBinding
import com.blueduck.easydentist.enums.FirebaseCollection
import com.blueduck.easydentist.enums.FirebaseDocumentField
import com.blueduck.easydentist.model.AppUser
import com.blueduck.easydentist.preferences.getUser
import com.blueduck.easydentist.preferences.setUser
import com.blueduck.easydentist.preferences.setUserLoginStatus
import com.blueduck.easydentist.ui.home.HomeActivity
import com.blueduck.easydentist.util.getSimpleProgressDialog
import com.blueduck.easydentist.util.logError
import com.blueduck.easydentist.util.showToast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment() {
    lateinit var binding: FragmentLoginBinding

    @Inject
    lateinit var db: FirebaseFirestore

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    private lateinit var progressDialog: ProgressDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        configureUi()
        return binding.root
    }

    private fun configureUi() {
        progressDialog = getSimpleProgressDialog(requireContext())
        setClickListeners()
    }

    private fun setClickListeners() {
        binding.btnLogin.setOnClickListener {
            loginToUser()
        }
        binding.tvGoToSignup.setOnClickListener {
            // navigates to the RegisterFragment using the findNavController method.
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
        binding.tvForgotPassword.setOnClickListener {
            // navigates to the ResetPasswordFragment using the findNavController method.
            findNavController().navigate(R.id.action_loginFragment_to_resetPasswordFragment)
        }
    }


    // the function uses the signInWithEmailAndPassword method of the firebaseAuth object to authenticate the user with the entered email and password. If the authentication is successful, the function queries the Firebase Firestore database for the user with the given email and retrieves their data.
    private fun loginToUser() {
        if (validateInputFields()) {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            progressDialog.show()
            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        db.collection(FirebaseCollection.USERS.value)
                            .whereEqualTo(FirebaseDocumentField.EMAIL.value, email).get().addOnSuccessListener {
                                // If the user data exists, the function deserializes the data into an AppUser object using the Gson library and sets the user login status to true.
                                val user = it.firstOrNull()
                                if (user != null) {
                                    val data = user.data
                                    val jsonObject = Gson().toJson(data)
                                    val appUser = Gson().fromJson(jsonObject.toString(), AppUser::class.java)
                                    requireContext().setUser(appUser)
                                    requireContext().setUserLoginStatus(true)
                                    startActivity(Intent(requireContext(), HomeActivity::class.java))
                                    requireActivity().finishAffinity()
                                    progressDialog.dismiss()
                                } else {
                                    showToast("User does not exist with this email.")
                                    progressDialog.dismiss()
                                }
                            }.addOnFailureListener {
                                logError(it.message.toString())
                                progressDialog.dismiss()
                            }
                    } else {
                        showToast("Sign in failed.")
                        progressDialog.dismiss()
                    }
                }.addOnFailureListener {
                    showToast( it.message.toString() )
                    progressDialog.dismiss()
                }
        }
    }


    // The function returns a Boolean value that is true if the input fields are valid, and false otherwise.
    private fun validateInputFields(): Boolean {
        return when {
            binding.etPassword.text.isNullOrEmpty() -> {
                showToast("Please enter a password.")
                false
            }
            binding.etEmail.text.isNullOrEmpty() -> {
                showToast("Please enter a email.")
                false
            }
            else -> {
                true
            }
        }
    }


}