package com.lock.blueduck.applock.repository

import androidx.lifecycle.LiveData
import com.lock.blueduck.applock.data.AppInfoDatabase
import com.lock.blueduck.applock.model.AppInfo
import javax.inject.Inject

class Repository @Inject constructor( private val database: AppInfoDatabase) {


    fun getAppList() : LiveData<List<AppInfo>> {
        return database.appInfoDao().getAppList()
    }

    suspend fun addAllApps(unlockedApps: ArrayList<AppInfo>) {
        database.appInfoDao().addAllApps(unlockedApps)
    }

    suspend fun addApp(app: AppInfo) {
        database.appInfoDao().addApp(app)
    }

     suspend fun getAppsById(packageName: String): List<AppInfo> {
         return database.appInfoDao().getAppById(packageName)
    }

    suspend fun deleteAllApps() {
        database.appInfoDao().deleteAllApps()
    }

    suspend fun updateAppInfo(app: AppInfo) {
        database.appInfoDao().updateAppInfo(app)
    }

    fun getAllLockedApps(): List<AppInfo> {
        return database.appInfoDao().getAllLockedApps(true)
    }
}