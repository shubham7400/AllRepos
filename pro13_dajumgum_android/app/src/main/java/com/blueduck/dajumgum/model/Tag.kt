package com.blueduck.dajumgum.model

import androidx.room.Entity
import androidx.room.PrimaryKey

 data class Tag(
    val id: String,
    val name: String,
    val timestamp: Long
)

