package com.blueduck.dajumgum

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun BookMarkScreen(
    bottomBarPadding: PaddingValues,
    onDetails: (Int) -> Unit
) {
    BookMark(
        bottomBarPadding = bottomBarPadding,
        onDetails = onDetails
    )
}

@Composable
internal fun BookMark(
    bottomBarPadding: PaddingValues,
    onDetails: (Int) -> Unit
) {

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(bottomBarPadding)
    ) {

    }


}
