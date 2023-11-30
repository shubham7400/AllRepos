package com.lock.blueduck.applock.util

import android.graphics.Bitmap
import java.io.ByteArrayOutputStream


fun convertBitmapToByteArray(bitmap: Bitmap): ByteArray {
    val stream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
    return stream.toByteArray()
}




