package com.blueduck.dajumgum

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ComponentActivity
import androidx.navigation.NavController

@Composable
fun SettingsScreen(
    navController: NavController,
    bottomBarPadding: PaddingValues
) {
    val context = LocalContext.current
    var newTextFieldValue by remember { mutableStateOf("") }
    var list by remember { mutableStateOf(listOf<PDFObj>()) }
    var i = 0


    @RequiresApi(Build.VERSION_CODES.P)
    fun uriToBitmap(contentResolver: ContentResolver, uri: Uri): Bitmap? {
        return try {
            val source = ImageDecoder.createSource(contentResolver, uri)
            ImageDecoder.decodeBitmap(source)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            // Handle the selected image URI
            val bitmap = uri?.let { selectedUri ->
                // Convert the selected URI to ImageBitmap
                uriToBitmap(context.contentResolver, uri)
            }
            if (bitmap != null) {
                list = list + PDFObj(++i, "image", null, bitmap)
            }
        }
    )



    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(bottomBarPadding)
    ) {

        list.forEach { obj ->
            if (obj.type == "image"){
                item {
                    Image(
                        bitmap = obj.image!!.asImageBitmap(),
                        contentDescription = "Selected Image",
                        modifier = Modifier
                            .size(200.dp)
                            .padding(16.dp)
                    )
                }
            }else{
                item {                 TextField(
                    value = obj.text ?: "",
                    onValueChange = {
                        obj.text = it
                        println("fdsfs ${obj.text}  $it")
                    },

                    modifier = Modifier.fillMaxWidth(),
                    enabled = false
                )
                }

             }
        }

        item {
            OutlinedTextField(
                value = newTextFieldValue,
                onValueChange = {
                    newTextFieldValue = it
                    println("fdsfs $newTextFieldValue  $it")
                },
                label = { Text("Enter additional text") },
                modifier = Modifier.fillMaxWidth()
            )

        }

        item {
            Button(onClick = {
                if (newTextFieldValue.isNotEmpty()){
                    list = list + PDFObj(++i, "text", newTextFieldValue, null)
                    newTextFieldValue = ""
                }
                println("fdsfjsd ${list.size}")
            }) {
                Text(text = "add text")
            }

        }

        item {
            Button(onClick = {
                galleryLauncher.launch("image/*")
            }) {
                Text(text = "add image")
            }

        }


        item {
            Button(onClick = {
                val pdfCreator = PdfCreator(context as ComponentActivity)
                pdfCreator.createPdf(list)
            }) {
                Text(text = "create pdf")
            }

        }

     }
}

data class PDFObj(val key: Int, val type: String, var text: String?, val image: Bitmap?)



