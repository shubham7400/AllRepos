package com.blueduck.crud

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.BottomSheetState
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blueduck.crud.model.Student
import kotlinx.coroutines.launch

class StudentViewModel : ViewModel() {

    private val _students = MutableLiveData<List<Student>>()
    val students: LiveData<List<Student>> get() = _students


    private val coroutineScope = viewModelScope

    fun fetchStudents() {
        coroutineScope.launch {
            try {
                val students = ApiClient.studentApiService.getStudents()
                _students.value = students
            } catch (e: Exception) {
                // Handle the error
                println("fdsfsdd ${e.message}")
            }
        }
    }

    fun addStudent(student: Student) {
        coroutineScope.launch {
            try {
                val added = ApiClient.studentApiService.createStudent(student = student)
                if (added){
                    if (_students.value != null){
                        _students.value = ArrayList(_students.value!!).apply { this.add(student) }
                    }else{
                        _students.value = listOf(student)
                    }
                }
            }catch (e: Exception){
                println("fdsfsdf ${e.message}")
            }
        }
    }

    fun removeStudent(phone: Int) {
        coroutineScope.launch {
            try {
                val removed = ApiClient.studentApiService.removeStudent(phone)
                if (removed){
                     _students.value?.let {
                         val list = ArrayList(it)
                         list.removeIf { student -> student.phone == phone }
                         _students.value = list
                     }
                }
            }catch (e: Exception){
                println("fdsfssdsdf ${e.message}")
            }
        }
    }

    fun updateStudent(student: Student) {
        coroutineScope.launch {
            try {
                val updated = ApiClient.studentApiService.updateStudent(student)
                if (updated){
                    val list = ArrayList(_students.value!!)
                    list.removeIf { it.phone == student.phone }
                    list.add(student)
                    _students.value = list
                }
            }catch (e: Exception){
                println("fdsfsjjsdsdf ${e.message}")
            }
        }
    }
}

