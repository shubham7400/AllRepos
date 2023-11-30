package com.blueduck.dajumgum.model

data class User(
    val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val dob: Long,
    val title: String,
    ) : java.io.Serializable {
    /*// No-argument constructor
    constructor() : this("", "","", "")*/
}
