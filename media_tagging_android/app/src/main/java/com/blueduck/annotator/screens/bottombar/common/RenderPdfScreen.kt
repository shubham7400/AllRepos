package com.blueduck.annotator.screens.bottombar.common

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.paging.compose.LazyPagingItems
import com.blueduck.annotator.model.MyFile
import com.rizzi.bouquet.ResourceType
import com.rizzi.bouquet.VerticalPDFReader
import com.rizzi.bouquet.rememberVerticalPdfReaderState

@Composable
fun RenderPdfScreen(files: LazyPagingItems<MyFile>, selectedTextFileIndex: Int) {
    val file = files[selectedTextFileIndex]

    fun isUri(input: String): Boolean {
        return input.contains("content:")
    }

    val pdfState = rememberVerticalPdfReaderState(
        resource =   if (isUri(file!!.fileUrl))  ResourceType.Local(Uri.parse(file.fileUrl)) else ResourceType.Remote(file.fileUrl),
        isZoomEnable = true
    )
    VerticalPDFReader(
        state = pdfState,
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Gray)
    )
}