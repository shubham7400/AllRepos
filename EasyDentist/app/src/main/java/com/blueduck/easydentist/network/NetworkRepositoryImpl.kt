package com.blueduck.easydentist.network

import android.content.Context
import com.blueduck.easydentist.enums.FirebaseCollection
import com.blueduck.easydentist.enums.FirebaseDocumentField
import com.blueduck.easydentist.enums.UserPosition
import com.blueduck.easydentist.model.AppUser
import com.blueduck.easydentist.model.Post
import com.blueduck.easydentist.preferences.getUser
import com.blueduck.easydentist.util.Response
import com.blueduck.easydentist.util.logError
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.core.OnlineState
import com.google.gson.Gson
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class NetworkRepositoryImpl @Inject constructor(private val networkDatabase: FirebaseFirestore) : NetworkRepository {

    override suspend fun getAllPost(userId: String, date: String, appUser: AppUser, onStatChange: (GetAllPostResponse) -> Unit) {
         try {
             onStatChange(Response.Loading)
            val query = if (UserPosition.DOCTOR.value == appUser.position){
                if (date.isEmpty()){
                    networkDatabase.collection(FirebaseCollection.POSTS.value)
                }else {
                    networkDatabase.collection(FirebaseCollection.POSTS.value).whereEqualTo(FirebaseDocumentField.DATE.value, date)
                }
            }else{
                if (date.isEmpty()) {
                    networkDatabase.collection(FirebaseCollection.POSTS.value).whereEqualTo(FirebaseDocumentField.USER_ID.value, appUser.userId)
                }else{
                    networkDatabase.collection(FirebaseCollection.POSTS.value).whereEqualTo(FirebaseDocumentField.USER_ID.value, appUser.userId).whereEqualTo(
                        FirebaseDocumentField.DATE.value, date)
                }
            }
            val queryDocumentSnapshots = query.get().await()
            val documents = queryDocumentSnapshots.documents
            val posts = arrayListOf<Post>()
            for (document in documents) {
                if (document.exists()) {
                    val data = document.data
                    val jsonObject = Gson().toJson(data)
                    val post = Gson().fromJson(jsonObject.toString(), Post::class.java)
                    posts.add(post)
                }
            }
            onStatChange(Response.Success(posts))
        }catch (e: java.lang.Exception){
            onStatChange(Response.Failure(Exception("Something Went Wrong ${e.message}")))
        }
    }

    override suspend fun createPost(post: Post, onStatChanged: (CreatePostResponse) -> Unit) {
        try {
            onStatChanged(Response.Loading)
            val docRef = networkDatabase.collection(FirebaseCollection.POSTS.value).add(post).await()
            val docSnapshot = docRef.get().await()
            val data = docSnapshot.data
            val jsonObject = Gson().toJson(data)
            val newPost = Gson().fromJson(jsonObject.toString(), Post::class.java)
            onStatChanged(Response.Success(newPost))
        }catch (e: java.lang.Exception){
            onStatChanged(Response.Failure(Exception("Something Went Wrong ${e.message}")))
        }
    }

    override fun updatePostImages(postId: String, post: Post): UpdatePostResponse {
        return Response.Failure(Exception("Something Went Wrong"))
    }

}