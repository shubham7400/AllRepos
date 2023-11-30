package com.blueduck.dajumgum.ui.bottombar.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.blueduck.dajumgum.ui.common.AppBar
import com.blueduck.dajumgum.ui.bottombar.HomeViewModel

@Composable
fun HomeScreen(navController: NavHostController, viewModel: HomeViewModel) {
    Scaffold(modifier = Modifier.fillMaxSize(),
        topBar = { AppBar(navController = navController, viewModel = viewModel) }) { p ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(p)) {
            Text(text = "Home Screen")
        }
    }

}