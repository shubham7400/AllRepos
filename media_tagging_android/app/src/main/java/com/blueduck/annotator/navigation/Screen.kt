package com.blueduck.annotator.navigation

sealed class Screen( val route: String) {
    object Login: Screen("login_screen")
    object Shared: Screen("shared_screen")
    object Notification: Screen("notification_screen")
    object Home: Screen("home_screen")
    object LocalProject: Screen("local_project_screen")
    object CloudProject: Screen("cloud_project_screen")
    object DriveProject: Screen("drive_project_screen")
    object Logout: Screen("logout")
    object Files: Screen("file_list_screen")
    object SingleFile: Screen("single_file_screen")
    object ZoomFile: Screen("zoom_file_screen")
    object ProjectDetail: Screen("project_detail")
    object FileDetail: Screen("file_detail_screen")
    object Search: Screen("search_screen")
}

enum class BottomBarScreen(val route: String){
    Home("home_screen"),
    Shared("shared_screen"),
    MyFile("my_file_screen"),
    Notification("notification_screen")
}