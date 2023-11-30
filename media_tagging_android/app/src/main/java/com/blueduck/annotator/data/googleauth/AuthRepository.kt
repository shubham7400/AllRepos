package com.blueduck.annotator.data.googleauth

import com.google.android.gms.auth.api.identity.BeginSignInResult
import com.google.firebase.auth.AuthCredential
 import com.google.firebase.auth.FirebaseUser
import com.blueduck.annotator.model.User
 import com.blueduck.annotator.util.Response

typealias OneTapSignInResponse = Response<BeginSignInResult>
typealias SignInWithGoogleResponse = Response<FirebaseUser>
typealias CheckUserExistResponse = Response<User?>
typealias CreateNewUserResponse = Response<Boolean>


interface AuthRepository {

    suspend fun oneTapSignInWithGoogle(): OneTapSignInResponse

    suspend fun firebaseSignInWithGoogle(googleCredential: AuthCredential): SignInWithGoogleResponse

    suspend fun isUserAlreadyExist(id: String, result: (CheckUserExistResponse) -> Unit)

    suspend fun createNewUser(user: User, result: (CheckUserExistResponse) -> Unit)

}