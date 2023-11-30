package com.blueduck.dajumgum

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun Discover(
    onDetails: (Int) -> Unit,
    onIngredients: () -> Unit,
    bottomBarPadding: PaddingValues,
    onIngredientSearch: (String) -> Unit
) {
    Discover(
        bottomBarPadding = bottomBarPadding,
        onCuisineSearch = onIngredientSearch,
        onDetails = onDetails,
        onIngredients = onIngredients,
        onIngredientSearch = onIngredientSearch
    )
}

@Composable
internal fun Discover(
    bottomBarPadding: PaddingValues,
    onCuisineSearch: (String) -> Unit,
    onDetails: (Int) -> Unit,
    onIngredients: () -> Unit,
    onIngredientSearch: (String) -> Unit
) {


    CircularLoading(
        isLoading = true
    ) {
        Surface(modifier = Modifier.fillMaxSize().padding(bottomBarPadding)) {

         }
    }
}






