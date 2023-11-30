package com.blueduck.dajumgum.ui.bottombar.inspection

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.blueduck.dajumgum.R
import com.blueduck.dajumgum.ui.common.AppBar
import com.blueduck.dajumgum.model.Customer
import com.blueduck.dajumgum.enums.Screen
import com.blueduck.dajumgum.preferences.DataStoreManager
import com.blueduck.dajumgum.ui.bottombar.HomeViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun InspectionScreen(navController: NavHostController, viewModel: HomeViewModel) {
    val context = LocalContext.current
    val user = DataStoreManager.getUser(context).collectAsState(initial = null)

    var selectedTabIndex by rememberSaveable { mutableStateOf(1) }

    val tabs = listOf("지도", "리스트")

    val customers = viewModel.customers

    LaunchedEffect(key1 = user.value, block = {
        user.value?.let {
            viewModel.getAllCustomers(user.value!!.id)
        }
    })

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            AppBar(navController, viewModel)
        },
        floatingActionButton = {
            FloatingActionButton(
                shape = CircleShape,
                containerColor = colorResource(id = R.color.orange),
                onClick = {
                    navController.navigate(Screen.CreateCustomer.route)
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
                            height = 0.dp,
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
                                            shape = if (selectedTabIndex == 0) {
                                                RoundedCornerShape(
                                                    topStart = 10.dp,
                                                    bottomStart = 10.dp
                                                )
                                            } else {
                                                RoundedCornerShape(
                                                    bottomEnd = 10.dp,
                                                    topEnd = 10.dp
                                                )
                                            }
                                        )
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    color = Color.White
                                )
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

                // Content of each tab
                when (selectedTabIndex) {
                    0 -> {
                        ShowMap()
                    }

                    1 -> {
                        DisplayCustomer(customers){ customer ->
                            viewModel.currentCustomer = customer
                            navController.navigate(Screen.DefectList.route)
                        }
                    }
                }
            }

        }
    }
}

@Composable
private fun DisplayCustomer(
    customers: List<Customer>,
    onInspectionClick: (Customer) -> Unit
) {
    LazyColumn(
        modifier = Modifier.padding(vertical = 20.dp),
        content = {
            items(customers) { customer ->
                Surface(
                    modifier = Modifier.padding(vertical = 4.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = Color.White,
                    shadowElevation = 2.dp, // Adjust the elevation value to control the shadow intensity
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = customer.name,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = customer.address)
                            Text(text = customer.mobile)
                            Text(
                                text = "검사시작",
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.clickable {
                                    onInspectionClick(customer)
                                },
                                color = colorResource(id = R.color.blue)
                            )
                        }
                    }
                }
            }
        })
}


@Composable
fun ShowMap() {
    val maheshwar = LatLng(22.187499285075372, 75.60046533484928)
    val ujjain = LatLng(23.188681784101313, 75.77122772419958)
    val dewas = LatLng(22.971595029593004, 76.06024958429667)
    val indore = LatLng(22.70384416788485, 75.84955141699396)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(maheshwar, 10f)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp)
            .clip(shape = RoundedCornerShape(15.dp))
    ) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState
        ) {
            Marker(
                state = MarkerState(position = maheshwar),
                title = "maheshwar",
                snippet = "Marker in maheshwar"
            )
            Marker(
                state = MarkerState(position = ujjain),
                title = "Ujjain",
                snippet = "Marker in Ujjain"
            )
            Marker(
                state = MarkerState(position = dewas),
                title = "Dewas",
                snippet = "Marker in Dewas"
            )
            Marker(
                state = MarkerState(position = indore),
                title = "Indore",
                snippet = "Marker in Indore"
            )
        }
    }
}
