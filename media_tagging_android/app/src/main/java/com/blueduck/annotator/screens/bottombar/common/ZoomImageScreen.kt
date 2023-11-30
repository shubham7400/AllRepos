package com.blueduck.annotator.screens.bottombar.common

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.paging.compose.collectAsLazyPagingItems
import com.blueduck.annotator.R
import com.blueduck.annotator.model.MyFile
import com.blueduck.annotator.screens.bottombar.home.HomeViewModel
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ZoomImageScreen(
    navController: NavHostController,
    viewModel: HomeViewModel,
) {
      val file = remember { mutableStateOf<MyFile?>(null) }

    viewModel.getFileByFileId("fileId"){
        file.value = it
    }

    val project = viewModel.selectedProject!!

    var itemCount by remember {
        mutableIntStateOf(0)
    }

    val files = viewModel.getAllFiles(projectId = project.id).collectAsLazyPagingItems()


    fun byteArrayToBitmap(byteArray: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }

    var selectedImageIndex by remember { mutableIntStateOf(0) }


    val pagerState = rememberPagerState(
        pageCount = { 4 },
        initialPage = 0
    )

    Scaffold(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            println("fsdfsd ${files.itemCount}  ${project.id}")
            if (files.itemCount > 0){
                HorizontalPager(state = pagerState) { page ->
                    val f = files[page]
                    val zoomState = rememberZoomState(contentSize = Size.Zero)
                    Image(
                        bitmap = byteArrayToBitmap(f!!.rawData!!).asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .zoomable(zoomState)
                    )
                }

            }
            IconButton(
                onClick = {
                    navController.popBackStack()
                },
                modifier = Modifier
                    .padding(16.dp)
                    .size(36.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_camera),
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }
    }

 }

