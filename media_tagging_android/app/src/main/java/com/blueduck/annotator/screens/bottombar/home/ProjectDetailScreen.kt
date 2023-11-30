package com.blueduck.annotator.screens.bottombar.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.blueduck.annotator.util.Constant
import com.blueduck.annotator.util.getDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectDetailScreen(navController: NavHostController, viewModel: HomeViewModel) {

    val project = viewModel.selectedProject

    Scaffold(topBar = {
        TopAppBar(
            title = { Text(text = "Details & Activity", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onPrimary)},
            navigationIcon = { Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier
                .padding(horizontal = 16.dp)
                .clickable { navController.popBackStack() })},
            colors = TopAppBarDefaults.topAppBarColors( containerColor = MaterialTheme.colorScheme.primary),
        )
    }) { paddingValues ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues = paddingValues)) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(all = 16.dp)) {
                Icon(imageVector = Icons.Default.Folder, contentDescription = null, modifier = Modifier.size(48.dp))
                Spacer(modifier = Modifier.width(16.dp))
                Text(text = project?.name ?: "", style = MaterialTheme.typography.titleLarge)
            }

            Text(text = "Data", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(all = 16.dp))
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).background(shape = MaterialTheme.shapes.medium, color = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp), )) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column(modifier = Modifier.fillMaxWidth().padding(all = 16.dp).weight(weight = 1f)) {
                        Text(text = "Type", style = MaterialTheme.typography.titleMedium)
                        Text(text = project?.projectFileType ?: "", style = MaterialTheme.typography.bodyMedium)
                    }
                    Column(modifier = Modifier.fillMaxWidth().padding(all = 16.dp).weight(weight = 1f)) {
                        Text(text = "Location", style = MaterialTheme.typography.titleMedium)
                        Text(text = project?.name ?: "", style = MaterialTheme.typography.bodyMedium)
                    }
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column(modifier = Modifier.fillMaxWidth().padding(all = 16.dp).weight(weight = 1f)) {
                        Text(text = "Created", style = MaterialTheme.typography.titleMedium)
                        Text(text = project?.createdAt!!.getDate(Constant.FULL_DATE_FORMAT), style = MaterialTheme.typography.bodyMedium)
                    }
                    Column(modifier = Modifier.fillMaxWidth().padding(all = 16.dp).weight(weight = 1f)) {}
                }
            }
        }
    }
}