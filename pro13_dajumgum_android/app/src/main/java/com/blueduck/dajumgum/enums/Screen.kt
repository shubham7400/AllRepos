package com.blueduck.dajumgum.enums

enum class Screen( val route: String) {
    LoginScreen("login_screen"),
    SignupScreen("signup_screen"),

    CreateCustomer("customer_information_screen"),
    AddKeyword("add_keyword_screen"),
    DefectList("defect_list_screen"),
    CreateInspectionDefect("create_inspection_defect_screen"),
    CreateTemperatureDefect("create_temperature_defect_screen"),
    CreateAirConditionerDefect("create_air_conditioner_screen"),
    InspectionReport("inspection_report_screen"),
    PdfView("pdf_view_screen"),
}

enum class BottomBarScreen(val route: String){
    Home("home_screen"),
    Inspection("inspection_screen"),
    Schedule("schedule_screen"),
    Chat("chat_screen"),
}