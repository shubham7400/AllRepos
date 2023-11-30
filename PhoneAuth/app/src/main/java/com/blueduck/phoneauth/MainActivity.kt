package com.blueduck.phoneauth

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.blueduck.phoneauth.ui.theme.PhoneAuthTheme
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PhoneAuthTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PhoneAuthenticationScreen()
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhoneAuthenticationScreen() {
    val context = LocalContext.current
    val activity = (context as MainActivity)

    val auth = FirebaseAuth.getInstance().also { it.useAppLanguage() }

    var phoneNumber by remember { mutableStateOf("") }
    var verificationId by remember { mutableStateOf("") }
    var verificationInProgress by remember { mutableStateOf(false) }
    var verificationCompleted by remember { mutableStateOf(false) }
    var verificationFailed by remember { mutableStateOf(false) }

    val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            // This callback will be invoked in two situations:
            // 1. Instant verification. In some cases, the phone number can be instantly verified
            //    without requiring the user to enter the OTP.
            // 2. Auto-retrieval. If the device has a phone number associated with it, the OTP
            //    will be automatically retrieved.
            // You can handle both cases here, or simply use onVerificationCompleted for simplicity.
            verificationCompleted = true
        }

        override fun onVerificationFailed(exception: FirebaseException) {
            // Called when an error occurred during verification.
            // Handle the error cases here and provide appropriate feedback to the user.
            // For example, you can set verificationFailed to true and display an error message.
            verificationFailed = true
        }

        override fun onCodeSent(vId: String, token: PhoneAuthProvider.ForceResendingToken) {
            // The SMS verification code has been sent to the provided phone number.
            // You can save the verificationId to use it later when verifying the OTP.
            // In this example, we will assume the verification process is in progress.
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
            .setActivity(activity) // Activity (for callback binding)
            .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("TAG", "signInWithCredential:success")

                    val user = task.result?.user
                } else {
                    // Sign in failed, display a message and update the UI
                    Log.w("TAG", "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                        Toast.makeText(context, "The verification code entered was invalid", Toast.LENGTH_SHORT).show()
                    }
                    // Update UI
                }
            }
    }

    Scaffold(
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it),
                verticalArrangement = Arrangement.Center
            ) {
                if (!verificationInProgress && !verificationCompleted) {
                    PhoneInput(
                        phoneNumber = phoneNumber,
                        onPhoneNumberChanged = { pn -> phoneNumber = pn }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    VerifyButton(
                        phoneNumber = phoneNumber,
                        onVerificationStarted = { phoneNumber -> startVerification(phoneNumber) }
                    )
                } else if (verificationInProgress && !verificationCompleted) {
                    VerifyOTP(onVerifyOtpClick = { otp ->
                        val credential = PhoneAuthProvider.getCredential(verificationId, otp)
                        signInWithPhoneAuthCredential(credential = credential)
                    })
                } else {
                    Text("Verification Successful!")
                    // Navigate to the next screen or perform further actions here
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhoneInput(
    phoneNumber: String,
    onPhoneNumberChanged: (String) -> Unit
) {
    OutlinedTextField(
        value = phoneNumber,
        onValueChange = onPhoneNumberChanged,
        label = { Text("Phone Number") }
    )
}

@Composable
fun VerifyButton(
    phoneNumber: String,
    onVerificationStarted: (String) -> Unit
) {
    val scope = rememberCoroutineScope()

    Button(
        onClick = {
            scope.launch {
                onVerificationStarted(phoneNumber)
            }
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Verify Phone Number")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerifyOTP(onVerifyOtpClick: (String) -> Unit, ) {
    var otp by remember { mutableStateOf("") }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = otp,
            onValueChange = { value ->
                otp = value
            },
            label = { Text("Enter OTP") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
               onVerifyOtpClick(otp)
            }
        ) {
            Text("Verify OTP")
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
