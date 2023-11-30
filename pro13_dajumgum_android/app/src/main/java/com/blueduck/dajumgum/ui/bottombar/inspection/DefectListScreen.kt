package com.blueduck.dajumgum.ui.bottombar.inspection

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.blueduck.dajumgum.R
import com.blueduck.dajumgum.ui.common.BackButtonAppBar
import com.blueduck.dajumgum.model.ACDefect
import com.blueduck.dajumgum.model.InspectionDefect
import com.blueduck.dajumgum.model.TemperatureDefect
import com.blueduck.dajumgum.enums.Screen
import com.blueduck.dajumgum.preferences.DataStoreManager
import com.blueduck.dajumgum.ui.bottombar.HomeViewModel

@Composable
fun DefectListScreen(navController: NavHostController, viewModel: HomeViewModel) {
    val context = LocalContext.current
    val user = DataStoreManager.getUser(context = context).collectAsState(initial = null)
    val customer = viewModel.currentCustomer
    var selectedTabIndex by rememberSaveable { mutableStateOf(0) }

    val tabs = listOf(R.drawable.ic_search, R.drawable.ic_tempreture, R.drawable.ic_ac)

    LaunchedEffect(key1 = user.value, block = {
        user.value?.let {
            viewModel.getInspectionDefects(
                user.value!!.id,
                customer.id,
                onSuccess = {},
                onFailure = { })
            viewModel.getTemperatureDefects(
                user.value!!.id,
                customer.id,
                onSuccess = {},
                onFailure = { })
            viewModel.getACDefects(user.value!!.id, customer.id, onSuccess = {}, onFailure = { })
        }
    })


    val inspectionDefects =
        viewModel.inspectionDefects.groupBy { it.category }.entries.map { it.value }
    val temperatureDefects =
        viewModel.temperatureDefects.groupBy { it.category }.entries.map { it.value }
    val acDefects = viewModel.acDefects.groupBy { it.category }.entries.map { it.value }



    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            BackButtonAppBar {
                navController.popBackStack()
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                shape = CircleShape,
                containerColor = colorResource(id = R.color.orange),
                onClick = {
                    viewModel.category = null
                    when (selectedTabIndex) {
                        0 -> {
                            viewModel.inspectionDefectToEdit = null
                            navController.navigate(Screen.CreateInspectionDefect.route)
                        }

                        1 -> {
                            viewModel.temperatureDefectToEdit = null
                            navController.navigate(Screen.CreateTemperatureDefect.route)
                        }

                        2 -> {
                            viewModel.acDefectToEdit = null
                            navController.navigate(Screen.CreateAirConditionerDefect.route)
                        }
                    }
                }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "", tint = Color.White)
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .clickable {
                                viewModel.fileUri = null
                                if (inspectionDefects.isEmpty() && temperatureDefects.isEmpty() && acDefects.isEmpty()) {
                                    Toast
                                        .makeText(
                                            context,
                                            "결함을 하나 이상 생성하십시오.",
                                            Toast.LENGTH_SHORT
                                        )
                                        .show()
                                } else {
                                    navController.navigate(Screen.InspectionReport.route)
                                }
                            }
                            .background(
                                color = colorResource(id = R.color.orange),
                                shape = RoundedCornerShape(10.dp)
                            )
                            .padding(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "",
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "생성", color = Color.White)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    TabRow(
                        divider = {},
                        selectedTabIndex = selectedTabIndex,
                        modifier = Modifier.border(
                            width = 2.dp,
                            color = colorResource(id = R.color.blue),
                            shape = RoundedCornerShape(10.dp)
                        ),
                        indicator = { tabPositions ->
                            TabRowDefaults.Indicator(
                                Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                                height = 0.dp,
                            )
                        },
                    ) {
                        tabs.forEachIndexed { index, iconResId ->
                            Tab(
                                selected = selectedTabIndex == index,
                                onClick = { selectedTabIndex = index },
                                modifier = Modifier.weight(1f)
                            ) {
                                if (selectedTabIndex == index) {
                                    Box(
                                        contentAlignment = Alignment.Center,
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

                                                    2 -> {
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
                                    ) {
                                        Icon(
                                            painter = painterResource(id = iconResId),
                                            contentDescription = "",
                                            tint = Color.White
                                        )
                                    }
                                } else if (index == 1 || index == 2) {
                                    Row {
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
                                        Box(
                                            contentAlignment = Alignment.Center,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .fillMaxHeight(),
                                        ) {
                                            Icon(
                                                painter = painterResource(id = iconResId),
                                                contentDescription = "",
                                                tint = colorResource(
                                                    id = R.color.blue
                                                )
                                            )
                                        }
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
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .fillMaxHeight(),
                                    ) {
                                        Icon(
                                            painter = painterResource(id = iconResId),
                                            contentDescription = "",
                                            tint = colorResource(
                                                id = R.color.blue
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }

                }

                Spacer(modifier = Modifier.height(16.dp))

                // Content of each tab
                when (selectedTabIndex) {
                    0 -> {
                        DisplayInspectionDefects(inspectionDefects, onDefectClick = { defect ->
                            viewModel.inspectionDefectToEdit = defect
                            navController.navigate(Screen.CreateInspectionDefect.route)
                        }) {
                            viewModel.category = it
                            viewModel.inspectionDefectToEdit = null
                            navController.navigate(Screen.CreateInspectionDefect.route)
                        }
                    }

                    1 -> {
                        DisplayTemperatureDefects(temperatureDefects, onDefectClick = { defect ->
                            viewModel.temperatureDefectToEdit = defect
                            navController.navigate(Screen.CreateTemperatureDefect.route)
                        }, onAddDefectClick = {
                            viewModel.category = it
                            viewModel.temperatureDefectToEdit = null
                            navController.navigate(Screen.CreateTemperatureDefect.route)
                        })
                    }

                    2 -> {
                        DisplayACDefects(acDefects, onDefectClick = { defect ->
                            viewModel.acDefectToEdit = defect
                            navController.navigate(Screen.CreateAirConditionerDefect.route)
                        }) {
                            viewModel.category = it
                            viewModel.acDefectToEdit = null
                            navController.navigate(Screen.CreateAirConditionerDefect.route)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DisplayACDefects(
    acDefects: List<List<ACDefect>>,
    onDefectClick: (ACDefect) -> Unit,
    onAddDefectClick: (String) -> Unit
) {
    LazyColumn(modifier = Modifier
        .padding(vertical = 10.dp)
        .fillMaxSize(),
        content = {
            items(acDefects.size) { index ->
                val group = acDefects[index]
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = group[0].category)
                        FloatingActionButton(
                            onClick = {
                                onAddDefectClick(group[0].category)
                            },
                            containerColor = colorResource(id = R.color.orange),
                            shape = CircleShape,
                            modifier = Modifier.size(30.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "",
                                tint = Color.White
                            )
                        }
                    }
                    group.forEach {
                        val defect = it
                        Surface(
                            modifier = Modifier
                                .clickable {
                                    onDefectClick(defect)
                                }
                                .fillMaxWidth()
                                .padding(vertical = 10.dp)
                                .shadow(
                                    elevation = 2.dp,
                                    shape = RoundedCornerShape(10.dp)
                                )
                        ) {
                            Column(
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Text(text = "HCHO: ${defect.hcho}")
                                Text(text = "TVOC: ${defect.tvoc}")
                                Text(text = "RADON: ${defect.radon}")
                                Text(text = "Status: ${defect.status}")
                            }
                        }

                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        })

}

@Composable
fun DisplayTemperatureDefects(
    temperatureDefects: List<List<TemperatureDefect>>,
    onDefectClick: (TemperatureDefect) -> Unit,
    onAddDefectClick: (String) -> Unit
) {
    LazyColumn(modifier = Modifier
        .padding(vertical = 10.dp)
        .fillMaxSize(),
        content = {
            items(temperatureDefects.size) { index ->
                val group = temperatureDefects[index]
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = group[0].category)
                        FloatingActionButton(
                            onClick = {
                                onAddDefectClick(group[0].category)
                            },
                            containerColor = colorResource(id = R.color.orange),
                            shape = CircleShape,
                            modifier = Modifier.size(30.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "",
                                tint = Color.White
                            )
                        }
                    }
                    group.forEach {
                        val defect = it
                        Surface(
                            modifier = Modifier
                                .clickable {
                                    onDefectClick(defect)
                                }
                                .fillMaxWidth()
                                .padding(vertical = 10.dp)
                                .shadow(
                                    elevation = 2.dp,
                                    shape = RoundedCornerShape(10.dp),
                                    spotColor = Color.Gray
                                )
                                .padding(vertical = 1.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp)
                            ) {
                                Text(text = "현관-${defect.defectNumber}")
                                Spacer(modifier = Modifier.height(10.dp))
                                val painterZoomImage =
                                    rememberAsyncImagePainter(model = defect.zoomedImageUrl)
                                val painterFarImage =
                                    rememberAsyncImagePainter(model = defect.farImageUrl)
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .border(
                                            width = 1.dp,
                                            color = Color.Gray,
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                        .fillMaxWidth()
                                        .padding(10.dp)
                                ) {
                                    Column {
                                        Text(text = defect.category)
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Text(text = defect.position.joinToString(separator = ", "))
                                    }
                                    Row {
                                        Image(
                                            painter = painterZoomImage, contentDescription = "",
                                            modifier = Modifier
                                                .clip(shape = RoundedCornerShape(10.dp))
                                                .width(60.dp)
                                                .height(60.dp),
                                            contentScale = ContentScale.FillBounds,
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Image(
                                            painter = painterFarImage, contentDescription = "",
                                            modifier = Modifier
                                                .clip(shape = RoundedCornerShape(10.dp))
                                                .width(60.dp)
                                                .height(60.dp),
                                            contentScale = ContentScale.FillBounds,
                                        )
                                    }
                                }
                            }
                        }

                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        })

}

@Composable
fun DisplayInspectionDefects(
    inspectionDefects: List<List<InspectionDefect>>,
    onDefectClick: (InspectionDefect) -> Unit,
    onAddDefectClick: (String) -> Unit
) {
    LazyColumn(modifier = Modifier
        .padding(vertical = 10.dp)
        .fillMaxSize(),
        content = {
            items(inspectionDefects.size) { index ->
                val group = inspectionDefects[index]
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = group[0].category)
                        FloatingActionButton(
                            onClick = {
                                onAddDefectClick(group[0].category)
                            },
                            containerColor = colorResource(id = R.color.orange),
                            shape = CircleShape,
                            modifier = Modifier.size(30.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "",
                                tint = Color.White
                            )
                        }
                    }
                    group.forEach {
                        val defect = it
                        Surface(
                            modifier = Modifier
                                .clickable {
                                    onDefectClick(defect)
                                }
                                .fillMaxWidth()
                                .padding(vertical = 10.dp)
                                .shadow(
                                    elevation = 2.dp,
                                    shape = RoundedCornerShape(10.dp),
                                    spotColor = Color.Gray
                                )
                                .padding(vertical = 1.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp)
                            ) {
                                Text(text = "현관-${defect.defectNumber}")
                                Spacer(modifier = Modifier.height(10.dp))
                                val painterZoomImage =
                                    rememberAsyncImagePainter(model = defect.zoomedImageUrl)
                                val painterFarImage =
                                    rememberAsyncImagePainter(model = defect.farImageUrl)
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .border(
                                            width = 1.dp,
                                            color = Color.Gray,
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                        .fillMaxWidth()
                                        .padding(10.dp)
                                ) {
                                    Column {
                                        Text(text = defect.category)
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Text(text = defect.position.joinToString(separator = ", "))
                                    }
                                    Row {
                                        Image(
                                            painter = painterZoomImage, contentDescription = "",
                                            modifier = Modifier
                                                .clip(shape = RoundedCornerShape(10.dp))
                                                .width(60.dp)
                                                .height(60.dp),
                                            contentScale = ContentScale.FillBounds,
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Image(
                                            painter = painterFarImage, contentDescription = "",
                                            modifier = Modifier
                                                .clip(shape = RoundedCornerShape(10.dp))
                                                .width(60.dp)
                                                .height(60.dp),
                                            contentScale = ContentScale.FillBounds,
                                        )
                                    }
                                }
                            }
                        }

                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        })
}
