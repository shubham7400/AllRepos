package com.blueduck.dajumgum.model

import android.util.Log
import java.io.Serializable

class Customer(
    val id: String,
    val name: String,
    val email: String,
    val mobile: String,
    val address: String,
    val floorPlanImageUrl: String,
    val width: Double,
    val height: Double,
    val dateOfInspection: Long,
) : Serializable