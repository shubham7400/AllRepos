package com.blueduck.crud.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.blueduck.crud.model.Student

@Composable
fun StudentDetailScreen(student: Student) {
    // Display student details
    Column {
        Text(text = "Name: ${student.name}")
        Text(text = "phone: ${student.phone}")
        Text(text = "email: ${student.email}")
        Text(text = "gender: ${student.gender}")
    }
}
