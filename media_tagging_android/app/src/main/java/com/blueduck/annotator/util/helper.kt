package com.blueduck.annotator.util

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.camera.core.impl.utils.ContextUtil.getApplicationContext
import androidx.core.content.FileProvider
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


fun fileDetails(uri: Uri, context: Context) : FileDetails? {
    val contentResolver = context.contentResolver

    val cursor = contentResolver.query(
        uri,
        null,
        null,
        null,
        null
    )

    return cursor?.use { c ->
        if (c.moveToFirst()) {
            val nameColumn = c.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME)
            val sizeColumn = c.getColumnIndex(MediaStore.MediaColumns.SIZE)

            val name = c.getString(nameColumn)
            val size = c.getLong(sizeColumn).toString()

            FileDetails(name, size)
        } else {
            null
        }
    }
}

data class FileDetails(val name: String, val size: String)


// Function to get a Bitmap from a Uri
fun getBitmapFromUri(contentResolver: ContentResolver, uri: Uri): Bitmap? {
    var inputStream: InputStream? = null
    try {
        inputStream = contentResolver.openInputStream(uri)
        if (inputStream != null) {
            return BitmapFactory.decodeStream(inputStream)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        inputStream?.close()
    }
    return null
}


fun uriToByteArray(contentResolver: ContentResolver, uri: Uri): ByteArray? {
    var inputStream: InputStream? = null
    try {
        inputStream = contentResolver.openInputStream(uri)
        if (inputStream != null) {
            val byteArrayOutputStream = ByteArrayOutputStream()
            val buffer = ByteArray(1024)
            var length: Int
            while (inputStream.read(buffer).also { length = it } != -1) {
                byteArrayOutputStream.write(buffer, 0, length)
            }
            return byteArrayOutputStream.toByteArray()
        }
    } catch (e: IOException) {
        e.printStackTrace()
    } finally {
        inputStream?.close()
    }
    return null
}

fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
    val outputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
    return outputStream.toByteArray()
}

fun byteArrayToBitmap(byteArray: ByteArray): Bitmap {
    return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
}


fun copyFileToInternalStorageAndRetrieveUri(fileUri: Uri, context: Context): Uri? {

    try {
        // Create a new file in internal storage with the same name as the original
        var originalFileName = System.currentTimeMillis().formatTime()
        val fileDetails = fileDetails(fileUri, context )
        originalFileName = fileDetails?.name ?: ""
        val internalDir = context.filesDir
        val newFile = File(internalDir, originalFileName)

        // Copy the image file
        val inputStream = context.contentResolver.openInputStream(fileUri)
        val outputStream = FileOutputStream(newFile)
        inputStream?.copyTo(outputStream)
        inputStream?.close()
        outputStream.close()

        // Return the URI of the copied file
        return FileProvider.getUriForFile(
            context,
            context.packageName + ".provider",
            newFile
        )
    } catch (e: IOException) {
        // Handle the exception as needed
        return null
    }
}


fun getThumbnailAndSaveToInternalStorage(imageUri: Uri, context: Context): Uri? {
    val thumbImage = MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri)

    // Create a file to save the bitmap to
    val filename = System.currentTimeMillis().formatTime()
    val file = File(context.filesDir, filename)

    // Create a FileOutputStream to write the bitmap to
    val fileOutputStream = FileOutputStream(file)

    // Write the bitmap to the FileOutputStream
    thumbImage.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)

    // Close the FileOutputStream
    fileOutputStream.close()

    // Return the Uri of the file

    return Uri.fromFile(file)
}

fun saveBitmapToInternalStorageAndGetUri(bitmap: Bitmap, context: Context): Uri? {
    // Create a file to save the bitmap to
    val filename = System.currentTimeMillis().formatTime()
    val file = File(context.filesDir, filename)

    // Create a FileOutputStream to write the bitmap to
    val fileOutputStream = FileOutputStream(file)

    // Write the bitmap to the FileOutputStream
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)

    // Close the FileOutputStream
    fileOutputStream.close()

    // Return the Uri of the file

    return Uri.fromFile(file)
}




fun createVideoThumb(context: Context, uri: Uri) : Bitmap?{
    return try {
        val mediaMetadataRetriever = MediaMetadataRetriever()
        mediaMetadataRetriever.setDataSource(context, uri)
        mediaMetadataRetriever.frameAtTime
    }catch (e: Exception){
        Toast.makeText(context, e.message.toString(), Toast.LENGTH_SHORT).show()
        null
    }
}

@Throws(IOException::class)
fun createImageFile(context: Context ): File {
    val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
     return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
}


fun createPdfFile(context: Context ): File {
    val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
     return File.createTempFile("PDF_${timeStamp}_", ".pdf", storageDir)
}


fun createVideoFile(context: Context ): File {
    val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_MOVIES)
     return File.createTempFile("VIDEO_${timeStamp}_", ".mp4", storageDir)
}

@Throws(IOException::class)
fun createAudioFile(context: Context ): File {
    val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date()).format(Date())
    val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_MUSIC)
     return File.createTempFile("AUDIO_${timeStamp}_", ".mp3", storageDir)
}

fun deleteFileUsingUri(fileUri: Uri, context: Context){
    val path = fileUri.path
    if (path != null){
        val file: File = File(path)
        file.delete()
        if (file.exists()){
            file.canonicalFile.delete()
            if (file.exists()){
                context.deleteFile(file.name)
            }
        }
    }
}




