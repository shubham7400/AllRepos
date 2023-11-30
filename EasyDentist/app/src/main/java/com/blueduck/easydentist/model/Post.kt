package com.blueduck.easydentist.model


// defines the post that user do post
data class Post(
    val postId: String,
    val userId: String,
    val creatorName: String,
    var diagnosisReport: String,
    val createdAt: Long,
    val date: String,
    val postImages: ArrayList<PostImage>) : java.io.Serializable
