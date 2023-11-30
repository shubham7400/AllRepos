package com.lock.blueduck.applock.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.lock.blueduck.applock.model.AppInfo
import com.lock.blueduck.applock.util.Constants.APP_INFO_TABLE


@Dao
interface AppInfoDao {

    @Query("SELECT * FROM $APP_INFO_TABLE")
    fun getAppList() : LiveData<List<AppInfo>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun addAllApps( apps: List<AppInfo>)

    @Insert
    fun addApp( apps: AppInfo)

    @Query("SELECT * FROM $APP_INFO_TABLE WHERE packageName == :packageName")
    suspend fun getAppById(packageName: String): List<AppInfo>

    @Update
    suspend fun updateAppInfo(app: AppInfo): Int

    @Query("DELETE FROM $APP_INFO_TABLE")
    suspend fun deleteAllApps()

    @Query("SELECT * FROM $APP_INFO_TABLE WHERE isLocked == :isLocked")
    fun getAllLockedApps(isLocked: Boolean): List<AppInfo>

}