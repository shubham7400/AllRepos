package com.blueduck.annotator.screens.bottombar.notification

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.blueduck.annotator.R
import com.blueduck.annotator.enums.NotificationType
import com.blueduck.annotator.model.Notification


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(navController: NavHostController, openDrawer: () -> Unit = {}) {

    val notifications = listOf<Notification>(
        Notification(
            1,
            NotificationType.TYPE_ONE,
            "Lorem Ipsum wants to share a file with you",
            null,
            "5 h"
        ),
                Notification(
                2,
        NotificationType.TYPE_ONE,
        "Lorem Ipsum wants to share a file with you",
        null,
        "5 d"
    ),
        Notification(
            3,
            NotificationType.TYPE_TWO,
            "Lorem Ipsum shared a file with you",
            "Android",
            "4 d"
        ),
        Notification(
            4,
            NotificationType.TYPE_TWO,
            "Lorem Ipsum shared a file with you",
            "Java",
            "25 m"
        ),
        Notification(
            5,
            NotificationType.TYPE_TWO,
            "Lorem Ipsum shared a file with you",
            "Kotlin",
            "4 d"
        )
    )

    Scaffold(topBar = {
        TopAppBar(
            title = { Text(text = "Notifications")},
            navigationIcon = {
                Icon(imageVector = Icons.Default.Menu, contentDescription = "", modifier = Modifier.clickable { openDrawer() }.padding(horizontal = 16.dp))
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                titleContentColor = MaterialTheme.colorScheme.onPrimary,
            )
        )
    }) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            items(notifications){ notification ->
                when ( notification.type) {
                    NotificationType.TYPE_ONE -> {
                        NotificationTypeOne(notification)
                        Divider()
                    }
                    NotificationType.TYPE_TWO -> {
                        NotificationTypeTwo(notification)
                        Divider()
                    }
                }
            }
        }
    }
}


@Composable
fun NotificationTypeOne(notification: Notification) {
    Row(
        modifier = Modifier.padding(16.dp),
    ) {
        CircularImage(painterResource(id = R.drawable.ic_app_logo))
        Spacer(modifier = Modifier.width(16.dp))
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = notification.description,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { /* Handle Accept click */ },
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(text = "Accept")
                }
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedButton(
                    onClick = { /* Handle Deny click */ },
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(text = "Deny")
                }
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = notification.time,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun NotificationTypeTwo(notification: Notification) {
    Row(
        modifier = Modifier.padding(16.dp),
    ) {
        CircularImage(painterResource(id = R.drawable.ic_app_logo))
        Spacer(modifier = Modifier.width(16.dp))
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = notification.description,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_folder),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = notification.projectName ?: "",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = notification.time,
        )
    }
}

@Composable
fun CircularImage(painter: Painter) {
    Image(
        painter = painter,
        contentDescription = null,
        modifier = Modifier
            .size(56.dp)
            .clip(CircleShape)
    )
}