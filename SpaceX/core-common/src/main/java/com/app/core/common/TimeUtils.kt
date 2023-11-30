package com.app.core.common

import java.text.SimpleDateFormat
import java.util.*

object TimeUtils {

    fun formatDate(time: Long, pattern: String = DATE_PATTERN): String =
        SimpleDateFormat(pattern, Locale.getDefault()).format(Date(time))

    private const val DATE_PATTERN = "dd.MM.yyyy - HH:mm"
}