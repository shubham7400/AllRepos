package com.blueduck.annotator.dialog

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun LockUnlockProjectDialog(onCloseClick: () -> Unit, lockProject: (String) -> Unit,  unlockProject: (String) -> Unit,projectLockPassword: String) {
    val context = LocalContext.current
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    Dialog(onDismissRequest = { }) {
        Surface(shape = MaterialTheme.shapes.medium) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth()
            ) {

                Text(
                    text = if (projectLockPassword.isEmpty()) "Lock the project" else "Unlock the project",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Enter password",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    modifier = Modifier.fillMaxWidth()
                )

                if (projectLockPassword.isEmpty()){
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Enter confirm password",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = { onCloseClick() }, content = { Text(text = "Cancel") })
                    TextButton(onClick = {
                        if (projectLockPassword.isNotEmpty()){
                            if (password == projectLockPassword){
                                unlockProject("")
                            }
                        }else{
                            if (password.isEmpty()){
                                Toast.makeText(context, "Please enter the password", Toast.LENGTH_SHORT).show()
                            }else if (password != confirmPassword){
                                Toast.makeText(context, "Password does not match", Toast.LENGTH_SHORT).show()
                            }else{
                                lockProject(password.trim())
                            }

                        }
                                          }, content = { Text(text = if (projectLockPassword.isEmpty()) "Lock" else "Unlock") })
                }
            }
        }

    }

}