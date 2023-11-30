package com.blueduck.annotator.screens.bottombar.home

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.navigation.NavHostController
import com.blueduck.annotator.DeviceInfo
import com.blueduck.annotator.R
import com.blueduck.annotator.VideoPlayerActivity
import com.blueduck.annotator.encryption.EncryptionAndDecryption
import com.blueduck.annotator.enums.CreateProject
import com.blueduck.annotator.util.Constant
import com.blueduck.annotator.util.bitmapToByteArray
import com.blueduck.annotator.util.byteArrayToBitmap
import com.blueduck.annotator.util.createImageFile
import com.blueduck.annotator.util.createPdfFile
import com.blueduck.annotator.util.formatTime
import com.blueduck.annotator.util.getBitmapFromUri
import com.blueduck.annotator.util.getDefaultImagePlaceHolderAsImageBitmap
import com.google.firebase.storage.FirebaseStorage
import com.rizzi.bouquet.ResourceType
import com.rizzi.bouquet.VerticalPDFReader
import com.rizzi.bouquet.rememberVerticalPdfReaderState
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable
import java.io.File
import java.lang.Exception

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SingleFileScreen(
    navController: NavHostController,
    viewModel: HomeViewModel,
    deviceInfo: DeviceInfo
) {
    val context = LocalContext.current
    val storage = FirebaseStorage.getInstance()
    val project = viewModel.selectedProject






    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = project!!.name) },
                navigationIcon = { Row(modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .clickable { navController.popBackStack() }) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "")
                }  },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                )
            )
        }
    ) { p ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(p)) {
            when(project!!.projectFileType){
                CreateProject.FileType.IMAGE.value -> {
                    ShowSingleImageFile(viewModel, storage)
                }
                CreateProject.FileType.VIDEO.value -> {
                    ShowSingleVideoFile(viewModel, storage)
                }
                CreateProject.FileType.AUDIO.value -> {
                    ShowSingleAudioFile(viewModel, storage)
                }
                CreateProject.FileType.TEXT.value -> {
                    ShowSingleTextFile(viewModel, storage)
                }
            }

        }
    }

}

@Composable
fun ShowSingleTextFile(viewModel: HomeViewModel, storage: FirebaseStorage) {
    val context = LocalContext.current

    val file = viewModel.selectedFile

    fun isUri(input: String): Boolean {
        return input.contains("content:")
    }

    var decryptedFileUri by remember { mutableStateOf<Uri?>(null) }

    val project = viewModel.selectedProject



    if (!isUri(file!!.fileUrl)){
        if (project!!.fileEncryptionPassword.isNotEmpty()){
            val encryptedFile = createPdfFile(context )
            val encryptedFileUri = FileProvider.getUriForFile(context, "${context.applicationContext.packageName}${Constant.PROVIDER}", encryptedFile)
            val httpsReference = storage.getReferenceFromUrl(file.fileUrl)
            httpsReference.getFile(encryptedFile).addOnSuccessListener {
                val decryptedFile = createPdfFile(context = context)
                 EncryptionAndDecryption.decryptPdfFile(EncryptionAndDecryption.decryptPassword(project.fileEncryptionPassword) , encryptedFile, decryptedFile)
                decryptedFileUri = FileProvider.getUriForFile(context, "${context.applicationContext.packageName}${Constant.PROVIDER}", decryptedFile)
            }.addOnFailureListener{
                Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
            }
        }
    }





    if (decryptedFileUri != null){
        val pdfState = rememberVerticalPdfReaderState(
            resource =    ResourceType.Local(decryptedFileUri!!),
            isZoomEnable = true
        )
        VerticalPDFReader(
            state = pdfState,
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.Gray)
        )
    }else{
        if (project!!.fileEncryptionPassword.isEmpty()){
            val pdfState = rememberVerticalPdfReaderState(
                resource =    if (isUri(file.fileUrl)){
                    ResourceType.Local(Uri.parse(file.fileUrl))
                }else{
                    ResourceType.Remote(file.fileUrl)
                },
                isZoomEnable = true
            )

            VerticalPDFReader(
                state = pdfState,
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color.Gray)
            )
        }
    }

   /* val pdfState = rememberVerticalPdfReaderState(
        resource =   if (isUri(file.fileUrl))  ResourceType.Local(Uri.parse(file.fileUrl)) else if(project!!.fileEncryptionPassword.isNotEmpty()) {if (decryptedFileUri == null) ResourceType.Remote(file.fileUrl) else ResourceType.Local(decryptedFileUri!!)} else ResourceType.Remote(file.fileUrl),
        isZoomEnable = true
    )
    VerticalPDFReader(
        state = pdfState,
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Gray)
    )*/
}


@Composable
fun BottomPlayerTab(
    viewModel: HomeViewModel,
    onProgressChanged: (Long) -> Unit,
    onPlayPauseClick: () -> Unit,
    onPlayPreviousAudio: () -> Unit,
    onPlayNextAudio: () -> Unit
) {
    val playbackStateValue = viewModel.playbackState.collectAsState(
        initial = PlaybackState(0L, 0L)
    ).value
    val currentMediaProgress = playbackStateValue.currentPlaybackPosition.toFloat()
    val currentTrackDuration = playbackStateValue.currentTrackDuration.toFloat()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.primary,
            )
            .padding(all = 16.dp)
    ) {


        Row(
            verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = { onPlayPreviousAudio()  }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_previous),
                    contentDescription = "",
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }

            if (viewModel.playerState == PlayerStates.STATE_BUFFERING){
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(size = 48.dp)
                        .padding(all = 9.dp),
                    color = MaterialTheme.colorScheme.onPrimary)
            }else{
                IconButton(onClick = { onPlayPauseClick()  }) {
                    Icon(
                        painter = if (viewModel.isPlaying) painterResource(id = R.drawable.ic_pause) else painterResource(id = R.drawable.ic_play),
                        contentDescription = "",
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
            IconButton(onClick = { onPlayNextAudio()  }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_next),
                    contentDescription = "",
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        Slider(
            value = currentMediaProgress,
            onValueChange = {
                onProgressChanged(it.toLong())
            },
            onValueChangeFinished = {},
            valueRange = 0f..currentTrackDuration,
            modifier = Modifier.fillMaxWidth(),
            colors = SliderDefaults.colors(
                activeTrackColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        )

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = playbackStateValue.currentPlaybackPosition.formatTime(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimary
            )
            Text(
                text = playbackStateValue.currentTrackDuration.formatTime(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = viewModel.selectedAudioFile!!.name,
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}

@Composable
fun ShowSingleAudioFile(viewModel: HomeViewModel, storage: FirebaseStorage) {
    val context = LocalContext.current

    val selectedFile = viewModel.selectedFile
    val files = viewModel.files
    var selectedFileIndex by remember { mutableIntStateOf(files.indexOf(selectedFile)) }

    Box(modifier = Modifier.fillMaxSize(),  contentAlignment = Alignment.Center) {
        if (viewModel.playerState == PlayerStates.STATE_END){
            val file = files[selectedFileIndex]
            viewModel.playNextAudio(file = file, context = context)
        }
        BottomPlayerTab(
            viewModel,
            onProgressChanged = {
                viewModel.seekTo(it)
            },
            onPlayPreviousAudio = {
                if (selectedFileIndex > 0){
                    selectedFileIndex--
                    val file = files[selectedFileIndex]
                    viewModel.playNextAudio(file = file, context = context)
                }
            },
            onPlayPauseClick = {
                viewModel.playPauseAudio()
            },
            onPlayNextAudio = {
                if (selectedFileIndex < (files.size - 1)){
                    selectedFileIndex++
                    val file = files[selectedFileIndex]
                    viewModel.playNextAudio(file = file, context = context)
                }
            }
        )
    }

}



@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ShowSingleVideoFile(viewModel: HomeViewModel, storage: FirebaseStorage) {
    val context = LocalContext.current

    val selectedFile = viewModel.selectedFile
    val files = viewModel.files
    val selectedFileIndex by remember { mutableIntStateOf(files.indexOf(selectedFile)) }

    val pagerState = rememberPagerState(
        pageCount = { files.size },
        initialPage = selectedFileIndex
    )

    var bitmap by remember { mutableStateOf<Bitmap?>(null) }


    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        HorizontalPager(state = pagerState) { page ->
            val file = files[page]


            if (viewModel.selectedProject!!.projectStorageType  == CreateProject.StorageType.LOCAL.value){
                try {
                    if (file.getThumbnailUri() != null){
                        bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, file.getThumbnailUri())
                    }
                }catch (e: Exception){
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


            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    ),
                contentAlignment = Alignment.Center
            ){

                Image(
                    bitmap = bitmap!!.asImageBitmap(),
                    contentDescription = "",
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.FillBounds
                )

                Icon(
                    imageVector = Icons.Default.PlayCircleOutline,
                    contentDescription = null,
                    modifier = Modifier.clickable {
                        val selectedProjectPassword = viewModel.selectedProject?.fileEncryptionPassword ?: ""
                        val intent = Intent(context, VideoPlayerActivity::class.java)
                        intent.putExtra("video_url", file.fileUrl)
                        intent.putExtra("selected_project_password", selectedProjectPassword)
                        context.startActivity(intent)
                    },
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }


    }

}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ShowSingleImageFile(viewModel: HomeViewModel, storage: FirebaseStorage) {
    val context = LocalContext.current
    val file = viewModel.selectedFile
    val files = viewModel.files
    val selectedFileIndex by remember { mutableIntStateOf(files.indexOf(file)) }

    var imageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }

    val pagerState = rememberPagerState(
        pageCount = { files.size },
        initialPage = selectedFileIndex
    )

    fun byteArrayToBitmap(byteArray: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }


    Column(modifier = Modifier.fillMaxSize()) {
        HorizontalPager( state = pagerState) { page ->
            val selectedFile = files[page]
            val zoomState = rememberZoomState(contentSize = Size.Zero)

            if (CreateProject.StorageType.LOCAL.value == viewModel.storageType){
                imageBitmap = getBitmapFromUri(context.contentResolver, selectedFile.getThumbnailUri()!!)!!.asImageBitmap()
            }else{
                if (selectedFile.rawData == null){
                    val decryptedFile = createImageFile(context)
                    val encryptedFileUri: Uri = FileProvider.getUriForFile(context, "com.blueduck.annotator.provider", decryptedFile)
                    val httpsReference = storage.getReferenceFromUrl(selectedFile.fileUrl)
                    httpsReference.getFile(decryptedFile)
                        .addOnSuccessListener {
                            // The file has been successfully downloaded
                            val bitmap = if (viewModel.selectedProject!!.fileEncryptionPassword.isNotEmpty()){
                                val imageByteArray = EncryptionAndDecryption.decrypt(
                                    EncryptionAndDecryption.decryptPassword(viewModel.selectedProject!!.fileEncryptionPassword), encryptedFileUri , context = context)
                                BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.size)
                            }else{
                                getBitmapFromUri(contentResolver = context.contentResolver, encryptedFileUri)
                            }

                            bitmap?.let {
                                selectedFile.rawData = bitmapToByteArray(bitmap)
                                viewModel.updateFile(selectedFile)
                                imageBitmap = bitmap.asImageBitmap()
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
                }else{
                    imageBitmap = byteArrayToBitmap(selectedFile.rawData!!).asImageBitmap()
                }

            }


            Image(
                bitmap = imageBitmap ?: context.getDefaultImagePlaceHolderAsImageBitmap(),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .zoomable(zoomState)
            )
        }

    }

}
