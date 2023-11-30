package com.blueduck.dajumgum.ui.bottombar.inspection

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.blueduck.dajumgum.ui.bottombar.HomeViewModel
import com.rizzi.bouquet.ResourceType
import com.rizzi.bouquet.VerticalPDFReader
import com.rizzi.bouquet.rememberVerticalPdfReaderState

/**
 * this screen is to preview pdf
 */

@Composable
fun PdfPreviewScreen(
    navController: NavHostController, viewModel: HomeViewModel
) {
    val uri = viewModel.fileUri

    val pdfState = rememberVerticalPdfReaderState(
        resource = ResourceType.Local(uri!!),
        isZoomEnable = true
    )

    Box(
        contentAlignment = Alignment.TopStart,
        modifier = Modifier.padding(vertical = 40.dp)
    ) {
        VerticalPDFReader(
            state = pdfState,
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.Gray)
        )
    }
}