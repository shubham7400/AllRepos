package com.blueduck.easydentist.enums


// defines the type of user, here we can have user either doctor or other
enum class UserPosition(val value: String) {
    OTHER("other"),
    DOCTOR("doctor")
}