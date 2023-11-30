package com.blueduck.easydentist.model

// this object defines the user of the app
data class AppUser(
    val userId: String,
    val accountName: String,
    val email: String,
    val name: String,
    val position: String
) : java.io.Serializable