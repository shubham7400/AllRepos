package com.blueduck.dajumgum.ui.auth

import androidx.lifecycle.ViewModel
import com.blueduck.dajumgum.model.Customer
import com.blueduck.dajumgum.model.InspectionError
import com.blueduck.dajumgum.model.User
import com.blueduck.dajumgum.util.FirebaseCollections
import com.blueduck.dajumgum.util.FirebaseDocumentProperties
import com.blueduck.dajumgum.util.convertJsonToCustomerArray
import com.google.android.gms.tasks.Task
import com.google.common.reflect.TypeToken
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(val firebaseAuth: FirebaseAuth, private val firestore: FirebaseFirestore): ViewModel() {


    // Simulated function to check user existence in Firebase users collection
    fun checkUserExistence(phoneNumber: String, onComplete: (Boolean, User?, String?) -> Unit) {
        val usersCollection = firestore.collection(FirebaseCollections.USERS)

        usersCollection
            .whereEqualTo(FirebaseDocumentProperties.User.PHONE, phoneNumber)
            .get()
            .addOnSuccessListener { querySnapshot ->
                // Check if any documents match the query
                val userExists = !querySnapshot.isEmpty
                if (userExists){
                    val doc = querySnapshot.documents[0]
                    val user = User(
                        doc.getString(FirebaseDocumentProperties.User.ID)!!,
                         doc.getString(FirebaseDocumentProperties.User.NAME)!!,
                         doc.getString(FirebaseDocumentProperties.User.EMAIL)!!,
                         doc.getString(FirebaseDocumentProperties.User.PHONE)!!,
                         doc.getLong(FirebaseDocumentProperties.User.DOB)!!,
                        doc.getString(FirebaseDocumentProperties.User.TITLE)!!,
                        //convertJsonToCustomerArray(doc.get(FirebaseDocumentProperties.User.CUSTOMERS).toString())
                    )
                    onComplete(userExists, user, null)
                }else{
                    onComplete(userExists, null, null)
                }
            }
            .addOnFailureListener { exception ->
                // Handle failurex
                onComplete(false,null, exception.message)
            }
    }




    fun saveUserToFirestore(user: User, onComplete: (isAdded: Boolean, exception: Exception?) -> Unit) {
        val usersCollection = firestore.collection(FirebaseCollections.USERS).document(user.id)
        usersCollection.set(user)
            .addOnSuccessListener {
                // User saved successfully
                onComplete(true, null)
            }
            .addOnFailureListener { e ->
                // Failed to save user
                onComplete(false, e)
            }
    }


    // Function to send password reset link
    fun sendPasswordResetEmail(email: String, onComplete: (Boolean) -> Unit) {
        val auth = FirebaseAuth.getInstance()

        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Password reset email sent successfully
                    onComplete(true)
                } else {
                    // Password reset email failed to send
                    onComplete(false)
                }
            }
    }

}