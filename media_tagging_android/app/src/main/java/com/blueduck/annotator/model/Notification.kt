package com.blueduck.annotator.model

import com.blueduck.annotator.enums.NotificationType

data class Notification(
    val id: Int,
    val type: NotificationType,
    val description: String,
    val projectName: String? = null,
    val time: String
)