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
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavHostController
import com.blueduck.dajumgum.R
import com.blueduck.dajumgum.model.User
import com.blueduck.dajumgum.preferences.DataStoreManager
import com.blueduck.dajumgum.ui.common.CustomDatePickerDialog
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
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
fun SignupScreen(navController: NavHostController, viewModel: AuthViewModel) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance().also { it.useAppLanguage() }
    val coroutineScope = rememberCoroutineScope()


    val SELECT = "Select"

    var phoneCode by remember { mutableStateOf(getDefaultPhoneCode(context)) }
    val phoneNumber = rememberSaveable { mutableStateOf("") }
    val fullPhoneNumber = remember { mutableStateOf("") }
    var defaultLang by rememberSaveable { mutableStateOf(getDefaultLangCode(context)) }

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var dateOfBirth by remember { mutableStateOf("") }
    var dateOfBirthInSecond by remember { mutableStateOf<Long>(0) }
    var selectedTitle by remember { mutableStateOf(SELECT) }
    var isTitleMenuExpanded by remember { mutableStateOf(false) }

    var openDatePicker by remember { mutableStateOf(false) }

    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }


    var verificationId by remember { mutableStateOf("") }
    var verificationInProgress by remember { mutableStateOf(false) }
    var verificationCompleted by remember { mutableStateOf(false) }
    var verificationFailed by remember { mutableStateOf(false) }
    var otp by remember { mutableStateOf("") }

    var isLoading by remember { mutableStateOf(false) }


    fun isInputValid(): Boolean {
        // Check if the name is not empty
        if (name.isEmpty()) {
            Toast.makeText(
                context,
                context.getString(R.string.please_enter_a_valid_name),
                Toast.LENGTH_SHORT
            ).show()
            return false
        }

        // Check if the email is not empty and in a valid format
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(
                context,
                context.getString(R.string.please_enter_a_valid_email_address),
                Toast.LENGTH_SHORT
            ).show()
            return false
        }

        // Check if the phone number is valid
        if (phoneNumber.value.length < 5) {
            Toast.makeText(
                context,
                context.getString(R.string.please_enter_a_valid_phone_number),
                Toast.LENGTH_SHORT
            ).show()
            return false
        } else {
            fullPhoneNumber.value = "$phoneCode${phoneNumber.value}"
        }

        // Check if the date of birth is valid
        if (dateOfBirthInSecond == 0.toLong()) {
            Toast.makeText(
                context,
                "생년월일을 입력하세요.",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }


        // Check if the title is not empty
        if (selectedTitle == SELECT) {
            Toast.makeText(
                context,
                context.getString(R.string.please_enter_a_valid_title),
                Toast.LENGTH_SHORT
            ).show()
            return false
        }

        // All validations passed, return true
        return true
    }

    fun createUser(id: String) {
       isLoading = true
        val user = User(id, name, email, fullPhoneNumber.value, dateOfBirthInSecond, selectedTitle/*, arrayListOf()*/)
        viewModel.saveUserToFirestore(user) { isAdded, e ->
            isLoading = false
            if (e != null) {
                Toast.makeText(context, e.message, Toast.LENGTH_SHORT)
                    .show()
            }
            if (isAdded) {
                coroutineScope.launch {
                    DataStoreManager.saveAppUser(context, user)
                    DataStoreManager.saveLoginStatus(context, true)
                }
            }
        }
    }


    fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(context as Activity) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("TAG", "signInWithCredential:success")

                    val id = task.result!!.user!!.uid
                    createUser(id)
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
            println("onVerificationFailed  ${exception.message}")
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
            .setActivity(context as Activity) // Activity (for callback binding)
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
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp),
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                    ) {
                                        Spacer(modifier = Modifier.height(32.dp))
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                        ) {
                                            Text(
                                                text = stringResource(R.string.signup),
                                                style = MaterialTheme.typography.displaySmall
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(16.dp))


                                        Text(
                                            text = stringResource(R.string.name), modifier = Modifier
                                                .fillMaxWidth()
                                      )

                                        Spacer(modifier = Modifier.height(4.dp))

                                        OutlinedTextField(
                                            value = name,
                                            onValueChange = { name = it },
                                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(height = 50.dp),
                                            shape = RoundedCornerShape(
                                                topStart = 50.dp,
                                                topEnd = 50.dp,
                                                bottomStart = 50.dp,
                                                bottomEnd = 50.dp
                                            ),
                                        )


                                        Spacer(modifier = Modifier.height(16.dp))

                                        Text(text = stringResource(R.string.date_of_birth), modifier = Modifier
                                            .fillMaxWidth())
                                        Spacer(modifier = Modifier.height(4.dp))
                                        OutlinedTextField(
                                            value = dateOfBirth,
                                            onValueChange = { dateOfBirth = it },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(height = 50.dp)
                                                .clickable { openDatePicker = true },
                                            shape = RoundedCornerShape(
                                                topStart = 50.dp,
                                                topEnd = 50.dp,
                                                bottomStart = 50.dp,
                                                bottomEnd = 50.dp
                                            ),
                                            enabled = false,
                                            colors = TextFieldDefaults.colors(
                                                disabledIndicatorColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                                disabledContainerColor = Color.Transparent,
                                                disabledTextColor = MaterialTheme.colorScheme.onSurface
                                            )
                                        )

                                        Spacer(modifier = Modifier.height(16.dp))

                                        Text(
                                            text = context.getString(R.string.phone_number), modifier = Modifier
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

                                        Spacer(modifier = Modifier.height(16.dp))

                                        Text(
                                            text = stringResource(R.string.email_address), modifier = Modifier
                                                .fillMaxWidth()
                                         )
                                        Spacer(modifier = Modifier.height(4.dp))

                                        OutlinedTextField(
                                            value = email,
                                            onValueChange = { email = it },
                                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(height = 50.dp),
                                            shape = RoundedCornerShape(
                                                topStart = 50.dp,
                                                topEnd = 50.dp,
                                                bottomStart = 50.dp,
                                                bottomEnd = 50.dp
                                            ),

                                        )


                                        Spacer(modifier = Modifier.height(16.dp))


                                        val titleOptions = listOf(stringResource(R.string.field_worker))

                                        Text(
                                            text = stringResource(R.string.title), modifier = Modifier
                                                .fillMaxWidth()
                                         )

                                        Spacer(modifier = Modifier.height(4.dp))

                                        Box {
                                            DropdownMenu(
                                                expanded = isTitleMenuExpanded,
                                                onDismissRequest = { isTitleMenuExpanded = false }
                                            ) {
                                                titleOptions.forEach { country ->
                                                    DropdownMenuItem(
                                                        text = {
                                                            Text(
                                                                text = country,
                                                                fontSize = 16.sp,
                                                                modifier = Modifier.fillMaxWidth()
                                                            )
                                                        },
                                                        onClick = {
                                                            selectedTitle = country
                                                            isTitleMenuExpanded = false
                                                        }
                                                    )
                                                }
                                            }

                                            OutlinedTextField(value = selectedTitle,
                                                enabled = false,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(height = 50.dp)
                                                    .clickable { isTitleMenuExpanded = true },
                                                shape = RoundedCornerShape(
                                                    topStart = 50.dp,
                                                    topEnd = 50.dp,
                                                    bottomStart = 50.dp,
                                                    bottomEnd = 50.dp
                                                ),
                                                colors = TextFieldDefaults.colors(
                                                    disabledIndicatorColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    disabledContainerColor = Color.Transparent,
                                                    disabledTextColor = MaterialTheme.colorScheme.onSurface
                                                ),
                                                onValueChange = { selectedTitle = it })
                                        }


                                        Spacer(modifier = Modifier.height(24.dp))

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
                                            onClick = {
                                                if (isInputValid()) {
                                                    isLoading = true
                                                    viewModel.checkUserExistence(fullPhoneNumber.value) { isExist, user, error ->
                                                        if (isExist) {
                                                            isLoading = false
                                                            showError = true
                                                            errorMessage = "이 전화번호를 가진 사용자가 이미 존재합니다."
                                                        } else {
                                                            startVerification(fullPhoneNumber.value)
                                                        }
                                                    }
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = colorResource(id = R.color.orange)
                                            ),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                         ) {
                                            Text(stringResource(R.string.sign_up))
                                        }

                                        Spacer(modifier = Modifier.height(32.dp))
                                    }

                                }
                            } else if (verificationInProgress && !verificationCompleted) {
                                item {
                                    Column(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                    ) {
                                        Spacer(modifier = Modifier.height(64.dp))
                                        Text(
                                            text = "OTP 검증",
                                            style = MaterialTheme.typography.displaySmall,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 16.dp)
                                        )

                                        Spacer(modifier = Modifier.height(4.dp))


                                        Text(
                                            text = "우리는 당신의 번호로 인증 코드를 보냈습니다", modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 16.dp)
                                        )

                                        Spacer(modifier = Modifier.height(16.dp))

                                        OutlinedTextField(value = otp,
                                            onValueChange = { otp = it },
                                            label = { Text(text = "OTP")},
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number,imeAction = ImeAction.Next),
                                                    modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 16.dp))

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
                                                .padding(horizontal = 16.dp)
                                        ) {
                                            Text(text = "코드 확인")
                                        }

                                        Spacer(modifier = Modifier.height(16.dp))
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

            if (openDatePicker) {
                CustomDatePickerDialog(onCancelClick = { openDatePicker = false }) { dateInFormat, dateInSecond ->
                    dateOfBirth = dateInFormat
                    dateOfBirthInSecond = dateInSecond
                    openDatePicker = false
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






