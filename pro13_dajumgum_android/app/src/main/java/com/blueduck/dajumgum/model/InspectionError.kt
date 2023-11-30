package com.blueduck.dajumgum.model

data class InspectionError(
    val errorNumber: Int,
    var spaces: ArrayList<String>,
    var locations: ArrayList<String>,
    var defects: ArrayList<String>,
    var situations: ArrayList<String>,
    var defectDetails: String,
    var image1Url: String,
    var image2Url: String
    )
