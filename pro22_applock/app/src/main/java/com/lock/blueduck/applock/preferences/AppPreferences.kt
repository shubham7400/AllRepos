package com.lock.blueduck.applock.preferences

import android.content.Context

private const val FILE_NAME = "app_pref"

fun Context.getTouchLockEnabledStatus(): Boolean {
    val sharedPreferences = getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
    return sharedPreferences.getBoolean("touch_lock", false)
}

fun Context.setTouchLockEnabledStatus(touchLock: Boolean) {
    val sharedPreferences = getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putBoolean("touch_lock", touchLock).apply()
}

fun Context.getPatternLockPath(): String {
    val sharedPreferences = getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
    return sharedPreferences.getString("pattern_lock_path", "") ?: ""

}

fun Context.setPatternLockPath(pattern: String) {
    val sharedPreferences = getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putString("pattern_lock_path", pattern).apply()
}

fun Context.getLockedAppsPackages(): ArrayList<String> {
    val sharedPreferences = getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
    val set = sharedPreferences.getStringSet("locked_apps", setOf()) ?: setOf()
    return ArrayList(set)

}

fun Context.setLockedAppsPackages(lockedAppsPackages: ArrayList<String>) {
    val sharedPreferences = getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    val set: MutableSet<String> = HashSet()
    set.addAll(lockedAppsPackages)
    editor.putStringSet("locked_apps", set).apply()
}

fun Context.getAppLanguage(): String {
    val sharedPreferences = getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
    return sharedPreferences.getString("app_language", "") ?: ""

}

fun Context.setAppLanguage(language: String) {
    val sharedPreferences = getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putString("app_language", language).apply()
}

fun Context.getWrongPatternLimit(): Int {
    val sharedPreferences = getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
    return sharedPreferences.getInt("wrong_pattern_limit", 3)

}

fun Context.setWrongPatternLimit(limit: Int) {
    val sharedPreferences = getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putInt("wrong_pattern_limit", limit).apply()
}

fun Context.getActiveActivityAlias(): String {
    val sharedPreferences = getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
    return sharedPreferences.getString("activeActivityAlias", ".MainActivity") ?: ".MainActivity"
}

fun Context.setActiveActivityAlias(alias: String) {
    val sharedPreferences = getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putString("activeActivityAlias", alias).apply()
}

fun Context.isFakeIconSuggestionAcknowledge(): Boolean {
    val sharedPreferences = getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
    return sharedPreferences.getBoolean("fake_icon_suggestion_acknowledge", false)
}

fun Context.setFakeIconSuggestionAcknowledge(status: Boolean) {
    val sharedPreferences = getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putBoolean("fake_icon_suggestion_acknowledge", status).apply()
}


fun Context.isAuthOptionPattern(): Boolean {
    val sharedPreferences = getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
    return sharedPreferences.getBoolean("auth_option", true)
}

fun Context.setAuthOptionPattern(status: Boolean) {
    val sharedPreferences = getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putBoolean("auth_option", status).apply()
}

fun Context.isAuthTypePattern(): Boolean {
    val sharedPreferences = getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
    return sharedPreferences.getBoolean("auth_type", true)
}

fun Context.setAuthTypePattern(status: Boolean) {
    val sharedPreferences = getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putBoolean("auth_type", status).apply()
}

fun Context.getOpenedAppPackage(): String {
    val sharedPreferences = getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
    return sharedPreferences.getString("opened_app_package", "") ?: ""
}

fun Context.setOpenedAppPackage(alias: String) {
    val sharedPreferences = getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putString("opened_app_package", alias).apply()
}