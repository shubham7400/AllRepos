package com.blueduck.easydentist.util

import java.text.SimpleDateFormat
import java.util.*

// this function to convert millis second to formatted date string
fun convertMillisToDateString(millis: Long): String {
    val date = Date(millis)
    val dateFormat = SimpleDateFormat("MMM d, yyyy")
    return dateFormat.format(date)
}

// this function to convert millis second to formatted date string
fun convertMillisToDateTimeString(millis: Long): String {
    val date = Date(millis)
    val dateFormat = SimpleDateFormat("MMM d, yyyy HH:mm:ss a")
    return dateFormat.format(date)
}
