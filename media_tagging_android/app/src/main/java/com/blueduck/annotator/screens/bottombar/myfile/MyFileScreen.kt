package com.blueduck.annotator.screens.bottombar.myfile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyFileScreen(navController: NavHostController) {
    Scaffold { padding ->
        Column(modifier = Modifier.padding(padding)) {
            Text(text = "My File Screen")
        }
    }
}