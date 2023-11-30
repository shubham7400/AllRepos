package com.blueduck.dajumgum.ui.bottombar.inspection

import android.Manifest
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.print.PrintAttributes
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.text.Layout
import android.util.Patterns
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.net.toUri
import androidx.navigation.NavHostController
import com.blueduck.dajumgum.R
import com.blueduck.dajumgum.ui.common.BackButtonAppBar
import com.blueduck.dajumgum.enums.Screen
import com.blueduck.dajumgum.preferences.DataStoreManager
import com.blueduck.dajumgum.ui.bottombar.HomeViewModel
import com.bumptech.glide.Glide
import com.wwdablu.soumya.simplypdf.SimplyPdf
import com.wwdablu.soumya.simplypdf.SimplyPdfDocument
import com.wwdablu.soumya.simplypdf.composers.properties.ImageProperties
import com.wwdablu.soumya.simplypdf.composers.properties.TableProperties
import com.wwdablu.soumya.simplypdf.composers.properties.TextProperties
import com.wwdablu.soumya.simplypdf.composers.properties.cell.Cell
import com.wwdablu.soumya.simplypdf.composers.properties.cell.ImageCell
import com.wwdablu.soumya.simplypdf.composers.properties.cell.TextCell
import com.wwdablu.soumya.simplypdf.document.DocumentInfo
import com.wwdablu.soumya.simplypdf.document.Margin
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.util.*


/**
 * this screen is to preview pdf, download pdf, and to mail the pdf
 */

@Composable
fun InspectionReportScreen(navController: NavHostController, viewModel: HomeViewModel) {
    val context = LocalContext.current
    val user = DataStoreManager.getUser(context = context).collectAsState(initial = null)

    val coroutineScope = rememberCoroutineScope()

    var isLoading by remember { mutableStateOf(false) }

    var email by remember { mutableStateOf("") }

    user.value?.let { email = it.email }

    val inspectionDefects = viewModel.inspectionDefects.groupBy { it.category}.entries.map { it.value }
    val temperatureDefects = viewModel.temperatureDefects.groupBy { it.category}.entries.map { it.value }
    val acDefects = viewModel.acDefects.groupBy { it.category}.entries.map { it.value }



    suspend fun getInspectionDefectRows(simplyPdfDocument: SimplyPdfDocument): LinkedList<LinkedList<Cell>> {

        return LinkedList<LinkedList<Cell>>().apply {

            // Define column width based on usable page width
            val columnWidth = simplyPdfDocument.usablePageWidth / 7

            this.add(
                LinkedList<Cell>().apply {
                    this.add(TextCell("Inspection Report", TextProperties().apply {
                        textSize = 30
                        textColor = "#01A368"
                        alignment = Layout.Alignment.ALIGN_CENTER
                    }, simplyPdfDocument.usablePageWidth))
                }
            )

            inspectionDefects.forEach { group ->

                this.add(
                    LinkedList<Cell>().apply {
                        this.add(TextCell(group[0].category, TextProperties().apply {
                            textSize = 20
                            textColor = "#F3B102"
                            alignment = Layout.Alignment.ALIGN_NORMAL
                        }, simplyPdfDocument.usablePageWidth))
                    }
                )

                // Add header row
                this.add(LinkedList<Cell>().apply {

                    add(TextCell("Number", TextProperties().apply {
                        textSize = 12
                        textColor = "#325ea8"
                        alignment = Layout.Alignment.ALIGN_CENTER
                    }, columnWidth))
                    add(TextCell("Category", TextProperties().apply {
                        textSize = 12
                        textColor = "#325ea8"
                        alignment = Layout.Alignment.ALIGN_CENTER
                    }, columnWidth))
                    add(TextCell("Position", TextProperties().apply {
                        textSize = 12
                        textColor = "#325ea8"
                        alignment = Layout.Alignment.ALIGN_CENTER
                    }, columnWidth))
                    add(TextCell("Inspection", TextProperties().apply {
                        textSize = 12
                        textColor = "#325ea8"
                        alignment = Layout.Alignment.ALIGN_CENTER
                    }, columnWidth))
                    add(TextCell("Close-up", TextProperties().apply {
                        textSize = 12
                        textColor = "#325ea8"
                        alignment = Layout.Alignment.ALIGN_CENTER
                    }, columnWidth))
                    add(TextCell("Zoom Out", TextProperties().apply {
                        textSize = 12
                        textColor = "#325ea8"
                        alignment = Layout.Alignment.ALIGN_CENTER
                    }, columnWidth))
                    add(TextCell("Status", TextProperties().apply {
                        textSize = 12
                        textColor = "#325ea8"
                        alignment = Layout.Alignment.ALIGN_CENTER
                    }, columnWidth))
                })

                // Add rows for each inspection report item
                for (defect in group) {
                    val rrr = LinkedList<Cell>()
                    for (j in 1..7) {
                        when (j) {
                            // Add cells with inspection report data
                            1 -> {
                                rrr.add(TextCell(j.toString(), TextProperties().apply {
                                    textSize = 12
                                    textColor = "#000000"
                                    alignment = Layout.Alignment.ALIGN_CENTER
                                }, columnWidth))
                            }
                            2 -> {
                                rrr.add(
                                    TextCell(
                                        defect.category,
                                        TextProperties().apply {
                                            textSize = 12
                                            textColor = "#000000"
                                        },
                                        columnWidth
                                    )
                                )
                            }
                            3 -> {
                                rrr.add(
                                    TextCell(
                                        defect.position.joinToString(separator = ","),
                                        TextProperties().apply {
                                            textSize = 12
                                            textColor = "#000000"
                                        },
                                        columnWidth
                                    )
                                )
                            }
                            4 -> {
                                rrr.add(
                                    TextCell(
                                        defect.inspection.joinToString(separator = ","),
                                        TextProperties().apply {
                                            textSize = 12
                                            textColor = "#000000"
                                        },
                                        columnWidth
                                    )
                                )
                            }
                            5 -> {
                                 val bitmap = loadBitmapFromImageUrl(defect.zoomedImageUrl, context)
                                 rrr.add(ImageCell(
                                     bitmap,
                                     ImageProperties(),
                                     columnWidth
                                 ))
                            }
                            6 -> {
                                val bitmap = loadBitmapFromImageUrl(defect.farImageUrl, context)
                                rrr.add( ImageCell(
                                    bitmap,
                                    ImageProperties(),
                                    columnWidth
                                ))
                            }
                            7 -> {
                                rrr.add(TextCell(defect.status, TextProperties().apply {
                                    textSize = 12
                                    textColor = "#000000"
                                }, columnWidth))
                            }
                        }
                    }
                    this.add(rrr)
                }

            }
        }

    }

    suspend fun getTemperatureDefectRows(simplyPdfDocument: SimplyPdfDocument): LinkedList<LinkedList<Cell>> {
        return LinkedList<LinkedList<Cell>>().apply {

            // Define column width based on usable page width
            val columnWidth = simplyPdfDocument.usablePageWidth / 7

            this.add(
                LinkedList<Cell>().apply {
                    this.add(TextCell("Temperature Report", TextProperties().apply {
                        textSize = 30
                        textColor = "#01A368"
                        alignment = Layout.Alignment.ALIGN_CENTER
                    }, simplyPdfDocument.usablePageWidth))
                }
            )


            temperatureDefects.forEach { group ->

                this.add(
                    LinkedList<Cell>().apply {
                        this.add(TextCell(group[0].category, TextProperties().apply {
                            textSize = 20
                            textColor = "#F3B102"
                            alignment = Layout.Alignment.ALIGN_NORMAL
                        }, simplyPdfDocument.usablePageWidth))
                    }
                )

                // Add header row
                this.add(LinkedList<Cell>().apply {

                    add(TextCell("Number", TextProperties().apply {
                        textSize = 12
                        textColor = "#325ea8"
                        alignment = Layout.Alignment.ALIGN_CENTER
                    }, columnWidth))
                    add(TextCell("Category", TextProperties().apply {
                        textSize = 12
                        textColor = "#325ea8"
                        alignment = Layout.Alignment.ALIGN_CENTER
                    }, columnWidth))
                    add(TextCell("Position", TextProperties().apply {
                        textSize = 12
                        textColor = "#325ea8"
                        alignment = Layout.Alignment.ALIGN_CENTER
                    }, columnWidth))
                    add(TextCell("Temperature", TextProperties().apply {
                        textSize = 12
                        textColor = "#325ea8"
                        alignment = Layout.Alignment.ALIGN_CENTER
                    }, columnWidth))
                    add(TextCell("Close-up", TextProperties().apply {
                        textSize = 12
                        textColor = "#325ea8"
                        alignment = Layout.Alignment.ALIGN_CENTER
                    }, columnWidth))
                    add(TextCell("Zoom Out", TextProperties().apply {
                        textSize = 12
                        textColor = "#325ea8"
                        alignment = Layout.Alignment.ALIGN_CENTER
                    }, columnWidth))
                    add(TextCell("Status", TextProperties().apply {
                        textSize = 12
                        textColor = "#325ea8"
                        alignment = Layout.Alignment.ALIGN_CENTER
                    }, columnWidth))
                })

                // Add rows for each inspection report item
                for (defect in group) {
                    val rrr = LinkedList<Cell>()
                    for (j in 1..7) {
                        when (j) {
                            // Add cells with inspection report data
                            1 -> {
                                rrr.add(TextCell(j.toString(), TextProperties().apply {
                                    textSize = 12
                                    textColor = "#000000"
                                    alignment = Layout.Alignment.ALIGN_CENTER
                                }, columnWidth))
                            }
                            2 -> {
                                rrr.add(
                                    TextCell(
                                        defect.category,
                                        TextProperties().apply {
                                            textSize = 12
                                            textColor = "#000000"
                                        },
                                        columnWidth
                                    )
                                )
                            }
                            3 -> {
                                rrr.add(
                                    TextCell(
                                        defect.position.joinToString(separator = ","),
                                        TextProperties().apply {
                                            textSize = 12
                                            textColor = "#000000"
                                        },
                                        columnWidth
                                    )
                                )
                            }
                            4 -> {
                                rrr.add(
                                    TextCell(
                                        defect.temperature.joinToString(separator = ","),
                                        TextProperties().apply {
                                            textSize = 12
                                            textColor = "#000000"
                                        },
                                        columnWidth
                                    )
                                )
                            }
                            5 -> {
                                val bitmap = loadBitmapFromImageUrl(defect.zoomedImageUrl, context)
                                rrr.add(ImageCell(
                                    bitmap,
                                    ImageProperties(),
                                    columnWidth
                                ))
                            }
                            6 -> {
                                val bitmap = loadBitmapFromImageUrl(defect.farImageUrl, context)
                                rrr.add( ImageCell(
                                    bitmap,
                                    ImageProperties(),
                                    columnWidth
                                ))
                            }
                            7 -> {
                                rrr.add(TextCell(defect.status, TextProperties().apply {
                                    textSize = 12
                                    textColor = "#000000"
                                }, columnWidth))
                            }
                        }
                    }
                    this.add(rrr)
                }

            }
        }

    }

    fun getACDefectRows(simplyPdfDocument: SimplyPdfDocument): LinkedList<LinkedList<Cell>> {
        return LinkedList<LinkedList<Cell>>().apply {

            // Define column width based on usable page width
            val columnWidth = simplyPdfDocument.usablePageWidth / 6

            this.add(
                LinkedList<Cell>().apply {
                    this.add(TextCell("Ac Report", TextProperties().apply {
                        textSize = 30
                        textColor = "#01A368"
                        alignment = Layout.Alignment.ALIGN_CENTER
                    }, simplyPdfDocument.usablePageWidth))
                }
            )

            acDefects.forEach { group ->

                this.add(
                    LinkedList<Cell>().apply {
                        this.add(TextCell(group[0].category, TextProperties().apply {
                            textSize = 20
                            textColor = "#F3B102"
                            alignment = Layout.Alignment.ALIGN_NORMAL
                        }, simplyPdfDocument.usablePageWidth))
                    }
                )

                // Add header row
                this.add(LinkedList<Cell>().apply {

                    add(TextCell("Number", TextProperties().apply {
                        textSize = 12
                        textColor = "#325ea8"
                        alignment = Layout.Alignment.ALIGN_CENTER
                    }, columnWidth))
                    add(TextCell("Category", TextProperties().apply {
                        textSize = 12
                        textColor = "#325ea8"
                        alignment = Layout.Alignment.ALIGN_CENTER
                    }, columnWidth))
                    add(TextCell("HCHO", TextProperties().apply {
                        textSize = 12
                        textColor = "#325ea8"
                        alignment = Layout.Alignment.ALIGN_CENTER
                    }, columnWidth))
                    add(TextCell("TVOC", TextProperties().apply {
                        textSize = 12
                        textColor = "#325ea8"
                        alignment = Layout.Alignment.ALIGN_CENTER
                    }, columnWidth))
                    add(TextCell("RADON", TextProperties().apply {
                        textSize = 12
                        textColor = "#325ea8"
                        alignment = Layout.Alignment.ALIGN_CENTER
                    }, columnWidth))
                    add(TextCell("Status", TextProperties().apply {
                        textSize = 12
                        textColor = "#325ea8"
                        alignment = Layout.Alignment.ALIGN_CENTER
                    }, columnWidth))
                })

                // Add rows for each inspection report item
                for (defect in group) {
                    val rrr = LinkedList<Cell>()
                    for (j in 1..7) {
                        when (j) {
                            // Add cells with inspection report data
                            1 -> {
                                rrr.add(TextCell(j.toString(), TextProperties().apply {
                                    textSize = 12
                                    textColor = "#000000"
                                    alignment = Layout.Alignment.ALIGN_CENTER
                                }, columnWidth))
                            }
                            2 -> {
                                rrr.add(
                                    TextCell(
                                        defect.category,
                                        TextProperties().apply {
                                            textSize = 12
                                            textColor = "#000000"
                                        },
                                        columnWidth
                                    )
                                )
                            }
                            3 -> {
                                rrr.add(
                                    TextCell(
                                        defect.hcho,
                                        TextProperties().apply {
                                            textSize = 12
                                            textColor = "#000000"
                                        },
                                        columnWidth
                                    )
                                )
                            }
                            4 -> {
                                rrr.add(
                                    TextCell(
                                        defect.tvoc,
                                        TextProperties().apply {
                                            textSize = 12
                                            textColor = "#000000"
                                        },
                                        columnWidth
                                    )
                                )
                            }
                            5 -> {
                                rrr.add(
                                    TextCell(
                                        defect.radon,
                                        TextProperties().apply {
                                            textSize = 12
                                            textColor = "#000000"
                                        },
                                        columnWidth
                                    )
                                )
                            }
                            6 -> {
                                rrr.add(TextCell(defect.status, TextProperties().apply {
                                    textSize = 12
                                    textColor = "#000000"
                                }, columnWidth))
                            }
                        }
                    }
                    this.add(rrr)
                }

            }
        }
    }



    /**
     * Creates a PDF document with inspection report details.
     * @param onComplete Callback invoked when the PDF creation is complete.
     *                   Provides a Boolean indicating success and a URI to the created PDF file.
     */
    suspend fun createPdf( onComplete: (Boolean, Uri?) -> Unit) {
        // Create a new PDF file in the cache directory with a unique name
        val file = File(context.cacheDir, "Dajumgum_${System.currentTimeMillis()}.pdf")

        // Generate a content URI for the PDF file using a FileProvider
        val fileUri = FileProvider.getUriForFile(context, context.packageName + ".provider", file)

        // Create a SimplyPdfDocument with specified properties
        val simplyPdfDocument = SimplyPdf.with(context, file)
            .colorMode(DocumentInfo.ColorMode.COLOR)
            .paperSize(PrintAttributes.MediaSize.ISO_A2)
            .margin(Margin(15U, 15U, 15U, 15U))
            .paperOrientation(DocumentInfo.Orientation.PORTRAIT)
             .build()

        isLoading = true
         val rows = LinkedList<LinkedList<Cell>>()
        val inspectionDefectRows = getInspectionDefectRows(simplyPdfDocument)
        inspectionDefectRows.forEach { rows.add(it) }
        val temperatureDefectRows = getTemperatureDefectRows(simplyPdfDocument)
        temperatureDefectRows.forEach { rows.add(it) }
        val acDefectRows = getACDefectRows(simplyPdfDocument)
        acDefectRows.forEach { rows.add(it) }

        isLoading = false

        // Draw the table with the constructed rows and properties
        simplyPdfDocument.table.draw(rows, TableProperties().apply {
            borderColor = "#000000"
            borderWidth = 1
            drawBorder = true
        })

        // complete all the task and write pdf to the location provided
        val job = coroutineScope.launch {
            simplyPdfDocument.finish()
        }

        job.invokeOnCompletion {
            if (it == null) {
                onComplete(true, fileUri)
            } else {
                onComplete(false, null)
            }
        }
    }

    fun getDisplayNameFromUri(contentResolver: ContentResolver, uri: Uri): String {
        var displayName = ""

        // Retrieve the display name from the content resolver
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val index = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (index >= 0){
                    displayName = it.getString(index )
                }
            }
        }

        return displayName
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    fun movePdfToDownloadsQ(context: Context, pdfUri: Uri) {


        val contentResolver: ContentResolver = context.contentResolver

        // Get the file name and extension from the URI
        val displayName = getDisplayNameFromUri(contentResolver, pdfUri)
        val extension = MimeTypeMap.getFileExtensionFromUrl(displayName)

        // Create a new file in the Download folder
        val values = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, displayName)
            put(MediaStore.Downloads.MIME_TYPE, "application/pdf")
            put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }

        val downloadUri = contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
            ?: return // Insert failed

        // Open input and output streams
        val inputStream: InputStream? = contentResolver.openInputStream(pdfUri)
        val outputStream: OutputStream? = contentResolver.openOutputStream(downloadUri)

        // Copy the PDF file to the Download folder
        inputStream?.use { input ->
            outputStream?.use { output ->
                input.copyTo(output)
            }
        }

        // Close the streams
        inputStream?.close()
        outputStream?.close()
    }


    fun movePdfToDownloadsLegacy(context: Context, pdfUri: Uri) {
        val downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val targetFile = File(downloadDir, getDisplayNameFromUri(context.contentResolver, pdfUri))

        val contentResolver: ContentResolver = context.contentResolver

        // Open input and output streams
        val inputStream: InputStream? = contentResolver.openInputStream(pdfUri)
        val outputStream: OutputStream? = contentResolver.openOutputStream(targetFile.toUri())

        // Copy the PDF file to the Download folder
        inputStream?.use { input ->
            outputStream?.use { output ->
                input.copyTo(output)
            }
        }

        // Close the streams
        inputStream?.close()
        outputStream?.close()
    }

    val storagePermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                movePdfToDownloadsLegacy(context, viewModel.fileUri!!)
            } else {
                Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    )

    fun movePdfToDownloads(context: Context, pdfUri: Uri) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10 (API level 29) and above
            movePdfToDownloadsQ(context, pdfUri)
        } else {
            // Below Android 10 (API level 29)
            storagePermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }


    Scaffold(modifier = Modifier.fillMaxSize(), topBar = { BackButtonAppBar { navController.popBackStack() } }) { paddingValues ->
        ConstraintLayout(modifier = Modifier.fillMaxSize().padding(paddingValues = paddingValues)) {
            val (mainContent, progressBar) = createRefs()

            Column(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {

                Spacer(modifier = Modifier.height(16.dp))


                Text(
                    text = stringResource(R.string.defect_inspection_report),
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                )


                Text(
                    text = stringResource(R.string.generate_defect_inspection_report),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                )

                ConstraintLayout {
                    val (first, second) = createRefs()
                    OutlinedButton(
                        onClick = {
                            if (viewModel.fileUri != null){
                                navController.navigate(Screen.PdfView.route)
                            }else{
                                coroutineScope.launch {
                                    createPdf{ isCreated, uri ->
                                        if (isCreated) {
                                            uri?.let {
                                                viewModel.fileUri = uri
                                                navController.navigate(Screen.PdfView.route)
                                            }
                                        }
                                    }
                                }
                            }
                        },
                        shape = RoundedCornerShape(
                            topStart = 5.dp,
                            topEnd = 5.dp,
                            bottomStart = 5.dp,
                            bottomEnd = 5.dp
                        ),
                        modifier = Modifier.constrainAs(first) {
                            bottom.linkTo(second.top)
                            start.linkTo(second.start)
                            end.linkTo(second.end)
                            top.linkTo(parent.top, margin = 16.dp)
                            width = Dimension.fillToConstraints
                        }
                    ) {
                        Text(text = stringResource(R.string.report_preview))
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(imageVector = Icons.Default.Person, contentDescription = "")
                    }

                    ElevatedButton(
                        onClick = {
                            if (viewModel.fileUri == null) {
                                coroutineScope.launch {
                                    createPdf{ success, uri ->
                                        if (success){
                                            uri?.let {
                                                viewModel.fileUri = uri
                                                movePdfToDownloads(context, uri)
                                                Toast.makeText(
                                                    context,
                                                    context.getString(R.string.pdf_saved),
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                    }

                                }
                            } else {
                                movePdfToDownloads(context, viewModel.fileUri!!)
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.pdf_saved),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        shape = RoundedCornerShape(
                            topStart = 5.dp,
                            topEnd = 5.dp,
                            bottomStart = 5.dp,
                            bottomEnd = 5.dp
                        ),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(id = R.color.orange)
                        ),
                        modifier = Modifier.constrainAs(second) {
                            bottom.linkTo(parent.bottom, margin = 16.dp)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }
                    ) {
                        Text(text = stringResource(R.string.create_and_download_report))
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "",
                            tint = Color.White
                        )
                    }
                }


                Spacer(modifier = Modifier.height(48.dp))

                Text(
                    text = stringResource(R.string.send_defect_inspection_report),
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                )


                Text(
                    text = stringResource(R.string.please_enter_the_email_address_to_which_the_defect_inspection_report_will_be_sent),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                )

                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    value = email,
                    onValueChange = { email = it },
                    label = { Text(text = stringResource(R.string.enter_email)) }
                )


                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(all = 16.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ElevatedButton(
                        onClick = {

                            if (viewModel.fileUri == null){
                                coroutineScope.launch {
                                    createPdf{ success, uri ->
                                        if (success){
                                            uri?.let {
                                                viewModel.fileUri = it
                                                emailPdf(email, context, viewModel)
                                            }
                                        }
                                    }
                                }
                            }else{
                                emailPdf(email, context, viewModel)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(id = R.color.orange)
                        )
                    ) {
                        Text(
                            text = stringResource(id = R.string.send_pdf),
                            modifier = Modifier.padding(horizontal = 20.dp)
                        )
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_forward),
                            contentDescription = ""
                        )
                    }
                }
            }

            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .background(color = Color.Transparent)
                        .constrainAs(progressBar) {
                            bottom.linkTo(parent.bottom)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            top.linkTo(parent.top)
                        },
                    color = colorResource(id = R.color.orange)
                )
            }
        }
    }


}


suspend fun loadBitmapFromImageUrl(imageUrl: String, context: Context): Bitmap {
    return try {
        withContext(Dispatchers.IO) {
            Glide.with(context)
                .asBitmap()
                .load(imageUrl)
                .submit()
                .get()
        }
    }catch (e: Exception){
        Toast.makeText(context, e.message.toString(), Toast.LENGTH_SHORT).show()
        ResourcesCompat.getDrawable(context.resources, R.drawable.ic_error, null)!!.toBitmap()
    }
}


private fun emailPdf(email: String,context: Context, viewModel: HomeViewModel) {
    if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email)
            .matches()
    ) {
        Toast.makeText(
            context,
            context.getString(R.string.please_enter_a_valid_email_address),
            Toast.LENGTH_SHORT
        ).show()
    } else {
        val emailIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            putExtra(Intent.EXTRA_SUBJECT, "PDF File")
            putExtra(Intent.EXTRA_STREAM, viewModel.fileUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        try {
            context.startActivity(
                Intent.createChooser(
                    emailIntent,
                    context.getString(R.string.send_pdf)
                )
            )
        } catch (e: Exception) {
            Toast.makeText(
                context,
                context.getString(R.string.failed_to_send_email)+ e.message,
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}