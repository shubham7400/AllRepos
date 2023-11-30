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
import com.blueduck.annotator.encryption.EncryptionAndDecryption
import com.blueduck.annotator.model.Project

@Composable
fun OpenProjectDialog(project: Project, onAuthSuccess: (Boolean) -> Unit, onCloseClick: () -> Unit) {
    val context = LocalContext.current
    var password by remember { mutableStateOf("") }

    Dialog(onDismissRequest = { }) {
        Surface(shape = MaterialTheme.shapes.medium) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth()
            ) {

                Text(
                    text = "Authenticate",
                    style = MaterialTheme.typography.headlineSmall,
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Enter password",
                    style = MaterialTheme.typography.bodyLarge
                )

                OutlinedTextField(value = password, onValueChange = { password = it} )

                Spacer(modifier = Modifier.height(24.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = { onCloseClick() }, content = { Text(text = "Cancel") })
                    TextButton(onClick = {
                                         if (EncryptionAndDecryption.decryptPassword(project.projectLockPassword) == password.trim()){
                                             onAuthSuccess(true)
                                         }else{
                                             Toast.makeText(context, "Please Enter correct password.", Toast.LENGTH_SHORT).show()
                                         }
                                         }, content = { Text(text = "Ok") })
                }
            }
        }

    }
}