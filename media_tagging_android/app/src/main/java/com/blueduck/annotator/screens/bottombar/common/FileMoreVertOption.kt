package com.blueduck.annotator.screens.bottombar.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.blueduck.annotator.navigation.Screen

@Composable
fun FileMoreVertOption(navController: NavHostController,
                       onRenameClick: () -> Unit,
                       onDeleteClick: () -> Unit,
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
            Text(text = "Rename file", style = MaterialTheme.typography.titleMedium)
        }


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = null,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Text(text = "Share", style = MaterialTheme.typography.titleMedium)
        }

        Row(
            modifier = Modifier.clickable {
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
                .clickable { navController.navigate(Screen.FileDetail.route) }
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