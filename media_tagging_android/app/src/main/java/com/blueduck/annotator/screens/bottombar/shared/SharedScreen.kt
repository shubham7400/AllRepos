package com.blueduck.annotator.screens.bottombar.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.blueduck.annotator.navigation.Screen


@Composable
fun SharedScreen(navController: NavHostController , openDrawer: () -> Unit = {} ) {
    Scaffold{ padding ->
        Row(
            modifier = Modifier
                .fillMaxWidth() .padding(all = 16.dp)
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
                modifier = Modifier.padding(start = 16.dp).clickable { navController.navigate(Screen.Search.route) }
            )
        }

        Box(modifier = Modifier
            .padding(padding)
            .fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "Shared Screen")
        }
    }
}