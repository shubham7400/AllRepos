package com.blueduck.dajumgum.ui.bottombar.inspection

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape

import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.blueduck.dajumgum.R
import com.blueduck.dajumgum.ui.common.BackButtonAppBar
import com.blueduck.dajumgum.ui.bottombar.HomeViewModel


@Composable
fun AddKeywordScreen(navController: NavHostController, viewModel: HomeViewModel) {
    val context = LocalContext.current

    val tabs = listOf("카테고리", "위치", "점검", "온도")
    var selectedTabIndex by rememberSaveable { mutableStateOf(0) }

    val categoryTags = remember { mutableStateOf<List<String>>(emptyList()) }
    val positionTags = remember { mutableStateOf<List<String>>(emptyList()) }
    val inspectionTags = remember { mutableStateOf<List<String>>(emptyList()) }
    val temperatureTags = remember { mutableStateOf<List<String>>(emptyList()) }

    categoryTags.value = viewModel.categoryTags
    positionTags.value = viewModel.positionTags
    inspectionTags.value = viewModel.inspectionTags
    temperatureTags.value = viewModel.temperatureTags

    LaunchedEffect(key1 = 1, block = {
        viewModel.getCategoryTags(context)
        viewModel.getPositionTags(context)
        viewModel.getInspectionTags(context)
        viewModel.getTemperatureTags(context)

        viewModel.getCategoryTagsFromRemote(context)
        viewModel.getPositionTagsFromRemote(context)
        viewModel.getInspectionTagsFromRemote(context)
        viewModel.getTemperatureTagsFromRemote(context)
    })

    var tag by remember { mutableStateOf("") }
    var tagToBeEdited by remember { mutableStateOf("") }


    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            BackButtonAppBar {
                navController.popBackStack()
            }
        }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {


            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {

                Spacer(modifier = Modifier.height(24.dp))

                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    modifier = Modifier.border(
                        width = 2.dp,
                        color = colorResource(id = R.color.blue),
                        shape = RoundedCornerShape(10.dp)
                    ),
                    indicator = { tabPositions ->
                        TabRowDefaults.Indicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                            height = 0.dp
                        )
                    },
                    divider = {}
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            modifier = Modifier.weight(1f)
                        ) {
                            if (selectedTabIndex == index) {
                                Text(
                                    text = title,
                                    fontSize = 16.sp,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .background(
                                            color = colorResource(id = R.color.blue),
                                            shape = when (selectedTabIndex) {
                                                0 -> {
                                                    RoundedCornerShape(
                                                        topStart = 10.dp,
                                                        bottomStart = 10.dp
                                                    )
                                                }

                                                3 -> {
                                                    RoundedCornerShape(
                                                        bottomEnd = 10.dp,
                                                        topEnd = 10.dp
                                                    )
                                                }

                                                else -> {
                                                    RoundedCornerShape(0.dp)
                                                }
                                            }
                                        )
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    color = Color.White
                                )
                            } else if (index == 1 || index == 2 || index == 3) {
                                Row(
                                    modifier = Modifier.fillMaxHeight(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Spacer(
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .width(1.dp)
                                            .background(
                                                color = colorResource(
                                                    id = R.color.blue
                                                )
                                            )
                                    )
                                    Text(
                                        text = title,
                                        fontSize = 16.sp,
                                        textAlign = TextAlign.Center,
                                        color = colorResource(id = R.color.blue),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Spacer(
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .width(1.dp)
                                            .background(
                                                color = colorResource(
                                                    id = R.color.blue
                                                )
                                            )
                                    )
                                }
                            } else {
                                Text(
                                    text = title,
                                    fontSize = 16.sp,
                                    textAlign = TextAlign.Center,
                                    color = colorResource(id = R.color.blue),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                // Content of each tab
                when (selectedTabIndex) {
                    0 -> {
                        Text(text = "키워드 추가", modifier = Modifier.padding(vertical = 8.dp))
                        if (tagToBeEdited.isNotEmpty()) {
                            OutlinedTextField(
                                value = tag,
                                onValueChange = { newValue ->
                                    tag = newValue
                                },
                                textStyle = LocalTextStyle.current.copy(color = Color.Black),
                                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                                keyboardActions = KeyboardActions(onDone = {
                                    // You can add additional logic here when the Done button is clicked
                                }),
                                modifier = Modifier
                                    .fillMaxWidth(),
                                trailingIcon = {
                                    if (tag.isNotEmpty()) {
                                        Row {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "",
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.clickable {
                                                    viewModel.deleteCategoryTag(
                                                        context,
                                                        tagToBeEdited
                                                    ) {
                                                        tagToBeEdited = ""
                                                        viewModel.saveCategoryTag(
                                                            context,
                                                            tag.trim()
                                                        ) {
                                                            tag = ""
                                                        }
                                                    }
                                                }
                                            )
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "",
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.clickable {
                                                    tagToBeEdited = ""
                                                    tag = ""
                                                }
                                            )
                                            Spacer(modifier = Modifier.width(10.dp))
                                        }
                                    }
                                }
                            )

                        } else {
                            NewTag { newTag ->
                                viewModel.saveCategoryTag(context, newTag) {}
                            }
                        }
                        DisplayTags(categoryTags, onDeleteTagClick = {
                            viewModel.deleteCategoryTag(context, it) {}
                        }, onTagClick = {
                            tag = it
                            tagToBeEdited = it
                        })
                    }

                    1 -> {
                        Text(text = "키워드 추가", modifier = Modifier.padding(vertical = 8.dp))
                        if (tagToBeEdited.isNotEmpty()) {
                            OutlinedTextField(
                                value = tag,
                                onValueChange = { newValue ->
                                    tag = newValue
                                },
                                textStyle = LocalTextStyle.current.copy(color = Color.Black),
                                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                                keyboardActions = KeyboardActions(onDone = {
                                    // You can add additional logic here when the Done button is clicked
                                }),
                                modifier = Modifier
                                    .fillMaxWidth(),
                                trailingIcon = {
                                    if (tag.isNotEmpty()) {
                                        Row {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "",
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.clickable {
                                                    viewModel.deletePositionTag(
                                                        context,
                                                        tagToBeEdited
                                                    ) {
                                                        tagToBeEdited = ""
                                                        viewModel.savePositionTag(
                                                            context,
                                                            tag.trim()
                                                        ) {
                                                            tag = ""
                                                        }
                                                    }
                                                }
                                            )
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "",
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.clickable {
                                                    tagToBeEdited = ""
                                                    tag = ""
                                                }
                                            )
                                            Spacer(modifier = Modifier.width(10.dp))
                                        }
                                    }
                                }
                            )

                        } else {
                            NewTag { newTag ->
                                viewModel.savePositionTag(context, newTag) {}
                            }
                        }
                        DisplayTags(positionTags, onDeleteTagClick = {
                            viewModel.deletePositionTag(context, it) {}
                        }, onTagClick = {
                            tag = it
                            tagToBeEdited = it
                        })
                    }

                    2 -> {
                        Text(text = "키워드 추가", modifier = Modifier.padding(vertical = 8.dp))
                        if (tagToBeEdited.isNotEmpty()) {
                            OutlinedTextField(
                                value = tag,
                                onValueChange = { newValue ->
                                    tag = newValue
                                },
                                textStyle = LocalTextStyle.current.copy(color = Color.Black),
                                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                                keyboardActions = KeyboardActions(onDone = {
                                    // You can add additional logic here when the Done button is clicked
                                }),
                                modifier = Modifier
                                    .fillMaxWidth(),
                                trailingIcon = {
                                    if (tag.isNotEmpty()) {
                                        Row {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "",
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.clickable {
                                                    viewModel.deleteInspectionTag(
                                                        context,
                                                        tagToBeEdited
                                                    ) {
                                                        tagToBeEdited = ""
                                                        viewModel.saveInspectionTag(
                                                            context,
                                                            tag.trim()
                                                        ) {
                                                            tag = ""
                                                        }
                                                    }
                                                }
                                            )
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "",
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.clickable {
                                                    tagToBeEdited = ""
                                                    tag = ""
                                                }
                                            )
                                            Spacer(modifier = Modifier.width(10.dp))
                                        }
                                    }
                                }
                            )

                        } else {
                            NewTag { newTag ->
                                viewModel.saveInspectionTag(context, newTag) {}
                            }
                        }
                        DisplayTags(inspectionTags, onDeleteTagClick = {
                            viewModel.deleteInspectionTag(context, it) {}
                        }, onTagClick = {
                            tag = it
                            tagToBeEdited = it
                        })
                    }

                    3 -> {
                        Text(text = "키워드 추가", modifier = Modifier.padding(vertical = 8.dp))
                        if (tagToBeEdited.isNotEmpty()) {
                            OutlinedTextField(
                                value = tag,
                                onValueChange = { newValue ->
                                    tag = newValue
                                },
                                textStyle = LocalTextStyle.current.copy(color = Color.Black),
                                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                                keyboardActions = KeyboardActions(onDone = {
                                    // You can add additional logic here when the Done button is clicked
                                }),
                                modifier = Modifier
                                    .fillMaxWidth(),
                                trailingIcon = {
                                    if (tag.isNotEmpty()) {
                                        Row {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "",
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.clickable {
                                                    viewModel.deleteTemperatureTag(
                                                        context,
                                                        tagToBeEdited
                                                    ) {
                                                        tagToBeEdited = ""
                                                        viewModel.saveTemperatureTag(
                                                            context,
                                                            tag.trim()
                                                        ) {
                                                            tag = ""
                                                        }
                                                    }
                                                }
                                            )
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "",
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.clickable {
                                                    tagToBeEdited = ""
                                                    tag = ""
                                                }
                                            )
                                            Spacer(modifier = Modifier.width(10.dp))
                                        }
                                    }
                                }
                            )

                        } else {
                            NewTag { newTag ->
                                viewModel.saveTemperatureTag(context, newTag) {}
                            }
                        }
                        DisplayTags(temperatureTags, onDeleteTagClick = {
                            viewModel.deleteTemperatureTag(context, it) {}
                        }, onTagClick = {
                            tag = it
                            tagToBeEdited = it
                        })
                    }
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalLayoutApi::class)
private fun DisplayTags(
    tags: MutableState<List<String>>,
    onDeleteTagClick: (String) -> Unit,
    onTagClick: (String) -> Unit
) {
    FlowRow {
        tags.value.forEach { tag ->
            AssistChip(
                onClick = {
                    onTagClick(tag)
                },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                ),
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "",
                        modifier = Modifier.clickable {
                            onDeleteTagClick(tag)
                        },
                        tint = colorResource(
                            id = R.color.orange
                        )
                    )
                },
                modifier = Modifier.padding(end = 4.dp),
                label = {
                    Text(
                        text = tag,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = colorResource(id = R.color.orange)
                    )
                },
                shape = CircleShape
            )
        }
    }
}

@Composable
private fun NewTag(onAddNewTagClick: (String) -> Unit) {
    var tag by remember { mutableStateOf("") }

    OutlinedTextField(
        value = tag,
        onValueChange = { newValue ->
            tag = newValue
        },
        placeholder = { Text(text = "Enter keyword here") },
        textStyle = LocalTextStyle.current.copy(color = Color.Black),
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = {
            // You can add additional logic here when the Done button is clicked
        }),
        modifier = Modifier
            .fillMaxWidth(),
        trailingIcon = {
            if (tag.isNotEmpty()) {
                Icon(
                    painter = painterResource(id = R.drawable.add_image),
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable {
                        onAddNewTagClick(tag.trim())
                        tag = ""
                    }
                )
            }
        },
    )
}



