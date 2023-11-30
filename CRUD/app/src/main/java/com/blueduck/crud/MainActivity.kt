package com.blueduck.crud

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.blueduck.crud.model.Student
import com.blueduck.crud.ui.StudentListScreen
import com.blueduck.crud.ui.theme.CRUDTheme
import com.google.gson.Gson

class MainActivity : ComponentActivity() {

    private lateinit var studentViewModel: StudentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        studentViewModel = ViewModelProvider(this)[StudentViewModel::class.java]

        setContent {
            CRUDTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    StudentApp(studentViewModel, this)
                }
            }
        }
    }
}

@Composable
fun StudentApp(studentViewModel: StudentViewModel, mainActivity: MainActivity) {

    val navController = rememberNavController()

    // State to hold the list of students
    val studentsState = remember { mutableStateOf(emptyList<Student>()) }

    // Fetch the list of students initially
    LaunchedEffect(Unit) {
        studentViewModel.fetchStudents()
    }

    studentViewModel.students.observe(mainActivity) { students ->
        studentsState.value = students
    }


    // State to hold the selected student
    val selectedStudent = remember { mutableStateOf<Student?>(null) }

    // Function to handle student selection
    val onStudentSelected = { student: Student ->
        selectedStudent.value = student
    }

    NavHost(navController = navController, startDestination = "studentList") {
        composable("studentList") {
            // Your existing Composable for displaying the student list
            StudentListScreen(
                students = studentsState.value, // Replace with your actual list of students
                onStudentSelected = {
                    navController.navigate("studentDetail/${Gson().toJson(it)}")
                },
                onAddStudentClicked = {
                    navController.navigate("addStudent")
                },
                onDeleteStudentClicked = {
                    studentViewModel.removeStudent(it.phone)
                },
                onUpdateStudentClicked = {
                    navController.navigate("updateStudent/${Gson().toJson(it)}")
                }
            )
        }
        composable("addStudent") {
            // The Add Student screen
            AddStudentScreen(onAddStudent = { student ->
                // Handle adding the student, e.g., by calling a ViewModel method or performing necessary logic
                studentViewModel.addStudent(student)
                navController.navigateUp()
            })
        }
        composable("updateStudent/{student}", arguments = listOf(navArgument("student"){
            type = NavType.StringType
        })) {
            // The Add Student screen
            it.arguments?.getString("student")?.let { jsonString ->
                val student = Gson().fromJson(jsonString, Student::class.java)
                UpdateStudentScreen(student) { updatedStudent ->
                    studentViewModel.updateStudent(updatedStudent)
                    navController.navigateUp()
                }
            }
        }
        composable("studentDetail/{student}", arguments = listOf(navArgument("student"){
            type = NavType.StringType
        })) {
            it.arguments?.getString("student")?.let { jsonString ->
                val student = Gson().fromJson(jsonString, Student::class.java)
                StudentDetailsScreen(student)
            }
        }
    }
}

