package com.blueduck.dajumgum.ui.common

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import com.blueduck.dajumgum.util.convertMillisToDate


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDatePickerDialog(onCancelClick: () -> Unit, onDateSelection: (String, Long) -> Unit) {
    val datePickerState = rememberDatePickerState()
    val confirmEnabled = remember {
        derivedStateOf { datePickerState.selectedDateMillis != null }
    }
    DatePickerDialog(
        onDismissRequest = {
            onCancelClick()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val date = datePickerState.selectedDateMillis?.let {
                        convertMillisToDate(
                            it
                        )
                    }
                    onDateSelection(date!!, (datePickerState.selectedDateMillis!! / 1000))
                },
                enabled = confirmEnabled.value
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onCancelClick()
                }
            ) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}