package com.blueduck.annotator.screens.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.blueduck.annotator.data.googleauth.*
import com.blueduck.annotator.model.User
import com.blueduck.annotator.util.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor( private val repository: AuthRepository, val oneTapClient: SignInClient, val auth: FirebaseAuth, val database: FirebaseFirestore) : ViewModel() {


    fun oneTapSignIn(result: (OneTapSignInResponse) -> Unit) = viewModelScope.launch {
        val oneTapSignInResponse = repository.oneTapSignInWithGoogle()
        result(oneTapSignInResponse)
    }

    fun signInWithGoogle(googleCredential: AuthCredential, result: (SignInWithGoogleResponse) -> Unit) = viewModelScope.launch {
        val signInWithGoogleResponse = repository.firebaseSignInWithGoogle(googleCredential)
        result(signInWithGoogleResponse)
    }

    fun isUserAlreadyExist(id: String,  result: (CheckUserExistResponse) -> Unit) = viewModelScope.launch{
        result(Response.Loading)
        repository.isUserAlreadyExist(id){
            result(it)
        }
    }

    fun createNewUser(user: User,  result: (CheckUserExistResponse) -> Unit) = viewModelScope.launch{
        result(Response.Loading)
        repository.createNewUser(user){
            result(it)
        }
    }

}