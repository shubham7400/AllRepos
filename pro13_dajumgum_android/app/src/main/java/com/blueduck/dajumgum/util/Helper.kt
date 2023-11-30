package com.blueduck.dajumgum.util

import com.blueduck.dajumgum.model.Customer
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun convertMillisToDate(millis: Long, pattern: String = "yyyy-MM-dd"): String {
    val sdf = SimpleDateFormat(pattern, Locale.getDefault())
    return sdf.format(Date(millis))
}

fun convertJsonToCustomerArray(json: String) : ArrayList<Customer> {
    val listType = object : TypeToken<ArrayList<Customer>>() {}.type
    return Gson().fromJson(json, listType)
}

fun convertJsonToStringArray(json: String) : ArrayList<String> {
    return try {
        val listType = object : TypeToken<ArrayList<String>>() {}.type
        Gson().fromJson(json, listType)
    }catch (e: Exception){
        arrayListOf()
    }
}


fun <A> A.toJson(): String? {
    return Gson().toJson(this)
}

fun <A> String.fromJson(type: Class<A>): A {
    return Gson().fromJson(this, type)
}