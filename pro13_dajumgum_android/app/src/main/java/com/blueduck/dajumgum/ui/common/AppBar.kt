package com.blueduck.dajumgum.ui.common

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.blueduck.dajumgum.MainActivity
import com.blueduck.dajumgum.R
import com.blueduck.dajumgum.ui.bottombar.HomeViewModel
import com.blueduck.dajumgum.model.User
import com.blueduck.dajumgum.enums.Screen
import com.blueduck.dajumgum.preferences.DataStoreManager
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch



@Composable fun AppBar(navController: NavHostController, viewModel: HomeViewModel, ) {
    val activity = LocalContext.current as Activity

    var isMenuOpen by remember { mutableStateOf(false) }

    // Create a coroutine scope
    val coroutineScope = rememberCoroutineScope()

    val userState: State<User?> = viewModel.user.collectAsState(null)
    LaunchedEffect(Unit) {
        viewModel.getUser(activity)
    }
    val user = userState.value


    Row(
        modifier = Modifier .background(color = colorResource(id = R.color.purple_blue)) .padding(16.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.splash_logo),
            contentDescription = "",
            modifier = Modifier
                .width(40.dp)
                .height(40.dp)
        )

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.End
        ) {
            Text(text = user?.name?.let { it.firstOrNull()?.toString() } ?: "",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier
                    .clickable {
                        isMenuOpen = true
                    }
                    .width(40.dp)
                    .height(40.dp)
                    .clip(
                        shape = RoundedCornerShape(
                            topStart = 50.dp,
                            topEnd = 50.dp,
                            bottomStart = 50.dp,
                            bottomEnd = 50.dp
                        )
                    )
                    .background(Color.White),
                textAlign = TextAlign.Center)



            DropdownMenu(
                expanded = isMenuOpen,
                onDismissRequest = { isMenuOpen = false },
                modifier = Modifier
                    .padding(1.dp)
                    .background(color = Color.White)
            ) {
                DropdownMenuItem(
                    onClick = {
                        isMenuOpen = false
                    },
                    text = { Text(text = "내 계정") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = ""
                        )
                    }
                )
                DropdownMenuItem(
                    onClick = {
                        isMenuOpen = false
                        navController.navigate(Screen.AddKeyword.route)
                    },
                    text = { Text(text = "설정") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = ""
                        )
                    }
                )
                DropdownMenuItem(
                    onClick = {
                        isMenuOpen = false
                        // Log out the user
                        try {
                            FirebaseAuth.getInstance().signOut()
                            coroutineScope.launch {
                                DataStoreManager.logout(context = activity)
                                activity.startActivity(Intent(activity, MainActivity::class.java))
                                activity.finish()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(activity, e.message, Toast.LENGTH_SHORT)
                                .show()
                        }
                    },
                    text = { Text(text = stringResource(R.string.log_out)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = ""
                        )
                    }
                )
            }
        }
    }
}
