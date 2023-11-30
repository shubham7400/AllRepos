package com.blueduck.annotator.screens.bottombar.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
 import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.blueduck.annotator.R
import com.blueduck.annotator.model.Project




@Composable
fun ProjectView(project: Project, onMoreVertClick: () -> Unit, onProjectClick: () -> Unit) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter =  if (project.projectLockPassword.isNotEmpty() && project.fileEncryptionPassword.isNotEmpty()){
                painterResource(id = R.drawable.ic_folder_key_lock)
            } else if (project.projectLockPassword.isNotEmpty()){
                painterResource(id = R.drawable.ic_folder_lock)
            } else if (project.fileEncryptionPassword.isNotEmpty()){
                painterResource(id = R.drawable.ic_folder_key)
            } else {
                painterResource(id = R.drawable.ic_folder)
            },
            contentDescription = "folder icon",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.clickable { onProjectClick() },
        )
        Row() {
            Text(
                text = project.name,
                modifier = Modifier
                    .width(128.dp)
                    .padding(16.dp, 0.dp, 16.dp, 0.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium
            )
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "folder menu action",
                modifier = Modifier.clickable {
                    onMoreVertClick()
                }
            )
        }
        
        Spacer(modifier = Modifier.height(height = 16.dp))
    }
}