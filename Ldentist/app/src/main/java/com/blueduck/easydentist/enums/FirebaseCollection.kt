package com.blueduck.easydentist.enums


// The FirebaseCollection enum class is useful when working with Firebase databases in Kotlin. By using this enum class, you can avoid hardcoding the names of collections and reduce the chance of errors caused by typos.
enum class FirebaseCollection(val value: String) {
    USERS("users"),
    POSTS("posts")
}