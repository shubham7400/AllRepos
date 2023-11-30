package com.blueduck.crud.model

import java.io.Serializable

data class Student(
    var name: String,
    val phone: Int,
    val email: String,
    var gender: String
) : Serializable
