package com.blueduck.annotator.util

import android.content.Context
import android.graphics.BitmapFactory
import android.text.format.DateFormat
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.blueduck.annotator.R
import com.google.gson.Gson
import org.json.JSONObject
import java.util.Calendar
import java.util.Locale

@Composable
fun LazyGridState.isScrollingUp(): Boolean {
    var previousIndex by remember(this) { mutableIntStateOf(firstVisibleItemIndex) }
    var previousScrollOffset by remember(this) { mutableIntStateOf(firstVisibleItemScrollOffset) }
    return remember(this) {
        derivedStateOf {
            if (previousIndex != firstVisibleItemIndex) {
                previousIndex > firstVisibleItemIndex
            } else {
                previousScrollOffset >= firstVisibleItemScrollOffset
            }.also {
                previousIndex = firstVisibleItemIndex
                previousScrollOffset = firstVisibleItemScrollOffset
            }
        }
    }.value
}

fun Long.formatTime(): String {
    val totalSeconds = this / 1000
    val minutes = totalSeconds / 60
    val remainingSeconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, remainingSeconds)
}

fun Long.getDate(format: CharSequence): String {
    val mediaDate = Calendar.getInstance(Locale.getDefault())
    mediaDate.timeInMillis = this
    return DateFormat.format(format, mediaDate).toString()
}

fun Long.formatFileSize(): String {
    val kb = 1024
    val mb = kb * 1024
    val gb = mb * 1024

    return when {
        this < kb -> "$this B"
        this < mb -> String.format("%.2f KB", this.toDouble() / kb)
        this < gb -> String.format("%.2f MB", this.toDouble() / mb)
        else -> String.format("%.2f GB", this.toDouble() / gb)
    }
}

fun String.getJsonObjectOfThumbnailUri() : String {
    val metadata = JSONObject()
    metadata.put(Constant.THUMBNAIL_URI, this)
    return metadata.toString()
}

fun String.getJsonObjectOfThumbnailUrl() :  String {
    return if (this.isNotEmpty()) {
        val metadataMap = mapOf(Constant.THUMBNAIL_URL to this)
        val gson = Gson()
        gson.toJson(metadataMap)
    } else {
        ""
    }
}

fun Context.getDefaultImagePlaceHolderAsImageBitmap() : ImageBitmap{
    return BitmapFactory.decodeResource(this.resources, R.drawable.error_image).asImageBitmap()
}

