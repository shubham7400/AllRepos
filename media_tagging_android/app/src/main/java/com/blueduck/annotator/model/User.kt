package com.blueduck.annotator.model

import java.io.Serializable

data class User(
    val id: String,
    val name: String,
    val profileImage: String,
    val email: String,
    val createdAt: Long,
    val lastSeen: Long,
    val userStatus: Boolean,
    val userType: String,
    ) : Serializable
