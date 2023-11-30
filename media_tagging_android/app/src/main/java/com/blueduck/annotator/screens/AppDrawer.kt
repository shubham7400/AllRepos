package com.blueduck.annotator.screens

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddToDrive
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.FolderShared
import androidx.compose.material.icons.filled.InstallMobile
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.google.firebase.auth.FirebaseAuth
import com.blueduck.annotator.HomeActivity
import com.blueduck.annotator.enums.CreateProject
import com.blueduck.annotator.navigation.Screen
import com.blueduck.annotator.preferences.getUser
import com.blueduck.annotator.preferences.logOutUser
import com.blueduck.annotator.screens.auth.MainActivity
import com.blueduck.annotator.screens.bottombar.home.HomeViewModel

@Composable
fun AppDrawer(
    route: String,
    navigateToLocalProject: (String) -> Unit = {},
    navigateToCloudProject: (String) -> Unit = {},
    navigateToDriveProject: (String) -> Unit = {},
    navigateToSettings: () -> Unit = {},
    navigateToShared: () -> Unit = {},
    closeDrawer: () -> Unit = {},
    viewModel: HomeViewModel
) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()

    ModalDrawerSheet(modifier = Modifier.fillMaxHeight(), drawerShape = RoundedCornerShape(size = 0.dp)) {

        LazyColumn( modifier = Modifier.fillMaxHeight(), verticalArrangement = Arrangement.SpaceBetween,content = {
            item {

                Column {
                    DrawerHeader()

                    Spacer(modifier = Modifier.height(height = 4.dp))

                    NavigationDrawerItem(
                        label = {
                            Text(
                                text = "Shared",
                                style = MaterialTheme.typography.labelLarge
                            )
                        },
                        selected = route == Screen.Shared.route,
                        onClick = {
                            navigateToShared()
                            closeDrawer()
                        },
                        modifier = Modifier.padding(start = 16.dp, end = 12.dp),
                        icon = { Icon(imageVector = Icons.Default.FolderShared, contentDescription = null) },
                        shape = MaterialTheme.shapes.extraLarge
                    )

                    Spacer(modifier = Modifier.height(height = 4.dp))
                    Divider()

                    Text(
                        text = "Location",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(horizontal = 32.dp, vertical = 8.dp)
                    )

                    NavigationDrawerItem(
                        label = {
                            Text(
                                text = "On My Device",
                                style = MaterialTheme.typography.labelLarge
                            )
                        },
                        selected = route == Screen.LocalProject.route,
                        onClick = {
                            navigateToLocalProject(Screen.LocalProject.route)
                            viewModel.storageType = CreateProject.StorageType.LOCAL.value
                            closeDrawer()
                        },
                        modifier = Modifier.padding(start = 16.dp, end = 12.dp),
                        icon = { Icon(imageVector = Icons.Default.InstallMobile, contentDescription = null) },
                        shape = MaterialTheme.shapes.extraLarge
                    )
                    Spacer(modifier = Modifier.height(height = 4.dp))
                    NavigationDrawerItem(
                        label = {
                            Text(
                                text = "Cloud",
                                style = MaterialTheme.typography.labelLarge
                            )
                        },
                        selected = route == Screen.CloudProject.route,
                        onClick = {
                            navigateToCloudProject(Screen.CloudProject.route)
                            viewModel.storageType = CreateProject.StorageType.CLOUD.value
                            closeDrawer()
                        },
                        modifier = Modifier.padding(start = 16.dp, end = 12.dp),
                        icon = { Icon(imageVector = Icons.Default.Cloud, contentDescription = null) },
                        shape = MaterialTheme.shapes.extraLarge
                    )
                    Spacer(modifier = Modifier.height(height = 4.dp))
                    NavigationDrawerItem(
                        label = {
                            Text(
                                text = "Drive",
                                style = MaterialTheme.typography.labelLarge
                            )
                        },
                        selected = route == Screen.DriveProject.route,
                        onClick = {
                            navigateToDriveProject(Screen.DriveProject.route)
                            viewModel.storageType = CreateProject.StorageType.DRIVE.value
                            closeDrawer()
                        },
                        modifier = Modifier.padding(start = 16.dp, end = 12.dp),
                        icon = { Icon(imageVector = Icons.Default.AddToDrive, contentDescription = null) },
                        shape = MaterialTheme.shapes.extraLarge
                    )

                    Spacer(modifier = Modifier.height(height = 4.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(height = 4.dp))

                    NavigationDrawerItem(
                        label = { Text(text = "Notification", style = MaterialTheme.typography.labelLarge) },
                        selected = route == Screen.Notification.route,
                        onClick = {
                            navigateToSettings()
                            closeDrawer()
                        },
                        modifier = Modifier.padding(start = 16.dp, end = 12.dp),
                        icon = { Icon(imageVector = Icons.Default.Person, contentDescription = null) },
                        shape = MaterialTheme.shapes.extraLarge
                    )

                    Spacer(modifier = Modifier.height(height = 4.dp))

                }
             }
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Divider()
                    Row( modifier = Modifier
                        .padding(start = 32.dp, end = 32.dp, top = 16.dp, bottom = 16.dp)
                        .fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Icon(imageVector = Icons.Default.Settings, contentDescription = "")
                        Icon(imageVector = Icons.Default.Logout, contentDescription = "",
                            modifier = Modifier.clickable {
                                closeDrawer()
                                auth.signOut()
                                context.logOutUser()
                                val intent = Intent(context, MainActivity::class.java)
                                context.startActivity(intent)
                                (context as HomeActivity).finish()
                            })
                    }

                }

            }
        })
    }
 }


@Composable
fun DrawerHeader() {

    val context = LocalContext.current
    val user = context.getUser()

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.secondaryContainer)
            .padding(all = 16.dp)
            .fillMaxWidth()
    ) {

        LoadProfileImage(url = user!!.profileImage)
        Spacer(modifier = Modifier.padding(all = 5.dp))

        Text(
            text = user.name,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge,
         )
        Text(
            text = user.email,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge,
         )
    }
}

@Composable
fun LoadProfileImage(url: String) {
    val painter = rememberAsyncImagePainter(
        ImageRequest.Builder(LocalContext.current).data(data = url).apply(block = fun ImageRequest.Builder.() {
            crossfade(true)
        }).build()
    )
    Image(
        painter = painter,
        contentDescription = null,
        modifier = Modifier
            .size(100.dp)
            .clip(CircleShape)
    )
}

