package com.blueduck.dajumgum.model

import java.io.Serializable

data class ACDefect(
    val id: String,
    val customerId: String,
    val defectType: String,
    val category: String,
    val hcho: String,
    val tvoc: String,
    val radon: String,
    val status: String,
    val createdAt: Long,
    val updatedAt: Long
) : Serializable
