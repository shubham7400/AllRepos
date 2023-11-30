package com.blueduck.annotator.screens.auth


import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.auth.api.identity.BeginSignInResult
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.blueduck.annotator.HomeActivity
import com.blueduck.annotator.R
import com.blueduck.annotator.enums.UserType
import com.blueduck.annotator.model.User
import com.blueduck.annotator.preferences.setUser
import com.blueduck.annotator.util.Response
import java.lang.Exception

@Composable
fun SignInScreen(isExpandedScreen: Boolean, viewModel: MainViewModel = hiltViewModel()) {
    var isLoading by remember { mutableStateOf(false) }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        ConstraintLayout(modifier = Modifier.fillMaxSize()) {
            val (mainContent, progressBar) = createRefs()

            if (isExpandedScreen){
                TabletLayout(viewModel, isLoading, updateLoading = { isLoading = it})
            }else{
                MobileLayout(viewModel, isLoading, updateLoading = { isLoading = it})
            }
        }
    }
}

@Composable
fun MobileLayout(viewModel: MainViewModel, isLoading: Boolean, updateLoading: (Boolean) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center) {
        LogoComposable()
        GoogleSignUpButton(viewModel, isLoading, updateLoading = updateLoading)
    }
}

@Composable
fun TabletLayout(viewModel: MainViewModel, isLoading: Boolean, updateLoading: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LogoComposable()

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "Annotator",
                style = LocalTextStyle.current.copy(
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                ),
                maxLines = 1
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            GoogleSignUpButton(viewModel, isLoading, updateLoading = updateLoading)
        }
    }
}

@Composable
fun LogoComposable() {
    Image(
        painter = painterResource(id = R.drawable.ic_app_logo),
        contentDescription = "App Logo",
        modifier = Modifier.padding(60.dp, 30.dp, 60.dp, 70.dp)
    )
}

@Composable
fun GoogleSignUpButton (viewModel: MainViewModel, isLoading: Boolean, updateLoading: (Boolean) -> Unit) {

    val context = LocalContext.current

     fun updateUI(firebaseUser: FirebaseUser?) {
        if (firebaseUser != null){
            try {
                val profile = firebaseUser.providerData[0]

                // UID specific to the provider
                val uid = profile.uid

                // Name, email address, and profile photo Url
                val name = profile.displayName ?: ""
                val email = profile.email ?: ""
                val photoUrl = profile.photoUrl?.toString() ?: ""

                Log.i("TAG", "updateUI: dsjfddsfds $uid \n $uid \n $name \n $email \n $photoUrl")
                viewModel.isUserAlreadyExist(uid){ exist ->
                    when(exist){
                        is Response.Loading -> { updateLoading(true)}
                        is Response.Success -> {
                            if (exist.data == null){
                                updateLoading(false)
                                val user = User(uid, name, photoUrl, email, System.currentTimeMillis(), System.currentTimeMillis(), false, "")
                                User(
                                    id = uid,
                                    name = name,
                                    profileImage = photoUrl,
                                    email = email,
                                    createdAt = System.currentTimeMillis(),
                                    lastSeen = System.currentTimeMillis(),
                                    userStatus = false,
                                    userType = UserType.ANNOTATOR.value
                                )
                                viewModel.createNewUser(user) { created ->
                                    when(created) {
                                        is Response.Loading -> { updateLoading(true) }
                                        is Response.Success -> {
                                            val newUser = created.data!!
                                            context.setUser(newUser)
                                            context.startActivity(Intent(context, HomeActivity::class.java))
                                            (context as MainActivity).finishAffinity()
                                        }
                                        is Response.Failure -> { println(created.e) }
                                    }
                                }
                            }else{
                                updateLoading(false)
                                val user = exist.data
                                context.setUser(user)
                                context.startActivity(Intent(context, HomeActivity::class.java))
                                (context as MainActivity).finishAffinity()
                            }
                        }
                        is Response.Failure -> {
                            updateLoading(false)
                            println("priddntln ${exist.e}")
                        }
                    }
                }
            }catch (e: Exception){}
        }else{
            Log.i("TAG", "updateUI: dkfdkfdk")
        }
    }


    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            try {
                val credentials = viewModel.oneTapClient.getSignInCredentialFromIntent(result.data)
                val googleIdToken = credentials.googleIdToken
                val firebaseCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
                viewModel.signInWithGoogle(firebaseCredential){ signInWithGoogleResponse ->
                    when(signInWithGoogleResponse) {
                        is Response.Loading -> { updateLoading(true) }
                        is Response.Success -> signInWithGoogleResponse.data?.let {
                            updateLoading(false)
                            updateUI(it)
                        }
                        is Response.Failure ->  {
                            updateLoading(false)
                            println(signInWithGoogleResponse.e)
                        }
                    }
                }
            } catch (it: ApiException) {
                updateLoading(false)
                print(it)
            }
        }
    }

    fun beginSignInResult(it: BeginSignInResult) {
        val intent = IntentSenderRequest.Builder(it.pendingIntent.intentSender).build()
        launcher.launch(intent)
    }



    Surface(
        modifier = Modifier.clickable {
            viewModel.oneTapSignIn { oneTapSignInResponse ->
                when(oneTapSignInResponse) {
                    is Response.Loading -> { updateLoading(true) }
                    is Response.Success -> oneTapSignInResponse.data?.let {
                        updateLoading(false)
                        beginSignInResult(it)
                    }
                    is Response.Failure -> {
                        updateLoading(false)
                        println(oneTapSignInResponse.e)
                    }
                }
            }
                                      },
        shape = ShapeDefaults.Medium,
        border = BorderStroke(1.dp, Color.LightGray)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center, modifier = Modifier.padding(16.dp, 12.dp, 24.dp, 12.dp)) {
            Icon(painter = painterResource(id = R.drawable.ic_google_logo), contentDescription = "google icon", tint = Color.Unspecified)
            Spacer(modifier = Modifier.size(8.dp))
            Text(text = "Sign In with Google", style = MaterialTheme.typography.labelLarge, fontSize = 20.sp)
            if (isLoading) {
                Spacer(modifier = Modifier.width(16.dp))
                CircularProgressIndicator(
                    modifier = Modifier
                        .height(16.dp)
                        .width(16.dp),
                    strokeWidth = 2.dp,
                )
            }
        }
     }
}








