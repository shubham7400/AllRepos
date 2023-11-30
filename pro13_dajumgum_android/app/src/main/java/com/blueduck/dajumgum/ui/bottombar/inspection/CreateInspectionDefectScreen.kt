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
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.content.FileProvider
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.blueduck.dajumgum.R
import com.blueduck.dajumgum.ui.common.BackButtonAppBar
import com.blueduck.dajumgum.enums.DefectType
import com.blueduck.dajumgum.model.InspectionDefect
import com.blueduck.dajumgum.photoediting.EditImageActivity
import com.blueduck.dajumgum.preferences.DataStoreManager
import com.blueduck.dajumgum.ui.bottombar.HomeViewModel
import com.blueduck.dajumgum.ui.common.CustomSpinner
import com.blueduck.dajumgum.ui.common.DefectImage
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.util.UUID


@Composable
fun CreateInspectionDefectScreen(navController: NavHostController, viewModel: HomeViewModel) {
    val storageRef = FirebaseStorage.getInstance().reference
    val context = LocalContext.current

    val customer = viewModel.currentCustomer

    var defectNumber by remember { mutableStateOf((viewModel.inspectionDefects.size + 1)) }

    var isLoading by remember { mutableStateOf(false) }


    val user = DataStoreManager.getUser(context = context).collectAsState(initial = null)

    var zoomImageUrl by remember { mutableStateOf("") }
    var farImageUrl by remember { mutableStateOf("") }

    var zoomImageUrlToEdit by remember { mutableStateOf("") }
    var farImageUrlToEdit by remember { mutableStateOf("") }

    var zoomImageUri by remember { mutableStateOf<Uri?>(null) }
    var farImageUri by remember { mutableStateOf<Uri?>(null) }

    var selectedCategoryTags by remember { mutableStateOf(listOf<String>()) }
    var selectedInspectionTags by remember { mutableStateOf(listOf<String>()) }
    var selectedPositionTags by remember { mutableStateOf(listOf<String>()) }
    var selectedStatusTags by remember { mutableStateOf(listOf<String>()) }

    if (viewModel.category != null) {
        if (!selectedCategoryTags.contains(viewModel.category)) {
            selectedCategoryTags = selectedCategoryTags + viewModel.category!!
        }
    }


    val categoryTags = viewModel.categoryTags
    val inspectionTags = viewModel.inspectionTags
    val positionTags = viewModel.positionTags
    val statusTags = viewModel.statusTags



    LaunchedEffect(key1 = user.value, block = {
        viewModel.getCategoryTags(context = context)
        viewModel.getInspectionTags(context = context)
        viewModel.getPositionTags(context = context)
        viewModel.getStatusTags(context = context)
    })


    LaunchedEffect(key1 = viewModel.inspectionDefectToEdit, block = {
        viewModel.inspectionDefectToEdit?.let { defect ->
            defectNumber = defect.defectNumber
            zoomImageUrlToEdit = defect.zoomedImageUrl
            farImageUrlToEdit = defect.farImageUrl
            selectedCategoryTags = selectedCategoryTags + defect.category
            selectedPositionTags = selectedPositionTags + defect.position
            selectedInspectionTags = selectedInspectionTags + defect.inspection
            selectedStatusTags = selectedStatusTags + defect.status
        }
    })


    if (zoomImageUrl.isNotEmpty() && farImageUrl.isNotEmpty()) {
        val defect = InspectionDefect(
            id = viewModel.inspectionDefectToEdit?.id ?: UUID.randomUUID().toString(),
            defectNumber = defectNumber,
            customerId = customer.id,
            defectType = DefectType.INSPECTION.value,
            farImageUrl = farImageUrl,
            zoomedImageUrl = zoomImageUrl,
            category = selectedCategoryTags.joinToString(separator = ""),
            position = selectedPositionTags,
            inspection = selectedInspectionTags,
            status = selectedStatusTags[0],
            createdAt = (System.currentTimeMillis() / 1000),
            updatedAt = (System.currentTimeMillis() / 1000)
        )
        zoomImageUrl = ""
        farImageUrl = ""
        isLoading = true
        viewModel.createInspectionDefect(defect, user.value!!.id,
            onSuccess = {
                isLoading = false
                navController.popBackStack()
            }, onFailure = {
                isLoading = false
                Toast.makeText(context, it.message.toString(), Toast.LENGTH_SHORT).show()
            })
    }

    fun uploadImageToFirebase(uri: Uri, onComplete: (String) -> Unit) {
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
                    onComplete(url.toString())
                }
            }
        }
    }

    fun isInputValid(): Boolean {
        // Check if the category is not empty
        if (zoomImageUrlToEdit.isEmpty()){
            if (zoomImageUri == null) {
                Toast.makeText(
                    context,
                    "확대된 이미지를 선택하세요.",
                    Toast.LENGTH_SHORT
                ).show()
                return false
            }
        }


        // Check if the category is not empty
        if (farImageUrlToEdit.isEmpty()){
            if (farImageUri == null) {
                Toast.makeText(
                    context,
                    "원거리 이미지를 선택하십시오.",
                    Toast.LENGTH_SHORT
                ).show()
                return false
            }
        }

        // Check if the category is not empty
        if (selectedCategoryTags.isEmpty()) {
            Toast.makeText(
                context,
                "카테고리를 입력해주세요",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }

        // Check if the position is not empty
        if (selectedPositionTags.isEmpty()) {
            Toast.makeText(
                context,
                "위치를 입력하세요.",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }

        // Check if the inspection is valid
        if (selectedInspectionTags.isEmpty()) {
            Toast.makeText(
                context,
                "검사를 입력하십시오",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }

        // Check if the status is not empty
        if (selectedStatusTags.isEmpty()) {
            Toast.makeText(
                context,
                "상태를 선택하세요.",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }

        // All validations passed, return true
        return true
    }



    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            BackButtonAppBar {
                navController.popBackStack()
            }
        },
    ) { innerPadding ->
        ConstraintLayout(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)) {
            val (mainContent, progressBar) = createRefs()
            LazyColumn(
                content = {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        ) {
                            Text(text = "Defect No.",  modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp))
                            Row(
                                modifier = Modifier
                                    .border(
                                        width = 1.dp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = defectNumber.toString())
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = ""
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp),
                                horizontalArrangement = Arrangement.SpaceAround,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                DefectImage(
                                    imageType = 1,
                                    defectType = DefectType.INSPECTION,
                                    viewModel
                                ){
                                    zoomImageUri = it
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                DefectImage(
                                    imageType = 2,
                                    defectType = DefectType.INSPECTION,
                                    viewModel
                                ){
                                    farImageUri = it
                                }

                            }

                            Text(
                                text = "Category", modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                            )
                            CustomSpinner(
                                categoryTags,
                                selectedCategoryTags,
                                true,
                                onTagSelection = { tag ->
                                    selectedCategoryTags = selectedCategoryTags + tag
                                },
                                onTagRemove = { selectedCategoryTags = emptyList() })

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "Position", modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                            )
                            CustomSpinner(positionTags, selectedPositionTags,
                                onTagSelection = { tag ->
                                    if (!selectedPositionTags.contains(tag)) {
                                        selectedPositionTags = selectedPositionTags + tag
                                    }
                                }, onTagRemove = { tag ->
                                    selectedPositionTags = selectedPositionTags.filter { it != tag }
                                }
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "Inspection", modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                            )
                            CustomSpinner(inspectionTags, selectedInspectionTags,
                                onTagSelection = { tag ->
                                    if (!selectedInspectionTags.contains(tag)) {
                                        selectedInspectionTags = selectedInspectionTags + tag
                                    }
                                }, onTagRemove = { tag ->
                                    selectedInspectionTags =
                                        selectedInspectionTags.filter { it != tag }
                                }
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "Status", modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                            )
                            CustomSpinner(statusTags, selectedStatusTags,
                                onTagSelection = { tag ->
                                    if (!selectedStatusTags.contains(tag)) {
                                        selectedStatusTags = listOf(tag)
                                    }
                                }, onTagRemove = { tag ->
                                    selectedStatusTags = listOf(tag)
                                }
                            )

                            Spacer(modifier = Modifier.height(32.dp))

                            ElevatedButton(
                                onClick = {
                                    if (isInputValid()) {
                                        if (zoomImageUri != null){
                                            uploadImageToFirebase(zoomImageUri!!) {
                                                zoomImageUrl = it
                                            }
                                        }else{
                                            zoomImageUrl = zoomImageUrlToEdit
                                        }
                                        if (farImageUri != null){
                                            uploadImageToFirebase(farImageUri!!) {
                                                farImageUrl = it
                                            }
                                        }else{
                                            farImageUrl = farImageUrlToEdit
                                        }
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










