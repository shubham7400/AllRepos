package com.blueduck.easydentist.ui.auth

import android.app.Activity
import android.app.ProgressDialog
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.blueduck.easydentist.R
import com.blueduck.easydentist.databinding.FragmentRegisterBinding
import com.blueduck.easydentist.dialog.RegistrationConfirmationDialog
import com.blueduck.easydentist.enums.FirebaseCollection
import com.blueduck.easydentist.enums.FirebaseDocumentField
import com.blueduck.easydentist.enums.UserPosition
import com.blueduck.easydentist.model.AppUser
import com.blueduck.easydentist.util.getSimpleProgressDialog
import com.blueduck.easydentist.util.logError
import com.blueduck.easydentist.util.showToast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import java.util.UUID
import javax.inject.Inject


@AndroidEntryPoint
class RegisterFragment : Fragment() {
    private lateinit var binding: FragmentRegisterBinding

    lateinit var adapter: UserPositionSpinnerAdapter

    // this variable stores selected user type
    var selectedPosition = ""

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
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        configureUi()
        return binding.root
    }

    private fun configureUi() {
        progressDialog = getSimpleProgressDialog(requireContext())
        setClickListeners()
        setupUserPositionSpinner()
    }

    private fun setupUserPositionSpinner() {
        val positions =
            arrayListOf("Select an option", UserPosition.DOCTOR.value, UserPosition.OTHER.value)
        adapter =
            UserPositionSpinnerAdapter(requireActivity(), R.layout.item_spinner_position, positions)
        binding.spUserPosition.adapter = adapter
        adapter.onTitleClick = {

        }
        binding.spUserPosition.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    when (positions[position]) {
                        UserPosition.DOCTOR.value -> {
                            selectedPosition = UserPosition.DOCTOR.value
                        }
                        UserPosition.OTHER.value -> {
                            selectedPosition = UserPosition.OTHER.value
                        }
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // do nothing
                }
            }
    }

    private fun setClickListeners() {
        binding.btnSignUp.setOnClickListener {
            registerNewUser()
        }
        binding.tvGoToLogin.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
    }


    // method to check if the user already exists in the database.
    private fun checkIfUserAlreadyExist(isUserAlreadyExist: (Boolean) -> Unit) {
        if (binding.etEmail.text.isNullOrEmpty()) {
            showToast("Please enter a email.")
        } else {
            val email = binding.etEmail.text.toString()
            progressDialog.show()
            db.collection(FirebaseCollection.USERS.value)
                .whereEqualTo(FirebaseDocumentField.EMAIL.value, email).get().addOnSuccessListener {
                    progressDialog.dismiss()
                    val user = it.firstOrNull()
                    if (user != null) {
                        showToast("User already exist with this email.")
                        isUserAlreadyExist(true)
                    } else {
                        isUserAlreadyExist(false)
                    }
                }.addOnFailureListener {
                    logError(it.message.toString())
                    progressDialog.dismiss()
                }
        }
    }


    // The function calls createUserWithEmailAndPassword() on the firebaseAuth object to create a new Firebase authentication user with the provided email and password.
    // If the user creation process is successful, the function adds the AppUser object to the Firebase database by calling add() on the db object, which represents a reference to the Firebase Firestore database.
    private fun registerNewUser() {
        if (validateInputFields()) {
            checkIfUserAlreadyExist { isExist ->
                if (!isExist) {
                    val accountName = binding.etAccountName.text.toString()
                    val password = binding.etPassword.text.toString()
                    val email = binding.etEmail.text.toString()
                    val name = binding.etName.text.toString()
                    val user = AppUser(UUID.randomUUID().toString(), accountName, email, name, selectedPosition)
                    progressDialog.show()
                    firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(requireActivity()) { task ->
                            if (task.isSuccessful) {
                                db.collection(FirebaseCollection.USERS.value)
                                    .add(user)
                                    .addOnSuccessListener {
                                        val dialog = RegistrationConfirmationDialog(requireActivity())
                                        dialog.onOkClick = {
                                            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                                        }
                                        dialog.show()
                                        progressDialog.dismiss()
                                    }.addOnFailureListener { e ->
                                        logError("Error adding document "+e.message)
                                        progressDialog.dismiss()
                                    }
                            } else {
                                showToast("Sign up failed.")
                                progressDialog.dismiss()
                            }
                        }.addOnFailureListener {
                            showToast("Sign up failed ${it.message}")
                        }
                }
            }
        }
    }

    // checks whether the input fields in a form are valid.
    private fun validateInputFields(): Boolean {
        when {
            binding.etAccountName.text.isNullOrEmpty() -> {
                showToast("Please enter a account name.")
                return false
            }
            binding.etPassword.text.isNullOrEmpty() -> {
                showToast("Please enter a password.")
                return false
            }
            binding.etRePassword.text.isNullOrEmpty() -> {
                showToast("Please enter a re-password.")
                return false
            }
            binding.etPassword.text.toString() != binding.etRePassword.text.toString() -> {
                showToast("Password and confirm password do not match.")
                return false
            }
            (binding.etPassword.text.toString().length < 8 ) -> {
                showToast("Password should be 8 character long.")
                return false
            }
            binding.etEmail.text.isNullOrEmpty() -> {
                showToast("Please enter a email.")
                return false
            }
            !Patterns.EMAIL_ADDRESS.matcher(binding.etEmail.text.toString()).matches() -> {
                showToast("Invalid email format. Please enter a valid email address.")
                return false
            }
            binding.etName.text.isNullOrEmpty() -> {
                showToast("Please enter a name.")
                return false
            }
            selectedPosition.isEmpty() -> {
                showToast("Please select a position.")
                return false
            }
            else -> {
                return true
            }
        }
    }
}


class UserPositionSpinnerAdapter(
    private val activity: Activity,
    id: Int,
    var list: ArrayList<String>
) : ArrayAdapter<String>(activity, id, list) {
    var onTitleClick: () -> Unit = {}

    // The getDropDownView() method is called to create a view for each item in the drop-down list of the Spinner.
    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getDropDownView(position, convertView, parent)
        val tv = view as TextView
        tv.text = list[position]
        if (position == 0) {
            tv.setTextAppearance(R.style.DropdownTitle)
            tv.setOnClickListener { }
        }
        return view
    }

    // The getView() method is called to create a view for the selected item in the Spinner.
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getDropDownView(position, convertView, parent)
        val tv = view as TextView
        tv.text = list[position]

        return view
    }


    // The isEnabled() method is used to determine whether an item at a given position is enabled or disabled.
    override fun isEnabled(position: Int): Boolean {
        super.isEnabled(position)
        return position != 0
    }
}
