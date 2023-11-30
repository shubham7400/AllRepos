package com.blueduck.annotator.screens.bottombar.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun SearchScreen(navController: NavHostController) {
    var searchQuery by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.surfaceVariant)
            .fillMaxSize()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "back arrow",
                modifier = Modifier.clickable { navController.popBackStack() }
                    .padding(start = 16.dp, 0.dp, 16.dp, 0.dp),
                tint = MaterialTheme.colorScheme.onSurface
            )
            BasicTextField(
                value = searchQuery, 
                onValueChange = { searchQuery = it },
                decorationBox = { innerTextField ->
                    if (searchQuery.isEmpty()){
                        Text(
                            text = "Search",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.background(color = Color.Transparent)
                        )
                    }
                    innerTextField()
                },
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurface)
            )
            Spacer(Modifier.weight(1f))
            if (searchQuery.isNotEmpty()){
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "back arrow",
                    modifier = Modifier.clickable { searchQuery = "" }
                        .padding(start = 16.dp, 0.dp, 16.dp, 0.dp)
                        .size(30.dp),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
         }
        Divider( color = MaterialTheme.colorScheme.outline)
    }
}