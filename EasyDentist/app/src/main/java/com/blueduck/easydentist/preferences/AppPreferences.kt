package com.blueduck.easydentist.preferences

import android.content.Context
import com.blueduck.easydentist.model.AppUser
import com.google.gson.Gson

private const val FILE_NAME = "app_pref"

// returns the login status of user that he is login or not
fun Context.getUserLoginStatus(): Boolean {
    val sharedPreferences = getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
    return sharedPreferences.getBoolean("user_login_status", false)
}

fun Context.setUserLoginStatus(isLogin: Boolean) {
    val sharedPreferences = getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putBoolean("user_login_status", isLogin).apply()
}


// return the current user that is logged in (in present)
fun Context.getUser(): AppUser? {
    val sharedPreferences = getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
    val userJsonString = sharedPreferences.getString("app_user_object", "") ?: ""
    return if (userJsonString.isEmpty()) {
        null
    }else{
         Gson().fromJson(userJsonString, AppUser::class.java)
    }
}

fun Context.setUser(user: AppUser) {
    val userJsonString = Gson().toJson(user)
    val sharedPreferences = getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putString("app_user_object", userJsonString).apply()
}

