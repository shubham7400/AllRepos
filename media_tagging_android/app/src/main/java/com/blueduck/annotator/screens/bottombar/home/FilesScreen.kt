package com.blueduck.annotator.screens.bottombar.home

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface


import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.content.FileProvider
import androidx.navigation.NavHostController
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.blueduck.annotator.CameraActivity
import com.blueduck.annotator.DeviceInfo
import com.blueduck.annotator.HomeActivity
import com.blueduck.annotator.R
import com.blueduck.annotator.dialog.DeleteFileDialog
import com.blueduck.annotator.dialog.RenameFileDialog
import com.blueduck.annotator.encryption.EncryptionAndDecryption
import com.blueduck.annotator.enums.BottomSheet
import com.blueduck.annotator.enums.CreateProject
import com.blueduck.annotator.enums.FileTab
import com.blueduck.annotator.enums.FirebaseStorageCollection
import com.blueduck.annotator.enums.FirestoreCollection
import com.blueduck.annotator.enums.ProjectProperties
import com.blueduck.annotator.model.MyFile
import com.blueduck.annotator.model.Project
import com.blueduck.annotator.model.UploadFile
import com.blueduck.annotator.navigation.Screen
import com.blueduck.annotator.screens.bottombar.common.FileMoreVertOption
import com.blueduck.annotator.util.Constant
import com.blueduck.annotator.util.Constant.FILE_URI
 import com.blueduck.annotator.util.Constant.THUMBNAIL_URI
import com.blueduck.annotator.util.Response
import com.blueduck.annotator.util.bitmapToByteArray
import com.blueduck.annotator.util.byteArrayToBitmap
import com.blueduck.annotator.util.copyFileToInternalStorageAndRetrieveUri
import com.blueduck.annotator.util.createImageFile
import com.blueduck.annotator.util.createVideoThumb
import com.blueduck.annotator.util.deleteFileUsingUri
import com.blueduck.annotator.util.fileDetails
import com.blueduck.annotator.util.getBitmapFromUri
import com.blueduck.annotator.util.getJsonObjectOfThumbnailUrl
import com.blueduck.annotator.util.getThumbnailAndSaveToInternalStorage
import com.blueduck.annotator.util.isScrollingUp
import com.blueduck.annotator.util.saveBitmapToInternalStorageAndGetUri
import com.blueduck.annotator.worker.UploadFileWorker
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storageMetadata
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.json.JSONObject
import java.io.File
import java.util.UUID


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class,
    ExperimentalPagerApi::class
)
@Composable
fun FilesScreen(navController: NavHostController, viewModel: HomeViewModel, deviceInfo: DeviceInfo) {
    val context = LocalContext.current

    val project = viewModel.selectedProject!!

    val workManager = WorkManager.getInstance(context.applicationContext)


    val tabs = listOf(FileTab.DATA, FileTab.EDITOR, FileTab.MEMBERS, FileTab.SETTINGS)
    var tabIndex by remember { mutableIntStateOf(0) }

    val pagerState = rememberPagerState()


    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text(text = project.name)},
            navigationIcon = { Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "",
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .clickable {
                        (context as HomeActivity).onBackPressed()
                    }
            ) },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                titleContentColor = MaterialTheme.colorScheme.onPrimary,
            )
        )
        TabRow(selectedTabIndex = tabIndex,
            backgroundColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    modifier = Modifier.pagerTabIndicatorOffset(pagerState = pagerState, tabPositions),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        ) {
            tabs.forEachIndexed { index, tab ->
                Tab(selected = tabIndex == index, onClick = {
                    tabIndex = index
                }) {
                    Text(text = tab.value, modifier = Modifier.padding(bottom = 8.dp))
                }
            }
        }
        HorizontalPager(
            count = tabs.size,
            state = pagerState,
        ) { index ->
            when(index){
                0 -> {
                    when(project.projectFileType){
                        CreateProject.FileType.IMAGE.value -> {
                            ShowImageFiles(navController, project, viewModel, deviceInfo, workManager )
                        }
                        CreateProject.FileType.VIDEO.value -> {
                            ShowVideoFiles(navController, project, viewModel, deviceInfo,  workManager)
                        }
                        CreateProject.FileType.AUDIO.value -> {
                            ShowAudioFiles(navController, project, viewModel, deviceInfo, workManager )
                        }
                        CreateProject.FileType.TEXT.value -> {
                            ShowTextFiles(navController, project, viewModel, deviceInfo, workManager )
                        }
                    }

                }
                1 -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = "This screen is coming soon.")
                    }
                }
                2 -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = "This screen is coming soon.")
                    }
                }
                3 -> {
                    Box(modifier = Modifier.fillMaxSize(),  contentAlignment = Alignment.Center) {
                        //Text(text = "This screen is coming soon.")
                        var tt by remember {
                            mutableStateOf("")
                        }
                        Column {
                            OutlinedTextField(value = tt, onValueChange =  { tt = it})
                            Row {
                                Button(onClick = {
                                    tt = EncryptionAndDecryption.encryptPassword(tt.trim())
                                }) {
                                    Text(text = "encrypt")
                                }
                                Button(onClick = {
                                    tt = EncryptionAndDecryption.decryptPassword(tt.trim())
                                }) {
                                    Text(text = "decrypt")
                                }
                            }
                        }
                    }
                }
            }
        }
     }

 }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowTextFiles(
    navController: NavHostController,
    project: Project,
    viewModel: HomeViewModel,
    deviceInfo: DeviceInfo,
    workManager: WorkManager,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val files = viewModel.getAllFiles(projectId = project.id).collectAsLazyPagingItems()

    val isFileUploading by remember { mutableStateOf(false) }
    var progress by remember { mutableStateOf("0.0%") }

     var selectedTextFileIndex by remember { mutableIntStateOf(0) }

    val scrollState = rememberLazyGridState()


    var showRenameFileDialog by remember { mutableStateOf(false) }
    var showDeleteFiletDialog by remember { mutableStateOf(false) }

    var showProgressIndicator by remember { mutableStateOf(false) }

    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = SheetState(
            skipPartiallyExpanded = false, initialValue = SheetValue.Hidden
        )
    )


    val filePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {

            context.grantUriPermission(context.packageName, uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)

            when(viewModel.storageType){
                CreateProject.StorageType.LOCAL.value -> {
                    val internalStorageFileUri = copyFileToInternalStorageAndRetrieveUri(uri, context)
                    internalStorageFileUri?.let {
                        val fileDetails = fileDetails(internalStorageFileUri, context)
                        if (fileDetails  != null){
                            val imageFile = MyFile(
                                id = UUID.randomUUID().toString(),
                                name = fileDetails.name,
                                tags = arrayListOf(),
                                size = fileDetails.size,
                                createdAt = System.currentTimeMillis(),
                                ownerId = project.ownerId,
                                projectId = project.id,
                                metaData = "",
                                mimeType = "",
                                fileUrl = internalStorageFileUri.toString(),
                                rawData = null
                            )
                            viewModel.addFile(imageFile)
                        }
                    }
                }
                CreateProject.StorageType.CLOUD.value -> {

                    val inputData = Data.Builder()
                        .putString(ProjectProperties.OWNER_ID.value, project.ownerId)
                        .putString(ProjectProperties.ID.value, project.id)
                        .putString(ProjectProperties.FILE_ENCRYPTION_PASSWORD.value, project.fileEncryptionPassword)
                        .putString(ProjectProperties.PROJECT_STORAGE_TYPE.value, project.projectStorageType)
                        .putString(ProjectProperties.PROJECT_FILE_TYPE.value, project.projectFileType)
                        .putString(FILE_URI, uri.toString())
                        .build()
                    val uploadFileWorker = OneTimeWorkRequestBuilder<UploadFileWorker>().setInputData(inputData = inputData)
                        .build()

                    workManager.enqueue(uploadFileWorker)
                }
                CreateProject.StorageType.DRIVE.value -> {

                }
            }
        }
    }



    Scaffold(
        floatingActionButton = {
            if (scrollState.isScrollingUp()  ){
                FloatingActionButton(onClick = {
                    filePickerLauncher.launch(arrayOf("application/pdf"))
                }) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "")
                }
            }
        }
    ) { paddingValues ->
        BottomSheetScaffold(modifier = Modifier.padding(top = paddingValues.calculateTopPadding()),
            scaffoldState = bottomSheetScaffoldState,
            sheetDragHandle = {},
            sheetSwipeEnabled = false,
            sheetContent = {
                FileMoreVertOption(
                    navController = navController,
                    onRenameClick = { showRenameFileDialog = true },
                    onDeleteClick = { showDeleteFiletDialog = true }) {
                    coroutineScope.launch {
                        bottomSheetScaffoldState.bottomSheetState.hide()
                    }
                }
            }){
            ConstraintLayout(modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)) {
                Column {
                    if (files.itemCount == 0){
                        Text(text = "No files", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                    }else{
                        LazyVerticalGrid(
                            modifier = Modifier
                                .weight(weight = 1f)
                                .padding(all = 8.dp)
                                .fillMaxSize(),
                            columns = GridCells.Fixed(if (deviceInfo.isMobile) 2 else 4 ),
                            state = scrollState
                        ) {
                            items(files.itemCount) { index ->
                                files[index]?.let { file ->
                                    PDFView(file, onFileClick =  {
                                        viewModel.selectedFile = file
                                        viewModel.files = files.itemSnapshotList.items
                                        selectedTextFileIndex = index
                                        navController.navigate(Screen.SingleFile.route)
                                    }, onFileOptionClick = {
                                        viewModel.selectedFile = file
                                        selectedTextFileIndex = index
                                        coroutineScope.launch {
                                            bottomSheetScaffoldState.bottomSheetState.expand()
                                        }
                                    })
                                }
                            }
                        }
                    }
                }

                if (showRenameFileDialog) {
                    RenameFileDialog(
                        files[selectedTextFileIndex]!!.name,
                        onCloseClick = {showRenameFileDialog = false },
                        onRenameClick = { newName ->
                            showRenameFileDialog = false
                            viewModel.renameFile(newName, files[selectedTextFileIndex]!!.id) { result ->
                                when(result){
                                    is Response.Loading -> { showProgressIndicator = true }
                                    is Response.Success -> {
                                        showProgressIndicator = false
                                    }
                                    is Response.Failure -> {
                                        showProgressIndicator = false
                                        Toast.makeText(context, result.e.message, Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }
                    )
                }

                if (showDeleteFiletDialog){
                    DeleteFileDialog(
                        onCloseClick = { showDeleteFiletDialog = false },
                        onDeleteClick = {
                            showDeleteFiletDialog = false
                            viewModel.deleteFile(files[selectedTextFileIndex]!!.id) { result ->
                                when(result){
                                    is Response.Loading -> { showProgressIndicator = true }
                                    is Response.Success -> {
                                        showProgressIndicator = false
                                    }
                                    is Response.Failure -> {
                                        showProgressIndicator = false
                                        Toast.makeText(context, result.e.message, Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }
                    )
                }

                if (showProgressIndicator) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(all = 16.dp), horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator()
                    }
                }

                if (isFileUploading){
                    UploadingProgressDialog(progress = progress)
                }
            }

        }
    }

}

@Composable
fun PDFView(file: MyFile, onFileOptionClick: () -> Unit, onFileClick: () -> Unit) {
    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .background(
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    shape = MaterialTheme.shapes.medium
                )
                .aspectRatio(1.2f),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_pdf),
                contentDescription = null,
                modifier = Modifier
                    .clickable {
                        onFileClick()
                    },
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }

        ConstraintLayout(modifier = Modifier) {
            val (name, morevert) = createRefs()
            Text(text = file.name, style = typography.bodyMedium, maxLines = 1,  modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 0.dp, end = 16.dp, bottom = 0.dp)
                .constrainAs(name) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    bottom.linkTo(parent.bottom)
                    end.linkTo(morevert.end)
                }, textAlign = TextAlign.Center)
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "folder menu action",
                modifier = Modifier
                    .clickable {
                        onFileOptionClick()
                    }
                    .constrainAs(morevert) {
                        top.linkTo(parent.top)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                    }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowAudioFiles(
    navController: NavHostController,
    project: Project,
    viewModel: HomeViewModel,
    deviceInfo: DeviceInfo,
    workManager: WorkManager,
 ) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()


    val files = if (viewModel.selectedProject!!.projectStorageType == CreateProject.StorageType.LOCAL.value){
        viewModel.getAllFilesFromLocal(projectId = project.id).collectAsLazyPagingItems()
    }else{
        viewModel.getAllFiles(projectId = project.id).collectAsLazyPagingItems()
    }

    var selectedAudioIndex by remember { mutableIntStateOf(-1) }

    var isFileUploading by remember { mutableStateOf(false) }
    var progress by remember { mutableStateOf("0.0%") }

    var showRenameFileDialog by remember { mutableStateOf(false) }
    var showDeleteFiletDialog by remember { mutableStateOf(false) }

    var showProgressIndicator by remember { mutableStateOf(false) }

    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = SheetState(
            skipPartiallyExpanded = false, initialValue = SheetValue.Hidden
        )
    )



    val scrollState = rememberLazyGridState()

    BackHandler {
        viewModel.releasePlayer()
        navController.popBackStack()
    }


    val audioPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->

            context.grantUriPermission(context.packageName, uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)

            uri?.let {
                when(viewModel.storageType){
                    CreateProject.StorageType.LOCAL.value -> {
                        val internalStorageFileUri = copyFileToInternalStorageAndRetrieveUri(uri, context)
                        internalStorageFileUri?.let {
                            val fileDetails = fileDetails(internalStorageFileUri, context)
                            if (fileDetails != null){
                                val imageFile = MyFile(
                                    id = UUID.randomUUID().toString(),
                                    name = fileDetails.name,
                                    tags = arrayListOf(),
                                    size = fileDetails.size,
                                    createdAt = System.currentTimeMillis(),
                                    ownerId = project.ownerId,
                                    projectId = project.id,
                                    metaData = "",
                                    mimeType = "",
                                    fileUrl = internalStorageFileUri.toString(),
                                    rawData = null
                                )
                                viewModel.addFile(imageFile)
                            }
                        }
                    }
                    CreateProject.StorageType.CLOUD.value -> {
                        val inputData = Data.Builder()
                            .putString(ProjectProperties.OWNER_ID.value, project.ownerId)
                            .putString(ProjectProperties.ID.value, project.id)
                            .putString(ProjectProperties.FILE_ENCRYPTION_PASSWORD.value, project.fileEncryptionPassword)
                            .putString(ProjectProperties.PROJECT_STORAGE_TYPE.value, project.projectStorageType)
                            .putString(ProjectProperties.PROJECT_FILE_TYPE.value, project.projectFileType)
                            .putString(FILE_URI, uri.toString())
                            .build()
                        val uploadFileWorker = OneTimeWorkRequestBuilder<UploadFileWorker>().setInputData(inputData = inputData)
                            .build()

                        workManager.enqueue(uploadFileWorker)

                    }
                    CreateProject.StorageType.DRIVE.value -> {

                    }
                }
            }
         }
    )

    Scaffold(modifier = Modifier.fillMaxSize(), floatingActionButton = {
        if (scrollState.isScrollingUp()){
            FloatingActionButton(onClick = {
                audioPickerLauncher.launch("audio/*")
            }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "")
            }
        }
    }) {pv ->
        BottomSheetScaffold(modifier = Modifier.padding(top = pv.calculateTopPadding()),
            scaffoldState = bottomSheetScaffoldState,
            sheetDragHandle = {},
            sheetSwipeEnabled = false,
            sheetContent = {
                FileMoreVertOption(
                    navController = navController,
                    onRenameClick = { showRenameFileDialog = true },
                    onDeleteClick = { showDeleteFiletDialog = true }) {
                    coroutineScope.launch {
                        bottomSheetScaffoldState.bottomSheetState.hide()
                    }
                }
            }){
            ConstraintLayout(modifier = Modifier
                .fillMaxSize()
                .padding(pv)) {
                Column(modifier = Modifier ) {

                    if (files.itemCount == 0){
                        Text(text = "No files", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                    }else{
                        LazyVerticalGrid(
                            modifier = Modifier
                                .weight(weight = 1f)
                                .padding(all = 8.dp)
                                .fillMaxSize(),
                            state = scrollState,
                            columns = GridCells.Fixed(if (deviceInfo.isMobile) 2 else 4 )
                        ) {
                            items(files.itemCount) { index ->
                                files[index]?.let { file ->
                                    val isSelected = selectedAudioIndex == index
                                    AudioView(file, isSelected, onFileClick = {
                                        viewModel.selectedFile = file
                                        viewModel.files = files.itemSnapshotList.items
                                        viewModel.playNextAudio(file, context)
                                        selectedAudioIndex = index
                                        navController.navigate(Screen.SingleFile.route)
                                    }, onFileOptionClick = {
                                        viewModel.selectedFile = file
                                        selectedAudioIndex = index
                                        coroutineScope.launch {
                                            bottomSheetScaffoldState.bottomSheetState.expand()
                                        }
                                    })
                                }
                            }
                        }
                    }
                }


                if (showRenameFileDialog) {
                    RenameFileDialog(
                        files[selectedAudioIndex]!!.name,
                        onCloseClick = {showRenameFileDialog = false },
                        onRenameClick = { newName ->
                            showRenameFileDialog = false
                            viewModel.renameFile(newName, files[selectedAudioIndex]!!.id) { result ->
                                when(result){
                                    is Response.Loading -> { showProgressIndicator = true }
                                    is Response.Success -> {
                                        showProgressIndicator = false
                                    }
                                    is Response.Failure -> {
                                        showProgressIndicator = false
                                        Toast.makeText(context, result.e.message, Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }
                    )
                }

                if (showDeleteFiletDialog){
                    DeleteFileDialog(
                        onCloseClick = { showDeleteFiletDialog = false },
                        onDeleteClick = {
                            showDeleteFiletDialog = false
                            viewModel.deleteFile(files[selectedAudioIndex]!!.id) { result ->
                                when(result){
                                    is Response.Loading -> { showProgressIndicator = true }
                                    is Response.Success -> {
                                        showProgressIndicator = false
                                    }
                                    is Response.Failure -> {
                                        showProgressIndicator = false
                                        Toast.makeText(context, result.e.message, Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }
                    )
                }


                if (showProgressIndicator) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(all = 16.dp), horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator()
                    }
                }

                if (isFileUploading){
                    UploadingProgressDialog(progress = progress)
                }
            }

        }
    }

}




@Composable
fun AudioView(file: MyFile , isSelected: Boolean, onFileOptionClick: () -> Unit, onFileClick: () -> Unit) {
     Column {
        Box(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .background(
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSecondaryContainer,
                    shape = MaterialTheme.shapes.medium
                )
                .aspectRatio(1.2f),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.headphones),
                contentDescription = null,
                modifier = Modifier
                    .clickable {
                        onFileClick()
                    },
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }

        ConstraintLayout(modifier = Modifier) {
            val (name, morevert) = createRefs()
            Text(text = file.name, style = typography.bodyMedium,   modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 0.dp, end = 16.dp, bottom = 0.dp)
                .constrainAs(name) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    bottom.linkTo(parent.bottom)
                    end.linkTo(morevert.end)
                }, textAlign = TextAlign.Center)
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "folder menu action",
                modifier = Modifier
                    .clickable {
                        onFileOptionClick()
                    }
                    .constrainAs(morevert) {
                        top.linkTo(parent.top)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                    }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
 }


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowVideoFiles(
    navController: NavHostController,
    project: Project,
    viewModel: HomeViewModel,
    deviceInfo: DeviceInfo,
    workManager: WorkManager,
 ) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val storage = FirebaseStorage.getInstance()
    val database = FirebaseFirestore.getInstance()

    var isFileUploading by remember { mutableStateOf(false) }
    var progress by remember { mutableStateOf("0.0%") }

    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = SheetState(
            skipPartiallyExpanded = false, initialValue = SheetValue.Hidden
        )
    )

    val files = viewModel.getAllFiles(projectId = project.id).collectAsLazyPagingItems()

    var selectedVideoIndex by remember { mutableIntStateOf(0) }

    var bottomSheetType by remember { mutableStateOf(BottomSheet.ADD_FILE) }

    var showRenameFileDialog by remember { mutableStateOf(false) }
    var showDeleteFiletDialog by remember { mutableStateOf(false) }

    var showProgressIndicator by remember { mutableStateOf(false) }






    val singleVideoPickerLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.PickVisualMedia(),
            onResult = { uri ->

                context.grantUriPermission(context.packageName, uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)

                uri?.let {
                    when(viewModel.storageType){
                        CreateProject.StorageType.LOCAL.value -> {
                            val internalStorageFileUri = copyFileToInternalStorageAndRetrieveUri(uri, context)
                            internalStorageFileUri?.let {
                                val fileDetails = fileDetails(internalStorageFileUri, context)
                                if (fileDetails != null){
                                    val thumbBitmap = createVideoThumb(context = context, uri)
                                    val thumbnailUri = saveBitmapToInternalStorageAndGetUri(thumbBitmap!!, context = context)
                                    // create file object and store it to the local storage
                                    println("sfdsfsd $thumbnailUri")
                                    val videoFile = MyFile(
                                        id = UUID.randomUUID().toString(),
                                        name = fileDetails.name,
                                        tags = arrayListOf(),
                                        size = fileDetails.size,
                                        createdAt = System.currentTimeMillis(),
                                        ownerId = project.ownerId,
                                        projectId = project.id,
                                        metaData = if (thumbnailUri != null) {
                                            val metadata = JSONObject()
                                            metadata.put(THUMBNAIL_URI, thumbnailUri.toString())
                                            metadata.toString()
                                        } else {
                                            ""
                                        },
                                        mimeType = "",
                                        fileUrl = internalStorageFileUri.toString(),
                                        rawData = null
                                    )
                                    viewModel.addFile(videoFile)
                                }
                            }
                        }
                        CreateProject.StorageType.CLOUD.value -> {
                            val inputData = Data.Builder()
                                .putString(ProjectProperties.OWNER_ID.value, project.ownerId)
                                .putString(ProjectProperties.ID.value, project.id)
                                .putString(ProjectProperties.FILE_ENCRYPTION_PASSWORD.value, project.fileEncryptionPassword)
                                .putString(ProjectProperties.PROJECT_STORAGE_TYPE.value, project.projectStorageType)
                                .putString(ProjectProperties.PROJECT_FILE_TYPE.value, project.projectFileType)
                                .putString(FILE_URI, uri.toString())
                                .build()
                            val uploadFileWorker = OneTimeWorkRequestBuilder<UploadFileWorker>().setInputData(inputData = inputData)
                                .build()

                            workManager.enqueue(uploadFileWorker)

                        }
                        CreateProject.StorageType.DRIVE.value -> {

                        }
                    }
                }
            })

    val takeVideoLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) { result: ActivityResult? ->
            val videoUriString = result?.data?.getStringExtra("video_uri")
            if (videoUriString.isNullOrEmpty()) {
                Toast.makeText(context, "Something Went Wrong", Toast.LENGTH_SHORT).show()
            } else {
                val uri = Uri.parse(videoUriString)

                context.grantUriPermission(context.packageName, uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)

                when(viewModel.storageType){
                    CreateProject.StorageType.LOCAL.value -> {
                        val internalStorageFileUri = copyFileToInternalStorageAndRetrieveUri(uri, context)
                        internalStorageFileUri?.let {
                            val fileDetails = fileDetails(internalStorageFileUri, context)
                            if (fileDetails != null){
                                val thumbBitmap = createVideoThumb(context = context, uri)
                                val thumbnailUri = saveBitmapToInternalStorageAndGetUri(thumbBitmap!!, context = context)
                                // create file object and store it to the local storage
                                println("sfdsfsd $thumbnailUri")
                                val videoFile = MyFile(
                                    id = UUID.randomUUID().toString(),
                                    name = fileDetails.name,
                                    tags = arrayListOf(),
                                    size = fileDetails.size,
                                    createdAt = System.currentTimeMillis(),
                                    ownerId = project.ownerId,
                                    projectId = project.id,
                                    metaData = if (thumbnailUri != null) {
                                        val metadata = JSONObject()
                                        metadata.put(THUMBNAIL_URI, thumbnailUri.toString())
                                        metadata.toString()
                                    } else {
                                        ""
                                    },
                                    mimeType = "",
                                    fileUrl = internalStorageFileUri.toString(),
                                    rawData = null
                                )
                                viewModel.addFile(videoFile)
                            }
                        }
                    }
                    CreateProject.StorageType.CLOUD.value -> {
                        val inputData = Data.Builder()
                            .putString(ProjectProperties.OWNER_ID.value, project.ownerId)
                            .putString(ProjectProperties.ID.value, project.id)
                            .putString(ProjectProperties.FILE_ENCRYPTION_PASSWORD.value, project.fileEncryptionPassword)
                            .putString(ProjectProperties.PROJECT_STORAGE_TYPE.value, project.projectStorageType)
                            .putString(ProjectProperties.PROJECT_FILE_TYPE.value, project.projectFileType)
                            .putString(FILE_URI, uri.toString())
                            .build()
                        val uploadFileWorker = OneTimeWorkRequestBuilder<UploadFileWorker>().setInputData(inputData = inputData)
                            .build()

                        workManager.enqueue(uploadFileWorker)

                       /* coroutineScope.launch {
                            uploadVideoToFirebase(uri, storage, project, context, database,
                                onFileAdded = { files.refresh() },
                                isFileUploading = {
                                    isFileUploading = it
                                },uploadProgress = {
                                    progress = it
                                })

                        }*/

                    }
                    CreateProject.StorageType.DRIVE.value -> {

                    }
                }


             }
        }


    Scaffold(modifier = Modifier.fillMaxSize(), floatingActionButton = {
        if (!bottomSheetScaffoldState.bottomSheetState.isVisible ) {
            FloatingActionButton(onClick = {
                bottomSheetType = BottomSheet.ADD_FILE
                coroutineScope.launch {
                    bottomSheetScaffoldState.bottomSheetState.expand()
                }
            }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "")
            }
        }
    }) { pv ->

        BottomSheetScaffold(modifier = Modifier.padding(top = pv.calculateTopPadding()),
            scaffoldState = bottomSheetScaffoldState,
            sheetDragHandle = {},
            sheetSwipeEnabled = false,
            sheetContent = {
                if(bottomSheetType == BottomSheet.FILE_OPTION){
                    FileMoreVertOption(
                        navController = navController,
                        onRenameClick = { showRenameFileDialog = true },
                        onDeleteClick = { showDeleteFiletDialog = true }) {
                        coroutineScope.launch {
                            bottomSheetScaffoldState.bottomSheetState.hide()
                        }
                    }
                }
                if(bottomSheetType == BottomSheet.ADD_FILE){
                    AddFile(onChooseFromGallery = {
                        singleVideoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly))
                        coroutineScope.launch {
                            bottomSheetScaffoldState.bottomSheetState.hide()
                        }
                    }, onTakePicture = {
                        val intent = Intent(context, CameraActivity::class.java)
                        takeVideoLauncher.launch(intent)
                        coroutineScope.launch {
                            bottomSheetScaffoldState.bottomSheetState.hide()
                        }
                    }, onCancel = {
                        coroutineScope.launch {
                            bottomSheetScaffoldState.bottomSheetState.hide()
                        }
                    })
                }
            }) { pv ->
            ConstraintLayout(modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)) {
                Column(modifier = Modifier.padding(top = pv.calculateTopPadding())) {
                    if (files.itemCount == 0){
                        Text(text = "No files", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                    }else{
                        LazyVerticalGrid(
                            modifier = Modifier.fillMaxWidth(),
                            columns = GridCells.Fixed(if (deviceInfo.isMobile) 2 else 4 )
                        ) {
                            items(files.itemCount) { index ->
                                files[index]?.let { file ->
                                    VideoItem(file, viewModel = viewModel, storage = storage, onFileClick = {
                                        viewModel.selectedFile = file
                                        viewModel.files = files.itemSnapshotList.items
                                        selectedVideoIndex = index
                                        navController.navigate(Screen.SingleFile.route)
                                     }, onFileOptionClick = {
                                        viewModel.selectedFile = file
                                        selectedVideoIndex = index
                                        bottomSheetType = BottomSheet.FILE_OPTION
                                        coroutineScope.launch {
                                            bottomSheetScaffoldState.bottomSheetState.expand()
                                        }
                                    })
                                }
                            }
                        }
                    }

                }

                if (showRenameFileDialog) {
                    RenameFileDialog(
                        files[selectedVideoIndex]!!.name,
                        onCloseClick = {showRenameFileDialog = false },
                        onRenameClick = { newName ->
                            showRenameFileDialog = false
                            viewModel.renameFile(newName, files[selectedVideoIndex]!!.id) { result ->
                                when(result){
                                    is Response.Loading -> { showProgressIndicator = true }
                                    is Response.Success -> {
                                        showProgressIndicator = false
                                    }
                                    is Response.Failure -> {
                                        showProgressIndicator = false
                                        Toast.makeText(context, result.e.message, Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }
                    )
                }

                if (showDeleteFiletDialog){
                    DeleteFileDialog(
                        onCloseClick = { showDeleteFiletDialog = false },
                        onDeleteClick = {
                            showDeleteFiletDialog = false
                            viewModel.deleteFile(files[selectedVideoIndex]!!.id) { result ->
                                when(result){
                                    is Response.Loading -> { showProgressIndicator = true }
                                    is Response.Success -> {
                                        showProgressIndicator = false
                                    }
                                    is Response.Failure -> {
                                        showProgressIndicator = false
                                        Toast.makeText(context, result.e.message, Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }
                    )
                }

                if (showProgressIndicator) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(all = 16.dp), horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator()
                    }
                }

                if (isFileUploading){
                    UploadingProgressDialog(progress = progress)
                }
            }
        }
     }
}

@Composable
fun VideoItem(file: MyFile, viewModel: HomeViewModel, storage: FirebaseStorage, onFileOptionClick: () -> Unit, onFileClick: () -> Unit) {
    val context = LocalContext.current

    var bitmap by remember { mutableStateOf<Bitmap?>(null) }


    if (viewModel.selectedProject!!.projectStorageType  == CreateProject.StorageType.LOCAL.value){
        try {
            if (file.getThumbnailUri() != null){
                bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, file.getThumbnailUri())
            }
        }catch (e: java.lang.Exception){
            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
        }
    }else{
        if (file.rawData != null){
            bitmap = byteArrayToBitmap(file.rawData!!)
        }else{
            val decryptedFile = createImageFile(context)
            val encryptedFileUri: Uri = FileProvider.getUriForFile(context, "com.blueduck.annotator.provider", decryptedFile)
            val httpsReference = file.getThumbnailUrl()?.let { storage.getReferenceFromUrl(it) }
            httpsReference?.getFile(decryptedFile)
                ?.addOnSuccessListener {
                    // The file has been successfully downloaded
                    bitmap = if (viewModel.selectedProject!!.fileEncryptionPassword.isNotEmpty()){
                        val pass = EncryptionAndDecryption.decryptPassword(viewModel.selectedProject!!.fileEncryptionPassword)
                        println("fsfsdfsd $pass")
                        val imageByteArray = EncryptionAndDecryption.decrypt(pass, encryptedFileUri , context = context)
                        BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.size)
                    }else{
                        getBitmapFromUri(contentResolver = context.contentResolver, encryptedFileUri)
                    }

                    bitmap?.let {
                        file.rawData = bitmapToByteArray(it)
                        viewModel.updateFile(file)
                    }

                    if (decryptedFile.exists()) {
                        decryptedFile.delete()
                    }
                    val encryptedFile = File(encryptedFileUri.path!!)
                    if (encryptedFile.exists()){
                        encryptedFile.delete()
                    }
                }
                ?.addOnFailureListener {
                    // Handle any errors
                    if (decryptedFile.exists()) {
                        decryptedFile.delete()
                    }
                    val encryptedFile = File(encryptedFileUri.path!!)
                    if (encryptedFile.exists()){
                        encryptedFile.delete()
                    }
                }
        }
    }




    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .background(
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    shape = MaterialTheme.shapes.medium
                )
                .aspectRatio(1.2f),
            contentAlignment = Alignment.Center
        ) {
            bitmap?.let {
                Image(
                    bitmap = bitmap!!.asImageBitmap(),
                    contentDescription = "",
                    contentScale = ContentScale.FillBounds
                )
            }

            Icon(
                painter = painterResource(id = R.drawable.ic_play_circle),
                contentDescription = null,
                modifier = Modifier
                    .clickable {
                        onFileClick()
                    },
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }


        ConstraintLayout(modifier = Modifier) {
            val (name, morevert) = createRefs()
            Text(text = file.name, style = typography.bodyMedium,  modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 0.dp, end = 16.dp, bottom = 0.dp)
                .constrainAs(name) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    bottom.linkTo(parent.bottom)
                    end.linkTo(morevert.end)
                }, textAlign = TextAlign.Center)
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "folder menu action",
                modifier = Modifier
                    .clickable {
                        onFileOptionClick()
                    }
                    .constrainAs(morevert) {
                        top.linkTo(parent.top)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                    }
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowImageFiles(
    navController: NavHostController,
    project: Project,
    viewModel: HomeViewModel,
    deviceInfo: DeviceInfo,
    workManager: WorkManager,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()



    val storage = FirebaseStorage.getInstance()
    val database = FirebaseFirestore.getInstance()

    var showRenameFileDialog by remember { mutableStateOf(false) }
    var showDeleteFiletDialog by remember { mutableStateOf(false) }

    var showProgressIndicator by remember { mutableStateOf(false) }
    val files = if (project.projectStorageType == CreateProject.StorageType.LOCAL.value){
        viewModel.getAllFilesFromLocal(projectId = project.id).collectAsLazyPagingItems()
    }else{
        viewModel.getAllFiles(projectId = project.id).collectAsLazyPagingItems()
    }

    var isFileUploading by remember { mutableStateOf(false) }
    var progress by remember { mutableStateOf("0.0%") }

    val scrollState = rememberLazyGridState()

    var selectedImageIndex by remember { mutableIntStateOf(0) }

    var bottomSheetType by remember { mutableStateOf(BottomSheet.ADD_FILE) }



    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = SheetState(
            skipPartiallyExpanded = false, initialValue = SheetValue.Hidden
        )
    )

    val singlePhotoPickerLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.PickVisualMedia(),
            onResult = {
                it?.let { uri ->
                    context.grantUriPermission(context.packageName, uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)

                    when(viewModel.storageType){
                        CreateProject.StorageType.LOCAL.value -> {
                            val internalStorageFileUri = copyFileToInternalStorageAndRetrieveUri(uri, context)
                            internalStorageFileUri?.let {
                                val fileDetails = fileDetails(internalStorageFileUri, context)
                                if (fileDetails != null){
                                    val thumbnailUri = getThumbnailAndSaveToInternalStorage(internalStorageFileUri, context = context)
                                    // create file object and store it to the local storage
                                    println("sfdsfsd $thumbnailUri")
                                    val imageFile = MyFile(
                                        id = UUID.randomUUID().toString(),
                                        name = fileDetails.name,
                                        tags = arrayListOf(),
                                        size = fileDetails.size,
                                        createdAt = System.currentTimeMillis(),
                                        ownerId = project.ownerId,
                                        projectId = project.id,
                                        metaData = if (thumbnailUri != null) {
                                            val metadata = JSONObject()
                                            metadata.put(THUMBNAIL_URI, thumbnailUri.toString())
                                            metadata.toString()
                                        } else {
                                            ""
                                        },
                                        mimeType = "",
                                        fileUrl = internalStorageFileUri.toString(),
                                        rawData = null
                                    )
                                    viewModel.addFile(imageFile)
                                }
                            }
                        }
                        CreateProject.StorageType.CLOUD.value -> {

                            coroutineScope.launch {

                                val inputData = Data.Builder()
                                    .putString(ProjectProperties.OWNER_ID.value, project.ownerId)
                                    .putString(ProjectProperties.ID.value, project.id)
                                    .putString(ProjectProperties.FILE_ENCRYPTION_PASSWORD.value, project.fileEncryptionPassword)
                                    .putString(ProjectProperties.PROJECT_STORAGE_TYPE.value, project.projectStorageType)
                                    .putString(ProjectProperties.PROJECT_FILE_TYPE.value, project.projectFileType)
                                    .putString(FILE_URI, uri.toString())
                                    .build()
                                val uploadFileWorker = OneTimeWorkRequestBuilder<UploadFileWorker>().setInputData(inputData = inputData)
                                    .build()

                                workManager.enqueue(uploadFileWorker)
                            }

                        }
                        CreateProject.StorageType.DRIVE.value -> {

                        }
                    }
                }
            })

    val takePictureLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) { result: ActivityResult? ->
            val imageUriString = result?.data?.getStringExtra("image_uri")
            if (imageUriString.isNullOrEmpty()) {
                Toast.makeText(context, "Something Went Wrong", Toast.LENGTH_SHORT).show()
            } else {
                val uri = Uri.parse(imageUriString)

                context.grantUriPermission(context.packageName, uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)

                when(viewModel.storageType){
                    CreateProject.StorageType.LOCAL.value -> {
                        val internalStorageFileUri = copyFileToInternalStorageAndRetrieveUri(uri, context)
                        internalStorageFileUri?.let {
                            val fileDetails = fileDetails(internalStorageFileUri, context)
                            if (fileDetails != null){
                                val thumbnailUri = getThumbnailAndSaveToInternalStorage(internalStorageFileUri, context = context)
                                // create file object and store it to the local storage
                                println("sfdsfsd $thumbnailUri")
                                val imageFile = MyFile(
                                    id = UUID.randomUUID().toString(),
                                    name = fileDetails.name,
                                    tags = arrayListOf(),
                                    size = fileDetails.size,
                                    createdAt = System.currentTimeMillis(),
                                    ownerId = project.ownerId,
                                    projectId = project.id,
                                    metaData = if (thumbnailUri != null) {
                                        val metadata = JSONObject()
                                        metadata.put(THUMBNAIL_URI, thumbnailUri.toString())
                                        metadata.toString()
                                    } else {
                                        ""
                                    },
                                    mimeType = "",
                                    fileUrl = internalStorageFileUri.toString(),
                                    rawData = null
                                )
                                viewModel.addFile(imageFile)
                            }
                        }
                    }
                    CreateProject.StorageType.CLOUD.value -> {

                        val inputData = Data.Builder()
                            .putString(ProjectProperties.OWNER_ID.value, project.ownerId)
                            .putString(ProjectProperties.ID.value, project.id)
                            .putString(ProjectProperties.FILE_ENCRYPTION_PASSWORD.value, project.fileEncryptionPassword)
                            .putString(ProjectProperties.PROJECT_STORAGE_TYPE.value, project.projectStorageType)
                            .putString(ProjectProperties.PROJECT_FILE_TYPE.value, project.projectFileType)
                            .putString(FILE_URI, uri.toString())
                            .build()
                        val uploadFileWorker = OneTimeWorkRequestBuilder<UploadFileWorker>().setInputData(inputData = inputData)
                            .build()

                        workManager.enqueue(uploadFileWorker)

                    }
                    CreateProject.StorageType.DRIVE.value -> {

                    }
                }

            }
        }

    Scaffold(modifier = Modifier.fillMaxSize(), floatingActionButton = {
        if (!bottomSheetScaffoldState.bottomSheetState.isVisible  && scrollState.isScrollingUp()) {
            FloatingActionButton(onClick = {
                bottomSheetType = BottomSheet.ADD_FILE
                coroutineScope.launch {
                    bottomSheetScaffoldState.bottomSheetState.expand()
                }
            }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "")
            }
        }
    }) {paddingValues ->

        BottomSheetScaffold(modifier = Modifier.padding(top = paddingValues.calculateTopPadding()),
            sheetDragHandle = {},
            sheetSwipeEnabled = false,
            scaffoldState = bottomSheetScaffoldState,
            sheetContent = {
                if (bottomSheetType == BottomSheet.ADD_FILE){
                    AddFile(onChooseFromGallery = {
                        singlePhotoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        coroutineScope.launch {
                            bottomSheetScaffoldState.bottomSheetState.hide()
                        }
                    }, onTakePicture = {
                        val intent = Intent(context, CameraActivity::class.java)
                        intent.putExtra(Constant.ARG_CAPTURE_IMAGE, Constant.ARG_CAPTURE_IMAGE)
                        takePictureLauncher.launch(intent)
                        coroutineScope.launch {
                            bottomSheetScaffoldState.bottomSheetState.hide()
                        }
                    }, onCancel = {
                        coroutineScope.launch {
                            bottomSheetScaffoldState.bottomSheetState.hide()
                        }
                    })
                }
                if (bottomSheetType == BottomSheet.FILE_OPTION){
                    FileMoreVertOption(navController,
                        onDeleteClick = { showDeleteFiletDialog = true },
                        onRenameClick = { showRenameFileDialog = true}) { coroutineScope.launch { bottomSheetScaffoldState.bottomSheetState.hide() } }
                }
            }) { pv ->
            ConstraintLayout(modifier = Modifier
                .fillMaxSize()
                .padding(all = 8.dp)) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(top = pv.calculateTopPadding())
                ) {

                    if (showRenameFileDialog) {
                        RenameFileDialog(
                            files[selectedImageIndex]!!.name,
                            onCloseClick = {showRenameFileDialog = false },
                            onRenameClick = { newName ->
                                showRenameFileDialog = false
                                viewModel.renameFile(newName, files[selectedImageIndex]!!.id) { result ->
                                    when(result){
                                        is Response.Loading -> { showProgressIndicator = true }
                                        is Response.Success -> {
                                            showProgressIndicator = false
                                        }
                                        is Response.Failure -> {
                                            showProgressIndicator = false
                                            Toast.makeText(context, result.e.message, Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            }
                        )
                    }

                    if (showDeleteFiletDialog){
                        DeleteFileDialog(
                            onCloseClick = { showDeleteFiletDialog = false },
                            onDeleteClick = {
                                showDeleteFiletDialog = false
                                viewModel.deleteFile(files[selectedImageIndex]!!.id) { result ->
                                    when(result){
                                        is Response.Loading -> { showProgressIndicator = true }
                                        is Response.Success -> {
                                            showProgressIndicator = false
                                        }
                                        is Response.Failure -> {
                                            showProgressIndicator = false
                                            Toast.makeText(context, result.e.message, Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            }
                        )
                    }


                    if (files.itemCount == 0){
                        Text(text = "No files", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                    }else{
                        LazyVerticalGrid(
                            modifier = Modifier.fillMaxSize(),
                            columns = GridCells.Fixed(if (deviceInfo.isMobile) 2 else 4 ),
                            state = scrollState
                        ) {
                            items(files.itemCount) { index ->
                                files[index]?.let { file ->
                                    ImageItem(file = file, project.fileEncryptionPassword, storage, viewModel,
                                        onFileOptionClick = {
                                            viewModel.selectedFile = file
                                            bottomSheetType = BottomSheet.FILE_OPTION
                                            coroutineScope.launch {
                                                bottomSheetScaffoldState.bottomSheetState.expand()
                                            }
                                            selectedImageIndex = index
                                        }, onFileClick = {
                                            viewModel.selectedFile = file
                                            viewModel.files = files.itemSnapshotList.items
                                            navController.navigate(Screen.SingleFile.route)
                                            bottomSheetType = BottomSheet.ADD_FILE
                                            coroutineScope.launch {
                                                bottomSheetScaffoldState.bottomSheetState.hide()
                                            }
                                            selectedImageIndex = index
                                            //isOverlayVisible = true
                                        })
                                }
                            }
                        }
                    }
                }
                if (isFileUploading){
                    UploadingProgressDialog(progress = progress)
                }

                if (showProgressIndicator) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(all = 16.dp), horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}



@Composable
fun ImageItem(file: MyFile, password: String, storage: FirebaseStorage, viewModel: HomeViewModel, onFileOptionClick: () -> Unit, onFileClick: () -> Unit) {
    val context = LocalContext.current
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }

    if (viewModel.selectedProject!!.projectStorageType  == CreateProject.StorageType.LOCAL.value){
        try {
            if (file.getThumbnailUri() != null){
                 bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, file.getThumbnailUri())
            }
        }catch (e: java.lang.Exception){
            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
        }
    }else{
        if (file.rawData != null){
            println("fsdfdsfkk")
            bitmap = byteArrayToBitmap(file.rawData!!)
        }else{
            val decryptedFile = createImageFile(context )
            val encryptedFileUri: Uri = FileProvider.getUriForFile(context, "com.blueduck.annotator.provider", decryptedFile)
            val httpsReference = storage.getReferenceFromUrl(file.fileUrl)
            httpsReference.getFile(decryptedFile)
                .addOnSuccessListener {
                    // The file has been successfully downloaded
                    bitmap = if (password.isNotEmpty()){
                        val imageByteArray = EncryptionAndDecryption.decrypt(EncryptionAndDecryption.decryptPassword(password), encryptedFileUri , context = context)
                        BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.size)
                    }else{
                        getBitmapFromUri(contentResolver = context.contentResolver, encryptedFileUri)
                    }

                    bitmap?.let {
                        file.rawData = bitmapToByteArray(it)
                        viewModel.updateFile(file)
                    }

                    if (decryptedFile.exists()) {
                        decryptedFile.delete()
                    }
                    val encryptedFile = File(encryptedFileUri.path!!)
                    if (encryptedFile.exists()){
                        encryptedFile.delete()
                    }
                }
                .addOnFailureListener {
                    // Handle any errors
                    if (decryptedFile.exists()) {
                        decryptedFile.delete()
                    }
                    val encryptedFile = File(encryptedFileUri.path!!)
                    if (encryptedFile.exists()){
                        encryptedFile.delete()
                    }
                }
        }
    }




    Column {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .aspectRatio(1.2f),
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        ) {
            if (bitmap != null){
                Image(
                    bitmap = bitmap!!.asImageBitmap(),
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable {
                            onFileClick()
                        }
                )
            }
        }

        ConstraintLayout(modifier = Modifier) {
            val (name, morevert) = createRefs()
            Text(text = file.name, style = typography.bodyMedium,  modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp, 0.dp, 16.dp, 0.dp)
                .constrainAs(name) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    bottom.linkTo(parent.bottom)
                    end.linkTo(morevert.end)
                }, textAlign = TextAlign.Center)
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "folder menu action",
                modifier = Modifier
                    .clickable {
                        onFileOptionClick()
                    }
                    .constrainAs(morevert) {
                        top.linkTo(parent.top)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                    }
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
    }

 }





@Composable
fun AddFile(
    onChooseFromGallery: () -> Unit, onTakePicture: () -> Unit, onCancel: () -> Unit
) {


    Column(
        modifier = Modifier
            .clip(
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            )
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Select an Option",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(24.dp))
        OptionItem(text = "Choose from Gallery",
            icon = painterResource(id = R.drawable.ic_gallery),
            onClick = { onChooseFromGallery() })
        OptionItem(text = "Take from Camera",
            icon = painterResource(id = R.drawable.ic_camera),
            onClick = { onTakePicture() })
        Spacer(modifier = Modifier.height(24.dp))
        OptionItem(text = "Cancel", icon = null, onClick = { onCancel() })
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun OptionItem(
    text: String, icon: Painter?, onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp)
    ) {
        icon?.let {
            Icon(
                painter = icon,
                contentDescription = null,
                modifier = Modifier.padding(end = 8.dp)
            )
        }
        Text(text = text, fontSize = 16.sp)
    }
}


@Composable
fun UploadingProgressDialog(progress: String) {
    Column(modifier = Modifier
        .fillMaxSize()
        .background(color = Color.Transparent),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier
                .size(150.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(10.dp),
            color = MaterialTheme.colorScheme.onSurface
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 8.dp,
                    modifier = Modifier.size(50.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = progress,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 20.sp
                )
            }
        }

    }
 }