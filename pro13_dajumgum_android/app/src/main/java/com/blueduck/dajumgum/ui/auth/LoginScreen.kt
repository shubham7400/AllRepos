package com.blueduck.dajumgum.ui.auth

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.blueduck.dajumgum.R
import com.blueduck.dajumgum.model.User
import com.blueduck.dajumgum.enums.Screen
import com.blueduck.dajumgum.preferences.DataStoreManager
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.simon.xmaterialccp.component.MaterialCountryCodePicker
import com.simon.xmaterialccp.data.ccpDefaultColors
import com.simon.xmaterialccp.data.utils.getDefaultLangCode
import com.simon.xmaterialccp.data.utils.getDefaultPhoneCode
import com.simon.xmaterialccp.data.utils.getLibCountries

import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit


@Composable
fun LoginScreen(navController: NavHostController, viewModel: AuthViewModel) {
    val auth = viewModel.firebaseAuth.also { it.useAppLanguage() }

    val context = LocalContext.current as Activity

    var isLoading by remember { mutableStateOf(false) }


    // Create a coroutine scope
    val coroutineScope = rememberCoroutineScope()

    var phoneCode by remember { mutableStateOf(getDefaultPhoneCode(context)) }
    val phoneNumber = rememberSaveable { mutableStateOf("") }
    val fullPhoneNumber = remember { mutableStateOf("") }
    var defaultLang by rememberSaveable { mutableStateOf(getDefaultLangCode(context)) }


    var verificationId by remember { mutableStateOf("") }
    var verificationInProgress by remember { mutableStateOf(false) }
    var verificationCompleted by remember { mutableStateOf(false) }
    var verificationFailed by remember { mutableStateOf(false) }
    var otp by remember { mutableStateOf("") }

    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    var user by remember { mutableStateOf<User?>(null) }


    fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(context) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("TAG", "signInWithCredential:success")

                    val u = task.result?.user

                    if (user != null){
                        coroutineScope.launch {
                            DataStoreManager.saveAppUser(context, user!!)
                            DataStoreManager.saveLoginStatus(context, true)
                        }
                    }
                } else {
                    // Sign in failed, display a message and update the UI
                    Log.w("TAG", "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                        Toast.makeText(
                          context,
                           "The verification code entered was invalid",
                           Toast.LENGTH_SHORT
                       ).show()
                    }
                    // Update UI
                }
            }
    }


    val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            // This callback will be invoked in two situations:
            // 1. Instant verification. In some cases, the phone number can be instantly verified
            //    without requiring the user to enter the OTP.
            // 2. Auto-retrieval. If the device has a phone number associated with it, the OTP
            //    will be automatically retrieved.
            // You can handle both cases here, or simply use onVerificationCompleted for simplicity.
            isLoading = false
            verificationCompleted = true
            signInWithPhoneAuthCredential(credential = credential)
        }

        override fun onVerificationFailed(exception: FirebaseException) {
            // Called when an error occurred during verification.
            // Handle the error cases here and provide appropriate feedback to the user.
            // For example, you can set verificationFailed to true and display an error message.
            isLoading = false
            verificationFailed = true
            Toast.makeText(context, exception.message, Toast.LENGTH_SHORT).show()
        }

        override fun onCodeSent(vId: String, token: PhoneAuthProvider.ForceResendingToken) {
            // The SMS verification code has been sent to the provided phone number.
            // You can save the verificationId to use it later when verifying the OTP.
            // In this example, we will assume the verification process is in progress.
            isLoading = false
            verificationId = vId
            verificationInProgress = true
        }
    }

    fun startVerification(phoneNumber: String) {
        // Start the phone number verification process using Firebase.
        // The verification code will be sent to the provided phone number.
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber) // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(context) // Activity (for callback binding)
            .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { p ->
        ConstraintLayout(modifier = Modifier
            .fillMaxSize()
            .padding(p)
            .background(color = MaterialTheme.colorScheme.primary)) {
            val (mainContent, progressBar) = createRefs()

            Column(modifier = Modifier
                .constrainAs(mainContent) {
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)
                }
                .fillMaxSize()) {
                Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Spacer(modifier = Modifier.height(32.dp))
                    Image(painter = painterResource(id = R.drawable.splash_logo), contentDescription = "")
                    Spacer(modifier = Modifier.height(32.dp))
                }
                Column(modifier = Modifier
                    .fillMaxSize(),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    LazyColumn(modifier = Modifier
                        .clip(shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
                        .fillMaxWidth()
                        .background(color = Color.White),
                        content = {
                            if (!verificationInProgress && !verificationCompleted) {
                                item {
                                    Column(
                                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                    ) {
                                        Spacer(modifier = Modifier.height(64.dp))

                                        Text(
                                            text = stringResource(R.string.signup),
                                            style = MaterialTheme.typography.displaySmall,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                        )

                                        Spacer(modifier = Modifier.height(4.dp))

                                        Text(
                                            text = "모바일 번호 입력",
                                            modifier = Modifier
                                                .fillMaxWidth()
                                        )

                                        Spacer(modifier = Modifier.height(4.dp))

                                        MaterialCountryCodePicker(
                                            text = phoneNumber.value,
                                            onValueChange = { phoneNumber.value = it},
                                            defaultCountry = getLibCountries().single { it.countryCode == defaultLang },
                                            pickedCountry =  {
                                                phoneCode = it.countryPhoneCode
                                                defaultLang = it.countryCode
                                            },
                                            modifier = Modifier.height(50.dp),
                                            colors = ccpDefaultColors(
                                                backgroundColor = Color.Transparent,
                                                surfaceColor = Color.Transparent,
                                                textColor =MaterialTheme.colorScheme.onSurface,
                                                cursorColor =MaterialTheme.colorScheme.primary,
                                                topAppBarColor =MaterialTheme.colorScheme.surface,
                                                countryItemBgColor =MaterialTheme.colorScheme.surface,
                                                searchFieldBgColor =MaterialTheme.colorScheme.surface,
                                            ),
                                            flagPadding = PaddingValues(15.dp),
                                            textFieldShapeCornerRadiusInPercentage = 50
                                        )


                                        if (showError) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth(),
                                                horizontalArrangement = Arrangement.Start,
                                                verticalAlignment = Alignment.CenterVertically,
                                            ) {
                                                Icon(
                                                    painter = painterResource(id = R.drawable.ic_error),
                                                    contentDescription = "",
                                                    tint = Color.Red
                                                )

                                                Spacer(modifier = Modifier.width(4.dp))

                                                Text(
                                                    text = errorMessage,
                                                    color = Color.Red,
                                                    style = MaterialTheme.typography.titleMedium,
                                                )
                                            }
                                        }


                                        Spacer(modifier = Modifier.height(16.dp))

                                        ElevatedButton(
                                            modifier = Modifier
                                                .fillMaxWidth(),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = colorResource(id = R.color.orange)
                                            ),
                                            onClick = {
                                                if (phoneNumber.value.length > 5) {
                                                    fullPhoneNumber.value = "$phoneCode${phoneNumber.value}"
                                                    // first check if account exist with this mobile number or not, if not then tell user to register first
                                                    isLoading = true
                                                    viewModel.checkUserExistence(fullPhoneNumber.value) { isExist, usr, error ->
                                                        isLoading = false
                                                        if (isExist) {
                                                            user = usr
                                                            isLoading = true
                                                            startVerification(fullPhoneNumber.value)
                                                        } else {
                                                            showError = true
                                                            errorMessage =
                                                                "이 휴대폰 번호로 계정이 존재하지 않습니다."
                                                        }
                                                    }
                                                }
                                            }
                                        ) {
                                            Text(text = stringResource(R.string.log_in))
                                        }



                                        Spacer(modifier = Modifier.height(24.dp))

                                        Text(text = stringResource(R.string.do_not_have_account),
                                            color = colorResource(id = R.color.blue),
                                            modifier = Modifier.clickable {
                                                navController.navigate(Screen.SignupScreen.route) {
                                                    popUpTo(navController.graph.findStartDestination().id) { }
                                                    launchSingleTop = true
                                                }
                                            }
                                        )

                                        Spacer(modifier = Modifier.height(32.dp))
                                    }

                                }

                            } else if (verificationInProgress && !verificationCompleted) {
                                item {
                                    Column(modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp)) {
                                        Spacer(modifier = Modifier.height(64.dp))

                                        Text(
                                            text = "휴대 전화 번호 확인",
                                            style = MaterialTheme.typography.displaySmall,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                        )

                                        Spacer(modifier = Modifier.height(4.dp))

                                        Text(
                                            text = "우리는 당신의 번호로 인증 코드를 보냈습니다",
                                            modifier = Modifier
                                                .fillMaxWidth()
                                        )

                                        Spacer(modifier = Modifier.height(16.dp))

                                        OutlinedTextField(value = otp,
                                            onValueChange = { otp = it },
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number,imeAction = ImeAction.Next),
                                                    label = { Text(text = "OTP")},
                                            modifier = Modifier
                                                .fillMaxWidth())

                                        Spacer(modifier = Modifier.height(32.dp))

                                        ElevatedButton(
                                            onClick = {
                                                if (otp.isNotEmpty()) {
                                                    val credential =
                                                        PhoneAuthProvider.getCredential(verificationId, otp)
                                                    signInWithPhoneAuthCredential(credential = credential)
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = colorResource(id = R.color.orange)
                                            ),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                        ) {
                                            Text(text = "코드 확인")
                                        }

                                        Spacer(modifier = Modifier.height(32.dp))

                                    }
                                }
                            } else {
                                item {
                                    Text("Verification Successful!", modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(20.dp), textAlign = TextAlign.Center)
                                }
                                // Navigate to the next screen or perform further actions here
                            }

                        })
                }
            }

            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .background(color = Color.Transparent)
                        .constrainAs(progressBar) {
                            bottom.linkTo(parent.bottom)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            top.linkTo(parent.top)
                        },
                    color = colorResource(id = R.color.orange)
                )
            }
        }
    }

}