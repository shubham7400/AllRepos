package com.blueduck.annotator.screens.bottombar.home

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavHostController
import androidx.paging.compose.collectAsLazyPagingItems
import com.blueduck.annotator.DeviceInfo
import com.blueduck.annotator.encryption.EncryptionAndDecryption
import com.blueduck.annotator.R
import com.blueduck.annotator.dialog.DeleteProjectDialog
import com.blueduck.annotator.dialog.LockUnlockProjectDialog
import com.blueduck.annotator.dialog.OpenProjectDialog
import com.blueduck.annotator.dialog.RenameProjectDialog
import com.blueduck.annotator.enums.BottomSheet
import com.blueduck.annotator.enums.CreateProject
import com.blueduck.annotator.model.Project
import com.blueduck.annotator.model.User
import com.blueduck.annotator.navigation.Screen
import com.blueduck.annotator.preferences.getUser
import com.blueduck.annotator.screens.bottombar.common.ProjectView
import com.blueduck.annotator.util.Response
import kotlinx.coroutines.launch
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CloudProjectScreen(navController: NavHostController, viewModel: HomeViewModel, deviceInfo: DeviceInfo, openDrawer: () -> Unit = {}) {

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

@Composable
fun ShareProject(onCloseSheetClick: () -> Unit) {
    var showAccessView by remember { mutableStateOf(false) }
    var memberEmail by remember { mutableStateOf("") }
    var members by remember { mutableStateOf<List<User>>(emptyList()) }

     Column(modifier = Modifier.fillMaxWidth()) {
        Box(modifier = Modifier
            .fillMaxWidth()
            .padding(all = 16.dp)) {
            Text(text = "Add Members", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
            Icon(imageVector = Icons.Default.Close, contentDescription = null, modifier = Modifier.clickable { onCloseSheetClick() })
        }
         Divider()
         if(showAccessView){

         }else{
             OutlinedTextField(
                 value = memberEmail,
                 onValueChange = { memberEmail = it },
                 placeholder = { Text(text = "Enter gmail id...")},
                 leadingIcon = { Icons.Default.Search },
                 trailingIcon = {
                     if (memberEmail.isNotEmpty()){
                         Icon(
                             imageVector = Icons.Default.PersonAdd,
                             contentDescription = null,
                             modifier = Modifier.clickable {
                                val u = User(
                                    id = UUID.randomUUID().toString(),
                                    name = UUID.randomUUID().toString(),
                                    profileImage = "",
                                    email = memberEmail,
                                    createdAt = 0,
                                    lastSeen = 0,
                                    userStatus = false,
                                    userType = ""
                                )
                                 members = members + u
                                 memberEmail = ""
                             }
                         )
                     }
                 },
                 shape = MaterialTheme.shapes.extraLarge,
                 modifier = Modifier
                     .fillMaxWidth()
                     .padding(all = 16.dp)
             )

             members.forEach {
                 MemberItem(it)
             }



             Row(modifier = Modifier
                 .fillMaxWidth()
                 .padding(16.dp),
                 verticalAlignment = Alignment.CenterVertically,
                 horizontalArrangement = Arrangement.SpaceBetween) {
                 Row(verticalAlignment = Alignment.CenterVertically,
                     horizontalArrangement = Arrangement.SpaceBetween) {
                     Icon(imageVector = Icons.Default.Person, contentDescription = null, modifier = Modifier.clip( shape = MaterialTheme.shapes.extraLarge))
                     Spacer(modifier = Modifier.width(16.dp))
                     Text(text = "Set this user as")
                 }
                 Icon(imageVector = Icons.Default.ArrowForwardIos, contentDescription = null, modifier = Modifier.clickable { showAccessView = true })
             }
         }
     }
}

@Composable
fun MemberItem(member: User) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .background(color = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp))
        .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween) {
        Row(verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Image(painter = painterResource(id = R.drawable.ic_app_logo), contentDescription = null, modifier = Modifier
                .clip(shape = MaterialTheme.shapes.extraLarge)
                .size(50.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = member.email,)
            Spacer(modifier = Modifier.width(16.dp))
        }
        ElevatedButton(
            onClick = {   },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text(text = "Remove")
        }
    }
}

@Composable
fun ProjectMoreVertOption(navController: NavHostController,
                          selectedProject: Project?,
                          viewModel: HomeViewModel,
                          onRenameClick: () -> Unit,
                          onLockProjectClick: () -> Unit,
                          onDeleteClick: () -> Unit,
                          onShareClick: () -> Unit,
                          onCloseSheetClick: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .clickable { onCloseSheetClick() }
                .padding(all = 16.dp)
        ) {
            Icon(imageVector = Icons.Default.Close, contentDescription = null)
        }

        Row(
            modifier = Modifier
                .clickable {
                    onRenameClick()
                    onCloseSheetClick()
                }
                .fillMaxWidth()
                .padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = null,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Text(text = "Rename Project", style = MaterialTheme.typography.titleMedium)
        }

        Row(
            modifier = Modifier
                .clickable {
                    onLockProjectClick()
                    onCloseSheetClick()
                }
                .fillMaxWidth()
                .padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            println("dfsfsfsdfsd  ${viewModel.selectedProject!!.projectLockPassword}")
            Icon(
                imageVector = if (selectedProject == null){
                    if (viewModel.selectedProject!!.projectLockPassword.isEmpty()) Icons.Default.Lock else Icons.Default.LockOpen
                } else{
                    if (selectedProject.projectLockPassword.isEmpty()) Icons.Default.Lock else Icons.Default.LockOpen
                      },
                contentDescription = null,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Text(text = if (selectedProject == null) {
                if (viewModel.selectedProject!!.projectLockPassword.isEmpty()) "Lock" else "Unlock"
            }else{
                if (selectedProject.projectLockPassword.isEmpty()) "Lock" else "Unlock"
                 }, style = MaterialTheme.typography.titleMedium)
        }

        if (viewModel.storageType == CreateProject.StorageType.CLOUD.value){
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onShareClick()
                    }
                    .padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = null,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Text(text = "Share", style = MaterialTheme.typography.titleMedium)
            }
        }

        Row(
            modifier = Modifier
                .clickable {
                    onDeleteClick()
                    onCloseSheetClick()
                }
                .fillMaxWidth()
                .padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = null,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Text(text = "Delete", style = MaterialTheme.typography.titleMedium)
        }

        Row(
            modifier = Modifier
                .clickable { navController.navigate(Screen.ProjectDetail.route) }
                .fillMaxWidth()
                .padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Text(text = "Details & Activity", style = MaterialTheme.typography.titleMedium)
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}












@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProject(hideSheet: () -> Unit, onCreateProjectClick: (Project) -> Unit, viewModel: HomeViewModel) {
    val context = LocalContext.current
    val user = LocalContext.current.getUser()
    var currentPage by remember { mutableIntStateOf(0) }

    var projectName by remember { mutableStateOf("") }
    var selectedFileType by remember { mutableStateOf(CreateProject.FileType.IMAGE.value) }
    var selectedProjectType by remember { mutableStateOf(CreateProject.ProjectType.TAG.value) }
    var selectedStorageType by remember { mutableStateOf(viewModel.storageType ?: CreateProject.StorageType.CLOUD.value) }
    var selectedPrivacyType by remember { mutableStateOf(CreateProject.PrivacyType.PASSWORD.value) }
    var password by remember { mutableStateOf("") }

    var expanded by remember { mutableStateOf(false) }
    var selectedFileTypeIndex by remember { mutableIntStateOf(0) }
    val fileTypeOptions = listOf(CreateProject.FileType.IMAGE.value, CreateProject.FileType.VIDEO.value, CreateProject.FileType.AUDIO.value, CreateProject.FileType.TEXT.value)

    var selectedSecurityOption by remember { mutableStateOf(SecurityOption.NONE) }

    val onCancelClick: () -> Unit = {

        projectName = ""
        selectedFileType = ""
        selectedProjectType = ""
        selectedStorageType = ""
        password = ""
        currentPage = 0

        hideSheet()
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "New Project",
            color = MaterialTheme.colorScheme.surfaceTint,
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.size(30.dp))
        Text(
            text = "Title",
            modifier = Modifier.align(alignment = Alignment.Start),
            color = MaterialTheme.colorScheme.surfaceTint,
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.size(10.dp))
        OutlinedTextField(
            value = projectName,
            onValueChange = {
                projectName = it
            },
            placeholder = { Text(text = "Enter a name") },
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.size(30.dp))
        Text(
            text = "Choose file type",
            modifier = Modifier.align(alignment = Alignment.Start),
            color = MaterialTheme.colorScheme.surfaceTint,
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.size(10.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth(),
        ) {
            OutlinedTextField(
                value = fileTypeOptions[selectedFileTypeIndex],
                onValueChange = {},
                readOnly = true,
                shape = MaterialTheme.shapes.small,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                fileTypeOptions.forEachIndexed { index, fileType ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = fileType,
                                fontWeight = if (index == selectedFileTypeIndex) FontWeight.Bold else null
                            )
                        },
                        onClick = {
                            selectedFileTypeIndex = index
                            expanded = false
                            selectedFileType = fileType
                        }
                    )
                }
            }
        }

        if (viewModel.storageType == CreateProject.StorageType.CLOUD.value) {

            Spacer(modifier = Modifier.size(30.dp))
            Text(
                text = "Security Option",
                modifier = Modifier.align(alignment = Alignment.Start),
                color = MaterialTheme.colorScheme.surfaceTint,
                style = MaterialTheme.typography.titleMedium
            )

            GroupOptions { selectedSecurityOption = it }

            if (selectedSecurityOption == SecurityOption.SECURE){
                Spacer(modifier = Modifier.size(10.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                    },
                    placeholder = { Text(text = "Enter password")},
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }


        Spacer(modifier = Modifier.size(40.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            OutlinedButton(
                onClick = { onCancelClick() },
                shape = MaterialTheme.shapes.small,
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "Cancel")
            }
            Spacer(modifier = Modifier.size(30.dp))
            Button(
                onClick = {
                    if (projectName.isNotEmpty()) {
                        //onContinueClick(projectName)
                        val project = Project(
                            id = UUID.randomUUID().toString(),
                            ownerId = user!!.id,
                            name = projectName,
                            isPublic = true,
                            description = "",
                            createdAt = System.currentTimeMillis(),
                            numberOfFiles = 0,
                            projectFileType = selectedFileType,
                            fileEncryptionPassword = if (password.isNotEmpty()) EncryptionAndDecryption.encryptPassword(password) else password,
                            projectLockPassword = "",
                            editorArrayId = arrayListOf(),
                            reviewerArrayId = arrayListOf(),
                            size = "0",
                            projectType = selectedProjectType,
                            projectStorageType = selectedStorageType,
                            lastModifiedDate = System.currentTimeMillis()
                        )
                        onCreateProjectClick(project)
                    }
                },
                shape = MaterialTheme.shapes.small,
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "Create project")
            }
        }

    }


 }

@Composable
fun GroupOptions( onSecurityOptionChange: (SecurityOption) -> Unit) {
    var selectedOption by remember { mutableStateOf(SecurityOption.NONE) }

    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = selectedOption == SecurityOption.NONE,
                onClick = {
                    selectedOption = SecurityOption.NONE
                    onSecurityOptionChange(SecurityOption.NONE)
                },
            )
            Text(text = SecurityOption.NONE.value)
        }
        Spacer(modifier = Modifier.width(width = 32.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = selectedOption == SecurityOption.SECURE,
                onClick = {
                    selectedOption = SecurityOption.SECURE
                    onSecurityOptionChange(SecurityOption.SECURE)
                },
            )
            Text(text = SecurityOption.SECURE.value)
        }
    }
}

enum class SecurityOption(val value: String){
    NONE("None"),
    SECURE("Secure")
}









