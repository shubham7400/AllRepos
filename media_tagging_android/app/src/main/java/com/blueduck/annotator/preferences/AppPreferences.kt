package com.blueduck.annotator.preferences

import android.content.Context
import com.google.gson.Gson
import com.blueduck.annotator.model.User


private const val FILE_NAME = "app_pref"

fun Context.logOutUser(){
    val sharedPreferences = getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.clear();
    editor.commit();
}

fun Context.getFolderCreatedAt(): Long {
    val sharedPreferences = getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
    return sharedPreferences.getLong("folder_created_at", 0)
}

fun Context.setFolderCreatedAt(createdAt: Long) {
    val sharedPreferences = getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putLong("folder_created_at", createdAt).apply()
}

fun Context.getTagCreatedAt(): Long {
    val sharedPreferences = getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
    return sharedPreferences.getLong("tag_created_at", 0)

}

fun Context.setTagCreatedAt(createdAt: Long) {
    val sharedPreferences = getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putLong("tag_created_at", createdAt).apply()
}
fun Context.getFileCreatedAt(): Long {
    val sharedPreferences = getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
    return sharedPreferences.getLong("file_created_at", 0)

}

fun Context.setFileCreatedAt(createdAt: Long) {
    val sharedPreferences = getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putLong("file_created_at", createdAt).apply()
}

fun Context.getUser(): User? {
    val sharedPreferences = getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
     val json = sharedPreferences.getString("user", null)
    return if (json != null){
        Gson().fromJson(json, User::class.java)
    }else{
        null
    }
}

fun Context.setUser(user: User) {
    val sharedPreferences = getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()

    val json = Gson().toJson(user)
    editor.putString("user", json).apply()
}

