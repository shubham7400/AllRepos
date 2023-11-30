package com.blueduck.annotator.screens.bottombar.home

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavHostController
import androidx.paging.compose.collectAsLazyPagingItems
import com.blueduck.annotator.DeviceInfo
import com.blueduck.annotator.dialog.DeleteProjectDialog
import com.blueduck.annotator.dialog.LockUnlockProjectDialog
import com.blueduck.annotator.dialog.OpenProjectDialog
import com.blueduck.annotator.dialog.RenameProjectDialog
import com.blueduck.annotator.encryption.EncryptionAndDecryption
import com.blueduck.annotator.enums.BottomSheet
import com.blueduck.annotator.enums.CreateProject
import com.blueduck.annotator.model.Project
import com.blueduck.annotator.navigation.Screen
import com.blueduck.annotator.preferences.getUser
import com.blueduck.annotator.screens.bottombar.common.ProjectView
import com.blueduck.annotator.util.Response
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriveProjectScreen(navController: NavHostController, viewModel: HomeViewModel, deviceInfo: DeviceInfo, openDrawer: () -> Unit = {}) {

    val context = LocalContext.current
    val user = context.getUser()

    var bottomSheetType by remember { mutableStateOf(BottomSheet.ADD_PROJECT) }

    val coroutineScope = rememberCoroutineScope()

    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = SheetState(
            skipPartiallyExpanded = false,
            initialValue = SheetValue.Hidden,
        )
    )

    val projects = viewModel.getAllProjects(user!!.id).collectAsLazyPagingItems()

    var selectedProject by remember { mutableStateOf<Project?>(null) }

    // State to manage the visibility of the profile dialog
    var showRenameProjectDialog by remember { mutableStateOf(false) }
    var showDeleteProjectDialog by remember { mutableStateOf(false) }
    var showLockProjectDialog by remember { mutableStateOf(false) }
    var showOpenProjectDialog by remember { mutableStateOf(false) }

    var showProgressIndicator by remember { mutableStateOf(false) }


    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            if (!bottomSheetScaffoldState.bottomSheetState.isVisible) {
                FloatingActionButton(onClick = {
                    coroutineScope.launch {
                        bottomSheetType = BottomSheet.ADD_PROJECT
                        bottomSheetScaffoldState.bottomSheetState.expand()
                    }
                }) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "")
                }
            }
        }
    ) {
        BottomSheetScaffold(
            modifier = Modifier.padding(it),
            scaffoldState = bottomSheetScaffoldState,
            sheetSwipeEnabled = false,
            sheetDragHandle = {},
            sheetContent = {
                // here is your bottom sheet content
                if (bottomSheetType == BottomSheet.ADD_PROJECT){
                    AddProject(
                        hideSheet = {
                            coroutineScope.launch {
                                bottomSheetScaffoldState.bottomSheetState.hide()
                            }
                        },
                        onCreateProjectClick = { project ->
                            viewModel.addProject(project) { result ->
                                coroutineScope.launch {
                                    bottomSheetScaffoldState.bottomSheetState.hide()
                                }
                                when (result) {
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
                        },
                        viewModel = viewModel
                    )
                }
                if (bottomSheetType == BottomSheet.PROJECT_OPTION){
                    ProjectMoreVertOption(navController, selectedProject, viewModel,
                        onShareClick = {
                            coroutineScope.launch { bottomSheetScaffoldState.bottomSheetState.hide() }
                            bottomSheetType = BottomSheet.SHARE_PROJECT
                            coroutineScope.launch { bottomSheetScaffoldState.bottomSheetState.expand() }
                        },
                        onLockProjectClick = {
                            showLockProjectDialog = true
                        },
                        onDeleteClick = { showDeleteProjectDialog = true },
                        onRenameClick = { showRenameProjectDialog = true}) { coroutineScope.launch { bottomSheetScaffoldState.bottomSheetState.hide() } }
                }
                if (bottomSheetType == BottomSheet.SHARE_PROJECT){
                    ShareProject(onCloseSheetClick = {
                        coroutineScope.launch { bottomSheetScaffoldState.bottomSheetState.hide() }
                    })
                }
            },
            sheetPeekHeight = 0.dp
        ) {
            ConstraintLayout(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {



                    if (showRenameProjectDialog) {
                        RenameProjectDialog(
                            viewModel.selectedProject!!.name,
                            onCloseClick = {showRenameProjectDialog = false },
                            onRenameClick = { newProjectName ->
                                showRenameProjectDialog = false
                                viewModel.renameProject(newProjectName) { result ->
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

                    if (showDeleteProjectDialog){
                        DeleteProjectDialog(
                            onCloseClick = { showDeleteProjectDialog = false },
                            onDeleteClick = {
                                showDeleteProjectDialog = false
                                viewModel.deleteProject() { result ->
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

                    if (showOpenProjectDialog){
                        OpenProjectDialog(
                            viewModel.selectedProject!!,
                            onCloseClick = { showOpenProjectDialog = false },
                            onAuthSuccess = { result ->
                                showOpenProjectDialog = false
                                if (result){
                                    navController.navigate(Screen.Files.route)
                                }
                            }
                        )
                    }

                    if (showLockProjectDialog){
                        LockUnlockProjectDialog(
                            onCloseClick = { showLockProjectDialog = false },
                            lockProject = { password ->
                                val encryptedPassword = EncryptionAndDecryption.encryptPassword(password)
                                showLockProjectDialog = false
                                viewModel.updateProjectPassword(encryptedPassword) { result ->
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
                            },
                            unlockProject = { password ->
                                showLockProjectDialog = false
                                viewModel.updateProjectPassword(password) { result ->
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
                            },
                            if (viewModel.selectedProject!!.projectLockPassword.isEmpty()){
                                viewModel.selectedProject!!.projectLockPassword
                            }else{
                                EncryptionAndDecryption.decryptPassword(viewModel.selectedProject!!.projectLockPassword)
                            }
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .clip(shape = MaterialTheme.shapes.extraLarge)
                            .background(color = MaterialTheme.colorScheme.surfaceVariant),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menu icon",
                            modifier = Modifier
                                .padding(start = 16.dp)
                                .clickable {
                                    openDrawer()
                                },
                            tint = MaterialTheme.colorScheme.onSurface
                        )

                        Text(
                            text = "Search",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier
                                .padding(start = 16.dp)
                                .clickable { navController.navigate(Screen.Search.route) }
                        )
                    }
                    if (viewModel.storageType == CreateProject.StorageType.DRIVE.value){
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(text = "Comming Soon", style = MaterialTheme.typography.headlineSmall)
                        }
                    }else{
                        LazyVerticalGrid(
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(8.dp),
                            columns = GridCells.Fixed(if (deviceInfo.isMobile) 2 else 4),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(projects.itemCount) { index ->
                                projects[index]?.let { project ->
                                    ProjectView(project, onMoreVertClick = {
                                        viewModel.selectedProject = project
                                        selectedProject = project
                                        coroutineScope.launch {
                                            bottomSheetType = BottomSheet.PROJECT_OPTION
                                            bottomSheetScaffoldState.bottomSheetState.expand()
                                        }
                                    }, onProjectClick = {
                                        viewModel.selectedProject = project
                                        if (viewModel.selectedProject!!.projectLockPassword.isEmpty()){
                                            navController.navigate(Screen.Files.route)
                                        }else{
                                            showOpenProjectDialog = true
                                        }
                                    })
                                }
                            }
                        }
                    }


                }

                if (showProgressIndicator){
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            }
        }

    }
}