package com.example.paginglibrarytest.screens.home

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.paging.compose.collectAsLazyPagingItems
import coil.annotation.ExperimentalCoilApi
import com.example.paginglibrarytest.navigation.Screen
import com.example.paginglibrarytest.screens.common.ListContent

@OptIn(ExperimentalMaterial3Api::class, ExperimentalCoilApi::class)
@Composable
fun HomeScreen( navController: NavHostController, homeViewModel: HomeViewModel = hiltViewModel() ) {
    val getAllImages = homeViewModel.getAllImages.collectAsLazyPagingItems()

    Scaffold(
        topBar = {
            HomeTopBar(onSearchClicked = { navController.navigate(Screen.Search.route) })
        },
        content = { padding -> ListContent(items = getAllImages, padding) }
    )
}