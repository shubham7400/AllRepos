package com.blueduck.dajumgum.ui.common

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.content.FileProvider
import coil.compose.rememberAsyncImagePainter
import com.blueduck.dajumgum.R
import com.blueduck.dajumgum.enums.DefectType
import com.blueduck.dajumgum.photoediting.EditImageActivity
import com.blueduck.dajumgum.ui.bottombar.HomeViewModel
import java.io.File

@Composable
fun DefectImage(imageType: Int, defectType: DefectType, viewModel: HomeViewModel, onImageUriChange: (Uri) -> Unit) {
    val context = LocalContext.current
    var cameraImageUri: Uri? = null
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    var zoomImageUrlToEdit by remember { mutableStateOf("") }
    var farImageUrlToEdit by remember { mutableStateOf("") }

    val stroke = Stroke(width = 4f,
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
    )

    when(defectType){
        DefectType.INSPECTION -> {
            LaunchedEffect(key1 = viewModel.inspectionDefectToEdit, block = {
                viewModel.inspectionDefectToEdit?.let { defect ->
                    zoomImageUrlToEdit = defect.zoomedImageUrl
                    farImageUrlToEdit = defect.farImageUrl
                }
            })
        }
        DefectType.TEMPERATURE -> {
            LaunchedEffect(key1 = viewModel.temperatureDefectToEdit, block = {
                viewModel.temperatureDefectToEdit?.let { defect ->
                    zoomImageUrlToEdit = defect.zoomedImageUrl
                    farImageUrlToEdit = defect.farImageUrl
                }
            })
        }
        DefectType.AC -> {

        }
    }



    val startForResult = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // Get the URI returned from Activity B
            val data: Intent? = result.data
            val returnedUri = data?.getParcelableExtra<Uri>("returnedUri")

            returnedUri?.let { uri ->
                imageUri = uri
                onImageUriChange(uri)
            }
        }
    }

    // Launches the EditImageActivity to edit the image specified by the given URI.
    fun editImage(uri: Uri) {
        // Create an intent to start EditImageActivity
        val intent = Intent(context, EditImageActivity::class.java)

        // Pass the URI of the image to be edited as an extra in the intent
        intent.putExtra("uri", uri)

        // Launch EditImageActivity with the intent and expect a result
        startForResult.launch(intent)
    }

    val takePictureLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.TakePicture()) { success: Boolean ->
            if (success) {
                // Check the image type to determine which image URI to assign
                imageUri = cameraImageUri
                onImageUriChange(cameraImageUri!!)
                editImage(cameraImageUri!!)
            }
        }

    // Captures an image using the device's camera and launches the camera app to take a picture.
    fun captureImage(context: Context) {
        // Create a new image file in the cache directory with a unique name
        val imageFile = File(context.cacheDir, "${System.currentTimeMillis()}.jpg")

        // Generate a content URI for the image file using a FileProvider
        cameraImageUri = FileProvider.getUriForFile(context, context.packageName + ".provider", imageFile)

        // Launch the camera app with the generated image URI
        takePictureLauncher.launch(cameraImageUri)
    }


    val singlePhotoPickerLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.PickVisualMedia(),
            onResult = {
                it?.let { uri ->
                    imageUri = uri
                    onImageUriChange(uri)
                    editImage(uri)
                }
            })


    Surface(
        modifier = Modifier
            .width(150.dp)
            .height(150.dp)
            .drawBehind {
                drawRoundRect(color = Color.Gray, style = stroke, cornerRadius = CornerRadius(10.dp.toPx()))
            }.clip(shape = RoundedCornerShape(10.dp))
    ) {
        ConstraintLayout {
            Image(
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.FillBounds,
                painter = if (imageUri == null) {
                    if (imageType == 1){
                        if (zoomImageUrlToEdit.isNotEmpty()) {
                            rememberAsyncImagePainter(model = zoomImageUrlToEdit)
                        } else {
                            painterResource(id = R.drawable.white_background)
                        }
                    }else{
                        if (farImageUrlToEdit.isNotEmpty()) {
                            rememberAsyncImagePainter(model = farImageUrlToEdit)
                        } else {
                            painterResource(id = R.drawable.white_background)
                        }
                    }
                } else {
                    rememberAsyncImagePainter(model = imageUri)
                },
                contentDescription = ""
            )
            Column(
                modifier = Modifier
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceAround,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(modifier = Modifier.drawWithCache {
                    onDrawBehind {
                        drawRect(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.1f),
                                    Color.White.copy(alpha = 0.2f),
                                    Color.White.copy(alpha = 0.5f),
                                    Color.White.copy(alpha = 0.2f),
                                    Color.White.copy(alpha = 0.1f),
                                )
                            )
                        )
                    }
                }) {
                    Text(
                        modifier = Modifier.padding(all = 4.dp),
                        text = "근접 사진",
                        textAlign = TextAlign.Center
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.drawWithCache {
                        onDrawBehind {
                            drawRect(
                                Brush.radialGradient(
                                    colors = listOf(
                                        Color.White,
                                        Color.Transparent
                                    )
                                )
                            )
                        }
                    }) {
                        Icon(
                            modifier = Modifier
                                .padding(10.dp)
                                .clickable {
                                    captureImage(context = context)
                                },
                            painter = painterResource(id = R.drawable.ic_camera),
                            contentDescription = ""
                        )
                    }
                    Box(modifier = Modifier.drawWithCache {
                        onDrawBehind {
                            drawRect(
                                Brush.radialGradient(
                                    colors = listOf(
                                        Color.White,
                                        Color.Transparent
                                    )
                                )
                            )
                        }
                    }) {
                        Icon(
                            modifier = Modifier
                                .padding(10.dp)
                                .clickable {
                                    singlePhotoPickerLauncher.launch(
                                        PickVisualMediaRequest(
                                            ActivityResultContracts.PickVisualMedia.ImageOnly
                                        )
                                    )
                                },
                            painter = painterResource(id = R.drawable.ic_gallery),
                            contentDescription = ""
                        )
                    }
                }
            }
        }
    }
}
