package com.blueduck.easydentist.network

import com.blueduck.easydentist.model.AppUser
import com.blueduck.easydentist.model.Post
import com.blueduck.easydentist.util.Response


typealias GetAllPostResponse = Response<ArrayList<Post>>
typealias CreatePostResponse = Response<Post>
typealias UpdatePostResponse = Response<Boolean>

interface NetworkRepository {
    // 1. other - if user type is other, than this api return all post created by him and if he passes particular date then it return all post for
    // that date created by him
    // 2. doctor - if user type is doctor, than this api will return all post. if he passes date then it returns all post for that date
    suspend fun getAllPost(userId: String, date: String = "", appUser: AppUser, onStateChange: (GetAllPostResponse) -> Unit)

    // this api we use create to create a new post
    suspend fun createPost(post: Post, onStatChanged: (CreatePostResponse) -> Unit)

    fun updatePostImages(postId: String, post: Post) : UpdatePostResponse

}