package com.blueduck.annotator.data.googleauth

import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.blueduck.annotator.enums.FirestoreCollection
import com.blueduck.annotator.enums.FirestoreDocumentProperties
import com.blueduck.annotator.model.User
import com.blueduck.annotator.util.Response
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(private val database: FirebaseFirestore, private val auth: FirebaseAuth, private val oneTapClient: SignInClient, private val signInRequest: BeginSignInRequest) : AuthRepository {

    override suspend fun oneTapSignInWithGoogle(): OneTapSignInResponse {
        return try {
            val signInResult = oneTapClient.beginSignIn(signInRequest).await()
            Response.Success(signInResult)
        } catch (e: Exception) {
            Response.Failure(e)
        }
    }

    override suspend fun firebaseSignInWithGoogle(googleCredential: AuthCredential): SignInWithGoogleResponse {
        return try {
            val authResult = auth.signInWithCredential(googleCredential).await()
            Response.Success(authResult.user)
        } catch (e: Exception) {
            Response.Failure(e)
        }
    }

    override suspend fun isUserAlreadyExist(id: String, result: (CheckUserExistResponse) -> Unit) {
        val userRef = database.collection(FirestoreCollection.USERS.value).document(id)
        userRef.get().addOnSuccessListener {  docSnapshot ->
            if ( docSnapshot.exists()) {
                val userId = docSnapshot.getString(FirestoreDocumentProperties.User.ID.value)!!
                val name = docSnapshot.getString(FirestoreDocumentProperties.User.NAME.value) ?: ""
                val profileImage = docSnapshot.getString(FirestoreDocumentProperties.User.PROFILE_IMAGE.value) ?: ""
                val email = docSnapshot.getString(FirestoreDocumentProperties.User.EMAIL.value) ?: ""
                val createdAt = docSnapshot.getLong(FirestoreDocumentProperties.User.CREATED_AT.value) ?: 0
                val lastSeen = docSnapshot.getLong(FirestoreDocumentProperties.User.LAST_SEEN.value) ?: 0
                val userStatus = docSnapshot.getBoolean(FirestoreDocumentProperties.User.USER_STATUS.value) ?: false
                val userType = docSnapshot.getString(FirestoreDocumentProperties.User.USER_TYPE.value) ?: ""
                val user = User(
                    id = userId,
                    name = name,
                    profileImage = profileImage,
                    email = email,
                    createdAt = createdAt,
                    lastSeen = lastSeen,
                    userStatus = userStatus,
                    userType = userType
                )
                result(Response.Success(user))
            }else{
                result(Response.Success(null))
            }
        }.addOnFailureListener {
            result(Response.Failure(it))
        }

    }

    override suspend fun createNewUser(user: User, result: (CheckUserExistResponse) -> Unit) {
        database.collection(FirestoreCollection.USERS.value).document(user.id).set(user).addOnSuccessListener {
            result(Response.Success(user))
        }.addOnFailureListener { e ->
            result(Response.Failure(e))
        }
    }


}