package com.blueduck.dajumgum.model

import android.graphics.Bitmap
import java.io.Serializable

class InspectionDefect(
    val id: String,
    val defectNumber: Int,
    val customerId: String,
    val defectType: String,
    val farImageUrl: String,
    val zoomedImageUrl: String,
    val category: String,
    val position: List<String>,
    val inspection: List<String>,
    val status: String,
    val createdAt: Long,
    val updatedAt: Long
): Serializable