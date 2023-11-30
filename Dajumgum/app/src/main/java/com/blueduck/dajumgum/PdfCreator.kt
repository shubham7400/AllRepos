package com.blueduck.dajumgum

import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.*
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.app.ComponentActivity
import androidx.core.content.ContextCompat
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*


private const val STORAGE_PERMISSION_REQUEST_CODE = 1

class PdfCreator(private val activity: ComponentActivity) {

    fun createPdf(list: List<PDFObj>) {
       /* if (!isStoragePermissionGranted()) {
            // Request the permission
            ActivityCompat.requestPermissions(activity, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), STORAGE_PERMISSION_REQUEST_CODE)
            return
        }*/

        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(400, 600, 1).create()
        var page = document.startPage(pageInfo)
        var canvas = page.canvas

        val paint = Paint()
        paint.color = Color.BLACK

        val textX = 20f
        var textY = 20f


        // Set maximum width for the text
        val maxWidth = pageInfo.pageWidth - textX * 2

        var currentHeight = 20

        list.forEach {
            if (it.type == "image"){
                // Draw the image on the canvas
                it.image?.let { bitmap ->
                    // Check if the image height exceeds the remaining height on the page
                    if (currentHeight + bitmap.height > pageInfo.pageHeight) {
                        // Finish the current page and start a new one
                        document.finishPage(page)
                        page = document.startPage(pageInfo)
                        canvas = page.canvas
                        currentHeight = 20 // Reset the current height
                    }

                    // Draw the image on the page
                    currentHeight += drawImage(canvas, bitmap, pageInfo, currentHeight, textX)

                }
            }else{
                val lines = splitTextIntoLines((it.text ?: ""), paint, maxWidth)
                for (line in lines) {
                    canvas.drawText(line, textX, textY, paint)
                    textY += paint.fontSpacing
                }
            }
        }

        document.finishPage(page)

        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "PDF_$timeStamp.pdf"

        val fileOutputStream = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val resolver = activity.contentResolver
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }
            val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
            resolver.openOutputStream(uri!!)
        } else {
            val filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            FileOutputStream(File(filePath, fileName))
        }

        try {
            fileOutputStream.use { outputStream ->
                document.writeTo(outputStream)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            document.close()
        }
    }

    // Method to check if the permission is granted or not
    private fun isStoragePermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            activity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }


    private fun drawImage(
        canvas: Canvas,
        bitmap: Bitmap,
        pageInfo: PdfDocument.PageInfo,
        startY: Int,
        textX: Float
    ): Int {
        val pageWidth = pageInfo.pageWidth
        val pageHeight = pageInfo.pageHeight

        // Calculate the scaled dimensions of the image
        val imageWidth = bitmap.width
        val imageHeight = bitmap.height
        val scale = Math.min(pageWidth.toFloat() / imageWidth, pageHeight.toFloat() / imageHeight)
        val scaledWidth = imageWidth * scale
        val scaledHeight = imageHeight * scale

        // Calculate the top position to draw the image

        val targetBmp: Bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, false)

        // Draw the image on the canvas
        canvas.drawBitmap(targetBmp, null, Rect(textX.toInt(), startY, scaledWidth.toInt(), (startY + scaledHeight).toInt()), null)

        // Return the current height after drawing the image
        return startY + scaledHeight.toInt()
    }


    private fun splitTextIntoLines(text: String, paint: Paint, maxWidth: Float): List<String> {
        val lines = mutableListOf<String>()
        val words = text.split(" ")

        var currentLine = ""
        for (word in words) {
            val lineWithWord = currentLine + word + " "
            val lineWithWordWidth = paint.measureText(lineWithWord)
            if (lineWithWordWidth <= maxWidth) {
                currentLine = lineWithWord
            } else {
                lines.add(currentLine.trim())
                currentLine = word + " "
            }
        }

        lines.add(currentLine.trim())
        return lines
    }
}