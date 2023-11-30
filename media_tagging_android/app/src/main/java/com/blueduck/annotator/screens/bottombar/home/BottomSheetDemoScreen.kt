package com.blueduck.annotator.screens.bottombar.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetDemoScreen() {
    val bottomSheetState = rememberBottomSheetScaffoldState()
    val coroutineScope = rememberCoroutineScope()
    val screens = listOf(
        1,
        2,
        // Add other screens here (Screen3, Screen4, etc.)
    )

    var currentPage by remember { mutableStateOf(0) }

    BottomSheetScaffold(
        scaffoldState = bottomSheetState,
        sheetSwipeEnabled = false,
        sheetPeekHeight = 0.dp, // Adjust this value to your preference
        sheetContent = {
            Column {
                when(currentPage){
                    0 -> {
                        Screen1 {
                            if (currentPage < screens.size){
                                currentPage++
                            }
                        }
                    }
                    1 -> {
                        Screen2 {
                            if (currentPage < screens.size){
                                currentPage++
                            }
                        }
                    }
                    2 -> {
                        Screen3 {
                            if (currentPage < screens.size){
                                currentPage++
                            }
                        }
                    }
                }
            }
        },
        content = {
            Button(onClick = {
                coroutineScope.launch {
                    bottomSheetState.bottomSheetState.expand()
                }
            }) {
                Text(text = "Open")
            }
        }
    )

}

@Composable
fun Screen1(onItemClick: () -> Unit){
    Text(text = "Screen 1", modifier = Modifier.clickable { onItemClick() })
}
@Composable
fun Screen2(onItemClick: () -> Unit){
    Text(text = "Screen 2", modifier = Modifier.clickable { onItemClick() })
}
@Composable
fun Screen3(onItemClick: () -> Unit){
    Text(text = "Screen 3", modifier = Modifier.clickable { onItemClick() })
}