package com.blueduck.dajumgum.ui.bottombar.inspection


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.content.FileProvider
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.blueduck.dajumgum.R
import com.blueduck.dajumgum.ui.common.BackButtonAppBar
import com.blueduck.dajumgum.model.Customer
import com.blueduck.dajumgum.photoediting.EditImageActivity
import com.blueduck.dajumgum.preferences.DataStoreManager
import com.blueduck.dajumgum.ui.bottombar.HomeViewModel
import com.blueduck.dajumgum.ui.common.CustomDatePickerDialog
import com.blueduck.dajumgum.util.convertMillisToDate
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.util.UUID


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateCustomerScreen(navController: NavHostController, viewModel: HomeViewModel) {
    val context = LocalContext.current

    val user = DataStoreManager.getUser(context = context).collectAsState(initial = null).value

    var isLoading by remember { mutableStateOf(false) }

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var nameOfPlace by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var width by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var dateOfInspection by remember { mutableStateOf("") }
    var dateOfInspectionInSecond by remember { mutableStateOf<Long>(0) }

    var floorImageUrl by remember { mutableStateOf("") }

    val storageRef = FirebaseStorage.getInstance().reference

    val stroke = Stroke(width = 4f,
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
    )

    var openDatePicker by remember {
        mutableStateOf(false)
    }

    var cameraImageUri: Uri? = null
    var imageUri by remember { mutableStateOf<Uri?>(null) }


    fun uploadImageToFirebase(uri: Uri) {
        val imagesRef = storageRef.child("images/${uri.lastPathSegment}")
        val uploadTask = imagesRef.putFile(uri)
        // Register observers to listen for when the download is done or if it fails
        isLoading = true
        uploadTask.addOnFailureListener { e ->
            isLoading = false
            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
        }.addOnSuccessListener { taskSnapshot ->
            isLoading = false
            if (taskSnapshot.task.isSuccessful) {
                imagesRef.downloadUrl.addOnFailureListener { ex ->
                    Toast.makeText(context, ex.message, Toast.LENGTH_SHORT).show()
                }.addOnSuccessListener { url ->
                    floorImageUrl = url.toString()
                }
            }
        }
    }

    fun isInputValid(): Boolean {
        // Check if the name is not empty
        if (name.isEmpty()) {
            Toast.makeText(
                context,
                context.getString(R.string.please_enter_a_valid_name),
                Toast.LENGTH_SHORT
            ).show()
            return false
        }


        if (nameOfPlace.isEmpty() ) {
            Toast.makeText(
                context,
                "장소명을 입력해주세요",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }

        // Check if the phone number is valid
        if (phoneNumber.length < 5) {
            Toast.makeText(
                context,
                context.getString(R.string.please_enter_a_valid_phone_number),
                Toast.LENGTH_SHORT
            ).show()
            return false
        }

        // Check if the address is valid
        if (address.isEmpty()) {
            Toast.makeText(
                context,
               "유효한 주소를 입력하십시오.",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }


        // Check if the width is valid
        if (width.isEmpty()) {
            Toast.makeText(
                context,
                "유효한 너비를 입력하십시오.",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }

        // Check if the height is valid
        if (height.isEmpty()) {
            Toast.makeText(
                context,
                "유효한 높이를 입력하십시오.",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }

        // Check if the date is valid
        if (dateOfInspection.isEmpty()) {
            Toast.makeText(
                context,
                "검사일자를 입력해주세요.",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }

        // Check if the date is valid
        if (imageUri == null) {
            Toast.makeText(
                context,
                "층 이미지를 선택하세요..",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }

        // All validations passed, return true
        return true
    }

    val startForResult = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // Get the URI returned from Activity B
            val data: Intent? = result.data
            val returnedUri = data?.getParcelableExtra<Uri>("returnedUri")

            returnedUri?.let { uri ->
                imageUri = uri
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
                editImage(imageUri!!)
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
                    editImage(uri)
                }
            })

    if (floorImageUrl.isNotEmpty()){
        val customer = Customer(
            id = UUID.randomUUID().toString(),
            name = name,
            email = email,
            mobile = phoneNumber,
            address = address,
            floorPlanImageUrl = floorImageUrl,
            width = width.toDouble(),
            height = height.toDouble(),
            dateOfInspection = dateOfInspectionInSecond
        )
        floorImageUrl = ""
        isLoading = true
        viewModel.createNewCustomer(customer, user!!.id, onSuccess = {
            isLoading = false
            navController.popBackStack()
        }, onFailure = {
            isLoading = false
            Toast.makeText(context, it.message.toString(), Toast.LENGTH_SHORT).show()
        })
    }

    Scaffold(modifier = Modifier.fillMaxSize(),
        topBar = {
            BackButtonAppBar {
                navController.popBackStack()
            }
        }
    ) { paddingValues ->

        ConstraintLayout(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)) {
            val (mainContent, progressBar) = createRefs()

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                content = {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp, bottom = 0.dp, start = 16.dp, end = 16.dp)
                        ) {
                            Text(text = "Name*" , modifier = Modifier.padding(vertical = 8.dp))
                            OutlinedTextField(
                                value = name,
                                onValueChange = { name = it },
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(height = 50.dp),
                                shape = RoundedCornerShape(
                                    topStart = 10.dp,
                                    topEnd = 10.dp,
                                    bottomStart = 10.dp,
                                    bottomEnd = 10.dp
                                ),
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(text = "Mobile No*", modifier = Modifier.padding(vertical = 8.dp))
                            OutlinedTextField(
                                value = phoneNumber,
                                onValueChange = { phoneNumber = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(height = 50.dp),
                                shape = RoundedCornerShape(
                                    topStart = 10.dp,
                                    topEnd = 10.dp,
                                    bottomStart = 10.dp,
                                    bottomEnd = 10.dp
                                ),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number,imeAction = ImeAction.Next)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(text = "email", modifier = Modifier.padding(vertical = 8.dp))
                            OutlinedTextField(
                                value = email,
                                onValueChange = { email = it },
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(height = 50.dp),
                                shape = RoundedCornerShape(
                                    topStart = 10.dp,
                                    topEnd = 10.dp,
                                    bottomStart = 10.dp,
                                    bottomEnd = 10.dp
                                ),
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(text = "Name of place", modifier = Modifier.padding(vertical = 8.dp))
                            OutlinedTextField(
                                value = nameOfPlace,
                                onValueChange = { nameOfPlace = it },
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(height = 50.dp),
                                shape = RoundedCornerShape(
                                    topStart = 10.dp,
                                    topEnd = 10.dp,
                                    bottomStart = 10.dp,
                                    bottomEnd = 10.dp
                                ),
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(text = "Address", modifier = Modifier.padding(vertical = 8.dp))
                            OutlinedTextField(
                                value = address,
                                onValueChange = { address = it },
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(height = 50.dp),
                                shape = RoundedCornerShape(
                                    topStart = 10.dp,
                                    topEnd = 10.dp,
                                    bottomStart = 10.dp,
                                    bottomEnd = 10.dp
                                ),
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(modifier = Modifier.fillMaxWidth()) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f)
                                ) {
                                    Text(text = "동", modifier = Modifier.padding(vertical = 8.dp))
                                    OutlinedTextField(
                                        value = width,
                                        onValueChange = { width = it },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(height = 50.dp),
                                        shape = RoundedCornerShape(
                                            topStart = 10.dp,
                                            topEnd = 10.dp,
                                            bottomStart = 10.dp,
                                            bottomEnd = 10.dp
                                        ),
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number,imeAction = ImeAction.Next)
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f)
                                ) {
                                    Text(text = "호", modifier = Modifier.padding(vertical = 8.dp))
                                    OutlinedTextField(
                                        value = height,
                                        onValueChange = { height = it },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(height = 50.dp),
                                        shape = RoundedCornerShape(
                                            topStart = 10.dp,
                                            topEnd = 10.dp,
                                            bottomStart = 10.dp,
                                            bottomEnd = 10.dp
                                        ),
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(text = "Date Of Inspection*", modifier = Modifier.padding(vertical = 8.dp))
                            OutlinedTextField(
                                value = dateOfInspection,
                                onValueChange = { dateOfInspection = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(height = 50.dp)
                                    .clickable { openDatePicker = true },
                                shape = RoundedCornerShape(
                                    topStart = 10.dp,
                                    topEnd = 10.dp,
                                    bottomStart = 10.dp,
                                    bottomEnd = 10.dp
                                ),
                                enabled = false,
                                colors = TextFieldDefaults.colors(
                                    disabledIndicatorColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    disabledContainerColor = Color.Transparent,
                                    disabledTextColor = MaterialTheme.colorScheme.onSurface
                                )
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            Row(
                               horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                ConstraintLayout(  modifier = Modifier
                                    .width(200.dp)
                                    .height(200.dp)
                                    .drawBehind {
                                    drawRoundRect(color = Color.Gray, style = stroke, cornerRadius = CornerRadius(10.dp.toPx()))
                                }
                                    .clip(shape = RoundedCornerShape(10.dp))
                                     ) {

                                    Image(
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.FillBounds,
                                        painter = if (imageUri == null) {
                                            painterResource(id = R.drawable.white_background)
                                        } else {
                                            rememberAsyncImagePainter(model = imageUri)
                                        } ,
                                        contentDescription = ""
                                    )
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clickable {
                                                singlePhotoPickerLauncher.launch(
                                                    PickVisualMediaRequest(
                                                        ActivityResultContracts.PickVisualMedia.ImageOnly
                                                    )
                                                )
                                            },
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(painter = painterResource(id = R.drawable.ic_upload), contentDescription = "")

                                        Spacer(modifier = Modifier.height(8.dp))

                                        Text(text = "Upload Floor Plan")

                                        Spacer(modifier = Modifier.height(8.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceAround,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(modifier = Modifier .drawWithCache {
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
                                            }){
                                                Icon(
                                                    modifier = Modifier.padding(10.dp).clickable {
                                                        captureImage(context = context)
                                                    },
                                                    painter = painterResource(id = R.drawable.ic_camera),
                                                    contentDescription = ""
                                                )

                                            }
                                            Box(modifier = Modifier .drawWithCache {
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
                                            }){
                                                Icon(
                                                    modifier = Modifier.padding(10.dp).clickable {
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


                            Spacer(modifier = Modifier.height(24.dp))

                            ElevatedButton(
                                onClick = {
                                    if (isInputValid()) {
                                        uploadImageToFirebase(imageUri!!)
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = colorResource(id = R.color.orange)
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                            ) {
                                Text(text = "저장")
                            }


                            Spacer(modifier = Modifier.height(32.dp))

                            if (openDatePicker) {
                                CustomDatePickerDialog(onCancelClick = { openDatePicker = false }) { dateInFormat, dateInSecond ->
                                    dateOfInspection = dateInFormat
                                    dateOfInspectionInSecond = dateInSecond
                                    openDatePicker = false
                                }
                            }
                        }

                    }
                })


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





