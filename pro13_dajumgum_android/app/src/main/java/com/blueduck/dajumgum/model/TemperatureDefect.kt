package com.blueduck.dajumgum.model

import java.io.Serializable

data class TemperatureDefect(
    val id: String,
    val defectNumber: Int,
    val customerId: String,
    val defectType: String,
    val category: String,
    val farImageUrl: String,
    val zoomedImageUrl: String,
    val position: List<String>,
    val temperature: List<String>,
    val status: String,
    val createdAt: Long,
    val updatedAt: Long
) : Serializable
